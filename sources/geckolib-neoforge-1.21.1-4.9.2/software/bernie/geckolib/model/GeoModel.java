package software.bernie.geckolib.model;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

public abstract class GeoModel<T extends GeoAnimatable> {
   private final AnimationProcessor<T> processor = new AnimationProcessor<>(this);
   private BakedGeoModel currentModel = null;
   private double animTime;
   private double lastGameTickTime;
   private long lastRenderedInstance = -1L;

   public ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer) {
      return this.getModelResource(animatable);
   }

   @Deprecated
   public abstract ResourceLocation getModelResource(T var1);

   public ResourceLocation getTextureResource(T animatable, @Nullable GeoRenderer<T> renderer) {
      return this.getTextureResource(animatable);
   }

   @Deprecated
   public abstract ResourceLocation getTextureResource(T var1);

   public abstract ResourceLocation getAnimationResource(T var1);

   public ResourceLocation[] getAnimationResourceFallbacks(T animatable) {
      return new ResourceLocation[0];
   }

   public boolean crashIfBoneMissing() {
      return false;
   }

   @Nullable
   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture);
   }

   public BakedGeoModel getBakedModel(ResourceLocation location) {
      BakedGeoModel model = GeckoLibCache.getBakedModels().get(location);
      if (model == null) {
         if (!location.getPath().contains("geo/")) {
            throw GeckoLibConstants.exception(location, "Invalid model resource path provided - GeckoLib models must be placed in assets/<modid>/geo/");
         } else {
            throw GeckoLibConstants.exception(location, "Unable to find model");
         }
      } else {
         if (model != this.currentModel) {
            this.processor.setActiveModel(model);
            this.currentModel = model;
         }

         return this.currentModel;
      }
   }

   public Optional<GeoBone> getBone(String name) {
      return Optional.ofNullable(this.getAnimationProcessor().getBone(name));
   }

   @Nullable
   public Animation getAnimation(T animatable, String name) {
      ResourceLocation location = this.getAnimationResource(animatable);
      BakedAnimations bakedAnimations = GeckoLibCache.getBakedAnimations().get(location);
      Animation animation = bakedAnimations != null ? bakedAnimations.getAnimation(name) : null;
      if (animation != null) {
         return animation;
      } else {
         for (ResourceLocation fallbackLocation : this.getAnimationResourceFallbacks(animatable)) {
            Map var10000 = GeckoLibCache.getBakedAnimations();
            location = fallbackLocation;
            bakedAnimations = (BakedAnimations)var10000.get(fallbackLocation);
            animation = bakedAnimations != null ? bakedAnimations.getAnimation(name) : null;
            if (animation != null) {
               return animation;
            }
         }

         if (bakedAnimations != null) {
            return null;
         } else if (!location.getPath().contains("animations/")) {
            throw GeckoLibConstants.exception(
               location, "Invalid animation resource path provided - GeckoLib animations must be placed in assets/<modid>/animations/"
            );
         } else {
            throw GeckoLibConstants.exception(location, "Unable to find animation file.");
         }
      }
   }

   public AnimationProcessor<T> getAnimationProcessor() {
      return this.processor;
   }

   public void addAdditionalStateData(T animatable, long instanceId, BiConsumer<DataTicket<T>, T> dataConsumer) {
   }

   @Internal
   public void handleAnimations(T animatable, long instanceId, AnimationState<T> animationState, float partialTick) {
      Minecraft mc = Minecraft.getInstance();
      AnimatableManager<T> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
      Double currentTick = animationState.getData(DataTickets.TICK);
      if (currentTick == null) {
         currentTick = animatable instanceof Entity entity ? entity.tickCount : RenderUtil.getCurrentTick();
      }

      if (animatableManager.getFirstTickTime() == -1.0) {
         animatableManager.startedAt(currentTick + partialTick);
      }

      double currentFrameTime = !(animatable instanceof Entity) && !(animatable instanceof GeoReplacedEntity)
         ? currentTick - animatableManager.getFirstTickTime()
         : currentTick + partialTick;
      boolean isReRender = !animatableManager.isFirstTick() && currentFrameTime == animatableManager.getLastUpdateTime();
      if (!isReRender || instanceId != this.lastRenderedInstance) {
         if (!mc.isPaused() || animatable.shouldPlayAnimsWhileGamePaused()) {
            animatableManager.updatedAt(currentFrameTime);
            double lastUpdateTime = animatableManager.getLastUpdateTime();
            this.animTime = this.animTime + (lastUpdateTime - this.lastGameTickTime);
            this.lastGameTickTime = lastUpdateTime;
         }

         animationState.animationTick = this.animTime;
         this.lastRenderedInstance = instanceId;
         AnimationProcessor<T> processor = this.getAnimationProcessor();
         processor.preAnimationSetup(animationState, this.animTime);
         if (!processor.getRegisteredBones().isEmpty()) {
            processor.tickAnimation(animatable, this, animatableManager, this.animTime, animationState, this.crashIfBoneMissing());
         }

         this.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
   }

   public void applyMolangQueries(AnimationState<T> animationState, double animTime) {
   }
}
