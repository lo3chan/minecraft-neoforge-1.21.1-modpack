/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.Config
 *  net.caffeinemc.mods.sodium.client.config.structure.StatefulOption
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action;

import java.util.Objects;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action.OptionUndoAction;
import me.flashyreese.mods.reeses_sodium_options.client.gui.option.OptionStateProvider;
import net.caffeinemc.mods.sodium.client.config.structure.Config;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.minecraft.resources.ResourceLocation;

public final class OptionResetAction {
    static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath((String)"reeses-sodium-options", (String)"textures/gui/reset_to_default.png");

    public static boolean isVisible(StatefulOption<?> option) {
        return ReeseSodiumOptionsConfig.config().isResetButtonOverlay() && (ReeseSodiumOptionsConfig.config().isAlwaysShowActionButtons() || OptionResetAction.canReset(option));
    }

    public static boolean isActive(StatefulOption<?> option) {
        return ReeseSodiumOptionsConfig.config().isResetButtonOverlay() && OptionResetAction.canReset(option);
    }

    public static boolean canReset(StatefulOption<?> option) {
        if (!option.isEnabled()) {
            return false;
        }
        Config config = OptionResetAction.getParentConfig(option);
        return config != null && !Objects.equals(option.getValidatedValue(), option.getDefaultValue().get(config));
    }

    public static void resetToDefault(StatefulOption<?> option) {
        option.resetToDefault();
        OptionUndoAction.normalizeEquivalentChange(option);
    }

    private static Config getParentConfig(StatefulOption<?> option) {
        if (!(option instanceof OptionStateProvider)) {
            return null;
        }
        OptionStateProvider stateProvider = (OptionStateProvider)option;
        return stateProvider.rso$getParentConfig();
    }
}

