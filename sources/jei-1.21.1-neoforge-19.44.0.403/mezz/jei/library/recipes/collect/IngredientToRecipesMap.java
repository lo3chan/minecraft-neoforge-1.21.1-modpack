package mezz.jei.library.recipes.collect;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.UnmodifiableView;

public class IngredientToRecipesMap<R> {
   private final Map<Object, ArrayList<R>> uidToRecipes = new Object2ObjectOpenHashMap();

   public void add(R recipe, Collection<Object> ingredientUids) {
      for (Object uid : ingredientUids) {
         List<R> recipes = this.uidToRecipes.computeIfAbsent(uid, k -> new ArrayList<>());
         recipes.add(recipe);
      }
   }

   @UnmodifiableView
   public List<R> get(Object ingredientUid) {
      List<R> recipes = this.uidToRecipes.get(ingredientUid);
      return recipes == null ? Collections.emptyList() : Collections.unmodifiableList(recipes);
   }

   public void compact() {
      this.uidToRecipes.values().forEach(ArrayList::trimToSize);
   }
}
