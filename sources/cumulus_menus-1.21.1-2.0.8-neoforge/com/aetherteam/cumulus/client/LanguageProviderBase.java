package com.aetherteam.cumulus.client;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface LanguageProviderBase {
   default void addTranslations() {
      this.addMenuText("button.world_preview", "W");
      this.addMenuText("button.quick_load", "Q");
      this.addMenuText("preview", "Toggle World");
      this.addMenuText("load", "Quick Load");
      this.addGuiText("button.menu_list", "Menu List");
      this.addGuiText("button.menu_launch", "Launch Menu");
      this.addGuiText("title.menu_selection", "Choose a Main Menu");
      this.addMenuTitle("minecraft", "Minecraft");
      this.addConfig("title", "Cumulus Configuration");
      this.addConfig("section.cumulus.menus.client.toml", "Client Settings");
      this.addConfig("section.cumulus.menus.client.toml.title", "Cumulus Client Configuration");
      this.addConfig("Menu", "Menu");
      this.addConfig("Menu.tooltip", "Config options for menu settings");
      this.addConfig("Menu.button", "Options");
      this.addClientConfig("menu", "enable_menu_api", "Determines whether the Menu API is enabled or not");
      this.addClientConfig("menu", "active_menu", "Sets the current active menu title screen");
      this.addClientConfig("menu", "enable_menu_list_button", "Adds a button to the top right of the main menu screen to open a menu selection screen");
      this.addPackDescription("mod", "Cumulus Resources");
      this.addToast("world_preview", "Server still saving", "Please wait before toggling World preview");
   }

   String id();

   void add(String var1, String var2);

   void addBlock(Supplier<? extends Block> var1, String var2);

   void add(Block var1, String var2);

   void addItem(Supplier<? extends Item> var1, String var2);

   void add(Item var1, String var2);

   void addItemStack(Supplier<ItemStack> var1, String var2);

   void add(ItemStack var1, String var2);

   void addEffect(Supplier<? extends MobEffect> var1, String var2);

   void add(MobEffect var1, String var2);

   void addEntityType(Supplier<? extends EntityType<?>> var1, String var2);

   void add(EntityType<?> var1, String var2);

   void addTag(Supplier<? extends TagKey<?>> var1, String var2);

   void add(TagKey<?> var1, String var2);

   default void addToast(String key, String title, String description) {
      this.add(this.id() + "." + key + ".toast.title", title);
      this.add(this.id() + "." + key + ".toast.description", description);
   }

   default void addGuiText(String key, String name) {
      this.add("gui." + this.id() + "." + key, name);
   }

   default void addMenuText(String key, String name) {
      this.addGuiText("menu." + key, name);
   }

   default void addMenuTitle(String key, String name) {
      this.add(this.id() + ".menu_title." + key, name);
   }

   default void addConfig(String prefix, String name) {
      this.add(this.id() + ".configuration." + prefix, name);
   }

   default void addClientConfig(String prefix, String key, String name) {
      this.add("config." + this.id() + ".client." + prefix + "." + key, name);
      this.add("config." + this.id() + ".client." + prefix + "." + key + ".tooltip", name);
   }

   default void addPackDescription(String packName, String description) {
      this.add("pack." + this.id() + "." + packName + ".description", description);
   }
}
