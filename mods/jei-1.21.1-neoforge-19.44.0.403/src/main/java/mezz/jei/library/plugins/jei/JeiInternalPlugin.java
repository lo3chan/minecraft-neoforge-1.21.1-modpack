/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package mezz.jei.library.plugins.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.library.plugins.jei.info.IngredientInfoRecipeCategory;
import mezz.jei.library.plugins.jei.tags.ITagInfoRecipe;
import mezz.jei.library.plugins.jei.tags.TagInfoRecipeCategory;
import mezz.jei.library.plugins.jei.tags.TagInfoRecipeMaker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class JeiInternalPlugin
implements IModPlugin {
    private final List<TagInfoRecipeMaker<?, ?>> tagInfoRecipeMakers = new ArrayList();

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath((String)"jei", (String)"internal");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IIngredientManager ingredientManager = jeiHelpers.getIngredientManager();
        Textures textures = Internal.getTextures();
        registration.addRecipeCategories(new IngredientInfoRecipeCategory(textures));
        this.tagInfoRecipeMakers.clear();
        IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
        IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
        if (clientConfig.showTagRecipesEnabled().getValue().booleanValue()) {
            RegistryUtil.getRegistryAccess().registries().forEach(entry -> {
                Registry registry = entry.value();
                JeiInternalPlugin.createAndRegisterTagCategory(registration, this.tagInfoRecipeMakers, ingredientManager, registry);
            });
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
        IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
        if (clientConfig.showTagRecipesEnabled().getValue().booleanValue()) {
            for (TagInfoRecipeMaker<?, ?> data : this.tagInfoRecipeMakers) {
                data.addRecipes(registration);
            }
        }
        this.tagInfoRecipeMakers.clear();
    }

    private static <B> void createAndRegisterTagCategory(IRecipeCategoryRegistration registration, List<TagInfoRecipeMaker<?, ?>> tagInfoRecipeMakers, IIngredientManager ingredientManager, Registry<B> registry) {
        registry.getAny().ifPresent(holder -> {
            Registry itemLikeRegistry;
            IJeiHelpers jeiHelpers = registration.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
            Object ingredient = holder.value();
            IIngredientType type = ingredientManager.getIngredientTypeChecked(ingredient).orElse(null);
            if (type != null) {
                ResourceLocation registryLocation = registry.key().location();
                RecipeType<ITagInfoRecipe> recipeType = JeiInternalPlugin.createTagInfoRecipeType(registryLocation);
                registration.addRecipeCategories(new TagInfoRecipeCategory(guiHelper, recipeType, registryLocation));
                tagInfoRecipeMakers.add(new TagInfoRecipeMaker(type, recipeType, Function.identity(), registry.key()));
                return;
            }
            IIngredientTypeWithSubtypes typeWithSubtypes = ingredientManager.getIngredientTypeWithSubtypesFromBase(ingredient).orElse(null);
            if (typeWithSubtypes != null && JeiInternalPlugin.createAndRegisterTagCategory(registration, tagInfoRecipeMakers, registry, ingredient, typeWithSubtypes)) {
                return;
            }
            if (ingredient instanceof ItemLike && JeiInternalPlugin.createAndRegisterItemLikeTagCategory(registration, tagInfoRecipeMakers, itemLikeRegistry = registry)) {
                return;
            }
        });
    }

    private static RecipeType<ITagInfoRecipe> createTagInfoRecipeType(ResourceLocation registryLocation) {
        return RecipeType.create(registryLocation.getNamespace(), "tag_recipes/" + registryLocation.getPath(), ITagInfoRecipe.class);
    }

    private static <B, I> boolean createAndRegisterTagCategory(IRecipeCategoryRegistration registration, List<TagInfoRecipeMaker<?, ?>> tagInfoRecipeMakers, Registry<B> registry, B baseIngredient, IIngredientTypeWithSubtypes<B, I> knownType) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        try {
            knownType.getDefaultIngredient(baseIngredient);
        }
        catch (UnsupportedOperationException ignored) {
            return false;
        }
        ResourceLocation registryLocation = registry.key().location();
        RecipeType<ITagInfoRecipe> recipeType = JeiInternalPlugin.createTagInfoRecipeType(registryLocation);
        registration.addRecipeCategories(new TagInfoRecipeCategory(guiHelper, recipeType, registryLocation));
        tagInfoRecipeMakers.add(new TagInfoRecipeMaker<Object, Object>(knownType, recipeType, knownType::getDefaultIngredient, registry.key()));
        return true;
    }

    private static <B extends ItemLike> boolean createAndRegisterItemLikeTagCategory(IRecipeCategoryRegistration registration, List<TagInfoRecipeMaker<?, ?>> tagInfoRecipeMakers, Registry<B> registry) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        ResourceKey registryKey = registry.key();
        ResourceLocation registryLocation = registryKey.location();
        RecipeType<ITagInfoRecipe> recipeType = JeiInternalPlugin.createTagInfoRecipeType(registryLocation);
        registration.addRecipeCategories(new TagInfoRecipeCategory(guiHelper, recipeType, registryLocation));
        tagInfoRecipeMakers.add(new TagInfoRecipeMaker<ItemLike, ItemStack>(VanillaTypes.ITEM_STACK, recipeType, i -> i.asItem().getDefaultInstance(), registryKey));
        return true;
    }
}

