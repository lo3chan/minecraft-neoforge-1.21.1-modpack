package vectorwing.farmersdelight.common.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import vectorwing.farmersdelight.common.registry.ModLootFunctions;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SmokerCookFunction extends LootItemConditionalFunction {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "smoker_cook");
   public static final MapCodec<SmokerCookFunction> CODEC = RecordCodecBuilder.mapCodec(
      p_298131_ -> commonFields(p_298131_).apply(p_298131_, SmokerCookFunction::new)
   );

   protected SmokerCookFunction(List<LootItemCondition> conditionsIn) {
      super(conditionsIn);
   }

   protected ItemStack run(ItemStack stack, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         Optional<RecipeHolder<SmokingRecipe>> recipe = context.getLevel()
            .getRecipeManager()
            .getAllRecipesFor(RecipeType.SMOKING)
            .stream()
            .filter(r -> ((Ingredient)((SmokingRecipe)r.value()).getIngredients().get(0)).test(stack))
            .findFirst();
         if (recipe.isPresent()) {
            ItemStack resultStack = ((SmokingRecipe)recipe.get().value()).getResultItem(context.getLevel().registryAccess()).copy();
            resultStack.setCount(resultStack.getCount() * stack.getCount());
            return resultStack;
         } else {
            return stack;
         }
      }
   }

   public LootItemFunctionType<SmokerCookFunction> getType() {
      return ModLootFunctions.SMOKER_COOK.get();
   }
}
