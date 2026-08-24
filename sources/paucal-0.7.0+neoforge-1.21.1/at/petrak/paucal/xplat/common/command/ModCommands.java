package at.petrak.paucal.xplat.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {
   public static void register(CommandDispatcher<CommandSourceStack> dp) {
      LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("paucal");
      CommandGetContributorInfo.add(builder);
      CommandReloadContributors.add(builder);
      CommandPatSound.add(builder);
      dp.register(builder);
   }
}
