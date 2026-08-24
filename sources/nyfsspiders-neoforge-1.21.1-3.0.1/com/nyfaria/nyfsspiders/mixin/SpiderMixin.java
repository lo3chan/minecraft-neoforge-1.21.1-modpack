package com.nyfaria.nyfsspiders.mixin;

import com.nyfaria.awcapi.ClimberHelper;
import com.nyfaria.awcapi.entity.ClimberComponent;
import com.nyfaria.awcapi.entity.IAdvancedClimber;
import com.nyfaria.awcapi.entity.movement.ClimberPathNavigator;
import com.nyfaria.nyfsspiders.Config;
import com.nyfaria.nyfsspiders.common.ModTags;
import com.nyfaria.nyfsspiders.common.entity.goal.BetterLeapAtTargetGoal;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Spider.class})
public abstract class SpiderMixin extends Monster implements IAdvancedClimber {
   @Unique
   private ClimberComponent nyfsspiders$climberComponent;
   @Unique
   private boolean nyfsspiders$pathFinderDebugPreview;
   @Unique
   private static final AttributeModifier FOLLOW_RANGE_INCREASE = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("nyfsspiders", "spider_follow_range_increase"), 8.0, Operation.ADD_VALUE
   );

   protected SpiderMixin(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void nyfsspiders$onConstructed(EntityType<? extends Spider> entityType, Level level, CallbackInfo ci) {
      this.nyfsspiders$climberComponent = new ClimberComponent(this);
      ClimberHelper.initClimber(this);
      this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(FOLLOW_RANGE_INCREASE);
   }

   @Inject(
      method = {"defineSynchedData"},
      at = {@At("RETURN")}
   )
   private void nyfsspiders$onDefineSynchedData(CallbackInfo ci) {
      this.nyfsspiders$pathFinderDebugPreview = (Boolean)Config.PATH_FINDER_DEBUG_PREVIEW.get();
   }

   @Inject(
      method = {"createNavigation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyfsspiders$onCreateNavigation(Level level, CallbackInfoReturnable<PathNavigation> ci) {
      ClimberPathNavigator<SpiderMixin> navigator = new ClimberPathNavigator(this, level, false);
      navigator.setCanFloat(true);
      ci.setReturnValue(navigator);
   }

   @Redirect(
      method = {"registerGoals"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"
      )
   )
   private void nyfsspiders$onAddGoal(GoalSelector selector, int priority, Goal task) {
      if (task instanceof LeapAtTargetGoal) {
         selector.addGoal(3, new BetterLeapAtTargetGoal(this, 0.4F));
      } else if (task instanceof TargetGoal targetGoal) {
         selector.addGoal(2, targetGoal.setUnseenMemoryTicks(200));
      } else {
         selector.addGoal(priority, task);
      }
   }

   public ClimberComponent getClimberComponent() {
      return this.nyfsspiders$climberComponent;
   }

   public Mob asMob() {
      return this;
   }

   public float getMovementSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   public float getBlockSlipperiness(BlockPos pos) {
      BlockState state = this.level().getBlockState(pos);
      float slipperiness = state.getBlock().getFriction() * 0.91F;
      if (state.is(ModTags.NON_CLIMBABLE)) {
         slipperiness = 1.0F - (1.0F - slipperiness) * 0.25F;
      }

      return slipperiness;
   }

   public boolean canClimbOnBlock(BlockState state, BlockPos pos) {
      return !state.is(ModTags.NON_CLIMBABLE);
   }

   public boolean shouldTrackPathingTargets() {
      return this.nyfsspiders$pathFinderDebugPreview;
   }

   public void setLerpYRot(Float yRot) {
      this.lerpYRot = yRot != null ? yRot.doubleValue() : 0.0;
   }

   public void setLerpXRot(Float xRot) {
      this.lerpXRot = xRot != null ? xRot.doubleValue() : 0.0;
   }

   public void setLerpYHeadRot(Float yHeadRot) {
      this.lerpYHeadRot = yHeadRot != null ? yHeadRot.doubleValue() : 0.0;
   }

   public void setLerpHeadSteps(int steps) {
      this.lerpHeadSteps = steps;
   }

   public Direction getGroundSide() {
      return this.getClimberComponent().getGroundSide();
   }

   public void onPathingObstructed(Direction facing) {
   }

   public int getMaxStuckCheckTicks() {
      return 40;
   }

   public float getBridgePathingMalus(Mob entity, BlockPos pos, @Nullable Node fallPathPoint) {
      return -1.0F;
   }

   public float getPathingMalus(BlockGetter cache, Mob entity, PathType nodeType, BlockPos pos, Vec3i direction, Predicate<Direction> sides) {
      if (direction.getY() != 0) {
         boolean hasClimbableNeighbor = false;
         MutableBlockPos offsetPos = new MutableBlockPos();

         for (Direction offset : Direction.values()) {
            if (sides.test(offset)) {
               offsetPos.set(pos.getX() + offset.getStepX(), pos.getY() + offset.getStepY(), pos.getZ() + offset.getStepZ());
               BlockState state = cache.getBlockState(offsetPos);
               if (this.canClimbOnBlock(state, offsetPos)) {
                  hasClimbableNeighbor = true;
                  break;
               }
            }
         }

         if (!hasClimbableNeighbor) {
            return -1.0F;
         }
      }

      return entity.getPathfindingMalus(nodeType);
   }

   public void pathFinderCleanup() {
   }

   public boolean onClimbable() {
      return false;
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void nyfsspiders$onTick(CallbackInfo ci) {
      ClimberHelper.tickClimber(this);
   }

   public void aiStep() {
      ClimberHelper.livingTickClimber(this);
      super.aiStep();
   }

   public void move(MoverType type, Vec3 movement) {
      ClimberHelper.handleMove(this, type, movement, true);
      super.move(type, movement);
      ClimberHelper.handleMove(this, type, movement, false);
   }

   public BlockPos getOnPos() {
      BlockPos pos = super.getOnPos();
      return ClimberHelper.getAdjustedOnPosition(this, pos);
   }

   public void travel(Vec3 travelVector) {
      if (!ClimberHelper.handleTravel(this, travelVector)) {
         super.travel(travelVector);
      }

      ClimberHelper.postTravel(this, travelVector);
   }

   public void jumpFromGround() {
      if (!ClimberHelper.handleJump(this)) {
         super.jumpFromGround();
      }
   }

   public void lookAt(Anchor anchor, Vec3 target) {
      Vec3 dir = target.subtract(this.position());
      dir = this.getOrientation().getLocal(dir);
      super.lookAt(anchor, this.position().add(dir));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.nyfsspiders$climberComponent != null) {
         this.nyfsspiders$climberComponent.writeToNbt(compound);
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (this.nyfsspiders$climberComponent != null) {
         this.nyfsspiders$climberComponent.readFromNbt(compound);
      }
   }
}
