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

@EventBusSubscriber(modid = MaidExpelMod.MOD_ID, value = Dist.CLIENT)
public final class MaidExpelClient {
    private MaidExpelClient() {}

    private static final String API_GUI_CLASS = "ApiContainerGui";

    // 按钮布局常量
    private static final int BTN_W = 66;
    private static final int BTN_H = 20;
    private static final int BTN_Y = 140;
    private static final int CANCEL_X = 110;   // 取消按钮：面板左侧
    private static final int EXPEL_X = 180;    // 放生按钮：面板右侧

    @SubscribeEvent
    public static void onMaidContainerInit(MaidContainerGuiEvent.Init event) {
        if (!event.getGui().getClass().getSimpleName().equals(API_GUI_CLASS)) {
            return;
        }

        int btnY = event.getTopPos() + BTN_Y;
        int expelX = event.getLeftPos() + EXPEL_X;
        int cancelX = event.getLeftPos() + CANCEL_X;

        // 用数组保存引用，方便 lambda 内互相引用（lambda 捕获要求 effectively final）
        final boolean[] confirming = {false};
        final Button[] expelBtn = new Button[1];
        final Button[] cancelBtn = new Button[1];

        // 放生 / 确认按钮
        expelBtn[0] = Button.builder(
                Component.translatable("maid_expel.button.expel"),
                b -> {
                    if (!confirming[0]) {
                        // 第一次点击 → 进入确认状态
                        confirming[0] = true;
                        expelBtn[0].setMessage(Component.translatable("maid_expel.button.expel.confirm"));
                        cancelBtn[0].visible = true;
                        return;
                    }
                    // 第二次点击 → 真正发送放生请求
                    PacketDistributor.sendToServer(
                            new ExpelMaidC2SPacket(event.getGui().getMaid().getId()));
                }
        ).pos(expelX, btnY).size(BTN_W, BTN_H).build();

        // 取消按钮（初始隐藏）
        cancelBtn[0] = Button.builder(
                Component.translatable("maid_expel.button.cancel"),
                b -> {
                    confirming[0] = false;
                    expelBtn[0].setMessage(Component.translatable("maid_expel.button.expel"));
                    cancelBtn[0].visible = false;
                }
        ).pos(cancelX, btnY).size(BTN_W, BTN_H).build();

        event.addButton("maid_expel:expel_button", expelBtn[0]);
        event.addButton("maid_expel:cancel_button", cancelBtn[0]);

        // 添加后再设置，防止 addButton 内部重置 visible
        cancelBtn[0].visible = false;
    }
}