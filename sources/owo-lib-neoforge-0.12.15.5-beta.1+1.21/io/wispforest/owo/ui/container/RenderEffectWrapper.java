package io.wispforest.owo.ui.container;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import io.wispforest.owo.ui.util.ScissorStack;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.joml.Matrix4f;

@Experimental
public class RenderEffectWrapper<C extends Component> extends WrappingParentComponent<C> {
   protected static final List<RenderTarget> FRAMEBUFFERS = new ArrayList<>();
   protected static int drawDepth = 0;
   protected final List<RenderEffectWrapper<C>.RenderEffectSlot> effects = new ArrayList<>();

   protected RenderEffectWrapper(C child) {
      super(Sizing.content(), Sizing.content(), child);
      this.allowOverflow = true;
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      super.draw(context, mouseX, mouseY, partialTicks, delta);

      try {
         drawDepth++;
         Window window = Minecraft.getInstance().getWindow();

         while (drawDepth > FRAMEBUFFERS.size()) {
            FRAMEBUFFERS.add(new TextureTarget(window.getWidth(), window.getHeight(), true, Minecraft.ON_OSX));
         }

         int previousFramebuffer = GlStateManager.getBoundFramebuffer();
         RenderTarget framebuffer = FRAMEBUFFERS.get(drawDepth - 1);
         framebuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         ScissorStack.drawUnclipped(() -> framebuffer.clear(Minecraft.ON_OSX));
         framebuffer.bindWrite(false);
         this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.childView);
         GlStateManager._glBindFramebuffer(36160, previousFramebuffer);
         ListIterator<RenderEffectWrapper<C>.RenderEffectSlot> iter = this.effects.listIterator();

         while (iter.hasNext()) {
            iter.next().effect.setup(this, context, partialTicks, delta);
         }

         BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         Matrix4f matrix = context.pose().last().pose();
         buffer.addVertex(matrix, 0.0F, window.getGuiScaledHeight(), 0.0F).setUv(0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.addVertex(matrix, window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0.0F).setUv(1.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.addVertex(matrix, window.getGuiScaledWidth(), 0.0F, 0.0F).setUv(1.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, framebuffer.getColorTextureId());
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         BufferUploader.drawWithShader(buffer.buildOrThrow());

         while (iter.hasPrevious()) {
            iter.previous().effect.cleanup(this, context, partialTicks, delta);
         }
      } finally {
         drawDepth--;
      }
   }

   public RenderEffectWrapper<C>.RenderEffectSlot effect(RenderEffectWrapper.RenderEffect effect) {
      RenderEffectWrapper<C>.RenderEffectSlot slot = new RenderEffectWrapper.RenderEffectSlot(effect);
      this.effects.add(slot);
      return slot;
   }

   public void clearEffects() {
      this.effects.clear();
   }

   static {
      WindowResizeCallback.EVENT
         .register(
            (WindowResizeCallback)(client, window) -> FRAMEBUFFERS.forEach(
               framebuffer -> framebuffer.resize(window.getWidth(), window.getHeight(), Minecraft.ON_OSX)
            )
         );
   }

   public interface RenderEffect {
      void setup(Component var1, GuiGraphics var2, float var3, float var4);

      void cleanup(Component var1, GuiGraphics var2, float var3, float var4);

      static RenderEffectWrapper.RenderEffect rotate(float angle) {
         return rotate(Axis.ZP, angle);
      }

      static RenderEffectWrapper.RenderEffect rotate(Axis axis, float angle) {
         return new RenderEffectWrapper.RenderEffect() {
            @Override
            public void setup(Component component, GuiGraphics context, float partialTicks, float delta) {
               Size size = component.fullSize();
               PoseStack matrices = context.pose();
               matrices.pushPose();
               matrices.translate(component.x() + size.width() / 2.0F, component.y() + size.height() / 2.0F, 0.0F);
               matrices.mulPose(axis.rotationDegrees(angle));
               matrices.translate(-(component.x() + size.width() / 2.0F), -(component.y() + size.height() / 2.0F), 0.0F);
            }

            @Override
            public void cleanup(Component component, GuiGraphics context, float partialTicks, float delta) {
               context.pose().popPose();
            }
         };
      }

      static RenderEffectWrapper.RenderEffect color(Color color) {
         return new RenderEffectWrapper.RenderEffect() {
            private float[] colors = null;

            @Override
            public void setup(Component component, GuiGraphics context, float partialTicks, float delta) {
               this.colors = (float[])RenderSystem.getShaderColor().clone();
               RenderSystem.setShaderColor(
                  this.colors[0] * color.red(), this.colors[1] * color.green(), this.colors[2] * color.blue(), this.colors[3] * color.alpha()
               );
               if (color.alpha() != 1.0F) {
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
               }
            }

            @Override
            public void cleanup(Component component, GuiGraphics context, float partialTicks, float delta) {
               RenderSystem.setShaderColor(this.colors[0], this.colors[1], this.colors[2], this.colors[3]);
            }
         };
      }

      static RenderEffectWrapper.RenderEffect transform(Matrix4f transform) {
         return transform((Consumer<PoseStack>)(matrices -> matrices.mulPose(transform)));
      }

      static RenderEffectWrapper.RenderEffect transform(Consumer<PoseStack> transform) {
         return new RenderEffectWrapper.RenderEffect() {
            @Override
            public void setup(Component component, GuiGraphics context, float partialTicks, float delta) {
               context.pose().pushPose();
               transform.accept(context.pose());
            }

            @Override
            public void cleanup(Component component, GuiGraphics context, float partialTicks, float delta) {
               context.pose().popPose();
            }
         };
      }
   }

   public class RenderEffectSlot {
      protected RenderEffectWrapper.RenderEffect effect;

      protected RenderEffectSlot(RenderEffectWrapper.RenderEffect effect) {
         this.effect = effect;
      }

      public void update(RenderEffectWrapper.RenderEffect newEffect) {
         this.effect = newEffect;
      }

      public void remove() {
         RenderEffectWrapper.this.effects.remove(this);
      }
   }
}
