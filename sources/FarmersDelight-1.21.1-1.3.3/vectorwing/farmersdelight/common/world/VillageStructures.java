package vectorwing.farmersdelight.common.world;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class VillageStructures {
   public static void addNewVillageBuilding(ServerAboutToStartEvent event) {
      if (Configuration.GENERATE_VILLAGE_COMPOST_HEAPS.get()) {
         Registry<StructureTemplatePool> templatePools = (Registry<StructureTemplatePool>)event.getServer()
            .registryAccess()
            .registry(Registries.TEMPLATE_POOL)
            .get();
         Registry<StructureProcessorList> processorLists = (Registry<StructureProcessorList>)event.getServer()
            .registryAccess()
            .registry(Registries.PROCESSOR_LIST)
            .get();
         addBuildingToPool(
            templatePools, processorLists, ResourceLocation.parse("minecraft:village/plains/houses"), "farmersdelight:village/houses/plains_compost_pile", 5
         );
         addBuildingToPool(
            templatePools, processorLists, ResourceLocation.parse("minecraft:village/snowy/houses"), "farmersdelight:village/houses/snowy_compost_pile", 3
         );
         addBuildingToPool(
            templatePools, processorLists, ResourceLocation.parse("minecraft:village/savanna/houses"), "farmersdelight:village/houses/savanna_compost_pile", 4
         );
         addBuildingToPool(
            templatePools, processorLists, ResourceLocation.parse("minecraft:village/desert/houses"), "farmersdelight:village/houses/desert_compost_pile", 3
         );
         addBuildingToPool(
            templatePools, processorLists, ResourceLocation.parse("minecraft:village/taiga/houses"), "farmersdelight:village/houses/taiga_compost_pile", 4
         );
      }

      if (Configuration.GENERATE_VILLAGE_FARM_FD_CROPS.get()) {
         Registry<StructureProcessorList> processorLists = (Registry<StructureProcessorList>)event.getServer()
            .registryAccess()
            .registry(Registries.PROCESSOR_LIST)
            .orElseThrow();
         StructureProcessor temperateCropProcessor = new RuleProcessor(
            List.of(
               new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, ModBlocks.CABBAGE_CROP.get().defaultBlockState()),
               new ProcessorRule(
                  new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, ((TomatoBlock)ModBlocks.TOMATO_CROP.get()).defaultBlockState()
               ),
               new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, ModBlocks.ONION_CROP.get().defaultBlockState())
            )
         );
         StructureProcessor coldCropProcessor = new RuleProcessor(
            List.of(
               new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, ModBlocks.CABBAGE_CROP.get().defaultBlockState()),
               new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, ModBlocks.ONION_CROP.get().defaultBlockState()),
               new ProcessorRule(new RandomBlockMatchTest(Blocks.POTATOES, 0.2F), AlwaysTrueTest.INSTANCE, ModBlocks.CABBAGE_CROP.get().defaultBlockState()),
               new ProcessorRule(new RandomBlockMatchTest(Blocks.POTATOES, 0.2F), AlwaysTrueTest.INSTANCE, ModBlocks.ONION_CROP.get().defaultBlockState())
            )
         );
         StructureProcessor aridCropProcessor = new RuleProcessor(
            List.of(
               new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, ModBlocks.CABBAGE_CROP.get().defaultBlockState()),
               new ProcessorRule(
                  new RandomBlockMatchTest(Blocks.WHEAT, 0.3F), AlwaysTrueTest.INSTANCE, ((TomatoBlock)ModBlocks.TOMATO_CROP.get()).defaultBlockState()
               )
            )
         );
         addNewRuleToProcessorList(ResourceLocation.parse("minecraft:farm_plains"), temperateCropProcessor, processorLists);
         addNewRuleToProcessorList(ResourceLocation.parse("minecraft:farm_savanna"), aridCropProcessor, processorLists);
         addNewRuleToProcessorList(ResourceLocation.parse("minecraft:farm_snowy"), coldCropProcessor, processorLists);
         addNewRuleToProcessorList(ResourceLocation.parse("minecraft:farm_taiga"), temperateCropProcessor, processorLists);
         addNewRuleToProcessorList(ResourceLocation.parse("minecraft:farm_desert"), aridCropProcessor, processorLists);
      }
   }

   public static void addBuildingToPool(
      Registry<StructureTemplatePool> templatePoolRegistry,
      Registry<StructureProcessorList> processorListRegistry,
      ResourceLocation poolRL,
      String nbtPieceRL,
      int weight
   ) {
      StructureTemplatePool pool = (StructureTemplatePool)templatePoolRegistry.get(poolRL);
      if (pool != null) {
         ResourceLocation emptyProcessor = ResourceLocation.withDefaultNamespace("empty");
         Holder<StructureProcessorList> processorHolder = processorListRegistry.getHolderOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, emptyProcessor));
         SinglePoolElement piece = (SinglePoolElement)SinglePoolElement.single(nbtPieceRL, processorHolder).apply(Projection.RIGID);

         for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
         }

         List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
         listOfPieceEntries.add(new Pair(piece, weight));
         pool.rawTemplates = listOfPieceEntries;
      }
   }

   private static void addNewRuleToProcessorList(
      ResourceLocation targetProcessorList, StructureProcessor processorToAdd, Registry<StructureProcessorList> processorListRegistry
   ) {
      processorListRegistry.getOptional(targetProcessorList).ifPresent(processorList -> {
         List<StructureProcessor> newSafeList = new ArrayList<>(processorList.list());
         newSafeList.add(processorToAdd);
         processorList.list = newSafeList;
      });
   }
}
