package com.alonie.brbe.compat.recipeviewer;

import java.util.ArrayList;
import java.util.List;

public final class RecipeViewerRegistry {
   private final List<RecipeViewer> viewers = new ArrayList<>();

   public void register(RecipeViewer viewer) {
      this.viewers.add(viewer);
   }

   public List<RecipeViewer> all() {
      return List.copyOf(this.viewers);
   }

   public RecipeViewer findFirst() {
      for (RecipeViewer v : this.viewers) {
         if (v.isAvailable()) {
            return v;
         }
      }

      return RecipeViewer.NONE;
   }

   public boolean anyAvailable() {
      return this.findFirst() != RecipeViewer.NONE;
   }
}
