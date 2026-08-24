package net.cibernet.alchemancy.modSupport.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.crafting.ForgeItemRecipe;
import net.cibernet.alchemancy.crafting.ForgePropertyRecipe;
import net.cibernet.alchemancy.crafting.ItemTransmutationRecipe;
import net.cibernet.alchemancy.crafting.PropertyWarpRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class AlchemancyJeiPlugin implements IModPlugin {
   private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("alchemancy", "alchemancy_forge");
   public static final RecipeType<ItemTransmutationRecipe> TRANSMUTATION = RecipeType.create("alchemancy", "transmutation", ItemTransmutationRecipe.class);
   public static final RecipeType<PropertyWarpRecipe> PROPERTY_WARPING = RecipeType.create("alchemancy", "property_warping", PropertyWarpRecipe.class);
   public static final RecipeType<ForgeItemRecipe> ITEM_FORGING = RecipeType.create("alchemancy", "item_forging", ForgeItemRecipe.class);
   public static final RecipeType<ForgePropertyRecipe> PROPERTY_FORGING = RecipeType.create("alchemancy", "property_forging", ForgePropertyRecipe.class);

   public ResourceLocation getPluginUid() {
      return UID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new ItemTransmutationCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new PropertyWarpingCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new ItemForgingCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new PropertyForgingCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerItemSubtypes(ISubtypeRegistration registration) {
      registration.registerSubtypeInterpreter(
         AlchemancyItems.PROPERTY_CAPSULE.asItem(),
         new ISubtypeInterpreter<ItemStack>() {
            @Nullable
            public Object getSubtypeData(ItemStack ingredient, UidContext context) {
               return ingredient.get(AlchemancyItems.Components.STORED_PROPERTIES);
            }

            public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
               return String.join(
                  " ",
                  ((InfusedPropertiesComponent)ingredient.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY))
                     .properties()
                     .stream()
                     .map(propertyHolder -> ((Property)propertyHolder.value()).getKey().toString())
                     .toList()
               );
            }
         }
      );
   }

   public void registerExtraIngredients(IExtraIngredientRegistration registration) {
      super.registerExtraIngredients(registration);
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalysts(TRANSMUTATION, new ItemLike[]{AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE});
      registration.addRecipeCatalysts(
         ITEM_FORGING, new ItemLike[]{AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE, AlchemancyItems.INFUSION_PEDESTAL}
      );
      registration.addRecipeCatalysts(
         PROPERTY_FORGING, new ItemLike[]{AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE, AlchemancyItems.INFUSION_PEDESTAL}
      );
      registration.addRecipeCatalysts(PROPERTY_WARPING, new ItemLike[]{AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE});
      registration.addRecipeCatalyst(InfusedPropertiesHelper.createPropertyIngredient(AlchemancyProperties.WARPED), new RecipeType[]{PROPERTY_WARPING});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      List<RecipeHolder<AbstractForgeRecipe<?>>> forgeRecipes = Minecraft.getInstance()
         .level
         .getRecipeManager()
         .getAllRecipesFor((net.minecraft.world.item.crafting.RecipeType)AlchemancyRecipeTypes.ALCHEMANCY_FORGE.get());
      List<ItemTransmutationRecipe> transmutationRecipes = new ArrayList<>();
      List<PropertyWarpRecipe> propertyWarpRecipes = new ArrayList<>();
      List<ForgeItemRecipe> forgeItemRecipes = new ArrayList<>();
      List<ForgePropertyRecipe> forgePropertyRecipes = new ArrayList<>();

      for (RecipeHolder<AbstractForgeRecipe<?>> recipe : forgeRecipes) {
         if ((this.isSecretTransmutation(recipe) || !this.addTo(transmutationRecipes, ItemTransmutationRecipe.class, recipe))
            && !this.addToExact(propertyWarpRecipes, PropertyWarpRecipe.class, recipe)
            && !this.addToExact(forgeItemRecipes, ForgeItemRecipe.class, recipe)
            && this.addToExact(forgePropertyRecipes, ForgePropertyRecipe.class, recipe)) {
         }
      }

      registration.addRecipes(TRANSMUTATION, transmutationRecipes);
      registration.addRecipes(PROPERTY_WARPING, propertyWarpRecipes);
      registration.addRecipes(ITEM_FORGING, forgeItemRecipes);
      registration.addRecipes(PROPERTY_FORGING, forgePropertyRecipes);
      List<ItemStack> dormantPropertyCapsules = new ArrayList<>();

      for (DeferredHolder<Property, ? extends Property> propertyHolder : AlchemancyProperties.REGISTRY.getEntries()) {
         TagKey<Item> tagKey = TagKey.create(
            Registries.ITEM, ResourceLocation.fromNamespaceAndPath("alchemancy", "dormant_properties/" + propertyHolder.getKey().location().getPath())
         );
         Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(tagKey);
         if (propertyHolder.equals(AlchemancyProperties.VOIDBORN)) {
            List<ItemStack> voidbornItems = new ArrayList<>();
            voidbornItems.add(InfusedPropertiesHelper.createPropertyIngredient(propertyHolder));
            tag.ifPresent(
               holders -> voidbornItems.addAll(
                  holders.stream().filter(Holder::isBound).map(itemHolder -> ((Item)itemHolder.value()).getDefaultInstance()).collect(Collectors.toSet())
               )
            );
            registration.addItemStackInfo(voidbornItems, new Component[]{Component.translatable("recipe.alchemancy.voidborn.info")});
         } else if (tag.isPresent() && tag.get().size() > 0) {
            dormantPropertyCapsules.add(InfusedPropertiesHelper.createPropertyIngredient(propertyHolder));
         }
      }

      registration.addItemStackInfo(dormantPropertyCapsules, new Component[]{Component.translatable("recipe.alchemancy.dormant_properties.info")});
   }

   public <T extends AbstractForgeRecipe<?>> boolean addToExact(List<T> list, Class<T> clazz, RecipeHolder<AbstractForgeRecipe<?>> holder) {
      if (clazz.equals(((AbstractForgeRecipe)holder.value()).getClass())) {
         list.add(clazz.cast(holder.value()));
         return true;
      } else {
         return false;
      }
   }

   private boolean isSecretTransmutation(RecipeHolder<AbstractForgeRecipe<?>> recipe) {
      return AlchemancyItems.SECRET_TRANSMUTATIONS
         .stream()
         .anyMatch(
            itemHolder -> ((Item)itemHolder.value()).equals(((AbstractForgeRecipe)recipe.value()).getResultItem(CommonUtils.registryAccessStatic()).getItem())
         );
   }

   public <T extends AbstractForgeRecipe<?>> boolean addTo(List<T> list, Class<T> clazz, RecipeHolder<AbstractForgeRecipe<?>> holder) {
      if (clazz.isInstance(holder.value())) {
         list.add(clazz.cast(holder.value()));
         return true;
      } else {
         return false;
      }
   }
}
