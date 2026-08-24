package io.wispforest.owo.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BufferBuilder.class})
public interface BufferBuilderAccessor {
   @Accessor("building")
   boolean isBuilding();

   @Accessor("format")
   VertexFormat getFormat();

   @Accessor("mode")
   Mode getDrawMode();
}
