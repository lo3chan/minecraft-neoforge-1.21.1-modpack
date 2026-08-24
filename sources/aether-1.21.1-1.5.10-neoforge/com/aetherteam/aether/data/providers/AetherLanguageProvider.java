package com.aetherteam.aether.data.providers;

import com.aetherteam.nitrogen.data.providers.NitrogenLanguageProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;

public abstract class AetherLanguageProvider extends NitrogenLanguageProvider {
   private final Map<String, String> PRO_TIPS = new HashMap<>();
   public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final PackOutput output;

   public AetherLanguageProvider(PackOutput output, String id) {
      super(output, id);
      this.output = output;
   }

   public void addJukeboxSong(String songName, String name) {
      this.add("jukebox_song." + this.id + "." + songName, name);
   }

   public void addMoaSkinsText(String key, String name) {
      this.addGuiText("moa_skins." + key, name);
   }

   public void addCustomizationText(String key, String name) {
      this.addGuiText("customization." + key, name);
   }

   public void addLoreBookText(String key, String name) {
      this.addGuiText("book_of_lore." + key, name);
   }

   public void addLore(Supplier<? extends ItemLike> key, String name) {
      this.add("lore." + key.get().asItem().getDescriptionId(), name);
   }

   public void addLoreUnique(String key, String name) {
      this.add("lore." + key, name);
   }

   public void addProTip(String key, String name) {
      String fullKey = "aether.pro_tips.line." + this.id + "." + key;
      this.add(fullKey, name);
      this.PRO_TIPS.put(fullKey, key);
   }

   public void addMenuTitle(String key, String name) {
      this.add(this.id + ".menu_title." + key, name);
   }

   public void addAttribute(String key, String name) {
      this.add(this.id + ".attribute.name." + key, name);
   }

   public CompletableFuture<?> run(CachedOutput cache) {
      return super.run(cache);
   }
}
