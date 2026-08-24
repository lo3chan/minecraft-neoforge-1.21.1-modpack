package net.diebuddies.mixins;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.ux.Animatable;
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

@Mixin({TitleScreen.class})
public class MixinTitleScreen {
   @Inject(
      at = {@At("RETURN")},
      method = {"preloadResources"},
      cancellable = true
   )
   private static void preloadResources(TextureManager textureManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Void>> info) {
      CompletableFuture<Void> future = (CompletableFuture<Void>)info.getReturnValue();
      info.setReturnValue(
         CompletableFuture.allOf(
            future,
            textureManager.preload(GUIResources.PARALLAX_BLOCKS_BACKGROUND, executor),
            textureManager.preload(GUIResources.PARALLAX_BLOCKS_CLOUDS, executor),
            textureManager.preload(GUIResources.PARALLAX_BLOCKS_RUBBLE, executor),
            textureManager.preload(GUIResources.EDIT_TEXTURE, executor),
            textureManager.preload(GUIResources.EDIT_TEXTURE, executor),
            textureManager.preload(GUIResources.REMOVE_TEXTURE, executor),
            textureManager.preload(GUIResources.BACKGROUND_TEXTURE, executor)
         )
      );
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"init"}
   )
   private void init(CallbackInfo info) {
      TitleScreen screen = (TitleScreen)this;
      if (screen.children != null && ConfigClient.firstStartup) {
         for (GuiEventListener widget : screen.children) {
            if (widget instanceof Button button) {
               Component component = button.getMessage();
               if (component != null) {
                  ComponentContents content = component.getContents();
                  if (content != null && content instanceof TranslatableContents translatable) {
                     String key = translatable.getKey();
                     if (key != null && key.equalsIgnoreCase("menu.options")) {
                        ((Animatable)button).addAnimator(new HighlightButtonRenderer());
                     }
                  }
               }
            }
         }
      }
   }
}
