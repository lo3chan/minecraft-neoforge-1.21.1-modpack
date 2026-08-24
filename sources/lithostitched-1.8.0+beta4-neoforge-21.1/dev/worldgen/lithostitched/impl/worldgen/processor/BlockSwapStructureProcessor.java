package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public final class BlockSwapStructureProcessor extends StructureProcessor {
   public static final MapCodec<BlockSwapStructureProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.unboundedMap(ResourceKey.codec(Registries.BLOCK), ResourceKey.codec(Registries.BLOCK))
               .fieldOf("blocks")
               .forGetter(BlockSwapStructureProcessor::blockSwapMap)
         )
         .apply(instance, BlockSwapStructureProcessor::new)
   );
   public static final StructureProcessorType<BlockSwapStructureProcessor> TYPE = () -> CODEC;
   private final Map<ResourceKey<Block>, ResourceKey<Block>> blockSwapMap;

   public BlockSwapStructureProcessor(Map<ResourceKey<Block>, ResourceKey<Block>> blockSwapMap) {
      this.blockSwapMap = blockSwapMap;
   }

   public Map<ResourceKey<Block>, ResourceKey<Block>> blockSwapMap() {
      return this.blockSwapMap;
   }

   public StructureBlockInfo processBlock(
      LevelReader level, BlockPos pos, BlockPos pivot, StructureBlockInfo relative, StructureBlockInfo absolute, StructurePlaceSettings settings
   ) {
      RegistryLookup<Block> registry = level.registryAccess().lookupOrThrow(Registries.BLOCK);
      ResourceKey<Block> key = absolute.state().getBlock().builtInRegistryHolder().key();
      if (this.blockSwapMap.containsKey(key)) {
         Optional<Reference<Block>> newBlock = registry.get(this.blockSwapMap.get(key));
         if (newBlock.isPresent()) {
            return new StructureBlockInfo(absolute.pos(), ((Block)newBlock.get().value()).withPropertiesOf(absolute.state()), absolute.nbt());
         }
      }

      return absolute;
   }

   protected StructureProcessorType<?> getType() {
      return TYPE;
   }
}
