package net.joefoxe.hexerei.screen.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.data.books.PageDrawing;
import net.joefoxe.hexerei.item.custom.HerbJarItem;
import net.joefoxe.hexerei.items.JarHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ClientHerbJarToolTip implements HexereiBookTooltip {
   public static final ResourceLocation TEXTURE_LOCATION = HexereiUtil.getResource("textures/gui/herb_jar_tooltip_inventory.png");
   private final JarHandler items;
   public int width;
   public Font font;
   public MutableComponent shift_down;
   public MutableComponent shift_up;

   public ClientHerbJarToolTip(HerbJarItem.HerbJarToolTip tooltip) {
      this.font = Minecraft.getInstance().font;
      this.shift_down = Component.translatable(
            "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
         )
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
      this.shift_up = Component.translatable(
            "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
         )
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
      this.items = tooltip.jarHandler();
   }

   public int getHeight() {
      return this.gridSizeY() * 20 + 2 + 4 + 10 + this.getHeightOffset();
   }

   public int getHeightOffset() {
      return (9 + 1) * (!Screen.hasShiftDown() ? 1 : (this.items.isEmpty() ? 1 : 1));
   }

   public int getWidth(Font font) {
      return this.gridSizeX() * 18 + 2 + 10;
   }

   public void renderImage(Font p_194042_, int xIn, int yIn, GuiGraphics guiGraphics) {
      int i = this.gridSizeX();
      int j = this.gridSizeY();
      this.drawBorder(xIn, yIn, i, j, guiGraphics, 0);
      this.renderSlot(xIn + 1 + 5, yIn + 1 + 5 + this.getHeightOffset(), 0, true, p_194042_, guiGraphics);
   }

   @Override
   public void renderImage(
      Font p_194042_, MultiBufferSource bufferSource, int xIn, int yIn, PoseStack matrixStack, ItemRenderer p_194046_, int z, int overlay, int light
   ) {
      int i = this.gridSizeX();
      int j = this.gridSizeY();
      VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));
      this.drawBorder(buffer, xIn, yIn, i, j, matrixStack, z, overlay, light);
      this.renderSlot(bufferSource, buffer, xIn + 1 + 5, yIn + 1 + 5 + this.getHeightOffset(), 0, p_194042_, matrixStack, p_194046_, z, overlay, light);
      this.renderSlotItemDecorations(
         bufferSource, buffer, xIn + 1 + 5, yIn + this.getHeightOffset() + 1 + 5, 0, p_194042_, matrixStack, p_194046_, z, overlay, light
      );
      this.renderSlotItemCount(bufferSource, buffer, xIn + 1 + 5, yIn + this.getHeightOffset() + 1 + 5, 0, p_194042_, matrixStack, p_194046_, z, overlay, light);
      this.renderSlotItem(bufferSource, buffer, xIn + 1 + 5, yIn + this.getHeightOffset() + 1 + 5, 0, p_194042_, matrixStack, p_194046_, z, overlay, light);
   }

   @OnlyIn(Dist.CLIENT)
   public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, BufferSource buffer) {
      if (Screen.hasShiftDown()) {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_down.getVisualOrderText(), mouseX, mouseY, 16777215, true, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      } else {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_up.getVisualOrderText(), mouseX, mouseY, 16777215, true, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void renderText(Font p_169953_, int mouseX, int mouseY, Matrix4f lastpose, BufferSource buffer, int overlay, int light) {
      if (Screen.hasShiftDown()) {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_down.getVisualOrderText(), mouseX, mouseY, 16777215, false, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      } else {
         Minecraft.getInstance()
            .font
            .drawInBatch(this.shift_up.getVisualOrderText(), mouseX, mouseY, 16777215, false, lastpose, buffer, DisplayMode.NORMAL, 0, 15728880);
      }
   }

   private void renderSlot(int p_194027_, int p_194028_, int slot, boolean isGui, Font p_194031_, GuiGraphics guiGraphics) {
      ItemStack itemstack = this.items.getStackInSlot(slot);
      if (itemstack.isEmpty()) {
         this.blit(guiGraphics, p_194027_, p_194028_, 1, ClientHerbJarToolTip.Texture.BLOCKED_SLOT);
      } else {
         this.blit(guiGraphics, p_194027_, p_194028_, 1, ClientHerbJarToolTip.Texture.SLOT);
      }

      if (isGui) {
         guiGraphics.renderItem(itemstack, p_194027_ + 1, p_194028_ + 1, slot);
         guiGraphics.renderItemDecorations(p_194031_, itemstack, p_194027_ + 1, p_194028_ + 1);
      }
   }

   private void renderSlot(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.items.getStackInSlot(slot);
      matrixStack.pushPose();
      matrixStack.scale(1.0F, 1.0F, 1.0E-4F);
      if (itemstack.isEmpty()) {
         this.blit(matrixStack, buffer, xIn, yIn, 0, ClientHerbJarToolTip.Texture.BLOCKED_SLOT, overlay, light);
      } else {
         this.blit(matrixStack, buffer, xIn, yIn, 0, ClientHerbJarToolTip.Texture.SLOT, overlay, light);
      }

      matrixStack.popPose();
      RenderSystem.enableDepthTest();
   }

   private void renderSlotItem(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.items.getStackInSlot(slot);
      PageDrawing.renderGuiItem(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);
      RenderSystem.enableDepthTest();
   }

   private void renderSlotItemCount(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.items.getStackInSlot(slot);
      PageDrawing.renderGuiItemCount(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);
      RenderSystem.enableDepthTest();
   }

   private void renderSlotItemDecorations(
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int slot,
      Font p_194031_,
      PoseStack matrixStack,
      ItemRenderer p_194033_,
      int z,
      int overlay,
      int light
   ) {
      ItemStack itemstack = this.items.getStackInSlot(slot);
      PageDrawing.renderGuiItemDecorations(bufferSource, p_194031_, itemstack, matrixStack, xIn, yIn, overlay, light);
      RenderSystem.enableDepthTest();
   }

   private void drawBorder(int p_194020_, int p_194021_, int p_194022_, int p_194023_, GuiGraphics guiGraphics, int p_194025_) {
      this.blit(guiGraphics, p_194020_ + 5, p_194021_ + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.BORDER_CORNER_TOP);
      this.blit(
         guiGraphics, p_194020_ + p_194022_ * 18 + 1 + 5, p_194021_ + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.BORDER_CORNER_TOP
      );

      for (int i = 0; i < p_194022_; i++) {
         this.blit(
            guiGraphics, p_194020_ + 1 + i * 18 + 5, p_194021_ + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.BORDER_HORIZONTAL_TOP
         );
         this.blit(
            guiGraphics,
            p_194020_ + 1 + i * 18 + 5,
            p_194021_ + p_194023_ * 20 - 1 + 5 + this.getHeightOffset(),
            p_194025_,
            ClientHerbJarToolTip.Texture.BORDER_HORIZONTAL_BOTTOM
         );
      }

      for (int j = 0; j < p_194023_; j++) {
         this.blit(guiGraphics, p_194020_ + 5, p_194021_ + j * 18 + 1 + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.BORDER_VERTICAL);
         this.blit(
            guiGraphics,
            p_194020_ + p_194022_ * 18 + 1 + 5,
            p_194021_ + j * 20 + 1 + 5 + this.getHeightOffset(),
            p_194025_,
            ClientHerbJarToolTip.Texture.BORDER_VERTICAL
         );
      }

      this.blit(
         guiGraphics, p_194020_ + 5, p_194021_ + p_194023_ * 18 + 1 + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.BORDER_CORNER_BOTTOM
      );
      this.blit(
         guiGraphics,
         p_194020_ + p_194022_ * 18 + 1 + 5,
         p_194021_ + p_194023_ * 18 + 1 + 5 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.BORDER_CORNER_BOTTOM
      );
      this.blit(guiGraphics, p_194020_, p_194021_ + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_VERTICAL);
      this.blit(guiGraphics, p_194020_ + 25, p_194021_ + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_VERTICAL);
      this.blit(guiGraphics, p_194020_ + 5, p_194021_ + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_HORIZONTAL);
      this.blit(guiGraphics, p_194020_ + 5, p_194021_ + 25 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_HORIZONTAL);
      this.blit(guiGraphics, p_194020_, p_194021_ + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT);
      this.blit(
         guiGraphics, p_194020_ + 18 + 7, p_194021_ + 18 + 7 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT
      );
      this.blit(guiGraphics, p_194020_, p_194021_ + 18 + 7 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT);
      this.blit(guiGraphics, p_194020_ + 18 + 7, p_194021_ + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT);
   }

   private void drawBorder(
      VertexConsumer buffer, int p_194020_, int p_194021_, int p_194022_, int p_194023_, PoseStack p_194024_, int p_194025_, int overlay, int light
   ) {
      this.blit(
         p_194024_, buffer, p_194020_ + 5, p_194021_ + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.BORDER_CORNER_TOP, overlay, light
      );
      this.blit(
         p_194024_,
         buffer,
         p_194020_ + p_194022_ * 18 + 1 + 5,
         p_194021_ + 5 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.BORDER_CORNER_TOP,
         overlay,
         light
      );

      for (int i = 0; i < p_194022_; i++) {
         this.blit(
            p_194024_,
            buffer,
            p_194020_ + 1 + i * 18 + 5,
            p_194021_ + 5 + this.getHeightOffset(),
            p_194025_,
            ClientHerbJarToolTip.Texture.BORDER_HORIZONTAL_TOP,
            overlay,
            light
         );
         this.blit(
            p_194024_,
            buffer,
            p_194020_ + 1 + i * 18 + 5,
            p_194021_ + p_194023_ * 20 - 1 + 5 + this.getHeightOffset(),
            p_194025_,
            ClientHerbJarToolTip.Texture.BORDER_HORIZONTAL_BOTTOM,
            overlay,
            light
         );
      }

      for (int j = 0; j < p_194023_; j++) {
         this.blit(
            p_194024_,
            buffer,
            p_194020_ + 5,
            p_194021_ + j * 18 + 1 + 5 + this.getHeightOffset(),
            p_194025_,
            ClientHerbJarToolTip.Texture.BORDER_VERTICAL,
            overlay,
            light
         );
         this.blit(
            p_194024_,
            buffer,
            p_194020_ + p_194022_ * 18 + 1 + 5,
            p_194021_ + j * 20 + 1 + 5 + this.getHeightOffset(),
            p_194025_,
            ClientHerbJarToolTip.Texture.BORDER_VERTICAL,
            overlay,
            light
         );
      }

      this.blit(
         p_194024_,
         buffer,
         p_194020_ + 5,
         p_194021_ + p_194023_ * 18 + 1 + 5 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.BORDER_CORNER_BOTTOM,
         overlay,
         light
      );
      this.blit(
         p_194024_,
         buffer,
         p_194020_ + p_194022_ * 18 + 1 + 5,
         p_194021_ + p_194023_ * 18 + 1 + 5 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.BORDER_CORNER_BOTTOM,
         overlay,
         light
      );
      this.blit(
         p_194024_, buffer, p_194020_, p_194021_ + 5 + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_VERTICAL, overlay, light
      );
      this.blit(
         p_194024_,
         buffer,
         p_194020_ + 25,
         p_194021_ + 5 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.THICK_BORDER_VERTICAL,
         overlay,
         light
      );
      this.blit(
         p_194024_, buffer, p_194020_ + 5, p_194021_ + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_HORIZONTAL, overlay, light
      );
      this.blit(
         p_194024_,
         buffer,
         p_194020_ + 5,
         p_194021_ + 25 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.THICK_BORDER_HORIZONTAL,
         overlay,
         light
      );
      this.blit(
         p_194024_, buffer, p_194020_, p_194021_ + this.getHeightOffset(), p_194025_, ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_LEFT, overlay, light
      );
      this.blit(
         p_194024_,
         buffer,
         p_194020_ + 18 + 7,
         p_194021_ + 18 + 7 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_RIGHT,
         overlay,
         light
      );
      this.blit(
         p_194024_,
         buffer,
         p_194020_,
         p_194021_ + 18 + 7 + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_BOTTOM_LEFT,
         overlay,
         light
      );
      this.blit(
         p_194024_,
         buffer,
         p_194020_ + 18 + 7,
         p_194021_ + this.getHeightOffset(),
         p_194025_,
         ClientHerbJarToolTip.Texture.THICK_BORDER_CORNER_TOP_RIGHT,
         overlay,
         light
      );
   }

   private void blit(GuiGraphics guiGraphics, int p_194037_, int p_194038_, int p_194039_, ClientHerbJarToolTip.Texture p_194040_) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(TEXTURE_LOCATION, p_194037_, p_194038_, p_194039_, p_194040_.x, p_194040_.y, p_194040_.w, p_194040_.h, 128, 128);
   }

   private void blit(PoseStack poseStack, VertexConsumer buffer, int xIn, int yIn, int zIn, ClientHerbJarToolTip.Texture texture, int overlay, int light) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
      blit(poseStack, buffer, xIn, yIn, zIn, texture.x, texture.y, texture.w, texture.h, 128, 128, overlay, light);
   }

   public static void blit(
      PoseStack poseStack,
      VertexConsumer buffer,
      int xIn,
      int yIn,
      int zIn,
      float p_93148_,
      float p_93149_,
      int p_93150_,
      int p_93151_,
      int p_93152_,
      int p_93153_,
      int overlay,
      int light
   ) {
      innerBlit(poseStack, buffer, xIn, xIn + p_93150_, yIn, yIn + p_93151_, zIn, p_93150_, p_93151_, p_93148_, p_93149_, p_93152_, p_93153_, overlay, light);
   }

   private static void innerBlit(
      PoseStack p_93188_,
      VertexConsumer buffer,
      int p_93189_,
      int p_93190_,
      int p_93191_,
      int p_93192_,
      int p_93193_,
      int p_93194_,
      int p_93195_,
      float p_93196_,
      float p_93197_,
      int p_93198_,
      int p_93199_,
      int overlay,
      int light
   ) {
      innerBlit(
         p_93188_,
         buffer,
         p_93189_,
         p_93190_,
         p_93191_,
         p_93192_,
         p_93193_,
         (p_93196_ + 0.0F) / p_93198_,
         (p_93196_ + p_93194_) / p_93198_,
         (p_93197_ + 0.0F) / p_93199_,
         (p_93197_ + p_93195_) / p_93199_,
         overlay,
         light
      );
   }

   private static void innerBlit(
      PoseStack poseStack,
      VertexConsumer buffer,
      int p_93114_,
      int p_93115_,
      int p_93116_,
      int p_93117_,
      int p_93118_,
      float p_93119_,
      float p_93120_,
      float p_93121_,
      float p_93122_,
      int overlay,
      int light
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0F, 0.0F, p_93118_);
      buffer.addVertex(poseStack.last().pose(), p_93114_, p_93117_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93119_, p_93122_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      buffer.addVertex(poseStack.last().pose(), p_93115_, p_93117_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93120_, p_93122_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      buffer.addVertex(poseStack.last().pose(), p_93115_, p_93116_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93120_, p_93121_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      buffer.addVertex(poseStack.last().pose(), p_93114_, p_93116_, p_93118_)
         .setColor(255, 255, 255, 255)
         .setUv(p_93119_, p_93121_)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      poseStack.popPose();
   }

   private int gridSizeX() {
      return 1;
   }

   private int gridSizeY() {
      return 1;
   }

   @OnlyIn(Dist.CLIENT)
   static enum Texture {
      SLOT(0, 0, 18, 18),
      BLOCKED_SLOT(0, 40, 18, 18),
      BORDER_VERTICAL(0, 18, 1, 18),
      BORDER_HORIZONTAL_TOP(0, 18, 18, 1),
      BORDER_HORIZONTAL_BOTTOM(0, 58, 18, 1),
      BORDER_CORNER_TOP(0, 18, 1, 1),
      BORDER_CORNER_BOTTOM(0, 58, 1, 1),
      THICK_BORDER_CORNER_TOP_LEFT(0, 60, 5, 5),
      THICK_BORDER_CORNER_TOP_RIGHT(5, 60, 5, 5),
      THICK_BORDER_CORNER_BOTTOM_LEFT(0, 65, 5, 5),
      THICK_BORDER_CORNER_BOTTOM_RIGHT(5, 65, 5, 5),
      THICK_BORDER_VERTICAL(0, 75, 5, 20),
      THICK_BORDER_HORIZONTAL(0, 70, 20, 5);

      public final int x;
      public final int y;
      public final int w;
      public final int h;

      private Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
         this.x = p_169928_;
         this.y = p_169929_;
         this.w = p_169930_;
         this.h = p_169931_;
      }
   }
}
