package at.petrak.hexcasting.mixin.accessor.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin({RenderType.class})
public interface AccessorRenderType {
   @Invoker("create")
   static RenderType hex$create(
      String string, VertexFormat vertexFormat, Mode mode, int bufSize, boolean hasCrumbling, boolean sortOnUpload, @Coerce Object compositeState
   ) {
      throw new IllegalStateException();
   }
}
