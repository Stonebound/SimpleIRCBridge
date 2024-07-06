package net.stonebound.simpleircbridge.simpleircbridge;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.stonebound.simpleircbridge.utils.MircColors;
import org.slf4j.Logger;

import static net.stonebound.simpleircbridge.simpleircbridge.Config.channel;
import static net.stonebound.simpleircbridge.simpleircbridge.Config.timestop;


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

		ChatEvent.RECEIVED.register((ServerPlayer player, Component chat) -> {
			eventHandler.serverChat(player, chat);
			return EventResult.pass();
		});
		LifecycleEvent.SERVER_STARTING.register(this::serverStarting);
		LifecycleEvent.SERVER_STOPPING.register(this::serverStopping);
		LifecycleEvent.SERVER_STOPPED.register(this::serverStopped);

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
	void sendToMinecraft (String line){
		if (this.mcServer != null) {
			this.mcServer.getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.literal(line)));
		}
	}
}