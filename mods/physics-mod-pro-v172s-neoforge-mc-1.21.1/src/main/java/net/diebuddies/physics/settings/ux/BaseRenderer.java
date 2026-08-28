/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.FastColor$ARGB32
 */
package net.diebuddies.physics.settings.ux;

import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.MainToolTipRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

public class BaseRenderer {
    public static void renderSettingsTooltip(LegacyOptionsList list, GuiGraphics guiGraphics, int mouseX, int mouseY, int width, int height) {
        Component tooltip = LegacyOptionsList.tooltipAt(list, mouseX, mouseY);
        if (tooltip != null) {
            float border = 28.0f;
            AbstractWidget widget = LegacyOptionsList.widgetAt(list, mouseX, mouseY);
            if ((float)mouseY > (float)height * 0.6f) {
                MainToolTipRenderer.renderToolTip(MainToolTipRenderer.TooltipAlignment.TOP, (Animatable)widget, tooltip, guiGraphics, border, (float)width - border * 2.0f, border, 1.0f, FastColor.ARGB32.color((int)255, (int)0, (int)0, (int)0));
            } else {
                MainToolTipRenderer.renderToolTip((Animatable)widget, tooltip, guiGraphics, border, (float)width - border * 2.0f, (float)height - border, 1.0f, FastColor.ARGB32.color((int)255, (int)0, (int)0, (int)0));
            }
        }
    }
}

