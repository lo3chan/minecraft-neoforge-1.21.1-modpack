/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.layouts.GridLayout
 *  net.minecraft.client.gui.layouts.GridLayout$RowHelper
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.options.OptionsScreen
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.diebuddies.mixins;

import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={OptionsScreen.class})
public class MixinOptionsScreen
extends Screen {
    protected MixinOptionsScreen(Component title) {
        super(title);
    }

    @Redirect(method={"init()V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/layouts/GridLayout;createRowHelper(I)Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;"))
    private GridLayout.RowHelper physicsmod$button(GridLayout instance, int columns) {
        GridLayout.RowHelper rowHelper = instance.createRowHelper(columns);
        Button physicsButton = Button.builder((Component)Component.translatable((String)"physicsmod.menu.main.title"), button -> this.minecraft.setScreen((Screen)new PhysicsSettingsScreen(this))).width(308).build();
        rowHelper.addChild((LayoutElement)physicsButton, 2);
        return rowHelper;
    }
}

