package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.renderer.v1.chunk.ChunkSectionLayer;
import java.util.Objects;
import net.minecraft.client.renderer.RenderType;

public interface RenderTypesContext<T> {
   void registerRenderType(T var1, RenderType var2);

   default void registerChunkRenderType(T object, ChunkSectionLayer chunkSectionLayer) {
      this.registerRenderType(object, chunkSectionLayer.renderType);
   }

   @Deprecated
   default void registerRenderType(RenderType renderType, T... objects) {
      Objects.requireNonNull((T)objects, "objects is null");

      for (T object : objects) {
         this.registerRenderType(object, renderType);
      }
   }

   RenderType getRenderType(T var1);
}
