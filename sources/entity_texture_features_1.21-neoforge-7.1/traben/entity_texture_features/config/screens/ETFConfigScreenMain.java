package traben.entity_texture_features.config.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Guardian;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.tconfig.gui.TConfigScreenMain;
import traben.tconfig.gui.entries.TConfigEntryCategory;

public class ETFConfigScreenMain extends TConfigScreenMain {
   final Set<ETFConfigWarning> warningsFound = new HashSet<>();
   private final Random rand = new Random();
   private final ETFConfigScreenMain.LogoCreeperRenderer LOGO_CREEPER = new ETFConfigScreenMain.LogoCreeperRenderer();
   private final ResourceLocation BLUE = ETFUtils2.res("entity_features", "textures/gui/entity/e.png");
   private final ResourceLocation RED = ETFUtils2.res("entity_features", "textures/gui/entity/t.png");
   private final ResourceLocation YELLOW = ETFUtils2.res("entity_features", "textures/gui/entity/f.png");
   boolean shownWarning = false;
   int warningCount = 0;
   private long timer = 0L;
   private LivingEntity livingEntity = null;

   public ETFConfigScreenMain(Screen parent) {
      super(
         "config.entity_features",
         parent,
         ETF.configHandlers,
         List.of(
            new TConfigEntryCategory("config.entity_features.textures_main"),
            new TConfigEntryCategory("config.entity_features.models_main").setEmptyTooltip("config.entity_features.empty_emf"),
            new TConfigEntryCategory("config.entity_features.sounds_main").setEmptyTooltip("config.entity_features.empty_esf"),
            new TConfigEntryCategory("config.entity_features.general_settings.title"),
            new TConfigEntryCategory("config.entity_features.optifine_settings"),
            new TConfigEntryCategory("config.entity_features.per_entity_settings")
         )
      );

      for (ETFConfigWarning warning : ETFConfigWarnings.getRegisteredWarnings()) {
         if (warning.isConditionMet()) {
            this.shownWarning = true;
            this.warningCount++;
            this.warningsFound.add(warning);
         }
      }
   }

