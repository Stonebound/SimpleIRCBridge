package net.stonebound.simpleircbridge.simpleircbridge;

import net.stonebound.simpleircbridge.genericircbot.AbstractIRCBot;
import net.stonebound.simpleircbridge.genericircbot.IRCConnectionInfo;
import net.stonebound.simpleircbridge.utils.IRCMinecraftConverter;

import java.net.InetSocketAddress;

import static net.stonebound.simpleircbridge.simpleircbridge.SIBConstants.FORMAT2_IRC_CHAT;

public class BridgeIRCBot extends AbstractIRCBot {

    private SimpleIRCBridgeCommon bridge;

    BridgeIRCBot(SimpleIRCBridgeCommon bridge) {
        super(
                new InetSocketAddress(Config.hostname, Config.port),
                Config.tls,
                new IRCConnectionInfo(
                        !Config.nick.contains("(rnd)") ? Config.nick : Config.nick.replace("(rnd)", String.valueOf((int) (Math.random() * 100000))),
                        Config.username,
                        Config.realname
                ),
                Config.password
        );
        this.bridge = bridge;
    }

    @Override
    protected void logMessage(String msg) {
        SimpleIRCBridgeCommon.log().debug(msg);
    }

    /* event handling */
//	@Override
//	protected void onJoin(String channel, String sender) {
//		toMc(String.format(FORMAT1_IRC_JOIN, sender));
//	}
//
//	@Override
//	protected void onPart(String channel, String sender, String reason) {
//		if (SIBConfig.MC_FORMATTING.get()) {
//			reason = IRCMinecraftConverter.convIRCtoMinecraft(reason);
//		}
//		toMc(String.format(FORMAT2_IRC_PART, sender, reason));
//	}
//
//	@Override
//	protected void onQuit(String sender, String reason) {
//		if (SIBConfig.MC_FORMATTING.get()) {
//			reason = IRCMinecraftConverter.convIRCtoMinecraft(reason);
//		}
//		toMc(String.format(FORMAT2_IRC_QUIT, sender, reason));
//	}
//
//	@Override
//	protected void onKick(String channel, String opsender, String victim, String reason) {
//		if (SIBConfig.MC_FORMATTING.get()) {
//			reason = IRCMinecraftConverter.convIRCtoMinecraft(reason);
//		}
//		toMc(String.format(FORMAT3_IRC_KICK, victim, opsender, reason));
//	}

    @Override
    protected void onMessage(String channel, String sender, String message) {
        if (Config.mcFormatting) {
            message = IRCMinecraftConverter.convIRCtoMinecraft(message);
        }
        if (sender.equals("bungee")) {
            toMc(message);
        } else if (sender.equals("discord")) {
            toMc(String.format(FORMAT2_IRC_CHAT, "\u00a7b" + sender + "\u00a7r", message));
        } else {
            toMc(String.format(FORMAT2_IRC_CHAT, "\u00a7a" + sender + "\u00a7r", message));
        }
    }

//	@Override
//	protected void onAction(String channel, String sender, String action) {
//		if (SIBConfig.MC_FORMATTING.get()) {
//			action = IRCMinecraftConverter.convIRCtoMinecraft(action);
//		}
//		toMc(String.format(FORMAT2_IRC_EMOTE, sender, action));
//	}
//
//	@Override
//	protected void onNickChange(String sender, String newnick) {
//		toMc(String.format(FORMAT2_IRC_NICKCHG, sender, newnick));
//	}

    @Override
    protected void onNumeric001() {
        joinChannel(Config.channel);
    }

    /**
     * {@inheritDoc}
     */ // re-declare protected, publish method for package
    @Override
    protected void disconnect() {
        super.disconnect();
    }

    /**
     * {@inheritDoc}
     */ // re-declare protected, publish method for package
    @Override
    protected void kill() {
        super.kill();
    }

    /**
     * {@inheritDoc}
     */ // re-declare protected, publish method for package
    @Override
    protected void sendMessage(String channel, String message) {
        super.sendMessage(channel, message);
    }

    private void toMc(String s) {
        this.bridge.sendToMinecraft(s);
    }
}
