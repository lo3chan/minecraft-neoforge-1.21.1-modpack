package com.alonie.brbe.config;

import com.alonie.brbe.InstantCraftingManager;
import com.alonie.brbe.PinnedRecipeManager;
import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.compat.recipeviewer.RecipeViewerRegistry;
import com.alonie.brbe.layout.BookLayout;
import com.alonie.brbe.util.BRBHelper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class AppContext {
   private static volatile AppContext INSTANCE;
   private final Config config;
   private final ConfigHolder<Config> configHolder;
   private final ConfigEventBus events;
   private final PinnedRecipeManager pinnedRecipeManager;
   private final InstantCraftingManager instantCraftingManager;
   private final BookLayout bookLayout;
   private final RecipeViewerRegistry recipeViewers;
   private final BRBHelper.Book brewing;
   private final BRBHelper.Book smithing;
   private final BRBBookCategories.Category brewingPotion;
   private final BRBBookCategories.Category brewingSplashPotion;
   private final BRBBookCategories.Category brewingLingeringPotion;
   private final BRBBookCategories.Category smithingSearch;
   private final BRBBookCategories.Category smithingTransform;
   private final BRBBookCategories.Category smithingTrim;

   private AppContext() {
      AutoConfig.register(Config.class, Toml4jConfigSerializer::new);
      this.configHolder = AutoConfig.getConfigHolder(Config.class);
      this.config = (Config)this.configHolder.getConfig();
      configHolder = this.configHolder;
      config = this.config;
      this.events = new ConfigEventBus();
      this.brewing = BRBHelper.createBook("brbe", "brewing_stand");
      this.smithing = BRBHelper.createBook("brbe", "smithing_table");
      this.brewingPotion = this.brewing.createCategory(new ItemStack(Items.POTION));
      this.brewingSplashPotion = this.brewing.createCategory(new ItemStack(Items.SPLASH_POTION));
      this.brewingLingeringPotion = this.brewing.createCategory(new ItemStack(Items.LINGERING_POTION));
      this.smithingSearch = this.smithing.createSearch();
      this.smithingTransform = this.smithing.createCategory(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
      this.smithingTrim = this.smithing.createCategory(new ItemStack(Items.NETHERITE_CHESTPLATE));
      this.bookLayout = new BookLayout();
      this.recipeViewers = new RecipeViewerRegistry();
      this.pinnedRecipeManager = new PinnedRecipeManager();
      this.instantCraftingManager = new InstantCraftingManager();
      this.configHolder.registerSaveListener((holder, cfg) -> {
         this.events.publish(new ConfigEventBus.ConfigChanged(cfg));
         this.events.publish(new ConfigEventBus.PartialCraftingChanged(cfg.partialCraftingEnabled, cfg.partialMarkingEnabled));
         this.events.publish(new ConfigEventBus.PinningChanged(true));
         this.events.publish(new ConfigEventBus.BookVisibilityChanged(cfg.enableBook));
         return InteractionResult.SUCCESS;
      });
      INSTANCE = this;
   }

   public static AppContext create() {
      if (INSTANCE != null) {
         throw new IllegalStateException("AppContext already created");
      } else {
         return new AppContext();
      }
   }

   public static AppContext instance() {
      if (INSTANCE == null) {
         throw new IllegalStateException("AppContext not yet created — call create() first");
      } else {
         return INSTANCE;
      }
   }

   public Config config() {
      return this.config;
   }

   public ConfigHolder<Config> configHolder() {
      return this.configHolder;
   }

   public ConfigEventBus events() {
      return this.events;
   }

   public PinnedRecipeManager pins() {
      return this.pinnedRecipeManager;
   }

   public InstantCraftingManager instantCraft() {
      return this.instantCraftingManager;
   }

   public BookLayout bookLayout() {
      return this.bookLayout;
   }

   public RecipeViewerRegistry recipeViewers() {
      return this.recipeViewers;
   }

   public BRBHelper.Book brewingBook() {
      return this.brewing;
   }

   public BRBHelper.Book smithingBook() {
      return this.smithing;
   }

   public BRBBookCategories.Category brewingPotion() {
      return this.brewingPotion;
   }

   public BRBBookCategories.Category brewingSplashPotion() {
      return this.brewingSplashPotion;
   }

   public BRBBookCategories.Category brewingLingeringPotion() {
      return this.brewingLingeringPotion;
   }

   public BRBBookCategories.Category smithingSearch() {
      return this.smithingSearch;
   }

   public BRBBookCategories.Category smithingTransform() {
      return this.smithingTransform;
   }

   public BRBBookCategories.Category smithingTrim() {
      return this.smithingTrim;
   }
}
