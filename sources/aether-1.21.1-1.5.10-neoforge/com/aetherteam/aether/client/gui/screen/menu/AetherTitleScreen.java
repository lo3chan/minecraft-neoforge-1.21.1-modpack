package com.aetherteam.aether.client.gui.screen.menu;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.gui.component.menu.AetherMenuButton;
import com.aetherteam.aether.client.gui.screen.menu.logo.AetherLogoRenderer;
import com.aetherteam.aether.client.gui.screen.menu.splash.AetherSplashRenderer;
import com.aetherteam.aether.mixin.mixins.client.accessor.TitleScreenAccessor;
import com.aetherteam.cumulus.CumulusConfig;
import com.aetherteam.cumulus.client.gui.screen.DynamicMenuButton;
import com.aetherteam.cumulus.mixin.mixins.client.accessor.SplashRendererAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.Music;
import net.neoforged.neoforge.internal.BrandingControl;

public class AetherTitleScreen extends TitleScreen implements TitleScreenBehavior, CustomBranding {
   public static final Music MENU = new Music(AetherSoundEvents.MUSIC_MENU, 20, 600, true);
   private final boolean alignedLeft;
   private Map<Component, AbstractWidget> widgetsByName = new HashMap<>();
   public int buttonRows = 0;
   public int lastY = 0;

   public AetherTitleScreen() {
      this(false);
   }

   public AetherTitleScreen(boolean alignedLeft) {
      this.alignedLeft = alignedLeft;
      TitleScreenAccessor accessor = (TitleScreenAccessor)this;
      accessor.aether$setFading(true);
      accessor.aether$setLogoRenderer(new AetherLogoRenderer(false, this.alignedLeft));
   }

   protected void init() {
      TitleScreenAccessor accessor = (TitleScreenAccessor)this;
      this.buttonRows = 0;
      this.lastY = 0;
      super.init();
      if (this.minecraft != null) {
         accessor.aether$setSplash(
            new AetherSplashRenderer(this.alignedLeft, ((SplashRendererAccessor)((TitleScreenAccessor)this).aether$getSplash()).cumulus$getSplash())
         );
      }

      this.setupButtons();
      this.widgetsByName = this.children()
         .stream()
         .filter(e -> e instanceof AbstractWidget)
         .map(e -> (AbstractWidget)e)
         .collect(Collectors.toMap(AbstractWidget::getMessage, e -> (AbstractWidget)e));
   }

   public void setupButtons() {
      if ((Boolean)AetherConfig.CLIENT.enable_server_button.get()) {
         Component component = ((TitleScreenAccessor)this).callGetMultiplayerDisabledReason();
         boolean flag = component == null;
         Tooltip tooltip = component != null ? Tooltip.create(component) : null;
         Button serverButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.aether.menu.server"), button -> {
            ServerData serverData = new ServerData("OATS", "oats.aether-mod.net", Type.OTHER);
            ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(serverData.ip), serverData, false, null);
         }).bounds(this.width / 2 - 100, this.height / 4 + 48 + 72, 200, 20).tooltip(tooltip).build());
         serverButton.active = flag;
         Predicate<AbstractWidget> predicate = abstractWidgetx -> abstractWidgetx.getMessage().equals(Component.translatable("menu.multiplayer"))
            || abstractWidgetx.getMessage().equals(Component.translatable("menu.online"));
         this.children().removeIf(button -> button instanceof AbstractWidget abstractWidgetx && predicate.test(abstractWidgetx));
         this.renderables.removeIf(button -> button instanceof AbstractWidget abstractWidgetx && predicate.test(abstractWidgetx));
      }

      for (Renderable renderable : this.renderables) {
         if (renderable instanceof AbstractWidget abstractWidget) {
            Component buttonText = abstractWidget.getMessage();
            if (TitleScreenBehavior.isImageButton(buttonText)) {
               abstractWidget.visible = false;
            }
         }
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      int xOffset = CumulusConfig.CLIENT.enable_menu_api.get() && CumulusConfig.CLIENT.enable_menu_list_button.get() ? -62 : 0;

      for (GuiEventListener child : this.children()) {
         if (child instanceof AetherMenuButton aetherButton) {
            if (aetherButton.isMouseOver(mouseX, mouseY)) {
               if (aetherButton.hoverOffset < 15) {
                  aetherButton.hoverOffset += 2;
               }
            } else if (aetherButton.hoverOffset > 0) {
               aetherButton.hoverOffset -= 2;
            }
         }

         if (child instanceof DynamicMenuButton dynamicMenuButton && dynamicMenuButton.enabled) {
            xOffset -= 24;
         }
      }

      TitleScreenBehavior.super.handleImageButtons(this, xOffset);
      if (this.alignedLeft) {
         TitleScreenBehavior.super.handleEssentialButtonsForLeftMenu(this);
      }
   }

   @Override
   public boolean forEachLineBranding(boolean includeMC, boolean reverse, BiConsumer<Integer, String> lineConsumer, GuiGraphics guiGraphics, int i) {
      if (this.alignedLeft) {
         BrandingControl.forEachLine(
            true,
            true,
            (brandingLine, branding) -> guiGraphics.drawString(
               this.font, branding, this.width - this.font.width(branding) - 1, this.height - (10 + (brandingLine + 1) * (9 + 1)), 16777215 | i
            )
         );
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean forEachAboveCopyrightLineBranding(BiConsumer<Integer, String> lineConsumer, GuiGraphics guiGraphics, int i) {
      if (this.alignedLeft) {
         BrandingControl.forEachAboveCopyrightLine(
            (brandingLine, branding) -> guiGraphics.drawString(this.font, branding, 1, this.height - (brandingLine + 1) * (9 + 1), 16777215 | i)
         );
         return true;
      } else {
         return false;
      }
   }

   protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T renderable) {
      if (renderable instanceof Button button && TitleScreenBehavior.isMainButton(button.getMessage())) {
         AetherMenuButton aetherButton = new AetherMenuButton(this, button);
         Component buttonText = aetherButton.getMessage();
         if (this.isAlignedLeft()) {
            this.buttonRows++;
         } else if (this.lastY < aetherButton.originalY) {
            this.lastY = aetherButton.originalY;
            this.buttonRows++;
         }

         if (buttonText.equals(Component.translatable("gui.aether.menu.server"))) {
            aetherButton.serverButton = true;
            aetherButton.buttonCountOffset = 2;
         } else {
            aetherButton.buttonCountOffset = this.buttonRows;
         }

         if ((Boolean)AetherConfig.CLIENT.enable_server_button.get() && buttonText.equals(Component.translatable("menu.singleplayer"))) {
            this.buttonRows++;
         }

         if (this.isAlignedLeft()) {
            aetherButton.setX(16);
            aetherButton.setY(50 + aetherButton.buttonCountOffset * 25);
            aetherButton.setWidth(200);
         } else {
            aetherButton.setY(this.height / 4 + 31 + 25 * (aetherButton.buttonCountOffset - 1));
         }

         return (T)super.addRenderableWidget(aetherButton);
      } else {
         return (T)super.addRenderableWidget(renderable);
      }
   }

   public boolean isAlignedLeft() {
      return this.alignedLeft;
   }

   @Override
   public Map<Component, AbstractWidget> getWidgetsByName() {
      return this.widgetsByName;
   }
}
