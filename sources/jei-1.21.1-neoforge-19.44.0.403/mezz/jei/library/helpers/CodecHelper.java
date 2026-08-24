package mezz.jei.library.helpers;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.ingredients.IIngredientSupplier;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.codecs.EnumCodec;
import mezz.jei.common.codecs.TupleCodec;
import mezz.jei.common.codecs.TypedIngredientCodecs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

public class CodecHelper implements ICodecHelper {
   private static final Codec<RecipeHolder<?>> RECIPE_HOLDER_CODEC = Codec.lazyInitialized(
      () -> {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;

         assert level != null;

         RecipeManager recipeManager = level.getRecipeManager();
         return Codec.either(ResourceLocation.CODEC, TupleCodec.of(ResourceLocation.CODEC, Recipe.CODEC))
            .flatXmap(
               either -> (DataResult)either.map(
                  recipeHolderId -> recipeManager.byKey(recipeHolderId)
                     .<DataResult>map(DataResult::success)
                     .orElseGet(() -> DataResult.error(() -> "Could not find recipe for key: " + recipeHolderId)),
                  pair -> {
                     ResourceLocation recipeHolderId = (ResourceLocation)pair.getFirst();
                     Recipe<?> recipe = (Recipe<?>)pair.getSecond();
                     if (recipe == null) {
                        return DataResult.error(() -> "Could not find recipe for key: " + recipeHolderId);
                     } else {
                        RecipeHolder<?> recipeHolder = new RecipeHolder(recipeHolderId, recipe);
                        return DataResult.success(recipeHolder);
                     }
                  }
               ),
               recipeHolder -> {
                  ResourceLocation recipeHolderId = recipeHolder.id();
                  Optional<RecipeHolder<?>> found = recipeManager.byKey(recipeHolderId);
                  if (found.isPresent() && found.get().equals(recipeHolder)) {
                     return DataResult.success(Either.left(recipeHolderId));
                  } else {
                     Recipe<?> recipe = recipeHolder.value();
                     return DataResult.success(Either.right(Pair.of(recipeHolderId, recipe)));
                  }
               }
            );
      }
   );
   private final IIngredientManager ingredientManager;
   private final IFocusFactory focusFactory;
   private final Map<RecipeType<?>, Codec<?>> defaultRecipeCodecs = new HashMap<>();
   @Nullable
   private Codec<RecipeType<?>> recipeTypeCodec;

   public CodecHelper(IIngredientManager ingredientManager, IFocusFactory focusFactory) {
      this.ingredientManager = ingredientManager;
      this.focusFactory = focusFactory;
   }

   @Override
   public Codec<IIngredientType<?>> getIngredientTypeCodec() {
      return TypedIngredientCodecs.getIngredientTypeCodec(this.ingredientManager);
   }

   @Override
   public MapCodec<ITypedIngredient<?>> getTypedIngredientCodec() {
      return TypedIngredientCodecs.getIngredientCodec(this.ingredientManager);
   }

   @Override
   public <T> Codec<ITypedIngredient<T>> getTypedIngredientCodec(IIngredientType<T> ingredientType) {
      return TypedIngredientCodecs.getIngredientCodec(ingredientType, this.ingredientManager);
   }

   @Override
   public <T extends RecipeHolder<?>> Codec<T> getRecipeHolderCodec() {
      return RECIPE_HOLDER_CODEC;
   }

   @Override
   public <T> Codec<T> getSlowRecipeCategoryCodec(IRecipeCategory<T> recipeCategory, IRecipeManager recipeManager) {
      RecipeType<T> recipeType = recipeCategory.getRecipeType();
      Codec<T> codec = (Codec<T>)this.defaultRecipeCodecs.get(recipeType);
      if (codec == null) {
         codec = this.createDefaultRecipeCategoryCodec(recipeManager, recipeCategory);
         this.defaultRecipeCodecs.put(recipeType, codec);
      }

      return codec;
   }

