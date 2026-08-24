package net.mehvahdjukaar.moonlight.api.fluids.platform;

import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;

public class SoftFluidColorsImpl {
   public static int getSpecialColor(SoftFluidStack stack, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
      DyedItemColor dyeColor = (DyedItemColor)stack.get(DataComponents.DYED_COLOR);
      if (dyeColor != null) {
         return dyeColor.rgb();
      } else {
         PotionContents potionContents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
         if (potionContents != null) {
            return potionContents.getColor();
         } else {
            DyeColor discreteDyeColor = (DyeColor)stack.get(DataComponents.BASE_COLOR);
            if (discreteDyeColor != null) {
               return discreteDyeColor.getTextureDiffuseColor();
            } else {
               int specialColor = 0;
               Holder<Fluid> f = stack.getVanillaFluid();
               if (!Fluids.EMPTY.isSame((Fluid)f.value())) {
                  IClientFluidTypeExtensions prop = IClientFluidTypeExtensions.of((Fluid)f.value());
                  if (prop != IClientFluidTypeExtensions.DEFAULT) {
                     int w = -1;
                     if (stack instanceof SoftFluidStackImpl ss) {
                        w = prop.getTintColor(ss.toForgeFluid());
                     }

                     if (w != -1) {
                        specialColor = w;
                     } else if (world != null && pos != null) {
                        w = prop.getTintColor(((Fluid)f.value()).defaultFluidState(), world, pos);
                        if (w != -1) {
                           specialColor = w;
                        }
                     }
                  }
               }

               return specialColor;
            }
         }
      }
   }
}
