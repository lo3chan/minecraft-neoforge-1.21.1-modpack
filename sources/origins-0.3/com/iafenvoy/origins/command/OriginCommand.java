package com.iafenvoy.origins.command;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.content.OrbOfOriginItem;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.layer.LayerRegistries;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.util.HolderHelper;
import com.iafenvoy.origins.util.RandomHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class OriginCommand {
   public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                              "origin"
                           )
                           .requires(source -> source.hasPermission(2)))
                        .then(
                           Commands.literal("set")
                              .then(
                                 Commands.argument("targets", EntityArgument.players())
                                    .then(
                                       Commands.argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY))
                                          .then(
                                             Commands.argument("origin", ResourceArgument.resource(context, OriginRegistries.ORIGIN_KEY))
                                                .executes(OriginCommand::set)
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("get")
                           .then(
                              Commands.argument("targets", EntityArgument.players())
                                 .then(Commands.argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY)).executes(OriginCommand::get))
                           )
                     ))
                  .then(
                     Commands.literal("has")
                        .then(
                           Commands.argument("targets", EntityArgument.players())
                              .then(
                                 Commands.argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY))
                                    .then(
                                       Commands.argument("origin", ResourceArgument.resource(context, OriginRegistries.ORIGIN_KEY))
                                          .executes(OriginCommand::has)
                                    )
                              )
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("gui").executes(ctx -> openGuiAll(ctx, true)))
                     .then(
                        ((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes(ctx -> openGuiAll(ctx, false)))
                           .then(
                              Commands.argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY))
                                 .executes(OriginCommand::openGuiSpecific)
                           )
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("random").executes(ctx -> randomAll(ctx, true)))
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes(ctx -> randomAll(ctx, false)))
                        .then(Commands.argument("layer", ResourceArgument.resource(context, LayerRegistries.LAYER_KEY)).executes(OriginCommand::randomSpecific))
                  )
            )
      );
   }

   private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      List<ServerPlayer> targets = new ObjectArrayList(EntityArgument.getPlayers(context, "targets"));
      Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);
      Holder<Origin> origin = ResourceArgument.getResource(context, "origin", OriginRegistries.ORIGIN_KEY);
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      int processedTargets = 0;
      if (!((Origin)origin.value()).equals(Origin.EMPTY) && !((Layer)layer.value()).collectOrigins(source.registryAccess(), null).anyMatch(origin::equals)) {
         source.sendFailure(
            Component.translatableEscape("commands.origin.unregistered_in_layer", new Object[]{HolderHelper.string(origin), HolderHelper.string(layer)})
         );
      } else {
         for (ServerPlayer target : targets) {
            OriginDataHolder holder = OriginDataHolder.get(target);
            holder.setOrigin(layer, origin);
            holder.sync();
            processedTargets++;
         }

         if (processedTargets == 1) {
            source.sendSuccess(
               () -> Component.translatable(
                  "commands.origin.set.success.single",
                  new Object[]{((ServerPlayer)targets.getFirst()).getName(), Layer.getName(layer), Origin.getName(origin)}
               ),
               true
            );
         } else {
            int finalProcessedTargets = processedTargets;
            source.sendSuccess(
               () -> Component.translatable(
                  "commands.origin.set.success.multiple", new Object[]{finalProcessedTargets, Layer.getName(layer), Origin.getName(origin)}
               ),
               true
            );
         }
      }

      return processedTargets;
   }

   private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "targets");
      Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      OriginDataHolder holder = OriginDataHolder.get(target);
      Holder<Origin> origin = holder.getOrigin(layer);
      source.sendSuccess(
         () -> Component.translatable(
            "commands.origin.get.result", new Object[]{target.getName(), Layer.getName(layer), Origin.getName(origin), HolderHelper.string(origin)}
         ),
         false
      );
      return 1;
   }

   private static int has(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
      Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);
      Holder<Origin> origin = ResourceArgument.getResource(context, "origin", OriginRegistries.ORIGIN_KEY);
      int count = 0;

      for (ServerPlayer player : targets) {
         if (OriginDataHolder.get(player).hasOrigin(layer, origin)) {
            source.sendSystemMessage(
               Component.translatable("commands.origin.has.success", new Object[]{player.getName(), Layer.getName(layer), Origin.getName(origin)})
            );
            count++;
         } else {
            source.sendSystemMessage(
               Component.translatable("commands.origin.has.failure", new Object[]{player.getName(), Layer.getName(layer), Origin.getName(origin)})
            );
         }
      }

      return count;
   }

   private static int openGuiAll(CommandContext<CommandSourceStack> context, boolean self) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      Collection<ServerPlayer> targets = (Collection<ServerPlayer>)(self
         ? List.of(source.getPlayerOrException())
         : EntityArgument.getPlayers(context, "targets"));

      for (ServerPlayer target : targets) {
         OrbOfOriginItem.openGuiForLayer(target, null);
      }

      source.sendSuccess(() -> Component.translatable("commands.origin.gui.all", new Object[]{targets.size()}), true);
      return targets.size();
   }

   private static int openGuiSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
      Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);

      for (ServerPlayer target : targets) {
         OrbOfOriginItem.openGuiForLayer(target, layer);
      }

      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.translatable("commands.origin.gui.layer", new Object[]{targets.size(), Layer.getName(layer)}), true);
      return targets.size();
   }

   private static int randomAll(CommandContext<CommandSourceStack> context, boolean self) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      Collection<ServerPlayer> targets = (Collection<ServerPlayer>)(self
         ? List.of(source.getPlayerOrException())
         : EntityArgument.getPlayers(context, "targets"));
      List<Holder<Layer>> layers = LayerRegistries.streamRandomizableLayers(((CommandSourceStack)context.getSource()).registryAccess()).toList();

      for (ServerPlayer target : targets) {
         for (Holder<Layer> layer : layers) {
            setAndGetRandomOrigin(target, layer);
         }
      }

      source.sendSuccess(() -> Component.translatable("commands.origin.random.all", new Object[]{targets.size(), layers.size()}), true);
      return targets.size();
   }

   private static int randomSpecific(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      List<ServerPlayer> targets = new ObjectArrayList(EntityArgument.getPlayers(context, "targets"));
      Holder<Layer> layer = ResourceArgument.getResource(context, "layer", LayerRegistries.LAYER_KEY);
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      AtomicReference<Holder<Origin>> origin = new AtomicReference<>();
      if (((Layer)layer.value()).allowRandom()) {
         for (ServerPlayer target : targets) {
            origin.set(setAndGetRandomOrigin(target, layer));
         }

         if (targets.size() > 1) {
            source.sendSuccess(
               () -> Component.translatable("commands.origin.random.success.multiple", new Object[]{targets.size(), Layer.getName(layer)}), true
            );
         } else {
            source.sendSuccess(
               () -> Component.translatable(
                  "commands.origin.random.success.single",
                  new Object[]{((ServerPlayer)targets.getFirst()).getName(), Origin.getName(origin.get()), Layer.getName(layer)}
               ),
               true
            );
         }
      }

      return targets.size();
   }

   private static Holder<Origin> setAndGetRandomOrigin(ServerPlayer target, Holder<Layer> layer) {
      List<Holder<Origin>> origins = ((Layer)layer.value()).collectRandomizableOrigins(target).toList();
      OriginDataHolder holder = OriginDataHolder.get(target);
      Holder<Origin> origin = RandomHelper.randomOne(origins);
      holder.setOrigin(layer, origin);
      holder.fillAutoChoosing();
      holder.sync();
      Origins.LOGGER
         .info(
            "Player {} was randomly assigned the origin {} for layer {}",
            new Object[]{target.getName().getString(), HolderHelper.id(origin), HolderHelper.id(layer)}
         );
      return origin;
   }
}
