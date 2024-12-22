package net.stonebound.simpleircbridge.simpleircbridge;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.stonebound.simpleircbridge.utils.IRCMinecraftConverter;

import static net.stonebound.simpleircbridge.simpleircbridge.SIBConstants.*;

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

//    @SubscribeEvent
//    public void command(CommandEvent e) {
//        String nickname = SIBUtil.mangle(e.getSender().getDisplayName().getUnformattedText());
//        /*
//         * Usually these would be instanceof checks, checking for
//         * net.minecraft.command.server.CommandEmote and
//         * net.minecraft.command.server.CommandBroadcast.
//         *
//         * However, some mods insist on overriding commands with their own wrappers
//         * (looking at you, FTBUtilities) so we're checking the names here.
//         */
//
//        String content = SIBUtil.join(" ", e.getParameters());
//
//        if ("say".equals(e.getCommand().getName())) {
//            if (this.bridge.getSibConf().ircFormatting) {
//                content = IRCMinecraftConverter.convMinecraftToIRC(content);
//            }
//            toIrc(String.format(FORMAT2_MC_BROADCAST, nickname, content));
//
//        } else if ("me".equals(e.getCommand().getName())) {
//            if (this.bridge.getSibConf().ircFormatting) {
//                content = IRCMinecraftConverter.convMinecraftToIRC(content);
//            }
//            toIrc(String.format(FORMAT2_MC_EMOTE, nickname, content));
//        }
//    }

    public void formatServerChat(ServerPlayer player, ChatEvent.ChatComponent component) {
        Component chatComponent = component.get().copy();
        String content = SIBUtil.getRawText(chatComponent);
        component.set(SimpleIRCBridgeCommon.newChatWithLinks(content, false));
    }

    public EventResult livingDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity instanceof ServerPlayer) {
            toIrc(String.format(FORMAT1_MC_DEATH, damageSource.getLocalizedDeathMessage(livingEntity).getString().replace(livingEntity.getName().toString(), SIBUtil.mangle(livingEntity.getName().toString())) ));
        }
        return EventResult.pass();
    }

    private void toIrc(String s) {
        this.bridge.sendToIrc(s);
    }

    public EventResult serverChat(ServerPlayer serverPlayer, Component component) {
        String content = SIBUtil.getRawText(component);
        if (serverPlayer != null) {
            String playername = SIBUtil.mangle(serverPlayer.getName().getString());
            if (Config.ircFormatting) {
                content = IRCMinecraftConverter.convMinecraftToIRC(content);
            }
            toIrc(String.format(FORMAT2_MC_CHAT, playername, content));
        } else {
            if (Config.ircFormatting) {
                content = IRCMinecraftConverter.convMinecraftToIRC(content);
            }
            toIrc(String.format(FORMAT2_MC_CHAT, "Server", content));
        }

        return EventResult.pass();
    }
}
