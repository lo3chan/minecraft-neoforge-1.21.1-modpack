/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.contents.TranslatableContents
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.GUIResources;
import net.diebuddies.physics.settings.ux.HighlightButtonRenderer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={TitleScreen.class})
public class MixinTitleScreen {
    @Inject(at={@At(value="RETURN")}, method={"preloadResources"}, cancellable=true)
    private static void preloadResources(TextureManager textureManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Void>> info) {
        CompletableFuture future = (CompletableFuture)info.getReturnValue();
        info.setReturnValue(CompletableFuture.allOf(future, textureManager.preload(GUIResources.PARALLAX_BLOCKS_BACKGROUND, executor), textureManager.preload(GUIResources.PARALLAX_BLOCKS_CLOUDS, executor), textureManager.preload(GUIResources.PARALLAX_BLOCKS_RUBBLE, executor), textureManager.preload(GUIResources.EDIT_TEXTURE, executor), textureManager.preload(GUIResources.EDIT_TEXTURE, executor), textureManager.preload(GUIResources.REMOVE_TEXTURE, executor), textureManager.preload(GUIResources.BACKGROUND_TEXTURE, executor)));
    }

    @Inject(at={@At(value="TAIL")}, method={"init"})
    private void init(CallbackInfo info) {
        TitleScreen screen = (TitleScreen)this;
        if (screen.children == null || !ConfigClient.firstStartup) {
            return;
        }
        for (GuiEventListener widget : screen.children) {
            TranslatableContents translatable;
            String key;
            ComponentContents content;
            Button button;
            Component component;
            if (!(widget instanceof Button) || (component = (button = (Button)widget).getMessage()) == null || (content = component.getContents()) == null || !(content instanceof TranslatableContents) || (key = (translatable = (TranslatableContents)content).getKey()) == null || !key.equalsIgnoreCase("menu.options")) continue;
            ((Animatable)button).addAnimator((Animator)new HighlightButtonRenderer());
        }
    }
}

