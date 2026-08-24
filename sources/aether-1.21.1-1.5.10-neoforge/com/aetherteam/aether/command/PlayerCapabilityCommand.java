package com.aetherteam.aether.command;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.network.packet.clientbound.HealthResetPacket;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlayerCapabilityCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("aether")
            .then(
               ((LiteralArgumentBuilder)Commands.literal("player").requires(commandSourceStack -> commandSourceStack.hasPermission(2)))
                  .then(
                     Commands.literal("life_shards")
                        .then(
                           Commands.literal("set")
                              .then(
                                 Commands.argument("targets", GameProfileArgument.gameProfile())
                                    .suggests((context, builder) -> {
                                       PlayerList playerlist = ((CommandSourceStack)context.getSource()).getServer().getPlayerList();
                                       return SharedSuggestionProvider.suggest(
                                          playerlist.getPlayers().stream().map(player -> player.getGameProfile().getName()), builder
                                       );
                                    })
                                    .then(
                                       Commands.argument("value", IntegerArgumentType.integer(0, 10))
                                          .executes(
                                             context -> setLifeShards(
                                                (CommandSourceStack)context.getSource(),
                                                GameProfileArgument.getGameProfiles(context, "targets"),
                                                IntegerArgumentType.getInteger(context, "value")
                                             )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static int setLifeShards(CommandSourceStack source, Collection<GameProfile> gameProfiles, int value) {
      ServerLevel level = source.getLevel();
      PlayerList playerList = source.getServer().getPlayerList();

      for (GameProfile gameProfile : gameProfiles) {
         ServerPlayer player = playerList.getPlayer(gameProfile.getId());
         if (player != null) {
            AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
            data.setSynched(player.getId(), Direction.CLIENT, "setLifeShardCount", value);
            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (attribute != null) {
               attribute.removeModifier(data.getLifeShardHealthAttributeModifier().id());
            }

            player.setHealth(player.getMaxHealth());
            PacketDistributor.sendToPlayersNear(
               level, player, player.getX(), player.getY(), player.getZ(), 5.0, new HealthResetPacket(player.getId(), value), new CustomPacketPayload[0]
            );
            source.sendSuccess(
               () -> Component.translatable("commands.aether.capability.player.life_shards.set", new Object[]{player.getDisplayName(), value}), true
            );
         }
      }

      return 1;
   }
}
