package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@FunctionalInterface
public interface RegisterPotionBrewingMixesCallback {
   EventInvoker<RegisterPotionBrewingMixesCallback> EVENT = EventInvoker.lookup(RegisterPotionBrewingMixesCallback.class);

   void onRegisterPotionBrewingMixes(RegisterPotionBrewingMixesCallback.Builder var1);

   public interface Builder {
      void registerPotionContainer(PotionItem var1);

      default void registerContainerRecipe(PotionItem from, Item item, PotionItem to) {
         this.registerContainerRecipe(from, Ingredient.of(new ItemLike[]{item}), to);
      }

      void registerContainerRecipe(PotionItem var1, Ingredient var2, PotionItem var3);

      default void registerPotionRecipe(Holder<Potion> from, Item item, Holder<Potion> to) {
         this.registerPotionRecipe(from, Ingredient.of(new ItemLike[]{item}), to);
      }

      void registerPotionRecipe(Holder<Potion> var1, Ingredient var2, Holder<Potion> var3);

      default void registerStartPotionRecipe(Item item, Holder<Potion> to) {
         this.registerStartPotionRecipe(Ingredient.of(new ItemLike[]{item}), to);
      }

      default void registerStartPotionRecipe(Ingredient ingredient, Holder<Potion> to) {
         this.registerPotionRecipe(Potions.WATER, ingredient, Potions.MUNDANE);
         this.registerPotionRecipe(Potions.AWKWARD, ingredient, to);
      }
   }
}
