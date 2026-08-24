package mezz.jei.library.ingredients.itemStacks;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.time.Duration;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public abstract class TypedItemStack implements ITypedIngredient<ItemStack> {
   private static final LoadingCache<TypedItemStack, ItemStack> CACHE = CacheBuilder.newBuilder()
      .expireAfterAccess(Duration.ofSeconds(1L))
      .concurrencyLevel(1)
      .build(new CacheLoader<TypedItemStack, ItemStack>() {
         public ItemStack load(TypedItemStack key) {
            return key.createItemStackUncached();
         }
      });

   public static ITypedIngredient<ItemStack> create(ItemStack ingredient) {
      return (ITypedIngredient<ItemStack>)(ingredient.getCount() == 1
         ? NormalizedTypedItemStack.create(ingredient.getItemHolder(), ingredient.getComponentsPatch())
         : new FullTypedItemStack(ingredient.getItemHolder(), ingredient.getComponentsPatch(), ingredient.getCount()));
   }

   public static ITypedIngredient<ItemStack> create(ItemLike itemLike) {
      Item item = itemLike.asItem();
      Reference<Item> itemHolder = item.builtInRegistryHolder();
      return new NormalizedTypedItem(itemHolder);
   }

   public static ITypedIngredient<ItemStack> normalize(ITypedIngredient<ItemStack> typedIngredient) {
      if (typedIngredient instanceof TypedItemStack typedItemStack) {
         return typedItemStack.getNormalized();
      } else {
         ItemStack itemStack = typedIngredient.getIngredient();
         return NormalizedTypedItemStack.create(itemStack.getItemHolder(), itemStack.getComponentsPatch());
      }
   }

   public final ItemStack getIngredient() {
      return (ItemStack)CACHE.getUnchecked(this);
   }

   @Override
   public final Optional<ItemStack> getItemStack() {
      return Optional.of(this.getIngredient());
   }

   @Override
   public final <B> B getBaseIngredient(IIngredientTypeWithSubtypes<B, ItemStack> ingredientType) {
      Item item = this.getItem();
      Class<? extends B> ingredientBaseClass = ingredientType.getIngredientBaseClass();
      return (B)ingredientBaseClass.cast(item);
   }

   @Override
   public final IIngredientType<ItemStack> getType() {
      return VanillaTypes.ITEM_STACK;
   }

   protected abstract Item getItem();

   protected abstract TypedItemStack getNormalized();

   protected abstract ItemStack createItemStackUncached();
}
