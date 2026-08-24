package net.joefoxe.hexerei.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.ToggleBookShadersPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ToggleBookShadersCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("hexerei")
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("book_shaders").requires(sender -> sender.hasPermission(0)))
                     .then(Commands.literal("on").executes(context -> execute(context, true))))
                  .then(Commands.literal("off").executes(context -> execute(context, false)))
            )
      );
   }

   private static int execute(CommandContext<CommandSourceStack> context, boolean toggle) {
      ServerPlayer player;
      try {
         player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      } catch (CommandSyntaxException var4) {
         var4.printStackTrace();
         return 1;
      }

      HexereiPacketHandler.sendToPlayerClient(new ToggleBookShadersPacket(toggle), player);
      return 1;
   }

   public static void toggleConfig(boolean enabled) {
      HexConfig.BOOK_SHADERS_TOGGLE.set(enabled);
      HexConfig.BOOK_SHADERS_TOGGLE.save();
   }
}
