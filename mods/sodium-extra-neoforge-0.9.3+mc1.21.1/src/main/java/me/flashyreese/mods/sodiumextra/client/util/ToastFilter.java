/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.toasts.AdvancementToast
 *  net.minecraft.client.gui.components.toasts.RecipeToast
 *  net.minecraft.client.gui.components.toasts.SystemToast
 *  net.minecraft.client.gui.components.toasts.Toast
 *  net.minecraft.client.gui.components.toasts.TutorialToast
 */
package me.flashyreese.mods.sodiumextra.client.util;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.TutorialToast;

public final class ToastFilter {
    public static boolean isEnabled(Toast toast) {
        SodiumExtraGameOptions.ExtraSettings extraSettings = SodiumExtraClientMod.options().extraSettings;
        return !(!extraSettings.toasts || !extraSettings.tutorialToast && toast instanceof TutorialToast || !extraSettings.systemToast && toast instanceof SystemToast || !extraSettings.recipeToast && toast instanceof RecipeToast || !extraSettings.advancementToast && toast instanceof AdvancementToast);
    }
}

