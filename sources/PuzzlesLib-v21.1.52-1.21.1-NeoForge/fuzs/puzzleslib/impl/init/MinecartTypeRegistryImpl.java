package fuzs.puzzleslib.impl.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.init.v3.MinecartTypeRegistry;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class MinecartTypeRegistryImpl implements MinecartTypeRegistry {
   private static final Map<Type, MinecartTypeRegistry.Factory> MINECART_FACTORIES = Maps.newConcurrentMap();

   @Override
   public void register(Type type, MinecartTypeRegistry.Factory factory) {
      Objects.requireNonNull(type, "type is null");
      Objects.requireNonNull(factory, "factory is null");
      MINECART_FACTORIES.put(type, factory);
   }

   public static Optional<AbstractMinecart> createMinecartForType(@Nullable Type type, Level level, double x, double y, double z) {
      return type != null && MINECART_FACTORIES.containsKey(type)
         ? Optional.of(MINECART_FACTORIES.get(type)).map(factory -> factory.create(level, x, y, z))
         : Optional.empty();
   }
}
