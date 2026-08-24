package software.bernie.geckolib.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.data.EntityModelData;

public class DefaultedEntityGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
   @Nullable
   protected String headBone;
   @Deprecated(
      forRemoval = true
   )
   protected boolean turnsHead;

   public DefaultedEntityGeoModel(ResourceLocation assetSubpath) {
      this(assetSubpath, false);
   }

   public DefaultedEntityGeoModel(ResourceLocation assetSubpath, boolean turnsHead) {
      this(assetSubpath, turnsHead ? "head" : null);
   }

   public DefaultedEntityGeoModel(ResourceLocation assetSubpath, @Nullable String headBone) {
      super(assetSubpath);
      this.turnsHead = headBone != null;
      this.headBone = headBone;
   }

   @Override
   protected String subtype() {
      return "entity";
   }

   @Override
   public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
      if (this.headBone != null && this.turnsHead) {
         GeoBone head = this.getAnimationProcessor().getBone(this.headBone);
         if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
         }
      }
   }

   public DefaultedEntityGeoModel<T> withAltModel(ResourceLocation altPath) {
      return (DefaultedEntityGeoModel<T>)super.withAltModel(altPath);
   }

   public DefaultedEntityGeoModel<T> withAltAnimations(ResourceLocation altPath) {
      return (DefaultedEntityGeoModel<T>)super.withAltAnimations(altPath);
   }

   public DefaultedEntityGeoModel<T> withAltTexture(ResourceLocation altPath) {
      return (DefaultedEntityGeoModel<T>)super.withAltTexture(altPath);
   }
}
