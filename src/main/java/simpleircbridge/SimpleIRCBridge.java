package simpleircbridge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


@OnlyIn(Dist.DEDICATED_SERVER)
@Mod(SimpleIRCBridge.MODID)
public class SimpleIRCBridge {
	public static final String MODID = "simpleircbridge";

	public static final Logger logger = LogUtils.getLogger();
	private BridgeIRCBot bot;
	private MinecraftServer mcServer;

	public SimpleIRCBridge() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SIBConfig.SERVER_CONFIG);
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new GameEventHandler(this));

		SIBConfig.loadConfig(SIBConfig.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("simpleircbridge-common.toml"));
	}

	@SubscribeEvent
	public void serverStarting(ServerStartingEvent event) {
		this.mcServer = ServerLifecycleHooks.getCurrentServer();
		this.bot = new BridgeIRCBot(this);
		this.bot.run();
	}

	@SubscribeEvent
	public void serverStopping(ServerStoppingEvent event) {
		this.bot.disconnect();
	}

	@SubscribeEvent
	public void serverStopped(ServerStoppedEvent event) {
		this.bot.kill();
		this.bot = null;
		this.mcServer = null;
	}

	/* package-private */ static Logger log() {
		return logger;
	}

	/* package-private */ void sendToIrc(String line) {
		if (this.bot != null) {
			this.bot.sendMessage(SIBConfig.IRC_CHANNEL.get().toString(), line);
		}
	}

	/* package-private */ void sendToMinecraft(String line) {
		if (this.mcServer != null) {
			this.mcServer.getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(ForgeHooks.newChatWithLinks(line)));
		}
	}
}
