package at.petrak.hexcasting.common.command;

import at.petrak.hexcasting.client.render.PatternTextureManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PatternTexturesCommand {
   public static void add(LiteralArgumentBuilder<CommandSourceStack> cmd) {
      cmd.then(((LiteralArgumentBuilder)Commands.literal("textureToggle").requires(dp -> dp.hasPermission(3))).executes(ctx -> {
         PatternTextureManager.useTextures = !PatternTextureManager.useTextures;
         return 1;
      }));
      cmd.then(((LiteralArgumentBuilder)Commands.literal("textureRepaint").requires(dp -> dp.hasPermission(3))).executes(ctx -> {
         PatternTextureManager.repaint();
         return 1;
      }));
      cmd.then(
         ((LiteralArgumentBuilder)Commands.literal("textureSetResolutionScaler").requires(dp -> dp.hasPermission(3)))
            .then(Commands.argument("integer", IntegerArgumentType.integer()).executes(ctx -> {
               PatternTextureManager.setResolutionScaler(IntegerArgumentType.getInteger(ctx, "integer"));
               PatternTextureManager.repaint();
               return 1;
            }))
      );
   }
}
