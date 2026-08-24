package com.alonie.brbe.api;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.util.BRBHelper;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class BRBBookSettings {
   private static final Map<ResourceLocation, BRBBookSettings.TypeSettings> states = new HashMap<>();

   public static void registerBook(BRBHelper.Book book) {
      if (book != null) {
         BetterRecipeBook.LOGGER.info("Registering book {}", book.resourceLocation);
         states.put(book.resourceLocation, new BRBBookSettings.TypeSettings(false, false));
      }
   }

   public static boolean isOpen(BRBHelper.Book book) {
      if (book == null) {
         return false;
      } else {
         BRBBookSettings.TypeSettings settings = states.get(book.resourceLocation);
         return settings == null ? false : settings.open;
      }
   }

   public static void setOpen(BRBHelper.Book book, boolean bl) {
      if (book != null) {
         BRBBookSettings.TypeSettings settings = states.get(book.resourceLocation);
         if (settings != null) {
            settings.open = bl;
         }
      }
   }

   public static boolean isFiltering(BRBHelper.Book book) {
      if (book == null) {
         return false;
      } else {
         BRBBookSettings.TypeSettings settings = states.get(book.resourceLocation);
         return settings == null ? false : settings.filtering;
      }
   }

   public static void setFiltering(BRBHelper.Book book, boolean bl) {
      if (book != null) {
         BRBBookSettings.TypeSettings settings = states.get(book.resourceLocation);
         if (settings != null) {
            settings.filtering = bl;
         }
      }
   }

   static class TypeSettings {
      boolean open;
      boolean filtering;

      public TypeSettings(boolean bl, boolean bl2) {
         this.open = bl;
         this.filtering = bl2;
      }
   }
}
