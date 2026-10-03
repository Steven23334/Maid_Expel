package com.github.steven23334.maid_expel.network;

import com.github.steven23334.maid_expel.MaidExpelMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 -> 服务端：请求放生指定女仆，清空背包并解除主人关系。
 */
public record ExpelMaidC2SPacket(int entityId) implements CustomPacketPayload {

    public static final Type<ExpelMaidC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    MaidExpelMod.MOD_ID, "expel_maid"));

    public static final StreamCodec<FriendlyByteBuf, ExpelMaidC2SPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ExpelMaidC2SPacket::entityId,
                    ExpelMaidC2SPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}