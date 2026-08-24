package software.bernie.geckolib.service;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;

public interface GeckoLibPlatform {
   boolean isDevelopmentEnvironment();

   boolean isPhysicalClient();

   Path getGameDir();

   <T> Supplier<DataComponentType<T>> registerDataComponent(String var1, UnaryOperator<Builder<T>> var2);
}
