package vazkii.psi.mixin.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.SequencedMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BufferSource.class})
public interface AccessorRenderBuffers {
   @Accessor
   SequencedMap<RenderType, ByteBufferBuilder> getFixedBuffers();
}
