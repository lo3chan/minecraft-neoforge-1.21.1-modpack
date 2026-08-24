package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRecipeProvider extends RecipeProvider {
   public static final Map<BlockSetVariant, AbstractRecipeProvider.FamilyRecipeProvider> VARIANT_STONE_PROVIDERS = ImmutableMap.builder()
      .put(BlockSetVariant.CHISELED, AbstractRecipeProvider.FamilyRecipeProvider.stonecutting())
      .put(BlockSetVariant.CUT, AbstractRecipeProvider.FamilyRecipeProvider.stonecutting())
      .put(BlockSetVariant.SLAB, AbstractRecipeProvider.FamilyRecipeProvider.stonecutting(2))
      .put(BlockSetVariant.STAIRS, AbstractRecipeProvider.FamilyRecipeProvider.stonecutting())
      .put(BlockSetVariant.POLISHED, AbstractRecipeProvider.FamilyRecipeProvider.stonecutting())
      .put(BlockSetVariant.WALL, AbstractRecipeProvider.FamilyRecipeProvider.stonecutting())
      .build();
   protected final String modId;

   public AbstractRecipeProvider(DataProviderContext context) {
      this(context.getModId(), context.getPackOutput(), context.getRegistries());
   }

   public AbstractRecipeProvider(String modId, PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
      super(packOutput, lookupProvider);
      this.modId = modId;
   }

   @Nullable
   protected static <T> JsonElement searchAndReplaceValue(@Nullable JsonElement jsonElement, T searchFor, T replaceWith) {
      Objects.requireNonNull(searchFor, "search for is null");
      Objects.requireNonNull(replaceWith, "replace with is null");
      if (jsonElement != null && !jsonElement.isJsonNull()) {
         if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            if (jsonPrimitive.isNumber()) {
               if (searchFor.equals(jsonPrimitive.getAsNumber())) {
                  return new JsonPrimitive((Number)replaceWith);
               }
            } else if (jsonPrimitive.isBoolean()) {
               if (searchFor.equals(jsonPrimitive.getAsBoolean())) {
                  return new JsonPrimitive((Boolean)replaceWith);
               }
            } else if (jsonPrimitive.isString() && searchFor.toString().equals(jsonPrimitive.getAsString())) {
               return new JsonPrimitive(replaceWith.toString());
            }

            return jsonElement;
         }

         if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
               jsonArray.set(i, searchAndReplaceValue(jsonArray.get(i), searchFor, replaceWith));
            }
         } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
               entry.setValue(searchAndReplaceValue(entry.getValue(), searchFor, replaceWith));
            }
         }
      }

      return jsonElement;
   }

   public static Map<BlockSetVariant, AbstractRecipeProvider.FamilyRecipeProvider> createVariantWoodProviders(
      BlockSetFamily blockSetFamily, Block strippedBlock
   ) {
      return ImmutableMap.builder()
         .put(
            BlockSetVariant.HANGING_SIGN,
            (AbstractRecipeProvider.FamilyRecipeProvider)(recipeOutput, result, input, recipeGroupPrefix, recipeUnlockedBy) -> hangingSign(
               recipeOutput, result, strippedBlock
            )
         )
         .put(
            BlockSetVariant.BOAT,
            (AbstractRecipeProvider.FamilyRecipeProvider)(recipeOutput, result, input, recipeGroupPrefix, recipeUnlockedBy) -> woodenBoat(
               recipeOutput, result, input
            )
         )
         .put(BlockSetVariant.CHEST_BOAT, (AbstractRecipeProvider.FamilyRecipeProvider)(recipeOutput, result, input, recipeGroupPrefix, recipeUnlockedBy) -> {
            Reference<Item> boatItem = blockSetFamily.getItem(BlockSetVariant.BOAT);
            Objects.requireNonNull(boatItem, "boat item is null");
            chestBoat(recipeOutput, result, (ItemLike)boatItem.value());
         })
         .build();
   }

   @Deprecated
   public static String getItemName(Ingredient ingredient) {
      return getItemName(Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toArray(ItemLike[]::new));
   }

   @Deprecated
   public static String getItemName(ItemLike... items) {
      Preconditions.checkState(items.length > 0, "items is empty");
      return Arrays.stream(items).<CharSequence>map(RecipeProvider::getItemName).collect(Collectors.joining("_or_"));
   }

   @Deprecated
   public static String getConversionRecipeName(ItemLike result, Ingredient ingredient) {
      return getConversionRecipeName(result, Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toArray(ItemLike[]::new));
   }

   @Deprecated
   public static String getConversionRecipeName(ItemLike result, ItemLike... items) {
      Preconditions.checkState(items.length > 0, "items is empty");
      return getItemName(result) + "_from_" + getItemName(items);
   }

   @Deprecated
   public static String getHasName(Ingredient ingredient) {
      return getHasName(Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toArray(ItemLike[]::new));
   }

   @Deprecated
   public static String getHasName(ItemLike... items) {
      Preconditions.checkState(items.length > 0, "items is empty");
      return "has_" + getItemName(items);
   }

   @Deprecated
   public static Criterion<TriggerInstance> has(Ingredient ingredient) {
      return has(Arrays.stream(ingredient.getItems()).map(ItemStack::getItem).toArray(ItemLike[]::new));
   }

   @Deprecated
   public static Criterion<TriggerInstance> has(ItemLike... items) {
      Preconditions.checkState(items.length > 0, "items is empty");
      return inventoryTrigger(new ItemPredicate[]{Builder.item().of(items).build()});
   }

   @Deprecated
   public static void stonecutterResultFromBase(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, Ingredient material) {
      stonecutterResultFromBase(recipeOutput, category, result, material, 1);
   }

   @Deprecated
   public static void stonecutterResultFromBase(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, Ingredient material, int resultCount) {
      SingleItemRecipeBuilder.stonecutting(material, category, result, resultCount)
         .unlockedBy(getHasName(material), has(material))
         .save(recipeOutput, getConversionRecipeName(result, material) + "_stonecutting");
   }

   public void generateFor(RecipeOutput recipeOutput, BlockSetFamily blockSetFamily, Map<BlockSetVariant, AbstractRecipeProvider.FamilyRecipeProvider> variants) {
      BlockFamily blockFamily = blockSetFamily.getBlockFamily();
      generateRecipes(recipeOutput, blockFamily, FeatureFlags.DEFAULT_FLAGS);
      if (blockFamily.shouldGenerateRecipe()) {
         blockSetFamily.getItemVariants().forEach((variant, holder) -> {
            AbstractRecipeProvider.FamilyRecipeProvider recipeProvider = variants.get(variant);
            if (recipeProvider != null) {
               ItemLike baseBlock;
               if (variant.toVanilla() != null) {
                  baseBlock = getBaseBlock(blockFamily, variant.toVanilla());
               } else {
                  baseBlock = (ItemLike)blockSetFamily.getBaseBlock().value();
               }

               recipeProvider.create(recipeOutput, (ItemLike)holder.value(), baseBlock, blockFamily.getRecipeGroupPrefix(), blockFamily.getRecipeUnlockedBy());
            }
         });
      }
   }

   public void stair(RecipeOutput output, RecipeCategory recipeCategory, ItemLike resultItem, ItemLike ingredientItem) {
      this.stairBuilder(recipeCategory, resultItem, Ingredient.of(new ItemLike[]{ingredientItem}))
         .unlockedBy(getHasName(ingredientItem), has(ingredientItem))
         .save(output);
   }

   public RecipeBuilder stairBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
      return ShapedRecipeBuilder.shaped(recipeCategory, resultItem, 4).define('#', ingredient).pattern("#  ").pattern("## ").pattern("###");
   }

   public void metalCooking(RecipeOutput output, ItemLike resultItem, ItemLike ingredientItem, float experience) {
      this.metalCooking(output, resultItem, ingredientItem, experience, 200);
   }

   public void metalCooking(RecipeOutput output, ItemLike resultItem, ItemLike ingredientItem, float experience, int baseCookingTime) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{ingredientItem}), RecipeCategory.MISC, resultItem, experience, baseCookingTime)
         .unlockedBy(getHasName(ingredientItem), has(ingredientItem))
         .save(output);
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(new ItemLike[]{ingredientItem}), RecipeCategory.MISC, resultItem, experience, baseCookingTime / 2)
         .unlockedBy(getHasName(ingredientItem), has(ingredientItem))
         .save(output, getBlastingRecipeName(resultItem));
   }

   public void foodCooking(RecipeOutput output, ItemLike resultItem, ItemLike ingredientItem) {
      this.foodCooking(output, resultItem, ingredientItem, 0.35F, 200);
   }

   public void foodCooking(RecipeOutput output, ItemLike resultItem, ItemLike ingredientItem, float experienceReward, int baseCookingTime) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(new ItemLike[]{ingredientItem}), RecipeCategory.FOOD, resultItem, experienceReward, baseCookingTime)
         .unlockedBy(getHasName(ingredientItem), has(ingredientItem))
         .save(output);
      SimpleCookingRecipeBuilder.smoking(Ingredient.of(new ItemLike[]{ingredientItem}), RecipeCategory.FOOD, resultItem, experienceReward, baseCookingTime / 2)
         .unlockedBy(getHasName(ingredientItem), has(ingredientItem))
         .save(output, getCraftingMethodRecipeName(resultItem, RecipeSerializer.SMOKING_RECIPE));
      SimpleCookingRecipeBuilder.campfireCooking(
            Ingredient.of(new ItemLike[]{ingredientItem}), RecipeCategory.FOOD, resultItem, experienceReward, baseCookingTime * 3
         )
         .unlockedBy(getHasName(ingredientItem), has(ingredientItem))
         .save(output, getCraftingMethodRecipeName(resultItem, RecipeSerializer.CAMPFIRE_COOKING_RECIPE));
   }

   public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient) {
      return this.stonecutterResultFromBaseBuilder(recipeCategory, resultItem, ingredient, 1);
   }

   public RecipeBuilder stonecutterResultFromBaseBuilder(RecipeCategory recipeCategory, ItemLike resultItem, Ingredient ingredient, int count) {
      return SingleItemRecipeBuilder.stonecutting(ingredient, recipeCategory, resultItem, count);
   }

   public void smithing(
      RecipeOutput output, RecipeCategory recipeCategory, ItemLike resultItem, ItemLike templateItem, ItemLike baseItem, ItemLike materialItem
   ) {
      SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(new ItemLike[]{templateItem}),
            Ingredient.of(new ItemLike[]{baseItem}),
            Ingredient.of(new ItemLike[]{materialItem}),
            recipeCategory,
            resultItem.asItem()
         )
         .unlocks(getHasName(materialItem), has(materialItem))
         .save(output, getSmithingRecipeName(resultItem));
   }

   public void waxing(RecipeOutput output, ItemLike resultItem, ItemLike ingredientItem) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, resultItem)
         .requires(ingredientItem)
         .requires(Items.HONEYCOMB)
         .group(getItemName(resultItem))
         .unlockedBy(getHasName(ingredientItem), has(ingredientItem))
         .save(output, getConversionRecipeName(resultItem, Items.HONEYCOMB));
   }

   public static String getCraftingMethodRecipeName(ItemLike resultItem, RecipeSerializer<?> recipeSerializer) {
      ResourceLocation identifier = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
      Objects.requireNonNull(identifier, "identifier is null");
      return getCraftingMethodRecipeName(resultItem, identifier.getPath());
   }

   public static String getCraftingMethodRecipeName(ItemLike resultItem, String craftingMethod) {
      return getItemName(resultItem) + "_from_" + craftingMethod;
   }

   public static String getStonecuttingRecipeName(ItemLike resultItem, ItemLike material) {
      return getConversionRecipeName(resultItem, material) + "_stonecutting";
   }

   public static String getSmithingRecipeName(ItemLike resultItem) {
      return getItemName(resultItem) + "_smithing";
   }

   public static String getHasName(TagKey<Item> tagKey) {
      return "has_" + tagKey.location().getPath();
   }

   public CompletableFuture<?> run(CachedOutput output, Provider registries) {
      List<CompletableFuture<?>> completableFutures = new ArrayList<>();
      this.buildRecipes(new AbstractRecipeProvider.IdentifiableRecipeOutput(output, registries, completableFutures));
      return CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new));
   }

   public final void buildRecipes(RecipeOutput recipeOutput) {
      this.addRecipes(recipeOutput);
   }

   public abstract void addRecipes(RecipeOutput var1);

   @FunctionalInterface
   public interface FamilyRecipeProvider {
      void create(RecipeOutput var1, ItemLike var2, ItemLike var3, Optional<String> var4, Optional<String> var5);

      static AbstractRecipeProvider.FamilyRecipeProvider stonecutting() {
         return stonecutting(1);
      }

      static AbstractRecipeProvider.FamilyRecipeProvider stonecutting(int count) {
         return (recipeOutput, result, input, recipeGroupPrefix, recipeUnlockedBy) -> {
            SingleItemRecipeBuilder recipeBuilder = SingleItemRecipeBuilder.stonecutting(
               Ingredient.of(new ItemLike[]{input}), RecipeCategory.BUILDING_BLOCKS, result, count
            );
            recipeBuilder.unlockedBy(recipeUnlockedBy.orElseGet(() -> RecipeProvider.getHasName(input)), RecipeProvider.has(input));
            recipeBuilder.save(recipeOutput, AbstractRecipeProvider.getStonecuttingRecipeName(result, input));
         };
      }
   }

   public class IdentifiableRecipeOutput implements RecipeOutput {
      private final CachedOutput output;
      private final Provider registries;
      private final List<CompletableFuture<?>> completableFutures;
      private final Set<ResourceLocation> generatedRecipes = new HashSet<>();

      public IdentifiableRecipeOutput(CachedOutput output, Provider registries, List<CompletableFuture<?>> completableFutures) {
         this.output = output;
         this.registries = registries;
         this.completableFutures = completableFutures;
      }

      public String getModId() {
         return AbstractRecipeProvider.this.modId;
      }

      public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
         location = ResourceLocationHelper.fromNamespaceAndPath(AbstractRecipeProvider.this.modId, location.getPath());
         if (!this.generatedRecipes.add(location)) {
            throw new IllegalStateException("Duplicate recipe " + location);
         } else {
            this.completableFutures
               .add(DataProvider.saveStable(this.output, this.registries, Recipe.CODEC, recipe, AbstractRecipeProvider.this.recipePathProvider.json(location)));
            if (advancement != null) {
               RegistryOps<JsonElement> registryOps = this.registries.createSerializationContext(JsonOps.INSTANCE);
               JsonElement jsonElement = (JsonElement)Advancement.CODEC.encodeStart(registryOps, advancement.value()).getOrThrow();
               jsonElement = AbstractRecipeProvider.searchAndReplaceValue(jsonElement, location, location);
               ResourceLocation advancementLocation = ResourceLocationHelper.fromNamespaceAndPath(AbstractRecipeProvider.this.modId, advancement.id().getPath());
               this.completableFutures
                  .add(DataProvider.saveStable(this.output, jsonElement, AbstractRecipeProvider.this.advancementPathProvider.json(advancementLocation)));
            }
         }
      }

      public net.minecraft.advancements.Advancement.Builder advancement() {
         return net.minecraft.advancements.Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
      }
   }
}
