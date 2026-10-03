package com.github.steven23334.maid_expel.client;

import com.github.steven23334.maid_expel.MaidExpelMod;
import com.github.steven23334.maid_expel.network.ExpelMaidC2SPacket;
import com.github.tartaricacid.touhoulittlemaid.api.event.client.MaidContainerGuiEvent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 客户端入口：在 API 女仆界面里注入"放生"按钮。
 * <p>
 * ⚠️ 这里通过类名反射判断是否是 ApiContainerGui，
 *    避免附属模组在编译期强依赖 API 内部类。
 */
@EventBusSubscriber(modid = MaidExpelMod.MOD_ID, value = Dist.CLIENT)
public final class MaidExpelClient {
    private MaidExpelClient() {}
    /** 是否是 API 的女仆界面 */
    private static final String API_GUI_CLASS = "ApiContainerGui";

    @SubscribeEvent
    public static void onMaidContainerInit(MaidContainerGuiEvent.Init event) {
        // 只在 API 界面里显示放生按钮
        if (!event.getGui().getClass().getSimpleName().equals(API_GUI_CLASS)) {
            return;
        }
        int btnX = event.getLeftPos() + 90;
        int btnY = event.getTopPos() + 140;
        int btnW = 60;
        int btnH = 20;

        event.addButton("maid_expel:expel_button",
                Button.builder(
                        Component.translatable("maid_expel.button.expel"),
                        b -> PacketDistributor.sendToServer(
                                new ExpelMaidC2SPacket(event.getGui().getMaid().getId()))
                ).pos(btnX, btnY).size(btnW, btnH).build());
    }
}