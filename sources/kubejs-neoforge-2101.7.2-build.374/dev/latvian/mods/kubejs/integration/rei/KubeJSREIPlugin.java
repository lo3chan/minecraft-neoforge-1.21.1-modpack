package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.EventResult;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.server.CategoryData;
import dev.latvian.mods.kubejs.recipe.viewer.server.FluidData;
import dev.latvian.mods.kubejs.recipe.viewer.server.ItemData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RemoteRecipeViewerDataUpdatedEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.FluidComparatorRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

@REIPluginClient
public class KubeJSREIPlugin implements REIClientPlugin {
   private final Set<CategoryIdentifier<?>> categoriesRemoved = new HashSet<>();
   private final Map<CategoryIdentifier<?>, Collection<ResourceLocation>> recipesRemoved = new HashMap<>();
   private RecipeViewerData remote = null;

   public KubeJSREIPlugin() {
      NeoForge.EVENT_BUS.register(this);
   }

   public double getPriority() {
      return 1.0E7;
   }

   @SubscribeEvent
   public void loadRemote(RemoteRecipeViewerDataUpdatedEvent event) {
      this.remote = event.data;
   }

   public void registerEntries(EntryRegistry registry) {
      for (RecipeViewerEntryType type : RecipeViewerEntryType.ALL_TYPES.get()) {
         EntryType<?> entryType = REIIntegration.typeOf(type);
         if (entryType != null && RecipeViewerEvents.ADD_ENTRIES.hasListeners(type)) {
            RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.CLIENT, type, new REIAddEntriesKubeEvent(type, entryType, registry));
         }
      }

