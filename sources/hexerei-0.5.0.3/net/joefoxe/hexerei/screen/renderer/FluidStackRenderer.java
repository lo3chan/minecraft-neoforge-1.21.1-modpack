package net.joefoxe.hexerei.screen.renderer;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class FluidStackRenderer {
   private static final NumberFormat nf = NumberFormat.getIntegerInstance();
   private static final int TEXTURE_SIZE = 16;
   private static final int MIN_FLUID_HEIGHT = 1;
   public final int capacityMb;
   private final FluidStackRenderer.TooltipMode tooltipMode;
   private final int width;
   private final int height;

   public FluidStackRenderer(int capacityMb, boolean showCapacity, int width, int height) {
      this(capacityMb, showCapacity ? FluidStackRenderer.TooltipMode.SHOW_AMOUNT_AND_CAPACITY : FluidStackRenderer.TooltipMode.SHOW_AMOUNT, width, height);
   }

   private FluidStackRenderer(int capacityMb, FluidStackRenderer.TooltipMode tooltipMode, int width, int height) {
      Preconditions.checkArgument(capacityMb > 0, "capacity must be > 0");
      Preconditions.checkArgument(width > 0, "width must be > 0");
      Preconditions.checkArgument(height > 0, "height must be > 0");
      this.capacityMb = capacityMb;
      this.tooltipMode = tooltipMode;
      this.width = width;
      this.height = height;
   }

   public void render(@NotNull GuiGraphics guiGraphics, FluidStack fluidStack) {
      RenderSystem.enableBlend();
      this.drawFluid(guiGraphics, this.width, this.height, fluidStack);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
   }

   public void render(GuiGraphics guiGraphics, int xPosition, int yPosition, FluidStack ingredient) {
      if (ingredient != null) {
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(xPosition, yPosition, 0.0F);
         this.render(guiGraphics, ingredient);
         guiGraphics.pose().popPose();
      }
   }

   private void drawFluid(GuiGraphics guiGraphics, int width, int height, FluidStack fluidStack) {
      if (!fluidStack.getFluid().isSame(Fluids.EMPTY)) {
         this.getStillFluidSprite(fluidStack).ifPresent(fluidStillSprite -> {
            int fluidColor = this.getColorTint(fluidStack);
            long amount = fluidStack.getAmount();
            long scaledAmount = amount * height / this.capacityMb;
            if (amount > 0L && scaledAmount < 1L) {
               scaledAmount = 1L;
            }

            if (scaledAmount > height) {
               scaledAmount = height;
            }

            drawTiledSprite(guiGraphics, width, height, fluidColor, scaledAmount, fluidStillSprite);
         });
      }
   }

   public Optional<TextureAtlasSprite> getStillFluidSprite(FluidStack fluidStack) {
      Fluid fluid = fluidStack.getFluid();
      IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
      ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);
      TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
      return Optional.of(sprite).filter(s -> s.atlasLocation() != MissingTextureAtlasSprite.getLocation());
   }

   public int getColorTint(FluidStack ingredient) {
      Fluid fluid = ingredient.getFluid();
      IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
      return renderProperties.getTintColor(ingredient);
   }

   private static void drawTiledSprite(GuiGraphics guiGraphics, int tiledWidth, int tiledHeight, int color, long scaledAmount, TextureAtlasSprite sprite) {
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      Matrix4f matrix = guiGraphics.pose().last().pose();
      setGLColorFromInt(color);
      int xTileCount = tiledWidth / 16;
      int xRemainder = tiledWidth - xTileCount * 16;
      int yTileCount = (int)(scaledAmount / 16L);
      int yRemainder = (int)(scaledAmount - yTileCount * 16);
      int yStart = tiledHeight;

      for (int xTile = 0; xTile <= xTileCount; xTile++) {
         for (int yTile = 0; yTile <= yTileCount; yTile++) {
            int width = xTile == xTileCount ? xRemainder : 16;
            int height = yTile == yTileCount ? yRemainder : 16;
            int x = xTile * 16;
            int y = yStart - (yTile + 1) * 16;
            if (width > 0 && height > 0) {
               int maskTop = 16 - height;
               int maskRight = 16 - width;
               drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100.0F);
            }
         }
      }
   }

   private static void setGLColorFromInt(int color) {
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      float alpha = (color >> 24 & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(red, green, blue, alpha);
   }

   private static void drawTextureWithMasking(
      Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel
   ) {
      float uMin = textureSprite.getU0();
      float uMax = textureSprite.getU1();
      float vMin = textureSprite.getV0();
      float vMax = textureSprite.getV1();
      uMin += maskRight / 16.0F * (uMax - uMin);
      vMin += maskTop / 16.0F * (vMax - vMin);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      Tesselator tessellator = Tesselator.getInstance();
      BufferBuilder bufferBuilder = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferBuilder.addVertex(matrix, xCoord, yCoord + 16.0F, zLevel).setUv(uMin, vMax);
      bufferBuilder.addVertex(matrix, xCoord + 16.0F - maskRight, yCoord + 16.0F, zLevel).setUv(uMax, vMax);
      bufferBuilder.addVertex(matrix, xCoord + 16.0F - maskRight, yCoord + maskTop, zLevel).setUv(uMax, vMin);
      bufferBuilder.addVertex(matrix, xCoord, yCoord + maskTop, zLevel).setUv(uMin, vMin);
      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
   }

   public List<Component> getTooltip(FluidStack fluidStack, TooltipFlag tooltipFlag) {
      List<Component> tooltip = new ArrayList<>();
      Fluid fluidType = fluidStack.getFluid();
      if (fluidType == null) {
         return tooltip;
      } else {
         Component displayName = fluidStack.getHoverName();
         if (fluidStack.isEmpty()) {
            displayName = Component.translatable("book.hexerei.tooltip.empty");
         }

         tooltip.add(displayName);
         int amount = fluidStack.getAmount();
         if (this.tooltipMode == FluidStackRenderer.TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
            MutableComponent amountString = Component.translatable(
               "book.hexerei.tooltip.liquid.amount.with.capacity", new Object[]{nf.format((long)amount), nf.format((long)this.capacityMb)}
            );
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
         } else if (this.tooltipMode == FluidStackRenderer.TooltipMode.SHOW_AMOUNT) {
            MutableComponent amountString = Component.translatable("book.hexerei.tooltip.liquid.amount", new Object[]{nf.format((long)amount)});
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
         }

         return tooltip;
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   static enum TooltipMode {
      SHOW_AMOUNT,
      SHOW_AMOUNT_AND_CAPACITY,
      ITEM_LIST;
   }
}
