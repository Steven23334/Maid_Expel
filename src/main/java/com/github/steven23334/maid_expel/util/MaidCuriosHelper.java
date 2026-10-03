package com.github.steven23334.maid_expel.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;

/**
 * Curios API 的隔离类。
 * <p>
 * ⚠️ 本类里所有代码都引用了 Curios 的类。
 *    调用方务必先通过 {@code ModList.get().isLoaded("curios")} 判断，
 *    否则 JVM 加载本类时会抛 NoClassDefFoundError。
 */
final class MaidCuriosHelper {
    private MaidCuriosHelper() {}

    /**
     * 清空女仆通过 Curios 装备的所有饰品，物品掉落在脚下。
     *
     * @return 掉落的物品堆叠数量
     */
    static int dropCuriosInventory(EntityMaid maid) {
        int[] count = {0};

        // Optional<ICuriosItemHandler>
        CuriosApi.getCuriosInventory(maid).ifPresent(curiosInventory -> {
            Map<String, ICurioStacksHandler> curiosSlots = curiosInventory.getCurios();

            for (ICurioStacksHandler stacksHandler : curiosSlots.values()) {
                IItemHandler handler = stacksHandler.getStacks();
                if (handler == null) {
                    continue;
                }
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (stack.isEmpty()) {
                        continue;
                    }
                    ItemStack extracted = handler.extractItem(i, stack.getCount(), false);
                    if (!extracted.isEmpty()) {
                        MaidInventoryHelper.dropAt(maid, extracted);
                        count[0]++;
                    }
                }
            }
        });

        return count[0];
    }
}