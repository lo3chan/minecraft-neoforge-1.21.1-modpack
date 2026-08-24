package net.mehvahdjukaar.moonlight.api.fluids.platform;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.moonlight.api.misc.Triplet;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class SoftFluidImpl {
   public static Pair<Integer, Component> getFluidSpecificAttributes(Fluid fluid) {
      FluidType type = fluid.getFluidType();
      int l = type.getLightLevel();
      Component tr = type.getDescription();
      return Pair.of(l, tr);
   }

   public static Triplet<ResourceLocation, ResourceLocation, Integer> getRenderingData(ResourceLocation useTexturesFrom) {
      Fluid f = (Fluid)BuiltInRegistries.FLUID.getOptional(useTexturesFrom).orElse(null);
      if (f != null && f != Fluids.EMPTY) {
         IClientFluidTypeExtensions prop = IClientFluidTypeExtensions.of(f);
         if (prop != IClientFluidTypeExtensions.DEFAULT) {
            FluidStack s = new FluidStack(f, 1000);
            ResourceLocation still = prop.getStillTexture(s);
            ResourceLocation flowing = prop.getFlowingTexture(s);
            int tint = prop.getTintColor(s);
            if (still != null && flowing != null) {
               return Triplet.of(still, flowing, tint);
            }

            Moonlight.LOGGER.warn("Fluid {} returned null on its textures. Its soft fluid might not render well", useTexturesFrom);
            return null;
         }
      }

      return null;
   }
}
