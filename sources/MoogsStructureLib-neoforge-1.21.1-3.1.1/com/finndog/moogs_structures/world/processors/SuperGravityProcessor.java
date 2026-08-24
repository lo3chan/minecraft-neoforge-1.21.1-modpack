package com.finndog.moogs_structures.world.processors;

import com.finndog.moogs_structures.modinit.MoogsStructuresProcessors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class SuperGravityProcessor extends StructureProcessor {
   public static final MapCodec<SuperGravityProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Types.CODEC.fieldOf("heightmap").orElse(Types.WORLD_SURFACE_WG).forGetter(p -> p.heightmap),
            Codec.INT.fieldOf("offset").orElse(0).forGetter(p -> p.offset),
            BuiltInRegistries.BLOCK
               .byNameCodec()
               .listOf()
               .fieldOf("ignore_block")
               .orElse(new ArrayList())
               .xmap(HashSet::new, ArrayList::new)
               .forGetter(p -> p.blocksToIgnore),
            Codec.BOOL.fieldOf("require_water_surface").orElse(false).forGetter(p -> p.requireWaterSurface)
         )
         .apply(instance, SuperGravityProcessor::new)
   );
   private final Types heightmap;
   private final int offset;
   private final HashSet<Block> blocksToIgnore;
   private final boolean requireWaterSurface;

   public SuperGravityProcessor(Types types, int offset, HashSet<Block> blocksToIgnore, boolean requireWaterSurface) {
      this.heightmap = types;
      this.offset = offset;
      this.blocksToIgnore = blocksToIgnore;
      this.requireWaterSurface = requireWaterSurface;
   }

   @Nullable
   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos pos,
      BlockPos blockPos,
      StructureBlockInfo structureBlockInfoLocal,
      StructureBlockInfo structureBlockInfoWorld,
      StructurePlaceSettings placeSettings
   ) {
      Types heightmap$types;
      if (levelReader instanceof ServerLevel) {
         if (this.heightmap == Types.WORLD_SURFACE_WG) {
            heightmap$types = Types.WORLD_SURFACE;
         } else if (this.heightmap == Types.OCEAN_FLOOR_WG) {
            heightmap$types = Types.OCEAN_FLOOR;
         } else {
            heightmap$types = this.heightmap;
         }
      } else {
         heightmap$types = this.heightmap;
      }

      int heightmapY = levelReader.getHeight(heightmap$types, structureBlockInfoWorld.pos().getX(), structureBlockInfoWorld.pos().getZ());
      int localY = structureBlockInfoLocal.pos().getY();
      MutableBlockPos mutable = new MutableBlockPos();
      mutable.set(structureBlockInfoWorld.pos().getX(), heightmapY, structureBlockInfoWorld.pos().getZ());
      BlockState aboveState = levelReader.getBlockState(mutable);
      mutable.move(Direction.DOWN);

      for (BlockState currentState = levelReader.getBlockState(mutable);
         this.blocksToIgnore.contains(currentState.getBlock()) || this.requireWaterSurface && currentState.getFluidState().is(FluidTags.WATER);
         currentState = levelReader.getBlockState(mutable)
      ) {
         aboveState = currentState;
         mutable.move(Direction.DOWN);
      }

      return (this.requireWaterSurface ? !aboveState.getFluidState().is(FluidTags.WATER) : !aboveState.isAir())
         ? null
         : new StructureBlockInfo(
            new BlockPos(structureBlockInfoWorld.pos().getX(), mutable.getY() + localY + this.offset, structureBlockInfoWorld.pos().getZ()),
            structureBlockInfoWorld.state(),
            structureBlockInfoWorld.nbt()
         );
   }

   protected StructureProcessorType<?> getType() {
      return MoogsStructuresProcessors.SUPER_GRAVITY_PROCESSOR.get();
   }
}
