package net.stonebound.simpleircbridge.simpleircbridge;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;

public class SIBUtil {
    private SIBUtil() {
        // utility class
    }

    /**
     * joins a number of Strings together with a given delimiter
     */
    public static String join(String delim, String... strings) {
        if (strings.length == 0)
            return "";
        if (strings.length == 1)
            return strings[0];
        else {
            StringBuilder sb = new StringBuilder(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                sb.append(delim);
                sb.append(strings[i]);
            }
            return sb.toString();
        }
    }

    /**
     * for Strings at least two chars long, inserts a ZWNJ at position 1
     */
    public static String mangle(String nick) {
        final String unicode_zwnj = "\u200c";
        if (nick.length() > 1) {
            return nick.charAt(0) + unicode_zwnj + nick.substring(1);
        }
        return nick;
    }


    //This is "borrowed" directly from forgehooks.java, and i will slightly rename variables if someone complains
    //1.20.4 update
    // since i didn't cheat to look where this was refactored to, is this now original code despite being exactly the same but with a different reference?
    //but how do I prove that?
    //This is truly something to despair over

    public static String getRawText(Component message) {
        return message.getContents() instanceof LiteralContents literalContents ? literalContents.text() : "";
    }
}
