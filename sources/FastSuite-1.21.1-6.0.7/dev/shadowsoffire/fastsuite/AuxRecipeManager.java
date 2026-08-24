package dev.shadowsoffire.fastsuite;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.VisibleForTesting;

public class AuxRecipeManager extends RecipeManager {
   private final Map<RecipeType<?>, CachedRecipeList<?, ?>> cachedRecipeListMap = new HashMap<>();

   public AuxRecipeManager(Provider registries) {
      super(registries);
   }

   @VisibleForTesting
   public <C extends RecipeInput, T extends Recipe<C>> Optional<RecipeHolder<T>> super_getRecipeFor(RecipeType<T> type, C inv, Level level) {
      return super.getRecipeFor(type, inv, level);
   }

   public <C extends RecipeInput, T extends Recipe<C>> Optional<RecipeHolder<T>> getRecipeFor(
      RecipeType<T> type, C inv, Level level, @Nullable RecipeHolder<T> lastRecipe
   ) {
      if (this.numRecipesOf(type) >= 100 && !FastSuite.singleThreadedLookups.contains(type)) {
         if (lastRecipe != null && lastRecipe.value().matches(inv, level)) {
            return Optional.of(lastRecipe);
         } else {
            this.lockAllStacks(inv, true);

            Optional ex;
            try {
               if (!FastSuite.unsafeMode) {
                  CachedRecipeList<C, T> cachedRecipeList = this.getCachedRecipeList(type);
                  Optional<RecipeHolder<T>> out = cachedRecipeList.getRecipeFor(inv, level);
                  if (FastSuite.DEBUG_MATCHING) {
                     FastSuite.LOGGER.info("Matched recipe: " + out + " for input " + inv);
                  }

                  return out;
               }

               ex = StreamUtils.executeUntil(
                  () -> this.byType(type).parallelStream().filter(recipe -> recipe.value().matches(inv, level)).findFirst(),
                  FastSuite.maxRecipeLookupTime,
                  TimeUnit.SECONDS,
                  Optional.empty(),
                  () -> CachedRecipeList.timeoutMsg(type)
               );
            } catch (Exception var11) {
               throw new RuntimeException(var11);
            } finally {
               this.lockAllStacks(inv, false);
            }

            return ex;
         }
      } else {
         return super.getRecipeFor(type, inv, level, lastRecipe);
      }
   }

   public <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> getRecipesFor(RecipeType<T> type, C inv, Level level) {
      if (this.numRecipesOf(type) >= 100 && !FastSuite.singleThreadedLookups.contains(type)) {
         this.lockAllStacks(inv, true);

         List ex;
         try {
            if (!FastSuite.unsafeMode) {
               CachedRecipeList<C, T> cachedRecipeList = this.getCachedRecipeList(type);
               return cachedRecipeList.getRecipesFor(inv, level);
            }

            ex = StreamUtils.executeUntil(
               () -> this.byType(type)
                  .parallelStream()
                  .filter(recipe -> recipe.value().matches(inv, level))
                  .sorted(Comparator.comparing(recipe -> recipe.value().getResultItem(level.registryAccess()).getDescriptionId()))
                  .collect(Collectors.toList()),
               FastSuite.maxRecipeLookupTime,
               TimeUnit.SECONDS,
               Collections.emptyList(),
               () -> CachedRecipeList.timeoutMsg(type)
            );
         } catch (Exception var9) {
            throw new RuntimeException(var9);
         } finally {
            this.lockAllStacks(inv, false);
         }

         return ex;
      } else {
         return super.getRecipesFor(type, inv, level);
      }
   }

   private <C extends RecipeInput, T extends Recipe<C>> CachedRecipeList<C, T> getCachedRecipeList(RecipeType<T> type) {
      synchronized (this.cachedRecipeListMap) {
         CachedRecipeList<C, T> list = (CachedRecipeList<C, T>)this.cachedRecipeListMap.get(type);
         if (list == null) {
            list = new CachedRecipeList<>(type, this.byType(type));
            this.cachedRecipeListMap.put(type, list);
         }

         return list;
      }
   }

   private <C extends RecipeInput> void lockAllStacks(C inv, boolean locked) {
      if (FastSuite.lockInputStacks) {
         for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty()) {
               ((ILockableItemStack)s).setLocked(locked);
            }
         }
      }
   }

   private int numRecipesOf(RecipeType type) {
      return this.byType(type).size();
   }
}
