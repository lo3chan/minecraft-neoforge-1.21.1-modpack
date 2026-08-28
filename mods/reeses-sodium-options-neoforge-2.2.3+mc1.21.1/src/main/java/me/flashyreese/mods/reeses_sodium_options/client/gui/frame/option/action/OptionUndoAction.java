/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.config.structure.StatefulOption
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action;

import java.util.Objects;
import me.flashyreese.mods.reeses_sodium_options.client.config.ReeseSodiumOptionsConfig;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.minecraft.resources.ResourceLocation;

public final class OptionUndoAction {
    static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath((String)"reeses-sodium-options", (String)"textures/gui/undo_to_unmodified.png");

    public static boolean isVisible(StatefulOption<?> option) {
        return ReeseSodiumOptionsConfig.config().isUndoButtonOverlay() && (ReeseSodiumOptionsConfig.config().isAlwaysShowActionButtons() || OptionUndoAction.canUndo(option));
    }

    public static boolean isActive(StatefulOption<?> option) {
        return ReeseSodiumOptionsConfig.config().isUndoButtonOverlay() && OptionUndoAction.canUndo(option);
    }

    public static boolean canUndo(StatefulOption<?> option) {
        return option.isEnabled() && option.hasChanged() && !Objects.equals(option.getValidatedValue(), option.getAppliedValue());
    }

    public static void undoChanges(StatefulOption<?> option) {
        option.modifyValue(option.getAppliedValue());
    }

    public static void normalizeEquivalentChange(StatefulOption<?> option) {
        if (option.hasChanged() && Objects.equals(option.getValidatedValue(), option.getAppliedValue())) {
            OptionUndoAction.undoChanges(option);
        }
    }
}

