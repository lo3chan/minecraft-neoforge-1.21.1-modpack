package net.diebuddies.physics.settings.cloth;

import com.google.gson.JsonArray;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.diebuddies.compat.Sodium;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.physics.DynamicsWorld;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.LegacyAbstractSelectionList;
import net.diebuddies.physics.settings.mobs.BoundingBoxGetter;
import net.diebuddies.physics.settings.mobs.MobEntry;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Math;
import org.joml.Matrix4d;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ClothDisplayScreen extends Screen {
   private static final double ROTATE_SPEED = 6.0;
   private static final double START_ROTATE_SPEED = 25.0;
   private Screen parent;
   private PartSelectionList partList;
   public LegacyAbstractSelectionList<?> activeList;
   private String selectedEntity = "physicsmod:yourself";
   public EntityType<? extends Entity> entityType;
   private ResourceLocation textureLocation;
   private Model model;
   private List<AbstractWidget> bottomWidgets;
   private Component customTitle;
   private Map<String, VerletSimulation> simulations;
   private long lastTime;
   private double timeDelta;
   private static final double FIXED_TIME_STEP = 0.016666666666666666;
   private double rotationSpeed = 25.0;
   private double totalRotation;
   private int entityXPosition;
   private Button rotateLeft;
   private Button rotateRight;
   private double startX;
   private double startY;
   private double startZ;
   private double endX;
   private double endY;
   private double endZ;
   private boolean reloadLater;
   @Nullable
   private Map<String, ConfigCloth.ClothList> playerCopy;

   protected ClothDisplayScreen(Screen parent) {
      super(Component.translatable("physicsmod.menu.cloth.partselection.title"));
      this.bottomWidgets = new ObjectArrayList();
      this.simulations = new Object2ObjectOpenHashMap();
      this.parent = parent;
      this.entityType = EntityType.PLAYER;
      Map<String, ConfigCloth.ClothList> toCopy = ConfigCloth.getCustomizationParts("physicsmod:yourself");
      if (toCopy == null) {
         toCopy = new Object2ObjectOpenHashMap();
      }

      this.playerCopy = new Object2ObjectOpenHashMap();

      for (Entry<String, ConfigCloth.ClothList> entry : toCopy.entrySet()) {
         this.playerCopy.put(entry.getKey(), entry.getValue().copy());
      }
   }

   protected void init() {
      super.init();
      this.reloadLater = !ConfigCloth.clothUpToDate;
      ConfigCloth.isChangingPlayer = true;
      PhysicsMod.loadCloth();
      PhysicsMod.resetClothSimulations();
      this.loadModelAndTexture();
      this.loadCloth();
      this.lastTime = System.nanoTime();
      PhysicsMod.createClothDirectory();
      this.entityXPosition = (int)(this.width * 0.25);
      this.addRenderableWidget(
         (Button)((Animatable)ButtonSettings.builder(
               this.entityXPosition - 10, this.height - 57, 20, 20, Component.literal("?"), button -> this.rotationSpeed = 25.0
            ))
            .setAnimDepth(200.0F)
      );
      this.addRenderableWidget(
         this.rotateLeft = (Button)((Animatable)ButtonSettings.builder(
               this.entityXPosition - 35, this.height - 57, 20, 20, Component.literal("<"), button -> {}
            ))
            .setAnimDepth(200.0F)
      );
      this.addRenderableWidget(
         this.rotateRight = (Button)((Animatable)ButtonSettings.builder(
               this.entityXPosition + 15, this.height - 57, 20, 20, Component.literal(">"), button -> {}
            ))
            .setAnimDepth(200.0F)
      );
      this.goToCategoryScreen();
   }

   private void goToCategoryScreen() {
      if (this.activeList != null) {
         this.children.remove(this.activeList);
      }

      if (this.reloadLater) {
         this.customTitle = Component.translatable("physicsmod.menu.cloth.downloading.title");
      } else {
         this.customTitle = Component.translatable("physicsmod.menu.cloth.categoryselection.title", new Object[]{getEntityName(this.selectedEntity)});
      }

      this.setBottomWidgets(
         ButtonSettings.builder(
            0,
            0,
            100,
            20,
            Component.translatable("physicsmod.menu.cloth.selection.changeEntity"),
            button -> this.minecraft.setScreen(new ClothEntitySelectionScreen(this, this.minecraft.options))
         ),
         ButtonSettings.builder(0, 0, 100, 20, CommonComponents.GUI_DONE, button -> this.onClose())
      );
      CategorySelectionList list = new CategorySelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(list);
      this.activeList = list;
   }

   private void goToPartsScreen() {
      if (this.activeList != null) {
         this.children.remove(this.activeList);
      }

      this.customTitle = Component.translatable("physicsmod.menu.cloth.partselection.title", new Object[]{getEntityName(this.selectedEntity)});
      this.setBottomWidgets(
         ButtonSettings.builder(
            0,
            0,
            100,
            20,
            Component.translatable("physicsmod.menu.cloth.selection.changeEntity"),
            button -> this.minecraft.setScreen(new ClothEntitySelectionScreen(this, this.minecraft.options))
         ),
         ButtonSettings.builder(0, 0, 100, 20, CommonComponents.GUI_DONE, button -> this.onClose())
      );
      this.partList = new PartSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.partList);
      this.activeList = this.partList;
   }

   public void goToClothScreen(String selectedCategory) {
      if (this.activeList != null) {
         this.children.remove(this.activeList);
      }

      this.customTitle = Component.translatable("physicsmod.menu.cloth.clothselection.title", new Object[]{getEntityName(this.selectedEntity)});
      this.setBottomWidgets(
         ButtonSettings.builder(
            0,
            this.height - 27,
            100,
            20,
            Component.translatable("physicsmod.menu.cloth.selection.openFolder"),
            button -> Util.getPlatform().openFile(PhysicsMod.CLOTH_DIRECTORY.toFile())
         ),
         ButtonSettings.builder(0, this.height - 27, 100, 20, Component.translatable("physicsmod.gui.select"), button -> this.goToCategoryScreen())
      );
      ClothSelectionList list = new ClothSelectionList(this, selectedCategory, this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(list);
      this.activeList = list;
   }

   public static String getEntityName(String selectedEntity) {
      if (selectedEntity.equals("physicsmod:yourself")) {
         return Language.getInstance().getOrDefault("physicsmod.menu.cloth.clothselection.yourself");
      } else {
         return selectedEntity.equals("minecraft:player")
            ? Language.getInstance().getOrDefault("physicsmod.menu.cloth.clothselection.allPlayers")
            : selectedEntity.replace("physicsmod:player:", "");
      }
   }

   public void setBottomWidgets(AbstractWidget... newWidgets) {
      for (AbstractWidget widget : this.bottomWidgets) {
         this.removeWidget(widget);
      }

      this.bottomWidgets.clear();
      int width = 100;
      int padding = 10;
      int y = this.height - 27;
      int centerX = this.width / 2;
      int offsetX = centerX - (width * newWidgets.length + (newWidgets.length - 1) * padding) / 2;

      for (AbstractWidget widget : newWidgets) {
         this.bottomWidgets.add(widget);
         widget.setY(y);
         widget.setX(offsetX);
         widget.setWidth(width);
         this.addRenderableWidget(widget);
         offsetX += width + padding;
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.activeList.render(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.drawCenteredString(this.font, this.customTitle, this.width / 2, 15, 16777215);
      if (this.rotateLeft.isHoveredOrFocused()) {
         this.rotationSpeed = -6.0;
      } else if (this.rotateRight.isHoveredOrFocused()) {
         this.rotationSpeed = 6.0;
      }

      this.updateCloth();
      this.renderEntity();
      super.render(guiGraphics, mouseX, mouseY, delta);
   }

   public void tick() {
      if (this.reloadLater && ConfigCloth.clothUpToDate) {
         this.init();
      }

      super.tick();
   }

   private void updateCloth() {
      long time = System.nanoTime();
      double passedTime = (System.nanoTime() - this.lastTime) / 1.0E9;
      this.lastTime = time;
      this.timeDelta += passedTime;

      while (this.timeDelta >= 0.016666666666666666) {
         this.timeDelta -= 0.016666666666666666;
         this.rotationSpeed = Math.lerp(this.rotationSpeed, 0.0, 0.06);
         this.rotateEntity();

         for (VerletSimulation simulation : this.simulations.values()) {
            this.rotateCloth(simulation);
            simulation.update(null, 0.016666666666666666);
         }
      }
   }

   private void rotateCloth(VerletSimulation simulation) {
      PoseStack stack = new PoseStack();
      stack.last().pose().rotate(new Quaternionf().rotationXYZ(0.0F, (float)(-java.lang.Math.toRadians(this.totalRotation)), 0.0F));
      simulation.getConstraint(ModelPartConstraint.class).modelPartTransformation(stack.last().pose());
      Matrix4d rotation = new Matrix4d(stack.last().pose());
      ColladaMesh mesh = simulation.cloth.mesh;
      int size = mesh.positions.size();
      List<Vector3f> positions = mesh.positions;
      List<VerletPoint> points = simulation.getPoints();
      Vector3d tmp = new Vector3d();

      for (int i = 0; i < points.size() && i < size; i++) {
         VerletPoint point = points.get(i);
         Vector3f pos = positions.get(i);
         tmp.set(pos.x, pos.y, pos.z);
         if (point.locked) {
            rotation.transformPosition(tmp);
            point.position.set(tmp);
         } else if (point.softRestriction != null) {
            rotation.transformPosition(tmp);
            point.softRestriction.set(tmp);
         }
      }
   }

   private void rotateEntity() {
      this.totalRotation = this.totalRotation + this.rotationSpeed;
   }

   private void renderEntity() {
      Matrix4fStack matrices = RenderSystem.getModelViewStack();
      float scale = this.height * 0.215F;
      double xPosition = this.entityXPosition;
      double yPosition = this.height * 0.5;
      float depth = 100.0F;
      matrices.pushMatrix();
      matrices.translate((float)xPosition, (float)yPosition, depth);
      matrices.scale(scale, scale, scale);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableDepthTest();
      Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
      Lighting.setupForEntityInInventory();
      RenderSystem.enableBlend();
      RenderSystem.enableCull();
      double mobWidth = this.endX - this.startX;
      double mobHeight = this.endY - this.startY;
      double mobDepth = this.endZ - this.startZ;
      float mobScale = -1.0F / (float)java.lang.Math.max(mobWidth, java.lang.Math.max(mobHeight, mobDepth)) * 2.0F;
      matrices.scale(mobScale, mobScale, mobScale);
      matrices.translate((float)(-mobWidth * 0.5 - this.startX), (float)(-mobHeight * 0.5 + this.endY), (float)(-mobDepth * 0.5 - this.startZ));
      matrices.rotate(
         new Quaternionf()
            .rotationXYZ((float)java.lang.Math.toRadians(-25.0), (float)java.lang.Math.toRadians(this.totalRotation), (float)java.lang.Math.toRadians(180.0))
      );
      RenderSystem.applyModelViewMatrix();

      try {
         RenderType renderType = RenderType.entityCutout(this.textureLocation);
         if (this.partList != null && this.partList.getHovered() != null) {
            String selected = (String)this.partList.getHovered().getUserData();
            ObjectListIterator vertexConsumer = ClothConstants.getModelParts(this.model).iterator();

            while (vertexConsumer.hasNext()) {
               ModelPart part = (ModelPart)vertexConsumer.next();
               if (selected.equals(((ModelPartParent)part).physicsmod$getName())) {
                  part.visible = true;
               } else {
                  part.visible = false;
               }
            }
         }

         ClothConstants.hideProperParts(this.selectedEntity, this.model);
         BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
         VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
         this.model.renderToBuffer(new PoseStack(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
         bufferSource.endBatch();
         ObjectListIterator var26 = ClothConstants.getModelParts(this.model).iterator();

         while (var26.hasNext()) {
            ModelPart part = (ModelPart)var26.next();
            part.visible = !part.visible;
         }

         ClothConstants.hideProperParts(this.selectedEntity, this.model);
         renderType = RenderType.entityTranslucent(this.textureLocation);
         vertexConsumer = bufferSource.getBuffer(renderType);
         this.model.renderToBuffer(new PoseStack(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, ARGB32.colorFromFloat(0.175F, 1.0F, 1.0F, 1.0F));
         bufferSource.endBatch();
         var26 = ClothConstants.getModelParts(this.model).iterator();

         while (var26.hasNext()) {
            ModelPart part = (ModelPart)var26.next();
            part.visible = true;
         }
      } catch (Exception var20) {
      }

      ShaderInstance shader = RenderSystem.getShader();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShader(GameRenderer::getRendertypeArmorCutoutNoCullShader);
      RenderSystem.enableDepthTest();
      Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
      Lighting.setupForEntityInInventory();
      RenderSystem.disableCull();
      RenderSystem.activeTexture(33984);
      this.renderStaticCloth(matrices);
      matrices.popMatrix();
      matrices.pushMatrix();
      matrices.translate((float)xPosition, (float)yPosition, depth);
      matrices.scale(scale, scale, scale);
      matrices.scale(mobScale, mobScale, mobScale);
      matrices.translate((float)(-mobWidth * 0.5 - this.startX), (float)(-mobHeight * 0.5 + this.endY), (float)(-mobDepth * 0.5 - this.startZ));
      matrices.rotate(new Quaternionf().rotationXYZ((float)java.lang.Math.toRadians(-25.0), 0.0F, (float)java.lang.Math.toRadians(180.0)));
      RenderSystem.applyModelViewMatrix();
      this.renderCloth(matrices);
      matrices.popMatrix();
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
      RenderSystem.setShader(() -> shader);
   }

   private void renderCloth(Matrix4fStack matrices) {
      for (VerletSimulation simulation : this.simulations.values()) {
         int glID = simulation.textureID;
         RenderSystem.setShaderTexture(0, glID);
         RenderSystem.bindTexture(glID);
         int brightness = 15728640;
         simulation.brightness = brightness;
         if (simulation.cloth.rules.isDynamic()) {
            simulation.render(matrices);
         }
      }
   }

   private void renderStaticCloth(Matrix4fStack matrices) {
      for (VerletSimulation simulation : this.simulations.values()) {
         int brightness = 15728640;
         matrices.pushMatrix();
         simulation.getConstraint(ModelPartConstraint.class).modelPartTransformation(matrices);
         RenderSystem.applyModelViewMatrix();
         if (!simulation.cloth.rules.isDynamic()) {
            int glID = simulation.textureID;
            RenderSystem.setShaderTexture(0, glID);
            RenderSystem.bindTexture(glID);
            simulation.cloth.mesh.renderSlow(brightness, ConfigClient.clothSmoothShading);
         }

         if (simulation.cloth.playerMesh != null && this.textureLocation != null) {
            int glID = Minecraft.getInstance().getTextureManager().getTexture(this.textureLocation).getId();
            RenderSystem.setShaderTexture(0, glID);
            RenderSystem.bindTexture(glID);
            simulation.cloth.playerMesh.renderSlow(brightness, false);
         }

         matrices.popMatrix();
      }
   }

   public void loadModelAndTexture() {
      EntityRenderer renderer = PhysicsMod.renderers.get(this.entityType);
      this.textureLocation = MobEntry.getTextureLocation(renderer, this.entityType);
      this.model = MobEntry.getModel(renderer, this.entityType);
      if (this.model instanceof EntityModel entityModel) {
         entityModel.young = false;
      }

      if (this.entityType == EntityType.PLAYER) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            this.textureLocation = MobEntry.getTextureLocation(renderer, this.entityType, player);
            this.model = MobEntry.getModel(renderer, this.entityType, player);
            if (this.model instanceof EntityModel entityModel) {
               entityModel.young = false;
            }
         }
      }

      PhysicsMod.sodiumCatchBoundingBox = true;
      PhysicsMod.sodiumBoundingBox.start.set(1.7976931348623157E308);
      PhysicsMod.sodiumBoundingBox.end.set(-1.7976931348623157E308);

      try {
         BoundingBoxGetter boundingBox = StarterClient.sodium ? Sodium.getNewBoundingBoxConsumer() : new BoundingBoxGetter();
         this.model.renderToBuffer(new PoseStack(), boundingBox, 15728880, OverlayTexture.NO_OVERLAY);
         if (StarterClient.sodium) {
            boundingBox.min = PhysicsMod.sodiumBoundingBox.getMin();
            boundingBox.max = PhysicsMod.sodiumBoundingBox.getMax();
         }

         this.startX = boundingBox.min.x;
         this.endX = boundingBox.max.x;
         this.startY = boundingBox.min.y;
         this.endY = boundingBox.max.y;
         this.startZ = boundingBox.min.z;
         this.endZ = boundingBox.max.z;
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      PhysicsMod.sodiumCatchBoundingBox = false;
   }

   public void loadCloth() {
      Map<String, ConfigCloth.ClothList> customizations = ConfigCloth.getCustomizationParts(this.selectedEntity);
      this.simulations = new Object2ObjectOpenHashMap();
      if (customizations != null) {
         for (Entry<String, ConfigCloth.ClothList> clothParts : customizations.entrySet()) {
            String modelPart = clothParts.getKey();
            ConfigCloth.ClothList clothList = clothParts.getValue();

            for (String clothPart : clothList.getClothPieces()) {
               Cloth cloth = PhysicsMod.cloth.get(clothPart);
               if (cloth != null) {
                  VerletSimulation simulation = new VerletSimulation(new Vector3d(DynamicsWorld.DEFAULT_GRAVITY).negate(), 45, 0.92, new Vector3d(0.0));
                  ModelPartConstraint modelPartConstraint = new ModelPartConstraint(simulation, cloth.rules.getIgnoreParts(), null, modelPart, this.model);
                  modelPartConstraint.setCustomTransformation(
                     matrix -> matrix.mulPose(new Quaternionf().rotationXYZ(0.0F, (float)(-java.lang.Math.toRadians(this.totalRotation)), 0.0F))
                  );
                  simulation.addConstraint(modelPartConstraint);
                  PoseStack modelMatrix = new PoseStack();
                  modelMatrix.last().pose().rotate(new Quaternionf().rotationXYZ(0.0F, (float)(-java.lang.Math.toRadians(this.totalRotation)), 0.0F));
                  modelPartConstraint.modelPartTransformation(modelMatrix.last().pose());
                  Matrix4d partTransformation = new Matrix4d(modelMatrix.last().pose());
                  int texture = cloth.getTexture(null);
                  if (texture == -1) {
                     texture = Minecraft.getInstance().getTextureManager().getTexture(this.textureLocation).getId();
                  }

                  simulation.addCloth(cloth, texture, partTransformation, false);
                  simulation.setTransformation(partTransformation);
                  simulation.setBufferTransformation(partTransformation);
                  simulation.updateOffsets();
                  simulation.calculateNormals();
                  modelPartConstraint.initAsyncData(null, simulation);
                  modelPartConstraint.changeInstantly = true;
                  modelPartConstraint.updateAfter(0.0, simulation);
                  simulation.downloadData();
                  this.simulations.put(modelPart + clothPart, simulation);
               }
            }
         }
      }
   }

   public String getSelectedEntity() {
      return this.selectedEntity;
   }

   public void setSelectedEntity(String selectedEntity) {
      this.selectedEntity = selectedEntity;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
      ConfigCloth.isChangingPlayer = false;
      Map<String, ConfigCloth.ClothList> toCheck = ConfigCloth.getCustomizationParts("physicsmod:yourself");
      if (toCheck == null) {
         toCheck = new Object2ObjectOpenHashMap();
      }

      if (!toCheck.equals(this.playerCopy)) {
         UUID uuid = ConfigCloth.getMinecraftUUID();
         if (uuid != null) {
            String uuidString = uuid.toString();
            ConfigCloth.setCustomizationParts(uuidString, toCheck);
            JsonArray array = new JsonArray();

            for (Entry<String, ConfigCloth.ClothList> entry : toCheck.entrySet()) {
               for (String part : entry.getValue().getClothPieces()) {
                  array.add(part);
               }
            }
         } else {
            StarterClient.logger.error("Couldn't find player uuid");
         }
      }

      ConfigCloth.save();
      PhysicsMod.resetClothSimulations();
   }

   public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
   }
}
