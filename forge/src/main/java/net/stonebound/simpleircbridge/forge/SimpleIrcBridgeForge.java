package net.stonebound.simpleircbridge.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.stonebound.simpleircbridge.SimpleIRCBridge;

@Mod(SimpleIRCBridge.MOD_ID)
public final class SimpleIrcBridgeForge {
    public SimpleIrcBridgeForge() {
        EventBuses.registerModEventBus(SimpleIRCBridge.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        SimpleIRCBridge.init();
    }
}
