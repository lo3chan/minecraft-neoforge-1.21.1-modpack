package at.petrak.hexcasting.common.command;

import at.petrak.hexcasting.server.ScrungledPatternsSave;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class RecalcPatternsCommand {
   public static void add(LiteralArgumentBuilder<CommandSourceStack> cmd) {
      cmd.then(((LiteralArgumentBuilder)Commands.literal("recalcPatterns").requires(dp -> dp.hasPermission(3))).executes(ctx -> {
         ServerLevel world = ((CommandSourceStack)ctx.getSource()).getServer().overworld();
         DimensionDataStorage ds = world.getDataStorage();
         ds.set("hexcasting.per-world-patterns.0.1.0", ScrungledPatternsSave.createFromScratch(world.getSeed()));
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.translatable("command.hexcasting.recalc"), true);
         return 1;
      }));
   }
}
