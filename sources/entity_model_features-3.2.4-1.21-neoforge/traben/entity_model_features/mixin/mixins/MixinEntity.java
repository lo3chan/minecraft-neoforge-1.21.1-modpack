package traben.entity_model_features.mixin.mixins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFEntity;

@Mixin({Entity.class})
public abstract class MixinEntity implements EMFEntity {
   @Unique
   private final Map<String, Float> emf$variableMap = new HashMap<>();
   @Unique
   @Nullable
   private Map<String, Float> emf$variableMapGuiCopy = null;
   @Shadow
   public double zo;
   @Shadow
   public double yo;
   @Shadow
   public double xo;
   @Shadow
   public float xRotO;
   @Shadow
   private float xRot;
   @Shadow
   private float yRot;
   @Shadow
   public int tickCount;
   @Shadow
   public double xOld;
   @Shadow
   public double yOld;
   @Shadow
   public double zOld;
   @Unique
   private int varHash = 0;

   @Shadow
   public abstract double getX();

   @Shadow
   public abstract double getY();

   @Shadow
   public abstract double getZ();

   @Shadow
   public abstract boolean isOnFire();

   @Shadow
   public abstract boolean isAlive();

   @Shadow
   public abstract boolean isInLava();

   @Shadow
   public abstract boolean isInvisible();

   @Shadow
   public abstract boolean isSprinting();

   @Shadow
   public abstract EntityType<?> getType();

   @Shadow
   public abstract boolean isCrouching();

   @Shadow
   public abstract boolean isInWater();

   @Shadow
   public abstract boolean isPassenger();

   @Shadow
   public abstract boolean onGround();

   @Shadow
   public abstract boolean hasGlowingTag();

   @Shadow
   public abstract List<Entity> getPassengers();

   @Shadow
   public abstract boolean isInWaterRainOrBubble();

   @Shadow
   public abstract Vec3 getDeltaMovement();

   @Shadow
   @Override
   public abstract boolean equals(Object var1);

   @Shadow
   public abstract Vec3 position();

   @Shadow
   public abstract boolean isInWaterOrRain();

   @Inject(
      method = {"getLeashOffset()Lnet/minecraft/world/phys/Vec3;"},
      at = {@At("RETURN")}
   )
   private void emf$leashwither(CallbackInfoReturnable<Vec3> cir) {
      if (EMFAnimationEntityContext.getLeashX() != 0.0F || EMFAnimationEntityContext.getLeashY() != 0.0F || EMFAnimationEntityContext.getLeashZ() != 0.0F) {
         Vec3 vec = (Vec3)cir.getReturnValue();
         vec.add(EMFAnimationEntityContext.getLeashX(), EMFAnimationEntityContext.getLeashY(), EMFAnimationEntityContext.getLeashZ());
      }
   }

   @Override
   public double emf$prevX() {
      return this.xo;
   }

   @Override
   public double emf$getX() {
      return this.getX();
   }

   @Override
   public double emf$prevY() {
      return this.yo;
   }

   @Override
   public double emf$getY() {
      return this.getY();
   }

   @Override
   public double emf$prevZ() {
      return this.zo;
   }

   @Override
   public double emf$getZ() {
      return this.getZ();
   }

   @Override
   public float emf$prevPitch() {
      return this.xRotO;
   }

   @Override
   public float emf$getPitch() {
      return this.xRot;
   }

   @Override
   public boolean emf$isTouchingWater() {
      return this.isInWater();
   }

   @Override
   public boolean emf$isOnFire() {
      return this.isOnFire();
   }

   @Override
   public boolean emf$hasVehicle() {
      return this.isPassenger();
   }

   @Override
   public boolean emf$isOnGround() {
      return this.onGround();
   }

   @Override
   public boolean emf$isAlive() {
      return this.isAlive();
   }

   @Override
   public boolean emf$isGlowing() {
      return this.hasGlowingTag();
   }

   @Override
   public boolean emf$isInLava() {
      return this.isInLava();
   }

   @Override
   public boolean emf$isInvisible() {
      return this.isInvisible();
   }

   @Override
   public boolean emf$hasPassengers() {
      return !this.getPassengers().isEmpty();
   }

   @Override
   public boolean emf$isSneaking() {
      return this.isCrouching();
   }

   @Override
   public boolean emf$isSprinting() {
      return this.isSprinting();
   }

   @Override
   public boolean emf$isWet() {
      return this.isInWaterRainOrBubble();
   }

   @Override
   public float emf$age() {
      return this.tickCount;
   }

   @Override
   public float emf$getYaw() {
      return this.yRot;
   }

   @Override
   public Vec3 emf$getVelocity() {
      return this.equals(Minecraft.getInstance().player) ? this.getDeltaMovement() : this.position().subtract(new Vec3(this.xOld, this.yOld, this.zOld));
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
         this.emf$variableMapGuiCopy = null;
      }

      if (EMFAnimationEntityContext.isInGui()) {
         if (this.emf$variableMapGuiCopy == null) {
            this.emf$variableMapGuiCopy = new HashMap<>(this.emf$variableMap);
         }

         return this.emf$variableMapGuiCopy;
      } else {
         return this.emf$variableMap;
      }
   }
}
