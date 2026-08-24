package fuzs.puzzleslib.api.data.v2.core;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;

public interface RegistriesDataProvider {
   CompletableFuture<Provider> getRegistries();
}
