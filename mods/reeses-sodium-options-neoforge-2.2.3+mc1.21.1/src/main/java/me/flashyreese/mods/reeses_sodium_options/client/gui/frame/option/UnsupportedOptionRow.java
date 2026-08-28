/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.minecraft.client.gui.GuiGraphics
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.AbstractOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.client.gui.GuiGraphics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UnsupportedOptionRow
extends AbstractOptionRow {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Reese's Sodium Options");
    private static final Set<Class<?>> WARNED_TYPES = Collections.newSetFromMap(new ConcurrentHashMap());

    UnsupportedOptionRow(LayoutBounds dim, GuiTheme theme, OptionStateStore optionStateStore, Option option) {
        super(dim, theme, optionStateStore, option);
        if (WARNED_TYPES.add(option.getClass())) {
            LOGGER.warn("No option row registered for option type {}; rendering it as unsupported instead of crashing the options screen", (Object)option.getClass().getName());
        }
    }

    @Override
    protected int controlContentWidth() {
        return 0;
    }

    @Override
    protected void renderControl(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
    }

    @Override
    protected boolean activateControl() {
        return false;
    }
}

