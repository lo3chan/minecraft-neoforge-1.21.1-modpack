package software.bernie.geckolib.renderer.layer.vanilla;

import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public abstract class GeoItemVanillaRenderLayer<A extends Item & GeoItem, T extends Entity, M extends EntityModel<T>, R extends GeoItemRenderer<A>>
   extends AttachedAnimatableRenderLayer<A, T, M, R> {
   public GeoItemVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
      super(renderer, instanceFactory);
   }

   @Nullable
   protected R getRenderer(A animatable) {
      return (R)(GeoRenderProvider.of(animatable).getGeoItemRenderer() instanceof GeoItemRenderer geoRenderer ? geoRenderer : null);
   }
}
