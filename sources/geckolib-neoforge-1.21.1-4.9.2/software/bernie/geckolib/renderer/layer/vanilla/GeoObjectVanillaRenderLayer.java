package software.bernie.geckolib.renderer.layer.vanilla;

import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public abstract class GeoObjectVanillaRenderLayer<A extends GeoAnimatable, T extends Entity, M extends EntityModel<T>, R extends GeoObjectRenderer<A>>
   extends AttachedAnimatableRenderLayer<A, T, M, R> {
   public GeoObjectVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
      super(renderer, instanceFactory);
   }
}
