package com.teamresourceful.resourcefulconfig.client;

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.client.components.configs.ConfigHeaderItem;
import com.teamresourceful.resourcefulconfig.client.components.configs.ConfigItem;
import com.teamresourceful.resourcefulconfig.client.components.configs.ConfigsListWidget;
import com.teamresourceful.resourcefulconfig.common.config.Configurations;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.Nullable;

public class ConfigsScreen extends Screen {
   private final Screen parent;
   private ConfigsListWidget configs;
   @Nullable
   private String modid;

   public ConfigsScreen(Screen parent, @Nullable String modid) {
      this(parent);
      this.modid = modid;
   }

   public ConfigsScreen(Screen parent) {
      super(CommonComponents.EMPTY);
      this.parent = parent;
   }

   protected void rebuildWidgets() {
      ConfigsListWidget oldList = this.configs;
      super.rebuildWidgets();
      this.configs.update(oldList);
   }

   protected void init() {
      int contentWidth = this.width - 20;
      int contentHeight = this.height - 20;
      this.configs = (ConfigsListWidget)this.addRenderableWidget(new ConfigsListWidget(contentWidth, contentHeight));
      this.configs.setPosition(10, 10);
      this.configs.add(new ConfigHeaderItem());
      if (this.modid == null) {
         for (ResourcefulConfig value : Configurations.INSTANCE.configs().values()) {
            this.configs.add(new ConfigItem(value));
         }
      } else {
         for (String config : Configurations.INSTANCE.modToConfigs().getOrDefault(this.modid, Set.of())) {
            this.configs.add(new ConfigItem(Configurations.INSTANCE.configs().get(config)));
         }
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.fill(0, 0, this.width, this.height, -15526633);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.getChildAt(mouseX, mouseY).isEmpty()) {
         this.setFocused(null);
         return false;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }
}
