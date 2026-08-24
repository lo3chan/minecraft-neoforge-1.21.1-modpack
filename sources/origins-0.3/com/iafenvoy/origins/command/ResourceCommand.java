package com.iafenvoy.origins.command;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.helper.ResourceHelper;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

public final class ResourceCommand {
   public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                           "resource"
                        )
                        .requires(source -> source.hasPermission(2)))
                     .then(
                        Commands.literal("has")
                           .then(
                              Commands.argument("target", EntityArgument.entity())
                                 .then(
                                    Commands.argument("power", ResourceLocationArgument.id())
                                       .suggests(ResourceCommand::suggestAllResource)
                                       .executes(ResourceCommand::has)
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("get")
                        .then(
                           Commands.argument("target", EntityArgument.entity())
                              .then(
                                 Commands.argument("power", ResourceLocationArgument.id())
                                    .suggests(ResourceCommand::suggestResource)
                                    .executes(ResourceCommand::get)
                              )
                        )
                  ))
               .then(
                  Commands.literal("change")
                     .then(
                        Commands.argument("target", EntityArgument.entity())
                           .then(
                              Commands.argument("power", ResourceLocationArgument.id())
                                 .suggests(ResourceCommand::suggestResource)
                                 .then(Commands.argument("value", IntegerArgumentType.integer()).executes(ResourceCommand::change))
                           )
                     )
               ))
            .then(
               Commands.literal("operation")
                  .then(
                     Commands.argument("target", EntityArgument.entity())
                        .then(
                           ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                                         "power", ResourceLocationArgument.id()
                                                      )
                                                      .suggests(ResourceCommand::suggestResource)
                                                      .then(operationBranch(ResourceCommand.Operation.MOD)))
                                                   .then(operationBranch(ResourceCommand.Operation.MUL)))
                                                .then(operationBranch(ResourceCommand.Operation.ADD)))
                                             .then(operationBranch(ResourceCommand.Operation.SUB)))
                                          .then(operationBranch(ResourceCommand.Operation.DIV)))
                                       .then(operationBranch(ResourceCommand.Operation.MIN)))
                                    .then(operationBranch(ResourceCommand.Operation.SET)))
                                 .then(operationBranch(ResourceCommand.Operation.MAX)))
                              .then(operationBranch(ResourceCommand.Operation.SWAP))
                        )
                  )
            )
      );
   }

   private static LiteralArgumentBuilder<CommandSourceStack> operationBranch(ResourceCommand.Operation operation) {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal(operation.symbol)
         .then(
            Commands.argument("sourceEntity", EntityArgument.entity())
               .then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes(ctx -> operation(ctx, operation)))
         );
   }

   private static CompletableFuture<Suggestions> suggestAllResource(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggestResource(
         PowerReference.listAllPowers(((CommandSourceStack)context.getSource()).registryAccess())
            .filter(x -> x.power() instanceof ResourceHelper)
            .flatMap(PowerHolder::stream)
            .map(PowerHolder::id),
         builder
      );
   }

   private static CompletableFuture<Suggestions> suggestResource(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
      ServerPlayer player = EntityArgument.getPlayer(context, "target");
      return SharedSuggestionProvider.suggestResource(
         OriginDataHolder.get(player)
            .getAllPowers()
            .stream()
            .filter(x -> x.power() instanceof ResourceHelper)
            .flatMap(PowerHolder::stream)
            .map(PowerHolder::id),
         builder
      );
   }

   private static int has(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      Entity target = EntityArgument.getEntity(context, "target");
      ResourceLocation power = ResourceLocationArgument.getId(context, "power");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      OriginDataHolder holder = OriginDataHolder.get(target);
      boolean has = getResourceComponent(holder, power, context) != null;
      if (has) {
         source.sendSuccess(() -> Component.translatable("commands.origins.resource.has.success", new Object[]{target.getName(), power.toString()}), false);
      }

      return has ? 1 : 0;
   }

   private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      Entity target = EntityArgument.getEntity(context, "target");
      ResourceLocation power = ResourceLocationArgument.getId(context, "power");
      OriginDataHolder holder = OriginDataHolder.get(target);
      ResourceHelper component = getResourceComponent(holder, power, context);
      if (component == null) {
         return 0;
      } else {
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> Component.translatable(
                  "commands.origins.resource.get.result", new Object[]{target.getName(), power.toString(), component.getValue(holder)}
               ),
               false
            );
         return 1;
      }
   }

   private static int change(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      Entity target = EntityArgument.getEntity(context, "target");
      ResourceLocation power = ResourceLocationArgument.getId(context, "power");
      OriginDataHolder holder = OriginDataHolder.get(target);
      ResourceHelper resource = getResourceComponent(holder, power, context);
      if (resource == null) {
         return 0;
      } else {
         int value = IntegerArgumentType.getInteger(context, "value");
         resource.setValue(holder, resource.getValue(holder) + value);
         holder.sync();
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> Component.translatable(
                  "commands.origins.resource.change.success", new Object[]{target.getName(), power.toString(), value, resource.getValue(holder)}
               ),
               true
            );
         return 1;
      }
   }

   private static int operation(CommandContext<CommandSourceStack> context, ResourceCommand.Operation operation) throws CommandSyntaxException {
      Entity target = EntityArgument.getEntity(context, "target");
      ResourceLocation power = ResourceLocationArgument.getId(context, "power");
      OriginDataHolder holder = OriginDataHolder.get(target);
      ResourceHelper resource = getResourceComponent(holder, power, context);
      if (resource == null) {
         return 0;
      } else {
         Entity sourceEntity = EntityArgument.getEntity(context, "sourceEntity");
         Objective objective = ObjectiveArgument.getObjective(context, "sourceObjective");
         Scoreboard scoreboard = sourceEntity.level().getScoreboard();
         ScoreHolder score = ScoreHolder.forNameOnly(sourceEntity.getScoreboardName());
         int sourceValue = scoreboard.getOrCreatePlayerScore(score, objective).get();
         int targetValue = resource.getValue(holder);
         int newValue = operation.apply(targetValue, sourceValue);
         resource.setValue(holder, newValue);
         holder.sync();
         if (operation == ResourceCommand.Operation.SWAP) {
            scoreboard.getOrCreatePlayerScore(score, objective).set(targetValue);
         } else {
            scoreboard.getOrCreatePlayerScore(score, objective).set(sourceValue);
         }

         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> Component.translatable(
                  "commands.origins.resource.operation.success",
                  new Object[]{target.getName(), power.toString(), operation.symbol, sourceEntity.getName(), objective.getName(), newValue}
               ),
               true
            );
         return 1;
      }
   }

   private static ResourceHelper getResourceComponent(OriginDataHolder holder, ResourceLocation power, CommandContext<CommandSourceStack> context) {
      ResourceHelper resource = holder.getAllPowers()
         .stream()
         .filter(x -> Objects.equals(x.id(), power))
         .findAny()
         .map(PowerHolder::power)
         .filter(ResourceHelper.class::isInstance)
         .map(ResourceHelper.class::cast)
         .orElse(null);
      if (resource != null) {
         return resource;
      } else {
         ((CommandSourceStack)context.getSource())
            .sendFailure(Component.translatable("commands.origins.resource.missing_power", new Object[]{holder.getEntity().getName(), power.toString()}));
         return null;
      }
   }

   private static enum Operation {
      MOD("%="),
      MUL("*="),
      ADD("+="),
      SUB("-="),
      DIV("/="),
      MIN("<"),
      SET("="),
      MAX(">"),
      SWAP("><");

      private final String symbol;

      private Operation(String symbol) {
         this.symbol = symbol;
      }

      private int apply(int targetValue, int sourceValue) {
         return switch (this) {
            case MOD -> sourceValue == 0 ? 0 : targetValue % sourceValue;
            case MUL -> targetValue * sourceValue;
            case ADD -> targetValue + sourceValue;
            case SUB -> targetValue - sourceValue;
            case DIV -> sourceValue == 0 ? 0 : targetValue / sourceValue;
            case MIN -> Math.min(targetValue, sourceValue);
            case SET, SWAP -> sourceValue;
            case MAX -> Math.max(targetValue, sourceValue);
         };
      }
   }
}
