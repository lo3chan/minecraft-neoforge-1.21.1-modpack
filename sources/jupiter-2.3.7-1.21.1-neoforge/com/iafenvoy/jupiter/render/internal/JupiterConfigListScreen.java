package com.iafenvoy.jupiter.render.internal;

import com.iafenvoy.jupiter.compat.ExtraConfigManager;
import com.iafenvoy.jupiter.config.ConfigSide;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.config.container.wrapper.RemoteConfigWrapper;
import com.iafenvoy.jupiter.network.ClientConfigNetwork;
import com.iafenvoy.jupiter.render.screen.JupiterScreen;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class JupiterConfigListScreen extends Screen implements JupiterScreen {
   private final Map<AbstractConfigContainer, RemoteConfigWrapper> remoteCache = new LinkedHashMap<>();
   private final Screen parent;
   private JupiterConfigListWidget widget;
   private Button editLocalButton = null;
   private Button editRemoteButton = null;
   private boolean initialized = false;

   public JupiterConfigListScreen(Screen parent) {
      super(TextUtil.translatable("jupiter.screen.config_list.title"));
      this.parent = parent;
      ExtraConfigManager.scanConfigs();
   }

   protected void init() {
      super.init();
      if (!this.initialized) {
         this.initialized = true;
         this.widget = new JupiterConfigListWidget(this, this.minecraft, this.width - 80, this.height - 80, 60);
      }

      this.widget.updateSize(this.width - 80, new HeaderAndFooterLayout(this, 50, 20));
      this.widget.setX(40);
      this.widget.update();
      this.remoteCache.clear();
      this.addRenderableWidget(this.widget);
      this.addRenderableWidget(JupiterScreen.createButton(40, 25, 60, 20, TextUtil.translatable("jupiter.screen.back"), button -> this.onClose()));
      ((EditBox)this.addRenderableWidget(new EditBox(this.font, 105, 25, Math.max(10, this.width - 315), 20, TextUtil.empty())))
         .setResponder(this.widget::setFilter);
      this.editLocalButton = (Button)this.addRenderableWidget(
         JupiterScreen.createButton(this.width - 205, 25, 80, 20, TextUtil.translatable("jupiter.screen.edit_local"), button -> {
            JupiterConfigListWidget.Entry handler = (JupiterConfigListWidget.Entry)this.widget.getSelected();
            if (this.minecraft != null && handler != null) {
               this.minecraft.setScreen(JupiterScreen.getConfigScreen(this, handler.getConfigContainer(), false));
            }
         })
      );
      this.editRemoteButton = (Button)this.addRenderableWidget(
         JupiterScreen.createButton(this.width - 120, 25, 80, 20, TextUtil.translatable("jupiter.screen.edit_remote"), button -> {
            JupiterConfigListWidget.Entry handler = (JupiterConfigListWidget.Entry)this.widget.getSelected();
            if (this.minecraft != null && handler != null) {
               AbstractConfigContainer container = handler.getConfigContainer();
               if (this.remoteCache.containsKey(container)) {
                  this.minecraft.setScreen(JupiterScreen.getConfigScreen(this, this.remoteCache.get(container), false));
               }
            }
         })
      );
      this.updateButtonState();
   }

   public void updateButtonState() {
      JupiterConfigListWidget.Entry entry = (JupiterConfigListWidget.Entry)this.widget.getSelected();
      if (this.editLocalButton != null) {
         this.editLocalButton.active = entry != null;
      }

      if (this.editRemoteButton != null) {
         this.editRemoteButton.active = false;
         if (entry == null) {
            return;
         }

         AbstractConfigContainer origin = entry.getConfigContainer();
         if (origin.getSide() == ConfigSide.CLIENT || this.remoteCache.containsKey(origin) || !JupiterScreen.connectedToDedicatedServer()) {
            return;
         }

         ClientConfigNetwork.syncConfig(origin.getConfigId(), tag -> {
            if (tag != null) {
               this.editRemoteButton.active = true;
               RemoteConfigWrapper wrapper = new RemoteConfigWrapper(origin);
               wrapper.deserializeNbt(tag);
               this.remoteCache.put(origin, wrapper);
            }
         });
      }
   }

   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.render(graphics, mouseX, mouseY, delta);
      this.widget.render(graphics, mouseX, mouseY, delta);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, -1);
   }

   public void onClose() {
      super.onClose();

      assert this.minecraft != null;

      this.minecraft.setScreen(this.parent);
   }
}
