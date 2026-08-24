package fuzs.puzzleslib.api.config.v3;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public interface ConfigCore {
   default void addToBuilder(Builder builder, ValueCallback callback) {
   }

   default void afterConfigReload() {
   }
}