   public static void drawEntity(
      GuiGraphics context, float x, float y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity, int mouseX, int mouseY
   ) {
      context.pose().pushPose();
      context.pose().translate(x, y, 150.0);
      context.pose().mulPose(new Matrix4f().scaling(size, size, -size));
      context.pose().mulPose(quaternionf);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      if (quaternionf2 != null) {
         quaternionf2.conjugate();
         entityRenderDispatcher.overrideCameraOrientation(quaternionf2);
      }

      entityRenderDispatcher.setRenderShadow(false);
      RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.pose(), context.bufferSource(), 15728880));
      context.flush();
      entityRenderDispatcher.setRenderShadow(true);
      context.pose().popPose();
      Lighting.setupFor3DItems();
   }

   @Override
   protected void init() {
      super.init();
      if (this.shownWarning) {
         this.addRenderableWidget(
            Button.builder(
                  Component.translatable("config.entity_features.warnings_main"),
                  button -> Objects.requireNonNull(this.minecraft).setScreen(new ETFConfigScreenWarnings(this, this.warningsFound))
               )
               .bounds((int)(this.width * 0.1), (int)(this.height * 0.1) - 15, (int)(this.width * 0.2), 20)
               .build()
         );
      }
   }

   @Override
   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      if (this.timer + 5000L < System.currentTimeMillis() && Minecraft.getInstance().player != null && Minecraft.getInstance().player.level() != null) {
         List<Entity> entityList = Minecraft.getInstance().player.level().getEntities(null, Minecraft.getInstance().player.getBoundingBox().inflate(128.0));
         Entity entity = null;

         for (int i = 0; i < Math.min(entityList.size(), 24); i++) {
            entity = entityList.get(this.rand.nextInt(entityList.size()));
            if (entity instanceof LivingEntity) {
               break;
            }
         }

         if (entity instanceof LivingEntity) {
            this.livingEntity = (LivingEntity)entity;
            this.timer = System.currentTimeMillis();
         }
      }

      if (this.livingEntity != null && !this.livingEntity.isRemoved()) {
         this.renderEntitySample(context, mouseX, mouseY);
      } else {
         this.renderETFLogoCreepers(context, mouseX, mouseY);
      }

      context.pose().popPose();
   }

   private void renderETFLogoCreepers(GuiGraphics context, int mouseX, int mouseY) {
      int y = (int)(this.height * 0.75);
      int x = (int)(this.width * 0.33);
      float g = (float)(-Math.atan((-mouseY + this.height / 2.0F) / 40.0F));
      float g2 = (float)(-Math.atan((-mouseX + this.width / 3.0F) / 400.0F));
      Quaternionf quaternionf = new Quaternionf().rotateZ(3.1415927F).rotateY(g2);
      Quaternionf quaternionf2 = new Quaternionf().rotateX(-(g * 20.0F * 0.017453292F));
      quaternionf.mul(quaternionf2);
      context.pose().pushPose();
      context.pose().translate(x, y, 150.0);
      float scaling = (float)(this.height * 0.3);
      context.pose().mulPose(new Matrix4f().scaling(scaling, scaling, -scaling));
      context.pose().mulPose(quaternionf);
      Lighting.setupForEntityInInventory();
      PoseStack matrixStack = context.pose();
      float sin1 = (float)(Math.sin(System.currentTimeMillis() / 500.0) / 32.0);
      float sin2 = (float)(Math.sin(System.currentTimeMillis() / 500.0 + 1.0) / 32.0);
      float sin3 = (float)(Math.sin(System.currentTimeMillis() / 500.0 + 2.0) / 32.0);
      MultiBufferSource bufferSource = context.bufferSource();
      matrixStack.pushPose();
      matrixStack.translate(-0.6, -sin1, 0.0);
      matrixStack.scale(1.0F + sin1, 1.0F + sin1, 1.0F + sin1);
      this.LOGO_CREEPER.renderSimple(matrixStack, bufferSource, this.YELLOW);
      matrixStack.popPose();
      matrixStack.pushPose();
      matrixStack.translate(0.0F, -sin2, 0.0F);
      matrixStack.scale(1.0F + sin2, 1.0F + sin2, 1.0F + sin2);
      this.LOGO_CREEPER.renderSimple(matrixStack, bufferSource, this.RED);
      matrixStack.popPose();
      matrixStack.pushPose();
      matrixStack.translate(0.6, -sin3, 0.0);
      matrixStack.scale(1.0F + sin3, 1.0F + sin3, 1.0F + sin3);
      this.LOGO_CREEPER.renderSimple(matrixStack, bufferSource, this.BLUE);
      matrixStack.popPose();
      Lighting.setupFor3DItems();
   }

   private void renderEntitySample(GuiGraphics context, int mouseX, int mouseY) {
      int y = (int)(this.height * 0.75);
      if (this.livingEntity.getBbHeight() < 0.7) {
         y -= (int)(this.height * 0.15);
      }

      int x = (int)(this.width * 0.33);
      float g = (float)Math.atan((-mouseY + this.height / 2.0F) / 40.0F);
      Quaternionf quaternionf = new Quaternionf().rotateZ(3.1415927F).rotateY((float)(System.currentTimeMillis() / 1000.0 % 6.283185307179586));
      Quaternionf quaternionf2 = new Quaternionf().rotateX(-(g * 20.0F * 0.017453292F));
      quaternionf.mul(quaternionf2);
      double autoScale = this.height * 0.4 / Math.max(1.0F, Math.max(this.livingEntity.getBbHeight(), this.livingEntity.getBbWidth()));
      double scale;
      if (this.livingEntity instanceof Squid) {
         y -= (int)(this.height * 0.15);
         scale = autoScale * 0.5;
      } else if (this.livingEntity instanceof Guardian || this.livingEntity instanceof Sniffer) {
         y -= (int)(this.height * 0.1);
         scale = autoScale * 0.7;
      } else if (this.livingEntity instanceof EnderDragon) {
         y -= (int)(this.height * 0.15);
         scale = autoScale * 1.5;
      } else {
         scale = autoScale;
      }

      double scaleModify = Math.sin((System.currentTimeMillis() - this.timer) / 5000.0 * 3.141592653589793) * 6.0;
      double scaleModify2 = Math.max(Math.min(Math.abs(scaleModify), 1.0), 0.0);
      int modelHeight = (int)Math.min(scale * scaleModify2, this.height * 0.4);
      context.pose().pushPose();
      drawEntity(context, x, y, modelHeight, quaternionf, quaternionf2, this.livingEntity, mouseX, mouseY);
   }

   public static class LogoCreeperRenderer {
      private final ModelPart root = CreeperModel.createBodyLayer(CubeDeformation.NONE).bakeRoot();

      public LogoCreeperRenderer() {
         this.root.getChild("right_hind_leg").xRot = (float)(-Math.toRadians(25.0));
         this.root.getChild("left_hind_leg").xRot = (float)Math.toRadians(25.0);
         this.root.getChild("left_hind_leg").z -= 2.0F;
         this.root.getChild("right_front_leg").xRot = (float)Math.toRadians(25.0);
         this.root.getChild("left_front_leg").xRot = (float)(-Math.toRadians(25.0));
         this.root.getChild("left_front_leg").z += 2.0F;
      }

      public void renderSimple(PoseStack matrix, MultiBufferSource vcp, ResourceLocation texture) {
         matrix.pushPose();
         matrix.scale(-1.0F, -1.0F, 1.0F);
         matrix.translate(0.0F, -1.501F, 0.0F);
         RenderType rendertype = RenderType.entitySolid(texture);
         if (rendertype != null) {
            VertexConsumer vertexconsumer = vcp.getBuffer(rendertype);
            this.root.render(matrix, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY);
         }

         matrix.popPose();
      }
   }
}
