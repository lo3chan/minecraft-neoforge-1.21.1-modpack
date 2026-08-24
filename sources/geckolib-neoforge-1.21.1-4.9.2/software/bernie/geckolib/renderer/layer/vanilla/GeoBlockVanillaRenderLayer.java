package software.bernie.geckolib.renderer.layer.vanilla;

import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public abstract class GeoBlockVanillaRenderLayer<A extends BlockEntity & GeoBlockEntity, T extends Entity, M extends EntityModel<T>, R extends GeoBlockRenderer<A>>
   extends AttachedAnimatableRenderLayer<A, T, M, R> {
   public GeoBlockVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
      super(renderer, instanceFactory);
   }

   @Nullable
   protected R getRenderer(A animatable) {
      BlockEntityRenderDispatcher blockRenderers = Minecraft.getInstance().getBlockEntityRenderDispatcher();
      return (R)(blockRenderers.getRenderer(animatable) instanceof GeoBlockRenderer<? super A> geoRenderer ? geoRenderer : null);
   }
}
