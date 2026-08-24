package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CanBeReplacedCallback {
   private final BlockPlaceContext context;

   public CanBeReplacedCallback(BlockPlaceContext blockPlaceContext, BlockState state) {
      this.context = blockPlaceContext;
   }

   public BlockPos getClickedPos() {
      return this.context.getClickedPos();
   }

   public LevelBlock getClickedBlock() {
      return this.getLevel().kjs$getBlock(this.getClickedPos());
   }

   public Direction getNearestLookingDirection() {
      return this.context.getNearestLookingDirection();
   }

   public Direction getNearestLookingVerticalDirection() {
      return this.context.getNearestLookingVerticalDirection();
   }

   public Direction[] getNearestLookingDirections() {
      return this.context.getNearestLookingDirections();
   }

   public Direction getClickedFace() {
      return this.context.getClickedFace();
   }

   public Vec3 getClickLocation() {
      return this.context.getClickLocation();
   }

   public boolean isInside() {
      return this.context.isInside();
   }

   public ItemStack getItem() {
      return this.context.getItemInHand();
   }

   @Nullable
   public Player getPlayer() {
      return this.context.getPlayer();
   }

   public InteractionHand getHand() {
      return this.context.getHand();
   }

   public Level getLevel() {
      return this.context.getLevel();
   }

   public Direction getHorizontalDirection() {
      return this.context.getHorizontalDirection();
   }

   public boolean isSecondaryUseActive() {
      return this.context.isSecondaryUseActive();
   }

   public float getRotation() {
      return this.context.getRotation();
   }

   public FluidState getFluidStateAtClickedPos() {
      return this.context.getLevel().getFluidState(this.context.getClickedPos());
   }

   public boolean isClickedPosIn(Fluid fluid) {
      return this.getFluidStateAtClickedPos().is(fluid);
   }

   public boolean canBeReplaced() {
      return this.getLevel().getBlockState(this.getClickedPos()).canBeReplaced();
   }
}
