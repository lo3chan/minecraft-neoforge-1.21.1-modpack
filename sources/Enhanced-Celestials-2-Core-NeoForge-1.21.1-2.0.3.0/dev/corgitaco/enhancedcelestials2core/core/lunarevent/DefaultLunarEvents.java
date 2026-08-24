package dev.corgitaco.enhancedcelestials2core.core.lunarevent;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifier;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class DefaultLunarEvents {
   public static final Map<ResourceKey<LunarEvent>, DefaultLunarEvents.LunarEventFactory> LUNAR_EVENT_FACTORIES = new Reference2ObjectOpenHashMap();
   public static final ResourceKey<LunarEvent> DEFAULT = createEvent("default", context -> new LunarEvent(modifiers(context)));

   public static ResourceKey<LunarEvent> createEvent(String id, Function<BootstrapContext<LunarEvent>, LunarEvent> event) {
      ResourceKey<LunarEvent> lunarEventResourceKey = ResourceKey.create(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY, EnhancedCelestials.createLocation(id));
      LUNAR_EVENT_FACTORIES.put(lunarEventResourceKey, event::apply);
      return lunarEventResourceKey;
   }

   @SafeVarargs
   private static List<Holder<LunarEventModifier>> modifiers(BootstrapContext<LunarEvent> context, ResourceKey<LunarEventModifier>... modifierKeys) {
      HolderGetter<LunarEventModifier> modifierLookup = context.lookup(EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_KEY);
      return Arrays.stream(modifierKeys).<Holder<LunarEventModifier>>map(modifierLookup::getOrThrow).collect(Collectors.toUnmodifiableList());
   }

   public static void loadClass() {
   }

   @FunctionalInterface
   public interface LunarEventFactory {
      LunarEvent generate(BootstrapContext<LunarEvent> var1);
   }
}
