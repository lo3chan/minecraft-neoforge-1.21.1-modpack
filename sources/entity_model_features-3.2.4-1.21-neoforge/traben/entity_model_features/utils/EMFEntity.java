package traben.entity_model_features.utils;

import java.util.Map;
import net.minecraft.world.phys.Vec3;
import traben.entity_texture_features.utils.ETFEntity;

public interface EMFEntity extends ETFEntity {
   double emf$prevX();

   double emf$getX();

   double emf$prevY();

   double emf$getY();

   double emf$prevZ();

   double emf$getZ();

   float emf$prevPitch();

   float emf$getPitch();

   boolean emf$isTouchingWater();

   boolean emf$isOnFire();

   boolean emf$hasVehicle();

   boolean emf$isOnGround();

   boolean emf$isAlive();

   boolean emf$isGlowing();

   boolean emf$isInLava();

   boolean emf$isInvisible();

   boolean emf$hasPassengers();

   boolean emf$isSneaking();

   boolean emf$isSprinting();

   boolean emf$isWet();

   float emf$age();

   float emf$getYaw();

   Vec3 emf$getVelocity();

   String emf$getTypeString();

   Map<String, Float> emf$getVariableMap();
}
