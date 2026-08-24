package net.joefoxe.hexerei.integration.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.PotionBottleTypeData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class PotionFluidSubtypeInterpreter implements ISubtypeInterpreter<FluidStack> {
   @Nullable
   public Object getSubtypeData(FluidStack ingredient, UidContext context) {
      return ingredient.getComponentsPatch();
   }

   public String getLegacyStringSubtypeInfo(FluidStack ingredient, UidContext context) {
      PotionContents potion = (PotionContents)ingredient.get(DataComponents.POTION_CONTENTS);
      if (potion == null) {
         return "";
      } else {
         String potionTypeString = Potion.getName(((PotionContents)ingredient.get(DataComponents.POTION_CONTENTS)).potion(), "");
         String bottleType = ((PotionBottleTypeData)ingredient.getOrDefault(ModDataComponents.POTION_BOTTLE_TYPE, PotionBottleTypeData.EMPTY))
            .bottleType()
            .toString();
         StringBuilder stringBuilder = new StringBuilder(potionTypeString);
         stringBuilder.append(";").append(bottleType);
         Iterable<MobEffectInstance> effects = potion.getAllEffects();

         for (MobEffectInstance effect : potion.getAllEffects()) {
            stringBuilder.append(";").append(effect);
         }

         for (MobEffectInstance effect : effects) {
            stringBuilder.append(";").append(effect);
         }

         return stringBuilder.toString();
      }
   }
}
