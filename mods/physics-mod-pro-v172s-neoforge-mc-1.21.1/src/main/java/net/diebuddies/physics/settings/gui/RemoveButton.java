/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.Component
 */
package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.gui.FunctionButton;
import net.diebuddies.physics.settings.ux.GUIResources;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class RemoveButton
extends FunctionButton {
    public RemoveButton(int i, int j, int k, int l, Component component, Button.OnPress onPress) {
        super(i, j, k, l, component, onPress, GUIResources.REMOVE_TEXTURE);
    }
}

