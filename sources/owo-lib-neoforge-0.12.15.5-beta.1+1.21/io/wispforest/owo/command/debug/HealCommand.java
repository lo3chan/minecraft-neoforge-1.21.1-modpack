package io.wispforest.owo.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HealCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("heal").executes(HealCommand::executeFullHeal))
               .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F)).executes(HealCommand::executeSelfHeal)))
            .then(
               ((RequiredArgumentBuilder)Commands.argument("entity", EntityArgument.entity()).executes(HealCommand::executeTargetedFullHeal))
                  .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F)).executes(HealCommand::executeTargetedHeal))
            )
      );
   }

   private static int executeFullHeal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      Entity target = ((CommandSourceStack)context.getSource()).getEntityOrException();
      return executeHeal(context, target, target instanceof LivingEntity living ? living.getMaxHealth() : 3.4028235E38F);
   }

   private static int executeSelfHeal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return executeHeal(context, ((CommandSourceStack)context.getSource()).getEntityOrException(), FloatArgumentType.getFloat(context, "amount"));
   }

   private static int executeTargetedFullHeal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      Entity target = EntityArgument.getEntity(context, "entity");
      return executeHeal(context, target, target instanceof LivingEntity living ? living.getMaxHealth() : 3.4028235E38F);
   }

   private static int executeTargetedHeal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return executeHeal(context, EntityArgument.getEntity(context, "entity"), FloatArgumentType.getFloat(context, "amount"));
   }

   private static int executeHeal(CommandContext<CommandSourceStack> context, Entity entity, float amount) throws CommandSyntaxException {
      if (entity instanceof LivingEntity living) {
         float healed = living.getHealth();
         living.heal(amount);
         healed = living.getHealth() - healed;
         ((CommandSourceStack)context.getSource())
            .sendSuccess(
               () -> TextOps.concat(
                  Owo.PREFIX, TextOps.withColor("healed §" + healed + " §hp", TextOps.color(ChatFormatting.GRAY), 12157951, TextOps.color(ChatFormatting.GRAY))
               ),
               false
            );
      } else {
         ((CommandSourceStack)context.getSource()).sendFailure(TextOps.concat(Owo.PREFIX, Component.nullToEmpty("Cannot heal non living entity")));
      }

      return (int)Math.floor(amount);
   }
}
