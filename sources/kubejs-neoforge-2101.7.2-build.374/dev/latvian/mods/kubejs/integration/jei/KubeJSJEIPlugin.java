package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.server.FluidData;
import dev.latvian.mods.kubejs.recipe.viewer.server.ItemData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RemoteRecipeViewerDataUpdatedEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.CompoundFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

@JeiPlugin
public class KubeJSJEIPlugin implements IModPlugin {
   public static final ResourceLocation ID = KubeJS.id("jei");
   public static final boolean DISABLED = ModList.get().isLoaded("emi");
   private RecipeViewerData remote = null;

   public KubeJSJEIPlugin() {
      NeoForge.EVENT_BUS.register(this);
   }

   public ResourceLocation getPluginUid() {
      return ID;
   }

   @SubscribeEvent
   public void loadRemote(RemoteRecipeViewerDataUpdatedEvent event) {
      this.remote = event.data;
   }

   public void onRuntimeAvailable(IJeiRuntime runtime) {
      if (!DISABLED) {
         IRecipeManager recipeManager = runtime.getRecipeManager();
         IIngredientManager ingredientManager = runtime.getIngredientManager();
         HashMap<ResourceLocation, IRecipeCategory<?>> categories = new HashMap<>(
            runtime.getRecipeManager().createRecipeCategoryLookup().get().collect(Collectors.toMap(cat -> cat.getRecipeType().getUid(), Function.identity()))
         );
         if (RecipeViewerEvents.REMOVE_CATEGORIES.hasListeners()) {
            RecipeViewerEvents.REMOVE_CATEGORIES.post(ScriptType.CLIENT, new JEIRemoveCategoriesKubeEvent(runtime.getRecipeManager(), categories));
         }

         if (RecipeViewerEvents.REMOVE_RECIPES.hasListeners()) {
            RecipeViewerEvents.REMOVE_RECIPES.post(ScriptType.CLIENT, new JEIRemoveRecipesKubeEvent(runtime.getRecipeManager(), categories));
         }

         if (this.remote != null) {
            for (ResourceLocation removedCategory : this.remote.removedCategories()) {
               IRecipeCategory<?> category = categories.get(removedCategory);
               if (category != null) {
                  recipeManager.hideRecipeCategory(category.getRecipeType());
                  categories.remove(removedCategory);
               }
            }
         }

         Collection<ItemStack> allItems = ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK);
         Collection<FluidStack> allFluids = ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK);

         for (RecipeViewerEntryType type : RecipeViewerEntryType.ALL_TYPES.get()) {
            IIngredientType<?> ingredientType = JEIIntegration.typeOf(type);
            if (ingredientType != null && RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(type)) {
               RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.CLIENT, type, new JEIRemoveEntriesKubeEvent(runtime, type, ingredientType));
            }

