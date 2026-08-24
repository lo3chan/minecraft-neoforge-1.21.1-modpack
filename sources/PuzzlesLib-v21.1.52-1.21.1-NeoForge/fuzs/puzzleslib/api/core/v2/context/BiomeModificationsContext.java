package fuzs.puzzleslib.api.core.v2.context;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface BiomeModificationsContext {
   void registerBiomeModification(BiomeLoadingPhase var1, Predicate<BiomeLoadingContext> var2, Consumer<BiomeModificationContext> var3);
}
