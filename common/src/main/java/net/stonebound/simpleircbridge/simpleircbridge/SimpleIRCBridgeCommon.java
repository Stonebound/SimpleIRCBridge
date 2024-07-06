package net.stonebound.simpleircbridge.simpleircbridge;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.stonebound.simpleircbridge.utils.MircColors;
import org.slf4j.Logger;

import static net.stonebound.simpleircbridge.simpleircbridge.Config.channel;
import static net.stonebound.simpleircbridge.simpleircbridge.Config.timestop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimpleIRCBridgeCommon {


	public static final Logger logger = LogUtils.getLogger();
	private BridgeIRCBot bot;
	private MinecraftServer mcServer;
	public static GameEventHandler eventHandler;

	public static String indicator = "This is a string to remind git this is a different file";

	public SimpleIRCBridgeCommon() {
		eventHandler = new GameEventHandler(this);


		PlayerEvent.PLAYER_JOIN.register((Playerjoin) -> eventHandler.playerLoggedIn(Playerjoin));
		PlayerEvent.PLAYER_QUIT.register((Playerquit) -> eventHandler.playerLoggedOut(Playerquit));


		LifecycleEvent.SERVER_STARTING.register(this::serverStarting);
		LifecycleEvent.SERVER_STOPPING.register(this::serverStopping);
		LifecycleEvent.SERVER_STOPPED.register(this::serverStopped);
        ChatEvent.DECORATE.register(eventHandler::serverChat);


	}


	public void serverStarting(MinecraftServer instance) {
		this.mcServer = instance;
		this.bot = new BridgeIRCBot(this);
		this.bot.run();
	}

	public void serverStopping(MinecraftServer instance) {
		if (this.mcServer != null && timestop) {
			this.mcServer.getPlayerList().getPlayers().forEach(player -> sendToIrc(MircColors.BOLD + MircColors.LIGHT_RED + ">>>" + player.getName().getString() + " was still online when time came to a halt<<<"));
		}
		if (this.bot != null) {
			this.bot.disconnect();
		}

	}

	public void serverStopped(MinecraftServer instance) {
		if (this.bot != null) {
			this.bot.kill();
			this.bot = null;
		}

		this.mcServer = null;
	}

	/* package-private */
	static Logger log() {
		return logger;
	}

	/* package-private */ void sendToIrc(String line) {
		if (this.bot != null) {
			this.bot.sendMessage(channel, line);
		}
	}


    /* package-private */
    void sendToMinecraft(String line) {
        if (this.mcServer != null) {
            this.mcServer.getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(newChatWithLinks(line, true)));
        }
    }

    /*
     * Copyright (c) Forge Development LLC and contributors
     * SPDX-License-Identifier: LGPL-2.1-only
     */
    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR        namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |-------------------------|    |---------| |--|   |---------------|
            "((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    public static Component newChatWithLinks(String string, boolean allowMissingHeader) {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        MutableComponent ichat = null;
        Matcher matcher = URL_PATTERN.matcher(string);
        int lastEnd = 0;

        // Find all urls
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // Append the previous left overs.
            String part = string.substring(lastEnd, start);
            if (!part.isEmpty()) {
                if (ichat == null)
                    ichat = Component.literal(part);
                else
                    ichat.append(part);
            }
            lastEnd = end;
            String url = string.substring(start, end);
            MutableComponent link = Component.literal(url);

            try {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null) {
                    if (!allowMissingHeader) {
                        if (ichat == null)
                            ichat = Component.literal(url);
                        else
                            ichat.append(url);
                        continue;
                    }
                    url = "http://" + url;
                }
            } catch (URISyntaxException e) {
                // Bad syntax bail out!
                if (ichat == null) ichat = Component.literal(url);
                else ichat.append(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.setStyle(link.getStyle().withClickEvent(click).withUnderlined(true).withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
            if (ichat == null)
                ichat = Component.literal("");
            ichat.append(link);
        }

        // Append the rest of the message.
        String end = string.substring(lastEnd);
        if (ichat == null)
            ichat = Component.literal(end);
        else if (!end.isEmpty())
            ichat.append(Component.literal(string.substring(lastEnd)));
        return ichat;
    }
}