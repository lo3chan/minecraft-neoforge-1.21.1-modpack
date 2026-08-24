package net.mehvahdjukaar.moonlight.api.platform.configs;

import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IConfigValue<T> extends Supplier<T> {
   boolean setValue(T var1);

   ConfigReloadType reloadType();

   boolean affectsDynamicPacks();
}
