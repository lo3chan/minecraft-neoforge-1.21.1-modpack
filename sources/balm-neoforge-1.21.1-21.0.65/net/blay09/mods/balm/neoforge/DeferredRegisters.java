package net.blay09.mods.balm.neoforge;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Collection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeferredRegisters {
   private static final Table<ResourceKey<?>, String, DeferredRegister<?>> deferredRegisters = Tables.synchronizedTable(HashBasedTable.create());

   public static <T> DeferredRegister<T> get(Registry<T> registry, String modId) {
      return get(registry.key(), modId);
   }

   public static <T> DeferredRegister<T> get(ResourceKey<? extends Registry<T>> registry, String modId) {
      DeferredRegister<?> register = (DeferredRegister<?>)deferredRegisters.get(registry, modId);
      if (register == null) {
         register = DeferredRegister.create(registry, modId);
         deferredRegisters.put(registry, modId, register);
      }

      return (DeferredRegister<T>)register;
   }

   public static Collection<DeferredRegister<?>> getByModId(String modId) {
      return deferredRegisters.column(modId).values();
   }

   public static void register(String modId, IEventBus modEventBus) {
      synchronized (deferredRegisters) {
         for (DeferredRegister<?> deferredRegister : getByModId(modId)) {
            deferredRegister.register(modEventBus);
         }
      }
   }
}
