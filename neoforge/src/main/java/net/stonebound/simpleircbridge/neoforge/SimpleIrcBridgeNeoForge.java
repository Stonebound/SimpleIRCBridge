package net.stonebound.simpleircbridge.neoforge;

import net.neoforged.fml.common.Mod;

import net.stonebound.simpleircbridge.simpleIRCbridgeLoader;

@Mod(simpleIRCbridgeLoader.MOD_ID)
public final class SimpleIrcBridgeNeoForge {
    public SimpleIrcBridgeNeoForge() {
        // Run our common setup.
        simpleIRCbridgeLoader.init();
    }
}
