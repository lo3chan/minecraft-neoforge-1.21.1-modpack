package io.wispforest.owo.itemgroup;

import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import io.wispforest.owo.mixin.itemgroup.ItemGroupAccessor;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayBuilder;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTab.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public abstract class OwoItemGroup extends CreativeModeTab {
   public static final BiConsumer<Item, Output> DEFAULT_STACK_GENERATOR = (item, stacks) -> stacks.accept(item.getDefaultInstance());
   protected static final ItemGroupTab PLACEHOLDER_TAB = new ItemGroupTab(
      Icon.of(Items.AIR), Component.empty(), (br, uh) -> {}, ItemGroupTab.DEFAULT_TEXTURE, false
   );
   public final List<ItemGroupTab> tabs = new ArrayList<>();
   public final List<ItemGroupButton> buttons = new ArrayList<>();
   private final Consumer<OwoItemGroup> initializer;
   private final Supplier<Icon> iconSupplier;
   private Icon icon;
   private final IntSet activeTabs = new IntAVLTreeSet(IntComparators.NATURAL_COMPARATOR);
   private final IntSet activeTabsView = IntSets.unmodifiable(this.activeTabs);
   private boolean initialized = false;
   @Nullable
   private final ResourceLocation backgroundTexture;
   @Nullable
   private final OwoItemGroup.ScrollerTextures scrollerTextures;
   @Nullable
   private final OwoItemGroup.TabTextures tabTextures;
   private final int tabStackHeight;
   private final int buttonStackHeight;
   private final boolean useDynamicTitle;
   private final boolean displaySingleTab;
   private final boolean allowMultiSelect;

   protected OwoItemGroup(
      ResourceLocation id,
      Consumer<OwoItemGroup> initializer,
      Supplier<Icon> iconSupplier,
      int tabStackHeight,
      int buttonStackHeight,
      @Nullable ResourceLocation backgroundTexture,
      @Nullable OwoItemGroup.ScrollerTextures scrollerTextures,
      @Nullable OwoItemGroup.TabTextures tabTextures,
      boolean useDynamicTitle,
      boolean displaySingleTab,
      boolean allowMultiSelect
   ) {
      super(
         null,
         -1,
         Type.CATEGORY,
         Component.translatable("itemGroup.%s.%s".formatted(id.getNamespace(), id.getPath())),
         () -> ItemStack.EMPTY,
         (displayContext, entries) -> {},
         null,
         false,
         89,
         ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tabs.png"),
         4210752,
         -2130706433,
         new ArrayList(),
         new ArrayList()
      );
      this.initializer = initializer;
      this.iconSupplier = iconSupplier;
      this.tabStackHeight = tabStackHeight;
      this.buttonStackHeight = buttonStackHeight;
      this.backgroundTexture = backgroundTexture;
      this.scrollerTextures = scrollerTextures;
      this.tabTextures = tabTextures;
      this.useDynamicTitle = useDynamicTitle;
      this.displaySingleTab = displaySingleTab;
      this.allowMultiSelect = allowMultiSelect;
      ((ItemGroupAccessor)this).owo$setEntryCollector((context, entries) -> {
         if (!this.initialized) {
            throw new IllegalStateException("oωo item group not initialized, was 'initialize()' called?");
         } else {
            this.activeTabs.forEach(tabIdx -> {
               this.tabs.get(tabIdx).contentSupplier().addItems(context, entries);
               this.collectItemsFromRegistry(entries, tabIdx);
            });
         }
      });
   }

   public static OwoItemGroup.Builder builder(ResourceLocation id, Supplier<Icon> iconSupplier) {
      return new OwoItemGroup.Builder(id, iconSupplier);
   }

   public void initialize() {
      if (!this.initialized) {
         if (FMLLoader.getDist() == Dist.CLIENT) {
            this.initializer.accept(this);
         }

         if (this.tabs.isEmpty()) {
            this.tabs.add(PLACEHOLDER_TAB);
         }

         if (this.allowMultiSelect) {
            for (int tabIdx = 0; tabIdx < this.tabs.size(); tabIdx++) {
               if (this.tabs.get(tabIdx).primary()) {
                  this.activeTabs.add(tabIdx);
               }
            }

            if (this.activeTabs.isEmpty()) {
               this.activeTabs.add(0);
            }
         } else {
            this.activeTabs.add(0);
         }

         this.initialized = true;
      }
   }

   public void addButton(ItemGroupButton button) {
      this.buttons.add(button);
   }

   public void addTab(Icon icon, String name, @Nullable TagKey<Item> contentTag, ResourceLocation texture, boolean primary) {
      this.tabs
         .add(
            new ItemGroupTab(
               icon,
               OwoItemGroup.ButtonDefinition.tooltipFor(this, "tab", name),
               contentTag == null
                  ? (context, entries) -> {}
                  : (context, entries) -> BuiltInRegistries.ITEM.stream().filter(item -> item.builtInRegistryHolder().is(contentTag)).forEach(entries::accept),
               texture,
               primary
            )
         );
   }

   public void addTab(Icon icon, String name, @Nullable TagKey<Item> contentTag, boolean primary) {
      this.addTab(icon, name, contentTag, ItemGroupTab.DEFAULT_TEXTURE, primary);
   }

   public void addCustomTab(Icon icon, String name, ItemGroupTab.ContentSupplier contentSupplier, ResourceLocation texture, boolean primary) {
      this.tabs.add(new ItemGroupTab(icon, OwoItemGroup.ButtonDefinition.tooltipFor(this, "tab", name), contentSupplier, texture, primary));
   }

   public void addCustomTab(Icon icon, String name, ItemGroupTab.ContentSupplier contentSupplier, boolean primary) {
      this.addCustomTab(icon, name, contentSupplier, ItemGroupTab.DEFAULT_TEXTURE, primary);
   }

   public void buildContents(ItemDisplayParameters context) {
      super.buildContents(context);
      OwoItemGroup.SearchOnlyEntries searchEntries = new OwoItemGroup.SearchOnlyEntries(this, context.enabledFeatures());
      this.collectItemsFromRegistry(searchEntries, -1);
      this.tabs.forEach(tab -> tab.contentSupplier().addItems(context, searchEntries));
      ((ItemGroupAccessor)this).owo$setSearchTabStacks(searchEntries.searchTabContents);
   }

   protected void collectItemsFromRegistry(Output entries, int tab) {
      BuiltInRegistries.ITEM
         .stream()
         .filter(item -> ((OwoItemExtensions)item).owo$group() == this && (tab < 0 || tab == ((OwoItemExtensions)item).owo$tab()))
         .forEach(item -> ((OwoItemExtensions)item).owo$stackGenerator().accept(item, entries));
   }

   public void selectSingleTab(int tab, ItemDisplayParameters context) {
      this.activeTabs.clear();
      this.activeTabs.add(tab);
      this.buildContents(context);
   }

   public void selectTab(int tab, ItemDisplayParameters context) {
      if (!this.allowMultiSelect) {
         this.activeTabs.clear();
      }

      this.activeTabs.add(tab);
      this.buildContents(context);
   }

   public void deselectTab(int tab, ItemDisplayParameters context) {
      if (this.allowMultiSelect) {
         this.activeTabs.remove(tab);
         if (this.activeTabs.isEmpty()) {
            for (int tabIdx = 0; tabIdx < this.tabs.size(); tabIdx++) {
               this.activeTabs.add(tabIdx);
            }
         }

         this.buildContents(context);
      }
   }

   public void toggleTab(int tab, ItemDisplayParameters context) {
      if (this.isTabSelected(tab)) {
         this.deselectTab(tab, context);
      } else {
         this.selectTab(tab, context);
      }
   }

   public IntSet selectedTabs() {
      return this.activeTabsView;
   }

   public boolean isTabSelected(int tab) {
      return this.activeTabs.contains(tab);
   }

   @Nullable
   public ResourceLocation owo$getBackgroundTexture() {
      return this.backgroundTexture;
   }

   @Nullable
   public OwoItemGroup.ScrollerTextures getScrollerTextures() {
      return this.scrollerTextures;
   }

   @Nullable
   public OwoItemGroup.TabTextures getTabTextures() {
      return this.tabTextures;
   }

   public int getTabStackHeight() {
      return this.tabStackHeight;
   }

   public int getButtonStackHeight() {
      return this.buttonStackHeight;
   }

   public boolean hasDynamicTitle() {
      return this.useDynamicTitle && (this.tabs.size() > 1 || this.shouldDisplaySingleTab());
   }

   public boolean shouldDisplaySingleTab() {
      return this.displaySingleTab;
   }

   public boolean canSelectMultipleTabs() {
      return this.allowMultiSelect;
   }

   public List<ItemGroupButton> getButtons() {
      return this.buttons;
   }

   public ItemGroupTab getTab(int index) {
      return index < this.tabs.size() ? this.tabs.get(index) : null;
   }

   public Icon icon() {
      return this.icon == null ? (this.icon = this.iconSupplier.get()) : this.icon;
   }

   public boolean shouldDisplay() {
      return true;
   }

   public ResourceLocation id() {
      return BuiltInRegistries.CREATIVE_MODE_TAB.getKey(this);
   }

   public boolean hasAnyItems() {
      return true;
   }

   public static class Builder {
      private final ResourceLocation id;
      private final Supplier<Icon> iconSupplier;
      private Consumer<OwoItemGroup> initializer = owoItemGroup -> {};
      private int tabStackHeight = 4;
      private int buttonStackHeight = 4;
      @Nullable
      private ResourceLocation backgroundTexture = null;
      @Nullable
      private OwoItemGroup.ScrollerTextures scrollerTextures = null;
      @Nullable
      private OwoItemGroup.TabTextures tabTextures = null;
      private boolean useDynamicTitle = true;
      private boolean displaySingleTab = false;
      private boolean allowMultiSelect = true;

      private Builder(ResourceLocation id, Supplier<Icon> iconSupplier) {
         this.id = id;
         this.iconSupplier = iconSupplier;
      }

      public OwoItemGroup.Builder initializer(Consumer<OwoItemGroup> initializer) {
         this.initializer = initializer;
         return this;
      }

      public OwoItemGroup.Builder tabStackHeight(int tabStackHeight) {
         this.tabStackHeight = tabStackHeight;
         return this;
      }

      public OwoItemGroup.Builder buttonStackHeight(int buttonStackHeight) {
         this.buttonStackHeight = buttonStackHeight;
         return this;
      }

      public OwoItemGroup.Builder backgroundTexture(@Nullable ResourceLocation backgroundTexture) {
         this.backgroundTexture = backgroundTexture;
         return this;
      }

      public OwoItemGroup.Builder scrollerTextures(OwoItemGroup.ScrollerTextures scrollerTextures) {
         this.scrollerTextures = scrollerTextures;
         return this;
      }

      public OwoItemGroup.Builder tabTextures(OwoItemGroup.TabTextures tabTextures) {
         this.tabTextures = tabTextures;
         return this;
      }

      public OwoItemGroup.Builder disableDynamicTitle() {
         this.useDynamicTitle = false;
         return this;
      }

      public OwoItemGroup.Builder displaySingleTab() {
         this.displaySingleTab = true;
         return this;
      }

      public OwoItemGroup.Builder withoutMultipleSelection() {
         this.allowMultiSelect = false;
         return this;
      }

      public OwoItemGroup build() {
         var group = new OwoItemGroup(
            this.id,
            this.initializer,
            this.iconSupplier,
            this.tabStackHeight,
            this.buttonStackHeight,
            this.backgroundTexture,
            this.scrollerTextures,
            this.tabTextures,
            this.useDynamicTitle,
            this.displaySingleTab,
            this.allowMultiSelect
         ) {};
         Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, this.id, group);
         return group;
      }
   }

   public interface ButtonDefinition {
      Icon icon();

      ResourceLocation texture();

      Component tooltip();

      static Component tooltipFor(CreativeModeTab group, String component, String componentName) {
         ResourceLocation registryId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group);
         String groupId = registryId.getNamespace().equals("minecraft") ? registryId.getPath() : registryId.getNamespace() + "." + registryId.getPath();
         return Component.translatable("itemGroup." + groupId + "." + component + "." + componentName);
      }
   }

   public record ScrollerTextures(ResourceLocation enabled, ResourceLocation disabled) {
   }

   protected static class SearchOnlyEntries extends ItemDisplayBuilder {
      public SearchOnlyEntries(CreativeModeTab group, FeatureFlagSet enabledFeatures) {
         super(group, enabledFeatures);
      }

      public void accept(ItemStack stack, TabVisibility visibility) {
         if (visibility != TabVisibility.PARENT_TAB_ONLY) {
            super.accept(stack, TabVisibility.SEARCH_TAB_ONLY);
         }
      }
   }

   public record TabTextures(
      ResourceLocation topSelected,
      ResourceLocation topSelectedFirstColumn,
      ResourceLocation topUnselected,
      ResourceLocation bottomSelected,
      ResourceLocation bottomSelectedFirstColumn,
      ResourceLocation bottomUnselected
   ) {
   }
}
