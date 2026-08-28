/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.CrashReport
 *  net.minecraft.ReportedException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.TooltipFlag$Default
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.common.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.platform.IPlatformInputHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

public final class SafeIngredientUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<IIngredientRenderer<?>> CRASHING_INGREDIENT_BATCH_RENDERERS = new HashSet();
    private static final Set<Object> CRASHING_INGREDIENT_RENDERERS = new HashSet<Object>();
    private static final Set<Object> CRASHING_INGREDIENT_TOOLTIPS = new HashSet<Object>();

    private SafeIngredientUtil() {
    }

    public static <T> void getRichTooltip(ITooltipBuilder tooltip, IIngredientManager ingredientManager, IIngredientRenderer<T> ingredientRenderer, ITypedIngredient<T> typedIngredient) {
        Minecraft minecraft = Minecraft.getInstance();
        TooltipFlag.Default tooltipFlag = minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        tooltipFlag = tooltipFlag.asCreative();
        SafeIngredientUtil.getRichTooltip(tooltip, ingredientManager, ingredientRenderer, typedIngredient, (TooltipFlag)tooltipFlag);
    }

    public static <T> void getRichTooltip(ITooltipBuilder tooltip, IIngredientManager ingredientManager, IIngredientRenderer<T> ingredientRenderer, ITypedIngredient<T> typedIngredient, TooltipFlag tooltipFlag) {
        T ingredient = typedIngredient.getIngredient();
        if (CRASHING_INGREDIENT_TOOLTIPS.contains(ingredient)) {
            SafeIngredientUtil.getTooltipErrorTooltip(tooltip);
            return;
        }
        IPlatformInputHelper inputHelper = Services.PLATFORM.getInputHelper();
        tooltipFlag = inputHelper.getClientTooltipFlag(tooltipFlag);
        tooltip.setIngredient(typedIngredient);
        try {
            ingredientRenderer.getTooltip(tooltip, ingredient, tooltipFlag);
            if (CRASHING_INGREDIENT_RENDERERS.contains(ingredient)) {
                SafeIngredientUtil.getRenderErrorTooltip(tooltip);
            }
        }
        catch (LinkageError | RuntimeException e) {
            CRASHING_INGREDIENT_TOOLTIPS.add(ingredient);
            ErrorUtil.logIngredientCrash(e, "Caught an error getting an Ingredient's tooltip", ingredientManager, typedIngredient.getType(), ingredient);
            SafeIngredientUtil.getTooltipErrorTooltip(tooltip);
        }
    }

    public static <T> @Unmodifiable List<Component> getPlainTooltipForSearch(IIngredientManager ingredientManager, IIngredientRenderer<T> ingredientRenderer, ITypedIngredient<T> typedIngredient, TooltipFlag tooltipFlag) {
        T ingredient = typedIngredient.getIngredient();
        if (CRASHING_INGREDIENT_TOOLTIPS.contains(ingredient)) {
            return List.of();
        }
        try {
            return ingredientRenderer.getTooltip(ingredient, tooltipFlag);
        }
        catch (LinkageError | RuntimeException e) {
            CRASHING_INGREDIENT_TOOLTIPS.add(ingredient);
            ErrorUtil.logIngredientCrash(e, "Caught an error getting an Ingredient's tooltip", ingredientManager, typedIngredient.getType(), ingredient);
            return List.of();
        }
    }

    private static void getTooltipErrorTooltip(ITooltipBuilder tooltip) {
        MutableComponent crash = Component.translatable((String)"jei.tooltip.error.crash");
        tooltip.add((FormattedText)crash.withStyle(ChatFormatting.RED));
    }

    private static void getRenderErrorTooltip(ITooltipBuilder tooltip) {
        MutableComponent crash = Component.translatable((String)"jei.tooltip.error.render.crash");
        tooltip.add((FormattedText)crash.withStyle(ChatFormatting.RED));
    }

    public static <T> void renderBatch(GuiGraphics guiGraphics, IIngredientType<T> ingredientType, IIngredientRenderer<T> ingredientRenderer, List<BatchRenderElement<T>> elements) {
        if (CRASHING_INGREDIENT_BATCH_RENDERERS.contains(ingredientRenderer)) {
            for (BatchRenderElement<T> element : elements) {
                SafeIngredientUtil.render(guiGraphics, ingredientRenderer, ingredientType, element);
            }
            return;
        }
        try {
            ingredientRenderer.renderBatch(guiGraphics, elements);
        }
        catch (LinkageError | RuntimeException e) {
            CRASHING_INGREDIENT_BATCH_RENDERERS.add(ingredientRenderer);
            LOGGER.error("Caught an error while rendering a batch of Ingredients with ingredient renderer: {}", ingredientRenderer.getClass(), (Object)e);
        }
    }

    public static <T> void render(GuiGraphics guiGraphics, IIngredientRenderer<T> ingredientRenderer, ITypedIngredient<T> typedIngredient, int x, int y) {
        SafeIngredientUtil.render(guiGraphics, ingredientRenderer, typedIngredient.getType(), typedIngredient.getIngredient(), x, y);
    }

    public static <T> void render(GuiGraphics guiGraphics, IIngredientRenderer<T> ingredientRenderer, IIngredientType<T> ingredientType, BatchRenderElement<T> element) {
        SafeIngredientUtil.render(guiGraphics, ingredientRenderer, ingredientType, element.ingredient(), element.x(), element.y());
    }

    public static <T> void render(GuiGraphics guiGraphics, IIngredientRenderer<T> ingredientRenderer, IIngredientType<T> ingredientType, T ingredient, int x, int y) {
        if (CRASHING_INGREDIENT_RENDERERS.contains(ingredient)) {
            SafeIngredientUtil.renderError(guiGraphics);
            return;
        }
        try {
            ingredientRenderer.render(guiGraphics, ingredient, x, y);
        }
        catch (LinkageError | RuntimeException e) {
            CRASHING_INGREDIENT_RENDERERS.add(ingredient);
            IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
            if (SafeIngredientUtil.shouldCatchRenderErrors()) {
                ErrorUtil.logIngredientCrash(e, "Caught an error rendering an Ingredient", ingredientManager, ingredientType, ingredient);
                SafeIngredientUtil.renderError(guiGraphics);
            }
            CrashReport crashReport = ErrorUtil.createIngredientCrashReport(e, "Rendering ingredient", ingredientManager, ingredientType, ingredient);
            throw new ReportedException(crashReport);
        }
    }

    private static boolean shouldCatchRenderErrors() {
        return Internal.getOptionalJeiClientConfigs().map(IJeiClientConfigs::getClientConfig).map(clientConfig -> clientConfig.catchRenderErrorsEnabled().getValue()).orElse(false);
    }

    private static void renderError(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        guiGraphics.drawString(font, "ERR", 0, 0, -65536, false);
        guiGraphics.drawString(font, "OR", 0, 8, -65536, false);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

