package io.github.razordevs.deep_aether.fluids;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class DAFluidTypes {
   public static final ResourceLocation POISON_STILL_RL = ResourceLocation.withDefaultNamespace("block/water_still");
   public static final ResourceLocation POISON_FLOWING_RL = ResourceLocation.withDefaultNamespace("block/water_flow");
   public static final ResourceLocation POISON_OVERLAY_RL = ResourceLocation.withDefaultNamespace("block/water_overlay");
   public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, "deep_aether");
   public static final Holder<FluidType> POISON_FLUID_TYPE = register(
      "poison_fluid", Properties.create().descriptionId("block.deep_aether.poison").canSwim(false)
   );

   private static Holder<FluidType> register(String name, Properties properties) {
      return FLUID_TYPES.register(name, () -> new FluidType(properties));
   }
}
