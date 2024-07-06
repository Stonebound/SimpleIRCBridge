package net.stonebound.simpleircbridge.simpleircbridge;

import static net.stonebound.simpleircbridge.simpleircbridge.Config.ircFormatting;
import static net.stonebound.simpleircbridge.simpleircbridge.SIBConstants.*;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.stonebound.simpleircbridge.utils.IRCMinecraftConverter;

public class GameEventHandler {
	private final SimpleIRCBridgeCommon bridge;

	public GameEventHandler(SimpleIRCBridgeCommon bridge) {
		this.bridge = bridge;
	}


	public void playerLoggedIn(ServerPlayer e) {
		toIrc(String.format(FORMAT1_MC_LOGIN, SIBUtil.mangle(e.getName().getString())));
	}


	public void playerLoggedOut(ServerPlayer e) {
		toIrc(String.format(FORMAT1_MC_LOGOUT, SIBUtil.mangle(e.getName().getString())));
	}

//	@SubscribeEvent
//	public void command(CommandEvent e) {
//		String nickname = SIBUtil.mangle(e.getSender().getDisplayName().getUnformattedText());
//		/*
//		 * Usually these would be instanceof checks, checking for
//		 * net.minecraft.command.server.CommandEmote and
//		 * net.minecraft.command.server.CommandBroadcast.
//		 *
//		 * However, some mods insist on overriding commands with their own wrappers
//		 * (looking at you, FTBUtilities) so we're checking the names here.
//		 */
//
//		String content = SIBUtil.join(" ", e.getParameters());
//
//		if ("say".equals(e.getCommand().getName())) {
//			if (this.bridge.getSibConf().ircFormatting) {
//				content = IRCMinecraftConverter.convMinecraftToIRC(content);
//			}
//			toIrc(String.format(FORMAT2_MC_BROADCAST, nickname, content));
//
//		} else if ("me".equals(e.getCommand().getName())) {
//			if (this.bridge.getSibConf().ircFormatting) {
//				content = IRCMinecraftConverter.convMinecraftToIRC(content);
//			}
//			toIrc(String.format(FORMAT2_MC_EMOTE, nickname, content));
//		}
//	}


	public void serverChat(ServerPlayer player, Component component) {
		String content = SIBUtil.getRawText(component);
		if (player != null) {

			String playername = player.getName().getString();
			if (ircFormatting) {
				content = IRCMinecraftConverter.convMinecraftToIRC(content);
			}
			toIrc(String.format(FORMAT2_MC_CHAT, playername, content));
		}

		else {
		if (ircFormatting) {
			content = IRCMinecraftConverter.convMinecraftToIRC(content);
		}
		toIrc(String.format(FORMAT2_MC_CHAT, "Server", content));
		}
		return;


	}

//	@SubscribeEvent
//	public void livingDeath(LivingDeathEvent e) {
//		if (e.getEntityLiving() instanceof PlayerEntity) {
//			toIrc(String.format(FORMAT1_MC_DEATH,
//					e.getSource().getDeathMessage(e.getEntityLiving()).getString()));
//		}
//	}

	private void toIrc(String s) {
		this.bridge.sendToIrc(s);
	}
}
