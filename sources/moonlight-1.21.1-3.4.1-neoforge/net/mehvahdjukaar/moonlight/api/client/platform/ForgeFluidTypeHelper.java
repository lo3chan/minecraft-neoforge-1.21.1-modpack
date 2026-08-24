package net.mehvahdjukaar.moonlight.api.client.platform;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.fluids.ModFlowingFluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;

@EventBusSubscriber(
   modid = "moonlight",
   value = {Dist.CLIENT}
)
public class ForgeFluidTypeHelper {
   private static final List<Pair<ModFlowingFluid, FluidType>> flowingFluids = new ArrayList<>();

   public static FluidType create(ModFlowingFluid.Properties properties, ModFlowingFluid fluid) {
      FluidType type = create(properties);
      flowingFluids.add(Pair.of(fluid, type));
      return type;
   }

   @SubscribeEvent
   public static void registerFluidExtensions(RegisterClientExtensionsEvent event) {
      for (Pair<ModFlowingFluid, FluidType> e : flowingFluids) {
         ModFlowingFluid flowingFluid = (ModFlowingFluid)e.getFirst();
         FluidType fluidType = (FluidType)e.getSecond();
         event.registerFluidType((IClientFluidTypeExtensions)flowingFluid.createRenderProperties(), new FluidType[]{fluidType});
      }
   }

   private static FluidType create(ModFlowingFluid.Properties properties) {
      return new FluidType(
         Properties.create()
            .adjacentPathType(properties.adjacentPathType)
            .canExtinguish(properties.canExtinguish)
            .fallDistanceModifier(properties.fallDistanceModifier)
            .pathType(properties.pathType)
            .canConvertToSource(properties.canConvertToSource)
            .supportsBoating(properties.supportsBoating)
            .canDrown(properties.canDrown)
            .canHydrate(properties.canHydrate)
            .lightLevel(properties.lightLevel)
            .canPushEntity(properties.canPushEntity)
            .density(properties.density)
            .temperature(properties.temperature)
            .viscosity(properties.viscosity)
            .rarity(properties.rarity)
            .descriptionId(properties.descriptionId)
            .motionScale(properties.motionScale)
            .canSwim(properties.canSwim)
      );
   }
}
