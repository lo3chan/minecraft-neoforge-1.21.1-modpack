package vectorwing.farmersdelight.integration.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.ingredient.condition.IngredientConditions;
import com.blamejared.crafttweaker.api.ingredient.transformer.IngredientTransformers;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.MCItemStackMutable;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.ItemAbility;
import org.openzen.zencode.java.ZenCodeType.Caster;
import org.openzen.zencode.java.ZenCodeType.Expansion;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;
import vectorwing.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient;

@Document("mods/FarmersDelight/ItemAbilityIngredient")
@ZenRegister
@Name("mods.farmersdelight.ItemAbilityIngredient")
public class CTItemAbilityIngredient implements IIngredient {
   public static final String PREFIX = "toolingredient";
   private final ItemAbilityIngredient ingredient;
   private final IngredientConditions conditions = new IngredientConditions();
   private final IngredientTransformers transformers = new IngredientTransformers();

   public CTItemAbilityIngredient(ItemAbility itemAbility) {
      this(new ItemAbilityIngredient(itemAbility));
   }

   public CTItemAbilityIngredient(ItemAbilityIngredient ingredient) {
      this.ingredient = ingredient;
   }

   public boolean matches(IItemStack stack) {
      return this.ingredient.test(stack.getInternal());
   }

   public Ingredient asVanillaIngredient() {
      return this.ingredient.toVanilla();
   }

   public String getCommandString() {
      return "<toolingredient:" + this.ingredient.getItemAbility().name() + ">";
   }

   public IItemStack[] getItems() {
      ItemStack[] stacks = (ItemStack[])this.ingredient.getItems().toArray();
      IItemStack[] out = new IItemStack[stacks.length];

      for (int i = 0; i < stacks.length; i++) {
         out[i] = new MCItemStackMutable(stacks[i]);
      }

      return out;
   }

   public IngredientTransformers transformers() {
      return this.transformers;
   }

   public IngredientConditions conditions() {
      return this.conditions;
   }

   @Expansion("crafttweaker.neoforge.api.item.ItemAbility")
   @ZenRegister
   public static class ExpandItemAbility {
      @Method
      @Caster(
         implicit = true
      )
      public static IIngredient asIIngredient(ItemAbility internal) {
         return new CTItemAbilityIngredient(internal);
      }
   }
}
