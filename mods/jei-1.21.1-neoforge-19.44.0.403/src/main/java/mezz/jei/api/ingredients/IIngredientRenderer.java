/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.TooltipFlag
 */
package mezz.jei.api.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

public interface IIngredientRenderer<T> {
    public void render(GuiGraphics var1, T var2);

    default public void render(GuiGraphics guiGraphics, T ingredient, int posX, int posY) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((float)posX, (float)posY, 0.0f);
        this.render(guiGraphics, ingredient);
        poseStack.popPose();
    }

    default public void renderBatch(GuiGraphics guiGraphics, List<BatchRenderElement<T>> elements) {
        for (BatchRenderElement<T> element : elements) {
            this.render(guiGraphics, element.ingredient(), element.x(), element.y());
        }
    }

    public List<Component> getTooltip(T var1, TooltipFlag var2);

    default public void getTooltip(ITooltipBuilder tooltip, T ingredient, TooltipFlag tooltipFlag) {
        List<Component> components = this.getTooltip(ingredient, tooltipFlag);
        tooltip.addAll(components);
    }

    default public Font getFontRenderer(Minecraft minecraft, T ingredient) {
        return minecraft.font;
    }

    default public int getWidth() {
        return 16;
    }

    default public int getHeight() {
        return 16;
    }
}

