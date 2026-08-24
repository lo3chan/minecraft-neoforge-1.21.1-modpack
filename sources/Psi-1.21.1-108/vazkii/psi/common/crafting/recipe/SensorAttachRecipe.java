package vazkii.psi.common.crafting.recipe;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.exosuit.IExosuitSensor;
import vazkii.psi.api.exosuit.ISensorHoldable;
import vazkii.psi.common.crafting.ModCraftingRecipes;

public class SensorAttachRecipe extends CustomRecipe {
   public SensorAttachRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
      boolean foundSensor = false;
      boolean foundTarget = false;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ISensorHoldable && ((ISensorHoldable)stack.getItem()).getAttachedSensor(stack).isEmpty()) {
               if (foundTarget) {
                  return false;
               }

               foundTarget = true;
            } else {
               if (!(stack.getItem() instanceof IExosuitSensor)) {
                  return false;
               }

               if (foundSensor) {
                  return false;
               }

               foundSensor = true;
            }
         }
      }

      return foundSensor && foundTarget;
   }

   @NotNull
   public ItemStack assemble(@NotNull CraftingInput inv, Provider pRegistries) {
      ItemStack sensor = ItemStack.EMPTY;
      ItemStack target = ItemStack.EMPTY;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getItem() instanceof IExosuitSensor) {
               sensor = stack;
            } else {
               target = stack;
            }
         }
      }

      ItemStack copy = target.copy();
      ISensorHoldable holdable = (ISensorHoldable)copy.getItem();
      holdable.attachSensor(copy, sensor);
      return copy;
   }

   @NotNull
   public RecipeType<?> getType() {
      return !DatagenModLoader.isRunningDataGen() ? RecipeType.CRAFTING : (RecipeType)ModCraftingRecipes.SENSOR_ATTACH_TYPE.get();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModCraftingRecipes.SENSOR_ATTACH_SERIALIZER.get();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }
}
