package net.joefoxe.hexerei.client.renderer.color;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.Coffer;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ModBlockColors {
   public static final BlockColor WATER_COLOR = setDynamicBlockColorProvider(1.0, 0.5);
   public static final BlockColor GRASS_COLOR = setDynamicBlockColorProviderGrass(1.0, 0.5);

   public static BlockColor setDynamicBlockColorProvider(double temp, double humidity) {
      return (unknown, lightReader, pos, unknown2) -> {
         assert lightReader != null;

         return BiomeColors.getAverageWaterColor(lightReader, pos);
      };
   }

   public static BlockColor setDynamicBlockColorProviderGrass(double temp, double humidity) {
      return (unknown, lightReader, pos, unknown2) -> {
         assert lightReader != null;

         return BiomeColors.getAverageGrassColor(lightReader, pos);
      };
   }

   @SubscribeEvent
   public static void onBlockColorsInit(Block event) {
      BlockColors blockColors = event.getBlockColors();
      event.register(
         (state, reader, pos, color) -> reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5, 0.5),
         new net.minecraft.world.level.block.Block[]{(net.minecraft.world.level.block.Block)ModBlocks.LILY_PAD_BLOCK.get()}
      );
      event.register(
         (state, reader, pos, color) -> reader != null && pos != null ? Coffer.getColorValue(state, pos, reader) : 4464659,
         new net.minecraft.world.level.block.Block[]{
            (net.minecraft.world.level.block.Block)ModBlocks.COFFER.get(), (net.minecraft.world.level.block.Block)ModBlocks.ENTANGLED_COFFER.get()
         }
      );
      event.register(
         (state, reader, pos, color) -> ConnectingCarpetDyed.getColorValue(state),
         new net.minecraft.world.level.block.Block[]{
            (net.minecraft.world.level.block.Block)ModBlocks.INFUSED_FABRIC_CARPET.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.INFUSED_FABRIC_BLOCK.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get()
         }
      );
      event.register(
         (state, reader, pos, color) -> reader != null && pos != null ? Coffer.getColorValue(state, pos, reader) : 4464659,
         new net.minecraft.world.level.block.Block[]{
            (net.minecraft.world.level.block.Block)ModBlocks.BOOK_OF_SHADOWS_BACK.get(),
            (net.minecraft.world.level.block.Block)ModBlocks.BOOK_OF_SHADOWS_COVER.get()
         }
      );
   }
}