   private <T> Codec<T> createDefaultRecipeCategoryCodec(IRecipeManager recipeManager, IRecipeCategory<T> recipeCategory) {
      Codec<CodecHelper.Data> dataCodec = RecordCodecBuilder.create(
         builder -> builder.group(
               ResourceLocation.CODEC.fieldOf("registryName").forGetter(CodecHelper.Data::registryName),
               this.getTypedIngredientCodec().codec().fieldOf("ingredient").forGetter(CodecHelper.Data::ingredient),
               EnumCodec.create(RecipeIngredientRole.class).fieldOf("ingredient_role").forGetter(CodecHelper.Data::ingredientRole)
            )
            .apply(builder, CodecHelper.Data::new)
      );
      Codec<T> codec = dataCodec.flatXmap(
         data -> {
            ResourceLocation registryName = data.registryName();
            ITypedIngredient<?> ingredient = data.ingredient();
            IFocus<?> focus = this.focusFactory.createFocus(data.ingredientRole(), ingredient);
            RecipeType<T> recipeType = recipeCategory.getRecipeType();
            return recipeManager.createRecipeLookup(recipeType)
               .limitFocus(List.of(focus))
               .get()
               .filter(recipe -> registryName.equals(recipeCategory.getRegistryName((T)recipe)))
               .findFirst()
               .<DataResult>map(DataResult::success)
               .orElseGet(() -> DataResult.error(() -> "No recipe found for registry name: " + registryName));
         },
         recipe -> {
            ResourceLocation registryName = recipeCategory.getRegistryName((T)recipe);
            if (registryName == null) {
               return DataResult.error(() -> "No registry name for recipe");
            } else {
               IIngredientSupplier ingredients = recipeManager.getRecipeIngredients(recipeCategory, (T)recipe);
               List<ITypedIngredient<?>> outputs = ingredients.getIngredients(RecipeIngredientRole.OUTPUT);
               if (!outputs.isEmpty()) {
                  CodecHelper.Data result = new CodecHelper.Data(registryName, (ITypedIngredient<?>)outputs.getFirst(), RecipeIngredientRole.OUTPUT);
                  return DataResult.success(result);
               } else {
                  List<ITypedIngredient<?>> inputs = ingredients.getIngredients(RecipeIngredientRole.INPUT);
                  if (!inputs.isEmpty()) {
                     CodecHelper.Data result = new CodecHelper.Data(registryName, (ITypedIngredient<?>)inputs.getFirst(), RecipeIngredientRole.INPUT);
                     return DataResult.success(result);
                  } else {
                     return DataResult.error(() -> "No inputs or outputs for recipe");
                  }
               }
            }
         }
      );
      return Codec.withAlternative(codec, this.createLegacyDefaultRecipeCategoryCodec(recipeManager, recipeCategory));
   }

   private <T> Codec<T> createLegacyDefaultRecipeCategoryCodec(IRecipeManager recipeManager, IRecipeCategory<T> recipeCategory) {
      Codec<Pair<ResourceLocation, ITypedIngredient<?>>> legacyPairCodec = RecordCodecBuilder.create(
         builder -> builder.group(
               ResourceLocation.CODEC.fieldOf("registryName").forGetter(Pair::getFirst),
               this.getTypedIngredientCodec().codec().fieldOf("output").forGetter(Pair::getSecond)
            )
            .apply(builder, Pair::new)
      );
      Codec<Pair<ResourceLocation, ITypedIngredient<?>>> tupleCodec = TupleCodec.of(ResourceLocation.CODEC, this.getTypedIngredientCodec().codec());
      return Codec.withAlternative(tupleCodec, legacyPairCodec)
         .flatXmap(
            pair -> {
               ResourceLocation registryName = (ResourceLocation)pair.getFirst();
               ITypedIngredient<?> output = (ITypedIngredient<?>)pair.getSecond();
               IFocus<?> focus = this.focusFactory.createFocus(RecipeIngredientRole.OUTPUT, output);
               RecipeType<T> recipeType = recipeCategory.getRecipeType();
               return recipeManager.createRecipeLookup(recipeType)
                  .limitFocus(List.of(focus))
                  .get()
                  .filter(recipe -> registryName.equals(recipeCategory.getRegistryName((T)recipe)))
                  .findFirst()
                  .<DataResult>map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "No recipe found for registry name: " + registryName));
            },
            recipe -> {
               ResourceLocation registryName = recipeCategory.getRegistryName((T)recipe);
               if (registryName == null) {
                  return DataResult.error(() -> "No registry name for recipe");
               } else {
                  IIngredientSupplier ingredients = recipeManager.getRecipeIngredients(recipeCategory, (T)recipe);
                  List<ITypedIngredient<?>> outputs = ingredients.getIngredients(RecipeIngredientRole.OUTPUT);
                  if (outputs.isEmpty()) {
                     return DataResult.error(() -> "No outputs for recipe");
                  } else {
                     Pair<ResourceLocation, ITypedIngredient<?>> result = new Pair(registryName, (ITypedIngredient)outputs.getFirst());
                     return DataResult.success(result);
                  }
               }
            }
         );
   }

   @Override
   public Codec<RecipeType<?>> getRecipeTypeCodec(IRecipeManager recipeManager) {
      if (this.recipeTypeCodec == null) {
         this.recipeTypeCodec = ResourceLocation.CODEC
            .flatXmap(
               resourceLocation -> recipeManager.getRecipeType(resourceLocation)
                  .<DataResult>map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Failed to find recipe type " + resourceLocation)),
               recipeType -> {
                  ResourceLocation uid = recipeType.getUid();
                  return DataResult.success(uid);
               }
            );
      }

      return this.recipeTypeCodec;
   }

   private record Data(ResourceLocation registryName, ITypedIngredient<?> ingredient, RecipeIngredientRole ingredientRole) {
   }
}
