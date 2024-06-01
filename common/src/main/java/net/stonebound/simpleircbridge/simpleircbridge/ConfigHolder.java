package net.stonebound.simpleircbridge.simpleircbridge;
import dev.architectury.platform.Platform;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.*;

import java.util.Hashtable;

public class ConfigHolder {


    public static Hashtable<String, ConfigValue> MemoryConfigs  = new Hashtable<String,ConfigValue>();


    static class ConfigValue {
        String id;
        String description;
        String value;

        public ConfigValue(String ID, String DESC, String VALUE) {
            id = ID;
            description = DESC;
            value = VALUE;
        }
    }




    public static boolean SetupConfigs() {
        Path modConfigFilePath = Paths.get(Platform.getConfigFolder().toString(), "SimpleIRCBridge.json");
        File modConfigFile = new File(String.valueOf(Paths.get(Platform.getConfigFolder().toString(), "SimpleIRCBridge.json")));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (modConfigFile.isFile()){
            //JASON
            try{
                Reader reader = Files.newBufferedReader(modConfigFilePath);
                ConfigValue[] ReadGsonConfigArray = gson.fromJson(reader, ConfigValue[].class);
                for (ConfigValue x : ReadGsonConfigArray) {
                    MemoryConfigs.put(x.id, x);
                }
                reader.close();
                return true;




            } catch (IOException e) {
                throw new RuntimeException(e);

            }

        }

        else {
            String json = gson.toJson(DefaultGsonConfigArray);
            try{
                FileWriter writer = new FileWriter(modConfigFile);
                writer.write(json);
                writer.close();
                return false;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }





    static ConfigValue[] DefaultGsonConfigArray = new ConfigValue[] {
            new ConfigValue("ircFormatting", "Whether minecraft formatting should be converted to IRC formatting", "true"),
            new ConfigValue("mcFormatting", "Whether IRC formatting should be converted to Minecraft formatting", "true"),
            new ConfigValue("nick", "The nickname that the relay bot will use, remember the 9 char limit", ""),
            new ConfigValue("password", "#IRC Server password (if any), it should be obvious but this is probably accessable to other mods", ""),
            new ConfigValue("hostname", "Hostname or IP address of your IRC server", ""),
            new ConfigValue("port", "Port of the IRC server to connect to. Common values: 6697 for TLS/SSL; 6667 for plaintext connections, Range 1025 ~ 65535", "6667"),
            new ConfigValue("channel", "IRC channel to relay into", ""),
            new ConfigValue("tls", "Whether TLS/SSL is enabled. Set to 'false' for a plaintext connection", "false"),
            new ConfigValue("username", "The username/ident that the relay bot will use", ""),
            new ConfigValue("realname", "The realname/gecos that the relay bot will use", ""),
            new ConfigValue("timestop", "Sends a message to the bridge for each player that was online at server closing, as the normal PLAYERQUIT event does not fire", "true")
    };
}
