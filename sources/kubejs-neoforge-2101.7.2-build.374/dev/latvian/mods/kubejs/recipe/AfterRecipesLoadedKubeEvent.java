package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;
import dev.latvian.mods.rhino.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AfterRecipesLoadedKubeEvent implements KubeEvent {
   private final RecipeManagerKJS recipeManager;
   private final RegistryAccessContainer registries;
   private List<RecipeLikeKJS> originalRecipes;
   private boolean changed;

   public AfterRecipesLoadedKubeEvent(ReloadableServerResources resources) {
      this.recipeManager = resources.getRecipeManager();
      this.registries = resources.kjs$getServerScriptManager().getRegistries();
      this.originalRecipes = null;
      this.changed = false;
   }

   private List<RecipeLikeKJS> getOriginalRecipes() {
      if (this.originalRecipes == null) {
         this.originalRecipes = new ArrayList<>(this.recipeManager.kjs$getRecipeIdMap().values());
      }

      return this.originalRecipes;
   }

   public void forEachRecipe(Context cx, RecipeFilter filter, Consumer<RecipeLikeKJS> consumer) {
      if (filter == ConstantFilter.TRUE) {
         this.getOriginalRecipes().forEach(consumer);
      } else if (filter != ConstantFilter.FALSE) {
         this.getOriginalRecipes()
            .stream()
            .filter(r -> filter.test((RecipeMatchContext)(new AfterRecipesLoadedKubeEvent.MatchCx(cx, this, r))))
            .forEach(consumer);
      }
   }

   public int countRecipes(Context cx, RecipeFilter filter) {
      if (filter == ConstantFilter.TRUE) {
         return this.getOriginalRecipes().size();
      } else {
         return filter != ConstantFilter.FALSE
            ? (int)this.getOriginalRecipes()
               .stream()
               .filter(r -> filter.test((RecipeMatchContext)(new AfterRecipesLoadedKubeEvent.MatchCx(cx, this, r))))
               .count()
            : 0;
      }
   }

   public int remove(Context cx, RecipeFilter filter) {
      int count = 0;
      Iterator<RecipeLikeKJS> itr = this.getOriginalRecipes().iterator();

      while (itr.hasNext()) {
         RecipeLikeKJS r = itr.next();
         if (filter.test((RecipeMatchContext)(new AfterRecipesLoadedKubeEvent.MatchCx(cx, this, r)))) {
            itr.remove();
            count++;
            this.changed = true;
            if (DevProperties.get().logRemovedRecipes) {
               ConsoleJS.SERVER.info("- " + r);
            } else if (ConsoleJS.SERVER.shouldPrintDebug()) {
               ConsoleJS.SERVER.debug("- " + r);
            }
         }
      }

      return count;
   }

   @Override
   public void afterPosted(EventResult result) {
      if (this.changed) {
         HashMap<ResourceLocation, RecipeHolder<?>> map = new HashMap<>();

         for (RecipeLikeKJS r : this.getOriginalRecipes()) {
            map.put(r.kjs$getOrCreateId(), (RecipeHolder<?>)r);
         }

         this.recipeManager.kjs$replaceRecipes(map);
      }
   }

   public record MatchCx(Context cx, RegistryAccessContainer registries, RegistryOpsContainer ops, RecipeLikeKJS recipe) implements RecipeMatchContext {
      public MatchCx(Context cx, AfterRecipesLoadedKubeEvent event, RecipeLikeKJS recipe) {
         this(cx, event.registries, event.registries, recipe);
      }
   }
}
