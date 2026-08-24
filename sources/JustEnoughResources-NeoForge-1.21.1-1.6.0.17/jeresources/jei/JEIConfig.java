package jeresources.jei;

import java.util.HashMap;
import java.util.Map;
import jeresources.config.Settings;
import jeresources.entry.AbstractVillagerEntry;
import jeresources.entry.DungeonEntry;
import jeresources.entry.MobEntry;
import jeresources.entry.PlantEntry;
import jeresources.entry.WorldGenEntry;
import jeresources.jei.dungeon.DungeonCategory;
import jeresources.jei.enchantment.EnchantmentCategory;
import jeresources.jei.enchantment.EnchantmentMaker;
import jeresources.jei.enchantment.EnchantmentWrapper;
import jeresources.jei.mob.MobCategory;
import jeresources.jei.plant.PlantCategory;
import jeresources.jei.villager.VillagerCategory;
import jeresources.jei.worldgen.WorldGenCategory;
import jeresources.platform.Services;
import jeresources.registry.DungeonRegistry;
import jeresources.registry.MobRegistry;
import jeresources.registry.PlantRegistry;
import jeresources.registry.VillagerRegistry;
import jeresources.registry.WorldGenRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIConfig implements IModPlugin {
   public static final ResourceLocation MOB = ResourceLocation.fromNamespaceAndPath("jeresources", "mob");
   public static final RecipeType<MobEntry> MOB_TYPE = new RecipeType(MOB, MobEntry.class);
   public static final ResourceLocation DUNGEON = ResourceLocation.fromNamespaceAndPath("jeresources", "dungeon");
   public static final RecipeType<DungeonEntry> DUNGEON_TYPE = new RecipeType(DUNGEON, DungeonEntry.class);
   public static final ResourceLocation WORLD_GEN = ResourceLocation.fromNamespaceAndPath("jeresources", "worldgen");
   public static final RecipeType<WorldGenEntry> WORLD_GEN_TYPE = new RecipeType(WORLD_GEN, WorldGenEntry.class);
   public static final ResourceLocation PLANT = ResourceLocation.fromNamespaceAndPath("jeresources", "plant");
   public static final RecipeType<PlantEntry> PLANT_TYPE = new RecipeType(PLANT, PlantEntry.class);
   public static final ResourceLocation ENCHANTMENT = ResourceLocation.fromNamespaceAndPath("jeresources", "enchantment");
   public static final RecipeType<EnchantmentWrapper> ENCHANTMENT_TYPE = new RecipeType(ENCHANTMENT, EnchantmentWrapper.class);
   public static final ResourceLocation VILLAGER = ResourceLocation.fromNamespaceAndPath("jeresources", "villager");
   public static final RecipeType<AbstractVillagerEntry> VILLAGER_TYPE = new RecipeType(VILLAGER, AbstractVillagerEntry.class);
   public static final Map<ResourceLocation, RecipeType<?>> TYPES = new HashMap<>();
   private static IJeiHelpers jeiHelpers;
   private static IJeiRuntime jeiRuntime;

   @NotNull
   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("jeresources", "minecraft");
   }

   public void registerRecipes(IRecipeRegistration registration) {
      registration.addRecipes(DUNGEON_TYPE, DungeonRegistry.getInstance().getDungeons());
      registration.addRecipes(ENCHANTMENT_TYPE, EnchantmentMaker.createRecipes(registration.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK)));
      registration.addRecipes(MOB_TYPE, MobRegistry.getInstance().getMobs());
      registration.addRecipes(PLANT_TYPE, PlantRegistry.getInstance().getAllPlants());
      registration.addRecipes(VILLAGER_TYPE, VillagerRegistry.getInstance().getVillagers());
      registration.addRecipes(WORLD_GEN_TYPE, WorldGenRegistry.getInstance().getWorldGen());
   }

   public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
      JEIConfig.jeiRuntime = jeiRuntime;
      hideCategories(Settings.hiddenCategories);
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      jeiHelpers = registration.getJeiHelpers();
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new DungeonCategory(), new EnchantmentCategory(), new MobCategory(), new PlantCategory(), new VillagerCategory(), new WorldGenCategory()
         }
      );
      Services.PLATFORM.getProxy().initCompatibility();
   }

   public static void resetCategories() {
      if (jeiRuntime != null) {
         for (RecipeType<?> recipeType : TYPES.values()) {
            jeiRuntime.getRecipeManager().unhideRecipeCategory(recipeType);
         }
      }
   }

   public static void hideCategories(String[] categories) {
      if (jeiRuntime != null) {
         for (String category : categories) {
            jeiRuntime.getRecipeManager().hideRecipeCategory(TYPES.get(ResourceLocation.fromNamespaceAndPath("jeresources", category)));
         }
      }
   }

   public static IJeiHelpers getJeiHelpers() {
      return jeiHelpers;
   }

   static {
      TYPES.put(MOB, MOB_TYPE);
      TYPES.put(DUNGEON, DUNGEON_TYPE);
      TYPES.put(WORLD_GEN, WORLD_GEN_TYPE);
      TYPES.put(PLANT, PLANT_TYPE);
      TYPES.put(ENCHANTMENT, ENCHANTMENT_TYPE);
      TYPES.put(VILLAGER, VILLAGER_TYPE);
   }
}
