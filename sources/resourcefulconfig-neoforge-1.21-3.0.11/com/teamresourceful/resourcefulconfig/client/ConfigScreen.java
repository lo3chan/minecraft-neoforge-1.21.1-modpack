package com.teamresourceful.resourcefulconfig.client;

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigButton;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigEntry;
import com.teamresourceful.resourcefulconfig.client.components.categories.CategoriesListWidget;
import com.teamresourceful.resourcefulconfig.client.components.categories.CategoryItem;
import com.teamresourceful.resourcefulconfig.client.components.header.HeaderWidget;
import com.teamresourceful.resourcefulconfig.client.components.options.Options;
import com.teamresourceful.resourcefulconfig.client.components.options.OptionsListWidget;
import com.teamresourceful.resourcefulconfig.client.screens.base.CloseableScreen;
import com.teamresourceful.resourcefulconfig.client.utils.ConfigSearching;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends Screen implements CloseableScreen {
   private final Screen parent;
   private final ResourcefulConfig config;
   private final Function<String, List<String>> termCollector;
   private OptionsListWidget optionsList = null;
   private CategoriesListWidget categoriesList = null;

   public ConfigScreen(Screen parent, ResourcefulConfig config) {
      this(parent, config, s -> List.of());
   }

   public ConfigScreen(Screen parent, ResourcefulConfig config, Function<String, List<String>> termCollector) {
      super(CommonComponents.EMPTY);
      this.parent = parent;
      this.config = config;
      this.termCollector = termCollector;
   }

   protected void rebuildWidgets() {
      OptionsListWidget oldList = this.optionsList;
      super.rebuildWidgets();
      this.optionsList.update(oldList);
   }

   protected void init() {
      int contentWidth = this.width - 20;
      int contentHeight = this.height - 20;
      int optionsWidth = contentWidth;
      LinearLayout layout = LinearLayout.vertical().spacing(10);
      HeaderWidget header = (HeaderWidget)layout.addChild(new HeaderWidget(this.width - 20, this.config, () -> {
         this.updateOptions();
         this.updateCategories();
      }));
      contentHeight -= header.getHeight() + 10;
      LinearLayout body = (LinearLayout)layout.addChild(LinearLayout.horizontal().spacing(10));
      if (!this.config.categories().isEmpty()) {
         int categoryWidth = contentWidth / 4;
         this.categoriesList = (CategoriesListWidget)body.addChild(new CategoriesListWidget(categoryWidth, contentHeight));
         this.updateCategories();
         optionsWidth = contentWidth - categoryWidth - 10;
      }

      this.optionsList = (OptionsListWidget)body.addChild(new OptionsListWidget(optionsWidth, contentHeight));
      this.updateOptions();
      layout.arrangeElements();
      layout.setPosition(10, 10);
      layout.visitWidgets(x$0 -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
      });
   }

   public void updateOptions() {
      this.optionsList.clear();
      Map<String, ResourcefulConfigEntry> entries = new LinkedHashMap<>();
      this.config.entries().forEach((key, value) -> {
         if (ConfigSearching.fulfillsSearch(value, this.termCollector)) {
            entries.put(key, value);
         }
      });
      List<ResourcefulConfigButton> buttons = new ArrayList<>();
      this.config.buttons().forEach(button -> {
         if (ConfigSearching.fulfillsSearch(button, this.termCollector)) {
            buttons.add(button);
         }
      });
      Options.populateOptions(this.optionsList, entries, buttons);
   }

   public void updateCategories() {
      if (this.categoriesList != null) {
         this.categoriesList.clear();

         for (ResourcefulConfig value : this.config.categories().values()) {
            if (ConfigSearching.fulfillsSearch(value, this.termCollector)) {
               this.categoriesList.add(new CategoryItem(this, value, this.termCollector));
            }
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

   public boolean keyPressed(int i, int j, int k) {
      if (super.keyPressed(i, j, k)) {
         return true;
      } else if (i == 256) {
         this.onClose();
         return true;
      } else {
         return false;
      }
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void onClosed(@Nullable Screen screen) {
      boolean shouldSave = screen == null || screen == this.parent && !(this.parent instanceof ConfigScreen);
      if (shouldSave) {
         this.config.save();
      }
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }
}
