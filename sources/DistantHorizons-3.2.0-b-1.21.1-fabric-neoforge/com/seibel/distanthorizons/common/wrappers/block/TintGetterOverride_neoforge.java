package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TintGetterOverride_neoforge extends AbstractDhTintGetter_neoforge {
   private LevelReader parent;

   @Override
   public void update(
      BiomeWrapper_neoforge biomeWrapper, BlockStateWrapper_neoforge blockStateWrapper, FullDataSourceV2 fullDataSource, IClientLevelWrapper clientLevelWrapper
   ) {
      super.update(biomeWrapper, blockStateWrapper, fullDataSource, clientLevelWrapper);
      this.parent = (LevelReader)this.clientLevelWrapper.getWrappedMcObject();
   }

   public float getShade(Direction direction, boolean bl) {
      return this.parent.getShade(direction, bl);
   }

   public LevelLightEngine getLightEngine() {
      return this.parent.getLightEngine();
   }

   public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
      return this.parent.getBrightness(lightLayer, blockPos);
   }

   public int getRawBrightness(BlockPos blockPos, int i) {
      return this.parent.getRawBrightness(blockPos, i);
   }

   public boolean canSeeSky(BlockPos blockPos) {
      return this.parent.canSeeSky(blockPos);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      return this.parent.getBlockEntity(blockPos);
   }

   public BlockState getBlockState(BlockPos blockPos) {
      return this.parent.getBlockState(blockPos);
   }

   public FluidState getFluidState(BlockPos blockPos) {
      return this.parent.getFluidState(blockPos);
   }

   public int getLightEmission(BlockPos blockPos) {
      return this.parent.getLightEmission(blockPos);
   }

   public int getMaxLightLevel() {
      return this.parent.getMaxLightLevel();
   }

   public Stream<BlockState> getBlockStates(AABB aABB) {
      return this.parent.getBlockStates(aABB);
   }

   public BlockHitResult clip(ClipContext clipContext) {
      return this.parent.clip(clipContext);
   }

   @Nullable
   public BlockHitResult clipWithInteractionOverride(Vec3 vec3, Vec3 vec32, BlockPos blockPos, VoxelShape voxelShape, BlockState blockState) {
      return this.parent.clipWithInteractionOverride(vec3, vec32, blockPos, voxelShape, blockState);
   }

   public double getBlockFloorHeight(VoxelShape voxelShape, Supplier<VoxelShape> supplier) {
      return this.parent.getBlockFloorHeight(voxelShape, supplier);
   }

   public double getBlockFloorHeight(BlockPos blockPos) {
      return this.parent.getBlockFloorHeight(blockPos);
   }

   public int getMaxBuildHeight() {
      return this.parent.getMaxBuildHeight();
   }

   public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos blockPos, BlockEntityType<T> blockEntityType) {
      return this.parent.getBlockEntity(blockPos, blockEntityType);
   }

   public BlockHitResult isBlockInLine(ClipBlockStateContext clipBlockStateContext) {
      return this.parent.isBlockInLine(clipBlockStateContext);
   }

   public int getHeight() {
      return this.parent.getHeight();
   }

   public int getMinBuildHeight() {
      return this.parent.getMinBuildHeight();
   }

   public int getSectionsCount() {
      return this.parent.getSectionsCount();
   }

   public int getMinSection() {
      return this.parent.getMinSection();
   }

   public int getMaxSection() {
      return this.parent.getMaxSection();
   }

   public boolean isOutsideBuildHeight(BlockPos blockPos) {
      return this.parent.isOutsideBuildHeight(blockPos);
   }

   public boolean isOutsideBuildHeight(int i) {
      return this.parent.isOutsideBuildHeight(i);
   }

   public int getSectionIndex(int i) {
      return this.parent.getSectionIndex(i);
   }

   public int getSectionIndexFromSectionY(int i) {
      return this.parent.getSectionIndexFromSectionY(i);
   }

   public int getSectionYFromSectionIndex(int i) {
      return this.parent.getSectionYFromSectionIndex(i);
   }
}
