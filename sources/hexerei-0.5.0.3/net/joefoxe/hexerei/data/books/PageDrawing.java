package net.joefoxe.hexerei.data.books;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.MixingCauldron;
import net.joefoxe.hexerei.client.renderer.ModRenderTypes;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.screen.BookOfShadowsScreen;
import net.joefoxe.hexerei.screen.CanvasPaintingCropScreen;
import net.joefoxe.hexerei.screen.tooltip.HexereiBookTooltip;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.Color;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.Pre;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PageDrawing {
   public float lineWidth;
   public float lineHeight;
   public BookOfShadowsAltarTile altarTile;
   public ItemStack tooltipStack;
   public List<Component> tooltipText;
   public BookImage slotOverlay;
   public boolean drawTooltip;
   public boolean drawTooltipStack;
   public boolean drawTooltipStackFlag;
   public boolean drawTooltipTextFlag;
   public float drawTooltipScale;
   public float drawTooltipScaleOld;
   public boolean drawTooltipText;
   public boolean drawSlotOverlay;
   public PageDrawing.PageOn slotOverlayPageOn;
   public ArrayList<Float> bookmarkHoverAmount = new ArrayList<>(Stream.<Float>generate(() -> 0.0F).limit(20L).toList());
   public ArrayList<Float> bookmarkHoverAmountOld = new ArrayList<>(Stream.<Float>generate(() -> 0.0F).limit(20L).toList());
   public ArrayList<Float> bookmarkHoverAmountRender = new ArrayList<>(Stream.<Float>generate(() -> 0.0F).limit(20L).toList());
   public ArrayList<Integer> bookmarkHovered = new ArrayList<>();
   public static boolean isClicked;
   public static boolean isClickedOld;
   public static ArrayList<ResourceLocation> pageTextureLocs = new ArrayList<>();
   public static ArrayList<ResourceLocation> overlayTextureLocs = new ArrayList<>();
   public static Triple<BookOfShadowsAltarTile, ResourceLocation, BookWritableTextBox> focusedWritableTextBox = null;
   public static Triple<BookOfShadowsAltarTile, ResourceLocation, BookWritableTextBox> focusedWritableTextBoxLast = null;
   public static ItemRenderer itemRenderer;
   private static final int TEXTURE_SIZE = 16;
   private static final int MIN_FLUID_HEIGHT = 1;
   private static final NumberFormat nf = NumberFormat.getIntegerInstance();
   public static final float CORNERS = (float)MixingCauldron.SHAPE.min(Axis.X) + 0.1875F;
   public static final float MIN_Y = 0.25F;
   public static final float MAX_Y = 0.9375F;
   protected static final Quaternionf ITEM_LIGHT_ROTATION_3D = (Quaternionf)Util.make(() -> {
      Quaternionf quaternion = new Quaternionf();
      quaternion.setAngleAxis(1.1344640137963142, 1.0, 0.0, 0.0);
      quaternion.rotateAxis(0.87266463F, 0.0F, 1.0F, 0.0F);
      return quaternion;
   });
   protected static final Quaternionf BLOCK_LIGHT_ROTATION_3D = (Quaternionf)Util.make(() -> {
      Quaternionf quaternion = new Quaternionf();
      quaternion.setAngleAxis(0.6108652381980153, 1.0, 0.0, 0.0);
      quaternion.rotateAxis(0.61086524F, 0.0F, 1.0F, 0.0F);
      return quaternion;
   });
   protected static final Quaternionf ITEM_LIGHT_ROTATION_FLAT = (Quaternionf)Util.make(() -> {
      Quaternionf quaternion = new Quaternionf();
      quaternion.setAngleAxis(-0.7853981633974483, 1.0, 0.0, 0.0);
      return quaternion;
   });

   public PageDrawing(BookOfShadowsAltarTile altarTile) {
      this.lineWidth = 0.0F;
      this.lineHeight = 0.0F;
      this.tooltipStack = ItemStack.EMPTY;
      this.tooltipText = new ArrayList<>();
      this.slotOverlay = new BookImage(
         0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 20.0F, 20.0F, 20.0F, 20.0F, 1.0F, "hexerei:textures/book/slot_hover.png", new ArrayList<>()
      );
      this.drawTooltipStack = false;
      this.drawTooltipStackFlag = false;
      this.drawTooltipTextFlag = false;
      this.drawTooltipScale = 0.0F;
      this.drawTooltipText = false;
      this.drawSlotOverlay = false;
      this.slotOverlayPageOn = PageDrawing.PageOn.LEFT_PAGE;
      itemRenderer = Hexerei.proxy.getLevel() == null ? null : (Hexerei.proxy.getLevel().isClientSide ? Minecraft.getInstance().getItemRenderer() : null);
      this.altarTile = altarTile;
   }

   public static void clearFocusedWritableTextBox() {
      if (focusedWritableTextBox != null) {
         ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.clicked = false;
         ((BookWritableTextBox)focusedWritableTextBox.getRight())
            .client
            .pageEdit
            .setCursorPos(((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.getCursorPos(), false);
         ((BookWritableTextBox)focusedWritableTextBox.getRight())
            .client
            .clearDisplayCache(((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook.getUUID());
      }

      setFocusedWritableTextBoxNull();
   }

   public static void setFocusedWritableTextBoxNull() {
      setFocusedWritableTextBox(null, null, null);
   }

   public static void setFocusedWritableTextBox(
      final BookOfShadowsAltarTile altarTile, final ResourceLocation pageLoc, final BookWritableTextBox bookWritableTextBox
   ) {
      if (altarTile != null && bookWritableTextBox != null && pageLoc != null) {
         focusedWritableTextBoxLast = focusedWritableTextBox;
         focusedWritableTextBox = new Triple<BookOfShadowsAltarTile, ResourceLocation, BookWritableTextBox>() {
            public BookOfShadowsAltarTile getLeft() {
               return altarTile;
            }

            public ResourceLocation getMiddle() {
               return pageLoc;
            }

            public BookWritableTextBox getRight() {
               return bookWritableTextBox;
            }
         };
      } else {
         focusedWritableTextBoxLast = focusedWritableTextBox;
         focusedWritableTextBox = null;
      }
   }

   public static ItemStack getTagStack(TagKey<Item> key) {
      float fl = 0.0F;
      if (FMLEnvironment.dist.isClient()) {
         fl = ClientEvents.getClientTicks();
      }

      return ((Item)BuiltInRegistries.ITEM.getRandomElementOf(key, RandomSource.create((long)(fl * 1000.0F))).orElse(Holder.direct(Items.AIR)).value())
         .getDefaultInstance();
   }

   public static Block getTagBlock(TagKey<Block> key) {
      float fl = 0.0F;
      if (FMLEnvironment.dist.isClient()) {
         fl = ClientEvents.getClientTicks();
      }

      return (Block)BuiltInRegistries.BLOCK.getRandomElementOf(key, RandomSource.create((long)(fl * 1000.0F))).orElse(Holder.direct(Blocks.AIR)).value();
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderItem(
      BookOfShadowsAltarTile altarTile,
      @NotNull BookItemsAndFluids itemStackElement,
      PoseStack poseStack,
      MultiBufferSource buffer,
      float xIn,
      float yIn,
      float zLevel,
      int combinedLight,
      int combinedOverlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      ItemStack itemStack = itemStackElement.item;
      if (itemStackElement.type.equals("tag")) {
         int mod = (int)ClientEvents.getClientTicks() % 20;
         if (itemStackElement.item.isEmpty()) {
            itemStack = getTagStack(itemStackElement.key);
            itemStackElement.item = itemStack;
            itemStackElement.refreshTag = false;
         }

         if ((mod == 19 || mod == 18) && itemStackElement.refreshTag) {
            itemStack = getTagStack(itemStackElement.key);
            if (itemStack.is(itemStackElement.item.getItem())) {
               itemStack = getTagStack(itemStackElement.key);
            }

            if (itemStack.is(itemStackElement.item.getItem())) {
               itemStack = getTagStack(itemStackElement.key);
            }

            itemStackElement.item = itemStack;
            itemStackElement.refreshTag = false;
            itemStackElement.modelCache = itemRenderer.getModel(itemStack, null, null, 0);
         }

         if (mod == 1 || mod == 2) {
            itemStackElement.refreshTag = true;
         }
      }

      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -0.0013125F);
      poseStack.scale(0.049F, 0.049F, 0.001F);
      poseStack.translate(yIn * 1.259F, -xIn * 1.259F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.pushPose();
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-180.0F));
      poseStack.translate(-0.59375F, -0.5625F, 0.0F);
      poseStack.scale(0.065F, 0.065F, 0.05F);
      renderGuiItemDecorations(buffer, Minecraft.getInstance().font, itemStack, poseStack, 0.0F, 0.0F, combinedOverlay, combinedLight);
      poseStack.translate(1.4423077F, 0.9615385F, 0.0F);
      poseStack.scale(0.965F, 0.965F, 0.965F);
      renderGuiItemCount(buffer, Minecraft.getInstance().font, itemStack, poseStack, 0.0F, 0.0F, combinedOverlay, combinedLight);
      poseStack.popPose();
      Vector3f[] shaderLightDirections = new Vector3f[]{
         new Vector3f(RenderSystem.shaderLightDirections[0]), new Vector3f(RenderSystem.shaderLightDirections[1])
      };
      int[] originalLightmap = (int[])Util.make(() -> {
         int[] vals = new int[12];

         for (int i = 0; i < 12; i++) {
            vals[i] = RenderSystem.getShaderTexture(i);
         }

         return vals;
      });

      try {
         if (itemRenderer == null) {
            itemRenderer = Minecraft.getInstance().getItemRenderer();
         }

         if (itemStackElement.modelCache == null) {
            itemStackElement.modelCache = itemRenderer.getModel(itemStack, null, null, 0);
         }

         if (itemStackElement.modelCache.isGui3d()) {
            poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_3D);
         } else {
            poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT);
         }

         itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, buffer, combinedLight, combinedOverlay, itemStackElement.modelCache);
      } catch (Exception var15) {
      }

      if (buffer instanceof BufferSource bufferSource) {
         bufferSource.endBatch();
      }

      for (int i = 0; i < 12; i++) {
         RenderSystem.setShaderTexture(i, originalLightmap[i]);
      }

      ShaderInstance shaderinstance = RenderSystem.getShader();
      RenderSystem.setShaderLights(shaderLightDirections[0], shaderLightDirections[1]);
      RenderSystem.setupShaderLights(shaderinstance);
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderBlock(
      BookOfShadowsAltarTile altarTile,
      @NotNull BookBlocks blockElement,
      PoseStack poseStack,
      MultiBufferSource buffer,
      float xIn,
      float yIn,
      float zLevel,
      int combinedLight,
      int combinedOverlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      BlockState blockState = blockElement.blockState;
      if (blockElement.type.equals("tag")) {
         int mod = (int)ClientEvents.getClientTicks() % 60;
         if (blockState.is(Blocks.AIR)) {
            blockState = getTagBlock(blockElement.key).defaultBlockState();
            blockElement.blockState = blockState;
         }

         if ((mod == 59 || mod == 58) && blockElement.refreshTag) {
            blockState = getTagBlock(blockElement.key).defaultBlockState();
            if (blockState.equals(blockElement.blockState)) {
               blockState = getTagBlock(blockElement.key).defaultBlockState();
            }

            if (blockState.equals(blockElement.blockState)) {
               blockState = getTagBlock(blockElement.key).defaultBlockState();
            }

            blockElement.blockState = blockState;
            blockElement.refreshTag = false;
         }

         if (mod == 1 || mod == 2) {
            blockElement.refreshTag = true;
         }
      }

      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      float scale = 0.62F;
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.348125F, -0.0013125F);
      poseStack.scale(0.049F * scale, 0.049F * scale, 0.001F);
      poseStack.translate(yIn * 1.259F * (1.0F / scale), -xIn * 1.259F * (1.0F / scale), 0.0F);
      poseStack.translate(0.25F, 0.25F, 0.25F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(30.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45.0F));
      poseStack.translate(-0.25F, -0.25F, -0.25F);
      int light = blockState.getLightEmission(altarTile.getLevel(), altarTile.getBlockPos());

      try {
         if (blockState.getBlock() instanceof LiquidBlock liquidBlock) {
            poseStack.last().normal().set(poseStack.last().normal().rotate(BLOCK_LIGHT_ROTATION_3D));
            renderFluidBlockGUI(poseStack, buffer, new FluidStack(liquidBlock.fluid, 2000), 1.0F, combinedLight, combinedOverlay);
            if (buffer instanceof BufferSource bufferSource) {
               bufferSource.endBatch();
            }
         } else {
            poseStack.last().normal().set(poseStack.last().normal().rotate(BLOCK_LIGHT_ROTATION_3D));
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, poseStack, buffer, combinedLight, combinedOverlay, ModelData.EMPTY, null);
         }
      } catch (Exception var16) {
      }

      poseStack.popPose();
   }

   public static int adjustCombinedLight(int currentCombinedLight, int otherBlockLight) {
      int currentBlockLight = currentCombinedLight >> 4 & 65535;
      int currentSkyLight = currentCombinedLight >> 20 & 65535;
      int adjustedBlockLight = Math.max(currentBlockLight, otherBlockLight);
      return LightTexture.pack(adjustedBlockLight, currentSkyLight);
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderFluidBlockGUI(
      PoseStack poseStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, int combinedLight, int combinedOverlay
   ) {
      VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucent());
      TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance()
         .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
         .apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
      int color = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
      alpha *= (color >> 24 & 0xFF) / 255.0F;
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      renderQuadsBlock(poseStack.last().pose(), vertexBuilder, sprite, red, green, blue, alpha, combinedLight, combinedOverlay);
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderQuadsBlock(
      Matrix4f matrix, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, int light, int overlay
   ) {
      float height = 0.75F;
      float minU = sprite.getU(CORNERS);
      float maxU = sprite.getU(1.0F - CORNERS);
      float minV = sprite.getV(CORNERS);
      float maxV = sprite.getV(1.0F - CORNERS);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, minV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(minU, maxV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, maxV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, height, CORNERS / 5.0F)
         .setColor(r, g, b, alpha)
         .setUv(maxU, minV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 1.0F, 0.0F);
      float shading = 0.75F;
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(minU, minV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(-1.0F, 0.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(minU, maxV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(-1.0F, 0.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, 0.0F, CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(maxU, maxV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(-1.0F, 0.0F, 0.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, 0.0F, 1.0F - CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(maxU, minV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(-1.0F, 0.0F, 0.0F);
      shading = 0.45F;
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(minU, minV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 0.0F, -1.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, height, 1.0F - CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(minU, maxV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 0.0F, -1.0F);
      vertexBuilder.addVertex(matrix, CORNERS / 5.0F, 0.0F, 1.0F - CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(maxU, maxV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 0.0F, -1.0F);
      vertexBuilder.addVertex(matrix, 1.0F - CORNERS / 5.0F, 0.0F, 1.0F - CORNERS / 5.0F)
         .setColor(r * shading, g * shading, b * shading, alpha)
         .setUv(maxU, minV)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(0.0F, 0.0F, -1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderGuiItemDecorations(
      MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack poseStack, float xIn, float yIn, int overlay, int light
   ) {
      if (itemStack.isBarVisible()) {
         poseStack.pushPose();
         int i = itemStack.getBarWidth();
         int j = itemStack.getBarColor();
         poseStack.translate(0.0F, 0.0F, -4.15F);
         fillRect(poseStack, bufferSource, xIn + 2.75F, yIn + 13.75F, 0.0F, 13.0F, 1.5F, 0, 0, 0, 255, overlay, light);
         fillRect(poseStack, bufferSource, xIn + 2.75F, yIn + 13.75F, -0.5F, i, 1.0F, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255, overlay, light);
         poseStack.popPose();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderGuiItemCount(
      MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack poseStack, float xIn, float yIn, int overlay, int light
   ) {
      if (itemStack.getCount() > 1) {
         poseStack.pushPose();
         poseStack.translate(0.0F, 0.0F, -7.0F);
         String s = String.valueOf(itemStack.getCount());
         BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
         font.drawInBatch(
            s,
            xIn + 19.0F - 2.0F - font.width(s) + 1.0F,
            yIn + 6.0F + 3.0F + 1.0F,
            HexereiUtil.getColorValueAlpha(0.245F, 0.245F, 0.245F, 1.0F),
            false,
            poseStack.last().pose(),
            bufferSource,
            DisplayMode.NORMAL,
            overlay,
            light
         );
         poseStack.translate(0.0F, 0.0F, -6.0F);
         font.drawInBatch(
            s,
            xIn + 19.0F - 2.0F - font.width(s),
            yIn + 6.0F + 3.0F,
            16777215,
            false,
            poseStack.last().pose(),
            bufferSource,
            DisplayMode.NORMAL,
            overlay,
            light
         );
         multibuffersource$buffersource.endBatch();
         poseStack.popPose();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderGuiItem(
      MultiBufferSource bufferSource, Font font, ItemStack itemStack, PoseStack poseStack, float xIn, float yIn, int overlay, int light
   ) {
      ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
      poseStack.pushPose();
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      poseStack.scale(16.0F, 16.0F, 1.0F);
      poseStack.translate(yIn * 1.25F * 2.0F / 40.0F + 0.55F, -xIn * 1.25F * 2.0F / 40.0F - 0.55F, -2.0F);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));

      try {
         BakedModel itemModel = itemRenderer.getModel(itemStack, null, null, 0);
         if (itemModel.isGui3d()) {
            poseStack.last().normal().set(poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_3D));
         } else {
            poseStack.last().normal().set(poseStack.last().normal().rotate(ITEM_LIGHT_ROTATION_FLAT));
         }

         itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, bufferSource, light, overlay, itemModel);
      } catch (Exception var10) {
      }

      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   private static void fill(
      RenderType renderType,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float xIn,
      float yIn,
      float zIn,
      float widthIn,
      float heightIn,
      int p_115158_,
      int p_115159_,
      int p_115160_,
      int p_115161_,
      int overlay,
      int light
   ) {
      poseStack.pushPose();
      Pose normal = poseStack.last();
      Matrix4f matrix4f = poseStack.last().pose();
      int u = 0;
      int v = 0;
      int imageWidth = 1;
      int imageHeight = 1;
      int width = 1;
      int height = 1;
      float u1 = (u + 0.0F) / imageWidth;
      float u2 = ((float)u + width) / imageWidth;
      float v1 = (v + 0.0F) / imageHeight;
      float v2 = ((float)v + height) / imageHeight;
      VertexConsumer buffer = bufferSource.getBuffer(renderType);
      buffer.addVertex(matrix4f, xIn + 0.0F, yIn + 0.0F, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u1, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, -1.0F, -1.0F, 0.0F);
      buffer.addVertex(matrix4f, xIn + 0.0F, yIn + heightIn, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u1, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, -1.0F, -1.0F, 0.0F);
      buffer.addVertex(matrix4f, xIn + widthIn, yIn + heightIn, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u2, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, -1.0F, -1.0F, 0.0F);
      buffer.addVertex(matrix4f, xIn + widthIn, yIn + 0.0F, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u2, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, -1.0F, -1.0F, 0.0F);
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   private static void fillRect(
      PoseStack poseStack,
      MultiBufferSource p_115153_,
      float xIn,
      float yIn,
      float zIn,
      float widthIn,
      float heightIn,
      int p_115158_,
      int p_115159_,
      int p_115160_,
      int p_115161_,
      int overlay,
      int light
   ) {
      poseStack.pushPose();
      Pose normal = poseStack.last();
      Matrix4f matrix4f = poseStack.last().pose();
      int u = 0;
      int v = 0;
      int imageWidth = 1;
      int imageHeight = 1;
      int width = 1;
      int height = 1;
      float u1 = (u + 0.0F) / imageWidth;
      float u2 = ((float)u + width) / imageWidth;
      float v1 = (v + 0.0F) / imageHeight;
      float v2 = ((float)v + height) / imageHeight;
      VertexConsumer buffer = p_115153_.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/blank.png")));
      buffer.addVertex(matrix4f, xIn + 0.0F, yIn + 0.0F, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u1, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix4f, xIn + 0.0F, yIn + heightIn, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u1, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix4f, xIn + widthIn, yIn + heightIn, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u2, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix4f, xIn + widthIn, yIn + 0.0F, zIn)
         .setColor(p_115158_, p_115159_, p_115160_, p_115161_)
         .setUv(u2, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToLeftPageUnder(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(90.0F - altarTile.degreesOpenedRender)));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(90.0F - altarTile.degreesOpenedRender) / 90.0F * -altarTile.pageTwoRotationRender));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F)));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * -altarTile.pageTwoRotationRender));
         poseStack.mulPose(
            com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * (altarTile.pageOneRotationRender / 16.0F))
         );
      }

      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-180.0F));
      poseStack.translate(0.0F, -0.3828125F, 0.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToLeftPage(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 33.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(90.0F - altarTile.degreesOpenedRender)));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(90.0F - altarTile.degreesOpenedRender) / 90.0F * -altarTile.pageTwoRotationRender));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F)));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * -altarTile.pageTwoRotationRender));
         poseStack.mulPose(
            com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * (altarTile.pageOneRotationRender / 16.0F))
         );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToRightPageUnder(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F - altarTile.degreesOpenedRender));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((90.0F - altarTile.degreesOpenedRender) / 90.0F * -altarTile.pageOneRotationRender));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(80.0F - altarTile.degreesOpenedRender / 1.12F));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * -altarTile.pageOneRotationRender));
         poseStack.mulPose(
            com.mojang.math.Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * (altarTile.pageTwoRotationRender / 16.0F))
         );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToRightPage(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F - altarTile.degreesOpenedRender));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((90.0F - altarTile.degreesOpenedRender) / 90.0F * -altarTile.pageOneRotationRender));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(80.0F - altarTile.degreesOpenedRender / 1.12F));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * -altarTile.pageOneRotationRender));
         poseStack.mulPose(
            com.mojang.math.Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * (altarTile.pageTwoRotationRender / 16.0F))
         );
      }

      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-180.0F));
      poseStack.translate(0.0F, -0.3828125F, 0.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToLeftPagePrevious(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 33.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(90.0F - altarTile.degreesOpenedRender)));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F)));
         poseStack.mulPose(
            com.mojang.math.Axis.ZP
               .rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * (-altarTile.pageTwoRotationRender / 16.0F + 11.25F))
         );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToRightPagePrevious(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F - altarTile.degreesOpenedRender));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(80.0F - altarTile.degreesOpenedRender / 1.12F));
         poseStack.mulPose(
            com.mojang.math.Axis.ZP
               .rotationDegrees((80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * (-altarTile.pageOneRotationRender / 16.0F + 11.25F))
         );
      }

      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-180.0F));
      poseStack.translate(0.0F, -0.3828125F, 0.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToLeftPagePrevious2(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 33.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(90.0F - altarTile.degreesOpenedRender)));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F)));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-(80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * 11.25F));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void translateToRightPagePrevious2(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 10.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.translate(0.0F, 0.03125F, 0.0F);
      if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F - altarTile.degreesOpenedRender));
      } else {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(80.0F - altarTile.degreesOpenedRender / 1.12F));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((80.0F - altarTile.degreesOpenedRender / 1.12F) / 90.0F * 11.25F));
      }

      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-180.0F));
      poseStack.translate(0.0F, -0.3828125F, 0.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public void translateToMiddleButton(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, PageDrawing.DrawingType drawingType, ItemDisplayContext transformType
   ) {
      float yPos = 0.0F;
      float xPos = 0.0F;
      float zPos = 0.0F;
      float degreesOpened = 0.0F;
      if (transformType == ItemDisplayContext.GUI) {
         yPos = 0.1875F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.375F;
      }

      if (transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
         degreesOpened = 90.0F;
         xPos = 0.25F;
         zPos = -0.03125F;
      }

      poseStack.translate(0.5F + xPos, 1.125F + yPos, 0.5F + zPos);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      if (drawingType == PageDrawing.DrawingType.BOOK) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      } else if (drawingType == PageDrawing.DrawingType.GUI) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F - 13.0F)));
      } else if (drawingType == PageDrawing.DrawingType.SCREEN) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      }

      if (drawingType == PageDrawing.DrawingType.GUI && transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-55.0F));
      }

      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(degreesOpened));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270.0F));
      poseStack.translate(0.04609375F, 0.44375F, 0.34375F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-180.0F));
   }

   public void drawPage(
      BookPage page,
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PoseStack poseStack,
      MultiBufferSource bufferIn,
      int combinedLightIn,
      int combinedOverlayIn,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      float partial
   ) {
      this.drawPage(
         page,
         altarTile,
         leftCursorX,
         leftCursorY,
         rightCursorX,
         rightCursorY,
         poseStack,
         bufferIn,
         combinedLightIn,
         combinedOverlayIn,
         pageOn,
         drawingType,
         transformType,
         -1,
         partial
      );
   }

   public void drawPage(
      BookPage page,
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PoseStack poseStack,
      MultiBufferSource bufferIn,
      int combinedLightIn,
      int combinedOverlayIn,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      int pageNum,
      float partial
   ) {
      boolean left = pageOn == PageDrawing.PageOn.LEFT_PAGE || pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV || pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER;
      BookEntries bookEntries = BookManager.getBookEntries(altarTile.currentBook.getBook());
      if (page != null && bookEntries != null) {
         for (BookPaintElement paintElement : page.paintElements) {
            this.drawPaintElement(
               paintElement,
               altarTile,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
               poseStack,
               bufferIn,
               -0.25F,
               combinedLightIn,
               combinedOverlayIn,
               pageOn,
               -1,
               drawingType,
               transformType,
               partial
            );
         }

         for (BookWritableTextBox writableTextBox : page.writableTextBoxes) {
            this.drawString(
               writableTextBox,
               altarTile,
               poseStack,
               bufferIn,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
               0.0F,
               combinedLightIn,
               combinedOverlayIn,
               pageOn,
               drawingType
            );
         }

         for (BookParagraph bookParagraph : page.paragraph) {
            this.drawString(bookParagraph, altarTile, poseStack, bufferIn, 0.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn, pageOn, drawingType);
         }

         int pageOnNum = pageNum + 1 - ((BookChapter)bookEntries.chapterList.getFirst()).endPage;
         BookParagraph bookParagraph = new BookParagraph(
            new ArrayList<>(List.of(new BookParagraphElements(left ? 14.3F : 0.0F, 19.25F, 1.0F, 30.0F, "top"))),
            pageOnNum > 0 ? String.valueOf(pageOnNum) : HexereiUtil.intToRoman(pageNum + 1),
            "left"
         );
         if (left) {
            BookParagraphElements var10000 = (BookParagraphElements)bookParagraph.paragraphElements.getFirst();
            var10000.x = var10000.x - Minecraft.getInstance().font.width(bookParagraph.passage) / 8.0F;
         }

         this.drawString(bookParagraph, altarTile, poseStack, bufferIn, 0.0F, 0.0F, 0.0F, combinedLightIn, combinedOverlayIn, pageOn, drawingType);

         for (BookItemsAndFluids bookItemStackInSlot : page.itemList) {
            this.drawItemInSlot(
               altarTile,
               bookItemStackInSlot,
               poseStack,
               bufferIn,
               bookItemStackInSlot.x,
               bookItemStackInSlot.y,
               0.0F,
               combinedLightIn,
               combinedOverlayIn,
               pageOn,
               drawingType
            );
         }

         for (BookBlocks bookBlocks : page.blockList) {
            this.drawBlock(
               altarTile, bookBlocks, poseStack, bufferIn, bookBlocks.x, bookBlocks.y, 0.0F, combinedLightIn, combinedOverlayIn, pageOn, drawingType
            );
         }

         if (transformType == ItemDisplayContext.NONE && (pageOn == PageDrawing.PageOn.LEFT_PAGE || pageOn == PageDrawing.PageOn.RIGHT_PAGE)) {
            for (BookItemsAndFluids bookItemStackInSlot : page.itemList) {
               if (canInteract(
                  pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
                  pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
                  bookItemStackInSlot.x,
                  bookItemStackInSlot.y,
                  0.86F,
                  0.86F,
                  altarTile,
                  drawingType
               )) {
                  if (bookItemStackInSlot.item != null) {
                     if (!bookItemStackInSlot.item.isEmpty()) {
                        this.tooltipStack = bookItemStackInSlot.item;
                        this.tooltipText = bookItemStackInSlot.extra_tooltips;
                        this.drawTooltipStack = true;
                     }
                  } else {
                     this.tooltipText = getFluidTooltip(bookItemStackInSlot);
                     this.tooltipStack = ItemStack.EMPTY;
                     this.drawTooltipText = true;
                  }

                  this.slotOverlay.x = bookItemStackInSlot.x;
                  this.slotOverlay.y = bookItemStackInSlot.y;
                  ArrayList<BookImageEffect> effects = new ArrayList<>();
                  effects.add(new BookImageEffect("scale", 20.0F, 1.1F));
                  this.slotOverlay.effects = effects;
                  this.slotOverlayPageOn = pageOn;
                  this.drawSlotOverlay = true;
                  break;
               }

               if (this.drawTooltipStack) {
                  break;
               }
            }

            for (BookBlocks bookBlock : page.blockList) {
               if (canInteract(
                  pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
                  pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
                  bookBlock.x,
                  bookBlock.y,
                  0.86F,
                  0.86F,
                  altarTile,
                  drawingType
               )) {
                  if (!bookBlock.blockState.is(Blocks.AIR)) {
                     List<Component> tooltipList = new ArrayList<>(bookBlock.extra_tooltips);
                     tooltipList.addFirst(bookBlock.blockState.getBlock().getName().withStyle(ChatFormatting.WHITE));
                     this.tooltipText = tooltipList;
                     this.drawTooltipText = true;
                     this.tooltipStack = ItemStack.EMPTY;
                  }

                  this.slotOverlay.x = bookBlock.x;
                  this.slotOverlay.y = bookBlock.y;
                  ArrayList<BookImageEffect> effects = new ArrayList<>();
                  effects.add(new BookImageEffect("scale", 20.0F, 1.1F));
                  this.slotOverlay.effects = effects;
                  this.slotOverlayPageOn = pageOn;
                  this.drawSlotOverlay = true;
                  break;
               }

               if (this.drawTooltipText) {
                  break;
               }
            }

            for (BookEntity bookEntity : page.entityList) {
               bookEntity.hoverTickRender = this.easeInOutElastic(Mth.lerp(partial, bookEntity.hoverTickO, bookEntity.hoverTick));
               if (bookEntity.entity != null) {
                  bookEntity.entity.tickCount = (int)ClientEvents.getClientTicksWithoutPartial();
               }

               float xIn = bookEntity.x + bookEntity.offset.x + 0.52F;
               float yIn = bookEntity.y + bookEntity.offset.y;
               float width = 1.25F + bookEntity.scale / 5.0F;
               if (canInteract(
                  pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
                  pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
                  xIn - width / 2.0F,
                  yIn - width / 2.0F,
                  width,
                  width,
                  altarTile,
                  drawingType
               )) {
                  bookEntity.hovered = true;
               }

               if (bookEntity.hoverTickRender > 0.0F) {
                  BookImage bookImage = new BookImage(
                     bookEntity.x,
                     bookEntity.y + 0.5F,
                     0.0F,
                     0.0F,
                     0.0F,
                     64.0F,
                     32.0F,
                     64.0F,
                     32.0F,
                     0.75F * bookEntity.hoverTickRender,
                     "hexerei:textures/book/rotate_entity.png",
                     new ArrayList<>()
                  );
                  this.drawImage(
                     bookImage,
                     altarTile,
                     pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
                     pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
                     poseStack,
                     bufferIn,
                     0.0F,
                     combinedLightIn,
                     combinedOverlayIn,
                     pageOn,
                     drawingType
                  );
                  float lerpRotate = Mth.lerp(ClientEvents.getPartial(), bookEntity.toRotateO, bookEntity.toRotate);
                  float v = Math.clamp(lerpRotate / 2000.0F, -0.8F, 0.8F);
                  float v1 = (float)Math.pow(Math.min(Math.abs(lerpRotate) / 4000.0F, 0.4F), 2.0) * 2.25F;
                  BookImage bookImage2;
                  if (bookEntity.clicked) {
                     bookImage2 = new BookImage(
                        bookEntity.x - v,
                        bookEntity.y + 0.85F - v1,
                        1.0F,
                        0.0F,
                        0.0F,
                        32.0F,
                        48.0F,
                        32.0F,
                        48.0F,
                        0.45F * bookEntity.hoverTickRender,
                        "hexerei:textures/book/right_click_icon_hover.png",
                        new ArrayList<>()
                     );
                  } else {
                     bookImage2 = new BookImage(
                        bookEntity.x - v,
                        bookEntity.y + 0.85F - v1,
                        1.0F,
                        0.0F,
                        0.0F,
                        32.0F,
                        48.0F,
                        32.0F,
                        48.0F,
                        0.45F * bookEntity.hoverTickRender,
                        "hexerei:textures/book/right_click_icon.png",
                        new ArrayList<>()
                     );
                  }

                  this.drawImage(
                     bookImage2,
                     altarTile,
                     pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
                     pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
                     poseStack,
                     bufferIn,
                     0.0F,
                     combinedLightIn,
                     combinedOverlayIn,
                     pageOn,
                     drawingType
                  );
               }
            }

            for (BookNonItemTooltip bookNonItemTooltip : page.nonItemTooltipList) {
               if (canInteract(
                  leftCursorX,
                  leftCursorY,
                  bookNonItemTooltip.x,
                  bookNonItemTooltip.y,
                  bookNonItemTooltip.width,
                  bookNonItemTooltip.height,
                  altarTile,
                  drawingType
               )) {
                  this.tooltipText = bookNonItemTooltip.tooltip;
                  this.tooltipStack = ItemStack.EMPTY;
                  this.drawTooltipText = true;
               }

               if (this.drawTooltipText) {
                  break;
               }
            }

            for (BookWritableTextBox bookWritableTextBox : page.writableTextBoxes) {
               float xCursor = pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX;
               float yCursor = pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY;
               if (canInteract(
                     xCursor,
                     yCursor,
                     bookWritableTextBox.paragraphElement.x + 0.45F,
                     bookWritableTextBox.paragraphElement.y,
                     bookWritableTextBox.paragraphElement.width / 6.15F,
                     bookWritableTextBox.paragraphElement.height / 2.57F,
                     altarTile,
                     drawingType
                  )
                  && focusedWritableTextBox != null
                  && focusedWritableTextBox.getLeft() == altarTile
                  && focusedWritableTextBox.getRight() == bookWritableTextBox
                  && ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.clicked) {
                  BookWritableTextBox.Client.DisplayCache bookeditscreen$displaycache = ((BookWritableTextBox)focusedWritableTextBox.getRight())
                     .client
                     .getDisplayCache(((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook);
                  BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i(
                     (int)((xCursor - bookWritableTextBox.paragraphElement.x - 0.45F) / 5.0F * 115.0F),
                     (int)((yCursor - bookWritableTextBox.paragraphElement.y) / 7.1F * 162.0F)
                  );
                  if (pos2i.x != ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.clickedPos.x
                     || pos2i.y != ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.clickedPos.y) {
                     int i = bookeditscreen$displaycache.getIndexAtPosition(ClientProxy.font(), pos2i);
                     ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.setCursorPos(i, true);
                     ((BookWritableTextBox)focusedWritableTextBox.getRight())
                        .client
                        .clearDisplayCache(((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook.getUUID());
                  }
               }
            }
         }

         for (BookImage bookImage : page.imageList) {
            this.drawImage(
               bookImage,
               altarTile,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
               poseStack,
               bufferIn,
               0.0F,
               combinedLightIn,
               combinedOverlayIn,
               pageOn,
               drawingType
            );
            if (bookImage.extra_tooltips != null && !bookImage.extra_tooltips.isEmpty()) {
               float w = bookImage.width / 330.0F * bookImage.scale / 0.062F;
               float h = bookImage.height / 330.0F * bookImage.scale / 0.062F;
               float x = bookImage.x - w / 2.0F + 0.45F;
               float y = bookImage.y - h / 2.0F + 0.49F;
               if (canInteract(
                     pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
                     pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
                     x,
                     y,
                     w,
                     h,
                     altarTile,
                     drawingType
                  )
                  && (pageOn == PageDrawing.PageOn.LEFT_PAGE || pageOn == PageDrawing.PageOn.RIGHT_PAGE)) {
                  this.tooltipText = bookImage.extra_tooltips;
                  this.tooltipStack = ItemStack.EMPTY;
                  this.drawTooltipText = true;
               }
            }
         }

         if (this.drawSlotOverlay) {
            this.drawImage(
               this.slotOverlay,
               altarTile,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorX : rightCursorX,
               pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftCursorY : rightCursorY,
               poseStack,
               bufferIn,
               0.0F,
               combinedLightIn,
               combinedOverlayIn,
               this.slotOverlayPageOn,
               drawingType
            );
         }

         for (BookEntity bookEntity : page.entityList) {
            bookEntity.markedForUpdate = true;
            if (bookEntity.entity instanceof TamableAnimal tamable && tamable.isOrderedToSit() && !tamable.isInSittingPose()) {
               tamable.setInSittingPose(true);
            }

            if (bookEntity.entity instanceof LivingEntity livingEntity) {
               this.drawLivingEntity(
                  altarTile,
                  poseStack,
                  bufferIn,
                  bookEntity.scale,
                  bookEntity.x,
                  bookEntity.y,
                  bookEntity.getRot(partial) + 3.1415927F,
                  20,
                  107.0F,
                  58.0F,
                  livingEntity,
                  combinedLightIn,
                  combinedOverlayIn,
                  pageOn,
                  drawingType
               );
            } else if (bookEntity.entity != null) {
               this.drawEntity(
                  altarTile,
                  poseStack,
                  bufferIn,
                  bookEntity.scale,
                  bookEntity.x,
                  bookEntity.y,
                  bookEntity.getRot(partial) + 3.1415927F,
                  20,
                  107.0F,
                  58.0F,
                  bookEntity.entity,
                  combinedLightIn,
                  combinedOverlayIn,
                  pageOn,
                  drawingType
               );
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawLivingEntity(
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferIn,
      float scale,
      float xIn,
      float yIn,
      float rot,
      int p_98853_,
      float p_98854_,
      float p_98855_,
      LivingEntity livingEntity,
      int combinedLightIn,
      int combinedOverlayIn,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      poseStack.translate(-0.001953125F, 0.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -0.0025F);
      poseStack.scale(0.049F * scale, 0.049F * scale, 0.003F);
      poseStack.translate(yIn * 1.25F / scale, -xIn * 1.25F / scale, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      float $$6 = (float)Math.atan(p_98854_ / 40.0F);
      float $$7 = (float)Math.atan(p_98855_ / 40.0F);
      Quaternionf $$10 = com.mojang.math.Axis.ZP.rotationDegrees(180.0F);
      Quaternionf $$11 = com.mojang.math.Axis.XP.rotationDegrees($$7 * 20.0F);
      $$10.mul($$11);
      float $$12 = livingEntity.yBodyRot;
      float $$13 = livingEntity.getYRot();
      float $$15 = livingEntity.yHeadRotO;
      float $$16 = livingEntity.yHeadRot;
      livingEntity.yBodyRot = rot + livingEntity.getId();
      livingEntity.setYRot(rot + livingEntity.getId());
      livingEntity.yHeadRot = livingEntity.getYRot();
      livingEntity.yHeadRotO = livingEntity.getYRot();
      EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
      $$11.conjugate();
      $$17.overrideCameraOrientation($$11);
      $$17.setRenderShadow(false);
      BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> $$17.render(livingEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, $$18, combinedLightIn));
      $$18.endBatch();
      $$17.setRenderShadow(true);
      livingEntity.yBodyRot = $$12;
      livingEntity.setYRot($$13);
      livingEntity.yHeadRotO = $$15;
      livingEntity.yHeadRot = $$16;
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawEntity(
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferIn,
      float scale,
      float xIn,
      float yIn,
      float rot,
      int p_98853_,
      float p_98854_,
      float p_98855_,
      Entity entity,
      int combinedLightIn,
      int combinedOverlayIn,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      poseStack.translate(-0.001953125F, 0.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -0.0025F);
      poseStack.scale(0.049F * scale, 0.049F * scale, 0.003F);
      poseStack.translate(yIn * 1.25F / scale, -xIn * 1.25F / scale, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      float $$7 = (float)Math.atan(p_98855_ / 40.0F);
      Quaternionf $$10 = com.mojang.math.Axis.ZP.rotationDegrees(180.0F);
      Quaternionf $$11 = com.mojang.math.Axis.XP.rotationDegrees($$7 * 20.0F);
      $$10.mul($$11);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-(rot + entity.getId())));
      EntityRenderDispatcher $$17 = Minecraft.getInstance().getEntityRenderDispatcher();
      $$11.conjugate();
      $$17.overrideCameraOrientation($$11);
      $$17.setRenderShadow(false);
      BufferSource $$18 = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> $$17.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, $$18, combinedLightIn));
      $$18.endBatch();
      $$17.setRenderShadow(true);
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTooltips(BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, float partialTicks) throws CommandSyntaxException {
      this.drawTooltip = altarTile.turnPage == 0;
      this.drawTooltipScale = Mth.lerp(partialTicks, altarTile.tooltipScaleOld, altarTile.tooltipScale);
      this.drawTooltipScaleOld = this.drawTooltipScale;
      if (this.drawTooltipStack && altarTile.turnPage == 0) {
         altarTile.drawTooltip = true;
         this.drawTooltipStackFlag = true;
         this.drawTooltipTextFlag = false;
      } else if (this.drawTooltipText && altarTile.turnPage == 0) {
         altarTile.drawTooltip = true;
         this.drawTooltipTextFlag = true;
         this.drawTooltipStackFlag = false;
      } else {
         altarTile.drawTooltip = false;
         if (this.drawTooltipScale == 0.0F) {
            this.drawTooltipStackFlag = false;
            this.drawTooltipTextFlag = false;
         }
      }

      if (this.drawTooltipScale > 0.0F) {
         if (this.drawTooltipStackFlag) {
            this.drawTooltipImage(this.tooltipStack, altarTile, poseStack, bufferSource, 0.0F, light, overlay, partialTicks);
         } else {
            this.drawTooltipText(altarTile, poseStack, bufferSource, 0.0F, light, overlay, partialTicks);
         }
      }
   }

   public int getBookPageSeed(String location, UUID bookuuid) {
      return location.hashCode() * 31959 * bookuuid.hashCode();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawPages(
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int light,
      int overlay,
      float partialTicks,
      PageDrawing.DrawingType drawingType
   ) {
      this.drawPages(
         altarTile,
         leftCursorX,
         leftCursorY,
         rightCursorX,
         rightCursorY,
         poseStack,
         bufferSource,
         light,
         overlay,
         drawingType,
         ItemDisplayContext.NONE,
         partialTicks
      );
   }

   @OnlyIn(Dist.CLIENT)
   public void drawPages(
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int light,
      int overlay,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      float partialTicks
   ) {
      if (ClientProxy.keys == null) {
         ClientProxy.keys = Minecraft.getInstance().options.keyMappings;
      }

      this.drawSlotOverlay = false;
      this.drawTooltipStack = false;
      this.drawTooltipText = false;
      BookData bookData = altarTile.currentBook;
      if (bookData != null) {
         BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
         if (bookEntries != null) {
            String left_page = "";
            String right_page = "";
            String left_page_prev = "";
            String left_page_under = "";
            String left_page_under_under = "";
            String left_page_under_under_under = "";
            String right_page_under = "";
            String right_page_prev = "";
            String right_page_prev_prev = "";
            int location1_p = 0;
            int location2_p = 0;
            int location1_back_p = 0;
            int location2_back_p = 0;
            int location1_next_p = 0;
            int location2_next_p = 0;
            int chapter = 0;
            int page = 0;
            chapter = Math.max(0, bookData.getChapter());
            page = Math.max(0, bookData.getPage());
            if (page % 2 == 1) {
               page--;
            }

            if (bookEntries.chapterList.get(chapter).pages.size() > page && page >= 0) {
               BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page);
               left_page = pageEntry.location;
               location1_p = pageEntry.pageNum;
            }

            if (bookEntries.chapterList.get(chapter).pages.size() > page + 1 && page >= 0) {
               BookPageEntry pageEntry = bookEntries.chapterList.get(chapter).pages.get(page + 1);
               right_page = pageEntry.location;
               location2_p = pageEntry.pageNum;
            }

            int next_page_chapter = chapter;
            int next_page_page = page;
            int back_page_chapter = chapter;
            int back_page_page = page;
            if (page < bookEntries.chapterList.get(chapter).pages.size() - 2) {
               next_page_page = page + 2;
            } else if (chapter < bookEntries.chapterList.size() - 1) {
               next_page_chapter = chapter + 1;
               next_page_page = 0;
            } else {
               next_page_chapter = -1;
            }

            if (next_page_chapter != -1
               && next_page_chapter < bookEntries.chapterList.size()
               && next_page_page < bookEntries.chapterList.get(next_page_chapter).pages.size()) {
               BookPageEntry pageEntry = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page);
               right_page_under = pageEntry.location;
               location1_next_p = pageEntry.pageNum;
               if (bookEntries.chapterList.get(next_page_chapter).pages.size() > next_page_page + 1) {
                  BookPageEntry pageEntry2 = bookEntries.chapterList.get(next_page_chapter).pages.get(next_page_page + 1);
                  right_page_prev = pageEntry2.location;
                  location2_next_p = pageEntry2.pageNum;
                  List<BookPageEntry> entries = bookEntries.chapterList.stream().flatMap(entry -> entry.pages.stream()).toList();
                  if (location2_next_p + 2 < entries.size()) {
                     right_page_prev_prev = entries.get(location2_next_p + 2).location;
                  }
               }
            }

            if (page - 2 >= 0) {
               back_page_page = page - 2;
            } else if (chapter > 0) {
               back_page_chapter = chapter - 1;
               back_page_page = bookEntries.chapterList.get(back_page_chapter).pages.size() - 1;
               if (back_page_page % 2 == 1) {
                  back_page_page--;
               }
            } else {
               back_page_chapter = -1;
            }

            if (back_page_chapter != -1
               && back_page_chapter < bookEntries.chapterList.size()
               && back_page_page < bookEntries.chapterList.get(back_page_chapter).pages.size()) {
               BookPageEntry pageEntry = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page);
               left_page_prev = pageEntry.location;
               location1_back_p = pageEntry.pageNum;
               if (bookEntries.chapterList.get(back_page_chapter).pages.size() > back_page_page + 1) {
                  BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page + 1);
                  left_page_under = pageEntry2.location;
                  location2_back_p = pageEntry2.pageNum;
               }

               if (back_page_page - 1 > 0) {
                  BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page - 1);
                  left_page_under_under = pageEntry2.location;
               } else if (back_page_chapter - 1 > 0) {
                  BookPageEntry pageEntry2 = (BookPageEntry)bookEntries.chapterList.get(back_page_chapter - 1).pages.getLast();
                  left_page_under_under = pageEntry2.location;
               }

               if (back_page_page - 3 > 0) {
                  BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter).pages.get(back_page_page - 3);
                  left_page_under_under_under = pageEntry2.location;
               } else if (back_page_chapter - 1 > 0) {
                  BookPageEntry pageEntry2 = bookEntries.chapterList.get(back_page_chapter - 1)
                     .pages
                     .get(bookEntries.chapterList.get(back_page_chapter - 1).pages.size() - 3);
                  left_page_under_under_under = pageEntry2.location;
               }
            }

            if (transformType != ItemDisplayContext.GUI) {
               if (drawingType == PageDrawing.DrawingType.SCREEN) {
                  if (altarTile.pageOneRotationRender < 155.0F && altarTile.pageTwoRotationRender < 155.0F) {
                     int seed = this.getBookPageSeed(left_page_under, bookData.getUUID());
                     Random random = new Random(seed);
                     String pageLoc = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                     this.drawBasePage(
                        new BookImage(2.325F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, pageLoc, new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                     seed = this.getBookPageSeed(right_page, bookData.getUUID());
                     random = new Random(seed);
                     pageLoc = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(random.nextInt(pageTextureLocs.size())).toString();
                     this.drawBasePage(
                        new BookImage(2.6750002F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, pageLoc, new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                     seed = this.getBookPageSeed(right_page, bookData.getUUID());
                     random = new Random(seed);
                     ResourceLocation loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                     if (loc != null && seed != 0) {
                        this.drawImage(
                           new BookImage(2.6750002F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, loc.toString(), new ArrayList<>()),
                           altarTile,
                           rightCursorX,
                           rightCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.RIGHT_PAGE,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }

                     seed = this.getBookPageSeed(left_page, bookData.getUUID());
                     random = new Random(seed);
                     loc = overlayTextureLocs.get(random.nextInt(overlayTextureLocs.size()));
                     if (loc != null && seed != 0) {
                        this.drawBasePage(
                           new BookImage(2.325F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, loc.toString(), new ArrayList<>()),
                           altarTile,
                           leftCursorX,
                           leftCursorY,
                           rightCursorX,
                           rightCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.LEFT_PAGE,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }

                     BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page));
                     BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page));
                     this.drawPage(
                        page1,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE,
                        drawingType,
                        transformType,
                        location1_p,
                        partialTicks
                     );
                     this.drawPage(
                        page2,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE,
                        drawingType,
                        transformType,
                        location2_p,
                        partialTicks
                     );
                  }

                  if (altarTile.pageOneRotationRender > 15.0F) {
                     int seedx = this.getBookPageSeed(right_page, bookData.getUUID());
                     Random randomx = new Random(seedx);
                     String pageLocx = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(randomx.nextInt(pageTextureLocs.size())).toString();
                     this.drawBasePage(
                        new BookImage(2.325F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, pageLocx, new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_UNDER,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                     seedx = this.getBookPageSeed(right_page_prev, bookData.getUUID());
                     randomx = new Random(seedx);
                     pageLocx = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(randomx.nextInt(pageTextureLocs.size())).toString();
                     this.drawBasePage(
                        new BookImage(2.6750002F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, pageLocx, new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_PREV,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                     seedx = this.getBookPageSeed(right_page_prev, bookData.getUUID());
                     randomx = new Random(seedx);
                     ResourceLocation locx = overlayTextureLocs.get(randomx.nextInt(overlayTextureLocs.size()));
                     if (locx != null && seedx != 0) {
                        this.drawImage(
                           new BookImage(2.6750002F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, locx.toString(), new ArrayList<>()),
                           altarTile,
                           rightCursorX,
                           rightCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.RIGHT_PAGE_PREV,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }

                     seedx = this.getBookPageSeed(right_page_under, bookData.getUUID());
                     randomx = new Random(seedx);
                     locx = overlayTextureLocs.get(randomx.nextInt(overlayTextureLocs.size()));
                     if (locx != null && seedx != 0) {
                        this.drawImage(
                           new BookImage(2.325F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, locx.toString(), new ArrayList<>()),
                           altarTile,
                           leftCursorX,
                           leftCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.RIGHT_PAGE_UNDER,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }

                     BookPage page2_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_under));
                     BookPage page2_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_prev));
                     this.drawPage(
                        page2_under,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_UNDER,
                        drawingType,
                        transformType,
                        location1_next_p,
                        partialTicks
                     );
                     this.drawPage(
                        page2_prev,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_PREV,
                        drawingType,
                        transformType,
                        location2_next_p,
                        partialTicks
                     );
                  }

                  if (altarTile.pageTwoRotationRender > 15.0F) {
                     int seedxx = this.getBookPageSeed(left_page_under_under, bookData.getUUID());
                     Random randomxx = new Random(seedxx);
                     String pageLocxx = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(randomxx.nextInt(pageTextureLocs.size())).toString();
                     this.drawBasePage(
                        new BookImage(2.325F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, pageLocxx, new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_PREV,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                     seedxx = this.getBookPageSeed(left_page_under, bookData.getUUID());
                     randomxx = new Random(seedxx);
                     pageLocxx = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(randomxx.nextInt(pageTextureLocs.size())).toString();
                     this.drawBasePage(
                        new BookImage(2.6750002F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, pageLocxx, new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_UNDER,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                     seedxx = this.getBookPageSeed(left_page_under, bookData.getUUID());
                     randomxx = new Random(seedxx);
                     ResourceLocation locxx = overlayTextureLocs.get(randomxx.nextInt(overlayTextureLocs.size()));
                     if (locxx != null && seedxx != 0) {
                        this.drawBasePage(
                           new BookImage(2.6750002F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, locxx.toString(), new ArrayList<>()),
                           altarTile,
                           leftCursorX,
                           leftCursorY,
                           rightCursorX,
                           rightCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.LEFT_PAGE_UNDER,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }

                     seedxx = this.getBookPageSeed(left_page_prev, bookData.getUUID());
                     randomxx = new Random(seedxx);
                     locxx = overlayTextureLocs.get(randomxx.nextInt(overlayTextureLocs.size()));
                     if (locxx != null && seedxx != 0) {
                        this.drawBasePage(
                           new BookImage(2.325F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, locxx.toString(), new ArrayList<>()),
                           altarTile,
                           leftCursorX,
                           leftCursorY,
                           rightCursorX,
                           rightCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.LEFT_PAGE_PREV,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }

                     BookPage page1_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_under));
                     BookPage page1_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_prev));
                     this.drawPage(
                        page1_under,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_UNDER,
                        drawingType,
                        transformType,
                        location2_back_p,
                        partialTicks
                     );
                     this.drawPage(
                        page1_prev,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_PREV,
                        drawingType,
                        transformType,
                        location1_back_p,
                        partialTicks
                     );
                  }
               } else {
                  int seedxxx = this.getBookPageSeed(left_page_under_under, bookData.getUUID());
                  Random randomxxx = new Random(seedxxx);
                  String pageLocxxx = pageTextureLocs.isEmpty()
                     ? "hexerei:textures/book/pages/page_1.png"
                     : pageTextureLocs.get(randomxxx.nextInt(pageTextureLocs.size())).toString();
                  this.drawBasePage(
                     new BookImage(2.325F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, pageLocxxx, new ArrayList<>()),
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     PageDrawing.PageOn.LEFT_PAGE_PREV,
                     -1,
                     drawingType,
                     transformType
                  );
                  seedxxx = this.getBookPageSeed(left_page_under, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  pageLocxxx = pageTextureLocs.isEmpty()
                     ? "hexerei:textures/book/pages/page_1.png"
                     : pageTextureLocs.get(randomxxx.nextInt(pageTextureLocs.size())).toString();
                  this.drawBasePage(
                     new BookImage(2.6750002F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, pageLocxxx, new ArrayList<>()),
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     PageDrawing.PageOn.LEFT_PAGE_UNDER,
                     -1,
                     drawingType,
                     transformType
                  );
                  this.drawBasePage(
                     new BookImage(2.325F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, pageLocxxx, new ArrayList<>()),
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     PageDrawing.PageOn.LEFT_PAGE,
                     -1,
                     drawingType,
                     transformType
                  );
                  seedxxx = this.getBookPageSeed(right_page, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  pageLocxxx = pageTextureLocs.isEmpty()
                     ? "hexerei:textures/book/pages/page_1.png"
                     : pageTextureLocs.get(randomxxx.nextInt(pageTextureLocs.size())).toString();
                  this.drawBasePage(
                     new BookImage(2.6750002F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, pageLocxxx, new ArrayList<>()),
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     PageDrawing.PageOn.RIGHT_PAGE,
                     -1,
                     drawingType,
                     transformType
                  );
                  this.drawBasePage(
                     new BookImage(2.325F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, pageLocxxx, new ArrayList<>()),
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     PageDrawing.PageOn.RIGHT_PAGE_UNDER,
                     -1,
                     drawingType,
                     transformType
                  );
                  seedxxx = this.getBookPageSeed(right_page_prev, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  pageLocxxx = pageTextureLocs.isEmpty()
                     ? "hexerei:textures/book/pages/page_1.png"
                     : pageTextureLocs.get(randomxxx.nextInt(pageTextureLocs.size())).toString();
                  this.drawBasePage(
                     new BookImage(2.6750002F, 3.0600002F, -0.2F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, pageLocxxx, new ArrayList<>()),
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     PageDrawing.PageOn.RIGHT_PAGE_PREV,
                     -1,
                     drawingType,
                     transformType
                  );
                  seedxxx = this.getBookPageSeed(left_page_prev, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  ResourceLocation locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                  if (locxxx != null && seedxxx != 0) {
                     this.drawImage(
                        new BookImage(2.325F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, locxxx.toString(), new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_PREV,
                        -1711276033,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                  }

                  seedxxx = this.getBookPageSeed(right_page, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                  if (locxxx != null && seedxxx != 0) {
                     this.drawImage(
                        new BookImage(2.6750002F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, locxxx.toString(), new ArrayList<>()),
                        altarTile,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE,
                        -1711276033,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                  }

                  seedxxx = this.getBookPageSeed(right_page_under, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                  if (locxxx != null && seedxxx != 0) {
                     this.drawImage(
                        new BookImage(2.325F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, locxxx.toString(), new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_UNDER,
                        -1711276033,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                  }

                  seedxxx = this.getBookPageSeed(left_page_under, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                  if (locxxx != null && seedxxx != 0) {
                     this.drawImage(
                        new BookImage(2.6750002F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, locxxx.toString(), new ArrayList<>()),
                        altarTile,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_UNDER,
                        -1711276033,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                  }

                  seedxxx = this.getBookPageSeed(left_page, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                  if (locxxx != null && seedxxx != 0) {
                     this.drawImage(
                        new BookImage(2.325F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, 13.0F, 18.0F, 10.2F, locxxx.toString(), new ArrayList<>()),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE,
                        -1711276033,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                  }

                  seedxxx = this.getBookPageSeed(right_page_prev, bookData.getUUID());
                  randomxxx = new Random(seedxxx);
                  locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                  if (locxxx != null && seedxxx != 0) {
                     this.drawImage(
                        new BookImage(2.6750002F, 3.0600002F, -0.19F, 0.0F, 0.0F, 13.0F, 18.0F, -13.0F, 18.0F, 10.2F, locxxx.toString(), new ArrayList<>()),
                        altarTile,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_PREV,
                        -1711276033,
                        drawingType,
                        ItemDisplayContext.NONE
                     );
                  }

                  if (altarTile.openedPercent < 0.6F) {
                     seedxxx = this.getBookPageSeed(left_page_under_under_under, bookData.getUUID());
                     randomxxx = new Random(seedxxx);
                     pageLocxxx = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(randomxxx.nextInt(pageTextureLocs.size())).toString();
                     this.drawImage(
                        new BookImage(
                           -0.5F - (0.5F - altarTile.pageTwoRotationRender / 180.0F * 0.05F) + 3.275F,
                           3.0600002F,
                           -0.3F + -altarTile.pageOneRotationRender / 180.0F / 64.0F,
                           0.0F,
                           0.0F,
                           13.0F,
                           18.0F,
                           13.0F,
                           18.0F,
                           10.2F,
                           pageLocxxx,
                           new ArrayList<>()
                        ),
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_PREV_PREV,
                        drawingType
                     );
                     seedxxx = this.getBookPageSeed(left_page_under_under_under, bookData.getUUID());
                     randomxxx = new Random(seedxxx);
                     locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                     if (locxxx != null && seedxxx != 0) {
                        this.drawImage(
                           new BookImage(
                              -0.5F - (0.5F - altarTile.pageTwoRotationRender / 180.0F * 0.05F) + 3.275F,
                              3.0600002F,
                              -0.29F,
                              0.0F,
                              0.0F,
                              13.0F,
                              18.0F,
                              13.0F,
                              18.0F,
                              10.2F,
                              locxxx.toString(),
                              new ArrayList<>()
                           ),
                           altarTile,
                           leftCursorX,
                           leftCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.LEFT_PAGE_PREV_PREV,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }

                     seedxxx = this.getBookPageSeed(right_page_prev_prev, bookData.getUUID());
                     randomxxx = new Random(seedxxx);
                     pageLocxxx = pageTextureLocs.isEmpty()
                        ? "hexerei:textures/book/pages/page_1.png"
                        : pageTextureLocs.get(randomxxx.nextInt(pageTextureLocs.size())).toString();
                     this.drawImage(
                        new BookImage(
                           -0.5F - (0.05F + altarTile.pageOneRotationRender / 180.0F * 0.05F) + 3.275F,
                           3.0600002F,
                           -0.3F + -altarTile.pageOneRotationRender / 180.0F / 64.0F,
                           0.0F,
                           0.0F,
                           13.0F,
                           18.0F,
                           -13.0F,
                           18.0F,
                           10.2F,
                           pageLocxxx,
                           new ArrayList<>()
                        ),
                        altarTile,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV,
                        drawingType
                     );
                     seedxxx = this.getBookPageSeed(right_page_prev_prev, bookData.getUUID());
                     randomxxx = new Random(seedxxx);
                     locxxx = overlayTextureLocs.get(randomxxx.nextInt(overlayTextureLocs.size()));
                     if (locxxx != null && seedxxx != 0) {
                        this.drawImage(
                           new BookImage(
                              -0.5F - (0.05F + altarTile.pageOneRotationRender / 180.0F * 0.05F) + 3.275F,
                              3.0600002F,
                              -0.29F,
                              0.0F,
                              0.0F,
                              13.0F,
                              18.0F,
                              -13.0F,
                              18.0F,
                              10.2F,
                              locxxx.toString(),
                              new ArrayList<>()
                           ),
                           altarTile,
                           rightCursorX,
                           rightCursorY,
                           poseStack,
                           bufferSource,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV,
                           -1711276033,
                           drawingType,
                           ItemDisplayContext.NONE
                        );
                     }
                  }

                  BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page));
                  BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page));
                  this.drawPage(
                     page1,
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     light,
                     overlay,
                     PageDrawing.PageOn.LEFT_PAGE,
                     drawingType,
                     transformType,
                     location1_p,
                     partialTicks
                  );
                  this.drawPage(
                     page2,
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     rightCursorX,
                     rightCursorY,
                     poseStack,
                     bufferSource,
                     light,
                     overlay,
                     PageDrawing.PageOn.RIGHT_PAGE,
                     drawingType,
                     transformType,
                     location2_p,
                     partialTicks
                  );
                  if (altarTile.pageTwoRotationRender < 87.5F) {
                     BookPage page2_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_under));
                     BookPage page2_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(right_page_prev));
                     this.drawPage(
                        page2_under,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_UNDER,
                        drawingType,
                        transformType,
                        location1_next_p,
                        partialTicks
                     );
                     this.drawPage(
                        page2_prev,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE_PREV,
                        drawingType,
                        transformType,
                        location2_next_p,
                        partialTicks
                     );
                  }

                  if (altarTile.pageOneRotationRender < 87.5F) {
                     BookPage page1_under = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_under));
                     BookPage page1_prev = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(left_page_prev));
                     this.drawPage(
                        page1_under,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_UNDER,
                        drawingType,
                        transformType,
                        location2_back_p,
                        partialTicks
                     );
                     this.drawPage(
                        page1_prev,
                        altarTile,
                        leftCursorX,
                        leftCursorY,
                        rightCursorX,
                        rightCursorY,
                        poseStack,
                        bufferSource,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE_PREV,
                        drawingType,
                        transformType,
                        location1_back_p,
                        partialTicks
                     );
                  }
               }
            } else {
               BookPage page1x = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse("hexerei:book/book_pages/gui_page_1"));
               BookPage page2x = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse("hexerei:book/book_pages/gui_page_1"));
               this.drawPage(
                  page1x,
                  altarTile,
                  leftCursorX,
                  leftCursorY,
                  rightCursorX,
                  rightCursorY,
                  poseStack,
                  bufferSource,
                  light,
                  overlay,
                  PageDrawing.PageOn.LEFT_PAGE,
                  drawingType,
                  transformType,
                  location1_p,
                  partialTicks
               );
               this.drawPage(
                  page2x,
                  altarTile,
                  leftCursorX,
                  leftCursorY,
                  rightCursorX,
                  rightCursorY,
                  poseStack,
                  bufferSource,
                  light,
                  overlay,
                  PageDrawing.PageOn.RIGHT_PAGE,
                  drawingType,
                  transformType,
                  location2_p,
                  partialTicks
               );
            }

            this.drawBaseButtons(
               altarTile,
               leftCursorX,
               leftCursorY,
               rightCursorX,
               rightCursorY,
               poseStack,
               bufferSource,
               light,
               overlay,
               !right_page_under.isEmpty(),
               !left_page_prev.isEmpty(),
               chapter,
               page,
               drawingType,
               partialTicks
            );
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawBaseButtons(
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int light,
      int overlay,
      boolean drawNext,
      boolean drawBack,
      int chapter,
      int page,
      PageDrawing.DrawingType drawingType,
      float partial
   ) {
      this.drawBaseButtons(
         altarTile,
         leftCursorX,
         leftCursorY,
         rightCursorX,
         rightCursorY,
         poseStack,
         bufferSource,
         light,
         overlay,
         drawNext,
         drawBack,
         chapter,
         page,
         drawingType,
         ItemDisplayContext.NONE,
         false,
         partial
      );
   }

   @OnlyIn(Dist.CLIENT)
   public void drawBaseButtons(
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int light,
      int overlay,
      boolean drawNext,
      boolean drawBack,
      int chapter,
      int page,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      boolean fullyExtended,
      float partial
   ) {
      BookEntries bookEntries = BookManager.getBookEntries(altarTile.currentBook.getBook());
      if (bookEntries != null) {
         for (int i = 0; i < this.bookmarkHoverAmountRender.size(); i++) {
            this.bookmarkHoverAmountRender
               .set(i, this.easeInOutElastic(Mth.lerp(partial, this.bookmarkHoverAmountOld.get(i), this.bookmarkHoverAmount.get(i))));
         }

         boolean drawBookmarkButton = chapter != 0;
         BookData bookData = altarTile.currentBook;
         if (drawBookmarkButton && drawingType != PageDrawing.DrawingType.GUI) {
            ArrayList<BookImageEffect> effects = new ArrayList<>();
            BookImageEffect bookImageEffect_scale = new BookImageEffect("scale", 50.0F, 1.15F);
            BookImageEffect bookImageEffect_tilt = new BookImageEffect("tilt", 35.0F, 10.0F);
            BookImageEffect bookImageEffect_hover_overlay = new BookImageEffect(
               "hover_overlay",
               35.0F,
               10.0F,
               new BookImage(
                  -0.5F,
                  -1.0F,
                  -1.0F,
                  0.0F,
                  0.0F,
                  32.0F,
                  32.0F,
                  32.0F,
                  32.0F,
                  altarTile.buttonScaleRender / 2.0F,
                  "hexerei:textures/book/bookmark_button_hover.png",
                  effects
               )
            );
            boolean flag = canInteract(leftCursorX, leftCursorY, -0.45F, -0.96F, 0.86F, 0.86F, altarTile, drawingType);
            if (flag) {
               effects.add(bookImageEffect_scale);
               effects.add(bookImageEffect_tilt);
               effects.add(bookImageEffect_hover_overlay);
            }

            if (bookData != null) {
               DyeColor bookmark_color = DyeColor.WHITE;
               int bookmark_chapter = 0;
               int bookmark_page = 0;
               boolean flag2 = false;

               for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
                  boolean flag3 = false;
                  if (!slot.getId().isEmpty()) {
                     bookmark_color = slot.getColor();
                     String bookmark_id = slot.getId();

                     for (BookChapter chapterEntry : bookEntries.chapterList) {
                        for (BookPageEntry pageEntry : chapterEntry.pages) {
                           if (pageEntry.location.equals(bookmark_id)) {
                              bookmark_chapter = pageEntry.chapterNum;
                              bookmark_page = pageEntry.chapterPageNum;
                              flag3 = true;
                              break;
                           }
                        }
                     }
                  }

                  if (flag3 && chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page)) {
                     flag2 = true;
                     break;
                  }
               }

               if (flag2) {
                  if (flag) {
                     List<Component> list = new ArrayList<>();
                     String output = bookmark_color.getName().substring(0, 1).toUpperCase() + bookmark_color.getName().substring(1);
                     output = output.replaceAll("_", " ");
                     list.add(
                        Component.translatable(
                              "Change Color - %s",
                              new Object[]{
                                 Component.translatable("%s", new Object[]{output}).withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                              }
                           )
                           .withStyle(Style.EMPTY.withItalic(true).withColor(10329495))
                     );
                     this.tooltipText = list;
                     this.tooltipStack = ItemStack.EMPTY;
                     this.drawTooltipText = true;
                  }

                  BookImage bookImage = new BookImage(
                     -0.5F,
                     -1.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     32.0F,
                     32.0F,
                     32.0F,
                     32.0F,
                     altarTile.buttonScaleRender / 2.0F * 1.15F,
                     "hexerei:textures/book/bookmark_button_underlay.png",
                     effects
                  );
                  BookImage bookImage_overlay = new BookImage(
                     -0.5F,
                     -1.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     32.0F,
                     32.0F,
                     32.0F,
                     32.0F,
                     altarTile.buttonScaleRender / 2.0F * 1.15F,
                     "hexerei:textures/book/bookmark_button_overlay.png",
                     effects
                  );
                  this.drawImage(
                     bookImage, altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0.0F, light, overlay, PageDrawing.PageOn.LEFT_PAGE, drawingType
                  );
                  this.drawImage(
                     bookImage_overlay,
                     altarTile,
                     leftCursorX,
                     leftCursorY,
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     PageDrawing.PageOn.LEFT_PAGE,
                     HexereiUtil.getColorValue(bookmark_color),
                     drawingType,
                     transformType
                  );
               } else {
                  if (flag) {
                     List<Component> list = new ArrayList<>();
                     list.add(Component.translatable("Bookmark Page").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                     this.tooltipText = list;
                     this.tooltipStack = ItemStack.EMPTY;
                     this.drawTooltipText = true;
                  }

                  BookImage bookImage = new BookImage(
                     -0.5F,
                     -1.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     32.0F,
                     32.0F,
                     32.0F,
                     32.0F,
                     altarTile.buttonScaleRender / 2.0F * 1.15F,
                     "hexerei:textures/book/bookmark_button.png",
                     effects
                  );
                  this.drawImage(
                     bookImage, altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0.0F, light, overlay, PageDrawing.PageOn.LEFT_PAGE, drawingType
                  );
               }
            }
         }

         if (bookData != null) {
            int bookmark_chapter = 0;
            int bookmark_page = 0;

            for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
               boolean flag2 = false;
               if (!slot.getId().isEmpty()) {
                  DyeColor bookmark_color = slot.getColor();
                  ResourceLocation bookmark_id;
                  if (!slot.getId().isEmpty()) {
                     bookmark_id = ResourceLocation.parse(slot.getId());
                  } else {
                     bookmark_id = null;
                  }

                  boolean flag3x = false;
                  if (bookmark_id != null) {
                     for (BookChapter chapterEntry : bookEntries.chapterList) {
                        for (BookPageEntry pageEntryx : chapterEntry.pages) {
                           if (ResourceLocation.parse(pageEntryx.location).equals(bookmark_id)) {
                              bookmark_chapter = pageEntryx.chapterNum;
                              bookmark_page = pageEntryx.chapterPageNum;
                              flag3x = true;
                              break;
                           }
                        }
                     }
                  }

                  ArrayList<BookImageEffect> effectsBookmark = new ArrayList<>();
                  if (slot.getIndex() < 5) {
                     float xIn = -0.3F - altarTile.buttonScaleRender - 0.15F;
                     float yIn = slot.getIndex() * 1.5F;
                     float width = 0.935F;
                     if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                        if (!this.bookmarkHovered.contains(slot.getIndex())) {
                           this.bookmarkHovered.add(slot.getIndex());
                        }

                        List<Component> list = new ArrayList<>();
                        if (flag3x) {
                           String name = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                           BookPage bookmarkedPage = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(name));
                           if (bookmarkedPage != null) {
                              name = bookmarkedPage.name;
                           }

                           if (name.isEmpty()) {
                              list.add(
                                 Component.translatable(
                                       "%s%s - Page %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(
                                                "%s",
                                                new Object[]{
                                                   bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1
                                                }
                                             )
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           } else {
                              list.add(
                                 Component.translatable(
                                       "%s%s - %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(name).withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           }

                           this.tooltipText = list;
                           this.tooltipStack = ItemStack.EMPTY;
                           this.drawTooltipText = true;
                        }
                     }

                     float bookX = xIn + 0.8F - this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2.0F * altarTile.buttonScaleRender;
                     if (fullyExtended) {
                        bookX = xIn + 0.4F - 0.33F;
                     }

                     BookImage bookImageUnderlay = new BookImage(
                        bookX, yIn, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark
                     );
                     BookImage bookImageOverlay = new BookImage(
                        bookX, yIn, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark
                     );
                     this.drawBookmark(
                        bookImageUnderlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        90.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                     this.drawBookmark(
                        bookImageOverlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        90.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                  }

                  if (slot.getIndex() >= 5 && slot.getIndex() < 10) {
                     float xInx = -5.5F + slot.getIndex() * 1.15F;
                     float yInx = -0.75F - altarTile.buttonScaleRender - 0.25F;
                     float widthx = 0.86F;
                     if (canInteract(leftCursorX, leftCursorY, xInx, yInx, widthx, widthx, altarTile, drawingType)) {
                        if (!this.bookmarkHovered.contains(slot.getIndex())) {
                           this.bookmarkHovered.add(slot.getIndex());
                        }

                        List<Component> list = new ArrayList<>();
                        if (flag3x) {
                           String namex = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                           BookPage bookmarkedPagex = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(namex));
                           if (bookmarkedPagex != null) {
                              namex = bookmarkedPagex.name;
                           }

                           if (namex.isEmpty()) {
                              list.add(
                                 Component.translatable(
                                       "%s%s - Page %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(
                                                "%s",
                                                new Object[]{
                                                   bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1
                                                }
                                             )
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           } else {
                              list.add(
                                 Component.translatable(
                                       "%s%s - %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(namex).withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           }

                           this.tooltipText = list;
                           this.tooltipStack = ItemStack.EMPTY;
                           this.drawTooltipText = true;
                        }
                     }

                     float bookY = yInx + 0.8F - this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2.0F * altarTile.buttonScaleRender;
                     BookImage bookImageUnderlay = new BookImage(
                        xInx, bookY, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark
                     );
                     BookImage bookImageOverlay = new BookImage(
                        xInx, bookY, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark
                     );
                     this.drawBookmark(
                        bookImageUnderlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                     this.drawBookmark(
                        bookImageOverlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.LEFT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                  }

                  if (slot.getIndex() >= 10 && slot.getIndex() < 15) {
                     float xInx = -11.25F + slot.getIndex() * 1.15F;
                     float yInx = -0.75F - altarTile.buttonScaleRender - 0.25F;
                     float widthx = 0.86F;
                     if (canInteract(rightCursorX, rightCursorY, xInx, yInx, widthx, widthx, altarTile, drawingType)) {
                        if (!this.bookmarkHovered.contains(slot.getIndex())) {
                           this.bookmarkHovered.add(slot.getIndex());
                        }

                        List<Component> list = new ArrayList<>();
                        if (flag3x) {
                           String namexx = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                           BookPage bookmarkedPagexx = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(namexx));
                           if (bookmarkedPagexx != null) {
                              namexx = bookmarkedPagexx.name;
                           }

                           if (namexx.isEmpty()) {
                              list.add(
                                 Component.translatable(
                                       "%s%s - Page %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(
                                                "%s",
                                                new Object[]{
                                                   bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1
                                                }
                                             )
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           } else {
                              list.add(
                                 Component.translatable(
                                       "%s%s - %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(namexx).withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           }

                           this.tooltipText = list;
                           this.tooltipStack = ItemStack.EMPTY;
                           this.drawTooltipText = true;
                        }
                     }

                     float bookY = yInx + 0.8F - this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2.0F * altarTile.buttonScaleRender;
                     BookImage bookImageUnderlay = new BookImage(
                        xInx, bookY, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark
                     );
                     BookImage bookImageOverlay = new BookImage(
                        xInx, bookY, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark
                     );
                     this.drawBookmark(
                        bookImageUnderlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                     this.drawBookmark(
                        bookImageOverlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        0.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                  }

                  if (slot.getIndex() >= 15) {
                     float xInx = 5.2F + altarTile.buttonScaleRender + 0.15F;
                     float yInx = (slot.getIndex() - 15) * 1.5F;
                     float widthx = 0.86F;
                     if (canInteract(rightCursorX, rightCursorY, xInx, yInx, widthx, widthx, altarTile, drawingType)) {
                        if (!this.bookmarkHovered.contains(slot.getIndex())) {
                           this.bookmarkHovered.add(slot.getIndex());
                        }

                        List<Component> list = new ArrayList<>();
                        if (flag3x) {
                           String namexxx = bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).location;
                           BookPage bookmarkedPagexxx = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(namexxx));
                           if (bookmarkedPagexxx != null) {
                              namexxx = bookmarkedPagexxx.name;
                           }

                           if (namexxx.isEmpty()) {
                              list.add(
                                 Component.translatable(
                                       "%s%s - Page %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(
                                                "%s",
                                                new Object[]{
                                                   bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).pages.get(Math.max(0, bookmark_page)).pageNum - 1
                                                }
                                             )
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           } else {
                              list.add(
                                 Component.translatable(
                                       "%s%s - %s%s",
                                       new Object[]{
                                          Component.translatable("[").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color))),
                                          Component.translatable("%s", new Object[]{bookEntries.chapterList.get(Math.max(0, bookmark_chapter)).name})
                                             .withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable(namexxx).withStyle(Style.EMPTY.withColor(10329495)),
                                          Component.translatable("]").withStyle(Style.EMPTY.withColor(HexereiUtil.getColorValue(bookmark_color)))
                                       }
                                    )
                                    .withStyle(Style.EMPTY.withColor(10329495))
                              );
                           }

                           this.tooltipText = list;
                           this.tooltipStack = ItemStack.EMPTY;
                           this.drawTooltipText = true;
                        }
                     }

                     float bookX = xInx - 0.7F + this.bookmarkHoverAmountRender.get(slot.getIndex()) / 2.0F * altarTile.buttonScaleRender;
                     BookImage bookImageUnderlay = new BookImage(
                        bookX, yInx, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_underlay.png", effectsBookmark
                     );
                     BookImage bookImageOverlay = new BookImage(
                        bookX, yInx, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F, 0.5F, "hexerei:textures/book/bookmark_overlay.png", effectsBookmark
                     );
                     this.drawBookmark(
                        bookImageUnderlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        -90.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                     this.drawBookmark(
                        bookImageOverlay,
                        altarTile,
                        poseStack,
                        bufferSource,
                        -10.0F,
                        -90.0F,
                        light,
                        overlay,
                        PageDrawing.PageOn.RIGHT_PAGE,
                        HexereiUtil.getColorValue(bookmark_color),
                        drawingType,
                        transformType
                     );
                  }

                  if (chapter == bookmark_chapter && (page == bookmark_page || page + 1 == bookmark_page) && !this.bookmarkHovered.contains(slot.getIndex())) {
                     this.bookmarkHovered.add(slot.getIndex());
                  }
               }
            }

            if (altarTile.slotClicked != -1) {
               for (int i = 0; i < 20; i++) {
                  if (i != altarTile.slotClicked) {
                     ArrayList<BookImageEffect> effectsBookmarkx = new ArrayList<>();
                     if (i < 5) {
                        float xInx = -0.3F - altarTile.buttonScaleRender - 0.15F;
                        float yInx = i * 1.5F;
                        float widthx = 0.935F;
                        if (canInteract(leftCursorX, leftCursorY, xInx, yInx, widthx, widthx, altarTile, drawingType)) {
                           effectsBookmarkx.add(new BookImageEffect("scale", 50.0F, 1.15F));
                           effectsBookmarkx.add(new BookImageEffect("tilt", 35.0F, 10.0F));
                        }

                        BookImage bookSelector = new BookImage(
                           xInx,
                           yInx,
                           0.0F,
                           0.0F,
                           0.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           0.5F * altarTile.bookmarkSelectorScale,
                           "hexerei:textures/book/bookmark_selector.png",
                           effectsBookmarkx
                        );
                        this.drawBookmark(
                           bookSelector,
                           altarTile,
                           poseStack,
                           bufferSource,
                           1.0F,
                           90.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.LEFT_PAGE,
                           -1,
                           drawingType,
                           transformType
                        );
                     }

                     if (i >= 5 && i < 10) {
                        float xInx = -5.5F + i * 1.15F;
                        float yInx = -0.75F - altarTile.buttonScaleRender - 0.25F;
                        float widthx = 0.935F;
                        if (canInteract(leftCursorX, leftCursorY, xInx, yInx, widthx, widthx, altarTile, drawingType)) {
                           effectsBookmarkx.add(new BookImageEffect("scale", 50.0F, 1.15F));
                           effectsBookmarkx.add(new BookImageEffect("tilt", 35.0F, 10.0F));
                        }

                        BookImage bookSelector = new BookImage(
                           xInx,
                           yInx,
                           0.0F,
                           0.0F,
                           0.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           0.5F * altarTile.bookmarkSelectorScale,
                           "hexerei:textures/book/bookmark_selector.png",
                           effectsBookmarkx
                        );
                        this.drawBookmark(
                           bookSelector,
                           altarTile,
                           poseStack,
                           bufferSource,
                           1.0F,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.LEFT_PAGE,
                           -1,
                           drawingType,
                           transformType
                        );
                     }

                     if (i >= 10 && i < 15) {
                        float xInx = -11.25F + i * 1.15F;
                        float yInx = -0.75F - altarTile.buttonScaleRender - 0.25F;
                        float widthx = 0.935F;
                        if (canInteract(rightCursorX, rightCursorY, xInx, yInx, widthx, widthx, altarTile, drawingType)) {
                           effectsBookmarkx.add(new BookImageEffect("scale", 50.0F, 1.15F));
                           effectsBookmarkx.add(new BookImageEffect("tilt", 35.0F, 10.0F));
                        }

                        BookImage bookSelector = new BookImage(
                           xInx,
                           yInx,
                           0.0F,
                           0.0F,
                           0.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           0.5F * altarTile.bookmarkSelectorScale,
                           "hexerei:textures/book/bookmark_selector.png",
                           effectsBookmarkx
                        );
                        this.drawBookmark(
                           bookSelector,
                           altarTile,
                           poseStack,
                           bufferSource,
                           1.0F,
                           0.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.RIGHT_PAGE,
                           -1,
                           drawingType,
                           transformType
                        );
                     }

                     if (i >= 15) {
                        float xInx = 5.2F + altarTile.buttonScaleRender + 0.15F;
                        float yInx = (i - 15) * 1.5F;
                        float widthx = 0.935F;
                        if (canInteract(rightCursorX, rightCursorY, xInx, yInx, widthx, widthx, altarTile, drawingType)) {
                           effectsBookmarkx.add(new BookImageEffect("scale", 50.0F, 1.15F));
                           effectsBookmarkx.add(new BookImageEffect("tilt", 35.0F, 10.0F));
                        }

                        BookImage bookSelector = new BookImage(
                           xInx,
                           yInx,
                           0.0F,
                           0.0F,
                           0.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           64.0F,
                           0.5F * altarTile.bookmarkSelectorScale,
                           "hexerei:textures/book/bookmark_selector.png",
                           effectsBookmarkx
                        );
                        this.drawBookmark(
                           bookSelector,
                           altarTile,
                           poseStack,
                           bufferSource,
                           1.0F,
                           -90.0F,
                           light,
                           overlay,
                           PageDrawing.PageOn.RIGHT_PAGE,
                           -1,
                           drawingType,
                           transformType
                        );
                     }
                  }
               }
            }
         }

         if (drawingType != PageDrawing.DrawingType.GUI) {
            ArrayList<BookImageEffect> effectsx = new ArrayList<>();
            BookImageEffect bookImageEffect_scalex = new BookImageEffect("scale", 50.0F, 1.15F);
            BookImageEffect bookImageEffect_tiltx = new BookImageEffect("tilt", 35.0F, 10.0F);
            String loc = "hexerei:textures/book/font_button.png";
            if (drawBack) {
               loc = "hexerei:textures/book/back_page.png";
            }

            float x = -0.45F;
            float y = 7.2F;
            float widthx = 0.86F;
            if (canInteract(leftCursorX, leftCursorY, x, y, widthx, widthx, altarTile, drawingType)) {
               effectsx.add(bookImageEffect_scalex);
               effectsx.add(bookImageEffect_tiltx);
               List<Component> list = new ArrayList<>();
               if (drawBack) {
                  list.add(Component.translatable("Back").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                  loc = "hexerei:textures/book/back_page_hover.png";
               } else {
                  list.add(Component.translatable("Change Font").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                  loc = "hexerei:textures/book/font_button_hover.png";
               }

               this.tooltipText = list;
               this.tooltipStack = ItemStack.EMPTY;
               this.drawTooltipText = true;
            }

            BookImage bookImage = new BookImage(-0.5F, 7.25F, 0.0F, 0.0F, 0.0F, 32.0F, 32.0F, 32.0F, 32.0F, altarTile.buttonScaleRender / 2.0F, loc, effectsx);
            this.drawImage(
               bookImage, altarTile, leftCursorX, leftCursorY, poseStack, bufferSource, 0.0F, light, overlay, PageDrawing.PageOn.LEFT_PAGE, drawingType
            );
         }

         if (drawingType != PageDrawing.DrawingType.GUI) {
            ArrayList<BookImageEffect> effectsxx = new ArrayList<>();
            BookImageEffect bookImageEffect_scalexx = new BookImageEffect("scale", 50.0F, 1.15F);
            BookImageEffect bookImageEffect_tiltxx = new BookImageEffect("tilt", 35.0F, 10.0F);
            String loc_close = "hexerei:textures/book/close.png";
            String loc_del = "hexerei:textures/book/delete.png";
            float widthx = 0.86F;
            if (canInteract(rightCursorX, rightCursorY, -0.25F - widthx / 2.0F, 7.5F - widthx / 2.0F, widthx, widthx, altarTile, drawingType)) {
               effectsxx.add(bookImageEffect_scalexx);
               effectsxx.add(bookImageEffect_tiltxx);
               if (altarTile.slotClicked != -1 && altarTile.slotClickedTick > 5) {
                  loc_del = "hexerei:textures/book/delete_hover.png";
                  List<Component> list = new ArrayList<>();
                  list.add(Component.translatable("Delete Bookmark").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                  this.tooltipText = list;
               } else {
                  loc_close = "hexerei:textures/book/close_hover.png";
                  List<Component> list = new ArrayList<>();
                  list.add(Component.translatable("Close Book").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                  this.tooltipText = list;
               }

               this.drawTooltipText = true;
               this.tooltipStack = ItemStack.EMPTY;
            }

            BookImage bookImage;
            if (altarTile.slotClicked != -1 && altarTile.slotClickedTick > 5) {
               bookImage = new BookImage(0.0F, 0.0F, 35.0F, 0.0F, 0.0F, 32.0F, 32.0F, 32.0F, 32.0F, altarTile.bookmarkSelectorScale / 1.5F, loc_del, effectsxx);
            } else {
               bookImage = new BookImage(0.0F, 0.0F, 35.0F, 0.0F, 0.0F, 32.0F, 32.0F, 32.0F, 32.0F, altarTile.buttonScaleRender / 2.0F, loc_close, effectsxx);
            }

            this.drawImage(
               bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0.0F, light, overlay, PageDrawing.PageOn.MIDDLE_BUTTON, drawingType
            );
            effectsxx = new ArrayList<>();
            bookImageEffect_scalexx = new BookImageEffect("scale", 50.0F, 1.15F);
            bookImageEffect_tiltxx = new BookImageEffect("tilt", 35.0F, 10.0F);
            String locx = "hexerei:textures/book/home.png";
            if (canInteract(rightCursorX, rightCursorY, -0.25F - widthx / 2.0F, -0.5F - widthx / 2.0F, widthx, widthx, altarTile, drawingType)) {
               effectsxx.add(bookImageEffect_scalexx);
               effectsxx.add(bookImageEffect_tiltxx);
               locx = "hexerei:textures/book/home_hover.png";
               List<Component> list = new ArrayList<>();
               list.add(Component.translatable("Home").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
               this.tooltipText = list;
               this.drawTooltipText = true;
               this.tooltipStack = ItemStack.EMPTY;
            }

            bookImage = new BookImage(0.0F, -8.1F, 0.0F, 0.0F, 0.0F, 32.0F, 32.0F, 32.0F, 32.0F, altarTile.buttonScaleRender / 2.0F, locx, effectsxx);
            this.drawImage(
               bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0.0F, light, overlay, PageDrawing.PageOn.MIDDLE_BUTTON, drawingType
            );
            if (drawNext) {
               effectsxx = new ArrayList<>();
               bookImageEffect_scalexx = new BookImageEffect("scale", 50.0F, 1.15F);
               bookImageEffect_tiltxx = new BookImageEffect("tilt", 35.0F, 10.0F);
               locx = "hexerei:textures/book/next_page.png";
               if (canInteract(rightCursorX, rightCursorY, 5.415F, 7.2F, 0.86F, 0.86F, altarTile, drawingType)) {
                  effectsxx.add(bookImageEffect_scalexx);
                  effectsxx.add(bookImageEffect_tiltxx);
                  locx = "hexerei:textures/book/next_page_hover.png";
                  List<Component> list = new ArrayList<>();
                  list.add(Component.translatable("Next").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                  this.tooltipText = list;
                  this.drawTooltipText = true;
                  this.tooltipStack = ItemStack.EMPTY;
               }

               bookImage = new BookImage(5.5F, 7.25F, 0.0F, 0.0F, 0.0F, 32.0F, 32.0F, 32.0F, 32.0F, altarTile.buttonScaleRender / 2.0F, locx, effectsxx);
               this.drawImage(
                  bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0.0F, light, overlay, PageDrawing.PageOn.RIGHT_PAGE, drawingType
               );
            }

            if (drawingType != PageDrawing.DrawingType.SCREEN) {
               effectsxx = new ArrayList<>();
               bookImageEffect_scalexx = new BookImageEffect("scale", 50.0F, 1.15F);
               bookImageEffect_tiltxx = new BookImageEffect("tilt", 35.0F, 10.0F);
               locx = "hexerei:textures/book/open_gui.png";
               if (Minecraft.getInstance().screen == null && canInteract(5.49F, -0.97F, 0.86F, 0.86F, altarTile, PageDrawing.PageOn.RIGHT_PAGE)) {
                  effectsxx.add(bookImageEffect_scalexx);
                  effectsxx.add(bookImageEffect_tiltxx);
                  locx = "hexerei:textures/book/open_gui_hover.png";
                  List<Component> list = new ArrayList<>();
                  list.add(Component.translatable("Open in GUI").withStyle(Style.EMPTY.withItalic(true).withColor(10329495)));
                  this.tooltipText = list;
                  this.drawTooltipText = true;
                  this.tooltipStack = ItemStack.EMPTY;
               }

               bookImage = new BookImage(5.5F, -1.0F, 0.0F, 0.0F, 0.0F, 32.0F, 32.0F, 32.0F, 32.0F, altarTile.buttonScaleRender / 2.0F, locx, effectsxx);
               this.drawImage(
                  bookImage, altarTile, rightCursorX, rightCursorY, poseStack, bufferSource, 0.0F, light, overlay, PageDrawing.PageOn.RIGHT_PAGE, drawingType
               );
            }
         }
      }
   }

   private float moveTo(float input, float moveTo, float speed) {
      float distance = moveTo - input;
      if (Math.abs(distance) <= speed) {
         return moveTo;
      } else {
         if (distance > 0.0F) {
            input += speed;
         } else {
            input -= speed;
         }

         return input;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawItemInSlot(
      BookOfShadowsAltarTile altarTile,
      BookItemsAndFluids bookItemStackInSlot,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float xIn,
      float yIn,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      if (!bookItemStackInSlot.type.equals("item") && !bookItemStackInSlot.type.equals("tag")) {
         if (bookItemStackInSlot.type.equals("fluid")) {
            this.drawFluidInSlot(altarTile, bookItemStackInSlot, poseStack, bufferSource, xIn, yIn, 0.0F, light, overlay, pageOn, drawingType);
         }
      } else {
         if (bookItemStackInSlot.show_slot) {
            this.drawSlot(altarTile, poseStack, bufferSource, xIn, yIn, 0.0F, light, overlay, pageOn, drawingType);
         }

         renderItem(altarTile, bookItemStackInSlot, poseStack, bufferSource, xIn, yIn, 0.0F, light, overlay, pageOn, drawingType);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawBlock(
      BookOfShadowsAltarTile altarTile,
      BookBlocks bookItemStackInSlot,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float xIn,
      float yIn,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      if (bookItemStackInSlot.type.equals("block") || bookItemStackInSlot.type.equals("tag")) {
         if (bookItemStackInSlot.show_slot) {
            this.drawSlot(altarTile, poseStack, bufferSource, xIn, yIn, 0.0F, light, overlay, pageOn, drawingType);
         }

         renderBlock(altarTile, bookItemStackInSlot, poseStack, bufferSource, xIn, yIn, 0.0F, light, overlay, pageOn, drawingType);
      }
   }

   public static Vec3 getPointOnPlane(float x, float y, float xscale, float yscale, BookOfShadowsAltarTile altarTile, PageDrawing.PageOn pageOn) {
      Vector3f leftOffset = new Vector3f(0.375F, 0.532F, -0.03F);
      Vector3f rightOffset = new Vector3f(-0.012F, 0.532F, -0.03F);
      return getPointOnPlane(leftOffset, rightOffset, x, y, xscale, yscale, altarTile, pageOn);
   }

   public static Vec3 getPointOnPlane(
      Vector3f leftOffset, Vector3f rightOffset, float x, float y, float xscale, float yscale, BookOfShadowsAltarTile altarTile, PageDrawing.PageOn pageOn
   ) {
      Vector3f offset = pageOn == PageDrawing.PageOn.RIGHT_PAGE ? rightOffset : leftOffset;
      BlockPos blockPos = altarTile.getBlockPos();
      Vec3 pointBase = new Vec3(
         blockPos.getX() + 0.5F + (float)Math.sin(altarTile.degreesSpun / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
         blockPos.getY() + 1.125F + (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F,
         blockPos.getZ() + 0.5F + (float)Math.cos(altarTile.degreesSpun / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
      );
      Vector3f vector3f = new Vector3f(offset.x, offset.y, offset.z).add(x * -xscale, y * -yscale, 0.03F);
      vector3f.rotate(com.mojang.math.Axis.YP.rotationDegrees((pageOn == PageDrawing.PageOn.RIGHT_PAGE ? -1 : 1) * (10.0F + altarTile.degreesOpened / 1.12F)));
      vector3f.add(0.0F, 0.0F, -0.03F);
      vector3f.rotate(com.mojang.math.Axis.XP.rotationDegrees(45.0F - altarTile.degreesOpened / 2.0F));
      vector3f.rotate(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpun));
      return pointBase.add(vector3f.x, vector3f.y, vector3f.z);
   }

   public void tick() {
      isClickedOld = isClicked;
      this.bookmarkHoverAmountOld = new ArrayList<>(this.bookmarkHoverAmount);

      for (int i = 0; i < this.bookmarkHoverAmount.size(); i++) {
         int finalI = i;
         if (!this.bookmarkHovered.stream().filter(f -> f == finalI).toList().isEmpty()) {
            this.bookmarkHoverAmount.set(i, this.moveTo(this.bookmarkHoverAmount.get(i), 1.0F, 0.1F));
         } else {
            this.bookmarkHoverAmount.set(i, this.moveTo(this.bookmarkHoverAmount.get(i), 0.0F, 0.05F));
         }
      }

      this.bookmarkHovered = new ArrayList<>();
      this.drawTooltipScaleOld = this.drawTooltipScale;
      if (this.drawTooltipStack && this.drawTooltip) {
         this.drawTooltipStackFlag = true;
         this.drawTooltipTextFlag = false;
         this.drawTooltipScale = this.moveTo(this.drawTooltipScale, 1.0F, 0.1F);
      } else if (this.drawTooltipText && this.drawTooltip) {
         this.drawTooltipTextFlag = true;
         this.drawTooltipStackFlag = false;
         this.drawTooltipScale = this.moveTo(this.drawTooltipScale, 1.0F, 0.1F);
      } else {
         this.drawTooltipScale = this.moveTo(this.drawTooltipScale, 0.0F, 0.2F);
         if (this.drawTooltipScale == 0.0F) {
            this.drawTooltipStackFlag = false;
            this.drawTooltipTextFlag = false;
         }
      }

      boolean debugDraw = false;
      if (this.altarTile.currentBook != null
         && BookManager.getBookEntries(this.altarTile.currentBook.getBook()) != null
         && this.altarTile.openedPercent != 1.0F
         && debugDraw) {
         BookEntries bookEntries = BookManager.getBookEntries(this.altarTile.currentBook.getBook());
         boolean drawEdges = true;
         if (drawEdges) {
            int count = 15;

            for (int ix = 0; ix < count + 1; ix++) {
               float xIn = -0.5F + ix * 6.5F / count;
               float yIn = -1.0F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 0; ix < count + 1; ix++) {
               float xIn = -0.5F + ix * 6.5F / count;
               float yIn = 8.0F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 0; ix < count + 1; ix++) {
               float xIn = -0.5F;
               float yIn = (float)(ix * 9) / count - 1.0F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 0; ix < count + 1; ix++) {
               float xIn = -0.15F + ix * 6.5F / count;
               float yIn = -1.0F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 0; ix < count + 1; ix++) {
               float xIn = -0.15F + ix * 6.5F / count;
               float yIn = 8.0F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 0; ix < count + 1; ix++) {
               float xIn = 6.35F;
               float yIn = (float)(ix * 9) / count - 1.0F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            }
         }

         boolean drawBookmarks = false;
         if (drawBookmarks) {
            for (int ix = 0; ix < 5; ix++) {
               float xIn = -0.3F - this.altarTile.buttonScaleRender - 0.15F;
               float yIn = ix * 1.5F;
               float width = 0.935F;
               float height = 0.935F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
               Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 5; ix < 10; ix++) {
               float xIn = -5.5F + ix * 1.15F;
               float yIn = -0.75F - this.altarTile.buttonScaleRender - 0.25F;
               float width = 0.935F;
               float height = 0.935F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
               Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 10; ix < 15; ix++) {
               float xIn = -11.25F + ix * 1.15F;
               float yIn = -0.75F - this.altarTile.buttonScaleRender - 0.25F;
               float width = 0.935F;
               float height = 0.935F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
               Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
            }

            for (int ix = 15; ix < 20; ix++) {
               float xIn = 5.2F + this.altarTile.buttonScaleRender + 0.15F;
               float yIn = (ix - 15) * 1.5F;
               float width = 0.935F;
               float height = 0.935F;
               float xscale = 0.062F;
               float yscale = 0.062F;
               Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
               Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
               this.altarTile
                  .getLevel()
                  .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
            }
         }

         boolean drawCorners = false;
         if (drawCorners) {
            float xIn = 5.415F;
            float yIn = 7.2F;
            float width = 0.86F;
            float height = 0.86F;
            float xscale = 0.062F;
            float yscale = 0.062F;
            Vec3 planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
            Vec3 planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
            this.altarTile.getLevel().addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            this.altarTile
               .getLevel()
               .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
            xIn = 5.415F;
            yIn = -0.97F;
            width = 0.86F;
            height = 0.86F;
            xscale = 0.062F;
            yscale = 0.062F;
            planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
            planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.RIGHT_PAGE);
            this.altarTile.getLevel().addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            this.altarTile
               .getLevel()
               .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
            xIn = -0.45F;
            yIn = -0.96F;
            width = 0.86F;
            height = 0.86F;
            xscale = 0.062F;
            yscale = 0.062F;
            planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
            planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
            this.altarTile.getLevel().addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            this.altarTile
               .getLevel()
               .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
            xIn = -0.45F;
            yIn = 7.2F;
            width = 0.86F;
            height = 0.86F;
            xscale = 0.062F;
            yscale = 0.062F;
            planePoint = getPointOnPlane(xIn, yIn, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
            planePoint2 = getPointOnPlane(xIn + width, yIn + height, xscale, yscale, this.altarTile, PageDrawing.PageOn.LEFT_PAGE);
            this.altarTile.getLevel().addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 0.0, 0.0, 0.0);
            this.altarTile
               .getLevel()
               .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
         }

         String location1 = "";
         String location2 = "";
         int chapter = this.altarTile.currentBook.getChapter();
         int page = this.altarTile.currentBook.getPage();
         if (page % 2 == 1) {
            page--;
         }

         if (bookEntries.chapterList.get(chapter).pages.size() > page && page >= 0) {
            location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
         }

         if (bookEntries.chapterList.get(chapter).pages.size() > page + 1 && page >= 0) {
            location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;
         }

         BookPage page1 = BookManager.getBookPages(this.altarTile.currentBook.getBook(), ResourceLocation.parse(location1));
         BookPage page2 = BookManager.getBookPages(this.altarTile.currentBook.getBook(), ResourceLocation.parse(location2));
         MutableComponent component = Component.literal("");

         for (PageDrawing.PageOn pageOn : List.of(PageDrawing.PageOn.LEFT_PAGE, PageDrawing.PageOn.RIGHT_PAGE)) {
            BookPage pageUsed = pageOn == PageDrawing.PageOn.LEFT_PAGE ? page1 : page2;
            if (pageUsed != null) {
               for (BookEntity entity : pageUsed.entityList) {
                  float xIn = entity.x + entity.offset.x + 0.52F;
                  float yIn = entity.y + entity.offset.y;
                  float xscale = 0.062F;
                  float width = 1.25F + entity.scale / 5.0F;
                  Vec3 planePoint2 = getPointOnPlane(xIn - width / 2.0F, yIn - width / 2.0F, xscale, xscale, this.altarTile, pageOn);
                  Vec3 planePoint3 = getPointOnPlane(xIn + width / 2.0F, yIn + width / 2.0F, xscale, xscale, this.altarTile, pageOn);
                  this.altarTile
                     .getLevel()
                     .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint2.x, planePoint2.y, planePoint2.z, 0.0, 0.0, 0.0);
                  this.altarTile
                     .getLevel()
                     .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint3.x, planePoint3.y, planePoint3.z, 0.0, 0.0, 0.0);
               }

               Vec2 ip = getIntersectPoint(Hexerei.proxy.getPlayer().getLookAngle(), Hexerei.proxy.getPlayer().getEyePosition(), this.altarTile, pageOn);
               if (ip != null) {
                  if (!component.getString().isEmpty()) {
                     component.append(Component.literal("    -    "));
                  }

                  component.append(Component.literal(pageOn + String.format(": %.3f,  %.3f", ip.x, ip.y)));
                  Vec3 planePoint = getPointOnPlane(ip.x, ip.y, 0.062F, 0.062F, this.altarTile, pageOn);
                  this.altarTile
                     .getLevel()
                     .addParticle((ParticleOptions)ModParticleTypes.BOOK_TEST.get(), planePoint.x, planePoint.y, planePoint.z, 2.0, 0.0, 0.0);
               }
            }
         }

         if (!component.getString().isEmpty()
            && Math.sqrt(this.altarTile.getBlockPos().distToCenterSqr(Hexerei.proxy.getPlayer().getEyePosition()))
               < Hexerei.proxy.getPlayer().getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)) {
            Hexerei.proxy.getPlayer().displayClientMessage(component, true);
         }
      }
   }

   public static String getModNameForModId(String modId) {
      return HexereiUtil.getModNameForModId(modId);
   }

   public static List<BlockPos> getAltars(Player playerIn) {
      double reach = playerIn.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
      List<BlockPos> altars = new ArrayList<>();
      float f = playerIn.getXRot();
      float f1 = playerIn.getYRot();
      Vec3 vec3 = playerIn.getEyePosition();
      Vec3 vec31 = new Vec3(0.0, 0.0, 0.25);
      float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
      float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
      float f4 = -Mth.cos(-f * 0.017453292F);
      float f5 = Mth.sin(-f * 0.017453292F);
      float f6 = f3 * f4;
      float f7 = f2 * f4;

      for (float section = 0.0F; section <= reach; section += 0.25F) {
         BlockPos pos = BlockPos.containing(vec3.add((double)f6 * section, (double)f5 * section - 1.0, (double)f7 * section));
         BlockPos pos2 = BlockPos.containing(
            vec3.add((double)f6 * section, (double)f5 * section - 1.0, (double)f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(90.0)))
         );
         BlockPos pos3 = BlockPos.containing(
            vec3.add((double)f6 * section, (double)f5 * section - 1.0, (double)f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(-90.0)))
         );
         if (!altars.contains(pos)) {
            altars.add(pos);
         }

         if (!altars.contains(pos2)) {
            altars.add(pos2);
         }

         if (!altars.contains(pos3)) {
            altars.add(pos3);
         }

         if (section > reach) {
            section = (float)reach;
            pos = BlockPos.containing(vec3.add((double)f6 * section, (double)f5 * section - 1.0, (double)f7 * section));
            pos2 = BlockPos.containing(
               vec3.add((double)f6 * section, (double)f5 * section - 1.0, (double)f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(90.0)))
            );
            pos3 = BlockPos.containing(
               vec3.add((double)f6 * section, (double)f5 * section - 1.0, (double)f7 * section).add(vec31.yRot(f1).yRot((float)Math.toRadians(-90.0)))
            );
            if (!altars.contains(pos)) {
               altars.add(pos);
            }

            if (!altars.contains(pos2)) {
               altars.add(pos2);
            }

            if (!altars.contains(pos3)) {
               altars.add(pos3);
            }
            break;
         }
      }

      return altars;
   }

   public static Vec3 calculatePlaneNormal(Vec3 originPointOnPlane, Vec3 anotherPointOnPlane, Vec3 thirdPointOnPlane) {
      Vec3 V1 = anotherPointOnPlane.subtract(originPointOnPlane);
      Vec3 V2 = thirdPointOnPlane.subtract(originPointOnPlane);
      return V1.cross(V2).normalize();
   }

   public static Vec2 getLookingAtPointOnPlane(Vec3 originPointOnPlane, Vec3 anotherPointOnPlane, Vec3 thirdPointOnPlane, Vec3 rayStart, Vec3 rayDirection) {
      Vec3 planeNormal = calculatePlaneNormal(originPointOnPlane, anotherPointOnPlane, thirdPointOnPlane);
      Vec3 u = anotherPointOnPlane.subtract(originPointOnPlane).normalize();
      Vec3 v = planeNormal.cross(u).normalize();
      double t = planeNormal.dot(originPointOnPlane.subtract(rayStart)) / planeNormal.dot(rayDirection);
      if (t < 0.0) {
         return null;
      } else {
         Vec3 intersection = rayStart.add(rayDirection.scale(t));
         Vec3 planeToPoint = intersection.subtract(originPointOnPlane);
         double x = planeToPoint.dot(u);
         double y = planeToPoint.dot(v);
         return new Vec2((float)x, (float)y);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean canInteract(float x, float y, float width, float height, BookOfShadowsAltarTile altarTile, PageDrawing.PageOn pageOn) {
      Player player = Minecraft.getInstance().player;
      Vec2 ip = getIntersectPoint(player.getLookAngle(), player.getEyePosition(), altarTile, pageOn);
      return ip != null
         && ip.x >= x
         && ip.x <= x + width
         && ip.y >= y
         && ip.y <= y + height
         && Math.sqrt(altarTile.getBlockPos().distToCenterSqr(player.getEyePosition())) <= player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean canInteract(
      float xCursor, float yCursor, float x, float y, float width, float height, BookOfShadowsAltarTile altarTile, PageDrawing.DrawingType drawingType
   ) {
      Player player = Minecraft.getInstance().player;
      return xCursor >= x
         && xCursor <= x + width
         && yCursor >= y
         && yCursor <= y + height
         && (
            drawingType == PageDrawing.DrawingType.SCREEN
               || Math.sqrt(altarTile.getBlockPos().distToCenterSqr(player.getEyePosition())) <= player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)
         );
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean canInteract(
      Vector3f leftOffset,
      Vector3f rightOffset,
      float x,
      float y,
      float width,
      float height,
      Player player,
      BookOfShadowsAltarTile altarTile,
      PageDrawing.PageOn pageOn
   ) {
      Vec2 ip = getIntersectPoint(leftOffset, rightOffset, player.getLookAngle(), player.getEyePosition(), altarTile, pageOn);
      return ip != null
         && ip.x >= x
         && ip.x <= x + width
         && ip.y >= y
         && ip.y <= y + height
         && Math.sqrt(altarTile.getBlockPos().distToCenterSqr(player.getEyePosition())) <= player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
   }

   @OnlyIn(Dist.CLIENT)
   public static Vec2 getIntersectPoint(Vec3 rayVector, Vec3 rayPoint, BookOfShadowsAltarTile altarTile, PageDrawing.PageOn pageOn) {
      Vector3f rightOffset = new Vector3f(-0.012F, 0.532F, -0.03F);
      Vector3f leftOffset = new Vector3f(0.375F, 0.532F, -0.03F);
      return getIntersectPoint(leftOffset, rightOffset, rayVector, rayPoint, altarTile, pageOn);
   }

   @OnlyIn(Dist.CLIENT)
   public static Vec2 getIntersectPoint(
      Vector3f leftOffset, Vector3f rightOffset, Vec3 rayVector, Vec3 rayPoint, BookOfShadowsAltarTile altarTile, PageDrawing.PageOn pageOn
   ) {
      if (pageOn != PageDrawing.PageOn.LEFT_PAGE && pageOn != PageDrawing.PageOn.RIGHT_PAGE && pageOn != PageDrawing.PageOn.MIDDLE_BUTTON) {
         return null;
      } else {
         float scale = 0.062F;
         Vector3f middleOffset = new Vector3f(0.0F, 0.5F, -0.03F);
         Vector3f offset = pageOn == PageDrawing.PageOn.RIGHT_PAGE ? rightOffset : (pageOn == PageDrawing.PageOn.LEFT_PAGE ? leftOffset : middleOffset);
         BlockPos blockPos = altarTile.getBlockPos();
         Vec3 pointBase = new Vec3(
            blockPos.getX() + 0.5F + (float)Math.sin(altarTile.degreesSpun / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F),
            blockPos.getY() + 1.125F + (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F,
            blockPos.getZ() + 0.5F + (float)Math.cos(altarTile.degreesSpun / 57.3F) / 32.0F * (altarTile.degreesOpened / 5.0F - 12.0F)
         );
         Vector3f vector3f = new Vector3f(offset.x, offset.y, offset.z).add(0.0F * -scale, 0.0F * -scale, 0.03F);
         if (pageOn != PageDrawing.PageOn.MIDDLE_BUTTON) {
            vector3f.rotate(
               com.mojang.math.Axis.YP.rotationDegrees((pageOn == PageDrawing.PageOn.RIGHT_PAGE ? -1 : 1) * (10.0F + altarTile.degreesOpened / 1.12F))
            );
         }

         vector3f.add(0.0F, 0.0F, -0.03F);
         vector3f.rotate(com.mojang.math.Axis.XP.rotationDegrees(45.0F - altarTile.degreesOpened / 2.0F));
         vector3f.rotate(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpun));
         Vec3 planePoint = pointBase.add(vector3f.x, vector3f.y, vector3f.z);
         vector3f = new Vector3f(offset.x, offset.y, offset.z).add(5.0F * -scale, 0.0F * -scale, 0.0F);
         if (pageOn != PageDrawing.PageOn.MIDDLE_BUTTON) {
            vector3f.rotate(
               com.mojang.math.Axis.YP.rotationDegrees((pageOn == PageDrawing.PageOn.RIGHT_PAGE ? -1 : 1) * (10.0F + altarTile.degreesOpened / 1.12F))
            );
         }

         vector3f.rotate(com.mojang.math.Axis.XP.rotationDegrees(45.0F - altarTile.degreesOpened / 2.0F));
         vector3f.rotate(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpun));
         Vec3 planePoint2 = pointBase.add(vector3f.x, vector3f.y, vector3f.z);
         vector3f = new Vector3f(offset.x, offset.y, offset.z).add(0.0F * -scale, 5.0F * -scale, 0.0F);
         if (pageOn != PageDrawing.PageOn.MIDDLE_BUTTON) {
            vector3f.rotate(
               com.mojang.math.Axis.YP.rotationDegrees((pageOn == PageDrawing.PageOn.RIGHT_PAGE ? -1 : 1) * (10.0F + altarTile.degreesOpened / 1.12F))
            );
         }

         vector3f.rotate(com.mojang.math.Axis.XP.rotationDegrees(45.0F - altarTile.degreesOpened / 2.0F));
         vector3f.rotate(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpun));
         Vec3 planePoint3 = pointBase.add(vector3f.x, vector3f.y, vector3f.z);
         Vec2 val = getLookingAtPointOnPlane(planePoint, planePoint2, planePoint3, rayPoint, rayVector);
         return val != null ? val.scale(1.0F / scale) : null;
      }
   }

   public boolean interactClick(
      BookOfShadowsAltarTile altarTile,
      Player playerIn,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PageDrawing.DrawingType drawingType
   ) {
      if (altarTile.turnPage == 0) {
         if (altarTile.slotClicked != -1 && ++altarTile.slotClickedTick > 0) {
            playerIn.swinging = false;
         }

         BookData bookData = altarTile.currentBook;
         if (bookData != null && bookData.isOpened() && checkClick(bookData, altarTile, leftCursorX, leftCursorY, rightCursorX, rightCursorY, drawingType)) {
            isClickedOld = true;
            return true;
         }
      }

      return false;
   }

   public boolean releaseClick(
      BookOfShadowsAltarTile altarTile,
      Player playerIn,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PageDrawing.DrawingType drawingType
   ) {
      if (focusedWritableTextBox != null) {
         ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.clicked = false;
      }

      if (altarTile.turnPage == 0) {
         BookData bookData = altarTile.currentBook;
         if (bookData != null) {
            BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
            if (bookEntries != null) {
               int chapterNum = bookData.getChapter();
               int pageNum = bookData.getPage();
               if (pageNum % 2 == 1) {
                  pageNum--;
               }

               String location1 = "";
               if (bookEntries.chapterList.get(chapterNum).pages.size() > pageNum && pageNum >= 0) {
                  location1 = bookEntries.chapterList.get(chapterNum).pages.get(pageNum).location;
               }

               BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location1));

               for (BookPageEntry bookPageEntry : bookEntries.chapterList.stream().flatMap(entry -> entry.pages.stream()).toList()) {
                  BookPage page = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(bookPageEntry.location));
                  if (page != null) {
                     boolean flag = page == page1;

                     for (BookPaintElement paintElement : page.paintElements) {
                        PaintSystem paintSystem = paintElement.client.getPaintSystem(bookData.getUUID());
                        if (paintElement.client != null) {
                           float w = paintElement.width / 326.0F * 2.55F * paintElement.scale / 0.062F;
                           float h = paintElement.height / 326.0F * 2.55F * paintElement.scale / 0.062F;
                           float x = paintElement.x + 0.025F;
                           float y = paintElement.y - 0.5F;
                           int xPixel = (int)(((flag ? leftCursorX : rightCursorX) - x + (flag ? 0.0F : 0.2F)) / w * paintElement.width);
                           int yPixel = (int)(((flag ? leftCursorY : rightCursorY) - y) / h * paintElement.height);
                           paintSystem.released(xPixel, yPixel);
                           paintSystem.getValueSliders().release();
                        }
                     }
                  }
               }

               label119:
               if (altarTile.slotClicked != -1) {
                  float width = 0.86F;
                  int bookmark_chapter = 0;
                  int bookmark_page = 0;
                  BookData.Bookmarks bookmarks = bookData.getBookmarks();
                  float x = -0.25F;
                  float y = 7.5F;
                  if (canInteract(rightCursorX, rightCursorY, x - width / 2.0F, y - width / 2.0F, width, width, altarTile, drawingType)) {
                     altarTile.deleteBookmark(altarTile.slotClicked);
                     altarTile.slotClicked = -1;
                     altarTile.slotClickedTick = 0;
                     return true;
                  }

                  boolean flag2 = false;

                  for (BookData.Bookmarks.Slot slot : bookmarks.getSlots()) {
                     ResourceLocation bookmark_id = ResourceLocation.parse(slot.getId());
                     if (slot.getIndex() < 5) {
                        x = -0.3F - altarTile.buttonScaleRender - 0.15F;
                        y = slot.getIndex() * 1.5F;
                        width = 0.935F;
                        if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {
                           flag2 = true;
                        }
                     }

                     if (slot.getIndex() >= 5 && slot.getIndex() < 10) {
                        x = -5.5F + slot.getIndex() * 1.15F;
                        y = -0.75F - altarTile.buttonScaleRender - 0.25F;
                        width = 0.935F;
                        if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {
                           flag2 = true;
                        }
                     }

                     if (slot.getIndex() >= 10 && slot.getIndex() < 15) {
                        x = -11.25F + slot.getIndex() * 1.15F;
                        y = -0.75F - altarTile.buttonScaleRender - 0.25F;
                        width = 0.935F;
                        if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType)) {
                           flag2 = true;
                        }
                     }

                     if (slot.getIndex() >= 15) {
                        x = 5.2F + altarTile.buttonScaleRender + 0.15F;
                        y = (slot.getIndex() - 15) * 1.5F;
                        width = 0.935F;
                        if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType)) {
                           flag2 = true;
                        }
                     }

                     if (flag2) {
                        if (altarTile.slotClicked != slot.getIndex()) {
                           altarTile.swapBookmarks(altarTile.slotClicked, slot.getIndex());
                           altarTile.drawing.bookmarkHoverAmount.set(slot.getIndex(), 0.0F);
                           altarTile.drawing.bookmarkHoverAmount.set(altarTile.slotClicked, 0.0F);
                           altarTile.slotClicked = -1;
                           altarTile.slotClickedTick = 0;
                           return true;
                        }

                        if (altarTile.slotClickedTick < 20) {
                           for (BookChapter chapter : bookEntries.chapterList) {
                              for (BookPageEntry pageEntry : chapter.pages) {
                                 if (ResourceLocation.parse(pageEntry.location).equals(bookmark_id)) {
                                    altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                                    altarTile.slotClicked = -1;
                                    altarTile.slotClickedTick = 0;
                                    return true;
                                 }
                              }
                           }

                           altarTile.setTurnPage(-1, bookmark_chapter, bookmark_page);
                           altarTile.slotClicked = -1;
                           altarTile.slotClickedTick = 0;
                           return true;
                        }
                        break label119;
                     }
                  }
               }
            }
         }
      }

      altarTile.slotClicked = -1;
      altarTile.slotClickedTick = 0;
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean checkClick(
      BookData bookData,
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PageDrawing.DrawingType drawingType
   ) {
      BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
      if (bookEntries == null) {
         return false;
      } else {
         if (!isClicked) {
            float width = 0.86F;
            float x = -0.45F;
            float y = -0.96F;
            if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType) && altarTile.currentBook.getChapter() != 0) {
               altarTile.clickPageBookmark(altarTile.currentBook.getChapter(), altarTile.currentBook.getPage());
               return true;
            }

            x = -0.45F;
            y = 7.2F;
            if (canInteract(leftCursorX, leftCursorY, x, y, width, width, altarTile, drawingType)) {
               if (altarTile.slotClicked == -1 && PageDrawingEvents.clickedBack(altarTile)) {
                  altarTile.setTurnPage(2);
                  return true;
               }

               if (altarTile.slotClicked == -1) {
                  ClientProxy.fontIndex++;
                  return true;
               }
            }

            x = -0.25F;
            y = -0.5F;
            if (canInteract(rightCursorX, rightCursorY, x - width / 2.0F, y - width / 2.0F, width, width, altarTile, drawingType)) {
               altarTile.setTurnPage(-1, 0, 0);
               return true;
            }

            x = -0.25F;
            y = 7.5F;
            if (canInteract(rightCursorX, rightCursorY, x - width / 2.0F, y - width / 2.0F, width, width, altarTile, drawingType)) {
               altarTile.setTurnPage(-2);
               return true;
            }

            x = 5.49F;
            y = -0.97F;
            if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType) && Minecraft.getInstance().screen == null) {
               Minecraft.getInstance().setScreen(new BookOfShadowsScreen(altarTile));
               return true;
            }

            x = 5.415F;
            y = 7.2F;
            if (canInteract(rightCursorX, rightCursorY, x, y, width, width, altarTile, drawingType)
               && altarTile.slotClicked == -1
               && PageDrawingEvents.clickedNext(altarTile)) {
               altarTile.setTurnPage(1);
               return true;
            }

            for (BookData.Bookmarks.Slot slot : bookData.getBookmarks().getSlots()) {
               if (!slot.getId().isEmpty()) {
                  if (slot.getIndex() < 5) {
                     float xIn = -0.3F - altarTile.buttonScaleRender - 0.15F;
                     float yIn = slot.getIndex() * 1.5F;
                     width = 0.935F;
                     if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                        altarTile.slotClicked = slot.getIndex();
                        return true;
                     }
                  }

                  if (slot.getIndex() >= 5 && slot.getIndex() < 10) {
                     float xIn = -5.5F + slot.getIndex() * 1.15F;
                     float yIn = -0.75F - altarTile.buttonScaleRender - 0.25F;
                     width = 0.935F;
                     if (canInteract(leftCursorX, leftCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                        altarTile.slotClicked = slot.getIndex();
                        return true;
                     }
                  }

                  if (slot.getIndex() >= 10 && slot.getIndex() < 15) {
                     float xIn = -11.25F + slot.getIndex() * 1.15F;
                     float yIn = -0.75F - altarTile.buttonScaleRender - 0.25F;
                     width = 0.935F;
                     if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                        altarTile.slotClicked = slot.getIndex();
                        return true;
                     }
                  }

                  if (slot.getIndex() >= 15) {
                     float xIn = 5.2F + altarTile.buttonScaleRender + 0.15F;
                     float yIn = (slot.getIndex() - 15) * 1.5F;
                     width = 0.935F;
                     if (canInteract(rightCursorX, rightCursorY, xIn, yIn, width, width, altarTile, drawingType)) {
                        altarTile.slotClicked = slot.getIndex();
                        return true;
                     }
                  }
               }
            }
         }

         String location1 = "";
         String location2 = "";
         int chapter = bookData.getChapter();
         int page = bookData.getPage();
         if (page % 2 == 1) {
            page--;
         }

         if (bookEntries.chapterList.get(chapter).pages.size() > page && page >= 0) {
            location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
         }

         if (bookEntries.chapterList.get(chapter).pages.size() > page + 1 && page >= 0) {
            location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;
         }

         BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location1));
         BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location2));
         if (page1 != null && !isClicked) {
            for (BookPaintElement paintElement : page1.paintElements) {
               if (paintElement.client != null) {
                  PaintSystem paintSystem = paintElement.client.getPaintSystem(bookData.getUUID());
                  float cursorX = leftCursorX;
                  float cursorY = leftCursorY;
                  if (Minecraft.getInstance().player != null
                     && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BOOK_CANVAS.get()) {
                     float w = paintElement.width / 326.0F * 2.55F * paintElement.scale / 0.062F;
                     float h = paintElement.height / 326.0F * 2.55F * paintElement.scale / 0.062F;
                     float xx = paintElement.x + 0.025F;
                     float yx = paintElement.y - 0.5F;
                     if (canInteract(leftCursorX, leftCursorY, xx, yx, w, h, altarTile, drawingType)) {
                        Minecraft.getInstance().setScreen(new CanvasPaintingCropScreen(paintElement, paintSystem));
                        return true;
                     }
                  }

                  if (paintSystem.getMovingSelection() == null) {
                     for (PaintSystem.Button button : paintSystem.buttons) {
                        if (button.isVisible(paintSystem)) {
                           BookImage image = new BookImage(
                              button.getX(paintSystem, PageDrawing.PageOn.LEFT_PAGE, 0.0F),
                              button.getY(paintSystem, PageDrawing.PageOn.LEFT_PAGE, 0.0F),
                              0.0F,
                              0.0F,
                              0.0F,
                              button.width,
                              button.height,
                              button.width,
                              button.height,
                              button.getScale(altarTile.buttonScaleRender),
                              button.getTexture(paintSystem),
                              new ArrayList<>()
                           );
                           float w = image.width / 330.0F * image.scale / 0.062F;
                           float h = image.height / 330.0F * image.scale / 0.062F;
                           float xx = image.x - w / 2.0F + 0.455F;
                           float yx = image.y - h / 2.0F + 0.49F;
                           if (canInteract(cursorX, cursorY, xx, yx, w, h, altarTile, drawingType)) {
                              if (!button.getDisabled(paintSystem)) {
                                 button.onClick(paintSystem);
                                 button.clicked = true;
                                 return true;
                              }

                              return false;
                           }
                        }
                     }

                     if (paintSystem.toolsVisible) {
                        for (int i = 0; i < 3; i++) {
                           if (i < paintSystem.getColors().colors.size()) {
                              PaintSystem.Colors.ColorSelection colorSelection = paintSystem.getColors().colors.get(i);
                              float w1 = colorSelection.colorPosData.width / 326.0F * 2.55F / 0.062F;
                              float h1 = colorSelection.colorPosData.height / 326.0F * 2.55F / 0.062F;
                              float x1 = (float)colorSelection.colorPosData.pos.x + 0.025F - w1 / 2.0F;
                              float y1 = (float)colorSelection.colorPosData.pos.y - 0.5F - 0.025F - h1 / 2.0F;
                              if (canInteract(cursorX, cursorY, x1, y1, w1, h1, altarTile, drawingType)) {
                                 if (i == 2) {
                                    paintSystem.getColors().cycleColorBack(paintSystem);
                                 } else {
                                    paintSystem.getColors().cycleColor(paintSystem);
                                 }

                                 return true;
                              }
                           }
                        }
                     }

                     if (paintSystem.toolsVisible && paintSystem.getValueSliders().click(cursorX, cursorY, PageDrawing.PageOn.LEFT_PAGE)) {
                        return true;
                     }
                  }

                  if (paintSystem.toolsVisible) {
                     float w = paintElement.width / 326.0F * 2.55F * paintElement.scale / 0.062F;
                     float h = paintElement.height / 326.0F * 2.55F * paintElement.scale / 0.062F;
                     float xx = paintElement.x + 0.025F;
                     float yx = paintElement.y - 0.5F;
                     paintSystem.getBrush();
                     float cushion = PaintSystem.Brush.size * 0.12F;
                     if (canInteract(cursorX, cursorY, xx - cushion, yx - cushion, w + cushion * 2.0F, h + cushion * 2.0F, altarTile, drawingType)) {
                        float xPixel = (cursorX - xx) / w * paintElement.width;
                        float yPixel = (cursorY - yx) / h * paintElement.height;
                        paintSystem.click(xPixel, yPixel);
                        return true;
                     }
                  }
               }
            }

            for (BookWritableTextBox bookWritableTextBox : page1.writableTextBoxes) {
               if (canInteract(
                  leftCursorX,
                  leftCursorY,
                  bookWritableTextBox.paragraphElement.x + 0.45F,
                  bookWritableTextBox.paragraphElement.y,
                  bookWritableTextBox.paragraphElement.width / 6.15F,
                  bookWritableTextBox.paragraphElement.height / 2.57F,
                  altarTile,
                  drawingType
               )) {
                  setFocusedWritableTextBox(altarTile, page1.location, bookWritableTextBox);
                  long ix = Util.getMillis();
                  BookWritableTextBox.Client.DisplayCache bookeditscreen$displaycache = ((BookWritableTextBox)focusedWritableTextBox.getRight())
                     .client
                     .getDisplayCache(((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook);
                  BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i(
                     (int)((leftCursorX - bookWritableTextBox.paragraphElement.x - 0.45F) / 5.0F * 115.0F),
                     (int)((leftCursorY - bookWritableTextBox.paragraphElement.y) / 7.1F * 162.0F)
                  );
                  int j = bookeditscreen$displaycache.getIndexAtPosition(ClientProxy.font(), pos2i);
                  bookWritableTextBox.client.clicked = true;
                  bookWritableTextBox.client.clickedPos = pos2i;
                  if (j >= 0) {
                     if (j != ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastIndex
                        || ix - ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastClickTime >= 250L) {
                        ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.setCursorPos(j, Screen.hasShiftDown());
                     } else if (!((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.isSelecting()) {
                        ((BookWritableTextBox)focusedWritableTextBox.getRight())
                           .client
                           .selectWord(j, ((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook);
                     } else {
                        ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.selectAll();
                     }

                     ((BookWritableTextBox)focusedWritableTextBox.getRight())
                        .client
                        .clearDisplayCache(((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook.getUUID());
                  }

                  ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastIndex = j;
                  ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastClickTime = ix;
                  return true;
               }
            }

            for (BookNonItemTooltip bookNonItemTooltip : page1.nonItemTooltipList) {
               if ((!bookNonItemTooltip.hyperlink.id.isEmpty() || !bookNonItemTooltip.hyperlink.url.isEmpty())
                  && canInteract(
                     leftCursorX,
                     leftCursorY,
                     bookNonItemTooltip.x,
                     bookNonItemTooltip.y,
                     bookNonItemTooltip.width,
                     bookNonItemTooltip.height,
                     altarTile,
                     drawingType
                  )) {
                  if (!bookNonItemTooltip.hyperlink.url.isEmpty()) {
                     showLinkScreenClient(bookNonItemTooltip.hyperlink.url);
                  }

                  if (!bookNonItemTooltip.hyperlink.id.isEmpty()) {
                     for (BookChapter chapterEntry : bookEntries.chapterList) {
                        for (BookPageEntry pageEntry : chapterEntry.pages) {
                           if (pageEntry.location.equals(bookNonItemTooltip.hyperlink.id)) {
                              altarTile.setTurnPage(-1, pageEntry.chapterNum, pageEntry.chapterPageNum);
                              return true;
                           }
                        }
                     }
                  }
                  break;
               }
            }

            for (BookItemsAndFluids bookItemStackInSlot : page1.itemList) {
               if (canInteract(leftCursorX, leftCursorY, bookItemStackInSlot.x, bookItemStackInSlot.y, 0.86F, 0.86F, altarTile, drawingType)) {
                  String itemRegistryName;
                  if (bookItemStackInSlot.item != null) {
                     itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.item.getItem()).toString();
                  } else {
                     itemRegistryName = HexereiUtil.getRegistryName(bookItemStackInSlot.fluid.getFluid()).toString();
                  }

                  boolean flag = false;
                  if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryName)) {
                     BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryName);
                     if (chapter != hyperlink.chapter || page != hyperlink.page && page != hyperlink.page - 1) {
                        altarTile.setTurnPage(-1, hyperlink.chapter, hyperlink.page);
                     }

                     flag = true;
                  }

                  if (!flag) {
                     for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                        for (int k = 0; k < bookEntries.chapterList.get(j).pages.size(); k++) {
                           String location3 = bookEntries.chapterList.get(j).pages.get(k).location;
                           BookPage page_check = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location3));
                           if (page_check != null && page_check.itemHyperlink.equals(itemRegistryName)) {
                              if (chapter != j || page != k && page != k - 1) {
                                 altarTile.setTurnPage(-1, j, k);
                              }

                              BookManager.addBookItemHyperlink(itemRegistryName, new BookHyperlink(j, k));
                              return true;
                           }
                        }
                     }
                  }
                  break;
               }
            }

            for (BookImage bookImage : page1.imageList) {
               float w = bookImage.width / 330.0F * bookImage.scale / 0.062F;
               float h = bookImage.height / 330.0F * bookImage.scale / 0.062F;
               float xx = bookImage.x - w / 2.0F + 0.45F;
               float yx = bookImage.y - h / 2.0F + 0.49F;
               if (canInteract(leftCursorX, leftCursorY, xx, yx, w, h, altarTile, drawingType)) {
                  if (!bookImage.hyperlink.url.isEmpty()) {
                     showLinkScreenClient(bookImage.hyperlink.url);
                  }

                  if (!bookImage.hyperlink.id.isEmpty()) {
                     for (BookChapter chapterEntry : bookEntries.chapterList) {
                        for (BookPageEntry pageEntryx : chapterEntry.pages) {
                           if (pageEntryx.location.equals(bookImage.hyperlink.id)) {
                              altarTile.setTurnPage(-1, pageEntryx.chapterNum, pageEntryx.chapterPageNum);
                              return true;
                           }
                        }
                     }
                  }
                  break;
               }
            }

            if (altarTile.slotClicked == -1) {
               for (BookEntity bookEntity : page1.entityList) {
                  float xIn = bookEntity.x + bookEntity.offset.x + 0.52F;
                  float yIn = bookEntity.y + bookEntity.offset.y;
                  float widthx = 1.25F + bookEntity.scale / 5.0F;
                  if (canInteract(leftCursorX, leftCursorY, xIn - widthx / 2.0F, yIn - widthx / 2.0F, widthx, widthx, altarTile, drawingType)) {
                     bookEntity.clicked = true;
                     return true;
                  }
               }
            }
         }

         if (page2 != null) {
            if (!isClicked) {
               for (BookPaintElement paintElementx : page2.paintElements) {
                  if (paintElementx.client != null) {
                     PaintSystem paintSystemx = paintElementx.client.getPaintSystem(bookData.getUUID());
                     float cursorXx = rightCursorX;
                     float cursorYx = rightCursorY;
                     if (Minecraft.getInstance().player != null
                        && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BOOK_CANVAS.get()) {
                        float w = paintElementx.width / 326.0F * 2.55F * paintElementx.scale / 0.062F;
                        float h = paintElementx.height / 326.0F * 2.55F * paintElementx.scale / 0.062F;
                        float xx = paintElementx.x + 0.025F;
                        float yx = paintElementx.y - 0.5F;
                        if (canInteract(rightCursorX + 0.22F, rightCursorY, xx, yx, w, h, altarTile, drawingType)) {
                           Minecraft.getInstance().setScreen(new CanvasPaintingCropScreen(paintElementx, paintSystemx));
                           return true;
                        }
                     }

                     if (paintSystemx.getMovingSelection() == null) {
                        for (PaintSystem.Button buttonx : paintSystemx.buttons) {
                           if (buttonx.isVisible(paintSystemx)) {
                              BookImage image = new BookImage(
                                 buttonx.getX(paintSystemx, PageDrawing.PageOn.RIGHT_PAGE, 0.0F),
                                 buttonx.getY(paintSystemx, PageDrawing.PageOn.RIGHT_PAGE, 0.0F),
                                 0.0F,
                                 0.0F,
                                 0.0F,
                                 buttonx.width,
                                 buttonx.height,
                                 buttonx.width,
                                 buttonx.height,
                                 buttonx.getScale(altarTile.buttonScaleRender),
                                 buttonx.getTexture(paintSystemx),
                                 new ArrayList<>()
                              );
                              float w = image.width / 330.0F * image.scale / 0.062F;
                              float h = image.height / 330.0F * image.scale / 0.062F;
                              float xx = image.x - w / 2.0F + 0.455F;
                              float yx = image.y - h / 2.0F + 0.49F;
                              if (canInteract(cursorXx, cursorYx, xx, yx, w, h, altarTile, drawingType)) {
                                 if (!buttonx.getDisabled(paintSystemx)) {
                                    buttonx.onClick(paintSystemx);
                                    buttonx.clicked = true;
                                    return true;
                                 }

                                 return false;
                              }
                           }
                        }

                        if (paintSystemx.toolsVisible) {
                           for (int ix = 0; ix < 3; ix++) {
                              if (ix < paintSystemx.getColors().colors.size()) {
                                 PaintSystem.Colors.ColorSelection colorSelection = paintSystemx.getColors().colors.get(ix);
                                 float w1 = colorSelection.colorPosData.width / 326.0F * 2.55F / 0.062F;
                                 float h1 = colorSelection.colorPosData.height / 326.0F * 2.55F / 0.062F;
                                 float x1 = (float)colorSelection.colorPosData.pos.x + 0.025F - w1 / 2.0F + 0.8F;
                                 float y1 = (float)colorSelection.colorPosData.pos.y - 0.5F - 0.025F - h1 / 2.0F;
                                 if (canInteract(cursorXx, cursorYx, x1, y1, w1, h1, altarTile, drawingType)) {
                                    if (ix == 2) {
                                       paintSystemx.getColors().cycleColorBack(paintSystemx);
                                    } else {
                                       paintSystemx.getColors().cycleColor(paintSystemx);
                                    }

                                    return true;
                                 }
                              }
                           }
                        }

                        if (paintSystemx.toolsVisible && paintSystemx.getValueSliders().click(cursorXx, cursorYx, PageDrawing.PageOn.RIGHT_PAGE)) {
                           return true;
                        }
                     }

                     if (paintSystemx.toolsVisible) {
                        float w = paintElementx.width / 326.0F * 2.55F * paintElementx.scale / 0.062F;
                        float h = paintElementx.height / 326.0F * 2.55F * paintElementx.scale / 0.062F;
                        float xx = paintElementx.x + 0.025F;
                        float yx = paintElementx.y - 0.5F;
                        paintSystemx.getBrush();
                        float cushion = PaintSystem.Brush.size * 0.12F;
                        if (canInteract(cursorXx + 0.22F, cursorYx, xx - cushion, yx - cushion, w + cushion * 2.0F, h + cushion * 2.0F, altarTile, drawingType)
                           )
                         {
                           float xPixel = (cursorXx - xx) / w * paintElementx.width + 1.8F;
                           float yPixel = (cursorYx - yx) / h * paintElementx.height;
                           paintSystemx.click(xPixel, yPixel);
                           return true;
                        }
                     }
                  }
               }

               for (BookWritableTextBox bookWritableTextBoxx : page2.writableTextBoxes) {
                  if (canInteract(
                     rightCursorX,
                     rightCursorY,
                     bookWritableTextBoxx.paragraphElement.x + 0.45F,
                     bookWritableTextBoxx.paragraphElement.y,
                     bookWritableTextBoxx.paragraphElement.width / 6.15F,
                     bookWritableTextBoxx.paragraphElement.height / 2.57F,
                     altarTile,
                     drawingType
                  )) {
                     setFocusedWritableTextBox(altarTile, page2.location, bookWritableTextBoxx);
                     long ixx = Util.getMillis();
                     BookWritableTextBox.Client.DisplayCache bookeditscreen$displaycache = ((BookWritableTextBox)focusedWritableTextBox.getRight())
                        .client
                        .getDisplayCache(((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook);
                     BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i(
                        (int)((rightCursorX - bookWritableTextBoxx.paragraphElement.x - 0.45F) / 5.0F * 115.0F),
                        (int)((rightCursorY - bookWritableTextBoxx.paragraphElement.y) / 7.1F * 162.0F)
                     );
                     int j = bookeditscreen$displaycache.getIndexAtPosition(ClientProxy.font(), pos2i);
                     bookWritableTextBoxx.client.clicked = true;
                     bookWritableTextBoxx.client.clickedPos = pos2i;
                     if (j >= 0) {
                        if (j != ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastIndex
                           || ixx - ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastClickTime >= 250L) {
                           ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.setCursorPos(j, Screen.hasShiftDown());
                        } else if (!((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.isSelecting()) {
                           ((BookWritableTextBox)focusedWritableTextBox.getRight())
                              .client
                              .selectWord(j, ((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook);
                        } else {
                           ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.pageEdit.selectAll();
                        }

                        ((BookWritableTextBox)focusedWritableTextBox.getRight())
                           .client
                           .clearDisplayCache(((BookOfShadowsAltarTile)focusedWritableTextBox.getLeft()).currentBook.getUUID());
                     }

                     ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastIndex = j;
                     ((BookWritableTextBox)focusedWritableTextBox.getRight()).client.lastClickTime = ixx;
                     return true;
                  }
               }

               for (BookNonItemTooltip bookNonItemTooltipx : page2.nonItemTooltipList) {
                  if ((!bookNonItemTooltipx.hyperlink.id.isEmpty() || !bookNonItemTooltipx.hyperlink.url.isEmpty())
                     && canInteract(
                        rightCursorX,
                        rightCursorY,
                        bookNonItemTooltipx.x,
                        bookNonItemTooltipx.y,
                        bookNonItemTooltipx.width,
                        bookNonItemTooltipx.height,
                        altarTile,
                        drawingType
                     )) {
                     if (!bookNonItemTooltipx.hyperlink.url.isEmpty()) {
                        showLinkScreenClient(bookNonItemTooltipx.hyperlink.url);
                     }

                     if (!bookNonItemTooltipx.hyperlink.id.isEmpty()) {
                        for (BookChapter chapterEntry : bookEntries.chapterList) {
                           for (BookPageEntry pageEntryxx : chapterEntry.pages) {
                              if (pageEntryxx.location.equals(bookNonItemTooltipx.hyperlink.id)) {
                                 altarTile.setTurnPage(-1, pageEntryxx.chapterNum, pageEntryxx.chapterPageNum);
                                 return true;
                              }
                           }
                        }
                     }
                     break;
                  }
               }

               for (BookItemsAndFluids bookItemStackInSlotx : page2.itemList) {
                  if (canInteract(rightCursorX, rightCursorY, bookItemStackInSlotx.x, bookItemStackInSlotx.y, 0.86F, 0.86F, altarTile, drawingType)) {
                     String itemRegistryNamex;
                     if (bookItemStackInSlotx.item != null) {
                        itemRegistryNamex = HexereiUtil.getRegistryName(bookItemStackInSlotx.item.getItem()).toString();
                     } else {
                        itemRegistryNamex = HexereiUtil.getRegistryName(bookItemStackInSlotx.fluid.getFluid()).toString();
                     }

                     boolean flagx = false;
                     if (BookManager.getBookItemHyperlinks().containsKey(itemRegistryNamex)) {
                        BookHyperlink hyperlink = BookManager.getBookItemHyperlinks().get(itemRegistryNamex);
                        if (chapter != hyperlink.chapter || page != hyperlink.page && page != hyperlink.page - 1) {
                           altarTile.setTurnPage(-1, hyperlink.chapter, hyperlink.page);
                        }

                        flagx = true;
                     }

                     if (!flagx) {
                        for (int j = 1; j < bookEntries.chapterList.size(); j++) {
                           for (int kx = 0; kx < bookEntries.chapterList.get(j).pages.size(); kx++) {
                              String location3 = bookEntries.chapterList.get(j).pages.get(kx).location;
                              BookPage page_check = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location3));
                              if (page_check != null && page_check.itemHyperlink.equals(itemRegistryNamex)) {
                                 if (chapter != j || page != kx && page != kx - 1) {
                                    altarTile.setTurnPage(-1, j, kx);
                                 }

                                 BookManager.addBookItemHyperlink(itemRegistryNamex, new BookHyperlink(j, kx));
                                 return true;
                              }
                           }
                        }
                     }
                     break;
                  }
               }

               for (BookImage bookImagex : page2.imageList) {
                  if (!bookImagex.hyperlink.id.isEmpty() || !bookImagex.hyperlink.url.isEmpty()) {
                     float w = bookImagex.width / 330.0F * bookImagex.scale / 0.062F;
                     float h = bookImagex.height / 330.0F * bookImagex.scale / 0.062F;
                     float xx = bookImagex.x - w / 2.0F + 0.45F;
                     float yx = bookImagex.y - h / 2.0F + 0.49F;
                     if (canInteract(rightCursorX, rightCursorY, xx, yx, w, h, altarTile, drawingType)) {
                        if (!bookImagex.hyperlink.url.isEmpty()) {
                           showLinkScreenClient(bookImagex.hyperlink.url);
                        }

                        if (!bookImagex.hyperlink.id.isEmpty()) {
                           for (BookChapter chapterEntry : bookEntries.chapterList) {
                              for (BookPageEntry pageEntryxxx : chapterEntry.pages) {
                                 if (pageEntryxxx.location.equals(bookImagex.hyperlink.id)) {
                                    altarTile.setTurnPage(-1, pageEntryxxx.chapterNum, pageEntryxxx.chapterPageNum);
                                    return true;
                                 }
                              }
                           }
                        }
                        break;
                     }
                  }
               }
            }

            if (altarTile.slotClicked == -1) {
               for (BookEntity bookEntityx : page2.entityList) {
                  float xIn = bookEntityx.x + bookEntityx.offset.x + 0.52F;
                  float yIn = bookEntityx.y + bookEntityx.offset.y;
                  float widthx = 1.25F + bookEntityx.scale / 5.0F;
                  if (canInteract(rightCursorX, rightCursorY, xIn - widthx / 2.0F, yIn - widthx / 2.0F, widthx, widthx, altarTile, drawingType)) {
                     bookEntityx.clicked = true;
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void showLinkScreenClient(String link) {
      ConfirmLinkScreen screen = new ConfirmLinkScreen(p_169232_ -> {
         if (p_169232_) {
            Util.getPlatform().openUri(link);
         }

         Minecraft.getInstance().setScreen(null);
      }, link, true);
      Minecraft.getInstance().setScreen(screen);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawSlot(
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float xIn,
      float yIn,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
      poseStack.scale(0.5F, 0.5F, 0.5F);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.translate(-0.001875F, -0.0033125F, 0.0F);
      poseStack.translate(xIn / 8.1F, yIn / 8.1F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
      Matrix4f matrix = poseStack.last().pose();
      VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/slot.png")));
      Pose normal = poseStack.last();
      int u = 0;
      int v = 0;
      int imageWidth = 32;
      int imageHeight = 32;
      int width = 18;
      int height = 18;
      float u1 = (u + 0.0F) / imageWidth;
      float u2 = ((float)u + width) / imageWidth;
      float v1 = (v + 0.0F) / imageHeight;
      float v2 = ((float)v + height) / imageHeight;
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, -0.0030555555F * width)
         .setColor(255, 255, 255, 125)
         .setUv(u1, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, -0.0030555555F * width)
         .setColor(255, 255, 255, 125)
         .setUv(u1, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0030555555F * width)
         .setColor(255, 255, 255, 125)
         .setUv(u2, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0030555555F * width)
         .setColor(255, 255, 255, 125)
         .setUv(u2, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      if (bufferSource instanceof BufferSource source) {
         source.endBatch();
      }

      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawFluidInSlot(
      BookOfShadowsAltarTile altarTile,
      @NotNull BookItemsAndFluids bookItemsAndFluids,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float xIn,
      float yIn,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      poseStack.pushPose();
      FluidStack stack = bookItemsAndFluids.fluid;
      int capacity = bookItemsAndFluids.capacity;
      boolean showSlot = bookItemsAndFluids.show_slot;
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
      poseStack.scale(0.5F, 0.5F, 0.5F);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.translate(-0.001875F, -0.0033125F, 0.0F);
      poseStack.translate(xIn / 8.1F, yIn / 8.1F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
      Matrix4f matrix = poseStack.last().pose();
      if (showSlot) {
         VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/slot.png")));
         Pose normal = poseStack.last();
         int u = 0;
         int v = 0;
         int imageWidth = 18;
         int imageHeight = 18;
         int width = 18;
         int height = 18;
         float u1 = (u + 0.0F) / imageWidth;
         float u2 = ((float)u + width) / imageWidth;
         float v1 = (v + 0.0F) / imageHeight;
         float v2 = ((float)v + height) / imageHeight;
         buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, -0.0030555555F * width)
            .setColor(255, 255, 255, 255)
            .setUv(u1, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, -0.0030555555F * width)
            .setColor(255, 255, 255, 255)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0030555555F * width)
            .setColor(255, 255, 255, 255)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0030555555F * width)
            .setColor(255, 255, 255, 255)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
      }

      this.drawFluid(
         poseStack,
         bufferSource,
         (int)bookItemsAndFluids.fluid_width,
         (int)bookItemsAndFluids.fluid_height,
         stack,
         capacity,
         light,
         overlay,
         bookItemsAndFluids.fluid_offset_x,
         bookItemsAndFluids.fluid_offset_y,
         bookItemsAndFluids.fluid_width,
         bookItemsAndFluids.fluid_height
      );
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   private void drawFluid(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int tiledWidth,
      int tiledHeight,
      FluidStack fluidStack,
      int capacity,
      int light,
      int overlay,
      float x_offset,
      float y_offset,
      float width,
      float height
   ) {
      Fluid fluid = fluidStack.getFluid();
      TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
      int fluidColor = IClientFluidTypeExtensions.of(fluid).getTintColor(fluidStack);
      int amount = fluidStack.getAmount();
      if (amount == 0) {
         amount = capacity > 0 ? capacity : 1000;
      }

      int scaledAmount = amount * tiledHeight / (capacity != 0 ? capacity : 1000);
      if (amount > 0 && scaledAmount < 1) {
         scaledAmount = 1;
      }

      if (scaledAmount > tiledHeight) {
         scaledAmount = tiledHeight;
      }

      if (capacity == 0) {
         scaledAmount = tiledHeight;
      }

      drawTiledSprite(
         poseStack,
         bufferSource,
         tiledWidth,
         tiledHeight,
         fluidColor,
         scaledAmount,
         fluidStillSprite,
         capacity,
         amount,
         light,
         overlay,
         x_offset,
         y_offset,
         width,
         height
      );
   }

   @OnlyIn(Dist.CLIENT)
   private static void drawTiledSprite(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int tiledWidth,
      int tiledHeight,
      int color,
      int scaledAmount,
      TextureAtlasSprite sprite,
      int capacity,
      int amount,
      int light,
      int overlay,
      float x_offset,
      float y_offset,
      float width,
      float height
   ) {
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      int xTileCount = tiledWidth / 16;
      int xRemainder = tiledWidth - xTileCount * 16;
      int yTileCount = scaledAmount / 16;
      int yRemainder = scaledAmount - yTileCount * 16;

      for (int xTile = 0; xTile <= xTileCount; xTile++) {
         for (int yTile = 0; yTile <= yTileCount; yTile++) {
            int width2 = xTile == xTileCount ? xRemainder : (int)width;
            int height2 = yTile == yTileCount ? yRemainder : (int)height;
            int x_tile = xTile * 16;
            int y_tile = tiledHeight - (yTile + 1) * (int)height;
            if (width2 > 0 && height2 > 0) {
               int maskTop = (int)height - height2;
               int maskRight = (int)width - width2;
               drawTextureWithMasking(
                  poseStack,
                  bufferSource,
                  capacity,
                  amount,
                  x_tile,
                  y_tile,
                  sprite,
                  color,
                  maskTop,
                  maskRight,
                  1.0F,
                  light,
                  overlay,
                  x_offset,
                  y_offset,
                  width,
                  height
               );
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
      Minecraft minecraft = Minecraft.getInstance();
      if (fluidStack.isEmpty()) {
         return (TextureAtlasSprite)minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.withDefaultNamespace("missingno"));
      } else {
         Fluid fluid = fluidStack.getFluid();
         ResourceLocation fluidStill = IClientFluidTypeExtensions.of(fluid).getStillTexture(fluidStack);
         return (TextureAtlasSprite)minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void setGLColorFromInt(int color) {
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      float alpha = (color >> 24 & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(red, green, blue, alpha);
   }

   @OnlyIn(Dist.CLIENT)
   private static void drawTextureWithMasking(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int capacity,
      int amount,
      float xCoord,
      float yCoord,
      TextureAtlasSprite textureSprite,
      int color,
      int maskTop,
      int maskRight,
      float zLevel,
      int light,
      int overlay,
      float x_offset,
      float y_offset,
      float width,
      float height
   ) {
      float uMin = textureSprite.getU0();
      float uMax = textureSprite.getU1();
      float vMin = textureSprite.getV0();
      float vMax = textureSprite.getV1();
      uMax -= maskRight / width * (uMax - uMin);
      vMax -= maskTop / height * (vMax - vMin);
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      float alpha = (color >> 24 & 0xFF) / 255.0F;
      poseStack.pushPose();
      poseStack.translate(0.001F, 0.0485F + y_offset * 0.005975F, x_offset * 0.005975F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
      Matrix4f matrix = poseStack.last().pose();
      Pose normal = poseStack.last();
      poseStack.popPose();
      VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * width, 0.0F)
         .setColor(red, green, blue, alpha)
         .setUv(uMin, vMax)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * width, 0.0F)
         .setColor(red, green, blue, alpha)
         .setUv(uMax, vMax)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * width, 0.006111111F * (height - maskTop))
         .setColor(red, green, blue, alpha)
         .setUv(uMax, vMin)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * width, 0.006111111F * (height - maskTop))
         .setColor(red, green, blue, alpha)
         .setUv(uMin, vMin)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
   }

   private float easeInOutElastic(double x) {
      double c5 = 1.3962634015954636;
      return (float)(
         x == 0.0 ? 0.0 : (x == 1.0 ? 1.0 : (x < 0.5 ? 4.0 * x * x * x : Math.pow(2.0, -20.0 * x + 10.0) * Math.sin((20.0 * x - 11.125) * c5) / 2.0 + 1.0))
      );
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTooltipImage(
      ItemStack stack,
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      float partialTicks
   ) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 1.125F, 0.5F);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270.0F));
      float scale = this.easeInOutElastic(Mth.lerp(partialTicks, this.drawTooltipScaleOld, this.drawTooltipScale));
      poseStack.translate(0.05F + 0.1F * scale, -(1.0F - (this.drawTooltipScale < 0.5F ? this.drawTooltipScale * 2.0F : 1.0F)) / 12.0F, 0.0F);
      poseStack.scale(scale, scale, scale);
      RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
      this.tooltipStack = stack;
      if (!this.tooltipStack.isEmpty()) {
         List<Component> tooltip = stack.getTooltipLines(
            TooltipContext.EMPTY, Hexerei.proxy.getPlayer(), Minecraft.getInstance().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL
         );
         if (!tooltip.isEmpty()) {
            tooltip.addAll(this.tooltipText);
         }

         String modId = HexereiUtil.getRegistryName(this.tooltipStack.getItem()).getNamespace();
         String modName = getModNameForModId(modId);
         MutableComponent modNameComponent = Component.translatable(modName);
         modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
         if (tooltip.isEmpty() || !((Component)tooltip.getLast()).getString().equals(modName)) {
            tooltip.add(modNameComponent);
         }

         List<Component> list = new ArrayList<>(this.tooltipText);
         list.addFirst(Component.translatable(""));
         this.renderTooltip(this.tooltipStack, bufferSource, poseStack, tooltip, stack.getTooltipImage(), 0, 0, overlay, light);
      }

      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public static List<Component> getFluidTooltip(BookItemsAndFluids bookItemStackInSlot) {
      FluidStack fluidStack = bookItemStackInSlot.fluid;
      int capacity = bookItemStackInSlot.capacity;
      int amount = bookItemStackInSlot.amount;
      List<Component> tooltip = new ArrayList<>();
      Fluid fluidType = fluidStack.getFluid();
      MutableComponent displayName = (MutableComponent)fluidStack.getHoverName();
      displayName.withStyle(ChatFormatting.WHITE);
      tooltip.add(displayName);
      if (capacity != 0) {
         MutableComponent amountString = Component.translatable(
            "book.hexerei.tooltip.liquid.amount.with.capacity", new Object[]{nf.format((long)amount), nf.format((long)capacity)}
         );
         tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
      } else if (amount != 0) {
         MutableComponent amountString = Component.translatable("book.hexerei.tooltip.liquid.amount", new Object[]{nf.format((long)amount)});
         tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
      }

      if (!bookItemStackInSlot.extra_tooltips.isEmpty()) {
         tooltip.addAll(bookItemStackInSlot.extra_tooltips);
      }

      String modId = HexereiUtil.getRegistryName(fluidStack.getFluid()).getNamespace();
      String modName = getModNameForModId(modId);
      MutableComponent modNameComponent = Component.translatable(modName);
      modNameComponent.withStyle(Style.EMPTY.withItalic(true).withColor(5592575));
      tooltip.add(modNameComponent);
      return tooltip;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTooltipText(
      BookOfShadowsAltarTile altarTile, PoseStack poseStack, MultiBufferSource bufferSource, float zLevel, int light, int overlay, float partialTicks
   ) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 1.125F, 0.5F);
      poseStack.translate(
         (float)Math.sin(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F),
         0.0F,
         (float)Math.cos(altarTile.degreesSpunRender / 57.3F) / 32.0F * (altarTile.degreesOpenedRender / 5.0F - 12.0F)
      );
      poseStack.translate(0.0F, (BookOfShadowsAltarTile.easeFlop(1.0F - altarTile.degreesFlopped / 90.0F) - 1.0F) / 16.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(altarTile.degreesSpunRender));
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-(altarTile.degreesOpenedRender / 2.0F + 45.0F)));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-altarTile.degreesFloppedRender));
      poseStack.translate(0.0F, 0.0F, -(altarTile.degreesFloppedRender / 10.0F) / 32.0F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270.0F));
      float scale = this.easeInOutElastic(Mth.lerp(partialTicks, this.drawTooltipScaleOld, this.drawTooltipScale));
      poseStack.translate(
         0.05F + 0.1F * scale, -(1.0F - (Math.min(this.drawTooltipScale, 1.0F) < 0.5F ? Math.min(this.drawTooltipScale, 1.0F) * 2.0F : 1.0F)) / 12.0F, 0.0F
      );
      if (scale < 0.0F) {
         scale = 0.0F;
      }

      poseStack.scale(scale, scale, scale);
      List<Component> list = new ArrayList<>(this.tooltipText);
      list.addFirst(Component.translatable(""));
      this.renderTooltip(this.tooltipStack, bufferSource, poseStack, this.tooltipText, Optional.empty(), 0, 0, overlay, light);
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void renderTooltip(
      ItemStack stack,
      MultiBufferSource buffer,
      PoseStack p_169389_,
      List<Component> components,
      Optional<TooltipComponent> p_169391_,
      int p_169392_,
      int p_169393_,
      int overlay,
      int light
   ) {
      List<ClientTooltipComponent> list = new ArrayList<>();
      List<ClientTooltipComponent> list2 = new ArrayList<>();

      try {
         list = ClientHooks.gatherTooltipComponents(stack, components, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font);
         List<Component> newComponentList = new ArrayList<>();

         for (Component component : components) {
            newComponentList.add(Component.translatable(component.getString()).withStyle(component.getStyle().withColor(2697513)));
         }

         list2 = ClientHooks.gatherTooltipComponents(stack, newComponentList, p_169391_, p_169392_, 300, 750, Minecraft.getInstance().font);
      } catch (RuntimeException var15) {
      }

      this.renderTooltipInternal(buffer, p_169389_, list, list2, p_169392_, p_169393_, overlay, light);
   }

   @OnlyIn(Dist.CLIENT)
   private void renderTooltipInternal(
      MultiBufferSource bufferSource,
      PoseStack poseStack,
      List<ClientTooltipComponent> clientTooltipComponentList,
      List<ClientTooltipComponent> clientTooltipComponentList2,
      int p_169386_,
      int p_169387_,
      int overlay,
      int light
   ) {
      if (!clientTooltipComponentList.isEmpty()) {
         Pre preEvent = ClientHooks.onRenderTooltipPre(
            this.tooltipStack,
            new GuiGraphics(Minecraft.getInstance(), (BufferSource)bufferSource),
            p_169386_,
            p_169387_,
            750,
            750,
            clientTooltipComponentList,
            Minecraft.getInstance().font,
            DefaultTooltipPositioner.INSTANCE
         );
         if (preEvent.isCanceled()) {
            return;
         }

         int i = 0;
         int j = clientTooltipComponentList.size() == 1 ? -2 : 0;

         for (ClientTooltipComponent clientTooltipComponent : clientTooltipComponentList) {
            int l = clientTooltipComponent.getWidth(preEvent.getFont());
            if (l > i) {
               i = l;
            }

            j += clientTooltipComponent.getHeight();
         }

         int j2 = preEvent.getX() + 12;
         int k2 = preEvent.getY() - 12;
         if (j2 + i > 750) {
            j2 -= 28 + i;
         }

         if (k2 + j + 6 > 750) {
            k2 = 750 - j - 6;
         }

         VertexConsumer buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(ResourceLocation.parse("hexerei:textures/book/blank.png")));
         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));
         poseStack.scale(0.003F, 0.003F, 0.003F);
         poseStack.translate(-(i + 15) / 2.0F, -(j + 15) / 2.0F, -10.0F);
         Color colorEvent = ClientHooks.onRenderTooltipColor(
            this.tooltipStack, new GuiGraphics(Minecraft.getInstance(), (BufferSource)bufferSource), j2, k2, preEvent.getFont(), clientTooltipComponentList
         );
         fillGradient(
            poseStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 0.2F, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light
         );
         fillGradient(
            poseStack, buffer, j2 - 3, k2 - 4, j2 + i + 3, k2 - 2, 0.1F, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart(), overlay, light
         );
         fillGradient(
            poseStack, buffer, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 4, 0.1F, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd(), overlay, light
         );
         fillGradient(
            poseStack, buffer, j2 - 4, k2 - 3, j2 - 2, k2 + j + 3, 0.1F, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light
         );
         fillGradient(
            poseStack, buffer, j2 + i + 2, k2 - 3, j2 + i + 4, k2 + j + 3, 0.1F, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), overlay, light
         );
         ((BufferSource)bufferSource).endBatch();
         buffer = bufferSource.getBuffer(RenderType.itemEntityTranslucentCull(ResourceLocation.parse("hexerei:textures/book/blank.png")));
         fillGradient(
            poseStack, buffer, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 0.0F, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light
         );
         fillGradient(
            poseStack, buffer, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 0.0F, colorEvent.getBorderStart(), colorEvent.getBorderEnd(), overlay, light
         );
         fillGradient(poseStack, buffer, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 0.0F, colorEvent.getBorderStart(), colorEvent.getBorderStart(), overlay, light);
         fillGradient(poseStack, buffer, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 0.0F, colorEvent.getBorderEnd(), colorEvent.getBorderEnd(), overlay, light);
         RenderSystem.enableDepthTest();
         BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
         poseStack.translate(0.0, 0.0, 0.01);
         poseStack.scale(1.0F, 1.0F, 1.0E-5F);
         int l1 = k2;
         Matrix4f matrix4f = poseStack.last().pose();

         for (int l2 = 0; l2 < clientTooltipComponentList.size(); l2++) {
            ClientTooltipComponent clientTooltipComponent2 = clientTooltipComponentList.get(l2);
            if (clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip) {
               hexereiBookTooltip.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource, overlay, light);
            } else if (clientTooltipComponent2 instanceof ClientTextTooltip clientTextTooltip) {
               int r = 63;
               int g = 63;
               int b = 63;
               int a = 255;
               int col = a << 24 | r << 16 | g << 8 | b;
               Font font = preEvent.getFont();
               Matrix4f var34 = poseStack.last().pose();
               font.drawInBatch(clientTextTooltip.text, j2, l1, col, false, var34, multibuffersource$buffersource, DisplayMode.NORMAL, 0, light);
               poseStack.pushPose();
               poseStack.translate(0.5F, 0.5F, 7500.0F);
               matrix4f = poseStack.last().pose();
               font.drawInBatch(
                  ((ClientTextTooltip)clientTooltipComponentList2.get(l2)).text,
                  j2,
                  l1,
                  col,
                  false,
                  matrix4f,
                  multibuffersource$buffersource,
                  DisplayMode.NORMAL,
                  0,
                  light
               );
               poseStack.popPose();
            }

            l1 += clientTooltipComponent2.getHeight() + (l2 == 0 ? 2 : 0);
         }

         multibuffersource$buffersource.endBatch();
         l1 = k2;
         poseStack.scale(1.0F, 1.0F, 333.333F);

         for (int var35 = 0; var35 < clientTooltipComponentList.size(); var35++) {
            ClientTooltipComponent clientTooltipComponent2 = clientTooltipComponentList.get(var35);
            RenderSystem.enableDepthTest();
            if (clientTooltipComponent2 instanceof HexereiBookTooltip hexereiBookTooltip) {
               hexereiBookTooltip.renderImage(preEvent.getFont(), bufferSource, j2, l1, poseStack, itemRenderer, 0, overlay, light);
            }

            l1 += clientTooltipComponent2.getHeight() + (var35 == 0 ? 2 : 0);
         }
      }
   }

   private static int adjustColor(int p_92720_) {
      return (p_92720_ & -67108864) == 0 ? p_92720_ | 0xFF000000 : p_92720_;
   }

   protected static void fillGradient(
      PoseStack poseStack,
      VertexConsumer buffer,
      int p_93126_,
      int p_93127_,
      int p_93128_,
      int p_93129_,
      float p_93130_,
      int pColorFrom,
      int pColorTo,
      int overlay,
      int light
   ) {
      float fromAlpha = ARGB32.alpha(pColorFrom) / 255.0F * 0.9F;
      float f1 = ARGB32.red(pColorFrom) / 255.0F;
      float f2 = ARGB32.green(pColorFrom) / 255.0F;
      float f3 = ARGB32.blue(pColorFrom) / 255.0F;
      float toAlpha = ARGB32.alpha(pColorTo) / 255.0F * 0.9F;
      float f5 = ARGB32.red(pColorTo) / 255.0F;
      float f6 = ARGB32.green(pColorTo) / 255.0F;
      float f7 = ARGB32.blue(pColorTo) / 255.0F;
      Pose normal = poseStack.last();
      Matrix4f matrix4f = poseStack.last().pose();
      int u = 0;
      int v = 0;
      int imageWidth = 1;
      int imageHeight = 1;
      int width = 1;
      int height = 1;
      float u1 = (u + 0.0F) / imageWidth;
      float u2 = ((float)u + width) / imageWidth;
      float v1 = (v + 0.0F) / imageHeight;
      float v2 = ((float)v + height) / imageHeight;
      buffer.addVertex(matrix4f, p_93128_, p_93127_, p_93130_)
         .setColor(f1, f2, f3, fromAlpha)
         .setUv(u1, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix4f, p_93126_, p_93127_, p_93130_)
         .setColor(f1, f2, f3, fromAlpha)
         .setUv(u1, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix4f, p_93126_, p_93129_, p_93130_)
         .setColor(f5, f6, f7, toAlpha)
         .setUv(u2, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix4f, p_93128_, p_93129_, p_93130_)
         .setColor(f5, f6, f7, toAlpha)
         .setUv(u2, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawBookmark(
      BookImage bookImage,
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      float rotate,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      int color,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType
   ) {
      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.MIDDLE_BUTTON) {
         this.translateToMiddleButton(altarTile, poseStack, drawingType, transformType);
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
      poseStack.scale(0.5F * bookImage.scale, 0.5F * bookImage.scale, 0.5F * bookImage.scale);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.translate(
         (bookImage.x / 8.1F - 0.001875F) / bookImage.scale,
         (bookImage.y / 8.1F - 0.0033125F) / bookImage.scale,
         (drawingType == PageDrawing.DrawingType.SCREEN ? -5.0F - zLevel : -zLevel) / 1600.0F / bookImage.scale
      );
      bookImage.effects.forEach(bookImageEffect -> {
         if (bookImageEffect.type.equals("scale")) {
            float f = bookImageEffect.amount - 1.0F;
            float x = f / 2.0F + 1.0F + f / 2.0F * Mth.sin(ClientEvents.getClientTicks() / bookImageEffect.speed);
            poseStack.scale(x, x, x);
         }
      });
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      bookImage.effects
         .forEach(
            bookImageEffect -> {
               if (bookImageEffect.type.equals("tilt")) {
                  poseStack.mulPose(
                     com.mojang.math.Axis.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin(ClientEvents.getClientTicks() / bookImageEffect.speed))
                  );
               }
            }
         );
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(rotate));
      if (transformType != ItemDisplayContext.NONE) {
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-35.0F));
      }

      RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
      Matrix4f matrix = poseStack.last().pose();
      VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(bookImage.texture)));
      Pose normal = poseStack.last();
      int u = (int)bookImage.u;
      int v = (int)bookImage.v;
      int imageWidth = (int)bookImage.imageWidth;
      int imageHeight = (int)bookImage.imageHeight;
      int width = (int)bookImage.width;
      int height = (int)bookImage.height;
      float u1 = (u + 0.0F) / imageWidth;
      float u2 = ((float)u + width) / imageWidth;
      float v1 = (v + 0.0F) / imageHeight;
      float v2 = ((float)v + height) / imageHeight;
      float a = 1.0F;
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      if (color != -1) {
         r = (color >> 16 & 0xFF) / 255.0F;
         g = (color >> 8 & 0xFF) / 255.0F;
         b = (color & 0xFF) / 255.0F;
      }

      if (transformType != ItemDisplayContext.NONE) {
         buffer.addVertex(matrix, 0.0F, -0.006111111F * height, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0F, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0F, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, -0.006111111F * height, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, -0.006111111F * height, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0F, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0F, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, -0.006111111F * height, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
      } else {
         buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
      }

      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawImage(
      BookImage bookImage,
      BookOfShadowsAltarTile altarTile,
      float cursorX,
      float cursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      this.drawImage(bookImage, altarTile, cursorX, cursorY, poseStack, bufferSource, zLevel, light, overlay, pageOn, -1, drawingType, ItemDisplayContext.NONE);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawImage(
      BookImage bookImage,
      BookOfShadowsAltarTile altarTile,
      float cursorX,
      float cursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      int color,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType
   ) {
      this.drawImage(bookImage, altarTile, cursorX, cursorY, poseStack, bufferSource, zLevel, light, overlay, pageOn, color, drawingType, transformType, false);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawImage(
      BookImage bookImage,
      BookOfShadowsAltarTile altarTile,
      float cursorX,
      float cursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      int color,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      boolean transparent
   ) {
      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV_PREV) {
         translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV) {
         translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.MIDDLE_BUTTON) {
         this.translateToMiddleButton(altarTile, poseStack, drawingType, transformType);
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
      poseStack.scale(0.5F * bookImage.scale, 0.5F * bookImage.scale, 0.5F * bookImage.scale);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.translate(
         (bookImage.x / 8.1F - 0.001875F) / bookImage.scale, (bookImage.y / 8.1F - 0.0033125F) / bookImage.scale, -(zLevel + bookImage.z) / 1600.0F
      );
      bookImage.effects.forEach(bookImageEffect -> {
         if (bookImageEffect.type.equals("scale")) {
            float f = bookImageEffect.amount - 1.0F;
            float x = f / 2.0F + 1.0F + f / 2.0F * Mth.sin(ClientEvents.getClientTicks() / bookImageEffect.speed);
            poseStack.scale(x, x, x);
         }
      });
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      bookImage.effects
         .forEach(
            bookImageEffect -> {
               if (bookImageEffect.type.equals("tilt")) {
                  poseStack.mulPose(
                     com.mojang.math.Axis.XP.rotationDegrees(-bookImageEffect.amount * Mth.sin(ClientEvents.getClientTicks() / bookImageEffect.speed))
                  );
               }
            }
         );
      RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
      AtomicReference<String> loc = new AtomicReference<>(bookImage.texture);
      AtomicReference<BookImage> overlay_image = new AtomicReference<>(bookImage);
      AtomicReference<Boolean> overlay_draw = new AtomicReference<>(false);
      AtomicReference<Integer> u = new AtomicReference<>((int)bookImage.u);
      AtomicReference<Integer> v = new AtomicReference<>((int)bookImage.v);
      AtomicReference<Integer> imageWidth = new AtomicReference<>((int)bookImage.imageWidth);
      AtomicReference<Integer> imageHeight = new AtomicReference<>((int)bookImage.imageHeight);
      AtomicReference<Integer> width = new AtomicReference<>((int)bookImage.width);
      AtomicReference<Integer> height = new AtomicReference<>((int)bookImage.height);
      AtomicBoolean flag = new AtomicBoolean(false);
      bookImage.effects
         .forEach(
            bookImageEffect -> {
               if (bookImageEffect.type.equals("hover_change_texture")) {
                  float w = bookImage.width / 330.0F * bookImage.scale / 0.062F;
                  float h = bookImage.height / 330.0F * bookImage.scale / 0.062F;
                  float x = bookImage.x - w / 2.0F + 0.455F;
                  float y = bookImage.y - h / 2.0F + 0.49F;
                  if (canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                     flag.set(true);
                     loc.set(bookImageEffect.hoverImage.texture);
                  }
               }

               if (bookImageEffect.type.equals("hover_overlay")) {
                  float w = bookImage.width / 330.0F * bookImage.scale / 0.062F;
                  float h = bookImage.height / 330.0F * bookImage.scale / 0.062F;
                  float x = bookImage.x - w / 2.0F + 0.45F;
                  float y = bookImage.y - h / 2.0F + 0.49F;
                  if (canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                     overlay_image.set(bookImageEffect.hoverImage);
                     overlay_draw.set(true);
                  }
               }

               if (flag.get()) {
                  bookImageEffect.hoverImage.effects.forEach(bookHoverImageEffect -> {
                     if (bookHoverImageEffect.type.equals("scale")) {
                        float f = bookHoverImageEffect.amount - 1.0F;
                        float xx = f / 2.0F + 1.0F + f / 2.0F * Mth.sin(ClientEvents.getClientTicks() / bookHoverImageEffect.speed);
                        poseStack.scale(xx, xx, xx);
                     }
                  });
                  bookImageEffect.hoverImage
                     .effects
                     .forEach(
                        bookHoverImageEffect -> {
                           if (bookHoverImageEffect.type.equals("tilt")) {
                              poseStack.mulPose(
                                 com.mojang.math.Axis.XP
                                    .rotationDegrees(-bookHoverImageEffect.amount * Mth.sin(ClientEvents.getClientTicks() / bookHoverImageEffect.speed))
                              );
                           }
                        }
                     );
               }
            }
         );
      Matrix4f matrix = poseStack.last().pose();
      Pose normal = poseStack.last();
      float u1 = (u.get().intValue() + 0.0F) / imageWidth.get().intValue();
      float u2 = ((float)u.get().intValue() + width.get().intValue()) / imageWidth.get().intValue();
      float v1 = (v.get().intValue() + 0.0F) / imageHeight.get().intValue();
      float v2 = ((float)v.get().intValue() + height.get().intValue()) / imageHeight.get().intValue();
      float a = 1.0F;
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      if (color != -1) {
         a = (color >> 24 & 0xFF) / 255.0F;
         r = (color >> 16 & 0xFF) / 255.0F;
         g = (color >> 8 & 0xFF) / 255.0F;
         b = (color & 0xFF) / 255.0F;
      }

      VertexConsumer buffer;
      if (a == 1.0F && !transparent) {
         buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(loc.get())));
      } else if ((Boolean)HexConfig.BOOK_SHADERS_TOGGLE.get()) {
         buffer = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(loc.get())));
      } else {
         buffer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(ResourceLocation.parse(loc.get())));
      }

      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height.get().intValue(), -0.0030555555F * width.get().intValue())
         .setColor(r, g, b, a)
         .setUv(u1, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height.get().intValue(), -0.0030555555F * width.get().intValue())
         .setColor(r, g, b, a)
         .setUv(u1, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height.get().intValue(), 0.0030555555F * width.get().intValue())
         .setColor(r, g, b, a)
         .setUv(u2, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height.get().intValue(), 0.0030555555F * width.get().intValue())
         .setColor(r, g, b, a)
         .setUv(u2, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      if (bufferSource instanceof BufferSource multiBufferSource) {
         multiBufferSource.endBatch();
      }

      if (overlay_draw.get()) {
         BookImage ov_img = overlay_image.get();
         VertexConsumer buffer2;
         if (a == 1.0F && !transparent) {
            buffer2 = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(loc.get())));
         } else {
            buffer2 = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(loc.get())));
         }

         float overlay_u1 = (ov_img.u + 0.0F) / ov_img.imageWidth;
         float overlay_u2 = (ov_img.u + ov_img.width) / ov_img.imageWidth;
         float overlay_v1 = (ov_img.v + 0.0F) / ov_img.imageHeight;
         float overlay_v2 = (ov_img.v + ov_img.height) / ov_img.imageHeight;
         float overlay_a = 1.0F;
         float overlay_r = 1.0F;
         float overlay_g = 1.0F;
         float overlay_b = 1.0F;
         if (color != -1) {
            overlay_r = (color >> 16 & 0xFF) / 255.0F;
            overlay_g = (color >> 8 & 0xFF) / 255.0F;
            overlay_b = (color & 0xFF) / 255.0F;
         }

         poseStack.pushPose();
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, -0.0030555555F * ov_img.height, -0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u1, overlay_v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, 0.0030555555F * ov_img.height, -0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u1, overlay_v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, 0.0030555555F * ov_img.height, 0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u2, overlay_v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, -0.0030555555F * ov_img.height, 0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u2, overlay_v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         poseStack.popPose();
         if (bufferSource instanceof BufferSource multiBufferSource) {
            multiBufferSource.endBatch();
         }
      }

      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawPaintColorSlider(
      float offset,
      PaintSystem.ValueSlider valueSlider,
      BookOfShadowsAltarTile altarTile,
      float cursorX,
      float cursorY,
      float scale,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      int color,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      float partial
   ) {
      if (scale != 0.0F) {
         float x;
         float y;
         float width;
         float height;
         if (valueSlider.isHorizontal()) {
            x = valueSlider.getX(pageOn) + valueSlider.getValue() * valueSlider.width / 8.0F + 0.006F;
            y = valueSlider.getY(pageOn) + offset;
            width = 0.85F;
            height = 1.35F;
            height *= valueSlider.getHoveringScale(partial);
         } else {
            x = valueSlider.getX(pageOn) + offset;
            y = valueSlider.getY(pageOn) - valueSlider.getValue() * valueSlider.height / 8.0F + valueSlider.height / 16.0F;
            width = 1.35F;
            height = 0.85F;
            width *= valueSlider.getHoveringScale(partial);
         }

         float width2 = width - 0.6F;
         float height2 = height - 0.6F;
         poseStack.pushPose();
         if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV_PREV) {
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
         }

         if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV) {
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.MIDDLE_BUTTON) {
            this.translateToMiddleButton(altarTile, poseStack, drawingType, transformType);
         }

         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
         poseStack.scale(0.5F * scale * 2.55F, 0.5F * scale * 2.55F, 1.275F);
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
         poseStack.translate((x / 8.1F - 0.05864197F) / (scale * 2.55F), (y / 8.1F - 0.12345678F) / (scale * 2.55F), -zLevel / 1600.0F);
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
         RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
         Matrix4f matrix = poseStack.last().pose();
         Pose normal = poseStack.last();
         float u1 = 0.0F;
         float u2 = 1.0F;
         float v1 = 0.0F;
         float v2 = 1.0F;
         float a = 1.0F;
         float r = 0.0F;
         float g = 0.0F;
         float b = 0.0F;
         VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));
         a = (color >> 24 & 0xFF) / 255.0F;
         r = (color >> 16 & 0xFF) / 255.0F;
         g = (color >> 8 & 0xFF) / 255.0F;
         b = (color & 0xFF) / 255.0F;
         buffer.addVertex(matrix, 1.0E-5F, -0.0030555555F * height2, -0.0030555555F * width2)
            .setColor(r, g, b, a)
            .setUv(u1, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 1.0E-5F, 0.0030555555F * height2, -0.0030555555F * width2)
            .setColor(r, g, b, a)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 1.0E-5F, 0.0030555555F * height2, 0.0030555555F * width2)
            .setColor(r, g, b, a)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 1.0E-5F, -0.0030555555F * height2, 0.0030555555F * width2)
            .setColor(r, g, b, a)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         if (bufferSource instanceof BufferSource multiBufferSource) {
            multiBufferSource.endBatch();
         }

         buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));
         a = 1.0F;
         r = 0.0F;
         g = 0.0F;
         b = 0.0F;
         buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, -0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0030555555F * width)
            .setColor(r, g, b, a)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         if (bufferSource instanceof BufferSource multiBufferSource) {
            multiBufferSource.endBatch();
         }

         poseStack.popPose();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawPaintColors(
      PaintSystem paintSystem,
      BookOfShadowsAltarTile altarTile,
      float cursorX,
      float cursorY,
      float scale,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      float partial
   ) {
      if (!(scale <= 0.0F)) {
         for (int i = 0; i < 3; i++) {
            if (i <= paintSystem.getColors().colors.size()) {
               PaintSystem.Colors.ColorSelection colorSelection = paintSystem.getColors().colors.get(i);
               float x = Mth.lerp(partial, (float)colorSelection.colorPosDataOld.pos.x, (float)colorSelection.colorPosData.pos.x)
                  + (pageOn.isOnLeftSide() ? 0.0F : 0.8F);
               float y = Mth.lerp(partial, (float)colorSelection.colorPosDataOld.pos.y, (float)colorSelection.colorPosData.pos.y);
               float z = Mth.lerp(partial, (float)colorSelection.colorPosDataOld.pos.z, (float)colorSelection.colorPosData.pos.z);
               float width = Mth.lerp(partial, colorSelection.colorPosDataOld.width, colorSelection.colorPosData.width);
               float height = Mth.lerp(partial, colorSelection.colorPosDataOld.height, colorSelection.colorPosData.height);
               float width2 = width - 0.75F;
               float height2 = height - 0.75F;
               poseStack.pushPose();
               if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
                  translateToLeftPage(altarTile, poseStack, drawingType, transformType);
               } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
                  translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
               } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
                  translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
               } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV_PREV) {
                  translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
               }

               if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
                  translateToRightPage(altarTile, poseStack, drawingType, transformType);
               } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
                  translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
               } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
                  translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
               } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV) {
                  translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
               } else if (pageOn == PageDrawing.PageOn.MIDDLE_BUTTON) {
                  this.translateToMiddleButton(altarTile, poseStack, drawingType, transformType);
               }

               poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
               poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
               poseStack.scale(0.5F * scale * 2.55F, 0.5F * scale * 2.55F, 1.275F);
               poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
               poseStack.translate((x / 8.1F - 0.05864197F) / (scale * 2.55F), (y / 8.1F - 0.12345678F) / (scale * 2.55F), -zLevel / 1600.0F);
               poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
               poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
               poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
               RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
               Matrix4f matrix = poseStack.last().pose();
               Pose normal = poseStack.last();
               float u1 = 0.0F;
               float u2 = 1.0F;
               float v1 = 0.0F;
               float v2 = 1.0F;
               int color = colorSelection.getColor();
               VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));
               float a = (color >> 24 & 0xFF) / 255.0F;
               float r = (color >> 16 & 0xFF) / 255.0F;
               float g = (color >> 8 & 0xFF) / 255.0F;
               float b = (color & 0xFF) / 255.0F;
               buffer.addVertex(matrix, 1.0E-4F + z, -0.0030555555F * height2, -0.0030555555F * width2)
                  .setColor(r, g, b, 1.0F)
                  .setUv(u1, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 1.0E-4F + z, 0.0030555555F * height2, -0.0030555555F * width2)
                  .setColor(r, g, b, a)
                  .setUv(u1, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 1.0E-4F + z, 0.0030555555F * height2, 0.0030555555F * width2)
                  .setColor(r, g, b, a)
                  .setUv(u2, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 1.0E-4F + z, -0.0030555555F * height2, 0.0030555555F * width2)
                  .setColor(r, g, b, 1.0F)
                  .setUv(u2, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               if (bufferSource instanceof BufferSource multiBufferSource) {
                  multiBufferSource.endBatch();
               }

               buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank.png")));
               a = 1.0F;
               r = 0.0F;
               g = 0.0F;
               b = 0.0F;
               buffer.addVertex(matrix, 7.0E-5F + z, -0.0030555555F * height, -0.0030555555F * width)
                  .setColor(r, g, b, a)
                  .setUv(u1, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 7.0E-5F + z, 0.0030555555F * height, -0.0030555555F * width)
                  .setColor(r, g, b, a)
                  .setUv(u1, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 7.0E-5F + z, 0.0030555555F * height, 0.0030555555F * width)
                  .setColor(r, g, b, a)
                  .setUv(u2, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 7.0E-5F + z, -0.0030555555F * height, 0.0030555555F * width)
                  .setColor(r, g, b, a)
                  .setUv(u2, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               if (bufferSource instanceof BufferSource multiBufferSource) {
                  multiBufferSource.endBatch();
               }

               poseStack.popPose();
               float w1 = width / 326.0F * 2.55F / 0.062F;
               float h1 = height / 326.0F * 2.55F / 0.062F;
               float x1 = x + 0.025F - w1 / 2.0F;
               float y1 = y - 0.5F - 0.025F - h1 / 2.0F;
               if (canInteract(cursorX, cursorY, x1, y1, w1, h1, altarTile, drawingType)) {
                  List<Component> tooltipList = new ArrayList<>();
                  tooltipList.add(Component.translatable("Cycle Colors").withStyle(ChatFormatting.GRAY));
                  this.tooltipText = tooltipList;
                  this.drawTooltipText = true;
                  this.tooltipStack = ItemStack.EMPTY;
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawPaintColorSliderBar(
      float heightPercent,
      PaintSystem.ValueSlider valueSlider,
      BookOfShadowsAltarTile altarTile,
      float cursorX,
      float cursorY,
      float scale,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      int color1,
      int color2,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      boolean hueSlider,
      float partial
   ) {
      if (scale != 0.0F) {
         float x = valueSlider.getX(pageOn);
         float y = valueSlider.getY(pageOn);
         float width = valueSlider.width;
         float height = valueSlider.height * heightPercent;
         poseStack.pushPose();
         if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV_PREV) {
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
         }

         if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV) {
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.MIDDLE_BUTTON) {
            this.translateToMiddleButton(altarTile, poseStack, drawingType, transformType);
         }

         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
         poseStack.scale(0.5F * scale * 2.55F, 0.5F * scale * 2.55F, 1.275F);
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
         poseStack.translate((x / 8.1F - 0.05864197F) / (scale * 2.55F), (y / 8.1F - 0.12345678F) / (scale * 2.55F), -zLevel / 1600.0F);
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
         RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
         Matrix4f matrix = poseStack.last().pose();
         Pose normal = poseStack.last();
         float u1 = 0.0F;
         float u2 = 1.0F;
         float v1 = 0.0F;
         float v2 = 1.0F;
         float a1 = 1.0F;
         float r1 = 1.0F;
         float g1 = 1.0F;
         float b1 = 1.0F;
         if (color1 != -1) {
            a1 = (color1 >> 24 & 0xFF) / 255.0F;
            r1 = (color1 >> 16 & 0xFF) / 255.0F;
            g1 = (color1 >> 8 & 0xFF) / 255.0F;
            b1 = (color1 & 0xFF) / 255.0F;
         }

         float a2 = 1.0F;
         float r2 = 1.0F;
         float g2 = 1.0F;
         float b2 = 1.0F;
         if (color2 != -1) {
            a2 = (color2 >> 24 & 0xFF) / 255.0F;
            r2 = (color2 >> 16 & 0xFF) / 255.0F;
            g2 = (color2 >> 8 & 0xFF) / 255.0F;
            b2 = (color2 & 0xFF) / 255.0F;
         }

         VertexConsumer buffer;
         if (hueSlider) {
            float bubblePosX = valueSlider.getValue();
            float bubblePosY = 0.5F;
            float bubbleRadius = 0.275F;
            float bubbleStrength = valueSlider.getHoveringScale(partial) - 1.0F;
            if (!valueSlider.isHorizontal()) {
               bubblePosY = 1.0F - valueSlider.getValue();
               bubblePosX = 0.5F;
            }

            if ((Boolean)HexConfig.BOOK_SHADERS_TOGGLE.get()) {
               ClientEvents.hueSliderShader.safeGetUniform("bubblePos").set(bubblePosX, bubblePosY);
               ClientEvents.hueSliderShader.safeGetUniform("bubbleRadius").set(bubbleRadius);
               ClientEvents.hueSliderShader.safeGetUniform("bubbleStrength").set(bubbleStrength);
               ClientEvents.hueSliderShader.safeGetUniform("isHorizontal").set(valueSlider.isHorizontal() ? 1 : 0);
               buffer = bufferSource.getBuffer(ModRenderTypes.hueSlider(ResourceLocation.parse("hexerei:textures/book/blank.png")));
            } else {
               buffer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/hue_slider.png")));
            }
         } else {
            float bubblePosXx = valueSlider.getValue();
            float bubblePosYx = 0.5F;
            float bubbleRadiusx = 0.275F;
            float bubbleStrengthx = valueSlider.getHoveringScale(partial) - 1.0F;
            if (!valueSlider.isHorizontal()) {
               bubblePosYx = 1.0F - valueSlider.getValue();
               bubblePosXx = 0.5F;
            }

            if ((Boolean)HexConfig.BOOK_SHADERS_TOGGLE.get()) {
               ClientEvents.sliderShader.safeGetUniform("bubblePos").set(bubblePosXx, bubblePosYx);
               ClientEvents.sliderShader.safeGetUniform("bubbleRadius").set(bubbleRadiusx);
               ClientEvents.sliderShader.safeGetUniform("bubbleStrength").set(bubbleStrengthx);
               ClientEvents.sliderShader.safeGetUniform("isHorizontal").set(valueSlider.isHorizontal() ? 1 : 0);
               buffer = bufferSource.getBuffer(ModRenderTypes.slider(ResourceLocation.parse("hexerei:textures/book/blank.png")));
            } else if (valueSlider.isHorizontal()) {
               buffer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank_slider.png")));
            } else {
               buffer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(ResourceLocation.parse("hexerei:textures/book/blank_slider_v.png")));
            }
         }

         if (hueSlider) {
            if (valueSlider.isHorizontal()) {
               buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u1, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u1, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.006111111F * width)
                  .setColor(255, 255, 255, 255)
                  .setUv(u2, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.006111111F * width)
                  .setColor(255, 255, 255, 255)
                  .setUv(u2, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
            } else {
               buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u1, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0F)
                  .setColor(255, 255, 255, 255)
                  .setUv(u1, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.006111111F * width)
                  .setColor(255, 255, 255, 255)
                  .setUv(u2, v2)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
               buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.006111111F * width)
                  .setColor(255, 255, 255, 255)
                  .setUv(u2, v1)
                  .setOverlay(overlay)
                  .setLight(light)
                  .setNormal(normal, 1.0F, 0.0F, 0.0F);
            }
         } else if (valueSlider.isHorizontal()) {
            buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0F)
               .setColor(r1, g1, b1, a1)
               .setUv(u1, v1)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
            buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0F)
               .setColor(r1, g1, b1, a1)
               .setUv(u1, v2)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
            buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.006111111F * width)
               .setColor(r2, g2, b2, a2)
               .setUv(u2, v2)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
            buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.006111111F * width)
               .setColor(r2, g2, b2, a2)
               .setUv(u2, v1)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
         } else {
            buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0F)
               .setColor(r2, g2, b2, a2)
               .setUv(u1, v1)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
            buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0F)
               .setColor(r1, g1, b1, a1)
               .setUv(u1, v2)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
            buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.006111111F * width)
               .setColor(r1, g1, b1, a1)
               .setUv(u2, v2)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
            buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.006111111F * width)
               .setColor(r2, g2, b2, a2)
               .setUv(u2, v1)
               .setOverlay(overlay)
               .setLight(light)
               .setNormal(normal, 1.0F, 0.0F, 0.0F);
         }

         poseStack.popPose();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawPaintElement(
      BookPaintElement paintElement,
      BookOfShadowsAltarTile altarTile,
      float cursorX,
      float cursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      int color,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType,
      float partial
   ) {
      if (paintElement.client == null) {
         paintElement.client = new BookPaintElement.Client(paintElement);
      } else {
         BookData bookData = altarTile.currentBook;
         PaintSystem paintSystem = paintElement.client.getPaintSystem(bookData.getUUID());
         paintSystem.shouldTick = true;
         poseStack.pushPose();
         if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
            translateToLeftPage(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
            translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV_PREV) {
            translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
         }

         if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
            translateToRightPage(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
            translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
            translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV) {
            translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
         } else if (pageOn == PageDrawing.PageOn.MIDDLE_BUTTON) {
            this.translateToMiddleButton(altarTile, poseStack, drawingType, transformType);
         }

         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
         poseStack.scale(0.5F * paintElement.scale * 2.55F, 0.5F * paintElement.scale * 2.55F, 1.275F);
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
         poseStack.translate(
            (paintElement.x / 8.1F - 0.05864197F) / (paintElement.scale * 2.55F) - (pageOn.isOnLeftSide() ? 0.0F : 0.0072621643F),
            (paintElement.y / 8.1F - 0.12345678F) / (paintElement.scale * 2.55F),
            -(zLevel + paintElement.z) / 1600.0F
         );
         poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
         RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
         AtomicReference<Integer> width = new AtomicReference<>((int)paintElement.width);
         AtomicReference<Integer> height = new AtomicReference<>((int)paintElement.height);
         Matrix4f matrix = poseStack.last().pose();
         Pose normal = poseStack.last();
         float u1 = 0.0F;
         float u2 = 1.0F;
         float v1 = 0.0F;
         float v2 = 1.0F;
         float a = 1.0F;
         float r = 1.0F;
         float g = 1.0F;
         float b = 1.0F;
         if (color != -1) {
            a = (color >> 24 & 0xFF) / 255.0F;
            r = (color >> 16 & 0xFF) / 255.0F;
            g = (color >> 8 & 0xFF) / 255.0F;
            b = (color & 0xFF) / 255.0F;
         }

         VertexConsumer buffer;
         if ((Boolean)HexConfig.BOOK_SHADERS_TOGGLE.get()) {
            buffer = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(paintSystem.getImageLocation()));
         } else {
            buffer = bufferSource.getBuffer(ModRenderTypes.entityTranslucent(paintSystem.getImageLocation()));
         }

         buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.006111111F * height.get().intValue(), 0.0F)
            .setColor(r, g, b, a)
            .setUv(u1, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.006111111F * height.get().intValue(), 0.006111111F * width.get().intValue())
            .setColor(r, g, b, a)
            .setUv(u2, v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer.addVertex(matrix, 0.0F, 0.0F, 0.006111111F * width.get().intValue())
            .setColor(r, g, b, a)
            .setUv(u2, v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         if (bufferSource instanceof BufferSource multiBufferSource) {
            multiBufferSource.endBatch();
         }

         poseStack.popPose();
         if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
            float w = paintElement.width / 326.0F * 2.55F * paintElement.scale / 0.062F;
            float h = paintElement.height / 326.0F * 2.55F * paintElement.scale / 0.062F;
            float x = paintElement.x + 0.025F;
            float y = paintElement.y - 0.5F;
            if (paintSystem.toolsVisible
               && canInteract(
                  cursorX,
                  cursorY,
                  x - paintElement.width * paintElement.scale * 0.24F,
                  y - paintElement.height * paintElement.scale * 0.24F,
                  w + paintElement.width * paintElement.scale * 0.24F * 2.0F,
                  h + paintElement.height * paintElement.scale * 0.24F * 2.0F,
                  altarTile,
                  drawingType
               )) {
               float xPixel = (cursorX - x) / w * paintElement.width;
               float yPixel = (cursorY - y) / h * paintElement.height;
               paintSystem.hover(xPixel, yPixel);
            }

            this.drawPaintColors(
               paintSystem,
               altarTile,
               cursorX,
               cursorY,
               altarTile.buttonScaleRender * paintSystem.getColorsVisibility(partial),
               poseStack,
               bufferSource,
               0.0F,
               light,
               overlay,
               pageOn,
               drawingType,
               ItemDisplayContext.NONE,
               partial
            );

            for (PaintSystem.ValueSlider slider : paintSystem.getValueSliders().getSliders()) {
               if (slider.shouldRender(paintSystem)) {
                  if (slider.isDragging() && slider.isVisible(paintSystem)) {
                     slider.updateValue(cursorX, cursorY, pageOn);
                  }

                  this.drawPaintColorSlider(
                     slider.isHorizontal() ? 0.0F : slider.width / 326.0F * 5.0F * 4.1F,
                     slider,
                     altarTile,
                     cursorX,
                     cursorY,
                     altarTile.buttonScaleRender * slider.getVisibility(partial),
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     pageOn,
                     slider.getSliderColor(paintSystem),
                     drawingType,
                     ItemDisplayContext.NONE,
                     partial
                  );
                  this.drawPaintColorSliderBar(
                     1.0F,
                     slider,
                     altarTile,
                     cursorX,
                     cursorY,
                     altarTile.buttonScaleRender * slider.getVisibility(partial),
                     poseStack,
                     bufferSource,
                     -0.1F,
                     light,
                     overlay,
                     pageOn,
                     slider.getColor1(paintSystem),
                     slider.getColor2(paintSystem),
                     drawingType,
                     ItemDisplayContext.NONE,
                     slider.isSpecialHueSlider(),
                     partial
                  );
                  w = slider.width / 326.0F * 2.55F / 0.062F;
                  h = slider.height / 326.0F * 2.55F / 0.062F;
                  x = slider.getX(pageOn) + 0.025F;
                  y = slider.getY(pageOn) - 0.5F - h / 2.0F;
                  if (slider.isVisible(paintSystem) && (slider.isDragging() || canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType))) {
                     slider.setHovering();
                     List<Component> tooltipList = new ArrayList<>();
                     tooltipList.add(slider.getTooltip(paintSystem));
                     this.tooltipText = tooltipList;
                     this.drawTooltipText = true;
                     this.tooltipStack = ItemStack.EMPTY;
                  }
               }
            }

            ArrayList<BookImageEffect> effects = new ArrayList<>(List.of(new BookImageEffect("scale", 50.0F, 1.25F), new BookImageEffect("tilt", 35.0F, 10.0F)));

            for (PaintSystem.Button button : paintSystem.buttons) {
               if (button.shouldRender(paintSystem) && button.selected.apply(paintSystem)) {
                  BookImage selectedTool = new BookImage(
                     button.getX(paintSystem, pageOn, partial),
                     button.getY(paintSystem, pageOn, partial),
                     0.0F,
                     0.0F,
                     0.0F,
                     button.width,
                     button.height,
                     button.width,
                     button.height,
                     Math.max(0.0F, button.getScale(altarTile.buttonScaleRender) * button.getVisibility(partial)),
                     "hexerei:textures/book/paint_tools/tool_selected.png",
                     new ArrayList<>()
                  );
                  this.drawImage(selectedTool, altarTile, cursorX, cursorY, poseStack, bufferSource, 0.0F, light, overlay, pageOn, drawingType);
               }
            }

            for (PaintSystem.Button buttonx : paintSystem.buttons) {
               if (buttonx.shouldRender(paintSystem)) {
                  boolean disabled = buttonx.getDisabled(paintSystem);
                  BookImage image = new BookImage(
                     buttonx.getX(paintSystem, pageOn, partial),
                     buttonx.getY(paintSystem, pageOn, partial),
                     0.0F,
                     0.0F,
                     0.0F,
                     buttonx.width,
                     buttonx.height,
                     buttonx.width,
                     buttonx.height,
                     Math.max(0.0F, buttonx.getScale(altarTile.buttonScaleRender) * buttonx.getVisibility(partial)),
                     disabled ? buttonx.getDisabledTexture(paintSystem) : buttonx.getTexture(paintSystem),
                     new ArrayList<>()
                  );
                  BookImage imageHover = new BookImage(
                     buttonx.getX(paintSystem, pageOn, partial),
                     buttonx.getY(paintSystem, pageOn, partial),
                     0.0F,
                     0.0F,
                     0.0F,
                     buttonx.width,
                     buttonx.height,
                     buttonx.width,
                     buttonx.height,
                     Math.max(0.0F, buttonx.getScale(altarTile.buttonScaleRender) * buttonx.getVisibility(partial)),
                     buttonx.getHoverTexture(paintSystem),
                     effects
                  );
                  boolean drawHover = false;
                  w = image.width / 330.0F * image.scale / 0.062F;
                  h = image.height / 330.0F * image.scale / 0.062F;
                  x = image.x - w / 2.0F + 0.455F;
                  y = image.y - h / 2.0F + 0.49F;
                  if (buttonx.clickedScale != 1.0F) {
                     drawHover = true;
                  }

                  if (buttonx.isVisible(paintSystem) && canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                     drawHover = true;
                     if (!buttonx.getTooltipList().isEmpty()) {
                        this.tooltipText = buttonx.getTooltipList();
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                     }
                  }

                  if (drawHover && !disabled) {
                     this.drawImage(
                        imageHover,
                        altarTile,
                        cursorX,
                        cursorY,
                        poseStack,
                        bufferSource,
                        0.1F,
                        light,
                        overlay,
                        pageOn,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE,
                        true
                     );
                  } else {
                     this.drawImage(
                        image,
                        altarTile,
                        cursorX,
                        cursorY,
                        poseStack,
                        bufferSource,
                        0.1F,
                        light,
                        overlay,
                        pageOn,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE,
                        true
                     );
                  }
               }
            }
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
            float w = paintElement.width / 326.0F * 2.55F * paintElement.scale / 0.062F;
            float h = paintElement.height / 326.0F * 2.55F * paintElement.scale / 0.062F;
            float x = paintElement.x + 0.025F;
            float y = paintElement.y - 0.5F;
            if (paintSystem.toolsVisible
               && canInteract(
                  cursorX + (pageOn.isOnLeftSide() ? 0.0F : 0.22F),
                  cursorY,
                  x - paintElement.width * paintElement.scale * 0.24F,
                  y - paintElement.height * paintElement.scale * 0.24F,
                  w + paintElement.width * paintElement.scale * 0.24F * 2.0F,
                  h + paintElement.height * paintElement.scale * 0.24F * 2.0F,
                  altarTile,
                  drawingType
               )) {
               float xPixel = (cursorX - x) / w * paintElement.width + (pageOn.isOnLeftSide() ? 0.0F : 1.8F);
               float yPixel = (cursorY - y) / h * paintElement.height;
               paintSystem.hover(xPixel, yPixel);
            }

            this.drawPaintColors(
               paintSystem,
               altarTile,
               cursorX,
               cursorY,
               altarTile.buttonScaleRender * paintSystem.getColorsVisibility(partial),
               poseStack,
               bufferSource,
               0.0F,
               light,
               overlay,
               pageOn,
               drawingType,
               ItemDisplayContext.NONE,
               partial
            );

            for (PaintSystem.ValueSlider sliderx : paintSystem.getValueSliders().getSliders()) {
               if (sliderx.shouldRender(paintSystem)) {
                  if (sliderx.isDragging() && sliderx.isVisible(paintSystem)) {
                     sliderx.updateValue(cursorX, cursorY, pageOn);
                  }

                  this.drawPaintColorSlider(
                     sliderx.isHorizontal() ? 0.0F : sliderx.width / 326.0F * 5.0F * 4.1F,
                     sliderx,
                     altarTile,
                     cursorX,
                     cursorY,
                     altarTile.buttonScaleRender * sliderx.getVisibility(partial),
                     poseStack,
                     bufferSource,
                     0.0F,
                     light,
                     overlay,
                     pageOn,
                     sliderx.getSliderColor(paintSystem),
                     drawingType,
                     ItemDisplayContext.NONE,
                     partial
                  );
                  this.drawPaintColorSliderBar(
                     1.0F,
                     sliderx,
                     altarTile,
                     cursorX,
                     cursorY,
                     altarTile.buttonScaleRender * sliderx.getVisibility(partial),
                     poseStack,
                     bufferSource,
                     -0.1F,
                     light,
                     overlay,
                     pageOn,
                     sliderx.getColor1(paintSystem),
                     sliderx.getColor2(paintSystem),
                     drawingType,
                     ItemDisplayContext.NONE,
                     sliderx.isSpecialHueSlider(),
                     partial
                  );
                  w = sliderx.width / 326.0F * 2.55F / 0.062F;
                  h = sliderx.height / 326.0F * 2.55F / 0.062F;
                  x = sliderx.getX(pageOn) + 0.025F - (pageOn.isOnLeftSide() ? 0.0F : 0.09F);
                  y = sliderx.getY(pageOn) - 0.5F - h / 2.0F;
                  if (sliderx.isVisible(paintSystem) && (sliderx.isDragging() || canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType))) {
                     sliderx.setHovering();
                     List<Component> tooltipList = new ArrayList<>();
                     tooltipList.add(sliderx.getTooltip(paintSystem));
                     this.tooltipText = tooltipList;
                     this.drawTooltipText = true;
                     this.tooltipStack = ItemStack.EMPTY;
                  }
               }
            }

            ArrayList<BookImageEffect> effects = new ArrayList<>(List.of(new BookImageEffect("scale", 50.0F, 1.25F), new BookImageEffect("tilt", 35.0F, 10.0F)));

            for (PaintSystem.Button buttonxx : paintSystem.buttons) {
               if (buttonxx.shouldRender(paintSystem) && buttonxx.selected.apply(paintSystem)) {
                  BookImage selectedTool = new BookImage(
                     buttonxx.getX(paintSystem, pageOn, partial),
                     buttonxx.getY(paintSystem, pageOn, partial),
                     0.0F,
                     0.0F,
                     0.0F,
                     buttonxx.width,
                     buttonxx.height,
                     buttonxx.width,
                     buttonxx.height,
                     Math.max(0.0F, buttonxx.getScale(altarTile.buttonScaleRender) * buttonxx.getVisibility(partial)),
                     "hexerei:textures/book/paint_tools/tool_selected.png",
                     new ArrayList<>()
                  );
                  this.drawImage(selectedTool, altarTile, cursorX, cursorY, poseStack, bufferSource, 0.0F, light, overlay, pageOn, drawingType);
               }
            }

            for (PaintSystem.Button buttonxxx : paintSystem.buttons) {
               if (buttonxxx.shouldRender(paintSystem)) {
                  boolean disabledx = buttonxxx.getDisabled(paintSystem);
                  BookImage imagex = new BookImage(
                     buttonxxx.getX(paintSystem, pageOn, partial),
                     buttonxxx.getY(paintSystem, pageOn, partial),
                     0.0F,
                     0.0F,
                     0.0F,
                     buttonxxx.width,
                     buttonxxx.height,
                     buttonxxx.width,
                     buttonxxx.height,
                     Math.max(0.0F, buttonxxx.getScale(altarTile.buttonScaleRender) * buttonxxx.getVisibility(partial)),
                     disabledx ? buttonxxx.getDisabledTexture(paintSystem) : buttonxxx.getTexture(paintSystem),
                     new ArrayList<>()
                  );
                  BookImage imageHoverx = new BookImage(
                     buttonxxx.getX(paintSystem, pageOn, partial),
                     buttonxxx.getY(paintSystem, pageOn, partial),
                     0.0F,
                     0.0F,
                     0.0F,
                     buttonxxx.width,
                     buttonxxx.height,
                     buttonxxx.width,
                     buttonxxx.height,
                     Math.max(0.0F, buttonxxx.getScale(altarTile.buttonScaleRender) * buttonxxx.getVisibility(partial)),
                     buttonxxx.getHoverTexture(paintSystem),
                     effects
                  );
                  boolean drawHoverx = false;
                  w = imagex.width / 330.0F * imagex.scale / 0.062F;
                  h = imagex.height / 330.0F * imagex.scale / 0.062F;
                  x = imagex.x - w / 2.0F + 0.455F;
                  y = imagex.y - h / 2.0F + 0.49F;
                  if (buttonxxx.clickedScale != 1.0F) {
                     drawHoverx = true;
                  }

                  if (buttonxxx.isVisible(paintSystem) && canInteract(cursorX, cursorY, x, y, w, h, altarTile, drawingType)) {
                     drawHoverx = true;
                     if (!buttonxxx.getTooltipList().isEmpty()) {
                        this.tooltipText = buttonxxx.getTooltipList();
                        this.drawTooltipText = true;
                        this.tooltipStack = ItemStack.EMPTY;
                     }
                  }

                  if (drawHoverx && !disabledx) {
                     this.drawImage(
                        imageHoverx,
                        altarTile,
                        cursorX,
                        cursorY,
                        poseStack,
                        bufferSource,
                        0.1F,
                        light,
                        overlay,
                        pageOn,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE,
                        true
                     );
                  } else {
                     this.drawImage(
                        imagex,
                        altarTile,
                        cursorX,
                        cursorY,
                        poseStack,
                        bufferSource,
                        0.1F,
                        light,
                        overlay,
                        pageOn,
                        -1,
                        drawingType,
                        ItemDisplayContext.NONE,
                        true
                     );
                  }
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawBasePage(
      BookImage bookImage,
      BookOfShadowsAltarTile altarTile,
      float leftCursorX,
      float leftCursorY,
      float rightCursorX,
      float rightCursorY,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      int color,
      PageDrawing.DrawingType drawingType,
      ItemDisplayContext transformType
   ) {
      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV_PREV) {
         translateToLeftPagePrevious2(altarTile, poseStack, drawingType, transformType);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV_PREV) {
         translateToRightPagePrevious2(altarTile, poseStack, drawingType, transformType);
      } else if (pageOn == PageDrawing.PageOn.MIDDLE_BUTTON) {
         this.translateToMiddleButton(altarTile, poseStack, drawingType, transformType);
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
      poseStack.scale(0.5F * bookImage.scale, 0.5F * bookImage.scale, 0.5F * bookImage.scale);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.translate(
         (bookImage.x / 8.1F - 0.001875F) / bookImage.scale, (bookImage.y / 8.1F - 0.0033125F) / bookImage.scale, -(zLevel + bookImage.z) / 1600.0F
      );
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
      AtomicReference<String> loc = new AtomicReference<>(bookImage.texture);
      AtomicReference<BookImage> overlay_image = new AtomicReference<>(bookImage);
      AtomicReference<Boolean> overlay_draw = new AtomicReference<>(false);
      int u = (int)bookImage.u;
      int v = (int)bookImage.v;
      int imageWidth = (int)bookImage.imageWidth;
      int imageHeight = (int)bookImage.imageHeight;
      int width = (int)bookImage.width;
      int height = (int)bookImage.height;
      Matrix4f matrix = poseStack.last().pose();
      Pose normal = poseStack.last();
      float u1 = 0.0F;
      float u2 = (float)imageWidth / Mth.abs(imageWidth);
      float v1 = 0.0F;
      float v2 = (float)imageHeight / Mth.abs(imageHeight);
      float a = 1.0F;
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      if (color != -1) {
         a = (color >> 24 & 0xFF) / 255.0F;
         r = (color >> 16 & 0xFF) / 255.0F;
         g = (color >> 8 & 0xFF) / 255.0F;
         b = (color & 0xFF) / 255.0F;
      }

      VertexConsumer buffer;
      if (a != 1.0F) {
         buffer = bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.parse(loc.get())));
      } else {
         buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse(loc.get())));
      }

      if ((Boolean)HexConfig.BOOK_SHADERS_TOGGLE.get()) {
         buffer = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(loc.get())));
      }

      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, -0.0030555555F * width)
         .setColor(r, g, b, a)
         .setUv(u1, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, -0.0030555555F * width)
         .setColor(r, g, b, a)
         .setUv(u1, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0030555555F * width)
         .setColor(r, g, b, a)
         .setUv(u2, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0030555555F * width)
         .setColor(r, g, b, a)
         .setUv(u2, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      if (bufferSource instanceof BufferSource multiBufferSource) {
         multiBufferSource.endBatch();
      }

      if (overlay_draw.get()) {
         BookImage ov_img = overlay_image.get();
         VertexConsumer buffer2 = bufferSource.getBuffer(ModRenderTypes.bookTranslucent(ResourceLocation.parse(ov_img.texture)));
         float overlay_u1 = (ov_img.u + 0.0F) / ov_img.imageWidth;
         float overlay_u2 = (ov_img.u + ov_img.width) / ov_img.imageWidth;
         float overlay_v1 = (ov_img.v + 0.0F) / ov_img.imageHeight;
         float overlay_v2 = (ov_img.v + ov_img.height) / ov_img.imageHeight;
         float overlay_a = 1.0F;
         float overlay_r = 1.0F;
         float overlay_g = 1.0F;
         float overlay_b = 1.0F;
         if (color != -1) {
            overlay_r = (color >> 16 & 0xFF) / 255.0F;
            overlay_g = (color >> 8 & 0xFF) / 255.0F;
            overlay_b = (color & 0xFF) / 255.0F;
         }

         poseStack.pushPose();
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, -0.0030555555F * ov_img.height, -0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u1, overlay_v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, 0.0030555555F * ov_img.height, -0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u1, overlay_v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, 0.0030555555F * ov_img.height, 0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u2, overlay_v2)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         buffer2.addVertex(matrix, ov_img.z / 2000.0F, -0.0030555555F * ov_img.height, 0.0030555555F * ov_img.width)
            .setColor(overlay_r, overlay_g, overlay_b, overlay_a)
            .setUv(overlay_u2, overlay_v1)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(normal, 1.0F, 0.0F, 0.0F);
         poseStack.popPose();
         if (bufferSource instanceof BufferSource multiBufferSource) {
            multiBufferSource.endBatch();
         }
      }

      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTitle(
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      poseStack.pushPose();
      if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
         translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
         translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
         translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
         translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
         translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
         translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
      }

      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.translate(-0.5F, 0.34375F, -7.5E-4F);
      poseStack.scale(0.5F, 0.5F, 0.5F);
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
      poseStack.translate(-0.001875F, -0.0033125F, 0.0F);
      poseStack.translate(0.296875F, 0.0F, 0.0F);
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0F));
      RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
      Matrix4f matrix = poseStack.last().pose();
      VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/title.png")));
      Pose normal = poseStack.last();
      int u = 0;
      int v = 0;
      int imageWidth = 128;
      int imageHeight = 128;
      int width = 100;
      int height = 26;
      float u1 = (u + 0.0F) / imageWidth;
      float u2 = ((float)u + width) / imageWidth;
      float v1 = (v + 0.0F) / imageHeight;
      float v2 = ((float)v + height) / imageHeight;
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, -0.0030555555F * width)
         .setColor(255, 255, 255, 255)
         .setUv(u1, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, -0.0030555555F * width)
         .setColor(255, 255, 255, 255)
         .setUv(u1, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, 0.0030555555F * height, 0.0030555555F * width)
         .setColor(255, 255, 255, 255)
         .setUv(u2, v2)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      buffer.addVertex(matrix, 0.0F, -0.0030555555F * height, 0.0030555555F * width)
         .setColor(255, 255, 255, 255)
         .setUv(u2, v1)
         .setOverlay(overlay)
         .setLight(light)
         .setNormal(normal, 1.0F, 0.0F, 0.0F);
      poseStack.popPose();
   }

   public void resetLines() {
      this.lineWidth = 0.0F;
      this.lineHeight = 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public BookParagraphElements resetLinesNewBox(List<BookParagraphElements> elements, int boxOn) {
      this.lineWidth = 0.0F;
      this.lineHeight = 0.0F;
      return boxOn + 1 < elements.size() ? elements.get(boxOn + 1) : null;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawString(
      BookParagraph bookParagraph,
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float mouseX,
      float mouseY,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      if (!bookParagraph.paragraphElements.isEmpty()) {
         MutableComponent pageText = bookParagraph.translatablePassage;
         int wordNumber = -1;
         int boxOn = 0;
         BookParagraphElements activeElement = (BookParagraphElements)bookParagraph.paragraphElements.getFirst();
         Font font = ClientProxy.font();
         boolean findNewWord = true;
         String[] words = pageText.getString().trim().split("(\\s+)");
         String pageTextString = pageText.getString();
         int itor = -1;

         for (String word : words) {
            itor++;
            if (word.length() > 2 && word.charAt(0) == '%' && word.charAt(1) == 'k') {
               String temp = word.substring(2);
               String[] temp2 = temp.split("%");
               temp = temp2[0];
               String alt = "key." + temp;

               for (KeyMapping k : ClientProxy.keys) {
                  String name = k.getName();
                  if (name.equals(temp) || name.equals(alt)) {
                     String keyName = k.getTranslatedKeyMessage().getString();
                     if (keyName.length() <= 1) {
                        keyName = keyName.toUpperCase(Locale.ROOT);
                     }

                     words[itor] = keyName + (temp2.length > 1 ? temp2[1] : "");
                     pageTextString = pageTextString.replaceAll(word, words[itor]);
                  }
               }
            }
         }

         List<String> combinedList = new ArrayList<>();

         for (int i = 0; i < words.length; i++) {
            if (words[i].equals(",") && i != 0) {
               combinedList.set(combinedList.size() - 1, (String)combinedList.getLast() + words[i]);
            } else {
               combinedList.add(words[i]);
            }
         }

         words = combinedList.toArray(new String[0]);
         char[] text = pageTextString.toCharArray();
         int[] wordLength = new int[words.length];
         float[] wordWidths = new float[words.length];

         for (int kx = 0; kx < words.length; kx++) {
            wordLength[kx] = words[kx].length();
            wordWidths[kx] = font.width(words[kx]);
         }

         boolean breakBool = false;
         ArrayList<String> strings = new ArrayList<>();
         StringBuilder stringBuilder = new StringBuilder();

         for (int ix = 0; ix < text.length && !breakBool; ix = ix) {
            if (text[ix] == '\n') {
               this.lineWidth = 0.0F;
               this.lineHeight++;
               strings.add(stringBuilder.toString());
               stringBuilder = new StringBuilder();
               if (this.lineHeight >= activeElement.height) {
                  activeElement = this.resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                  if (activeElement == null) {
                     breakBool = true;
                     break;
                  }
               }

               ix++;
            } else if (text[ix] == ' ') {
               findNewWord = true;
               stringBuilder.append(' ');
               this.lineWidth = this.lineWidth + font.width(" ");
               if (this.lineWidth > activeElement.width * 3.75F) {
                  this.lineWidth = 0.0F;
                  this.lineHeight++;
                  strings.add(stringBuilder.toString());
                  stringBuilder = new StringBuilder();
                  if (this.lineHeight >= activeElement.height) {
                     activeElement = this.resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                     if (activeElement == null) {
                        breakBool = true;
                        break;
                     }
                  }
               }

               ix++;
            } else if (findNewWord) {
               char[] wordText = words[++wordNumber].toCharArray();
               if (this.lineWidth > 0.0F && this.lineWidth + wordWidths[wordNumber] > activeElement.width * 3.75F) {
                  this.lineWidth = 0.0F;
                  this.lineHeight++;
                  strings.add(stringBuilder.toString());
                  stringBuilder = new StringBuilder();
                  if (this.lineHeight >= activeElement.height) {
                     activeElement = this.resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                     if (activeElement == null) {
                        breakBool = true;
                        break;
                     }
                  }
               }

               for (char character : wordText) {
                  stringBuilder.append(character);
                  this.lineWidth = this.lineWidth + font.width(String.valueOf(character));
                  if (this.lineWidth > activeElement.width * 3.75F) {
                     this.lineWidth = 0.0F;
                     this.lineHeight++;
                     strings.add(stringBuilder.toString());
                     stringBuilder = new StringBuilder();
                     if (this.lineHeight >= activeElement.height) {
                        activeElement = this.resetLinesNewBox(bookParagraph.paragraphElements, boxOn++);
                        if (activeElement == null) {
                           breakBool = true;
                           break;
                        }
                     }
                  }
               }

               ix += wordLength[wordNumber];
            }
         }

         if (!stringBuilder.toString().isEmpty()) {
            strings.add(stringBuilder.toString());
         }

         poseStack.pushPose();
         if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         }

         if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         }

         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.translate(-0.521875F, 0.28125F, -6.25E-4F);
         poseStack.scale(0.00272F, 0.00272F, 0.00272F);
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
         BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
         int boxId = 0;
         int linenumber = 0;
         boolean flag = true;

         while (flag) {
            ArrayList<String> remainder = new ArrayList<>();
            if (bookParagraph.paragraphElements.size() > boxId && bookParagraph.paragraphElements.get(boxId) != null) {
               BookParagraphElements box = bookParagraph.paragraphElements.get(boxId);
               boolean alignVerticalMiddle = box.verticalAlign.equals("middle");
               float offsetY = 0.0F;
               if (alignVerticalMiddle && Math.round(box.height * 9.0F) + 1 > strings.size()) {
                  offsetY = box.height * 9.0F / 2.0F - strings.size() / 2.0F * 9.0F;
               }

               for (String s1 : strings) {
                  if ((linenumber + 1) * 9 <= Math.round(box.height * 9.0F) + 1) {
                     float offsetX = 0.0F;
                     if (bookParagraph.align.equals("middle")) {
                        offsetX = font.width(s1) / 2;
                     }

                     MutableComponent component = Component.literal(s1);
                     if (ClientProxy.fontId() != null) {
                        component = component.withStyle(Style.EMPTY.withFont(ClientProxy.fontId()));
                     }

                     float var69 = box.x * 8.0F - 24.0F - offsetX;
                     Minecraft.getInstance()
                        .font
                        .drawInBatch(
                           component,
                           var69,
                           box.y * 9.0F + linenumber * 9 - 4.0F + offsetY,
                           HexereiUtil.getColorValue(0.12F, 0.12F, 0.12F),
                           false,
                           poseStack.last().pose(),
                           bufferSource,
                           DisplayMode.NORMAL,
                           0,
                           light
                        );
                     poseStack.pushPose();
                     poseStack.translate(0.25F, 0.25F, 0.0625F);
                     var69 = box.x * 8.0F - 24.0F - offsetX;
                     Minecraft.getInstance()
                        .font
                        .drawInBatch(
                           component,
                           var69,
                           box.y * 9.0F + linenumber * 9 - 4.0F + offsetY,
                           16777216,
                           false,
                           poseStack.last().pose(),
                           bufferSource,
                           DisplayMode.NORMAL,
                           0,
                           light
                        );
                     poseStack.popPose();
                  } else {
                     remainder.add(s1);
                  }

                  linenumber++;
               }
            } else {
               flag = false;
            }

            if (remainder.isEmpty()) {
               flag = false;
            } else {
               boxId++;
               linenumber = 0;
               strings = remainder;
            }
         }

         buffer.endBatch();
         poseStack.popPose();
         this.resetLines();
      }
   }

   private void renderHighlight(
      Rect2i[] highlightAreas, PoseStack poseStack, MultiBufferSource bufferSource, int overlay, int light, float xOffset, float yOffset
   ) {
      for (Rect2i rect2i : highlightAreas) {
         float[] col = HexereiUtil.rgbaIntToFloatArray(-16776961);
         fill(
            RenderType.guiTextHighlight(),
            poseStack,
            bufferSource,
            rect2i.getX() + xOffset,
            rect2i.getY() + yOffset,
            0.0F,
            rect2i.getWidth(),
            rect2i.getHeight(),
            (int)(col[0] * 255.0F),
            (int)(col[1] * 255.0F),
            (int)(col[2] * 255.0F),
            (int)(col[3] * 255.0F),
            overlay,
            light
         );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawString(
      BookWritableTextBox bookWritableTextBox,
      BookOfShadowsAltarTile altarTile,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      float xCursor,
      float yCursor,
      float zLevel,
      int light,
      int overlay,
      PageDrawing.PageOn pageOn,
      PageDrawing.DrawingType drawingType
   ) {
      if (bookWritableTextBox.client == null) {
         bookWritableTextBox.client = new BookWritableTextBox.Client(bookWritableTextBox);
      } else {
         BookWritableTextBox.Client.DisplayCache displaycache = bookWritableTextBox.client.getDisplayCache(altarTile.currentBook);
         Font font = ClientProxy.font();
         poseStack.pushPose();
         if (pageOn == PageDrawing.PageOn.LEFT_PAGE) {
            translateToLeftPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_UNDER) {
            translateToLeftPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.LEFT_PAGE_PREV) {
            translateToLeftPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         }

         if (pageOn == PageDrawing.PageOn.RIGHT_PAGE) {
            translateToRightPage(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_UNDER) {
            translateToRightPageUnder(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         } else if (pageOn == PageDrawing.PageOn.RIGHT_PAGE_PREV) {
            translateToRightPagePrevious(altarTile, poseStack, drawingType, ItemDisplayContext.NONE);
         }

         poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
         poseStack.translate(-0.521875F, 0.28125F, -6.25E-4F);
         poseStack.scale(0.00272F, 0.00272F, 0.00272F);
         poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0F));
         BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
         int linenumber = 0;
         BookParagraphElements box = bookWritableTextBox.paragraphElement;
         float offsetY = 0.0F;

         for (BookWritableTextBox.Client.LineInfo line : displaycache.lines) {
            if ((linenumber + 1) * 9 <= Math.round(box.height * 9.0F) + 1) {
               float offsetX = 0.0F;
               Component component = (Component)(ClientProxy.fontId() != null
                  ? Component.literal("").append(line.asComponent).withStyle(Style.EMPTY.withFont(ClientProxy.fontId()))
                  : line.asComponent);
               font.drawInBatch(
                  component,
                  box.x * 8.0F - 24.0F - offsetX,
                  box.y * 9.0F + linenumber * 9 - 4.0F + offsetY,
                  HexereiUtil.getColorValue(0.12F, 0.12F, 0.12F),
                  false,
                  poseStack.last().pose(),
                  bufferSource,
                  DisplayMode.NORMAL,
                  0,
                  light
               );
               poseStack.pushPose();
               poseStack.translate(0.25F, 0.25F, 0.0625F);
               font.drawInBatch(
                  component,
                  box.x * 8.0F - 24.0F - offsetX,
                  box.y * 9.0F + linenumber * 9 - 4.0F + offsetY,
                  16777216,
                  false,
                  poseStack.last().pose(),
                  bufferSource,
                  DisplayMode.NORMAL,
                  0,
                  light
               );
               poseStack.popPose();
            }

            linenumber++;
         }

         if (focusedWritableTextBox != null && focusedWritableTextBox.getRight() == bookWritableTextBox && altarTile == focusedWritableTextBox.getLeft()) {
            if ((int)ClientEvents.getClientTicks() / 6 % 3 == 0 || (int)ClientEvents.getClientTicks() / 6 % 3 == 1) {
               fill(
                  RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/pencil_cursor.png")),
                  poseStack,
                  bufferSource,
                  box.x * 8.0F - 24.0F + displaycache.cursor.x,
                  box.y * 9.0F - 5.0F + displaycache.cursor.y,
                  -1.0F,
                  9.0F,
                  9.0F,
                  255,
                  255,
                  255,
                  255,
                  overlay,
                  light
               );
            }

            this.renderHighlight(displaycache.selection, poseStack, bufferSource, overlay, light, box.x * 8.0F - 92.0F, box.y * 9.0F - 36.0F);
         } else if (canInteract(
            xCursor,
            yCursor,
            bookWritableTextBox.paragraphElement.x + 0.45F,
            bookWritableTextBox.paragraphElement.y,
            bookWritableTextBox.paragraphElement.width / 6.15F,
            bookWritableTextBox.paragraphElement.height / 2.57F,
            altarTile,
            drawingType
         )) {
            BookWritableTextBox.Client.Pos2i pos2i = new BookWritableTextBox.Client.Pos2i(
               (int)((xCursor - bookWritableTextBox.paragraphElement.x - 0.45F) / 5.0F * 115.0F),
               (int)((yCursor - bookWritableTextBox.paragraphElement.y) / 7.1F * 162.0F)
            );
            int i = displaycache.getIndexAtPosition(ClientProxy.font(), pos2i);
            pos2i = bookWritableTextBox.client.getCursorPosOf(i, altarTile.currentBook);
            fill(
               RenderType.entityCutout(ResourceLocation.parse("hexerei:textures/book/pencil_cursor.png")),
               poseStack,
               bufferSource,
               box.x * 8.0F - 24.0F + pos2i.x,
               box.y * 9.0F - 5.0F + pos2i.y,
               -1.0F,
               9.0F,
               9.0F,
               255,
               255,
               255,
               255,
               overlay,
               light
            );
         }

         buffer.endBatch();
         poseStack.popPose();
         this.resetLines();
      }
   }

   public static enum DrawingType {
      BOOK,
      SCREEN,
      GUI;

      public static PageDrawing.DrawingType byId(int id) {
         PageDrawing.DrawingType[] type = values();
         return type[id >= 0 && id < type.length ? id : 0];
      }
   }

   public static class ImageConverter {
      public static NativeImage convertToNativeImage(BufferedImage bufferedImage) {
         int width = bufferedImage.getWidth();
         int height = bufferedImage.getHeight();
         NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);

         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               int argb = bufferedImage.getRGB(x, y);
               int a = argb >> 24 & 0xFF;
               int r = argb >> 16 & 0xFF;
               int g = argb >> 8 & 0xFF;
               int b = argb & 0xFF;
               int rgba = a << 24 | r << 16 | g << 8 | b;
               nativeImage.setPixelRGBA(x, y, rgba);
            }
         }

         return nativeImage;
      }

      public static NativeImage convertToNativeImage(BufferedImage bufferedImage, NativeImage baseImage) {
         if (baseImage == null) {
            return null;
         } else {
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            int baseWidth = baseImage.getWidth();
            int baseHeight = baseImage.getHeight();
            int pasteWidth = Math.min(width, baseWidth);
            int pasteHeight = Math.min(height, baseHeight);

            for (int y = 0; y < pasteHeight; y++) {
               for (int x = 0; x < pasteWidth; x++) {
                  int argb = bufferedImage.getRGB(x, y);
                  int a = argb >> 24 & 0xFF;
                  int b = argb >> 16 & 0xFF;
                  int g = argb >> 8 & 0xFF;
                  int r = argb & 0xFF;
                  int rgba = a << 24 | r << 16 | g << 8 | b;
                  baseImage.setPixelRGBA(x, y, rgba);
               }
            }

            return baseImage;
         }
      }
   }

   public static enum PageOn {
      LEFT_PAGE,
      LEFT_PAGE_UNDER,
      LEFT_PAGE_PREV,
      LEFT_PAGE_PREV_PREV,
      RIGHT_PAGE,
      RIGHT_PAGE_UNDER,
      RIGHT_PAGE_PREV,
      RIGHT_PAGE_PREV_PREV,
      MIDDLE_BUTTON;

      public boolean isOnLeftSide() {
         return this == LEFT_PAGE || this == LEFT_PAGE_PREV || this == LEFT_PAGE_PREV_PREV || this == RIGHT_PAGE_UNDER;
      }
   }
}
