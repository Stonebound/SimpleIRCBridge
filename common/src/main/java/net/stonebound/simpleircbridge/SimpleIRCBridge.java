package net.stonebound.simpleircbridge;

import net.stonebound.simpleircbridge.simpleircbridge.SimpleIRCBridgeCommon;

public final class SimpleIRCBridge {
    public static final String MOD_ID = "simpleircbridge";

    public static void init() {
        new SimpleIRCBridgeCommon();
    }
}
