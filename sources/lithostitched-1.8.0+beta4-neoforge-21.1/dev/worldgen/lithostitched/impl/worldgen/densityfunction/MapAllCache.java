package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.Visitor;

public class MapAllCache {
   private static final ThreadLocal<MapAllCache.State> STATE = ThreadLocal.withInitial(MapAllCache.State::new);

   public static DensityFunction get(Visitor visitor, DensityFunction function) {
      MapAllCache.State state = STATE.get();
      Map<DensityFunction, DensityFunction> vCache = state.cache.get(visitor);
      return vCache != null ? vCache.get(function) : null;
   }

   public static void put(Visitor visitor, DensityFunction function, DensityFunction result) {
      MapAllCache.State state = STATE.get();
      state.cache.computeIfAbsent(visitor, k -> new IdentityHashMap<>()).put(function, result);
   }

   public static void push() {
      STATE.get().depth++;
   }

   public static void pop() {
      MapAllCache.State state = STATE.get();
      state.depth--;
      if (state.depth <= 0) {
         state.depth = 0;
         state.cache.clear();
      }
   }

   private static class State {
      final Map<Visitor, Map<DensityFunction, DensityFunction>> cache = new IdentityHashMap<>();
      int depth = 0;
   }
}
