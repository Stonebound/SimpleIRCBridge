package net.stonebound.simpleircbridge;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.stonebound.simpleircbridge.simpleircbridge.ConfigHolder;
import net.stonebound.simpleircbridge.simpleircbridge.SimpleIRCBridge;

public final class simpleIRCbridgeLoader {
    public static final String MOD_ID = "simpleircbridge";

    public static SimpleIRCBridge simpleircbridge;

    public static void init() {
        if (Platform.getEnvironment() == Env.SERVER && ConfigHolder.SetupConfigs()){
            simpleircbridge = new SimpleIRCBridge();

        }
    }
}
