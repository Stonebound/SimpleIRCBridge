package net.stonebound.simpleircbridge.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.stonebound.simpleircbridge.simpleIRCbridgeLoader;
//TODO Test to ensure this actually works on forge, for some reason its not working in IDE but is assembled, but I can't get a normal vanilla forge server to work
@Mod(simpleIRCbridgeLoader.MOD_ID)
public final class simpleircbridgeForge {
    public simpleircbridgeForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(simpleIRCbridgeLoader.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        simpleIRCbridgeLoader.init();
    }
}
