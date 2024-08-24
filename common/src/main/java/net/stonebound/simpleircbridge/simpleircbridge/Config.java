package net.stonebound.simpleircbridge.simpleircbridge;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import dev.architectury.platform.Platform;
import net.minecraft.server.MinecraftServer;
import net.stonebound.simpleircbridge.utils.CommentedConfigSpec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Config {
    public static final CommentedConfigSpec commonSpec;
    public static CommentedFileConfig commonConfig;

    private static final Path commonPath = Platform.getConfigFolder().resolve("simpleircbridge-common.toml");

    private Config() {
    }

    public static Boolean ircFormatting = true;
    public static Boolean mcFormatting = true;
    public static String nick = "bridgebot";
    public static String password = "";
    public static String hostname = "localhost";
    public static Integer port = 6667;
    public static String channel = "#general";
    public static Boolean tls = false;
    public static String username = "bridgebot";

    public static String realname = "SimpleIRCBridge";
    public static Boolean timestop = true;

    static {
        System.setProperty("nightconfig.preserveInsertionOrder", "true");

        commonSpec = new CommentedConfigSpec();

        commonSpec.comment("ircFormatting", "Whether minecraft formatting should be converted to IRC formatting");
        commonSpec.define("ircFormatting", Config.ircFormatting);

        commonSpec.comment("mcFormatting", "Whether IRC formatting should be converted to Minecraft formatting");
        commonSpec.define("mcFormatting", Config.mcFormatting);

        commonSpec.comment("nick", "The nickname that the relay bot will use, remember the 9 char limit");
        commonSpec.define("nick", Config.nick);

        commonSpec.comment("password", "#IRC Server password (if any), it should be obvious but this is probably accessable to other mods");
        commonSpec.define("password", Config.password);

        commonSpec.comment("hostname", "Hostname or IP address of your IRC server");
        commonSpec.define("hostname", Config.hostname);

        commonSpec.comment("port", "Port of the IRC server to connect to. Common values: 6697 for TLS/SSL; 6667 for plaintext connections");
        commonSpec.defineInRange("port", Config.port, 1025, 65535);

        commonSpec.comment("channel", "IRC channel to relay into");
        commonSpec.define("channel", Config.channel);

        commonSpec.comment("tls", "Whether TLS/SSL is enabled. Set to 'false' for a plaintext connection");
        commonSpec.define("tls", Config.tls);

        commonSpec.comment("username", "The username/ident that the relay bot will use");
        commonSpec.define("username", Config.username);

        commonSpec.comment("realname", "The realname/gecos that the relay bot will use");
        commonSpec.define("realname", Config.realname);

        commonSpec.comment("timestop", "Sends a message to the bridge for each player that was online at server closing, as the normal PLAYERQUIT event does not fire");
        commonSpec.define("timestop", Config.timestop);
    }

    private static final FileNotFoundAction MAKE_DIRECTORIES_AND_FILE = (file, configFormat) -> {
        Files.createDirectories(file.getParent());
        Files.createFile(file);
        configFormat.initEmptyFile(file);
        return false;
    };

    private static CommentedFileConfig buildFileConfig(Path path) {
        return CommentedFileConfig.builder(path)
                .onFileNotFound(MAKE_DIRECTORIES_AND_FILE)
                .preserveInsertionOrder()
                .build();
    }

    private static void saveConfig(UnmodifiableConfig config, CommentedConfigSpec spec, Path path) {
        try (CommentedFileConfig fileConfig = buildFileConfig(path)) {
            fileConfig.putAll(config);
            spec.correct(fileConfig);
            fileConfig.save();
        }
    }

    public static void save() {
        if (commonConfig != null) {
            saveConfig(commonConfig, commonSpec, commonPath);
        }
    }

    public static void serverStarting(MinecraftServer server) {
        try (CommentedFileConfig config = buildFileConfig(commonPath)) {
            config.load();
            commonSpec.correct(config, Config::correctionListener);
            config.save();
            commonConfig = config;
            sync();
        }
    }

    public static void serverStopping(MinecraftServer server) {
        commonConfig = null;
    }

    private static void correctionListener(ConfigSpec.CorrectionAction action, List<String> path, Object incorrectValue,
                                           Object correctedValue) {
        String key = String.join(".", path);
        switch (action) {
            case ADD:
                SimpleIRCBridgeCommon.log().warn("Config key {} missing -> added default value.", key);
                break;
            case REMOVE:
                SimpleIRCBridgeCommon.log().warn("Config key {} not defined -> removed from config.", key);
                break;
            case REPLACE:
                SimpleIRCBridgeCommon.log().warn("Config key {} not valid -> replaced with default value.", key);
        }
    }

    public static void sync() {
        if (commonConfig != null) {
            Config.ircFormatting = commonConfig.<Boolean>get("ircFormatting");
            Config.mcFormatting = commonConfig.<Boolean>get("mcFormatting");
            Config.nick = commonConfig.<String>get("nick");
            Config.password = commonConfig.<String>get("password");
            Config.hostname = commonConfig.<String>get("hostname");
            Config.port = commonConfig.<Integer>get("port");
            Config.channel = commonConfig.<String>get("channel");
            Config.tls = commonConfig.<Boolean>get("tls");
            Config.username = commonConfig.<String>get("username");
            Config.realname = commonConfig.<String>get("realname");
            Config.timestop = commonConfig.<Boolean>get("timestop");
        }
    }
}
