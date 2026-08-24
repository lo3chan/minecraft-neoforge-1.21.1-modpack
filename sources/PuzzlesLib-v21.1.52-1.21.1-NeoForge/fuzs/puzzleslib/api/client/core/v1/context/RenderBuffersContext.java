package fuzs.puzzleslib.api.client.core.v1.context;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.Objects;
import net.minecraft.client.renderer.RenderType;

public interface RenderBuffersContext {
   default void registerRenderBuffer(RenderType... renderTypes) {
      Objects.requireNonNull(renderTypes, "render types is null");
      Preconditions.checkState(renderTypes.length > 0, "render types is empty");

      for (RenderType renderType : renderTypes) {
         Objects.requireNonNull(renderType, "render type is null");
         this.registerRenderBuffer(renderType, new ByteBufferBuilder(renderType.bufferSize()));
      }
   }

   void registerRenderBuffer(RenderType var1, ByteBufferBuilder var2);
}
