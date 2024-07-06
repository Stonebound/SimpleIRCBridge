package net.stonebound.simpleircbridge.quilt;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import net.stonebound.simpleircbridge.SimpleIRCBridge;

public final class SimpleIRCBridgeQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        // Run our common setup.
        SimpleIRCBridge.init();
    }
}
