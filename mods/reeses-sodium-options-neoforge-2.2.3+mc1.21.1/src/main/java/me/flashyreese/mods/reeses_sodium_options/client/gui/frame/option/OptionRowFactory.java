/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.BooleanOption
 *  net.caffeinemc.mods.sodium.client.config.structure.EnumOption
 *  net.caffeinemc.mods.sodium.client.config.structure.ExternalButtonOption
 *  net.caffeinemc.mods.sodium.client.config.structure.IntegerOption
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.caffeinemc.mods.sodium.client.gui.ColorTheme
 *  net.caffeinemc.mods.sodium.client.gui.options.control.Control
 *  net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl
 *  net.caffeinemc.mods.sodium.client.gui.options.control.ExternalButtonControl
 *  net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl
 *  net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl
 *  net.minecraft.client.gui.screens.Screen
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.BooleanOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.EnumOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.ExternalButtonOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.IntegerSliderOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.PageLayout;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.SodiumControlElementOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.UnsupportedOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.BooleanOption;
import net.caffeinemc.mods.sodium.client.config.structure.EnumOption;
import net.caffeinemc.mods.sodium.client.config.structure.ExternalButtonOption;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.ExternalButtonControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class OptionRowFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Reese's Sodium Options");
    private final Screen screen;
    private final ColorTheme sodiumTheme;
    private final GuiTheme theme;
    private final OptionStateStore optionStateStore;

    OptionRowFactory(Screen screen, ColorTheme sodiumTheme, GuiTheme theme, OptionStateStore optionStateStore) {
        this.screen = screen;
        this.sodiumTheme = sodiumTheme;
        this.theme = theme;
        this.optionStateStore = optionStateStore;
    }

    OptionRow create(Option option, LayoutBounds dim) {
        OptionRow element = this.createOptionRow(option, dim);
        this.registerOptionBounds(element, dim);
        return element;
    }

    void registerParentBounds(PageLayout layout, LayoutBounds parentDim) {
        for (PageLayout.Row row : layout.rows()) {
            PageLayout.OptionRow optionRow;
            Option option;
            if (!(row instanceof PageLayout.OptionRow) || !((option = (optionRow = (PageLayout.OptionRow)row).option()) instanceof OptionExtended)) continue;
            OptionExtended optionExtended = (OptionExtended)option;
            this.optionStateStore.optionLayoutState(optionExtended.rso$getId()).setParentBounds(parentDim);
        }
    }

    void registerOptionBounds(OptionRow element, LayoutBounds dim) {
        Option option = element.getOption();
        if (option instanceof OptionExtended) {
            OptionExtended optionExtended = (OptionExtended)option;
            this.optionStateStore.optionLayoutState(optionExtended.rso$getId()).setBounds(dim);
        }
    }

    private OptionRow createOptionRow(Option option, LayoutBounds dim) {
        OptionRow optionRow;
        Control control = option.getControl();
        Option option2 = option;
        Objects.requireNonNull(option2);
        Option option3 = option2;
        int n = 0;
        block6: while (true) {
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BooleanOption.class, IntegerOption.class, EnumOption.class, ExternalButtonOption.class}, (Object)option3, n)) {
                case 0: {
                    BooleanOption booleanOption = (BooleanOption)option3;
                    if (!(control instanceof TickBoxControl)) {
                        n = 1;
                        continue block6;
                    }
                    optionRow = new BooleanOptionRow(dim, this.theme, this.optionStateStore, booleanOption);
                    break block6;
                }
                case 1: {
                    IntegerOption integerOption = (IntegerOption)option3;
                    if (!(control instanceof SliderControl)) {
                        n = 2;
                        continue block6;
                    }
                    optionRow = new IntegerSliderOptionRow(dim, this.theme, this.optionStateStore, integerOption);
                    break block6;
                }
                case 2: {
                    EnumOption enumOption = (EnumOption)option3;
                    if (!(control instanceof CyclingControl)) {
                        n = 3;
                        continue block6;
                    }
                    optionRow = new EnumOptionRow(dim, this.theme, this.optionStateStore, enumOption);
                    break block6;
                }
                case 3: {
                    ExternalButtonOption externalButtonOption = (ExternalButtonOption)option3;
                    if (!(control instanceof ExternalButtonControl)) {
                        n = 4;
                        continue block6;
                    }
                    optionRow = new ExternalButtonOptionRow(this.screen, dim, this.theme, this.optionStateStore, externalButtonOption);
                    break block6;
                }
                default: {
                    optionRow = this.createSodiumFallbackRow(option, dim);
                    break block6;
                }
            }
            break;
        }
        return optionRow;
    }

    private OptionRow createSodiumFallbackRow(Option option, LayoutBounds dim) {
        try {
            return new SodiumControlElementOptionRow(this.screen, dim, this.sodiumTheme, option);
        }
        catch (RuntimeException e) {
            LOGGER.warn("Could not create Sodium fallback row for option type {}; rendering it as unsupported", (Object)option.getClass().getName(), (Object)e);
            return new UnsupportedOptionRow(dim, this.theme, this.optionStateStore, option);
        }
    }
}

