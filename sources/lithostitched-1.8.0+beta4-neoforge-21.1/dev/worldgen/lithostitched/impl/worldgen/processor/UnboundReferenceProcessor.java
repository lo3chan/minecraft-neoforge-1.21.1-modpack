package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class UnboundReferenceProcessor extends StructureProcessor {
   public static final MapCodec<UnboundReferenceProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(ResourceKey.codec(Registries.PROCESSOR_LIST).fieldOf("name").forGetter(UnboundReferenceProcessor::name))
         .apply(instance, UnboundReferenceProcessor::new)
   );
   public static final StructureProcessorType<UnboundReferenceProcessor> TYPE = () -> CODEC;
   private final ResourceKey<StructureProcessorList> name;

   private UnboundReferenceProcessor(ResourceKey<StructureProcessorList> name) {
      this.name = name;
   }

   public static UnboundReferenceProcessor of(String name) {
      return new UnboundReferenceProcessor(key(Lithostitched.id(name)));
   }

   private static ResourceKey<StructureProcessorList> key(ResourceLocation identifier) {
      return ResourceKey.create(Registries.PROCESSOR_LIST, identifier);
   }

   public ResourceKey<StructureProcessorList> name() {
      return this.name;
   }

   public StructureBlockInfo processBlock(
      LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureBlockInfo relative, StructureBlockInfo absolute, StructurePlaceSettings settings
   ) {
      StructureBlockInfo processedBlock = absolute;
      Registry<StructureProcessorList> registry = Lithostitched.registry(levelReader.registryAccess(), Registries.PROCESSOR_LIST);
      Optional<StructureProcessorList> list = registry.getOptional(this.name);
      if (list.isPresent()) {
         for (StructureProcessor processor : list.get().list()) {
            processedBlock = processor.processBlock(levelReader, pos, pivot, relative, processedBlock, settings);
            if (processedBlock == null) {
               return null;
            }
         }
      }

      return processedBlock;
   }

   protected StructureProcessorType<?> getType() {
      return TYPE;
   }
}
