package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.item.CitadelDataComponents;
import com.github.alexthe666.citadel.item.data.FancyItemDisplay;
import com.github.alexthe666.citadel.item.data.IconItemDisplay;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;

public class CitadelItemstackRenderer extends BlockEntityWithoutLevelRenderer {
   private static final ResourceLocation DEFAULT_ICON_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/icon_default.png");
   private static final Map<String, ResourceLocation> LOADED_ICONS = new HashMap<>();
   private static List<Reference<MobEffect>> mobEffectList = null;

   public CitadelItemstackRenderer() {
      super(null, null);
   }

   public void renderByItem(
      ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
      float ticksExisted = (float)Util.getMillis() / 50.0F + partialTicks;
      int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
      if (stack.getItem() == Citadel.FANCY_ITEM.get()) {
         Random random = new Random();
         boolean animateAnyways = false;
         ItemStack toRender = null;
         FancyItemDisplay display = (FancyItemDisplay)stack.get((DataComponentType)CitadelDataComponents.FANCY_ITEM_DISPLAY.get());
         if (display != null && !display.displayItem().isEmpty()) {
            Item item = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse(display.displayItem()));
            toRender = new ItemStack(item);
         }

         if (toRender == null || toRender.isEmpty()) {
            animateAnyways = true;
            toRender = new ItemStack(Items.BARRIER);
         }

         poseStack.pushPose();
         poseStack.translate(0.5F, 0.5F, 0.5F);
         if (display != null && display.displayShake()) {
            poseStack.translate((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
         }

         if (animateAnyways || display != null && display.displayBob()) {
            poseStack.translate(0.0F, 0.05F + 0.1F * Mth.sin(0.3F * ticksExisted), 0.0F);
         }

         if (display != null && display.displaySpin()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(6.0F * ticksExisted));
         }

         if (animateAnyways || display != null && display.displayZoom()) {
            float scale = (float)(1.0 + 0.15000000596046448 * (Math.sin(ticksExisted * 0.3F) + 1.0));
            poseStack.scale(scale, scale, scale);
         }

         if (display != null && display.displayScale() != 1.0F) {
            float scale = display.displayScale();
            poseStack.scale(scale, scale, scale);
         }

         Minecraft.getInstance().getItemRenderer().renderStatic(toRender, displayContext, packedLight, packedOverlay, poseStack, buffer, null, id);
         poseStack.popPose();
      }

      if (stack.getItem() == Citadel.EFFECT_ITEM.get()) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         if (mobEffectList == null) {
            mobEffectList = BuiltInRegistries.MOB_EFFECT.holders().toList();
         }

         int size = mobEffectList.size();
         int time = (int)(Util.getMillis() / 500L);
         Holder<MobEffect> effect = (Holder<MobEffect>)mobEffectList.get(time % size);
         if (effect == null) {
            effect = MobEffects.MOVEMENT_SPEED.getDelegate();
         }

         MobEffectTextureManager potionspriteuploader = Minecraft.getInstance().getMobEffectTextures();
         poseStack.pushPose();
         poseStack.translate(0.0F, 0.0F, 0.5F);
         TextureAtlasSprite sprite = potionspriteuploader.get(effect);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, sprite.atlasLocation());
         BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
         Matrix4f mx = poseStack.last().pose();
         int br = 255;
         bufferbuilder.addVertex(mx, 1.0F, 1.0F, 0.0F).setUv(sprite.getU1(), sprite.getV0()).setColor(br, br, br, 255).setLight(packedLight);
         bufferbuilder.addVertex(mx, 0.0F, 1.0F, 0.0F).setUv(sprite.getU0(), sprite.getV0()).setColor(br, br, br, 255).setLight(packedLight);
         bufferbuilder.addVertex(mx, 0.0F, 0.0F, 0.0F).setUv(sprite.getU0(), sprite.getV1()).setColor(br, br, br, 255).setLight(packedLight);
         bufferbuilder.addVertex(mx, 1.0F, 0.0F, 0.0F).setUv(sprite.getU1(), sprite.getV1()).setColor(br, br, br, 255).setLight(packedLight);
         poseStack.popPose();
      }

      if (stack.getItem() == Citadel.ICON_ITEM.get()) {
         ResourceLocation texture = DEFAULT_ICON_TEXTURE;
         IconItemDisplay displayx = (IconItemDisplay)stack.get((DataComponentType)CitadelDataComponents.ICON_ITEM_DISPLAY.get());
         if (displayx != null && !displayx.iconLocation().isEmpty()) {
            String iconLocationStr = displayx.iconLocation();
            if (LOADED_ICONS.containsKey(iconLocationStr)) {
               texture = LOADED_ICONS.get(iconLocationStr);
            } else {
               texture = ResourceLocation.parse(iconLocationStr);
               LOADED_ICONS.put(iconLocationStr, texture);
            }
         }

         poseStack.pushPose();
         poseStack.translate(0.0F, 0.0F, 0.5F);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, texture);
         Tesselator tessellator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
         Matrix4f mx = poseStack.last().pose();
         int br = 255;
         bufferbuilder.addVertex(mx, 1.0F, 1.0F, 0.0F).setUv(1.0F, 0.0F).setColor(br, br, br, 255).setLight(packedLight);
         bufferbuilder.addVertex(mx, 0.0F, 1.0F, 0.0F).setUv(0.0F, 0.0F).setColor(br, br, br, 255).setLight(packedLight);
         bufferbuilder.addVertex(mx, 0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F).setColor(br, br, br, 255).setLight(packedLight);
         bufferbuilder.addVertex(mx, 1.0F, 0.0F, 0.0F).setUv(1.0F, 1.0F).setColor(br, br, br, 255).setLight(packedLight);
         poseStack.popPose();
      }
   }
}
