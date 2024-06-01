package net.stonebound.simpleircbridge.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.stonebound.simpleircbridge.simpleIRCbridgeLoader;

@Mod(simpleIRCbridgeLoader.MOD_ID)
public final class simpleircbridgeForge {
    public simpleircbridgeForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(simpleIRCbridgeLoader.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        simpleIRCbridgeLoader.init();
    }
}
