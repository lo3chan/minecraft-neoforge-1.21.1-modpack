package com.alonie.brbe.util;

import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.api.BRBBookSettings;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BRBHelper {
   public static BRBHelper.Book createBook(String mod_id, String name) {
      ResourceLocation location = ResourceLocation.fromNamespaceAndPath(mod_id, name);
      String hash = location + "#";
      Pair<String, String> pair = new Pair(hash + "isGuiOpen", hash + "isFiltering");
      BRBHelper.Book book = new BRBHelper.Book(location, pair);
      BRBBookSettings.registerBook(book);
      return book;
   }

   public static class Book {
      public ResourceLocation resourceLocation;
      public Pair<String, String> pair;

      Book(ResourceLocation resourceLocation, Pair<String, String> pair) {
         this.resourceLocation = resourceLocation;
         this.pair = pair;
      }

      public BRBBookCategories.Category createCategory(ItemStack... entries) {
         return BRBBookCategories.createCategory(this, entries);
      }

      public BRBBookCategories.Category createSearch() {
         return BRBBookCategories.createSearch(this);
      }
   }
}
