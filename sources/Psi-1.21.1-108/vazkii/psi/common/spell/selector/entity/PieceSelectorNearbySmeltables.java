package vazkii.psi.common.spell.selector.entity;

import java.util.function.Predicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

public class PieceSelectorNearbySmeltables extends PieceSelectorNearby {
   public PieceSelectorNearbySmeltables(Spell spell) {
      super(spell);
   }

   public static ItemStack simulateSmelt(Level world, ItemStack input) {
      return world.getRecipeManager()
         .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), world)
         .map(foo -> ((SmeltingRecipe)foo.value()).getResultItem(RegistryAccess.EMPTY))
         .orElse(ItemStack.EMPTY);
   }

   @Override
   public Predicate<Entity> getTargetPredicate(SpellContext context) {
      return this::accept;
   }

   public boolean accept(Entity e) {
      return e instanceof ItemEntity eitem ? !simulateSmelt(e.getCommandSenderWorld(), eitem.getItem()).isEmpty() : false;
   }
}
