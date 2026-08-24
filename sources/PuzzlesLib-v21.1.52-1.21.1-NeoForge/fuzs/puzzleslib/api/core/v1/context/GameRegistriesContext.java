package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.Registry;

public interface GameRegistriesContext {
   <T> void registerRegistry(Registry<T> var1);
}
