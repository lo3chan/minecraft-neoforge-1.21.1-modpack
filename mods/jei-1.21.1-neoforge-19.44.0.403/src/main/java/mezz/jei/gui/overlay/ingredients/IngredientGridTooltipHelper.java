/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.search.SearchMode;
import mezz.jei.common.util.SafeIngredientUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public final class IngredientGridTooltipHelper {
    private final IIngredientManager ingredientManager;
    private final IIngredientFilterConfig ingredientFilterConfig;
    private final IClientToggleState toggleState;
    private final IInternalKeyMappings keyBindings;
    private final IColorHelper colorHelper;

    public IngredientGridTooltipHelper(IIngredientManager ingredientManager, IIngredientFilterConfig ingredientFilterConfig, IClientToggleState toggleState, IInternalKeyMappings keyBindings, IColorHelper colorHelper) {
        this.ingredientManager = ingredientManager;
        this.ingredientFilterConfig = ingredientFilterConfig;
        this.toggleState = toggleState;
        this.keyBindings = keyBindings;
        this.colorHelper = colorHelper;
    }

    public <T> void getIngredientTooltip(JeiTooltip tooltip, ITypedIngredient<T> typedIngredient, IIngredientRenderer<T> ingredientRenderer, IIngredientHelper<T> ingredientHelper) {
        SafeIngredientUtil.getRichTooltip(tooltip, this.ingredientManager, ingredientRenderer, typedIngredient);
        if (this.ingredientFilterConfig.colorSearchMode().getValue() != SearchMode.DISABLED) {
            this.addColorSearchInfoToTooltip(tooltip, typedIngredient, ingredientHelper);
        }
        if (this.ingredientFilterConfig.searchIngredientAliases().getValue().booleanValue()) {
            this.addIngredientAliasesToTooltip(tooltip, typedIngredient, this.ingredientManager);
        }
        if (this.toggleState.isEditModeEnabled()) {
            IngredientGridTooltipHelper.addEditModeInfoToTooltip(tooltip, this.keyBindings);
        }
    }

    private <T> void addIngredientAliasesToTooltip(JeiTooltip tooltip, ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
        Collection<String> aliases = ingredientManager.getIngredientAliases(typedIngredient);
        if (aliases.isEmpty()) {
            return;
        }
        tooltip.add((FormattedText)Component.empty());
        tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.item.search.aliases").withStyle(ChatFormatting.GRAY));
        for (String alias : aliases) {
            tooltip.add((FormattedText)Component.literal((String)("\u2022 " + alias)).withStyle(ChatFormatting.GRAY));
        }
    }

    private <T> void addColorSearchInfoToTooltip(JeiTooltip tooltip, ITypedIngredient<T> typedIngredient, IIngredientHelper<T> ingredientHelper) {
        Iterable<Integer> colors = ingredientHelper.getColors(typedIngredient.getIngredient());
        String colorNamesString = StreamSupport.stream(colors.spliterator(), false).map(this.colorHelper::getClosestColorName).collect(Collectors.joining(", "));
        if (!colorNamesString.isEmpty()) {
            MutableComponent colorTranslation = Component.translatable((String)"jei.tooltip.item.colors", (Object[])new Object[]{colorNamesString}).withStyle(ChatFormatting.GRAY);
            tooltip.add((FormattedText)colorTranslation);
        }
    }

    private static void addEditModeInfoToTooltip(JeiTooltip tooltip, IInternalKeyMappings keyBindings) {
        tooltip.add((FormattedText)CommonComponents.EMPTY);
        tooltip.add((FormattedText)Component.translatable((String)"gui.jei.editMode.description").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.addKeyUsageComponent("gui.jei.editMode.description.hide", keyBindings.getToggleHideIngredient());
        tooltip.addKeyUsageComponent("gui.jei.editMode.description.hide.wild", keyBindings.getToggleWildcardHideIngredient());
    }
}

