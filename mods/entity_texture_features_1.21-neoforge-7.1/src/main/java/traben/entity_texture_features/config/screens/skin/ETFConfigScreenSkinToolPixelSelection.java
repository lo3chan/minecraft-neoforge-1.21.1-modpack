/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.config.screens.skin.ETFScreenOldCompat;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFConfigScreenSkinToolPixelSelection
extends ETFScreenOldCompat {
    private final SelectionMode MODE;
    private final ETFConfigScreenSkinTool etfParent;
    Set<Integer> selectedPixels;
    ResourceLocation currentSkinToRender = ETFUtils2.res("entity_texture_features:textures/gui/icon.png");

    protected ETFConfigScreenSkinToolPixelSelection(ETFConfigScreenSkinTool parent, SelectionMode mode) {
        super("config.entity_texture_features" + (mode == SelectionMode.EMISSIVE ? ".emissive_select" : ".enchanted_select") + ".title", parent, false);
        this.MODE = mode;
        this.etfParent = parent;
    }

    @Override
    protected void init() {
        super.init();
        ResourceLocation randomID = ETFUtils2.res("entity_texture_features_ignore", "gui_skin_" + System.currentTimeMillis() + ".png");
        if (ETFUtils2.registerNativeImageToIdentifier(this.etfParent.currentEditorSkin, randomID)) {
            this.currentSkinToRender = randomID;
        }
        this.selectedPixels = new HashSet<Integer>();
        for (int x = this.MODE.startX; x < this.MODE.startX + 8; ++x) {
            for (int y = this.MODE.startY; y < this.MODE.startY + 8; ++y) {
                int color = ETFUtils2.getPixel(this.etfParent.currentEditorSkin, x, y);
                if (color == 0) continue;
                this.selectedPixels.add(color);
            }
        }
        this.addRenderableWidget((GuiEventListener)this.getETFButton((int)((double)this.width * 0.024), (int)((double)this.height * 0.2), 20, 20, Component.nullToEmpty((String)"\u27f3"), button -> {
            this.etfParent.flipView = !this.etfParent.flipView;
        }));
        this.addRenderableWidget((GuiEventListener)this.getETFButton((int)((double)this.width * 0.55), (int)((double)this.height * 0.9), (int)((double)this.width * 0.2), 20, CommonComponents.GUI_BACK, button -> Objects.requireNonNull(this.minecraft).setScreen(this.parent)));
        int pixelSize = (int)((double)this.height * 0.7 / 64.0);
        for (int x = 0; x < 64; ++x) {
            for (int y = 0; y < 64; ++y) {
                Button butt = this.getButtonPixels(x, y, pixelSize);
                this.addRenderableWidget((GuiEventListener)butt);
            }
        }
    }

    @NotNull
    private Button getButtonPixels(int x, int y, int pixelSize) {
        return new Button(this, (int)((double)this.width * 0.35 + (double)(x * pixelSize)), (int)((double)this.height * 0.2 + (double)(y * pixelSize)), pixelSize, pixelSize, Component.nullToEmpty((String)""), button -> {
            int colorAtPixel = ETFUtils2.getPixel(this.etfParent.currentEditorSkin, x, y);
            if (this.selectedPixels.contains(colorAtPixel)) {
                this.selectedPixels.remove(colorAtPixel);
            } else {
                this.selectedPixels.add(colorAtPixel);
            }
            this.applyCurrentSelectedPixels();
            this.etfParent.thisETFPlayerTexture.changeSkinToThisForTool(this.etfParent.currentEditorSkin);
            ResourceLocation randomID2 = ETFUtils2.res("entity_texture_features_ignore", "gui_skin_" + System.currentTimeMillis() + ".png");
            if (ETFUtils2.registerNativeImageToIdentifier(this.etfParent.currentEditorSkin, randomID2)) {
                this.currentSkinToRender = randomID2;
            }
        }, Supplier::get){

            protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            }
        };
    }

    private void applyCurrentSelectedPixels() {
        ArrayList<Integer> integerSet = new ArrayList<Integer>(this.selectedPixels);
        for (int x = this.MODE.startX; x < this.MODE.startX + 8; ++x) {
            for (int y = this.MODE.startY; y < this.MODE.startY + 8; ++y) {
                if (integerSet.isEmpty()) {
                    ETFUtils2.setPixel(this.etfParent.currentEditorSkin, x, y, 0);
                    continue;
                }
                ETFUtils2.setPixel(this.etfParent.currentEditorSkin, x, y, integerSet.get(0));
                integerSet.remove(0);
            }
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int pixelSize = (int)((double)this.height * 0.7 / 64.0);
        ETFConfigScreenSkinToolPixelSelection.renderGUITexture(context, this.currentSkinToRender, (int)((double)this.width * 0.35), (int)((double)this.height * 0.2), (int)((double)this.width * 0.35 + (double)(64 * pixelSize)), (int)((double)this.height * 0.2 + (double)(64 * pixelSize)));
        context.drawString(this.font, ETF.getTextFromTranslation("config.entity_texture_features.skin_select" + (this.selectedPixels.size() > 64 ? ".warn" : ".hint")), this.width / 7, (int)((double)this.height * 0.8), this.selectedPixels.size() > 64 ? 0xFF1515 : 0xFFFFFF);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            int height = (int)((double)this.height * 0.75);
            int playerX = (int)((double)this.width * 0.14);
            this.drawEntity(context, playerX, height, (int)((double)this.height * 0.3), -mouseX + playerX, (float)((double)(-mouseY) + (double)this.height * 0.3), (LivingEntity)player);
        } else {
            context.drawString(this.font, Component.nullToEmpty((String)"Player model only visible while in game!"), this.width / 7, (int)((double)this.height * 0.4), 0xFFFFFF);
            context.drawString(this.font, Component.nullToEmpty((String)"load a single-player world and then open this menu."), this.width / 7, (int)((double)this.height * 0.45), 0xFFFFFF);
        }
    }

    public void drawEntity(GuiGraphics context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan(mouseX / 40.0f);
        float g = (float)Math.atan(mouseY / 40.0f);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(0.0f);
        quaternionf.mul((Quaternionfc)quaternionf2);
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
        float h = entity.yBodyRot;
        float i = entity.getYRot();
        float j = entity.getXRot();
        float k = entity.yHeadRotO;
        float l = entity.yHeadRot;
        entity.yBodyRot = (this.etfParent.flipView ? 0.0f : 180.0f) + f * 20.0f;
        entity.setYRot((this.etfParent.flipView ? 0.0f : 180.0f) + f * 40.0f);
        entity.setXRot(-g * 20.0f);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render((Entity)entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, context.pose(), (MultiBufferSource)context.bufferSource(), 0xF000F0));
        context.flush();
        entityRenderDispatcher.setRenderShadow(true);
        context.pose().popPose();
        Lighting.setupFor3DItems();
        entity.yBodyRot = h;
        entity.setYRot(i);
        entity.setXRot(j);
        entity.yHeadRotO = k;
        entity.yHeadRot = l;
    }

    public static enum SelectionMode {
        EMISSIVE(56, 16),
        ENCHANTED(56, 24);

        final int startX;
        final int startY;

        private SelectionMode(int start_x, int start_y) {
            this.startX = start_x;
            this.startY = start_y;
        }
    }
}

