package com.seibel.distanthorizons.neoforge.mixins.client;

import com.seibel.distanthorizons.common.wrappers.gui.DhScreenUtil_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.GetConfigScreen_neoforge;
import com.seibel.distanthorizons.common.wrappers.gui.TexturedButtonWidget_neoforge;
import com.seibel.distanthorizons.core.config.Config;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.FrameLayout.ChildContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({OptionsScreen.class})
public class MixinOptionsScreen extends Screen {
   @Unique
   private static final ResourceLocation ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath("distanthorizons", "textures/gui/button.png");
   @Unique
   private TexturedButtonWidget_neoforge optionsButton = null;
   @Shadow
   @Final
   protected HeaderAndFooterLayout layout;

   protected MixinOptionsScreen(Component title) {
      super(title);
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"init"}
   )
   private void dhConfig$init(CallbackInfo ci) {
      if (Config.Client.showDhOptionsButtonInMinecraftUi.get()) {
         this.addRenderableWidget(this.getOptionsButton());
         LinearLayout layout = (LinearLayout)((ChildContainer)this.layout.headerFrame.children.get(0)).child;
         AtomicInteger width = new AtomicInteger(0);
         layout.visitChildren(x -> width.addAndGet(x.getWidth()));
         width.addAndGet(-10);
         layout.wrapped.addChild(this.getOptionsButton(), 1, 2, settings -> settings.paddingLeft(width.get() * -1));
         layout.arrangeElements();
      }
   }

   @Unique
   public TexturedButtonWidget_neoforge getOptionsButton() {
      if (this.optionsButton == null) {
         this.optionsButton = new TexturedButtonWidget_neoforge(
            this.width / 2 - 180,
            this.height / 6 - 12,
            20,
            20,
            0,
            0,
            20,
            ICON_TEXTURE,
            20,
            40,
            buttonWidget -> DhScreenUtil_neoforge.setScreen(GetConfigScreen_neoforge.getScreen(this)),
            Component.translatable("distanthorizons.title")
         );
      }

      return this.optionsButton;
   }
}
