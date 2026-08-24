package com.alonie.brbe;

import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.compat.emi.EmiCompat;
import com.alonie.brbe.compat.rei.ReiCompat;
import com.alonie.brbe.config.AppContext;
import com.alonie.brbe.config.Config;
import com.alonie.brbe.config.ConfigEventBus;
import com.alonie.brbe.loaders.PotionLoader;
import com.alonie.brbe.pin.JsonPinStore;
import com.alonie.brbe.util.BRBHelper;
import com.alonie.brbe.util.BrbeDiagnostic;
import com.alonie.brbe.util.BrbeLogger;
import com.alonie.brbe.util.CollectionCategory;
import com.alonie.brbe.util.PartialCraftingUtil;
import com.alonie.brbe.util.RecipeUnlockUtil;
import com.alonie.recipebookispain_extended.RecipeBookIsPain;
import com.mojang.blaze3d.platform.InputConstants.Type;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterRecipeBook {
   public static final String MOD_ID = "brbe";
   private static int queuedScroll;
   public static Config config;
   public static ConfigHolder<Config> configHolder;
   public static PinnedRecipeManager pinnedRecipeManager;
   public static InstantCraftingManager instantCraftingManager;
   public static final Logger LOGGER = LogManager.getLogger("brbe");
   public static final KeyMapping PIN_MAPPING = new KeyMapping("key.brbe.pin", Type.KEYSYM, 70, "category.brbe");
   public static final KeyMapping DIAGNOSTIC_MAPPING = new KeyMapping("key.brbe.diagnostic", Type.KEYSYM, 297, "category.brbe");
   public static BRBHelper.Book BREWING = BRBHelper.createBook("brbe", "brewing_stand");
   public static BRBHelper.Book SMITHING = BRBHelper.createBook("brbe", "smithing_table");
   public static BRBBookCategories.Category BREWING_POTION = BREWING.createCategory(new ItemStack(Items.POTION));
   public static BRBBookCategories.Category BREWING_SPLASH_POTION = BREWING.createCategory(new ItemStack(Items.SPLASH_POTION));
   public static BRBBookCategories.Category BREWING_LINGERING_POTION = BREWING.createCategory(new ItemStack(Items.LINGERING_POTION));
   public static BRBBookCategories.Category SMITHING_SEARCH = SMITHING.createSearch();
   public static BRBBookCategories.Category SMITHING_TRANSFORM = SMITHING.createCategory(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
   public static BRBBookCategories.Category SMITHING_TRIM = SMITHING.createCategory(new ItemStack(Items.NETHERITE_CHESTPLATE));
   private static AppContext appContext;

   public static int getQueuedScroll() {
      return queuedScroll;
   }

   public static void setQueuedScroll(int value) {
      queuedScroll = value;
   }

   public static void addQueuedScroll(int delta) {
      queuedScroll += delta;
   }

   public static AppContext ctx() {
      return appContext;
   }

   public static void init() {
      Class<CollectionCategory> _cc = CollectionCategory.class;
      BrbeLogger.init(Minecraft.getInstance().gameDirectory.toPath());
      PotionLoader.init();
      ReiCompat.register();
      EmiCompat.register();
      queuedScroll = 0;
      appContext = AppContext.create();
      config = appContext.config();
      configHolder = appContext.configHolder();
      pinnedRecipeManager = appContext.pins();
      instantCraftingManager = appContext.instantCraft();
      appContext.events().subscribe(ConfigEventBus.PartialCraftingChanged.class, event -> PartialCraftingUtil.invalidateCaches());
      appContext.events().subscribe(ConfigEventBus.ConfigChanged.class, event -> {
         config = event.config();
         appContext.events().requestConfigRefresh();
         RecipeUnlockUtil.syncToConfig();
         BrbeDiagnostic.dump();
      });
      RecipeBookIsPain.init(appContext.events());
      JsonPinStore pinStore = new JsonPinStore(Minecraft.getInstance().gameDirectory.toPath());
      pinnedRecipeManager.setStore(pinStore);
      pinnedRecipeManager.read();
      BrbeDiagnostic.dump();
   }
}
