package net.blay09.mods.balm.neoforge.stats.internal;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.blay09.mods.balm.stats.internal.AbstractBalmCustomStatRegistrarImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class NeoForgeBalmCustomStatRegistrar extends AbstractBalmCustomStatRegistrarImpl {
   public NeoForgeBalmCustomStatRegistrar(BalmRegistrar registrar, String namespace) {
      super(registrar, namespace);
   }

   @Override
   public ResourceLocation register(ResourceLocation statIdentifier, StatFormatter formatter) {
      ResourceLocation stat = super.register(statIdentifier, formatter);
      ModBusEventRegisters.getRegistrations(this.namespace, NeoForgeBalmCustomStatRegistrar.Registrations.class).customStats.add(Pair.of(stat, formatter));
      return stat;
   }

   public static class Registrations {
      public final List<Pair<ResourceLocation, StatFormatter>> customStats = new ArrayList<>();

      @SubscribeEvent
      public void commonSetup(FMLCommonSetupEvent event) {
         event.enqueueWork(() -> this.customStats.forEach(it -> Stats.CUSTOM.get((ResourceLocation)it.getFirst(), (StatFormatter)it.getSecond())));
      }
   }
}
