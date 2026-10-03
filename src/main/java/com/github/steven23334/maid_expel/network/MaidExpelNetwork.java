package com.github.steven23334.maid_expel.network;

import com.github.steven23334.maid_expel.util.MaidInventoryHelper;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class MaidExpelNetwork {
    private MaidExpelNetwork() {}
    /** 允许放生的最大距离平方（8 格），防止远程操作 */
    private static final double MAX_EXPEL_DISTANCE_SQR = 64.0;

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, MaidExpelNetwork::onRegister);
    }

    private static void onRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ExpelMaidC2SPacket.TYPE,
                ExpelMaidC2SPacket.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> handleExpel(context.player(), payload.entityId())));
    }

    private static void handleExpel(Player player, int entityId) {
        Entity entity = player.level().getEntity(entityId);
        if (!(entity instanceof EntityMaid maid)) {
            return;
        }

        // ===== 安全性校验 =====

        // 1. 必须是自己的女仆
        if (!maid.isOwnedBy(player)) {
            return;
        }

        // 2. 距离校验
        if (player.distanceToSqr(maid) > MAX_EXPEL_DISTANCE_SQR) {
            return;
        }

        // 3. 玩家当前必须开着 API 女仆界面，避免被外挂调用
        String menuName = player.containerMenu.getClass().getSimpleName();
        if (!menuName.contains("ApiContainer")) {
            return;
        }

        // 先关 GUI，避免界面停在已失效的实体上
        player.closeContainer();

        // ===== 1. 清空背包并掉落物品 =====
        int dropped = MaidInventoryHelper.dropAllInventory(maid,player);

        // ===== 2. 解除主人关系 =====
        maid.setOwnerUUID(null);
        maid.setTame(false, false);
        maid.setInSittingPose(false);
        maid.setOrderedToSit(false);

        // ===== 3. 给予玩家蛋糕 =====
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(
                new PlayerMainInvWrapper(player.getInventory()),
                new ItemStack(Items.CAKE),
                false);
        if (!remainder.isEmpty()) {
            player.drop(remainder, false);
        }
        dropped++;
        // ===== 4. 通知玩家 =====
        player.displayClientMessage(
                Component.translatable("maid_expel.message.expel.success",dropped),
                false);
    }
}