package net.stonebound.simpleircbridge.neoforge;

import net.neoforged.fml.common.Mod;

import net.stonebound.simpleircbridge.SimpleIRCBridge;

@Mod(SimpleIRCBridge.MOD_ID)
public final class SimpleIrcBridgeNeoForge {
    public SimpleIrcBridgeNeoForge() {
        // Run our common setup.
        SimpleIRCBridge.init();
    }
}
