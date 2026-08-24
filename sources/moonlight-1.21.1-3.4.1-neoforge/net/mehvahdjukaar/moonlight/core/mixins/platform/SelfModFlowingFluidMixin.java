package net.mehvahdjukaar.moonlight.core.mixins.platform;

import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.client.platform.ForgeFluidTypeHelper;
import net.mehvahdjukaar.moonlight.api.fluids.ModFlowingFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ModFlowingFluid.class})
public abstract class SelfModFlowingFluidMixin extends FlowingFluid {
   @Unique
   private Supplier<FluidType> type;

   @Overwrite(
      remap = false
   )
   private void afterInit(ModFlowingFluid.Properties properties) {
      FluidType t = ForgeFluidTypeHelper.create(properties, (ModFlowingFluid)this);
      this.type = () -> t;
   }

   public FluidType getFluidType() {
      return this.type.get();
   }
}
