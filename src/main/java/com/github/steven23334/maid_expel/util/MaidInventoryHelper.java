package com.github.steven23334.maid_expel.util;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.handler.MaidBackpackHandler;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;

public final class MaidInventoryHelper {
    private MaidInventoryHelper() {}

    private static final String CURIOS_MOD_ID = "curios";

    public static int dropAllInventory(EntityMaid maid, Player player) {
        int[] count = {0};

        // 1. 先处理背包（含背包物品本身）
        dropBackpackItem(maid, player, count);

        // 2. maidInv 里剩余内容物（背包内部的普通物品）
        tryDrop(maid.getMaidInv(), maid, count);

        // 3. TLM 饰品栏
        tryDrop(maid.getMaidBauble(), maid, count);

        // 4. 盔甲 / 手持
        dropVanillaSlots(maid, count);

        // 5. Curios（可选）
        if (ModList.get().isLoaded(CURIOS_MOD_ID)) {
            count[0] += MaidCuriosHelper.dropCuriosInventory(maid);
        }

        return count[0];
    }

    /**
     * 卸下女仆当前背着的背包物品本身，并掉落。
     * <p>
     * 流程参考 EntityMaid#dropEquipment 里生成墓碑时的官方做法：
     * 从 maidInv 的 BACKPACK_ITEM_SLOT 拿到背包物品，走 IMaidBackpack 的
     * getTakeOffItemStack / onTakeOff 组合，然后重置背包类型。
     */
    private static void dropBackpackItem(EntityMaid maid, Player player, int[] count) {
        IMaidBackpack backpackType = maid.getMaidBackpackType();
        if (backpackType == BackpackManager.getEmptyBackpack()) {
            return;  // 女仆没背背包
        }

        IItemHandler inv = maid.getMaidInv();
        ItemStack current = inv.getStackInSlot(MaidBackpackHandler.BACKPACK_ITEM_SLOT);

        // 通过官方 API 计算"卸下后应掉落的物品"（可能保留背包等级/内容数据）
        ItemStack toDrop = backpackType.getTakeOffItemStack(current, player, maid);
        if (toDrop.isEmpty()) {
            toDrop = current.copy();
        }

        // 触发官方"卸下背包"回调（模型更新、事件通知等）
        backpackType.onTakeOff(current, player, maid);

        // 从 maidInv 清空背包槽位
        inv.extractItem(MaidBackpackHandler.BACKPACK_ITEM_SLOT, current.getCount(), false);

        // 重置女仆的背包类型为"空背包"
        maid.setMaidBackpackType(BackpackManager.getEmptyBackpack());

        // 掉落物品
        if (!toDrop.isEmpty()) {
            dropAt(maid, toDrop);
            count[0]++;
        }
    }

    static void tryDrop(IItemHandler handler, EntityMaid maid, int[] count) {
        if (handler == null) {
            return;
        }
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack extracted = handler.extractItem(i, stack.getCount(), false);
            if (!extracted.isEmpty()) {
                dropAt(maid, extracted);
                count[0]++;
            }
        }
    }

    private static void dropVanillaSlots(EntityMaid maid, int[] count) {
        for (var slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            if (!slot.isArmor()) {
                continue;
            }
            ItemStack stack = maid.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                maid.setItemSlot(slot, ItemStack.EMPTY);
                dropAt(maid, stack);
                count[0]++;
            }
        }

        ItemStack main = maid.getMainHandItem();
        if (!main.isEmpty()) {
            maid.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            dropAt(maid, main);
            count[0]++;
        }
        ItemStack off = maid.getOffhandItem();
        if (!off.isEmpty()) {
            maid.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, ItemStack.EMPTY);
            dropAt(maid, off);
            count[0]++;
        }
    }

    static void dropAt(EntityMaid maid, ItemStack stack) {
        Containers.dropItemStack(
                maid.level(),
                maid.getX(),
                maid.getY() + 0.5,
                maid.getZ(),
                stack
        );
    }
}