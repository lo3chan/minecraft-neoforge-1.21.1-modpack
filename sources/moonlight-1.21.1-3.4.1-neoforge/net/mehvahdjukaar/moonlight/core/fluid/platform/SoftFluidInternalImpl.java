package net.mehvahdjukaar.moonlight.core.fluid.platform;

import java.util.Map;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing;

public class SoftFluidInternalImpl {
   public static void registerExistingVanillaFluids(RegistryAccess ra, Map<Fluid, Holder<SoftFluid>> fluidMap, Map<Item, Holder<SoftFluid>> itemMap) {
      MappedRegistry<SoftFluid> reg = (MappedRegistry<SoftFluid>)SoftFluidRegistry.get(ra);
      reg.unfreeze();

      for (Fluid f : BuiltInRegistries.FLUID) {
         try {
            if (f != null
               && !(f instanceof FlowingFluid flowingFluid && flowingFluid.getSource() != f)
               && !(f instanceof Flowing)
               && f != Fluids.EMPTY
               && !fluidMap.containsKey(f)) {
               SoftFluid sf = new SoftFluid(BuiltInRegistries.FLUID.wrapAsHolder(f));
               Registry.register(reg, Utils.getID(f), sf);
               Holder<SoftFluid> holder = reg.wrapAsHolder(sf);
               fluidMap.put(f, holder);
               Item bucket = f.getBucket();
               if (bucket != Items.AIR) {
                  itemMap.put(bucket, holder);
               }
            }
         } catch (Exception var9) {
         }
      }

      reg.freeze();
   }
}
