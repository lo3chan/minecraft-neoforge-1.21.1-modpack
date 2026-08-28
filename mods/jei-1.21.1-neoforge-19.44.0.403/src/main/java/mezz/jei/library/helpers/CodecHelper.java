/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeManager
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.helpers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
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

public class CodecHelper
implements ICodecHelper {
    private static final Codec<RecipeHolder<?>> RECIPE_HOLDER_CODEC = Codec.lazyInitialized(() -> {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        assert (level != null);
        RecipeManager recipeManager = level.getRecipeManager();
        return Codec.either((Codec)ResourceLocation.CODEC, TupleCodec.of(ResourceLocation.CODEC, Recipe.CODEC)).flatXmap(either -> (DataResult)either.map(recipeHolderId -> recipeManager.byKey(recipeHolderId).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Could not find recipe for key: " + String.valueOf(recipeHolderId))), pair -> {
            ResourceLocation recipeHolderId = (ResourceLocation)pair.getFirst();
            Recipe recipe = (Recipe)pair.getSecond();
            if (recipe == null) {
                return DataResult.error(() -> "Could not find recipe for key: " + String.valueOf(recipeHolderId));
            }
            RecipeHolder recipeHolder = new RecipeHolder(recipeHolderId, recipe);
            return DataResult.success((Object)recipeHolder);
        }), recipeHolder -> {
            ResourceLocation recipeHolderId = recipeHolder.id();
            Optional found = recipeManager.byKey(recipeHolderId);
            if (found.isPresent() && ((RecipeHolder)found.get()).equals(recipeHolder)) {
                return DataResult.success((Object)Either.left((Object)recipeHolderId));
            }
            Recipe recipe = recipeHolder.value();
            return DataResult.success((Object)Either.right((Object)Pair.of((Object)recipeHolderId, (Object)recipe)));
        });
    });
    private final IIngredientManager ingredientManager;
    private final IFocusFactory focusFactory;
    private final Map<RecipeType<?>, Codec<?>> defaultRecipeCodecs = new HashMap();
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
        Codec<RecipeHolder<?>> recipeHolderCodec = RECIPE_HOLDER_CODEC;
        return recipeHolderCodec;
    }

    @Override
    public <T> Codec<T> getSlowRecipeCategoryCodec(IRecipeCategory<T> recipeCategory, IRecipeManager recipeManager) {
        RecipeType<T> recipeType = recipeCategory.getRecipeType();
        Object codec = this.defaultRecipeCodecs.get(recipeType);
        if (codec == null) {
            codec = this.createDefaultRecipeCategoryCodec(recipeManager, recipeCategory);
            this.defaultRecipeCodecs.put(recipeType, (Codec<?>)codec);
        }
        return codec;
    }

    private <T> Codec<T> createDefaultRecipeCategoryCodec(IRecipeManager recipeManager, IRecipeCategory<T> recipeCategory) {
        Codec dataCodec = RecordCodecBuilder.create(builder -> builder.group((App)ResourceLocation.CODEC.fieldOf("registryName").forGetter(Data::registryName), (App)this.getTypedIngredientCodec().codec().fieldOf("ingredient").forGetter(Data::ingredient), (App)EnumCodec.create(RecipeIngredientRole.class).fieldOf("ingredient_role").forGetter(Data::ingredientRole)).apply((Applicative)builder, Data::new));
        Codec codec = dataCodec.flatXmap(data -> {
            ResourceLocation registryName = data.registryName();
            ITypedIngredient<?> ingredient = data.ingredient();
            IFocus<?> focus = this.focusFactory.createFocus(data.ingredientRole(), ingredient);
            RecipeType recipeType = recipeCategory.getRecipeType();
            return recipeManager.createRecipeLookup(recipeType).limitFocus(List.of(focus)).get().filter(recipe -> registryName.equals((Object)recipeCategory.getRegistryName(recipe))).findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No recipe found for registry name: " + String.valueOf(registryName)));
        }, recipe -> {
            ResourceLocation registryName = recipeCategory.getRegistryName(recipe);
            if (registryName == null) {
                return DataResult.error(() -> "No registry name for recipe");
            }
            IIngredientSupplier ingredients = recipeManager.getRecipeIngredients(recipeCategory, recipe);
            List<ITypedIngredient<?>> outputs = ingredients.getIngredients(RecipeIngredientRole.OUTPUT);
            if (!outputs.isEmpty()) {
                Data result = new Data(registryName, (ITypedIngredient)outputs.getFirst(), RecipeIngredientRole.OUTPUT);
                return DataResult.success((Object)result);
            }
            List<ITypedIngredient<?>> inputs = ingredients.getIngredients(RecipeIngredientRole.INPUT);
            if (!inputs.isEmpty()) {
                Data result = new Data(registryName, (ITypedIngredient)inputs.getFirst(), RecipeIngredientRole.INPUT);
                return DataResult.success((Object)result);
            }
            return DataResult.error(() -> "No inputs or outputs for recipe");
        });
        return Codec.withAlternative((Codec)codec, this.createLegacyDefaultRecipeCategoryCodec(recipeManager, recipeCategory));
    }

    private <T> Codec<T> createLegacyDefaultRecipeCategoryCodec(IRecipeManager recipeManager, IRecipeCategory<T> recipeCategory) {
        Codec legacyPairCodec = RecordCodecBuilder.create(builder -> builder.group((App)ResourceLocation.CODEC.fieldOf("registryName").forGetter(Pair::getFirst), (App)this.getTypedIngredientCodec().codec().fieldOf("output").forGetter(Pair::getSecond)).apply((Applicative)builder, Pair::new));
        TupleCodec tupleCodec = TupleCodec.of(ResourceLocation.CODEC, this.getTypedIngredientCodec().codec());
        return Codec.withAlternative(tupleCodec, (Codec)legacyPairCodec).flatXmap(pair -> {
            ResourceLocation registryName = (ResourceLocation)pair.getFirst();
            ITypedIngredient output = (ITypedIngredient)pair.getSecond();
            IFocus focus = this.focusFactory.createFocus(RecipeIngredientRole.OUTPUT, output);
            RecipeType recipeType = recipeCategory.getRecipeType();
            return recipeManager.createRecipeLookup(recipeType).limitFocus(List.of(focus)).get().filter(recipe -> registryName.equals((Object)recipeCategory.getRegistryName(recipe))).findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No recipe found for registry name: " + String.valueOf(registryName)));
        }, recipe -> {
            ResourceLocation registryName = recipeCategory.getRegistryName(recipe);
            if (registryName == null) {
                return DataResult.error(() -> "No registry name for recipe");
            }
            IIngredientSupplier ingredients = recipeManager.getRecipeIngredients(recipeCategory, recipe);
            List<ITypedIngredient<?>> outputs = ingredients.getIngredients(RecipeIngredientRole.OUTPUT);
            if (outputs.isEmpty()) {
                return DataResult.error(() -> "No outputs for recipe");
            }
            Pair result = new Pair((Object)registryName, (Object)((ITypedIngredient)outputs.getFirst()));
            return DataResult.success((Object)result);
        });
    }

    @Override
    public Codec<RecipeType<?>> getRecipeTypeCodec(IRecipeManager recipeManager) {
        if (this.recipeTypeCodec == null) {
            this.recipeTypeCodec = ResourceLocation.CODEC.flatXmap(resourceLocation -> recipeManager.getRecipeType((ResourceLocation)resourceLocation).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Failed to find recipe type " + String.valueOf(resourceLocation))), recipeType -> {
                ResourceLocation uid = recipeType.getUid();
                return DataResult.success((Object)uid);
            });
        }
        return this.recipeTypeCodec;
    }

    private record Data(ResourceLocation registryName, ITypedIngredient<?> ingredient, RecipeIngredientRole ingredientRole) {
    }
}