            if (ingredientType != null && RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.hasListeners(type)) {
               RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.post(ScriptType.CLIENT, type, new JEIRemoveEntriesKubeEvent(runtime, type, ingredientType));
            }
         }

         if (this.remote != null) {
            if (!this.remote.itemData().removedEntries().isEmpty() || !this.remote.itemData().completelyRemovedEntries().isEmpty()) {
               ArrayList<Ingredient> filterList = new ArrayList<>(
                  this.remote.itemData().removedEntries().size() + this.remote.itemData().completelyRemovedEntries().size()
               );
               filterList.addAll(this.remote.itemData().removedEntries());
               filterList.addAll(this.remote.itemData().completelyRemovedEntries());
               Ingredient filter = CompoundIngredient.of(filterList.toArray(new Ingredient[0]));
               ArrayList<ItemStack> removed = new ArrayList<>();

               for (ItemStack stack : allItems) {
                  if (filter.test(stack)) {
                     removed.add(stack);
                  }
               }

               ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, removed);
            }

            if (!this.remote.fluidData().removedEntries().isEmpty() || !this.remote.fluidData().completelyRemovedEntries().isEmpty()) {
               ArrayList<FluidIngredient> filterList = new ArrayList<>(
                  this.remote.fluidData().removedEntries().size() + this.remote.fluidData().completelyRemovedEntries().size()
               );
               filterList.addAll(this.remote.fluidData().removedEntries());
               filterList.addAll(this.remote.fluidData().completelyRemovedEntries());
               CompoundFluidIngredient filter = new CompoundFluidIngredient(filterList);
               ArrayList<FluidStack> removed = new ArrayList<>();

               for (FluidStack stackx : allFluids) {
                  if (filter.test(stackx)) {
                     removed.add(stackx);
                  }
               }

               ingredientManager.removeIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, removed);
            }
         }

         for (RecipeViewerEntryType type : RecipeViewerEntryType.ALL_TYPES.get()) {
            IIngredientType<?> ingredientTypex = JEIIntegration.typeOf(type);
            if (ingredientTypex != null && RecipeViewerEvents.ADD_ENTRIES.hasListeners(type)) {
               RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.CLIENT, type, new JEIAddEntriesKubeEvent(runtime, type, ingredientTypex));
            }
         }

         if (this.remote != null) {
            if (!this.remote.itemData().addedEntries().isEmpty()) {
               ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, this.remote.itemData().addedEntries());
            }

            if (!this.remote.fluidData().addedEntries().isEmpty()) {
               ingredientManager.addIngredientsAtRuntime(NeoForgeTypes.FLUID_STACK, this.remote.fluidData().addedEntries());
            }
         }
      }
   }

   public void registerRecipes(IRecipeRegistration registration) {
      if (!DISABLED) {
         for (RecipeViewerEntryType type : RecipeViewerEntryType.ALL_TYPES.get()) {
            IIngredientType<?> ingredientType = JEIIntegration.typeOf(type);
            if (ingredientType != null && RecipeViewerEvents.ADD_INFORMATION.hasListeners(type)) {
               RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.CLIENT, type, new JEIAddInformationKubeEvent(type, ingredientType, registration));
            }
         }

         if (this.remote != null) {
            Collection<ItemStack> allItems = registration.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK);

            for (ItemData.Info info : this.remote.itemData().info()) {
               ArrayList<ItemStack> stacks = new ArrayList<>();

               for (ItemStack stack : allItems) {
                  if (info.filter().test(stack)) {
                     stacks.add(stack);
                  }
               }

               registration.addIngredientInfo(stacks, VanillaTypes.ITEM_STACK, info.info().toArray(new Component[0]));
            }

            Collection<FluidStack> allFluids = registration.getIngredientManager().getAllIngredients(NeoForgeTypes.FLUID_STACK);

            for (FluidData.Info info : this.remote.fluidData().info()) {
               ArrayList<FluidStack> stacks = new ArrayList<>();

               for (FluidStack stackx : allFluids) {
                  if (info.filter().test(stackx)) {
                     stacks.add(stackx);
                  }
               }

               registration.addIngredientInfo(stacks, NeoForgeTypes.FLUID_STACK, info.info().toArray(new Component[0]));
            }
         }
      }
   }

   public void registerItemSubtypes(ISubtypeRegistration registration) {
      if (!DISABLED) {
         if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.ITEM)) {
            RecipeViewerEvents.REGISTER_SUBTYPES
               .post(
                  ScriptType.CLIENT,
                  RecipeViewerEntryType.ITEM,
                  new JEIRegisterSubtypesKubeEvent(RecipeViewerEntryType.ITEM, VanillaTypes.ITEM_STACK, registration)
               );
         }

         if (this.remote != null) {
            for (ItemData.DataComponentSubtypes subtypes : this.remote.itemData().dataComponentSubtypes()) {
               DataComponentTypeInterpreter in = DataComponentTypeInterpreter.of(subtypes.components());

               for (Item item : subtypes.filter().kjs$getItemTypes()) {
                  registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, in);
               }
            }
         }
      }
   }

   public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
      if (!DISABLED) {
         if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.FLUID)) {
            RecipeViewerEvents.REGISTER_SUBTYPES
               .post(
                  ScriptType.CLIENT,
                  RecipeViewerEntryType.FLUID,
                  new JEIRegisterSubtypesKubeEvent(RecipeViewerEntryType.FLUID, NeoForgeTypes.FLUID_STACK, registration)
               );
         }

         if (this.remote != null) {
            for (FluidData.DataComponentSubtypes subtypes : this.remote.fluidData().dataComponentSubtypes()) {
               DataComponentTypeInterpreter in = DataComponentTypeInterpreter.of(subtypes.components());

               for (Fluid fluid : Arrays.stream(subtypes.filter().getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new)) {
                  registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, fluid, in);
               }
            }
         }
      }
   }
}
