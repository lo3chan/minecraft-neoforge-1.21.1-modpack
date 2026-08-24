package net.mehvahdjukaar.moonlight.api.misc.fake_level;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiFunction;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.VisibleForTesting;
import org.jetbrains.annotations.ApiStatus.Internal;

public class FakeLevelManager {
   protected static final Map<String, Level> INSTANCES = new Object2ObjectArrayMap();

   @Internal
   @VisibleForTesting
   public static void invalidateAll() {
      new ArrayList<>(INSTANCES.values()).forEach(FakeLevelManager::invalidate);
   }

   public static boolean invalidate(Level level) {
      boolean removed = INSTANCES.entrySet().removeIf(e -> e.getValue() == level);
      if (level != null) {
         PlatHelper.invokeLevelUnload(level);
      }

      try {
         if (level instanceof FakeServerLevel) {
            level.close();
         }
      } catch (Exception var3) {
         if (PlatHelper.isDev()) {
            throw new RuntimeException(var3);
         }

         Moonlight.LOGGER.error("An error occurred while closing fake level", var3);
      }

      return removed;
   }

   @Deprecated(
      forRemoval = true
   )
   public static void invalidate(String name) {
   }

   public static FakeLevel getDefaultClient(Level original) {
      return getClient("dummy_world", original, FakeLevel::new);
   }

   public static <T extends FakeLevel> T getClient(String id, Level original, BiFunction<String, RegistryAccess, FakeLevel> constructor) {
      id = "client_" + id;
      return (T)INSTANCES.computeIfAbsent(id, k -> constructor.apply(id, original.registryAccess()));
   }

   public static FakeServerLevel getDefaultServer(ServerLevel original) {
      return getServer("dummy_world", original, FakeServerLevel::new);
   }

   public static <T extends FakeServerLevel> T getServer(String id, ServerLevel original, BiFunction<String, ServerLevel, FakeServerLevel> constructor) {
      id = "server_" + id;
      return (T)INSTANCES.computeIfAbsent(id, k -> (Level)constructor.apply(id, original));
   }

   public static Level get(
      String id, Level original, BiFunction<String, RegistryAccess, FakeLevel> clientConstr, BiFunction<String, ServerLevel, FakeServerLevel> serverConstr
   ) {
      return original instanceof ServerLevel sl ? getServer(id, sl, serverConstr) : getClient(id, original, clientConstr);
   }

   public static Level getDefault(Level original) {
      return (Level)(original instanceof ServerLevel sl ? getDefaultServer(sl) : getDefaultClient(original));
   }

   public interface ILevelLike {
      Level cast();
   }
}
