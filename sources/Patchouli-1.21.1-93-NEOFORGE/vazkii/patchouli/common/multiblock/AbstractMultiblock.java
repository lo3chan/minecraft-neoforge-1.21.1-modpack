package vazkii.patchouli.common.multiblock;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.TriPredicate;
import vazkii.patchouli.common.util.RotationUtil;

public abstract class AbstractMultiblock implements IMultiblock, BlockAndTintGetter {
   public ResourceLocation id;
   protected int offX;
   protected int offY;
   protected int offZ;
   protected int viewOffX;
   protected int viewOffY;
   protected int viewOffZ;
   private boolean symmetrical;
   Level world;
   private final transient Map<BlockPos, BlockEntity> teCache = new HashMap<>();

   @Override
   public IMultiblock offset(int x, int y, int z) {
      return this.setOffset(this.offX + x, this.offY + y, this.offZ + z);
   }

   public IMultiblock setOffset(int x, int y, int z) {
      this.offX = x;
      this.offY = y;
      this.offZ = z;
      return this.setViewOffset(x, y, z);
   }

   void setViewOffset() {
      this.setViewOffset(this.offX, this.offY, this.offZ);
   }

   @Override
   public IMultiblock offsetView(int x, int y, int z) {
      return this.setViewOffset(this.viewOffX + x, this.viewOffY + y, this.viewOffZ + z);
   }

   public IMultiblock setViewOffset(int x, int y, int z) {
      this.viewOffX = x;
      this.viewOffY = y;
      this.viewOffZ = z;
      return this;
   }

   @Override
   public IMultiblock setSymmetrical(boolean symmetrical) {
      this.symmetrical = symmetrical;
      return this;
   }

   @Override
   public ResourceLocation getID() {
      return this.id;
   }

   @Override
   public IMultiblock setId(ResourceLocation res) {
      this.id = res;
      return this;
   }

   @Override
   public void place(Level world, BlockPos pos, Rotation rotation) {
      this.setWorld(world);
      ((Collection)this.simulate(world, pos, rotation, false).getSecond()).forEach(r -> {
         BlockPos placePos = r.getWorldPosition();
         BlockState targetState = r.getStateMatcher().getDisplayedState(world.getGameTime()).rotate(rotation);
         if (!targetState.isAir() && targetState.canSurvive(world, placePos) && world.getBlockState(placePos).canBeReplaced()) {
            world.setBlockAndUpdate(placePos, targetState);
         }
      });
   }

   @Override
   public Rotation validate(Level world, BlockPos pos) {
      if (this.isSymmetrical() && this.validate(world, pos, Rotation.NONE)) {
         return Rotation.NONE;
      } else {
         for (Rotation rot : Rotation.values()) {
            if (this.validate(world, pos, rot)) {
               return rot;
            }
         }

         return null;
      }
   }

   @Override
   public boolean validate(Level world, BlockPos pos, Rotation rotation) {
      this.setWorld(world);
      Pair<BlockPos, Collection<IMultiblock.SimulateResult>> sim = this.simulate(world, pos, rotation, false);
      return ((Collection)sim.getSecond()).stream().allMatch(r -> {
         BlockPos checkPos = r.getWorldPosition();
         TriPredicate<BlockGetter, BlockPos, BlockState> pred = r.getStateMatcher().getStatePredicate();
         BlockState state = world.getBlockState(checkPos).rotate(RotationUtil.fixHorizontal(rotation));
         return pred.test(world, checkPos, state);
      });
   }

   @Override
   public boolean isSymmetrical() {
      return this.symmetrical;
   }

   public void setWorld(Level world) {
      this.world = world;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      BlockState state = this.getBlockState(pos);
      return state.getBlock() instanceof EntityBlock
         ? this.teCache.computeIfAbsent(pos.immutable(), p -> ((EntityBlock)state.getBlock()).newBlockEntity(pos, state))
         : null;
   }

   public FluidState getFluidState(BlockPos pos) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Override
   public abstract Vec3i getSize();

   public float getShade(Direction direction, boolean shaded) {
      return 1.0F;
   }

   public LevelLightEngine getLightEngine() {
      return null;
   }

   public int getBlockTint(BlockPos pos, ColorResolver color) {
      Biome plains = (Biome)this.world.registryAccess().registryOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
      return color.getColor(plains, pos.getX(), pos.getZ());
   }

   public int getBrightness(LightLayer type, BlockPos pos) {
      return 15;
   }

   public int getRawBrightness(BlockPos pos, int ambientDarkening) {
      return 15 - ambientDarkening;
   }

   public int getHeight() {
      return 255;
   }

   public int getMinBuildHeight() {
      return 0;
   }
}
