package simpleircbridge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;


@OnlyIn(Dist.DEDICATED_SERVER)
@Mod(SimpleIRCBridge.MODID)
public class SimpleIRCBridge {
	public static final String MODID = "simpleircbridge";

	private static Logger logger = LogManager.getLogger();
	private BridgeIRCBot bot;
	private MinecraftServer mcServer;

	public SimpleIRCBridge() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SIBConfig.SERVER_CONFIG);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new GameEventHandler(this));

		SIBConfig.loadConfig(SIBConfig.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("simpleircbridge-common.toml"));
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		this.mcServer = ServerLifecycleHooks.getCurrentServer();
		this.bot = new BridgeIRCBot(this);
		this.bot.run();
	}

	@SubscribeEvent
	public void serverStopping(FMLServerStoppingEvent event) {
		this.bot.disconnect();
	}

	@SubscribeEvent
	public void serverStopped(FMLServerStoppedEvent event) {
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
			this.mcServer.getPlayerList().getPlayers().forEach(player -> player.sendMessage(ForgeHooks.newChatWithLinks(line), player.getUUID()));
		}
	}
}
