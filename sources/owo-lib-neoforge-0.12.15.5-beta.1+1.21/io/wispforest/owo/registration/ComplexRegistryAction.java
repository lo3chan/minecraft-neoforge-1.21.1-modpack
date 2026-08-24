package io.wispforest.owo.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ComplexRegistryAction {
   private final List<ResourceLocation> predicates;
   private final Runnable action;

   protected ComplexRegistryAction(List<ResourceLocation> predicates, Runnable action) {
      this.predicates = predicates;
      this.action = action;
   }

   protected <T> boolean preCheck(Registry<T> registry) {
      this.predicates.removeIf(registry::containsKey);
      if (!this.predicates.isEmpty()) {
         return false;
      } else {
         this.action.run();
         return true;
      }
   }

   protected boolean update(ResourceLocation id, Collection<Runnable> actionList) {
      this.predicates.remove(id);
      if (!this.predicates.isEmpty()) {
         return false;
      } else {
         actionList.add(this.action);
         return true;
      }
   }

   public static class Builder {
      private final Runnable action;
      private final List<ResourceLocation> predicates;

      private Builder(Runnable action) {
         this.action = action;
         this.predicates = new ArrayList<>();
      }

      public static ComplexRegistryAction.Builder create(Runnable action) {
         return new ComplexRegistryAction.Builder(action);
      }

      public ComplexRegistryAction.Builder entry(ResourceLocation id) {
         this.predicates.add(id);
         return this;
      }

      public ComplexRegistryAction.Builder entries(Collection<ResourceLocation> ids) {
         this.predicates.addAll(ids);
         return this;
      }

      public ComplexRegistryAction build() {
         if (this.predicates.isEmpty()) {
            throw new IllegalStateException("Predicate list must not be empty");
         } else {
            return new ComplexRegistryAction(this.predicates, this.action);
         }
      }
   }
}
