package net.mehvahdjukaar.moonlight.core.fluid;

import java.util.IdentityHashMap;
import java.util.Map;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidColors;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.misc.SidedInstance;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.fluid.platform.SoftFluidInternalImpl;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundFinalizeFluidsMessage;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class SoftFluidInternal {
   private static final SidedInstance<Map<Fluid, Holder<SoftFluid>>> FLUID_MAP = SidedInstance.of(r -> {
      IdentityHashMap<Fluid, Holder<SoftFluid>> m = new IdentityHashMap<>();
      populateFluidSlaveMap(r, m);
      return m;
   });
   private static final SidedInstance<Map<Item, Holder<SoftFluid>>> ITEM_MAP = SidedInstance.of(r -> {
      IdentityHashMap<Item, Holder<SoftFluid>> m = new IdentityHashMap<>();
      populateItemSlaveMap(r, m);
      return m;
   });

   public static Holder<SoftFluid> fromVanillaFluid(Fluid fluid, Provider registryAccess) {
      return FLUID_MAP.get(registryAccess).get(fluid);
   }

   public static Holder<SoftFluid> fromVanillaItem(Item item, Provider registryAccess) {
      return ITEM_MAP.get(registryAccess).get(item);
   }

   private static void populateFluidSlaveMap(Provider registryAccess, Map<Fluid, Holder<SoftFluid>> fluidMap) {
      fluidMap.clear();

      for (Reference<SoftFluid> h : SoftFluidRegistry.get(registryAccess).listElements().toList()) {
         SoftFluid s = (SoftFluid)h.value();
         if (s.isEnabled()) {
            for (Holder<Fluid> eq : s.getEquivalentFluids()) {
               Fluid value = (Fluid)eq.value();
               if (value == Fluids.EMPTY) {
                  Moonlight.LOGGER.error("!!Invalid fluid for fluid. This is a bug! {}", h);
                  if (PlatHelper.isDev()) {
                     throw new AssertionError("Invalid fluid for fluid. This is a bug! " + h);
                  }
               }

               fluidMap.put(value, h);
            }

            s.getEquivalentFluids().forEach(f -> fluidMap.put((Fluid)f.value(), h));
         }
      }
   }

   private static void populateItemSlaveMap(Provider registryAccess, Map<Item, Holder<SoftFluid>> itemMap) {
      itemMap.clear();

      for (Reference<SoftFluid> h : SoftFluidRegistry.get(registryAccess).listElements().toList()) {
         SoftFluid s = (SoftFluid)h.value();
         if (s.isEnabled()) {
            s.getContainerList().getPossibleFilled().forEach(i -> {
               if (i != Items.POTION || !MLBuiltinSoftFluids.WATER.is(h)) {
                  if (i == Items.AIR) {
                     Moonlight.LOGGER.error("!!Invalid item for fluid. This is a bug! {}", h);
                     if (PlatHelper.isDev()) {
                        throw new AssertionError("Invalid item for fluid. This is a bug! " + h);
                     }
                  }

                  itemMap.put(i, h);
               }
            });
         }
      }
   }

   public static void init() {
      RegHelper.registerDataPackRegistry(SoftFluidRegistry.KEY, SoftFluid.CODEC, SoftFluid.CODEC);
   }

   public static void postInitClient(RegistryAccess ra) {
      FLUID_MAP.get(ra);
      ITEM_MAP.get(ra);
      Registry<SoftFluid> reg = SoftFluidRegistry.get(ra);

      for (SoftFluid f : reg) {
         f.afterInit();
      }

      SoftFluidColors.refreshParticleColors(reg);
   }

   public static void onDataSyncToPlayer(ServerPlayer player, boolean isJoined) {
      if (isJoined) {
         NetworkHelper.sendToClientPlayer(player, new ClientBoundFinalizeFluidsMessage());
      }
   }

   public static void doPostInitServer(RegistryAccess ra) {
      FLUID_MAP.get(ra);
      ITEM_MAP.get(ra);
      registerExistingVanillaFluids(ra, FLUID_MAP.get(ra), ITEM_MAP.get(ra));

      for (SoftFluid f : SoftFluidRegistry.get(ra)) {
         f.afterInit();
      }
   }

   private static void registerExistingVanillaFluids(RegistryAccess var0, Map<Fluid, Holder<SoftFluid>> var1, Map<Item, Holder<SoftFluid>> var2) {
      SoftFluidInternalImpl.registerExistingVanillaFluids(var0, var1, var2);
   }
}
