/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package mezz.jei.library.render.batch;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.render.ItemStackRenderer;
import mezz.jei.library.render.batch.ElementWithModel;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ItemStackBatchRenderer {
    private final List<ElementWithModel> useBlockLight = new ArrayList<ElementWithModel>();
    private final List<ElementWithModel> noBlockLight = new ArrayList<ElementWithModel>();
    private final List<BatchRenderElement<ItemStack>> customRender = new ArrayList<BatchRenderElement<ItemStack>>();

    public ItemStackBatchRenderer(Minecraft minecraft, List<BatchRenderElement<ItemStack>> elements) {
        ClientLevel level = minecraft.level;
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        for (BatchRenderElement<ItemStack> element : elements) {
            ElementWithModel elementWithModel;
            ItemStack itemStack = element.ingredient();
            if (itemStack.isEmpty()) continue;
            BakedModel bakedmodel = itemRenderer.getModel(itemStack, (Level)level, null, 0);
            if (bakedmodel.isCustomRenderer()) {
                this.customRender.add(element);
                continue;
            }
            if (bakedmodel.usesBlockLight()) {
                elementWithModel = new ElementWithModel(bakedmodel, itemStack, element.x(), element.y());
                this.useBlockLight.add(elementWithModel);
                continue;
            }
            if (!bakedmodel.isGui3d()) {
                IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
                bakedmodel = renderHelper.createLimitedQuadItemModel(bakedmodel);
            }
            elementWithModel = new ElementWithModel(bakedmodel, itemStack, element.x(), element.y());
            this.noBlockLight.add(elementWithModel);
        }
    }

    public void render(GuiGraphics guiGraphics, Minecraft minecraft, ItemRenderer itemRenderer, ItemStackRenderer itemStackRenderer) {
        Font font;
        ItemStack ingredient;
        if (!this.noBlockLight.isEmpty()) {
            Lighting.setupForFlatItems();
            for (ElementWithModel element : this.noBlockLight) {
                this.renderItem(guiGraphics, itemRenderer, element.model(), element.stack(), element.x(), element.y());
            }
            guiGraphics.flush();
            Lighting.setupFor3DItems();
        }
        if (!this.useBlockLight.isEmpty()) {
            Lighting.setupFor3DItems();
            for (ElementWithModel element : this.useBlockLight) {
                this.renderItem(guiGraphics, itemRenderer, element.model(), element.stack(), element.x(), element.y());
            }
            guiGraphics.flush();
        }
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        for (ElementWithModel elementWithModel : this.useBlockLight) {
            ingredient = elementWithModel.stack();
            font = renderHelper.getFontRenderer(minecraft, ingredient);
            guiGraphics.renderItemDecorations(font, ingredient, elementWithModel.x(), elementWithModel.y());
        }
        for (ElementWithModel elementWithModel : this.noBlockLight) {
            ingredient = elementWithModel.stack();
            font = renderHelper.getFontRenderer(minecraft, ingredient);
            guiGraphics.renderItemDecorations(font, ingredient, elementWithModel.x(), elementWithModel.y());
        }
        RenderSystem.disableBlend();
        for (BatchRenderElement batchRenderElement : this.customRender) {
            ingredient = (ItemStack)batchRenderElement.ingredient();
            itemStackRenderer.render(guiGraphics, ingredient, batchRenderElement.x(), batchRenderElement.y());
            RenderSystem.disableBlend();
        }
        RenderSystem.disableBlend();
    }

    private void renderItem(GuiGraphics guiGraphics, ItemRenderer itemRenderer, BakedModel bakedmodel, ItemStack itemStack, int x, int y) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((float)(x + 8), (float)(y + 8), 150.0f);
        poseStack.scale(16.0f, -16.0f, 16.0f);
        try {
            itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, (MultiBufferSource)guiGraphics.bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY, bakedmodel);
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable((Throwable)throwable, (String)"Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type", () -> String.valueOf(itemStack.getItem()));
            crashreportcategory.setDetail("Item Components", () -> String.valueOf(itemStack.getComponents()));
            crashreportcategory.setDetail("Item Foil", () -> String.valueOf(itemStack.hasFoil()));
            throw new ReportedException(crashreport);
        }
        poseStack.popPose();
    }
}

