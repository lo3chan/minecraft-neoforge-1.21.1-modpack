package com.iafenvoy.jupiter.render.screen;

import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.render.TitleStack;
import com.iafenvoy.jupiter.render.screen.scrollbar.HorizontalScrollBar;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigContainerScreen extends ConfigListScreen {
   protected final AbstractConfigContainer container;
   protected final List<ConfigContainerScreen.TabButton> groupButtons = new ArrayList<>();
   protected final HorizontalScrollBar groupScrollBar = new HorizontalScrollBar();
   private int currentTab = 0;
   private ConfigGroup currentGroup;

   public ConfigContainerScreen(Screen parent, AbstractConfigContainer container, boolean client) {
      super(parent, TitleStack.create(container.getTitle()), container.getConfigId(), client);
      this.container = container;
      this.currentGroup = container.getConfigTabs().isEmpty() ? ConfigGroup.EMPTY : (ConfigGroup)container.getConfigTabs().getFirst();
      this.topBorder = 60;
   }

   @Override
   protected void init() {
      this.entries = this.currentGroup.getConfigs();
      super.init();
      int x = 10;
      int y = 27;
      this.groupButtons.clear();
      List<ConfigGroup> configTabs = this.container.getConfigTabs();

      for (int i = 0; i < configTabs.size(); i++) {
         ConfigGroup category = configTabs.get(i);
         ConfigContainerScreen.TabButton tabButton = (ConfigContainerScreen.TabButton)this.addRenderableWidget(
            new ConfigContainerScreen.TabButton(category, x, y, this.font.width(category.getName()) + 10, 20, button -> {
               this.currentTab = this.container.getConfigTabs().indexOf(button.group);
               this.currentGroup = button.group;
               this.rebuildWidgets();
            })
         );
         tabButton.active = i != this.currentTab;
         this.groupButtons.add(tabButton);
         x += tabButton.getWidth() + 2;
      }

      x += 10;
      this.groupScrollBar.setMaxValue(Math.max(0, x - this.width));
      this.updateTabPos();
   }

   protected void updateTabPos() {
      for (ConfigContainerScreen.TabButton button : this.groupButtons) {
         button.updatePos(this.groupScrollBar.getValue());
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
         return true;
      } else if (mouseX >= 10.0 && mouseX <= this.width - 20 && mouseY >= 25.0 && mouseY <= 60.0) {
         this.groupScrollBar.setValue(this.groupScrollBar.getValue() + (scrollY > 0.0 ? -20 : 20));
         this.updateTabPos();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onClose() {
      this.container.onConfigsChanged();
      super.onClose();
   }

   @Nullable
   @Override
   protected ResourceLocation getBackgroundTexture(boolean ingame) {
      return this.container.getBackgroundTexture(ingame);
   }

   @Override
   public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.groupScrollBar.render(graphics, mouseX, mouseY, partialTicks, 10, 50, this.width - 20, 8, this.width + this.groupScrollBar.getMaxValue());
      if (this.groupScrollBar.isDragging()) {
         this.updateTabPos();
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.groupScrollBar.wasMouseOver()) {
         this.groupScrollBar.setIsDragging(true);
         this.updateTabPos();
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.groupScrollBar.setIsDragging(false);
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public static class TabButton extends Button {
      private final ConfigGroup group;
      private final int baseX;

      public TabButton(ConfigGroup group, int baseX, int y, int width, int height, Consumer<ConfigContainerScreen.TabButton> listener) {
         super(baseX, y, width, height, group.getName(), button -> listener.accept((ConfigContainerScreen.TabButton)button), DEFAULT_NARRATION);
         this.group = group;
         this.baseX = baseX;
      }

      public void updatePos(int offsetX) {
         this.setX(this.baseX - offsetX);
      }
   }
}
