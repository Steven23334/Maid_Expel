package com.github.steven23334.maid_expel.network;

import com.github.steven23334.maid_expel.util.MaidInventoryHelper;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public final class MaidExpelNetwork {
    private MaidExpelNetwork() {}

    /** 本类的日志记录器 */
    private static final Logger LOGGER = LogUtils.getLogger();

    /** 允许放生的最大距离平方（8 格），防止远程操作 */
    private static final double MAX_EXPEL_DISTANCE_SQR = 64.0;

    /** 通过反射缓存的 DATA_EXPERIENCE 字段，避免每次调用都进行反射查找 */
    private static Field dataExperienceField = null;

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

        // ===== 1. 通过反射读取女仆经验槽中的经验值 =====
        int maidExperience = getMaidExperience(maid);

        // ===== 2. 清空背包并掉落物品 =====
        int dropped = MaidInventoryHelper.dropAllInventory(maid, player);

        // ===== 3. 解除主人关系 =====
        maid.setOwnerUUID(null);
        maid.setTame(false, false);
        maid.setInSittingPose(false);
        maid.setOrderedToSit(false);

        // ===== 4. 掉落女仆经验槽中的经验 =====
        if (maidExperience > 0 && player.level() instanceof ServerLevel serverLevel) {
            ExperienceOrb.award(serverLevel, maid.position(), maidExperience);
            // 清空经验槽，避免反复驯服/放生刷经验
            setMaidExperience(maid, 0);
        }

        // ===== 5. 给予玩家蛋糕 =====
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(
                new PlayerMainInvWrapper(player.getInventory()),
                new ItemStack(Items.CAKE),
                false);
        if (!remainder.isEmpty()) {
            player.drop(remainder, false);
        }
        dropped++;

        // ===== 6. 通知玩家 =====
        player.displayClientMessage(
                Component.translatable("maid_expel.message.expel.success", dropped),
                false);
    }

    /**
     * 通过反射获取 EntityMaid 的 DATA_EXPERIENCE 字段，并读取其经验值。
     */
    @SuppressWarnings("unchecked")
    private static int getMaidExperience(EntityMaid maid) {
        try {
            if (dataExperienceField == null) {
                dataExperienceField = EntityMaid.class.getDeclaredField("DATA_EXPERIENCE");
                dataExperienceField.setAccessible(true);
            }
            EntityDataAccessor<Integer> accessor =
                    (EntityDataAccessor<Integer>) dataExperienceField.get(null);
            return maid.getEntityData().get(accessor);
        } catch (Exception e) {
            LOGGER.error("Failed to read maid experience via reflection", e);
            return 0;
        }
    }

    /**
     * 通过反射设置女仆的经验值（用于清空）。
     */
    @SuppressWarnings("unchecked")
    private static void setMaidExperience(EntityMaid maid, int experience) {
        try {
            if (dataExperienceField == null) {
                dataExperienceField = EntityMaid.class.getDeclaredField("DATA_EXPERIENCE");
                dataExperienceField.setAccessible(true);
            }
            EntityDataAccessor<Integer> accessor =
                    (EntityDataAccessor<Integer>) dataExperienceField.get(null);
            maid.getEntityData().set(accessor, experience);
        } catch (Exception e) {
            LOGGER.error("Failed to reset maid experience via reflection", e);
        }
    }
}