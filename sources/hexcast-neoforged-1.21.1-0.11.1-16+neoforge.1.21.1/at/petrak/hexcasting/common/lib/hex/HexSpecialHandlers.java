package at.petrak.hexcasting.common.lib.hex;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.common.casting.actions.math.SpecialHandlerNumberLiteral;
import at.petrak.hexcasting.common.casting.actions.stack.SpecialHandlerMask;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;

public class HexSpecialHandlers {
   private static final Map<ResourceLocation, SpecialHandler.Factory<?>> SPECIAL_HANDLERS = new LinkedHashMap<>();
   public static final SpecialHandler.Factory<SpecialHandlerNumberLiteral> NUMBER = make("number", new SpecialHandlerNumberLiteral.Factory());
   public static final SpecialHandler.Factory<SpecialHandlerMask> MASK = make("mask", new SpecialHandlerMask.Factory());

   private static <T extends SpecialHandler> SpecialHandler.Factory<T> make(String name, SpecialHandler.Factory<T> handler) {
      SpecialHandler.Factory<?> old = SPECIAL_HANDLERS.put(HexAPI.modLoc(name), handler);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         return handler;
      }
   }

   public static void register(BiConsumer<SpecialHandler.Factory<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, SpecialHandler.Factory<?>> e : SPECIAL_HANDLERS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }
}
