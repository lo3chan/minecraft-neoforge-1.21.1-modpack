package com.iafenvoy.jupiter.config.container;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.config.ConfigGroup;
import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import com.iafenvoy.jupiter.util.TextUtil;
import java.lang.reflect.Field;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AutoInitConfigContainer extends FileConfigContainer {
   public AutoInitConfigContainer(ResourceLocation id, String titleKey, String path) {
      super(id, titleKey, path);
   }

   public AutoInitConfigContainer(ResourceLocation id, Component title, String path) {
      super(id, title, path);
   }

   @Override
   public void init() {
      for (Field field : this.getClass().getFields()) {
         if (AutoInitConfigContainer.AutoInitConfigCategoryBase.class.isAssignableFrom(field.getType())) {
            try {
               this.configTabs.add(((AutoInitConfigContainer.AutoInitConfigCategoryBase)field.get(this)).getCategory());
            } catch (Exception var6) {
               Jupiter.LOGGER.error("Failed to auto init category {}", field.getName(), var6);
            }
         }
      }
   }

   public static class AutoInitConfigCategoryBase {
      private final ConfigGroup category;
      private boolean loaded = false;

      public AutoInitConfigCategoryBase(String id, String translateKey) {
         this(id, TextUtil.translatable(translateKey));
      }

      public AutoInitConfigCategoryBase(String id, Component name) {
         this.category = new ConfigGroup(id, name);
      }

      public ConfigGroup getCategory() {
         if (!this.loaded) {
            this.loaded = true;

            for (Field field : this.getClass().getFields()) {
               if (ConfigEntry.class.isAssignableFrom(field.getType()) || IConfigEntry.class.isAssignableFrom(field.getType())) {
                  try {
                     this.category.addEntry((ConfigEntry<?>)field.get(this));
                  } catch (Exception var6) {
                     Jupiter.LOGGER.error("Failed to auto init config key {}", field.getName(), var6);
                  }
               }
            }
         }

         return this.category;
      }
   }
}
