package net.joefoxe.hexerei.fluid;

import com.mojang.datafixers.util.Pair;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.PotionBottleTypeData;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class PotionFluidHandler {
   public static final int POTION_MB_AMOUNT = 250;

   public static Pair<FluidStack, ItemStack> emptyPotion(ItemStack stack, boolean simulate) {
      FluidStack fluid = getFluidFromPotionItem(stack);
      if (!simulate) {
         stack.shrink(1);
      }

      return Pair.of(fluid, new ItemStack(Items.GLASS_BOTTLE));
   }

   public static FluidStack getFluidFromPotionItem(ItemStack stack) {
      PotionContents potion = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
      PotionFluid.BottleType bottleTypeFromItem = bottleTypeFromItem(stack.getItem());
      if (potion != null && (potion == PotionContents.EMPTY || potion.is(Potions.WATER)) && bottleTypeFromItem == PotionFluid.BottleType.REGULAR) {
         return new FluidStack(Fluids.WATER, 250);
      } else {
         FluidStack fluid = PotionFluid.of(250, potion);
         fluid.set(ModDataComponents.POTION_BOTTLE_TYPE, new PotionBottleTypeData(bottleTypeFromItem));
         return fluid;
      }
   }

   public static FluidStack getFluidFromPotion(Holder<Potion> potion, PotionFluid.BottleType bottleType, int amount) {
      if (potion == Potions.WATER && bottleType == PotionFluid.BottleType.REGULAR) {
         return new FluidStack(Fluids.WATER, amount);
      } else {
         FluidStack fluid = PotionFluid.of(amount, new PotionContents(potion));
         fluid.set(ModDataComponents.POTION_BOTTLE_TYPE, new PotionBottleTypeData(bottleType));
         return fluid;
      }
   }

   public static PotionFluid.BottleType bottleTypeFromItem(Item item) {
      if (item == Items.LINGERING_POTION) {
         return PotionFluid.BottleType.LINGERING;
      } else {
         return item == Items.SPLASH_POTION ? PotionFluid.BottleType.SPLASH : PotionFluid.BottleType.REGULAR;
      }
   }

   public static ItemLike itemFromBottleType(PotionFluid.BottleType type) {
      return switch (type) {
         case LINGERING -> Items.LINGERING_POTION;
         case SPLASH -> Items.SPLASH_POTION;
         case REGULAR -> Items.POTION;
      };
   }

   public static int getRequiredAmountForFilledBottle(ItemStack stack, FluidStack availableFluid) {
      return 250;
   }

   public static ItemStack fillBottle(FluidStack fluid) {
      ItemStack potionStack = new ItemStack(
         itemFromBottleType(((PotionBottleTypeData)fluid.getOrDefault(ModDataComponents.POTION_BOTTLE_TYPE, PotionBottleTypeData.EMPTY)).bottleType())
      );
      potionStack.set(DataComponents.POTION_CONTENTS, (PotionContents)fluid.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
      return potionStack;
   }
}
