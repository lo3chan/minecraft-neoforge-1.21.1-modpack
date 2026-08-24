package dev.architectury.registry.registries;

import java.util.function.Consumer;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface RegistrySupplier<T> extends DeferredSupplier<T>, Holder<T> {
   RegistrarManager getRegistrarManager();

   Registrar<T> getRegistrar();

   default void listen(Consumer<T> callback) {
      this.getRegistrar().listen(this, callback);
   }
}
