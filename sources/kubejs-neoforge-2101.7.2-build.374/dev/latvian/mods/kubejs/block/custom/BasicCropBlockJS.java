package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.KubeJSBlockProperties;
import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BasicCropBlockJS extends CropBlock {
   private final CropBlockBuilder builder;
   private IntegerProperty ageProperty;

   public BasicCropBlockJS(CropBlockBuilder builder) {
      super(builder.createProperties().sound(SoundType.CROP).randomTicks());
      this.builder = builder;
   }

   public IntegerProperty getAgeProperty() {
      if (this.ageProperty == null) {
         this.ageProperty = IntegerProperty.create("age", 0, ((CropBlockBuilder)((KubeJSBlockProperties)this.properties).blockBuilder).age);
      }

      return this.ageProperty;
   }

   public int getMaxAge() {
      return this.builder.age;
   }

   protected ItemLike getBaseSeedId() {
      return (ItemLike)(this.builder.itemBuilder != null ? (ItemLike)this.builder.itemBuilder.get() : Items.AIR);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{this.getAgeProperty()});
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.builder.shapeByAge.get((Integer)blockState.getValue(this.getAgeProperty()));
   }

   public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
      double f = this.builder.growSpeedCallback == null
         ? -1.0
         : this.builder.growSpeedCallback.applyAsDouble(new RandomTickCallback(serverLevel.kjs$getBlock(blockPos).cache(blockState), random));
      int age = this.getAge(blockState);
      if (age < this.getMaxAge()) {
         if (f < 0.0) {
            f = getGrowthSpeed(blockState, serverLevel, blockPos);
         }

         if (f > 0.0 && random.nextInt((int)(25.0 / f) + 1) == 0) {
            serverLevel.setBlock(blockPos, this.getStateForAge(age + 1), 2);
         }
      }
   }

   public void growCrops(Level level, BlockPos blockPos, BlockState blockState) {
      if (this.builder.fertilizerCallback == null) {
         super.growCrops(level, blockPos, blockState);
      } else {
         int effect = this.builder.fertilizerCallback.applyAsInt(new RandomTickCallback(level.kjs$getBlock(blockPos).cache(blockState), level.random));
         if (effect > 0) {
            level.setBlock(blockPos, this.getStateForAge(Integer.min(this.getAge(blockState) + effect, this.getMaxAge())), 2);
         }
      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return this.builder.surviveCallback != null
         ? this.builder.surviveCallback.survive(blockState, levelReader, blockPos)
         : super.canSurvive(blockState, levelReader, blockPos);
   }
}
