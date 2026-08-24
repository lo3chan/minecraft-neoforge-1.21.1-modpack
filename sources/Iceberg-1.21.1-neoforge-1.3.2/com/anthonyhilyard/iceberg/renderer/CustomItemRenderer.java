package com.anthonyhilyard.iceberg.renderer;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.util.EntityCollector;
import com.anthonyhilyard.iceberg.util.GuiHelper;
import com.anthonyhilyard.iceberg.util.ItemUtil;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.mojang.math.MatrixUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.AnimalArmorItem.BodyType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CustomItemRenderer extends ItemRenderer {
   private static CustomItemRenderer INSTANCE = null;
   private static RenderTarget iconFrameBuffer = null;
   private static ArmorStand armorStand = null;
   private static Wolf wolf = null;
   private static Horse horse = null;
   private static Entity entity = null;
   private static Pair<Item, DataComponentMap> cachedArmorStandItem = null;
   private static Pair<Item, DataComponentMap> cachedHorseArmorItem = null;
   private static Pair<Item, DataComponentMap> cachedWolfArmorItem = null;
   private static Pair<Item, DataComponentMap> cachedEntityItem = null;
   private static Map<Pair<Item, DataComponentMap>, CustomItemRenderer.ModelBounds> modelBoundsCache = Maps.newHashMap();
   private static Map<BakedModel, Boolean> testedModels = Maps.newHashMap();
   private static final List<Direction> quadDirections = new ArrayList<>(Arrays.asList(Direction.values()));
   private Minecraft minecraft;
   private final ModelManager modelManager;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

   public static CustomItemRenderer getInstance() {
      if (INSTANCE == null) {
         Minecraft minecraft = Minecraft.getInstance();
         INSTANCE = new CustomItemRenderer(
            minecraft.getTextureManager(), minecraft.getModelManager(), minecraft.itemColors, minecraft.getItemRenderer().blockEntityRenderer, minecraft
         );
      }

      return INSTANCE;
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.12"
   )
   public CustomItemRenderer(
      TextureManager textureManagerIn,
      ModelManager modelManagerIn,
      ItemColors itemColorsIn,
      BlockEntityWithoutLevelRenderer blockEntityRendererIn,
      Minecraft mcIn
   ) {
      super(mcIn, textureManagerIn, modelManagerIn, itemColorsIn, blockEntityRendererIn);
      this.minecraft = mcIn;
      this.modelManager = modelManagerIn;
      this.blockEntityRenderer = blockEntityRendererIn;
      if (iconFrameBuffer == null) {
         iconFrameBuffer = new MainTarget(96, 96);
         iconFrameBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         iconFrameBuffer.clear(Minecraft.ON_OSX);
      }
   }

   private void renderGuiModel(ItemStack itemStack, int x, int y, Quaternionf rotation, BakedModel bakedModel, GuiGraphics graphics) {
      this.minecraft.getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
      RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.translate(x + 8.0F, y + 8.0F, 150.0F);
      modelViewStack.mul(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
      modelViewStack.scale(16.0F, 16.0F, 16.0F);
      RenderSystem.applyModelViewMatrix();
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      boolean flatLighting = !bakedModel.usesBlockLight();
      if (flatLighting) {
         Lighting.setupForFlatItems();
      }

      PoseStack poseStack = new PoseStack();
      this.renderModel(itemStack, ItemDisplayContext.GUI, false, poseStack, rotation, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
      RenderSystem.disableDepthTest();
      bufferSource.endBatch();
      RenderSystem.enableDepthTest();
      if (flatLighting) {
         Lighting.setupFor3DItems();
      }

      modelViewStack.popMatrix();
      RenderSystem.applyModelViewMatrix();
   }

   private void renderEntityModel(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      Minecraft minecraft = Minecraft.getInstance();
      EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
      Lighting.setupForEntityInInventory();
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      entityRenderDispatcher.setRenderShadow(false);
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

      try {
         RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, bufferSource, packedLight));
      } catch (Exception var8) {
      }

      poseStack.popPose();
      if (bufferSource instanceof BufferSource source) {
         source.endBatch();
      }

      entityRenderDispatcher.setRenderShadow(true);
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   private <T extends MultiBufferSource> void renderModelInternal(
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      boolean leftHanded,
      PoseStack poseStack,
      Quaternionf rotation,
      T bufferSource,
      int packedLight,
      int packedOverlay,
      BakedModel bakedModel,
      Predicate<T> bufferSourceReady
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      if (ItemUtil.getEquipmentSlot(itemStack).isArmor() && this.updateArmorStand(itemStack)) {
         poseStack.pushPose();
         poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
         this.renderEntityModel(armorStand, poseStack, bufferSource, packedLight);
         poseStack.popPose();
      }

      if (!bakedModel.isCustomRenderer() && !itemStack.is(Items.TRIDENT)) {
         boolean fabulous;
         if (displayContext != ItemDisplayContext.GUI && !displayContext.firstPerson() && itemStack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            fabulous = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
         } else {
            fabulous = true;
         }

         if (bufferSourceReady.test(bufferSource) && itemStack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BakedModel blockModel = null;
            BlockModelShaper blockModelShaper = minecraft.getBlockRenderer().getBlockModelShaper();
            boolean isBlockEntity = false;
            blockModel = blockModelShaper.getBlockModel(block.defaultBlockState());
            if (blockModel != this.modelManager.getMissingModel()) {
               try {
                  this.blockEntityRenderer.renderByItem(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
               } catch (Exception var23) {
               }
            } else {
               blockModel = null;
            }

            if (block.defaultBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
               BlockState bottomState = (BlockState)block.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
               BakedModel bottomModel = blockModelShaper.getBlockModel(bottomState);
               this.renderBakedModelSafe(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay, bottomModel, fabulous);
               poseStack.pushPose();
               poseStack.translate(0.0F, 1.0F, 0.0F);
               BlockState topState = (BlockState)block.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
               BakedModel topModel = blockModelShaper.getBlockModel(topState);
               this.renderBakedModelSafe(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay, topModel, fabulous);
               poseStack.popPose();
            }

            if (blockItem.getBlock() instanceof EntityBlock entityBlock) {
               isBlockEntity = true;

               try {
                  this.renderBlockEntity(
                     itemStack, poseStack, bufferSource, packedLight, packedOverlay, minecraft, entityBlock, blockItem.getBlock().defaultBlockState()
                  );
               } catch (Exception var22) {
               }
            }

            if (blockModel != null && (bufferSourceReady.test(bufferSource) || isBlockEntity)) {
               this.renderBakedModelSafe(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay, blockModel, fabulous);
            }
         }

         if (bufferSourceReady.test(bufferSource) && EntityCollector.itemCreatesEntity(itemStack, Entity.class) && this.updateEntity(itemStack)) {
            this.renderEntityModel(entity, poseStack, bufferSource, packedLight);
         }

         if (bufferSourceReady.test(bufferSource) && itemStack.getItem() instanceof AnimalArmorItem animalArmor) {
            switch (animalArmor.getBodyType()) {
               case EQUESTRIAN:
                  if (this.updateHorseArmor(itemStack)) {
                     this.renderEntityModel(horse, poseStack, bufferSource, packedLight);
                  }
                  break;
               case CANINE:
                  if (this.updateWolfArmor(itemStack)) {
                     this.renderEntityModel(wolf, poseStack, bufferSource, packedLight);
                  }
            }
         }

         if (bufferSourceReady.test(bufferSource)) {
            this.renderBakedModelSafe(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay, bakedModel, fabulous);
         }
      } else if (bufferSourceReady.test(bufferSource)) {
         this.blockEntityRenderer.renderByItem(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
      }
   }

   private void renderModel(
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      boolean leftHanded,
      PoseStack poseStack,
      Quaternionf rotation,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay,
      BakedModel bakedModel
   ) {
      if (!itemStack.isEmpty()) {
         ItemDisplayContext previewContext = displayContext;
         if (!bakedModel.getTransforms().hasTransform(displayContext)) {
            previewContext = ItemDisplayContext.GROUND;
         }

         boolean isBlockItem = false;
         boolean spawnsEntity = false;
         boolean isArmor = false;
         if (itemStack.getItem() instanceof BlockItem) {
            isBlockItem = true;
         } else if (EntityCollector.itemCreatesEntity(itemStack, Entity.class)) {
            spawnsEntity = true;
         }

         if (ItemUtil.getEquipmentSlot(itemStack).isArmor()) {
            isArmor = true;
         }

         poseStack.pushPose();
         poseStack.translate(0.5F, 0.5F, 0.5F);
         if (!isBlockItem && !spawnsEntity) {
            bakedModel.getTransforms().getTransform(previewContext).apply(leftHanded, poseStack);
         } else {
            poseStack.mulPose(new Quaternionf().rotationXYZ((float)Math.toRadians(30.0), (float)Math.toRadians(225.0), 0.0F));
         }

         poseStack.translate(-0.5F, -0.5F, -0.5F);
         CustomItemRenderer.ModelBounds modelBounds = this.getModelBounds(
            itemStack, previewContext, leftHanded, poseStack, rotation, bufferSource, packedLight, packedOverlay, bakedModel
         );
         poseStack.popPose();
         poseStack.pushPose();
         poseStack.mulPose(rotation);
         float scale = 0.8F / Math.max(modelBounds.height, modelBounds.radius * 2.0F);
         if (isArmor) {
            switch (ItemUtil.getEquipmentSlot(itemStack)) {
               case HEAD:
                  scale *= 0.75F;
                  break;
               case LEGS:
                  scale *= 1.3F;
                  break;
               case FEET:
                  scale *= 0.85F;
            }
         }

         poseStack.scale(scale, scale, scale);
         poseStack.translate(-modelBounds.center.x(), -modelBounds.center.y(), -modelBounds.center.z());
         poseStack.translate(0.5F, 0.5F, 0.5F);
         if (!isBlockItem && !spawnsEntity) {
            bakedModel.getTransforms().getTransform(previewContext).apply(leftHanded, poseStack);
         } else {
            poseStack.mulPose(new Quaternionf().rotationXYZ((float)Math.toRadians(30.0), (float)Math.toRadians(225.0), 0.0F));
         }

         poseStack.translate(-0.5F, -0.5F, -0.5F);
         CheckedBufferSource checkedBufferSource = CheckedBufferSource.create(bufferSource);
         this.renderModelInternal(
            itemStack, previewContext, leftHanded, poseStack, rotation, checkedBufferSource, packedLight, packedOverlay, bakedModel, b -> !b.hasRendered()
         );
         poseStack.popPose();
      }
   }

   private void renderBlockEntity(
      ItemStack itemStack,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay,
      Minecraft minecraft,
      EntityBlock entityBlock,
      BlockState blockState
   ) throws Exception {
      BlockEntity blockEntity = entityBlock.newBlockEntity(BlockPos.ZERO, blockState);
      if (blockEntity != null) {
         blockEntity.applyComponentsFromItemStack(itemStack);
         BlockEntityRenderer<BlockEntity> renderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
         if (renderer != null) {
            renderer.render(blockEntity, minecraft.getTimer().getRealtimeDeltaTicks(), poseStack, bufferSource, packedLight, packedOverlay);
         }
      }
   }

   private void renderBakedModelSafe(
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay,
      BakedModel bakedModel,
      boolean fabulous
   ) {
      if (!testedModels.containsKey(bakedModel)) {
         try {
            this.renderBakedModel(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay, bakedModel, fabulous);
            testedModels.put(bakedModel, true);
         } catch (Exception var10) {
            Iceberg.LOGGER.info(var10);
            testedModels.put(bakedModel, false);
         }
      } else if (testedModels.get(bakedModel)) {
         this.renderBakedModel(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay, bakedModel, fabulous);
      }
   }

   private void renderBakedModel(
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay,
      BakedModel bakedModel,
      boolean fabulous
   ) {
      RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, fabulous);
      VertexConsumer vertexConsumer;
      if (hasAnimatedTexture(itemStack) && itemStack.hasFoil()) {
         Pose pose = poseStack.last().copy();
         if (displayContext == ItemDisplayContext.GUI) {
            MatrixUtil.mulComponentWise(pose.pose(), 0.5F);
         } else if (displayContext.firstPerson()) {
            MatrixUtil.mulComponentWise(pose.pose(), 0.75F);
         }

         vertexConsumer = getCompassFoilBuffer(bufferSource, renderType, pose);
      } else if (fabulous) {
         vertexConsumer = getFoilBufferDirect(bufferSource, renderType, true, itemStack.hasFoil());
      } else {
         vertexConsumer = getFoilBuffer(bufferSource, renderType, true, itemStack.hasFoil());
      }

      this.renderModelLists(bakedModel, itemStack, packedLight, packedOverlay, poseStack, vertexConsumer);
   }

   private boolean updateArmorStand(ItemStack itemStack) {
      EquipmentSlot equipmentSlot = ItemUtil.getEquipmentSlot(itemStack);
      if (!equipmentSlot.isArmor()) {
         return false;
      } else {
         if (armorStand == null) {
            Minecraft minecraft = Minecraft.getInstance();
            armorStand = (ArmorStand)EntityType.ARMOR_STAND.create(minecraft.level);
            armorStand.setInvisible(true);
         }

         if (armorStand == null) {
            return false;
         } else {
            if (cachedArmorStandItem != Pair.of(itemStack.getItem(), ItemUtil.getItemComponents(itemStack))) {
               for (EquipmentSlot slot : EquipmentSlot.values()) {
                  armorStand.setItemSlot(slot, ItemStack.EMPTY);
               }

               armorStand.setItemSlot(equipmentSlot, itemStack);
               cachedArmorStandItem = Pair.of(itemStack.getItem(), ItemUtil.getItemComponents(itemStack));
            }

            return true;
         }
      }
   }

   private Entity getEntityFromItem(ItemStack itemStack) {
      Entity collectedEntity = null;
      List<Entity> collectedEntities = EntityCollector.collectEntitiesFromItem(itemStack);
      if (!collectedEntities.isEmpty()) {
         collectedEntity = collectedEntities.get(0);
      }

      return collectedEntity;
   }

   private boolean updateEntity(ItemStack itemStack) {
      Pair<Item, DataComponentMap> entityItem = Pair.of(itemStack.getItem(), ItemUtil.getItemComponents(itemStack));
      if (entity == null || cachedEntityItem != entityItem) {
         entity = this.getEntityFromItem(itemStack);
         cachedEntityItem = entityItem;
      }

      return entity != null;
   }

   private boolean updateHorseArmor(ItemStack horseArmorItem) {
      if (horseArmorItem.getItem() instanceof AnimalArmorItem animalArmor && animalArmor.getBodyType() == BodyType.EQUESTRIAN) {
         if (horse == null) {
            Minecraft minecraft = Minecraft.getInstance();
            horse = (Horse)EntityType.HORSE.create(minecraft.level);
            horse.setInvisible(true);
         }

         if (horse == null) {
            return false;
         } else {
            if (cachedHorseArmorItem != Pair.of(horseArmorItem.getItem(), ItemUtil.getItemComponents(horseArmorItem))) {
               horse.setBodyArmorItem(horseArmorItem);
               cachedHorseArmorItem = Pair.of(horseArmorItem.getItem(), ItemUtil.getItemComponents(horseArmorItem));
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean updateWolfArmor(ItemStack wolfArmorItem) {
      if (wolfArmorItem.getItem() instanceof AnimalArmorItem animalArmor && animalArmor.getBodyType() == BodyType.CANINE) {
         if (wolf == null) {
            Minecraft minecraft = Minecraft.getInstance();
            wolf = (Wolf)EntityType.WOLF.create(minecraft.level);
            wolf.setInvisible(true);
         }

         if (wolf == null) {
            return false;
         } else {
            if (cachedWolfArmorItem != Pair.of(wolfArmorItem.getItem(), ItemUtil.getItemComponents(wolfArmorItem))) {
               wolf.setBodyArmorItem(wolfArmorItem);
               cachedWolfArmorItem = Pair.of(wolfArmorItem.getItem(), ItemUtil.getItemComponents(wolfArmorItem));
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private CustomItemRenderer.ModelBounds boundsFromVertices(Set<Vector3f> vertices) {
      new Vector3f();
      float radius = 0.0F;
      float height = 0.0F;
      float minX = 3.4028235E38F;
      float minY = 3.4028235E38F;
      float minZ = 3.4028235E38F;
      float maxX = 1.0E-45F;
      float maxY = 1.0E-45F;
      float maxZ = 1.0E-45F;

      for (Vector3f vertex : vertices) {
         minX = Math.min(minX, vertex.x);
         minY = Math.min(minY, vertex.y);
         minZ = Math.min(minZ, vertex.z);
         maxX = Math.max(maxX, vertex.x);
         maxY = Math.max(maxY, vertex.y);
         maxZ = Math.max(maxZ, vertex.z);
      }

      Vector3f center = new Vector3f((minX + maxX) / 2.0F, (minY + maxY) / 2.0F, (minZ + maxZ) / 2.0F);
      height = maxY - minY;

      for (Vector3f vertex : vertices) {
         radius = Math.max(radius, (float)Math.sqrt((vertex.x - center.x) * (vertex.x - center.x) + (vertex.z - center.z) * (vertex.z - center.z)));
      }

      return new CustomItemRenderer.ModelBounds(center, height, radius);
   }

   private CustomItemRenderer.ModelBounds getModelBounds(
      ItemStack itemStack,
      ItemDisplayContext displayContext,
      boolean leftHanded,
      PoseStack poseStack,
      Quaternionf rotation,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay,
      BakedModel bakedModel
   ) {
      Pair<Item, DataComponentMap> key = Pair.of(itemStack.getItem(), ItemUtil.getItemComponents(itemStack));
      if (!modelBoundsCache.containsKey(key)) {
         VertexCollector vertexCollector = VertexCollector.create();
         this.renderModelInternal(
            itemStack, displayContext, leftHanded, poseStack, rotation, vertexCollector, packedLight, packedOverlay, bakedModel, b -> b.getVertices().isEmpty()
         );
         modelBoundsCache.put(key, this.boundsFromVertices(vertexCollector.getVertices()));
      }

      return modelBoundsCache.get(key);
   }

   public void renderDetailModelIntoGUI(ItemStack stack, int x, int y, Quaternionf rotation, GuiGraphics graphics) {
      Minecraft minecraft = Minecraft.getInstance();
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 0);

      try {
         this.renderGuiModel(stack, x, y, rotation, bakedModel, graphics);
      } catch (Throwable var11) {
         CrashReport crashReport = CrashReport.forThrowable(var11, "Rendering item");
         CrashReportCategory crashReportCategory = crashReport.addCategory("Item being rendered");
         crashReportCategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
         crashReportCategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
         crashReportCategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
         throw new ReportedException(crashReport);
      }
   }

   public void renderItemModelIntoGUIWithAlpha(GuiGraphics graphics, ItemStack stack, int x, int y, float alpha) {
      BakedModel bakedModel = this.minecraft.getItemRenderer().getModel(stack, null, null, 0);
      RenderTarget lastFrameBuffer = this.minecraft.getMainRenderTarget();
      iconFrameBuffer.clear(Minecraft.ON_OSX);
      iconFrameBuffer.bindWrite(true);
      Matrix4f matrix = new Matrix4f();
      matrix.setOrtho(0.0F, iconFrameBuffer.width, iconFrameBuffer.height, 0.0F, 1000.0F, 3000.0F);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(matrix, VertexSorting.ORTHOGRAPHIC_Z);
      RenderSystem.disableCull();
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.identity();
      modelViewStack.translate(48.0F, 48.0F, -2000.0F);
      modelViewStack.scale(96.0F, 96.0F, 96.0F);
      RenderSystem.applyModelViewMatrix();
      BufferSource bufferSource = graphics.bufferSource();
      boolean flatLighting = !bakedModel.usesBlockLight();
      if (flatLighting) {
         Lighting.setupForFlatItems();
      }

      this.render(stack, ItemDisplayContext.GUI, false, new PoseStack(), bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
      graphics.flush();
      if (flatLighting) {
         Lighting.setupFor3DItems();
      }

      modelViewStack.popMatrix();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.restoreProjectionMatrix();
      if (lastFrameBuffer != null) {
         lastFrameBuffer.bindWrite(true);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
         RenderSystem.setShaderTexture(0, iconFrameBuffer.getColorTextureId());
         GuiHelper.blit(graphics.pose(), x, y, 16, 16, 0.0F, 0.0F, iconFrameBuffer.width, iconFrameBuffer.height, iconFrameBuffer.width, iconFrameBuffer.height);
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.flush();
         iconFrameBuffer.unbindRead();
      } else {
         iconFrameBuffer.unbindWrite();
      }
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      super.onResourceManagerReload(resourceManager);
      modelBoundsCache.clear();
   }

   static {
      quadDirections.add(null);
   }

   private record ModelBounds(Vector3f center, float height, float radius) {
   }
}
