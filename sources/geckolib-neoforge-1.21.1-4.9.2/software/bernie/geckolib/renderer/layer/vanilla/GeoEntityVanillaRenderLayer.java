package software.bernie.geckolib.renderer.layer.vanilla;

import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class GeoEntityVanillaRenderLayer<A extends Entity & GeoEntity, T extends Entity, M extends EntityModel<T>, R extends GeoEntityRenderer<A>>
   extends AttachedAnimatableRenderLayer<A, T, M, R> {
   public GeoEntityVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
      super(renderer, instanceFactory);
   }

   @Nullable
   protected R getRenderer(A animatable) {
      EntityRenderDispatcher entityRenderers = Minecraft.getInstance().getEntityRenderDispatcher();
      return (R)(entityRenderers.getRenderer(animatable) instanceof GeoEntityRenderer<? super A> geoRenderer ? geoRenderer : null);
   }
}
