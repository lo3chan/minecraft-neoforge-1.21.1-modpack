package traben.entity_model_features.models.animation.state;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderStateViaReference;

public class EMFEntityRenderStateViaReference extends ETFEntityRenderStateViaReference implements EMFEntityRenderState {
   private final EMFEntity emfEntity;
   private Function<ResourceLocation, RenderType> layerFactory = null;
   private EMFAttachments leftArmOverride = null;
   private EMFAttachments rightArmOverride = null;
   EMFBipedPose bipedPose = null;

   public EMFEntityRenderStateViaReference(EMFEntity emfEntity) {
      super(emfEntity);
      this.emfEntity = emfEntity;
   }

   @Deprecated
   @Override
   public EMFEntity emfEntity() {
      return this.emfEntity;
   }

   @Override
   public double prevX() {
      return this.emfEntity.emf$prevX();
   }

   @Override
   public double x() {
      return this.emfEntity.emf$getX();
   }

   @Override
   public double prevY() {
      return this.emfEntity.emf$prevY();
   }

   @Override
   public double y() {
      return this.emfEntity.emf$getY();
   }

   @Override
   public double prevZ() {
      return this.emfEntity.emf$prevZ();
   }

   @Override
   public double z() {
      return this.emfEntity.emf$getZ();
   }

   @Override
   public float prevPitch() {
      return this.emfEntity.emf$prevPitch();
   }

   @Override
   public float pitch() {
      return this.emfEntity.emf$getPitch();
   }

   @Override
   public boolean isTouchingWater() {
      return this.emfEntity.emf$isTouchingWater();
   }

   @Override
   public boolean isOnFire() {
      return this.emfEntity.emf$isOnFire();
   }

   @Override
   public boolean hasVehicle() {
      return this.emfEntity.emf$hasVehicle();
   }

   @Override
   public boolean isOnGround() {
      return this.emfEntity.emf$isOnGround();
   }

   @Override
   public boolean isAlive() {
      return this.emfEntity.emf$isAlive();
   }

   @Override
   public boolean isGlowing() {
      return this.emfEntity.emf$isGlowing();
   }

   @Override
   public boolean isInLava() {
      return this.emfEntity.emf$isInLava();
   }

   @Override
   public boolean isInvisible() {
      return this.emfEntity.emf$isInvisible();
   }

   @Override
   public boolean hasPassengers() {
      return this.emfEntity.emf$hasPassengers();
   }

   @Override
   public boolean isSneaking() {
      return this.emfEntity.emf$isSneaking();
   }

   @Override
   public boolean isSprinting() {
      return this.emfEntity.emf$isSprinting();
   }

   @Override
   public boolean isWet() {
      return this.emfEntity.emf$isWet();
   }

   @Override
   public float age() {
      return this.emfEntity.emf$age();
   }

   @Override
   public float yaw() {
      return this.emfEntity.emf$getYaw();
   }

   @Override
   public Vec3 emfVelocity() {
      return this.emfEntity.emf$getVelocity();
   }

   @Override
   public String typeString() {
      return this.emfEntity.emf$getTypeString();
   }

   @Override
   public Map<String, Float> variableMap() {
      return this.emfEntity.emf$getVariableMap();
   }

   @Override
   public Function<ResourceLocation, RenderType> layerFactory() {
      return this.layerFactory;
   }

   @Override
   public void setLayerFactory(Function<ResourceLocation, RenderType> layerFactory) {
      this.layerFactory = layerFactory;
   }

   @Nullable
   @Override
   public EMFAttachments leftArmOverride() {
      return this.leftArmOverride;
   }

   @Override
   public void setLeftArmOverride(EMFAttachments override) {
      this.leftArmOverride = override;
   }

   @Nullable
   @Override
   public EMFAttachments rightArmOverride() {
      return this.rightArmOverride;
   }

   @Override
   public void setRightArmOverride(EMFAttachments override) {
      this.rightArmOverride = override;
   }

   @Override
   public void setBipedPose(EMFBipedPose pose) {
      this.bipedPose = pose;
   }

   @Override
   public EMFBipedPose getBipedPose() {
      return this.bipedPose;
   }
}
