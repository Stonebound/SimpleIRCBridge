package net.stonebound.simpleircbridge;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.stonebound.simpleircbridge.simpleircbridge.SimpleIRCBridgeCommon;

public final class SimpleIRCBridge {
    public static final String MOD_ID = "simpleircbridge";

    public static SimpleIRCBridgeCommon simpleircbridge;

    public static void init() {
        if (Platform.getEnvironment() == Env.SERVER){
            simpleircbridge = new SimpleIRCBridgeCommon();

        }
    }
}
