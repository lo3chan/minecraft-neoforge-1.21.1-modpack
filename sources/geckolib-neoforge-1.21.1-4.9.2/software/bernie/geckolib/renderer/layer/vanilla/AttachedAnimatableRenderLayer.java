package software.bernie.geckolib.renderer.layer.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.ref.WeakReference;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;

public abstract class AttachedAnimatableRenderLayer<A extends GeoAnimatable, T extends Entity, M extends EntityModel<T>, R extends GeoRenderer<A>>
   extends RenderLayer<T, M> {
   private final Function<Level, A> instanceCache = new Function<Level, A>() {
      @Nullable
      private WeakReference<A> cachedInstance = null;

      @Contract("null->null;!null->!null")
      @Nullable
      public A apply(@Nullable Level level) {
         if (level == null) {
            this.cachedInstance = null;
            return null;
         } else {
            A instance;
            if (this.cachedInstance == null || (instance = this.cachedInstance.get()) == null) {
               this.cachedInstance = new WeakReference<>(instance = AttachedAnimatableRenderLayer.this.instanceFactory.apply(level));
            }

            return instance;
         }
      }
   };
   private final Function<Level, A> instanceFactory;

   public AttachedAnimatableRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
      super(renderer);
      this.instanceFactory = instanceFactory;
   }

   protected abstract void renderAnimatableOnModel(
      T var1,
      A var2,
      M var3,
      R var4,
      PoseStack var5,
      MultiBufferSource var6,
      float var7,
      int var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13
   );

   @Nullable
   protected abstract R getRenderer(A var1);

   protected boolean shouldRender(T entity) {
      return true;
   }

   @Nullable
   public A getAnimatableInstance(T entity) {
      A animatable = this.instanceCache.apply(entity.level());
      if (animatable != null) {
         this.updateAnimatableTick(animatable, entity.tickCount);
      }

      return animatable;
   }

   protected void updateAnimatableTick(A instance, int tick) {
      if (instance instanceof Entity geoEntity) {
         geoEntity.tickCount = tick;
      }
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      T entity,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (this.shouldRender(entity)) {
         A animatable = this.getAnimatableInstance(entity);
         R renderer;
         if (animatable != null && (renderer = this.getRenderer(animatable)) != null) {
            poseStack.pushPose();
            this.renderAnimatableOnModel(
               entity,
               animatable,
               (M)this.getParentModel(),
               renderer,
               poseStack,
               bufferSource,
               partialTick,
               packedLight,
               ageInTicks,
               limbSwing,
               limbSwingAmount,
               netHeadYaw,
               headPitch
            );
            poseStack.popPose();
         }
      }
   }
}
