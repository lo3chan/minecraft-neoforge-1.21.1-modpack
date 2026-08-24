package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DebugCommand_neoforge extends AbstractCommand_neoforge {
   private static String getDebugString() {
      List<String> lines = new ArrayList<>();
      F3Screen.addStringToDisplay(lines);
      return String.join("\n", lines);
   }

   @Override
   public LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("debug").executes(c -> this.sendSuccessResponse(c, getDebugString(), false));
   }
}
