package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Predicates;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

@Deprecated
@FunctionalInterface
public interface LivingEntityRenderLayersContext {
   default <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(
      BiFunction<RenderLayerParent<T, M>, Context, RenderLayer<T, M>> factory
   ) {
      this.registerRenderLayer(Predicates.alwaysTrue(), factory);
   }

   default <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(
      EntityType<E> entityType, BiFunction<RenderLayerParent<T, M>, Context, RenderLayer<T, M>> factory
   ) {
      Objects.requireNonNull(entityType, "entity type is null");
      this.registerRenderLayer((Predicate<EntityType<E>>)(entityTypeX -> entityTypeX == entityType), factory);
   }

   <E extends LivingEntity, T extends E, M extends EntityModel<T>> void registerRenderLayer(
      Predicate<EntityType<E>> var1, BiFunction<RenderLayerParent<T, M>, Context, RenderLayer<T, M>> var2
   );
}
