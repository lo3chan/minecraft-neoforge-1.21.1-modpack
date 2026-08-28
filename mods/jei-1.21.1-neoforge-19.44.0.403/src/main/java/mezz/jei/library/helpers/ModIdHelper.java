/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.library.helpers;

import com.google.common.collect.ImmutableSetMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.platform.IPlatformModHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.config.IModIdFormatConfig;
import mezz.jei.library.config.ModIdFormatConfig;
import mezz.jei.library.config.StyledTextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ModIdHelper
implements IModIdHelper {
    private final IModIdFormatConfig modIdFormattingConfig;
    private final Function<ITypedIngredient<?>, String> getDisplayModId;
    private final ImmutableSetMultimap<String, String> modAliases;

    public ModIdHelper(IModIdFormatConfig modIdFormattingConfig, Function<ITypedIngredient<?>, String> getDisplayModId, ImmutableSetMultimap<String, String> modAliases) {
        this.modIdFormattingConfig = modIdFormattingConfig;
        this.getDisplayModId = getDisplayModId;
        this.modAliases = modAliases;
    }

    @Override
    public boolean isDisplayingModNameEnabled() {
        Component modNameFormat = this.modIdFormattingConfig.getModNameFormat();
        return !modNameFormat.getString().isEmpty();
    }

    @Override
    public <T> List<Component> addModNameToIngredientTooltip(List<Component> tooltip, T ingredient, IIngredientHelper<T> ingredientHelper) {
        if (!this.isDisplayingModNameEnabled()) {
            return tooltip;
        }
        if (this.modIdFormattingConfig.isModNameFormatOverrideActive() && ingredient instanceof ItemStack) {
            return tooltip;
        }
        String modId = ingredientHelper.getDisplayModId(ingredient);
        return this.addModNameToTooltip(tooltip, modId);
    }

    @Override
    public <T> Optional<Component> getModNameForTooltip(ITypedIngredient<T> typedIngredient) {
        if (!this.isDisplayingModNameEnabled()) {
            return Optional.empty();
        }
        IIngredientType<T> type = typedIngredient.getType();
        if (this.modIdFormattingConfig.isModNameFormatOverrideActive() && type == VanillaTypes.ITEM_STACK) {
            return Optional.empty();
        }
        String modId = this.getDisplayModId.apply(typedIngredient);
        return Optional.of(this.getFormattedModNameComponentForModId(modId));
    }

    @Override
    public <T> List<Component> addModNameToIngredientTooltip(List<Component> tooltip, ITypedIngredient<T> typedIngredient) {
        IIngredientType<T> type = typedIngredient.getType();
        if (!this.isDisplayingModNameEnabled()) {
            return tooltip;
        }
        if (this.modIdFormattingConfig.isModNameFormatOverrideActive() && type == VanillaTypes.ITEM_STACK) {
            return tooltip;
        }
        String modId = this.getDisplayModId.apply(typedIngredient);
        return this.addModNameToTooltip(tooltip, modId);
    }

    @Override
    @Deprecated(since="19.31.0", forRemoval=true)
    public String getFormattedModNameForModId(String modId) {
        Component modName = this.getFormattedModNameComponentForModId(modId);
        return StyledTextHelper.toLegacyString(modName);
    }

    @Override
    public Component getFormattedModNameComponentForModId(String modId) {
        String modName = this.getModNameForModId(modId);
        modName = ChatFormatting.stripFormatting((String)modName);
        Component modNameFormat = this.modIdFormattingConfig.getModNameFormat();
        if (!modNameFormat.getString().isEmpty()) {
            return ModIdFormatConfig.replaceModNameFormatCode(modNameFormat, modName);
        }
        return Component.literal((String)modName);
    }

    private List<Component> addModNameToTooltip(List<Component> tooltip, String modId) {
        ArrayList<Component> tooltipCopy = new ArrayList<Component>(tooltip);
        tooltipCopy.add(this.getFormattedModNameComponentForModId(modId));
        return tooltipCopy;
    }

    @Override
    public Set<String> getModAliases(String modId) {
        return this.modAliases.get((Object)modId);
    }

    @Override
    public String getModNameForModId(String modId) {
        IPlatformModHelper modHelper = Services.PLATFORM.getModHelper();
        return modHelper.getModNameForModId(modId);
    }
}

