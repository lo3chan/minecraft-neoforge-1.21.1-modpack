package net.mehvahdjukaar.moonlight.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

public class IUsedToRollTheDice implements Command<CommandSourceStack> {
   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext dispatcher) {
      return ((LiteralArgumentBuilder)Commands.literal("roll").requires(cs -> cs.hasPermission(0)))
         .then(Commands.argument("dice", IntegerArgumentType.integer(1)).executes(new IUsedToRollTheDice()));
   }

   public int run(CommandContext<CommandSourceStack> context) {
      RandomSource r = ((CommandSourceStack)context.getSource()).getLevel().random;
      int dice = IntegerArgumentType.getInteger(context, "dice");
      int roll = r.nextInt(dice);
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable("commands.moonlight.dice", new Object[]{dice, roll}), false);
      return roll;
   }
}
