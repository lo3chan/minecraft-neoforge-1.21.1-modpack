package net.mehvahdjukaar.moonlight.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.world.level.block.Block;

public class BlockStateStatsCommand implements Command<CommandSourceStack> {
   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext dispatcher) {
      return ((LiteralArgumentBuilder)Commands.literal("blockstate_stats").requires(cs -> cs.hasPermission(4))).executes(new BlockStateStatsCommand());
   }

   public int run(CommandContext<CommandSourceStack> context) {
      Registry<Block> registry = BuiltInRegistries.BLOCK;
      Map<String, Integer> modBlockCounts = new HashMap<>();
      Map<String, Integer> modBlockStateCounts = new HashMap<>();
      List<Block> blocksList = new ArrayList<>();
      int totalBlocks = 0;
      int totalBlockStates = 0;

      for (Block block : registry) {
         totalBlocks++;
         blocksList.add(block);
         String modId = block.builtInRegistryHolder().key().location().getNamespace();
         modBlockCounts.put(modId, modBlockCounts.getOrDefault(modId, 0) + 1);
         int blockStateCount = block.getStateDefinition().getPossibleStates().size();
         totalBlockStates += blockStateCount;
         modBlockStateCounts.put(modId, modBlockStateCounts.getOrDefault(modId, 0) + blockStateCount);
      }

      double averageBlockStates = (double)totalBlockStates / totalBlocks;
      Path outputPath = PlatHelper.getGamePath().resolve("blockstate_stats.txt");

      try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
         writer.write("=== Minecraft Blockstate Statistics ===\n");
         writer.write(String.format("Total blocks: %d%n", totalBlocks));
         writer.write(String.format("Total blockstates: %d%n", totalBlockStates));
         writer.write(String.format("Average blockstates per block: %.2f%n%n", averageBlockStates));
         writer.write("--- Blocks per Mod ---\n");
         modBlockCounts.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).forEach(entry -> {
            try {
               String mod = entry.getKey();
               writer.write(String.format("%s: %d blocks, %d blockstates%n", mod, modBlockCounts.get(mod), modBlockStateCounts.get(mod)));
            } catch (IOException var5x) {
            }
         });
         writer.write("\n");
         writer.write("--- Blocks with More Than Average Blockstates ---\n");
         blocksList.stream()
            .map(blockx -> new SimpleEntry<>(blockx, blockx.getStateDefinition().getPossibleStates().size()))
            .filter(entry -> entry.getValue().intValue() > averageBlockStates)
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .forEach(entry -> {
               try {
                  String blockId = BuiltInRegistries.BLOCK.getKey(entry.getKey()).toString();
                  int stateCount = entry.getValue();
                  writer.write(String.format("%s: %d blockstates%n", blockId, stateCount));
               } catch (IOException var4x) {
               }
            });
      } catch (IOException var16) {
         Moonlight.LOGGER.error("Failed to write blockstate statistics to file: {}", var16.getMessage());
      }

      String totalBlockStateString = totalBlockStates + "";
      String totalBlocksString = totalBlocks + "";
      ((CommandSourceStack)context.getSource())
         .sendSuccess(
            () -> {
               MutableComponent clickablePath = Component.literal(" [" + outputPath.toString() + "]").withStyle(style -> style.withColor(ChatFormatting.AQUA));
               if (PlatHelper.isIntegratedServer()) {
                  clickablePath.withStyle(style -> style.withClickEvent(new ClickEvent(Action.OPEN_FILE, outputPath.toString())));
               }

               MutableComponent message = Component.translatable(
                  "commands.moonlight.blockstate_stats", new Object[]{totalBlocksString, totalBlockStateString, clickablePath}
               );
               return message.append(clickablePath);
            },
            false
         );
      return 0;
   }
}
