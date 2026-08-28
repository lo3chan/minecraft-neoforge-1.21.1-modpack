/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Style
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.chat;

import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.common.chat.JeiChatItemLinkHover;
import mezz.jei.common.chat.JeiChatItemLinks;
import mezz.jei.common.gui.IngredientTooltipComponent;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.util.SafeIngredientUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public final class ChatIngredientTooltip {
    private ChatIngredientTooltip() {
    }

    public static boolean setTooltipForHoveredText(GuiGraphics guiGraphics, @Nullable Style hoveredStyle, int mouseX, int mouseY) {
        Optional<IngredientTooltipData<?>> optionalTooltipData = ChatIngredientTooltip.getTooltipForHoveredText(hoveredStyle);
        if (optionalTooltipData.isEmpty()) {
            return false;
        }
        IngredientTooltipData<?> tooltipData = optionalTooltipData.get();
        tooltipData.draw(guiGraphics, mouseX, mouseY);
        return true;
    }

    public static Optional<IngredientTooltipData<?>> getTooltipForHoveredChatLink(@Nullable Screen screen, double mouseX, double mouseY) {
        if (screen == null) {
            return Optional.empty();
        }
        return JeiChatItemLinkHover.getHoveredStyle(screen, mouseX, mouseY).flatMap(ChatIngredientTooltip::getTooltipForHoveredText);
    }

    public static Optional<IngredientTooltipData<?>> getTooltipForHoveredText(@Nullable Style hoveredStyle) {
        Optional<JeiChatItemLinks.IngredientLink> optionalLink = JeiChatItemLinkHover.getIngredientLink(hoveredStyle);
        if (optionalLink.isEmpty()) {
            return Optional.empty();
        }
        Optional<IJeiRuntime> optionalRuntime = Internal.getOptionalJeiRuntime();
        if (optionalRuntime.isEmpty()) {
            return Optional.empty();
        }
        IJeiRuntime jeiRuntime = optionalRuntime.get();
        IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
        JeiChatItemLinks.IngredientLink link = optionalLink.get();
        Optional<ITypedIngredient<?>> optionalTypedIngredient = JeiChatItemLinks.resolveTypedIngredient(link, ingredientManager);
        if (optionalTypedIngredient.isEmpty()) {
            return Optional.empty();
        }
        ITypedIngredient<?> typedIngredient = optionalTypedIngredient.get();
        IngredientTooltipData<?> tooltipData = ChatIngredientTooltip.createTooltipData(typedIngredient, ingredientManager);
        return Optional.of(tooltipData);
    }

    private static <T> IngredientTooltipData<T> createTooltipData(ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
        IIngredientRenderer<T> ingredientRenderer = ingredientManager.getIngredientRenderer(typedIngredient.getType());
        JeiTooltip tooltip = new JeiTooltip();
        tooltip.add(new IngredientTooltipComponent<T>(typedIngredient, ingredientRenderer));
        SafeIngredientUtil.getRichTooltip(tooltip, ingredientManager, ingredientRenderer, typedIngredient);
        return new IngredientTooltipData<T>(typedIngredient, ingredientRenderer, ingredientManager, tooltip);
    }

    public record IngredientTooltipData<T>(ITypedIngredient<T> typedIngredient, IIngredientRenderer<T> ingredientRenderer, IIngredientManager ingredientManager, JeiTooltip tooltip) {
        public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            this.tooltip.draw(guiGraphics, mouseX, mouseY, this.typedIngredient, this.ingredientRenderer, this.ingredientManager);
        }
    }
}

