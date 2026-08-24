package com.seibel.distanthorizons.fabric.mixins.client;

import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.GetConfigScreen_fabric;
import com.seibel.distanthorizons.common.wrappers.gui.TexturedButtonWidget_fabric;
import com.seibel.distanthorizons.core.config.Config;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_429;
import net.minecraft.class_437;
import net.minecraft.class_8132;
import net.minecraft.class_8667;
import net.minecraft.class_7843.class_7844;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_429.class})
public class MixinOptionsScreen extends class_437 {
   @Unique
   private static final class_2960 ICON_TEXTURE = class_2960.method_60655("distanthorizons", "textures/gui/button.png");
   @Unique
   private TexturedButtonWidget_fabric optionsButton = null;
   @Shadow
   @Final
   protected class_8132 field_49502;

   protected MixinOptionsScreen(class_2561 title) {
      super(title);
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"init"}
   )
   private void dhConfig$init(CallbackInfo ci) {
      if (Config.Client.showDhOptionsButtonInMinecraftUi.get()) {
         this.method_37063(this.getOptionsButton());
         class_8667 layout = (class_8667)((class_7844)this.field_49502.field_42491.field_40766.get(0)).field_40752;
         AtomicInteger width = new AtomicInteger(0);
         layout.method_48227(x -> width.addAndGet(x.method_25368()));
         width.addAndGet(-10);
         layout.field_45400.method_52734(this.getOptionsButton(), 1, 2, settings -> settings.method_46469(width.get() * -1));
         layout.method_48222();
      }
   }

   @Unique
   public TexturedButtonWidget_fabric getOptionsButton() {
      if (this.optionsButton == null) {
         this.optionsButton = new TexturedButtonWidget_fabric(
            this.field_22789 / 2 - 180,
            this.field_22790 / 6 - 12,
            20,
            20,
            0,
            0,
            20,
            ICON_TEXTURE,
            20,
            40,
            buttonWidget -> DhScreenUtil_fabric.setScreen(GetConfigScreen_fabric.getScreen(this)),
            class_2561.method_43471("distanthorizons.title")
         );
      }

      return this.optionsButton;
   }
}
