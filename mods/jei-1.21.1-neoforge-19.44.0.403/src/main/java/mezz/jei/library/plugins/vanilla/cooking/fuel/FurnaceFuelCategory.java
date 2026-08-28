/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.cooking.fuel;

import java.text.NumberFormat;
import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiFuelingRecipe;
import mezz.jei.common.gui.textures.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class FurnaceFuelCategory
extends AbstractRecipeCategory<IJeiFuelingRecipe> {
    public FurnaceFuelCategory(Textures textures) {
        super(RecipeTypes.FUELING, (Component)Component.translatable((String)"gui.jei.category.fuel"), textures.getFlameIcon(), FurnaceFuelCategory.getMaxWidth(), 34);
    }

    private static int getMaxWidth() {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        Component maxSmeltCountText = FurnaceFuelCategory.createSmeltCountText(2000000000);
        int maxStringWidth = fontRenderer.width(maxSmeltCountText.getString());
        int textPadding = 20;
        return 18 + textPadding + maxStringWidth;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiFuelingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1, 17).setStandardSlotBackground().addItemStacks((List)recipe.getInputs());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, IJeiFuelingRecipe recipe, IFocusGroup focuses) {
        int burnTime = recipe.getBurnTime();
        builder.addAnimatedRecipeFlame(burnTime).setPosition(1, 0);
        Component smeltCountText = FurnaceFuelCategory.createSmeltCountText(recipe.getBurnTime());
        ((ITextWidget)builder.addText((FormattedText)smeltCountText, this.getWidth() - 20, this.getHeight()).setPosition(20, 0)).setTextAlignment(HorizontalAlignment.CENTER).setTextAlignment(VerticalAlignment.CENTER).setColor(-8355712);
    }

    public static Component createSmeltCountText(int burnTime) {
        if (burnTime == 200) {
            return Component.translatable((String)"gui.jei.category.fuel.smeltCount.single");
        }
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        String smeltCount = numberInstance.format((float)burnTime / 200.0f);
        return Component.translatable((String)"gui.jei.category.fuel.smeltCount", (Object[])new Object[]{smeltCount});
    }

    @Override
    @Nullable
    public ResourceLocation getRegistryName(IJeiFuelingRecipe recipe) {
        return null;
    }
}

