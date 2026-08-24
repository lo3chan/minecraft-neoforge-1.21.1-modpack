package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchor;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateAnchorWinch;
import com.github.alexthe666.alexsmobs.client.model.ModelEndPirateShipWheel;
import com.github.alexthe666.alexsmobs.client.model.ModelMysteriousWorm;
import com.github.alexthe666.alexsmobs.client.model.ModelShieldOfTheDeep;
import com.github.alexthe666.alexsmobs.client.model.ModelTransmutationTable;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemStinkRay;
import com.github.alexthe666.alexsmobs.item.ItemTabIcon;
import com.github.alexthe666.alexsmobs.item.ItemVineLasso;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class AMItemstackRenderer extends BlockEntityWithoutLevelRenderer {
   public static int ticksExisted = 0;
   private static final RandomSource RANDOM = RandomSource.create();
   private static final ModelShieldOfTheDeep SHIELD_OF_THE_DEEP_MODEL = new ModelShieldOfTheDeep();
   private static final ResourceLocation SHIELD_OF_THE_DEEP_TEXTURE = AMCompat.rl("alexsmobs:textures/armor/shield_of_the_deep.png");
   private static final ModelMysteriousWorm MYTERIOUS_WORM_MODEL = new ModelMysteriousWorm();
   private static final ResourceLocation MYTERIOUS_WORM_TEXTURE = AMCompat.rl("alexsmobs:textures/item/mysterious_worm_model.png");
   private static final ModelEndPirateAnchor ANCHOR_MODEL = new ModelEndPirateAnchor();
   private static final ResourceLocation ANCHOR_TEXTURE = AMCompat.rl("alexsmobs:textures/entity/end_pirate/anchor.png");
   private static final ModelEndPirateAnchorWinch WINCH_MODEL = new ModelEndPirateAnchorWinch();
   private static final ResourceLocation WINCH_TEXTURE = AMCompat.rl("alexsmobs:textures/entity/end_pirate/anchor_winch.png");
   private static final ModelEndPirateShipWheel SHIP_WHEEL_MODEL = new ModelEndPirateShipWheel();
   private static final ResourceLocation SHIP_WHEEL_TEXTURE = AMCompat.rl("alexsmobs:textures/entity/end_pirate/ship_wheel.png");
   private static final ResourceLocation TRANSMUTATION_TABLE_TEXTURE = AMCompat.rl("alexsmobs:textures/entity/farseer/transmutation_table.png");
   private static final ResourceLocation TRANSMUTATION_TABLE_GLOW_TEXTURE = AMCompat.rl("alexsmobs:textures/entity/farseer/transmutation_table_glow.png");
   private static final ResourceLocation TRANSMUTATION_TABLE_OVERLAY = AMCompat.rl("alexsmobs:textures/entity/farseer/transmutation_table_overlay.png");
   private static final ModelTransmutationTable TRANSMUTATION_TABLE_MODEL = new ModelTransmutationTable(0.0F);
   private static final ModelTransmutationTable TRANSMUTATION_TABLE_OVERLAY_MODEL = new ModelTransmutationTable(0.01F);
   private static List<ItemStack> DIMENSIONAL_CARVER_SHARDS;
   private final Map<String, Entity> renderedEntites = new HashMap<>();
   private final List<EntityType> blockedRenderEntities = new ArrayList<>();

   public AMItemstackRenderer() {
      super(null, null);
   }

   public static void incrementTick() {
      ticksExisted++;
   }

   private static float getScaleFor(EntityType type, List<Pair<EntityType, Float>> mobIcons) {
      for (Pair<EntityType, Float> pair : mobIcons) {
         if (pair.getFirst() == type) {
            return (Float)pair.getSecond();
         }
      }

      return 1.0F;
   }

   private static List<ItemStack> getDimensionalCarverShards() {
      if (DIMENSIONAL_CARVER_SHARDS == null || DIMENSIONAL_CARVER_SHARDS.isEmpty()) {
         DIMENSIONAL_CARVER_SHARDS = (List<ItemStack>)Util.make(Lists.newArrayList(), list -> {
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_0"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_1"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_2"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_3"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_4"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_5"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_6"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_7"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_8"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_9"))));
            list.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl("alexsmobs:dimensional_carver_shard_10"))));
         });
      }

      return DIMENSIONAL_CARVER_SHARDS;
   }

   public static void drawEntityOnScreen(
      PoseStack matrixstack,
      MultiBufferSource bufferIn,
      int posX,
      int posY,
      float scale,
      boolean follow,
      double xRot,
      double yRot,
      double zRot,
      float mouseX,
      float mouseY,
      Entity entity
   ) {
      float f = (float)Math.atan(-mouseX / 40.0F);
      float f1 = (float)Math.atan(mouseY / 40.0F);
      matrixstack.scale(scale, scale, scale);
      entity.setOnGround(false);
      float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
      Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
      Quaternionf quaternion1 = Axis.XP.rotationDegrees(20.0F);
      float partialTicksForRender = !Minecraft.getInstance().isPaused() && !(entity instanceof EntityMimicOctopus) ? partialTicks : 0.0F;
      int tick;
      if (Minecraft.getInstance().player != null && !Minecraft.getInstance().isPaused()) {
         tick = Minecraft.getInstance().player.tickCount;
      } else {
         tick = ticksExisted;
      }

      if (follow) {
         float yaw = f * 45.0F;
         entity.setYRot(yaw);
         entity.tickCount = tick;
         if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).yBodyRot = yaw;
            ((LivingEntity)entity).yBodyRotO = yaw;
            ((LivingEntity)entity).yHeadRot = yaw;
            ((LivingEntity)entity).yHeadRotO = yaw;
         }

         quaternion1 = Axis.XP.rotationDegrees(f1 * 20.0F);
         quaternion.mul(quaternion1);
      }

      matrixstack.mulPose(quaternion);
      matrixstack.mulPose(Axis.XP.rotationDegrees((float)(-xRot)));
      matrixstack.mulPose(Axis.YP.rotationDegrees((float)yRot));
      matrixstack.mulPose(Axis.ZP.rotationDegrees((float)zRot));
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conjugate();
      entityrenderdispatcher.overrideCameraOrientation(quaternion1);
      entityrenderdispatcher.setRenderShadow(false);
      BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(
         () -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, partialTicksForRender, matrixstack, multibuffersource$buffersource, 15728880)
      );
      multibuffersource$buffersource.endBatch();
      entityrenderdispatcher.setRenderShadow(true);
      entity.setYRot(0.0F);
      entity.setXRot(0.0F);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).yBodyRot = 0.0F;
         ((LivingEntity)entity).yHeadRotO = 0.0F;
         ((LivingEntity)entity).yHeadRot = 0.0F;
      }

      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   public void renderByItem(
      ItemStack itemStackIn, ItemDisplayContext transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn
   ) {
      int tick;
      if (Minecraft.getInstance().player != null && !Minecraft.getInstance().isPaused()) {
         tick = Minecraft.getInstance().player.tickCount;
      } else {
         tick = ticksExisted;
      }

      Level level = Minecraft.getInstance().level;
      if (itemStackIn.getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()) {
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.4F, -0.75F, 0.5F);
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180.0F));
         VertexConsumer vertexconsumer = AMRenderCompat.armorFoilBuffer(
            bufferIn, RenderType.armorCutoutNoCull(SHIELD_OF_THE_DEEP_TEXTURE), itemStackIn.hasFoil()
         );
         SHIELD_OF_THE_DEEP_MODEL.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.popPose();
      }

      if (itemStackIn.getItem() == AMItemRegistry.MYSTERIOUS_WORM.get()) {
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.0F, -2.0F, 0.0F);
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180.0F));
         MYTERIOUS_WORM_MODEL.animateStack(itemStackIn);
         MYTERIOUS_WORM_MODEL.renderToBuffer(
            matrixStackIn,
            bufferIn.getBuffer(RenderType.entityCutoutNoCull(MYTERIOUS_WORM_TEXTURE)),
            combinedLightIn,
            combinedOverlayIn,
            1.0F,
            1.0F,
            1.0F,
            1.0F
         );
         matrixStackIn.popPose();
      }

      if (itemStackIn.getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
         matrixStackIn.translate(0.5F, 0.5F, 0.5F);
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            AMRenderCompat.renderItemStatic(
               new ItemStack((ItemLike)AMItemRegistry.FALCONRY_GLOVE_INVENTORY.get()),
               transformType,
               transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
               combinedOverlayIn,
               matrixStackIn,
               bufferIn,
               level,
               0
            );
         } else {
            AMRenderCompat.renderItemStatic(
               new ItemStack((ItemLike)AMItemRegistry.FALCONRY_GLOVE_HAND.get()),
               transformType,
               combinedLightIn,
               combinedOverlayIn,
               matrixStackIn,
               bufferIn,
               level,
               0
            );
         }
      }

      if (itemStackIn.getItem() == AMItemRegistry.VINE_LASSO.get()) {
         matrixStackIn.translate(0.5F, 0.5F, 0.5F);
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            AMRenderCompat.renderItemStatic(
               new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO_INVENTORY.get()),
               transformType,
               transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
               combinedOverlayIn,
               matrixStackIn,
               bufferIn,
               level,
               0
            );
         } else {
            if (ItemVineLasso.isItemInUse(itemStackIn)) {
               if (transformType.firstPerson()) {
                  matrixStackIn.translate(transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.0F, -0.5F);
               }

               matrixStackIn.mulPose(Axis.YP.rotation(tick + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)));
            }

            AMRenderCompat.renderItemStatic(
               new ItemStack((ItemLike)AMItemRegistry.VINE_LASSO_HAND.get()),
               transformType,
               combinedLightIn,
               combinedOverlayIn,
               matrixStackIn,
               bufferIn,
               level,
               0
            );
         }
      }

      if (itemStackIn.getItem() == AMItemRegistry.SKELEWAG_SWORD.get()) {
         matrixStackIn.translate(0.5F, 0.5F, 0.5F);
         ItemStack spriteItem = new ItemStack((ItemLike)AMItemRegistry.SKELEWAG_SWORD_INVENTORY.get());
         ItemStack handItem = new ItemStack((ItemLike)AMItemRegistry.SKELEWAG_SWORD_HAND.get());
         AMCompat.setTag(spriteItem, AMCompat.getTag(itemStackIn));
         AMCompat.setTag(handItem, AMCompat.getTag(itemStackIn));
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            AMRenderCompat.renderItemStatic(
               spriteItem,
               transformType,
               transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
               combinedOverlayIn,
               matrixStackIn,
               bufferIn,
               level,
               0
            );
         } else {
            AMRenderCompat.renderItemStatic(handItem, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
         }
      }

      if (itemStackIn.getItem() == AMBlockRegistry.TRANSMUTATION_TABLE.get().asItem()) {
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.5F, 1.6F, 0.5F);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180.0F));
         TRANSMUTATION_TABLE_MODEL.resetToDefaultPose();
         TRANSMUTATION_TABLE_MODEL.renderToBuffer(
            matrixStackIn,
            bufferIn.getBuffer(RenderType.entityCutoutNoCull(TRANSMUTATION_TABLE_TEXTURE)),
            combinedLightIn,
            combinedOverlayIn,
            1.0F,
            1.0F,
            1.0F,
            1.0F
         );
         TRANSMUTATION_TABLE_MODEL.renderToBuffer(
            matrixStackIn,
            bufferIn.getBuffer(RenderType.entityTranslucentEmissive(TRANSMUTATION_TABLE_GLOW_TEXTURE)),
            combinedLightIn,
            combinedOverlayIn,
            1.0F,
            1.0F,
            1.0F,
            1.0F
         );
         TRANSMUTATION_TABLE_OVERLAY_MODEL.resetToDefaultPose();
         VertexConsumer staticyOverlay = bufferIn.getBuffer(RenderType.eyes(TRANSMUTATION_TABLE_OVERLAY));
         TRANSMUTATION_TABLE_OVERLAY_MODEL.renderToBuffer(matrixStackIn, staticyOverlay, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStackIn.popPose();
      }

      if (itemStackIn.getItem() == AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER.get()) {
         matrixStackIn.translate(0.5F, 0.5F, 0.5F);
         float f = tick + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
         List<ItemStack> shards = getDimensionalCarverShards();
         matrixStackIn.pushPose();
         if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            matrixStackIn.translate(-0.2F, 0.0F, 0.0F);
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(60.0F));
         }

         for (int i = 0; i < shards.size(); i++) {
            matrixStackIn.pushPose();
            ItemStack shard = shards.get(i);
            matrixStackIn.translate(
               (float)Math.sin(f * 0.15F + i * 1.0F) * 0.035F,
               -((float)Math.cos(f * 0.15F + i * 1.0F)) * 0.035F,
               (float)Math.cos(f * 0.15F + i * 0.5F + 1.5707963267948966) * 0.025F
            );
            AMRenderCompat.renderItemStatic(
               shard, transformType, transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240, combinedOverlayIn, matrixStackIn, bufferIn, level, 0
            );
            matrixStackIn.popPose();
         }

         matrixStackIn.popPose();
      }

      if (itemStackIn.getItem() == AMItemRegistry.STINK_RAY.get()) {
         matrixStackIn.translate(0.5F, 0.5F, 0.5F);
         ItemStack hand = new ItemStack(
            ItemStinkRay.isUsable(itemStackIn) ? (ItemLike)AMItemRegistry.STINK_RAY_HAND.get() : (ItemLike)AMItemRegistry.STINK_RAY_EMPTY_HAND.get()
         );
         ItemStack inventory = new ItemStack(
            ItemStinkRay.isUsable(itemStackIn) ? (ItemLike)AMItemRegistry.STINK_RAY_INVENTORY.get() : (ItemLike)AMItemRegistry.STINK_RAY_EMPTY_INVENTORY.get()
         );
         if (transformType != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            && transformType != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            AMRenderCompat.renderItemStatic(
               inventory,
               transformType,
               transformType == ItemDisplayContext.GROUND ? combinedLightIn : 240,
               combinedOverlayIn,
               matrixStackIn,
               bufferIn,
               level,
               0
            );
         } else {
            AMRenderCompat.renderItemStatic(hand, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, 0);
         }
      }

      if (itemStackIn.getItem() == AMItemRegistry.FANCY_ITEM.get()) {
         float ticks = (float)Util.getMillis() / 50.0F + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
         int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
         boolean animateAnyways = false;
         ItemStack toRender = null;
         if (AMCompat.getTag(itemStackIn) != null && AMCompat.getTag(itemStackIn).contains("DisplayItem")) {
            String displayID = AMCompat.getString(AMCompat.getTag(itemStackIn), "DisplayItem");
            toRender = new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl(displayID)));
            if (AMCompat.getTag(itemStackIn).contains("DisplayItemNBT")) {
               try {
                  AMCompat.setTag(toRender, AMCompat.getCompound(AMCompat.getTag(itemStackIn), "DisplayItemNBT"));
               } catch (Exception var23) {
                  toRender = new ItemStack(Items.BARRIER);
               }
            }
         }

         if (toRender == null) {
            animateAnyways = true;
            toRender = new ItemStack(Items.BARRIER);
         }

         matrixStackIn.pushPose();
         matrixStackIn.translate(0.5F, 0.5F, 0.5F);
         if (AMCompat.getTag(itemStackIn) != null && AMCompat.getBoolean(AMCompat.getTag(itemStackIn), "DisplayShake")) {
            matrixStackIn.translate((RANDOM.nextFloat() - 0.5F) * 0.1F, (RANDOM.nextFloat() - 0.5F) * 0.1F, (RANDOM.nextFloat() - 0.5F) * 0.1F);
         }

         if (animateAnyways || AMCompat.getTag(itemStackIn) != null && AMCompat.getBoolean(AMCompat.getTag(itemStackIn), "DisplayBob")) {
            matrixStackIn.translate(0.0F, 0.05F + 0.1F * Mth.sin(0.3F * ticks), 0.0F);
         }

         if (AMCompat.getTag(itemStackIn) != null && AMCompat.getBoolean(AMCompat.getTag(itemStackIn), "DisplaySpin")) {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(6.0F * ticks));
         }

         if (animateAnyways || AMCompat.getTag(itemStackIn) != null && AMCompat.getBoolean(AMCompat.getTag(itemStackIn), "DisplayZoom")) {
            float scale = (float)(1.0 + 0.15000000596046448 * (Math.sin(ticks * 0.3F) + 1.0));
            matrixStackIn.scale(scale, scale, scale);
         }

         if (AMCompat.getTag(itemStackIn) != null
            && AMCompat.getFloat(AMCompat.getTag(itemStackIn), "DisplayScale") != 1.0F
            && AMCompat.getTag(itemStackIn).contains("DisplayScale")) {
            float scale = AMCompat.getFloat(AMCompat.getTag(itemStackIn), "DisplayScale");
            matrixStackIn.scale(scale, scale, scale);
         }

         AMRenderCompat.renderItemStatic(toRender, transformType, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, level, id);
         matrixStackIn.popPose();
      }

      if (itemStackIn.getItem() == AMItemRegistry.EFFECT_ITEM.get()) {
         Holder<MobEffect> effect = null;
         if (AMCompat.getTag(itemStackIn) != null && AMCompat.getTag(itemStackIn).contains("DisplayEffect")) {
            effect = (Holder<MobEffect>)BuiltInRegistries.MOB_EFFECT
               .getHolder(AMCompat.rl(AMCompat.getTag(itemStackIn).getString("DisplayEffect")))
               .orElse(null);
         }

         if (effect == null) {
            effect = MobEffects.MOVEMENT_SPEED;
         }

         TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
         matrixStackIn.pushPose();
         matrixStackIn.translate(0.0F, 0.0F, 0.5F);
         VertexConsumer consumer = bufferIn.getBuffer(RenderType.text(sprite.atlasLocation()));
         Matrix4f mx = matrixStackIn.last().pose();
         consumer.addVertex(mx, 1.0F, 1.0F, 0.0F).setColor(255, 255, 255, 255).setUv(sprite.getU1(), sprite.getV0()).setLight(combinedLightIn);
         consumer.addVertex(mx, 0.0F, 1.0F, 0.0F).setColor(255, 255, 255, 255).setUv(sprite.getU0(), sprite.getV0()).setLight(combinedLightIn);
         consumer.addVertex(mx, 0.0F, 0.0F, 0.0F).setColor(255, 255, 255, 255).setUv(sprite.getU0(), sprite.getV1()).setLight(combinedLightIn);
         consumer.addVertex(mx, 1.0F, 0.0F, 0.0F).setColor(255, 255, 255, 255).setUv(sprite.getU1(), sprite.getV1()).setLight(combinedLightIn);
         matrixStackIn.popPose();
      }

      if (itemStackIn.getItem() == AMItemRegistry.TAB_ICON.get()) {
         Entity fakeEntity = null;
         List<Pair<EntityType, Float>> mobIcons = AMMobIcons.getMobIcons();
         int entityIndex = tick / 40 % mobIcons.size();
         float scale = 1.0F;
         int flags = 0;
         if (level != null) {
            if (ItemTabIcon.hasCustomEntityDisplay(itemStackIn)) {
               flags = AMCompat.getInt(AMCompat.getTag(itemStackIn), "DisplayMobFlags");
               String index = ItemTabIcon.getCustomDisplayEntityString(itemStackIn);
               EntityType local = ItemTabIcon.getEntityType(AMCompat.getTag(itemStackIn));
               scale = getScaleFor(local, mobIcons);
               if (AMCompat.getFloat(AMCompat.getTag(itemStackIn), "DisplayMobScale") > 0.0F) {
                  scale = AMCompat.getFloat(AMCompat.getTag(itemStackIn), "DisplayMobScale");
               }

               if (this.renderedEntites.get(index) == null && !this.blockedRenderEntities.contains(local)) {
                  try {
                     Entity entity = AMCompat.createForDisplay(local, level);
                     if (entity instanceof EntityBlobfish) {
                        ((EntityBlobfish)entity).setDepressurized(true);
                     }

                     this.renderedEntites.put(local.getDescriptionId(), entity);
                     fakeEntity = entity;
                  } catch (Exception var22) {
                     this.blockedRenderEntities.add(local);
                     AlexsMobs.LOGGER.error("Could not render item for entity: " + local);
                  }
               } else {
                  fakeEntity = this.renderedEntites.get(local.getDescriptionId());
               }
            } else {
               EntityType type = (EntityType)mobIcons.get(entityIndex).getFirst();
               scale = (Float)mobIcons.get(entityIndex).getSecond();
               if (type != null) {
                  if (this.renderedEntites.get(type.getDescriptionId()) == null && !this.blockedRenderEntities.contains(type)) {
                     try {
                        Entity entity = AMCompat.createForDisplay(type, level);
                        if (entity instanceof EntityBlobfish) {
                           ((EntityBlobfish)entity).setDepressurized(true);
                        }

                        this.renderedEntites.put(type.getDescriptionId(), entity);
                        fakeEntity = entity;
                     } catch (Exception var21) {
                        this.blockedRenderEntities.add(type);
                        AlexsMobs.LOGGER.error("Could not render item for entity: " + type);
                     }
                  } else {
                     fakeEntity = this.renderedEntites.get(type.getDescriptionId());
                  }
               }
            }
         }

         if (fakeEntity instanceof EntityCockroach) {
            if (flags == 99) {
               matrixStackIn.translate(0.0F, 0.25F, 0.0F);
               matrixStackIn.mulPose(Axis.XP.rotationDegrees(-80.0F));
               ((EntityCockroach)fakeEntity).setMaracas(true);
            } else {
               ((EntityCockroach)fakeEntity).setMaracas(false);
            }
         }

         if (fakeEntity instanceof EntityElephant) {
            if (flags == 99) {
               ((EntityElephant)fakeEntity).setTusked(true);
               ((EntityElephant)fakeEntity).setColor(null);
            } else if (flags == 98) {
               ((EntityElephant)fakeEntity).setTusked(false);
               ((EntityElephant)fakeEntity).setColor(DyeColor.BROWN);
            } else {
               ((EntityElephant)fakeEntity).setTusked(false);
               ((EntityElephant)fakeEntity).setColor(null);
            }
         }

         if (fakeEntity instanceof EntityBaldEagle) {
            if (flags == 98) {
               ((EntityBaldEagle)fakeEntity).setCap(true);
            } else {
               ((EntityBaldEagle)fakeEntity).setCap(false);
            }
         }

         if (fakeEntity instanceof EntityVoidWorm) {
            matrixStackIn.translate(0.0F, 0.5F, 0.0F);
         }

         if (fakeEntity instanceof EntityMimicOctopus) {
            matrixStackIn.translate(0.0F, 0.5F, 0.0F);
         }

         if (fakeEntity instanceof EntityLaviathan) {
            RenderLaviathan.renderWithoutShaking = true;
            matrixStackIn.translate(0.0F, 0.3F, 0.0F);
         }

         if (fakeEntity instanceof EntityCosmaw) {
            matrixStackIn.translate(0.0F, 0.2F, 0.0F);
         }

         if (fakeEntity instanceof EntityGiantSquid) {
            matrixStackIn.translate(0.0F, 0.5F, 0.3F);
         }

         if (fakeEntity instanceof EntityUnderminer) {
            RenderUnderminer.renderWithPickaxe = true;
         }

         if (fakeEntity instanceof EntityMurmur) {
            RenderMurmurBody.renderWithHead = true;
            matrixStackIn.translate(0.0F, -0.2F, 0.0F);
         }

         if (fakeEntity != null) {
            MouseHandler mouseHelper = Minecraft.getInstance().mouseHandler;
            double mouseX = mouseHelper.xpos() * Minecraft.getInstance().getWindow().getGuiScaledWidth() / Minecraft.getInstance().getWindow().getScreenWidth();
            double mouseY = mouseHelper.ypos()
               * Minecraft.getInstance().getWindow().getGuiScaledHeight()
               / Minecraft.getInstance().getWindow().getScreenHeight();
            matrixStackIn.translate(0.5F, 0.0F, 0.0F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F));
            if (transformType != ItemDisplayContext.GUI) {
               mouseX = 0.0;
               mouseY = 0.0;
            }

            try {
               drawEntityOnScreen(matrixStackIn, bufferIn, 0, 0, scale, true, 0.0, -45.0, 0.0, (float)mouseX, (float)mouseY, fakeEntity);
            } catch (Exception var20) {
            }
         }

         if (fakeEntity instanceof EntityLaviathan) {
            RenderLaviathan.renderWithoutShaking = false;
         }

         if (fakeEntity instanceof EntityUnderminer) {
            RenderUnderminer.renderWithPickaxe = false;
         }

         if (fakeEntity instanceof EntityMurmur) {
            RenderMurmurBody.renderWithHead = false;
         }
      }
   }
}
