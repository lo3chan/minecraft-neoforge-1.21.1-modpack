package traben.entity_model_features.models.animation.state;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public interface EMFEntityRenderState extends ETFEntityRenderState {
   @Deprecated
   EMFEntity emfEntity();

   double prevX();

   double x();

   double prevY();

   double y();

   double prevZ();

   double z();

   float prevPitch();

   float pitch();

   boolean isTouchingWater();

   boolean isOnFire();

   boolean hasVehicle();

   boolean isOnGround();

   boolean isAlive();

   boolean isGlowing();

   boolean isInLava();

   boolean isInvisible();

   boolean hasPassengers();

   boolean isSneaking();

   boolean isSprinting();

   boolean isWet();

   float age();

   float yaw();

   Vec3 emfVelocity();

   String typeString();

   Map<String, Float> variableMap();

   Function<ResourceLocation, RenderType> layerFactory();

   void setLayerFactory(Function<ResourceLocation, RenderType> var1);

   @Nullable
   EMFAttachments leftArmOverride();

   void setLeftArmOverride(EMFAttachments var1);

   @Nullable
   EMFAttachments rightArmOverride();

   void setRightArmOverride(EMFAttachments var1);

   void setBipedPose(EMFBipedPose var1);

   @Nullable
   EMFBipedPose getBipedPose();
}
