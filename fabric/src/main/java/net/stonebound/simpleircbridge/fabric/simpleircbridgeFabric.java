package net.stonebound.simpleircbridge.fabric;

import net.fabricmc.api.ModInitializer;

import net.stonebound.simpleircbridge.simpleIRCbridgeLoader;

public final class simpleircbridgeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        simpleIRCbridgeLoader.init();
    }
}
