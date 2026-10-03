package com.github.steven23334.maid_expel;

import com.github.steven23334.maid_expel.network.MaidExpelNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MaidExpelMod.MOD_ID)
public class MaidExpelMod {
    public static final String MOD_ID = "maid_expel";

    public MaidExpelMod(IEventBus modEventBus) {
        MaidExpelNetwork.register(modEventBus);
    }
}