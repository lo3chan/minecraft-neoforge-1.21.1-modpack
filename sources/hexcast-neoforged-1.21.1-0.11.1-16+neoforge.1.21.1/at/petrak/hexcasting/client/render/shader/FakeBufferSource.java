package at.petrak.hexcasting.client.render.shader;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record FakeBufferSource(MultiBufferSource parent, Function<ResourceLocation, RenderType> mapper) implements MultiBufferSource {
   @NotNull
   public VertexConsumer getBuffer(@NotNull RenderType renderType) {
      return this.parent.getBuffer(renderType);
   }
}