      if (this.remote != null) {
         for (ItemStack stack : this.remote.itemData().addedEntries()) {
            registry.addEntries(new EntryStack[]{EntryStacks.of(stack)});
         }

         for (FluidStack stack : this.remote.fluidData().addedEntries()) {
            registry.addEntries(new EntryStack[]{EntryStacks.of(FluidStackHooksForge.fromForge(stack))});
         }
      }
   }

   public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
      for (RecipeViewerEntryType type : RecipeViewerEntryType.ALL_TYPES.get()) {
         EntryType<?> entryType = REIIntegration.typeOf(type);
         if (entryType != null && RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(type)) {
            EntryRegistry registry = EntryRegistry.getInstance();
            List<EntryStack<?>> allItems = registry.getEntryStacks().filter(e -> e.getType() == entryType).toList();
            RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.CLIENT, type, new REIRemoveEntriesKubeEvent(type, registry, allItems));
         }

         if (entryType != null && RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.hasListeners(type)) {
            EntryRegistry registry = EntryRegistry.getInstance();
            List<EntryStack<?>> allFluids = registry.getEntryStacks().filter(e -> e.getType() == entryType).toList();
            RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.post(ScriptType.CLIENT, type, new REIRemoveEntriesCompletelyKubeEvent(type, allFluids, rule));
         }
      }

      if (this.remote != null) {
         EntryRegistry registry = EntryRegistry.getInstance();
         if (!this.remote.itemData().removedEntries().isEmpty() || this.remote.itemData().completelyRemovedEntries().isEmpty()) {
            List<EntryStack<?>> allItems = registry.getEntryStacks().filter(e -> e.getType() == VanillaEntryTypes.ITEM).toList();

            for (Ingredient filter : this.remote.itemData().removedEntries()) {
               for (EntryStack<?> entry : allItems) {
                  if (filter.test((ItemStack)entry.getValue())) {
                     registry.removeEntry(entry);
                  }
               }
            }

            for (Ingredient filter : this.remote.itemData().completelyRemovedEntries()) {
               rule.hide(allItems.stream().filter(e -> filter.test((ItemStack)e.getValue())).toList());
            }
         }

         if (!this.remote.fluidData().removedEntries().isEmpty() || this.remote.fluidData().completelyRemovedEntries().isEmpty()) {
            List<EntryStack<?>> allFluids = registry.getEntryStacks().filter(e -> e.getType() == VanillaEntryTypes.FLUID).toList();

            for (FluidIngredient filter : this.remote.fluidData().removedEntries()) {
               for (EntryStack<?> entryx : allFluids) {
                  if (filter.test(FluidStackHooksForge.toForge((dev.architectury.fluid.FluidStack)entryx.getValue()))) {
                     registry.removeEntry(entryx);
                  }
               }
            }

            for (FluidIngredient filter : this.remote.fluidData().completelyRemovedEntries()) {
               rule.hide(allFluids.stream().filter(e -> filter.test(FluidStackHooksForge.toForge((dev.architectury.fluid.FluidStack)e.getValue()))).toList());
            }
         }
      }
   }

   public void registerDisplays(DisplayRegistry registry) {
      for (RecipeViewerEntryType type : RecipeViewerEntryType.ALL_TYPES.get()) {
         if (RecipeViewerEvents.ADD_INFORMATION.hasListeners(type)) {
            RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.CLIENT, type, new REIAddInformationKubeEvent(type));
         }
      }

      if (this.remote != null) {
         for (ItemData.Info info : this.remote.itemData().info()) {
            if (!info.filter().isEmpty() && !info.info().isEmpty()) {
               BuiltinClientPlugin.getInstance()
                  .registerInformation(EntryIngredients.ofIngredient(info.filter()), (Component)info.info().getFirst(), components -> {
                     for (int i = 1; i < info.info().size(); i++) {
                        components.add(info.info().get(i));
                     }

                     return components;
                  });
            }
         }

         for (FluidData.Info infox : this.remote.fluidData().info()) {
            if (!infox.filter().isEmpty() && !infox.info().isEmpty()) {
               BuiltinClientPlugin.getInstance()
                  .registerInformation(REIIntegration.fluidIngredient(infox.filter()), (Component)infox.info().getFirst(), components -> {
                     for (int i = 1; i < info.info().size(); i++) {
                        components.add(info.info().get(i));
                     }

                     return components;
                  });
            }
         }
      }

      registry.registerVisibilityPredicate(
         (cat, display) -> {
            Optional<ResourceLocation> id = display.getDisplayLocation();
            return id.isPresent() && this.recipesRemoved.getOrDefault(cat.getCategoryIdentifier(), List.of()).contains(id.get())
               ? EventResult.interruptFalse()
               : EventResult.pass();
         }
      );
   }

   public void registerCategories(CategoryRegistry registry) {
      registry.registerVisibilityPredicate(
         category -> this.categoriesRemoved.contains(category.getCategoryIdentifier()) ? EventResult.interruptFalse() : EventResult.pass()
      );
   }

   public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
      if (stage == ReloadStage.END) {
         this.categoriesRemoved.clear();
         this.recipesRemoved.clear();
         if (RecipeViewerEvents.REMOVE_CATEGORIES.hasListeners()) {
            RecipeViewerEvents.REMOVE_CATEGORIES.post(ScriptType.CLIENT, new REIRemoveCategoriesKubeEvent(this.categoriesRemoved));
         }

         if (RecipeViewerEvents.REMOVE_RECIPES.hasListeners()) {
            RecipeViewerEvents.REMOVE_RECIPES.post(ScriptType.CLIENT, new REIRemoveRecipeKubeEvent(this.recipesRemoved));
         }

         if (this.remote != null) {
            this.categoriesRemoved.addAll(this.remote.removedCategories().stream().map(CategoryIdentifier::of).toList());

            for (CategoryData entry : this.remote.categoryData()) {
               this.recipesRemoved.computeIfAbsent(CategoryIdentifier.of(entry.category()), k -> new HashSet<>()).addAll(entry.removedRecipes());
            }
         }
      }
   }

   public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
      for (RecipeViewerEntryType type : RecipeViewerEntryType.ALL_TYPES.get()) {
         EntryType<?> entryType = REIIntegration.typeOf(type);
         if (entryType != null && RecipeViewerEvents.GROUP_ENTRIES.hasListeners(type)) {
            RecipeViewerEvents.GROUP_ENTRIES.post(ScriptType.CLIENT, type, new REIGroupEntriesKubeEvent(type, entryType, registry));
         }
      }

      if (this.remote != null) {
         for (ItemData.Group group : this.remote.itemData().groupedEntries()) {
            registry.group(group.groupId(), group.description(), e -> e.getType() == VanillaEntryTypes.ITEM && group.filter().test((ItemStack)e.getValue()));
         }

         for (FluidData.Group group : this.remote.fluidData().groupedEntries()) {
            registry.group(
               group.groupId(),
               group.description(),
               e -> e.getType() == VanillaEntryTypes.FLUID
                  && group.filter().test(FluidStackHooksForge.toForge((dev.architectury.fluid.FluidStack)e.getValue()))
            );
         }
      }
   }

   public void registerItemComparators(ItemComparatorRegistry registry) {
      if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.ITEM)) {
         RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.ITEM, new REIRegisterItemSubtypesKubeEvent(registry));
      }

      if (this.remote != null) {
         for (ItemData.DataComponentSubtypes subtypes : this.remote.itemData().dataComponentSubtypes()) {
            Item[] items = subtypes.filter().kjs$getItemTypes().toArray(new Item[0]);
            if (subtypes.components().isEmpty()) {
               registry.registerComponents(items);
            } else {
               registry.register(DataComponentComparator.of(subtypes.components()), items);
            }
         }
      }
   }

   public void registerFluidComparators(FluidComparatorRegistry registry) {
      if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.FLUID)) {
         RecipeViewerEvents.REGISTER_SUBTYPES.post(ScriptType.CLIENT, RecipeViewerEntryType.FLUID, new REIRegisterFluidSubtypesKubeEvent(registry));
      }

      if (this.remote != null) {
         for (FluidData.DataComponentSubtypes subtypes : this.remote.fluidData().dataComponentSubtypes()) {
            Fluid[] fluids = Arrays.stream(subtypes.filter().getStacks()).map(FluidStack::getFluid).toArray(Fluid[]::new);
            registry.register(DataComponentComparator.of(subtypes.components()), fluids);
         }
      }
   }
}
