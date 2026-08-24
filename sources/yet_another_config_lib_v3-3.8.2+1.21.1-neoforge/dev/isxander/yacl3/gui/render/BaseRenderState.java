package dev.isxander.yacl3.gui.render;

import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public record BaseRenderState(RenderType renderType, Matrix4f pose) {
   public static BaseRenderState create(GuiGraphics graphics, @Nullable ResourceLocation texture, int x0, int y0, int x1, int y1) {
      return create(graphics, texture);
   }

   public static BaseRenderState create(GuiGraphics graphics, @Nullable ResourceLocation texture) {
      return new BaseRenderState(texture != null ? GuiUtils.guiTextured(false).apply(texture) : RenderType.gui(), graphics.pose().last().pose());
   }
}
