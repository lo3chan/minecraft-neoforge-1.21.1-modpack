package io.github.razordevs.deep_aether.world.structure;

import com.aetherteam.aether.world.processor.DoubleDropsProcessor;
import io.github.razordevs.deep_aether.world.structure.brass.BrassDungeonPiece;
import io.github.razordevs.deep_aether.world.structure.brass.processor.BrassDungeonRoomProcessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class DAStructureProcessorLists {
   public static final ResourceKey<StructureProcessorList> BRASS_ROOM = createKey("bronze_room");
   public static final ResourceKey<StructureProcessorList> BRASS_BOSS_ROOM = createKey("bronze_boss_room");

   private static ResourceKey<StructureProcessorList> createKey(String name) {
      return ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.fromNamespaceAndPath("deep_aether", name));
   }

   public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
      register(context, BRASS_ROOM, createBrassDungeonList(BrassDungeonPiece.TRAPPED_SKYROOT_PLANKS));
      register(context, BRASS_BOSS_ROOM, createBrassDungeonList(BrassDungeonPiece.TRAPPED_SKYROOT_PLANKS, BrassDungeonRoomProcessor.INSTANCE));
   }

   private static void register(BootstrapContext<StructureProcessorList> context, ResourceKey<StructureProcessorList> key, List<StructureProcessor> processors) {
      context.register(key, new StructureProcessorList(processors));
   }

   private static List<StructureProcessor> createBrassDungeonList(StructureProcessor... ruleProcessor) {
      List<StructureProcessor> list = new ArrayList<StructureProcessor>() {
         {
            this.add(BrassDungeonPiece.LOCKED_NIMBUS_STONE);
            this.add(BrassDungeonPiece.TRAPPED_SKYROOT_PLANKS);
            this.add(BrassDungeonPiece.MOSS_CARPET);
            this.add(BrassDungeonPiece.COBWEB);
            this.add(BrassDungeonPiece.FLOWERING_ROSEROOT_LEAVES);
            this.add(DoubleDropsProcessor.INSTANCE);
         }
      };
      list.addAll(List.of(ruleProcessor));
      return list;
   }
}
