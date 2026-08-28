/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.model.CreeperModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.builders.CubeDeformation
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.Squid
 *  net.minecraft.world.entity.animal.sniffer.Sniffer
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.entity.monster.Guardian
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
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
import net.minecraft.client.gui.components.events.GuiEventListener;
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
import org.joml.Quaternionfc;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.config.screens.ETFConfigScreenWarnings;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.tconfig.gui.TConfigScreenMain;
import traben.tconfig.gui.entries.TConfigEntryCategory;

public class ETFConfigScreenMain
extends TConfigScreenMain {
    final Set<ETFConfigWarning> warningsFound = new HashSet<ETFConfigWarning>();
    private final Random rand = new Random();
    private final LogoCreeperRenderer LOGO_CREEPER = new LogoCreeperRenderer();
    private final ResourceLocation BLUE = ETFUtils2.res("entity_features", "textures/gui/entity/e.png");
    private final ResourceLocation RED = ETFUtils2.res("entity_features", "textures/gui/entity/t.png");
    private final ResourceLocation YELLOW = ETFUtils2.res("entity_features", "textures/gui/entity/f.png");
    boolean shownWarning = false;
    int warningCount = 0;
    private long timer = 0L;
    private LivingEntity livingEntity = null;

    public ETFConfigScreenMain(Screen parent) {
        super("config.entity_features", parent, ETF.configHandlers, List.of(new TConfigEntryCategory("config.entity_features.textures_main"), new TConfigEntryCategory("config.entity_features.models_main").setEmptyTooltip("config.entity_features.empty_emf"), new TConfigEntryCategory("config.entity_features.sounds_main").setEmptyTooltip("config.entity_features.empty_esf"), new TConfigEntryCategory("config.entity_features.general_settings.title"), new TConfigEntryCategory("config.entity_features.optifine_settings"), new TConfigEntryCategory("config.entity_features.per_entity_settings")));
        for (ETFConfigWarning warning : ETFConfigWarnings.getRegisteredWarnings()) {
            if (!warning.isConditionMet()) continue;
            this.shownWarning = true;
            ++this.warningCount;
            this.warningsFound.add(warning);
        }
    }

    public static void drawEntity(GuiGraphics context, float x, float y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity, int mouseX, int mouseY) {
        context.pose().pushPose();
        context.pose().translate((double)x, (double)y, 150.0);
        context.pose().mulPose(new Matrix4f().scaling((float)size, (float)size, (float)(-size)));
        context.pose().mulPose(quaternionf);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.overrideCameraOrientation(quaternionf2);
        }
        entityRenderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render((Entity)entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, context.pose(), (MultiBufferSource)context.bufferSource(), 0xF000F0));
        context.flush();
        entityRenderDispatcher.setRenderShadow(true);
        context.pose().popPose();
        Lighting.setupFor3DItems();
    }

    @Override
    protected void init() {
        super.init();
        if (this.shownWarning) {
            this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"config.entity_features.warnings_main"), button -> Objects.requireNonNull(this.minecraft).setScreen((Screen)new ETFConfigScreenWarnings(this, this.warningsFound))).bounds((int)((double)this.width * 0.1), (int)((double)this.height * 0.1) - 15, (int)((double)this.width * 0.2), 20).build());
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.timer + 5000L < System.currentTimeMillis() && Minecraft.getInstance().player != null && Minecraft.getInstance().player.level() != null) {
            List entityList = Minecraft.getInstance().player.level().getEntities(null, Minecraft.getInstance().player.getBoundingBox().inflate(128.0));
            Entity entity = null;
            for (int i = 0; i < Math.min(entityList.size(), 24) && !((entity = (Entity)entityList.get(this.rand.nextInt(entityList.size()))) instanceof LivingEntity); ++i) {
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
        int y = (int)((double)this.height * 0.75);
        int x = (int)((double)this.width * 0.33);
        float g = (float)(-Math.atan(((float)(-mouseY) + (float)this.height / 2.0f) / 40.0f));
        float g2 = (float)(-Math.atan(((float)(-mouseX) + (float)this.width / 3.0f) / 400.0f));
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI).rotateY(g2);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(-(g * 20.0f * ((float)Math.PI / 180)));
        quaternionf.mul((Quaternionfc)quaternionf2);
        context.pose().pushPose();
        context.pose().translate((double)x, (double)y, 150.0);
        float scaling = (float)((double)this.height * 0.3);
        context.pose().mulPose(new Matrix4f().scaling(scaling, scaling, -scaling));
        context.pose().mulPose(quaternionf);
        Lighting.setupForEntityInInventory();
        PoseStack matrixStack = context.pose();
        float sin1 = (float)(Math.sin((double)System.currentTimeMillis() / 500.0) / 32.0);
        float sin2 = (float)(Math.sin((double)System.currentTimeMillis() / 500.0 + 1.0) / 32.0);
        float sin3 = (float)(Math.sin((double)System.currentTimeMillis() / 500.0 + 2.0) / 32.0);
        MultiBufferSource.BufferSource bufferSource = context.bufferSource();
        matrixStack.pushPose();
        matrixStack.translate(-0.6, (double)(-sin1), 0.0);
        matrixStack.scale(1.0f + sin1, 1.0f + sin1, 1.0f + sin1);
        this.LOGO_CREEPER.renderSimple(matrixStack, (MultiBufferSource)bufferSource, this.YELLOW);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0f, -sin2, 0.0f);
        matrixStack.scale(1.0f + sin2, 1.0f + sin2, 1.0f + sin2);
        this.LOGO_CREEPER.renderSimple(matrixStack, (MultiBufferSource)bufferSource, this.RED);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.6, (double)(-sin3), 0.0);
        matrixStack.scale(1.0f + sin3, 1.0f + sin3, 1.0f + sin3);
        this.LOGO_CREEPER.renderSimple(matrixStack, (MultiBufferSource)bufferSource, this.BLUE);
        matrixStack.popPose();
        Lighting.setupFor3DItems();
    }

    private void renderEntitySample(GuiGraphics context, int mouseX, int mouseY) {
        double scale;
        int y = (int)((double)this.height * 0.75);
        if ((double)this.livingEntity.getBbHeight() < 0.7) {
            y -= (int)((double)this.height * 0.15);
        }
        int x = (int)((double)this.width * 0.33);
        float g = (float)Math.atan(((float)(-mouseY) + (float)this.height / 2.0f) / 40.0f);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI).rotateY((float)((double)System.currentTimeMillis() / 1000.0 % (Math.PI * 2)));
        Quaternionf quaternionf2 = new Quaternionf().rotateX(-(g * 20.0f * ((float)Math.PI / 180)));
        quaternionf.mul((Quaternionfc)quaternionf2);
        double autoScale = (double)this.height * 0.4 / (double)Math.max(1.0f, Math.max(this.livingEntity.getBbHeight(), this.livingEntity.getBbWidth()));
        if (this.livingEntity instanceof Squid) {
            y -= (int)((double)this.height * 0.15);
            scale = autoScale * 0.5;
        } else if (this.livingEntity instanceof Guardian || this.livingEntity instanceof Sniffer) {
            y -= (int)((double)this.height * 0.1);
            scale = autoScale * 0.7;
        } else if (this.livingEntity instanceof EnderDragon) {
            y -= (int)((double)this.height * 0.15);
            scale = autoScale * 1.5;
        } else {
            scale = autoScale;
        }
        double scaleModify = Math.sin((double)(System.currentTimeMillis() - this.timer) / 5000.0 * Math.PI) * 6.0;
        double scaleModify2 = Math.max(Math.min(Math.abs(scaleModify), 1.0), 0.0);
        int modelHeight = (int)Math.min(scale * scaleModify2, (double)this.height * 0.4);
        context.pose().pushPose();
        ETFConfigScreenMain.drawEntity(context, x, y, modelHeight, quaternionf, quaternionf2, this.livingEntity, mouseX, mouseY);
    }

    public static class LogoCreeperRenderer {
        private final ModelPart root = CreeperModel.createBodyLayer((CubeDeformation)CubeDeformation.NONE).bakeRoot();

        public LogoCreeperRenderer() {
            this.root.getChild((String)"right_hind_leg").xRot = (float)(-Math.toRadians(25.0));
            this.root.getChild((String)"left_hind_leg").xRot = (float)Math.toRadians(25.0);
            this.root.getChild((String)"left_hind_leg").z -= 2.0f;
            this.root.getChild((String)"right_front_leg").xRot = (float)Math.toRadians(25.0);
            this.root.getChild((String)"left_front_leg").xRot = (float)(-Math.toRadians(25.0));
            this.root.getChild((String)"left_front_leg").z += 2.0f;
        }

        public void renderSimple(PoseStack matrix, MultiBufferSource vcp, ResourceLocation texture) {
            matrix.pushPose();
            matrix.scale(-1.0f, -1.0f, 1.0f);
            matrix.translate(0.0f, -1.501f, 0.0f);
            RenderType rendertype = RenderType.entitySolid((ResourceLocation)texture);
            if (rendertype != null) {
                VertexConsumer vertexconsumer = vcp.getBuffer(rendertype);
                this.root.render(matrix, vertexconsumer, 0xF000F0, OverlayTexture.NO_OVERLAY);
            }
            matrix.popPose();
        }
    }
}

