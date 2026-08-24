package fuzs.puzzleslib.api.config.v3;

import java.util.function.Consumer;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

@FunctionalInterface
public interface ValueCallback {
   <S, V extends ConfigValue<S>> V accept(V var1, Consumer<S> var2);
}
