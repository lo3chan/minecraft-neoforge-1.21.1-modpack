package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS("kjs$")
@Mixin({Entity.class})
public abstract class EntityMixin implements EntityKJS {
   @Unique
   private CompoundTag kjs$persistentData;
   @Shadow
   @RemapForJS("tickCount")
   public int tickCount;

   @Shadow
   public abstract void playerTouch(Player arg);

   @Override
   public CompoundTag kjs$getPersistentData() {
      if (this.kjs$persistentData == null) {
         this.kjs$persistentData = new CompoundTag();
      }

      return this.kjs$persistentData;
   }

   @Shadow(
      remap = false
   )
   @RemapForJS("getForgePersistentData")
   public abstract CompoundTag getPersistentData();

   @Inject(
      method = {"saveWithoutId"},
      at = {@At("RETURN")}
   )
   private void saveKJS(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
      if (this.kjs$persistentData != null && !this.kjs$persistentData.isEmpty()) {
         tag.put("KubeJSPersistentData", this.kjs$persistentData);
      }
   }

   @Inject(
      method = {"load"},
      at = {@At("RETURN")}
   )
   private void loadKJS(CompoundTag tag, CallbackInfo ci) {
      if (tag.contains("KubeJSPersistentData")) {
         this.kjs$persistentData = tag.getCompound("KubeJSPersistentData");
      } else {
         this.kjs$persistentData = null;
      }
   }

   @HideFromJS
   @Nullable
   @Override
   public CompoundTag kjs$getRawPersistentData() {
      return this.kjs$persistentData;
   }

   @HideFromJS
   @Override
   public void kjs$setRawPersistentData(@Nullable CompoundTag tag) {
      this.kjs$persistentData = tag;
   }

   @Shadow
   @RemapForJS("getUuid")
   public abstract UUID getUUID();

   @Shadow
   @RemapForJS("getStringUuid")
   public abstract String getStringUUID();

   @Shadow
   @RemapForJS("isGlowing")
   public abstract boolean isCurrentlyGlowing();

   @Shadow
   @RemapForJS("setGlowing")
   public abstract void setGlowingTag(boolean glowing);

   @Shadow
   @RemapForJS("getYaw")
   public abstract float getYRot();

   @Shadow
   @RemapForJS("setYaw")
   public abstract void setYRot(float yaw);

   @Shadow
   @RemapForJS("getPitch")
   public abstract float getXRot();

   @Shadow
   @RemapForJS("setPitch")
   public abstract void setXRot(float pitch);

   @Shadow
   @RemapForJS("setBodyYaw")
   @Info("Sets the entity's body yaw.")
   public abstract void setYBodyRot(float yBodyRot);

   @Shadow
   @RemapForJS("getBodyYaw")
   @Info("Gets the entity's body yaw (if the entity is a `LivingEntity`), or the entity's visual rotation (if the entity is an item entity or an item frame).")
   public abstract float getVisualRotationYInDegrees();

   @Shadow
   @RemapForJS("setMotion")
   public abstract void setDeltaMovement(double x, double y, double z);

   @Shadow
   @RemapForJS("setPositionAndRotation")
   public abstract void moveTo(double x, double y, double z, float yaw, float pitch);

   @Shadow
   @RemapForJS("addMotion")
   public abstract void push(double x, double y, double z);

   @Shadow
   @HideFromJS
   public abstract List<Entity> getPassengers();

   @Shadow
   @RemapForJS("isOnSameTeam")
   public abstract boolean isAlliedTo(Entity e);

   @Shadow
   @RemapForJS("getHorizontalFacing")
   public abstract Direction getDirection();

   @Shadow
   @RemapForJS("extinguish")
   public abstract void extinguishFire();

   @Shadow
   @HideFromJS
   public abstract boolean hurt(DamageSource source, float hp);

   @Shadow
   @RemapForJS("getEntityType")
   public abstract EntityType<?> getType();

   @Shadow
   @Info("Measures the **square** of a distance of entity to the point at specified 3D position vector.")
   public abstract double distanceToSqr(Vec3 vec);

   @Shadow
   @Info("Measures the distance of entity to the point at specified `x`, `y` and `z`.")
   public abstract double distanceToSqr(double x, double y, double z);

   @Shadow
   @RemapForJS("distanceToEntitySqr")
   @Info("Measures the **square** of a distance of entity to another entity.")
   public abstract double distanceToSqr(Entity arg);

   @Shadow
   @RemapForJS("distanceToEntity")
   @Info("Measures the distance of entity to another entity.")
   public abstract float distanceTo(Entity arg);

   @Shadow
   @HideFromJS
   public abstract void teleportTo(double x, double y, double z);

   @Shadow
   @HideFromJS
   public abstract boolean teleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yaw, float pitch);

   @Shadow
   @HideFromJS
   public abstract Level level();

   @Shadow
   @HideFromJS
   public abstract ItemEntity spawnAtLocation(ItemLike item);

   @Shadow
   @HideFromJS
   public abstract ItemEntity spawnAtLocation(ItemLike item, int offsetY);

   @Shadow
   @RemapForJS("moveToBlockPos")
   public abstract void moveTo(BlockPos pos, float yRot, float xRot);
}
