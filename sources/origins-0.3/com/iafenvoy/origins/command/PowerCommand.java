package com.iafenvoy.origins.command;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.PowerRegistries;
import com.iafenvoy.origins.util.HolderHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class PowerCommand {
   public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("power").requires(source -> source.hasPermission(2)))
            .then(
               ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                       "target", EntityArgument.player()
                                    )
                                    .then(
                                       Commands.literal("grant")
                                          .then(
                                             ((RequiredArgumentBuilder)Commands.argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                                   .executes(PowerCommand::grantDefault))
                                                .then(
                                                   Commands.argument("source", ResourceLocationArgument.id())
                                                      .suggests(PowerCommand::suggestAllSources)
                                                      .executes(PowerCommand::grantFromSource)
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("revoke")
                                       .then(
                                          ((RequiredArgumentBuilder)Commands.argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                                .suggests(PowerCommand::suggestPowers)
                                                .executes(PowerCommand::revokeDefault))
                                             .then(
                                                Commands.argument("source", ResourceLocationArgument.id())
                                                   .suggests(PowerCommand::suggestSources)
                                                   .executes(PowerCommand::revokeFromSource)
                                             )
                                       )
                                 ))
                              .then(Commands.literal("clear").executes(PowerCommand::clear)))
                           .then(
                              Commands.literal("has")
                                 .then(
                                    ((RequiredArgumentBuilder)Commands.argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY))
                                          .executes(PowerCommand::has))
                                       .then(
                                          Commands.argument("source", ResourceLocationArgument.id())
                                             .suggests(PowerCommand::suggestAllSources)
                                             .executes(PowerCommand::has)
                                       )
                                 )
                           ))
                        .then(Commands.literal("list").executes(PowerCommand::list)))
                     .then(
                        Commands.literal("revokeall")
                           .then(
                              Commands.argument("source", ResourceLocationArgument.id())
                                 .suggests(PowerCommand::suggestAllSources)
                                 .executes(PowerCommand::revokeAll)
                           )
                     ))
                  .then(
                     Commands.literal("sources")
                        .then(Commands.argument("power", ResourceArgument.resource(context, PowerRegistries.POWER_KEY)).executes(PowerCommand::sources))
                  )
            )
      );
   }

   private static CompletableFuture<Suggestions> suggestPowers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
      ServerPlayer player = EntityArgument.getPlayer(context, "target");
      Collection<Holder<Power>> sources = OriginDataHolder.get(player).getData().getPowers().values();
      Stream<String> stream = sources.stream()
         .<Optional>map(Holder::unwrapKey)
         .filter(Optional::isPresent)
         .map(Optional::get)
         .map(ResourceKey::location)
         .map(ResourceLocation::toString);
      return SharedSuggestionProvider.suggest(stream, builder);
   }

   private static CompletableFuture<Suggestions> suggestAllSources(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
      ServerPlayer player = EntityArgument.getPlayer(context, "target");
      Set<ResourceLocation> sources = new HashSet<>(OriginDataHolder.get(player).getData().getPowers().keySet());
      sources.add(OriginDataHolder.DEFAULT_SOURCE);
      return SharedSuggestionProvider.suggest(sources.stream().map(ResourceLocation::toString), builder);
   }

   private static CompletableFuture<Suggestions> suggestSources(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
      ServerPlayer player = EntityArgument.getPlayer(context, "target");
      Reference<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
      Stream<String> stream = OriginDataHolder.get(player)
         .getData()
         .getPowers()
         .entries()
         .stream()
         .filter(x -> ((Holder)x.getValue()).value() == power.value())
         .map(Entry::getKey)
         .map(ResourceLocation::toString);
      return SharedSuggestionProvider.suggest(stream, builder);
   }

   private static int grantDefault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return grant(context, OriginDataHolder.DEFAULT_SOURCE);
   }

   private static int grantFromSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return grant(context, ResourceLocationArgument.getId(context, "source"));
   }

   private static int grant(CommandContext<CommandSourceStack> context, ResourceLocation source) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
      CommandSourceStack src = (CommandSourceStack)context.getSource();
      OriginDataHolder holder = OriginDataHolder.get(target);
      holder.grantPower(source, power);
      holder.sync();
      src.sendSuccess(
         () -> Component.translatable("commands.power.grant.success", new Object[]{target.getName(), HolderHelper.string(power), source.toString()}), true
      );
      return 1;
   }

   private static int revokeDefault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return revoke(context, OriginDataHolder.DEFAULT_SOURCE);
   }

   private static int revokeFromSource(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return revoke(context, ResourceLocationArgument.getId(context, "source"));
   }

   private static int revoke(CommandContext<CommandSourceStack> context, ResourceLocation source) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
      CommandSourceStack src = (CommandSourceStack)context.getSource();
      OriginDataHolder holder = OriginDataHolder.get(target);
      if (!holder.getEntityPowers().containsEntry(source, power)) {
         src.sendFailure(Component.translatable("commands.power.revoke.failure", new Object[]{target.getName(), HolderHelper.string(power), source.toString()}));
         return 0;
      } else {
         holder.revokePower(source, power);
         holder.sync();
         src.sendSuccess(
            () -> Component.translatable("commands.power.revoke.success", new Object[]{target.getName(), HolderHelper.string(power), source.toString()}), true
         );
         return 1;
      }
   }

   private static int clear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      OriginDataHolder holder = OriginDataHolder.get(target);
      List<Entry<ResourceLocation, Holder<Power>>> entries = new ArrayList<>(holder.getData().getPowers().entries());

      for (Entry<ResourceLocation, Holder<Power>> e : entries) {
         holder.revokePower(e.getKey(), e.getValue());
      }

      holder.sync();
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable("commands.power.clear.success", new Object[]{target.getName()}), true);
      return entries.size();
   }

   private static int has(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
      CommandSourceStack src = (CommandSourceStack)context.getSource();
      OriginDataHolder holder = OriginDataHolder.get(target);

      try {
         ResourceLocation source = ResourceLocationArgument.getId(context, "source");
         if (holder.getEntityPowers().containsEntry(source, power)) {
            src.sendSuccess(
               () -> Component.translatable("commands.power.has.success.source", new Object[]{target.getName(), HolderHelper.string(power), source.toString()}),
               false
            );
         } else {
            src.sendFailure(
               Component.translatable("commands.power.has.failure.source", new Object[]{target.getName(), HolderHelper.string(power), source.toString()})
            );
         }
      } catch (Exception var6) {
         if (holder.hasPower(power)) {
            src.sendSuccess(() -> Component.translatable("commands.power.has.success", new Object[]{target.getName(), HolderHelper.string(power)}), false);
         } else {
            src.sendFailure(Component.translatable("commands.power.has.failure", new Object[]{target.getName(), HolderHelper.string(power)}));
         }
      }

      return 1;
   }

   private static int list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      OriginDataHolder holder = OriginDataHolder.get(target);
      String list = holder.getData()
         .getPowers()
         .entries()
         .stream()
         .map(e -> HolderHelper.string((Holder<?>)e.getValue()) + " (" + ((ResourceLocation)e.getKey()).toString() + ")")
         .reduce((a, b) -> a + ", " + b)
         .orElse("(none)");
      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.translatable("commands.power.list.result", new Object[]{target.getName(), list}), false);
      return 1;
   }

   private static int revokeAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      ResourceLocation source = ResourceLocationArgument.getId(context, "source");
      OriginDataHolder holder = OriginDataHolder.get(target);
      holder.revokeAllPowers(source);
      holder.sync();
      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.translatable("commands.power.revokeall.success", new Object[]{target.getName(), source.toString()}), true);
      return 1;
   }

   private static int sources(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      Holder<Power> power = ResourceArgument.getResource(context, "power", PowerRegistries.POWER_KEY);
      OriginDataHolder holder = OriginDataHolder.get(target);
      String list = holder.getData()
         .getPowers()
         .entries()
         .stream()
         .filter(e -> ((Holder)e.getValue()).equals(power))
         .map(Entry::getKey)
         .<String>map(ResourceLocation::toString)
         .reduce((a, b) -> a + ", " + b)
         .orElse("(none)");
      ((CommandSourceStack)context.getSource())
         .sendSuccess(() -> Component.translatable("commands.power.sources.result", new Object[]{target.getName(), HolderHelper.string(power), list}), false);
      return 1;
   }
}
