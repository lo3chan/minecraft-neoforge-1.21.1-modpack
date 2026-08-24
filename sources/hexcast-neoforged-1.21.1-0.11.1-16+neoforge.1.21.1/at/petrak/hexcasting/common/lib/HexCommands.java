package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.common.command.BrainsweepCommand;
import at.petrak.hexcasting.common.command.ListPerWorldPatternsCommand;
import at.petrak.hexcasting.common.command.PatternTexturesCommand;
import at.petrak.hexcasting.common.command.RecalcPatternsCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HexCommands {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> mainCmd = Commands.literal("hexcasting");
      BrainsweepCommand.add(mainCmd);
      ListPerWorldPatternsCommand.add(mainCmd);
      RecalcPatternsCommand.add(mainCmd);
      PatternTexturesCommand.add(mainCmd);
      dispatcher.register(mainCmd);
   }
}
