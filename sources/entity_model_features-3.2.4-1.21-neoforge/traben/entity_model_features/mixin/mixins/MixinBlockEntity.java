package traben.entity_model_features.mixin.mixins;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFEntity;

@Mixin({BlockEntity.class})
public abstract class MixinBlockEntity implements EMFEntity {
   @Unique
   private final Map<String, Float> emf$variableMap = new HashMap<>();
   @Unique
   private int varHash = 0;

   @Shadow
   @Nullable
   public abstract Level getLevel();

   @Shadow
   public abstract BlockEntityType<?> getType();

   @Shadow
   public abstract BlockPos getBlockPos();

   @Shadow
   public abstract BlockState getBlockState();

   @Override
   public double emf$prevX() {
      return this.getBlockPos().getX();
   }

   @Override
   public double emf$getX() {
      return this.getBlockPos().getX();
   }

   @Override
   public double emf$prevY() {
      return this.getBlockPos().getY();
   }

   @Override
   public double emf$getY() {
      return this.getBlockPos().getY();
   }

   @Override
   public double emf$prevZ() {
      return this.getBlockPos().getZ();
   }

   @Override
   public double emf$getZ() {
      return this.getBlockPos().getZ();
   }

   @Override
   public float emf$prevPitch() {
      return 0.0F;
   }

   @Override
   public float emf$getPitch() {
      return 0.0F;
   }

   @Override
   public boolean emf$isTouchingWater() {
      return !this.getBlockState().getFluidState().isEmpty();
   }

   @Override
   public boolean emf$isOnFire() {
      return false;
   }

   @Override
   public boolean emf$hasVehicle() {
      return false;
   }

   @Override
   public boolean emf$isOnGround() {
      return this.getLevel() != null && !this.getLevel().getBlockState(this.getBlockPos()).isAir();
   }

   @Override
   public boolean emf$isAlive() {
      return false;
   }

   @Override
   public boolean emf$isGlowing() {
      return false;
   }

   @Override
   public boolean emf$isInLava() {
      return false;
   }

   @Override
   public boolean emf$isInvisible() {
      return false;
   }

   @Override
   public boolean emf$hasPassengers() {
      return false;
   }

   @Override
   public boolean emf$isSneaking() {
      return false;
   }

   @Override
   public boolean emf$isSprinting() {
      return false;
   }

   @Override
   public boolean emf$isWet() {
      return !this.getBlockState().getFluidState().isEmpty();
   }

   @Override
   public float emf$age() {
      return 0.0F;
   }

   @Override
   public float emf$getYaw() {
      return 0.0F;
   }

   @Override
   public Vec3 emf$getVelocity() {
      return new Vec3(0.0, 0.0, 0.0);
   }

   @Override
   public String emf$getTypeString() {
      return this.getType().toString();
   }

   @Override
   public Map<String, Float> emf$getVariableMap() {
      int managerHash = EMFManager.getManagerInstanceHash();
      if (this.varHash != managerHash) {
         this.varHash = managerHash;
         this.emf$variableMap.clear();
      }

      return this.emf$variableMap;
   }
}
