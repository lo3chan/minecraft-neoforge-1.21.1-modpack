package net.diebuddies.mixins;

import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({OptionsScreen.class})
public class MixinOptionsScreen extends Screen {
   protected MixinOptionsScreen(Component title) {
      super(title);
   }

   @Redirect(
      method = {"init()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/layouts/GridLayout;createRowHelper(I)Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;"
      )
   )
   private RowHelper physicsmod$button(GridLayout instance, int columns) {
      RowHelper rowHelper = instance.createRowHelper(columns);
      Button physicsButton = Button.builder(
            Component.translatable("physicsmod.menu.main.title"), button -> this.minecraft.setScreen(new PhysicsSettingsScreen(this))
         )
         .width(308)
         .build();
      rowHelper.addChild(physicsButton, 2);
      return rowHelper;
   }
}
