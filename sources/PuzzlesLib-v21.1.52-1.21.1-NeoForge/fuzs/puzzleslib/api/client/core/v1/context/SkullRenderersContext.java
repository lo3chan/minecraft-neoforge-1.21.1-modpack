package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock.Type;

@FunctionalInterface
public interface SkullRenderersContext {
   @Deprecated
   void registerSkullRenderer(SkullRenderersFactory var1);

   default void registerSkullRenderer(Type skullBlockType, ResourceLocation textureLocation, Function<EntityModelSet, SkullModelBase> skullModelFactory) {
      Objects.requireNonNull(skullBlockType, "skull block type is null");
      Objects.requireNonNull(textureLocation, "texture location is null");
      Objects.requireNonNull(skullModelFactory, "skull model factory is null");
      SkullBlockRenderer.SKIN_BY_TYPE.put(skullBlockType, textureLocation);
      this.registerSkullRenderer((entityModelSet, consumer) -> consumer.accept(skullBlockType, skullModelFactory.apply(entityModelSet)));
   }
}
