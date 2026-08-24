package software.bernie.geckolib.platform;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.service.GeckoLibPlatform;

public final class GeckoLibNeoForge implements GeckoLibPlatform {
   @Override
   public boolean isDevelopmentEnvironment() {
      return !FMLEnvironment.production;
   }

   @Override
   public Path getGameDir() {
      return FMLPaths.GAMEDIR.get();
   }

   @Override
   public boolean isPhysicalClient() {
      return FMLEnvironment.dist.isClient();
   }

   @Override
   public <T> Supplier<DataComponentType<T>> registerDataComponent(String id, UnaryOperator<Builder<T>> builder) {
      return GeckoLib.DATA_COMPONENTS_REGISTER.registerComponentType(id, builder);
   }
}
