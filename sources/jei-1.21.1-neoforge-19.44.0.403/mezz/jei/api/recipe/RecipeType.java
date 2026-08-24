package mezz.jei.api.recipe;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RecipeType<T> {
   private final ResourceLocation uid;
   private final Class<? extends T> recipeClass;

   public static <T> RecipeType<T> create(String nameSpace, String path, Class<? extends T> recipeClass) {
      ResourceLocation uid = ResourceLocation.fromNamespaceAndPath(nameSpace, path);
      return new RecipeType<>(uid, recipeClass);
   }

   public static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createFromVanilla(net.minecraft.world.item.crafting.RecipeType<R> vanillaRecipeType) {
      ResourceLocation uid = BuiltInRegistries.RECIPE_TYPE.getKey(vanillaRecipeType);
      if (uid == null) {
         throw new IllegalArgumentException("Vanilla Recipe Type must be registered before using it here. %s".formatted(vanillaRecipeType));
      } else {
         return createRecipeHolderType(uid);
      }
   }

   public static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createRecipeHolderType(ResourceLocation uid) {
      Class<? extends RecipeHolder<R>> holderClass = RecipeHolder.class;
      return new RecipeType<>(uid, holderClass);
   }

   public static <R extends Recipe<?>> Supplier<RecipeType<RecipeHolder<R>>> createFromDeferredVanilla(
      Supplier<net.minecraft.world.item.crafting.RecipeType<R>> deferredVanillaRecipeType
   ) {
      return Suppliers.memoize(() -> createFromVanilla(deferredVanillaRecipeType.get()));
   }

   public RecipeType(ResourceLocation uid, Class<? extends T> recipeClass) {
      if (uid == null) {
         throw new NullPointerException("uid must not be null.");
      } else if (recipeClass == null) {
         throw new NullPointerException("recipeClass must not be null.");
      } else {
         this.uid = uid;
         this.recipeClass = recipeClass;
      }
   }

   public ResourceLocation getUid() {
      return this.uid;
   }

   public Class<? extends T> getRecipeClass() {
      return this.recipeClass;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof RecipeType<?> other) ? false : this.recipeClass == other.recipeClass && this.uid.equals(other.uid);
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.uid.hashCode() + this.recipeClass.hashCode();
   }

   @Override
   public String toString() {
      return "RecipeType[uid=" + this.uid + ", recipeClass=" + this.recipeClass + "]";
   }
}
