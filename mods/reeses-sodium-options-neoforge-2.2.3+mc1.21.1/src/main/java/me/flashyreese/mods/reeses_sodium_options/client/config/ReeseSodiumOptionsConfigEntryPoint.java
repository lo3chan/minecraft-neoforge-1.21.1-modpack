/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint
 *  net.caffeinemc.mods.sodium.api.config.structure.BooleanOptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder
 *  net.caffeinemc.mods.sodium.api.config.structure.PageBuilder
 *  net.minecraft.Util
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.config;

import java.util.function.Consumer;
import java.util.function.Supplier;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.BooleanOptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.PageBuilder;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ReeseSodiumOptionsConfigEntryPoint
implements ConfigEntryPoint {
    private static final String MOD_ID = "reeses-sodium-options";
    private static final String KO_FI_URL = "https://ko-fi.com/flashyreese";

    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions().setNonTintedIcon(this.modIcon()).addPage((PageBuilder)this.createOptionsPage(builder));
    }

    private OptionPageBuilder createOptionsPage(ConfigBuilder builder) {
        return builder.createOptionPage().setName((Component)Component.translatable((String)"rso.options.page")).addOptionGroup(this.createGeneralOptions(builder)).addOptionGroup(this.createAppearanceOptions(builder)).addOptionGroup(this.createBehaviorOptions(builder)).addOptionGroup(this.createSupportOptions(builder));
    }

    private OptionGroupBuilder createGeneralOptions(ConfigBuilder builder) {
        return builder.createOptionGroup().setName((Component)Component.translatable((String)"rso.options.group.general")).addOption((OptionBuilder)builder.createBooleanOption(this.optionId("enabled")).setName((Component)Component.translatable((String)"rso.options.enabled.name")).setTooltip((Component)Component.translatable((String)"rso.options.enabled.tooltip")).setDefaultValue(Boolean.valueOf(true)).setBinding(value -> ReeseSodiumOptionsConfig.config().setEnabled((boolean)value), () -> ReeseSodiumOptionsConfig.config().isEnabled()).setStorageHandler(ReeseSodiumOptionsConfig.STORAGE_HANDLER).setApplyHook(ReeseSodiumOptionsConfig::reopenScreen));
    }

    private OptionGroupBuilder createAppearanceOptions(ConfigBuilder builder) {
        return builder.createOptionGroup().setName((Component)Component.translatable((String)"rso.options.group.appearance")).addOption(this.createBooleanOption(builder, "tab_header_icons", value -> ReeseSodiumOptionsConfig.config().setTabHeaderIcons((boolean)value), () -> ReeseSodiumOptionsConfig.config().isTabHeaderIcons(), true, true)).addOption(this.createBooleanOption(builder, "tab_header_version_labels", value -> ReeseSodiumOptionsConfig.config().setTabHeaderVersionLabels((boolean)value), () -> ReeseSodiumOptionsConfig.config().isTabHeaderVersionLabels(), true, true)).addOption((OptionBuilder)builder.createEnumOption(this.optionId("tab_header_collapse_mode"), ReeseSodiumOptionsConfig.TabHeaderCollapseMode.class).setName((Component)Component.translatable((String)"rso.options.tab_header_collapse_mode.name")).setTooltip((Component)Component.translatable((String)"rso.options.tab_header_collapse_mode.tooltip")).setDefaultValue((Enum)ReeseSodiumOptionsConfig.DEFAULT_TAB_HEADER_COLLAPSE_MODE).setElementNameProvider(value -> Component.translatable((String)("rso.options.tab_header_collapse_mode.value." + value.id()))).setBinding(value -> ReeseSodiumOptionsConfig.config().setTabHeaderCollapseMode((ReeseSodiumOptionsConfig.TabHeaderCollapseMode)((Object)value)), () -> ReeseSodiumOptionsConfig.config().getTabHeaderCollapseMode()).setStorageHandler(ReeseSodiumOptionsConfig.STORAGE_HANDLER).setApplyHook(ReeseSodiumOptionsConfig::rebuildCurrentScreen)).addOption(this.createBooleanOption(builder, "tab_headers", value -> ReeseSodiumOptionsConfig.config().setTabHeaders((boolean)value), () -> ReeseSodiumOptionsConfig.config().isTabHeaders(), true, true)).addOption(this.createBooleanOption(builder, "collapse_single_page_groups", value -> ReeseSodiumOptionsConfig.config().setCollapseSinglePageGroups((boolean)value), () -> ReeseSodiumOptionsConfig.config().isCollapseSinglePageGroups(), true, true)).addOption(this.createBooleanOption(builder, "collapsible_groups", value -> ReeseSodiumOptionsConfig.config().setCollapsibleGroups((boolean)value), () -> ReeseSodiumOptionsConfig.config().isCollapsibleGroups(), true, true)).addOption((OptionBuilder)builder.createIntegerOption(this.optionId("tooltip_delay")).setName((Component)Component.translatable((String)"rso.options.tooltip_delay.name")).setTooltip((Component)Component.translatable((String)"rso.options.tooltip_delay.tooltip")).setDefaultValue(Integer.valueOf(500)).setRange(0, 5000, 100).setValueFormatter(value -> Component.translatable((String)"rso.options.value.milliseconds", (Object[])new Object[]{value})).setBinding(value -> ReeseSodiumOptionsConfig.config().setTooltipDelayMs((int)value), () -> ReeseSodiumOptionsConfig.config().getTooltipDelayMs()).setStorageHandler(ReeseSodiumOptionsConfig.STORAGE_HANDLER)).addOption(this.createBooleanOption(builder, "tooltip_option_ids", value -> ReeseSodiumOptionsConfig.config().setTooltipOptionIds((boolean)value), () -> ReeseSodiumOptionsConfig.config().isTooltipOptionIds(), false, false)).addOption(this.createBooleanOption(builder, "color_themes", value -> ReeseSodiumOptionsConfig.config().setColorThemes((boolean)value), () -> ReeseSodiumOptionsConfig.config().isColorThemes(), true, true)).addOption(this.createBooleanOption(builder, "themed_headers_and_labels", value -> ReeseSodiumOptionsConfig.config().setThemedHeadersAndLabels((boolean)value), () -> ReeseSodiumOptionsConfig.config().isThemedHeadersAndLabels(), true, true)).addOption(this.createBooleanOption(builder, "themed_tooltip_borders", value -> ReeseSodiumOptionsConfig.config().setThemedTooltipBorders((boolean)value), () -> ReeseSodiumOptionsConfig.config().isThemedTooltipBorders(), true, false)).addOption(this.createBooleanOption(builder, "reduced_motion", value -> ReeseSodiumOptionsConfig.config().setReducedMotion((boolean)value), () -> ReeseSodiumOptionsConfig.config().isReducedMotion(), false, false));
    }

    private OptionGroupBuilder createBehaviorOptions(ConfigBuilder builder) {
        return builder.createOptionGroup().setName((Component)Component.translatable((String)"rso.options.group.behavior")).addOption(this.createBooleanOption(builder, "reverse_cycling_controls", value -> ReeseSodiumOptionsConfig.config().setReverseCyclingControls((boolean)value), () -> ReeseSodiumOptionsConfig.config().isReverseCyclingControls(), true, false)).addOption(this.createBooleanOption(builder, "shift_scroll_slider_adjustments", value -> ReeseSodiumOptionsConfig.config().setShiftScrollSliderAdjustments((boolean)value), () -> ReeseSodiumOptionsConfig.config().isShiftScrollSliderAdjustments(), true, false)).addOption((OptionBuilder)builder.createIntegerOption(this.optionId("search_result_limit")).setName((Component)Component.translatable((String)"rso.options.search_result_limit.name")).setTooltip((Component)Component.translatable((String)"rso.options.search_result_limit.tooltip")).setDefaultValue(Integer.valueOf(15)).setRange(1, 50, 1).setValueFormatter(value -> Component.translatable((String)"rso.options.value.results", (Object[])new Object[]{value})).setBinding(value -> ReeseSodiumOptionsConfig.config().setSearchResultLimit((int)value), () -> ReeseSodiumOptionsConfig.config().getSearchResultLimit()).setStorageHandler(ReeseSodiumOptionsConfig.STORAGE_HANDLER).setApplyHook(ReeseSodiumOptionsConfig::rebuildCurrentScreen)).addOption(this.createBooleanOption(builder, "hide_non_matching_options", value -> ReeseSodiumOptionsConfig.config().setHideNonMatchingOptions((boolean)value), () -> ReeseSodiumOptionsConfig.config().isHideNonMatchingOptions(), true, true)).addOption(this.createBooleanOption(builder, "hide_non_matching_tabs", value -> ReeseSodiumOptionsConfig.config().setHideNonMatchingTabs((boolean)value), () -> ReeseSodiumOptionsConfig.config().isHideNonMatchingTabs(), true, true)).addOption((OptionBuilder)builder.createEnumOption(this.optionId("disabled_option_visibility"), ReeseSodiumOptionsConfig.DisabledOptionVisibility.class).setName((Component)Component.translatable((String)"rso.options.disabled_option_visibility.name")).setTooltip((Component)Component.translatable((String)"rso.options.disabled_option_visibility.tooltip")).setDefaultValue((Enum)ReeseSodiumOptionsConfig.DEFAULT_DISABLED_OPTION_VISIBILITY).setElementNameProvider(value -> Component.translatable((String)("rso.options.disabled_option_visibility.value." + value.id()))).setBinding(value -> ReeseSodiumOptionsConfig.config().setDisabledOptionVisibility((ReeseSodiumOptionsConfig.DisabledOptionVisibility)((Object)value)), () -> ReeseSodiumOptionsConfig.config().getDisabledOptionVisibility()).setStorageHandler(ReeseSodiumOptionsConfig.STORAGE_HANDLER).setApplyHook(ReeseSodiumOptionsConfig::rebuildCurrentScreen)).addOption((OptionBuilder)builder.createEnumOption(this.optionId("focus_border_mode"), ReeseSodiumOptionsConfig.FocusBorderMode.class).setName((Component)Component.translatable((String)"rso.options.focus_border_mode.name")).setTooltip((Component)Component.translatable((String)"rso.options.focus_border_mode.tooltip")).setDefaultValue((Enum)ReeseSodiumOptionsConfig.DEFAULT_FOCUS_BORDER_MODE).setElementNameProvider(value -> Component.translatable((String)("rso.options.focus_border_mode.value." + value.id()))).setBinding(value -> ReeseSodiumOptionsConfig.config().setFocusBorderMode((ReeseSodiumOptionsConfig.FocusBorderMode)((Object)value)), () -> ReeseSodiumOptionsConfig.config().getFocusBorderMode()).setStorageHandler(ReeseSodiumOptionsConfig.STORAGE_HANDLER)).addOption(this.createBooleanOption(builder, "controller_guides", value -> ReeseSodiumOptionsConfig.config().setControllerGuides((boolean)value), () -> ReeseSodiumOptionsConfig.config().isControllerGuides(), true, false)).addOption(this.createBooleanOption(builder, "reset_button_overlay", value -> ReeseSodiumOptionsConfig.config().setResetButtonOverlay((boolean)value), () -> ReeseSodiumOptionsConfig.config().isResetButtonOverlay(), true, false)).addOption(this.createBooleanOption(builder, "undo_button_overlay", value -> ReeseSodiumOptionsConfig.config().setUndoButtonOverlay((boolean)value), () -> ReeseSodiumOptionsConfig.config().isUndoButtonOverlay(), true, false)).addOption(this.createBooleanOption(builder, "always_show_action_buttons", value -> ReeseSodiumOptionsConfig.config().setAlwaysShowActionButtons((boolean)value), () -> ReeseSodiumOptionsConfig.config().isAlwaysShowActionButtons(), false, false));
    }

    private OptionGroupBuilder createSupportOptions(ConfigBuilder builder) {
        return builder.createOptionGroup().setName((Component)Component.translatable((String)"rso.options.group.support")).addOption((OptionBuilder)builder.createExternalButtonOption(this.optionId("support_project")).setName((Component)Component.translatable((String)"rso.options.support_project.name")).setTooltip((Component)Component.translatable((String)"rso.options.support_project.tooltip")).setScreenConsumer(screen -> Util.getPlatform().openUri(KO_FI_URL)));
    }

    private OptionBuilder createBooleanOption(ConfigBuilder builder, String name, Consumer<Boolean> setter, Supplier<Boolean> getter, boolean defaultValue, boolean rebuildScreen) {
        BooleanOptionBuilder option = builder.createBooleanOption(this.optionId(name)).setName((Component)Component.translatable((String)("rso.options." + name + ".name"))).setTooltip((Component)Component.translatable((String)("rso.options." + name + ".tooltip"))).setDefaultValue(Boolean.valueOf(defaultValue)).setBinding(setter, getter).setStorageHandler(ReeseSodiumOptionsConfig.STORAGE_HANDLER);
        if (rebuildScreen) {
            option.setApplyHook(ReeseSodiumOptionsConfig::rebuildCurrentScreen);
        }
        return option;
    }

    private ResourceLocation optionId(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)MOD_ID, (String)path);
    }

    private ResourceLocation modIcon() {
        return ResourceLocation.fromNamespaceAndPath((String)MOD_ID, (String)"icon.png");
    }
}

