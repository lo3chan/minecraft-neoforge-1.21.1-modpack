package com.nyfaria.nyfsspiders.registration.neoforge;

import java.util.Objects;
import java.util.ServiceLoader;
import javax.annotation.Nullable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.javafmlmod.FMLModContainer;

public interface NeoForgeBusGetter {
   @Nullable
   IEventBus getModEventBus(ModContainer var1);

   @Nullable
   static IEventBus getBus(ModContainer container) {
      return ServiceLoader.load(NeoForgeBusGetter.class)
         .stream()
         .map(p -> p.get().getModEventBus(container))
         .filter(Objects::nonNull)
         .findFirst()
         .orElseGet(() -> container instanceof FMLModContainer fmlModContainer ? fmlModContainer.getEventBus() : null);
   }
}
