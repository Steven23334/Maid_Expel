package com.github.steven23334.maid_expel.util;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;

public final class MaidInventoryHelper {
    private MaidInventoryHelper() {}

    private static final String CURIOS_MOD_ID = "curios";

    public static int dropAllInventory(EntityMaid maid, Player player) {
        int[] count = {0};

        // 1. 先处理背包物品本身（必须在 tryDrop(getMaidInv) 之前）
        dropBackpackItem(maid, player, count);

        // 2. 掉落 getMaidInv() 里剩余的内容物
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

    private static void dropBackpackItem(EntityMaid maid, Player player, int[] count) {
        IItemHandler inv = maid.getMaidInv();
        if (inv == null) {
            return;
        }

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }

            Optional<IMaidBackpack> opt = BackpackManager.findBackpack(stack);
            if (opt.isEmpty()) {
                continue;
            }

            opt.get().onTakeOff(stack, player, maid);

            ItemStack extracted = inv.extractItem(i, stack.getCount(), false);
            if (!extracted.isEmpty()) {
                dropAt(maid, extracted);
                count[0]++;
            }
            break;
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