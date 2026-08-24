package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2168;
import net.minecraft.class_2170;

public class DebugCommand_fabric extends AbstractCommand_fabric {
   private static String getDebugString() {
      List<String> lines = new ArrayList<>();
      F3Screen.addStringToDisplay(lines);
      return String.join("\n", lines);
   }

   @Override
   public LiteralArgumentBuilder<class_2168> buildCommand() {
      return (LiteralArgumentBuilder<class_2168>)class_2170.method_9247("debug").executes(c -> this.sendSuccessResponse(c, getDebugString(), false));
   }
}
