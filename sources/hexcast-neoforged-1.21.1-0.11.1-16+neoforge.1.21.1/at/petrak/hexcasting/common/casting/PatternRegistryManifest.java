package at.petrak.hexcasting.common.casting;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.server.ScrungledPatternsSave;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class PatternRegistryManifest {
   private static final ConcurrentMap<String, ResourceKey<ActionRegistryEntry>> NORMAL_ACTION_LOOKUP = new ConcurrentHashMap<>();

   public static void processRegistry(@Nullable ServerLevel overworld) {
      int perWorldActionCount = 0;
      Registry<ActionRegistryEntry> registry = IXplatAbstractions.INSTANCE.getActionRegistry();

      for (ResourceKey<ActionRegistryEntry> key : registry.registryKeySet()) {
         ActionRegistryEntry entry = (ActionRegistryEntry)registry.get(key);
         if (!HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN)) {
            NORMAL_ACTION_LOOKUP.put(entry.prototype().anglesSignature(), key);
         } else {
            perWorldActionCount++;
         }
      }

      HexAPI.LOGGER
         .info(
            "We're on the %s! Loaded %d regular actions, %d per-world actions, and %d special handlers"
               .formatted(
                  overworld == null ? "client" : "server",
                  NORMAL_ACTION_LOOKUP.size(),
                  perWorldActionCount,
                  IXplatAbstractions.INSTANCE.getSpecialHandlerRegistry().size()
               )
         );
   }

   @Nullable
   public static Pair<SpecialHandler, ResourceKey<SpecialHandler.Factory<?>>> matchPatternToSpecialHandler(HexPattern pat) {
      Registry<SpecialHandler.Factory<?>> registry = IXplatAbstractions.INSTANCE.getSpecialHandlerRegistry();

      for (ResourceKey<SpecialHandler.Factory<?>> key : registry.registryKeySet()) {
         SpecialHandler.Factory<?> factory = (SpecialHandler.Factory<?>)registry.get(key);

         try {
            SpecialHandler handler = factory.tryMatch(pat);
            if (handler != null) {
               return Pair.of(handler, key);
            }
         } catch (RuntimeException var6) {
            HexAPI.LOGGER.error("Special pattern handler {} failed to inspect pattern {}", key.location(), pat.anglesSignature(), var6);
         }
      }

      return null;
   }

   public static PatternShapeMatch matchPattern(HexPattern pat, ServerLevel overworld, boolean checkForAlternateStrokeOrders) {
      String sig = pat.anglesSignature();
      if (NORMAL_ACTION_LOOKUP.containsKey(sig)) {
         ResourceKey<ActionRegistryEntry> key = NORMAL_ACTION_LOOKUP.get(sig);
         return new PatternShapeMatch.Normal(key);
      } else {
         ScrungledPatternsSave perWorldPatterns = ScrungledPatternsSave.open(overworld);
         ScrungledPatternsSave.PerWorldEntry entry = perWorldPatterns.lookup(sig);
         if (entry != null) {
            return new PatternShapeMatch.PerWorld(entry.key(), true);
         } else {
            Pair<SpecialHandler, ResourceKey<SpecialHandler.Factory<?>>> shMatch = matchPatternToSpecialHandler(pat);
            return (PatternShapeMatch)(shMatch != null
               ? new PatternShapeMatch.Special((ResourceKey<SpecialHandler.Factory<?>>)shMatch.getSecond(), (SpecialHandler)shMatch.getFirst())
               : new PatternShapeMatch.Nothing());
         }
      }
   }

   @Nullable
   public static HexPattern getCanonicalStrokesPerWorld(ResourceKey<ActionRegistryEntry> key, ServerLevel overworld) {
      ScrungledPatternsSave perWorldPatterns = ScrungledPatternsSave.open(overworld);
      Pair<String, ScrungledPatternsSave.PerWorldEntry> pair = perWorldPatterns.lookupReverse(key);
      if (pair == null) {
         return null;
      } else {
         String sig = (String)pair.getFirst();
         ScrungledPatternsSave.PerWorldEntry entry = (ScrungledPatternsSave.PerWorldEntry)pair.getSecond();
         return HexPattern.fromAngles(sig, entry.canonicalStartDir());
      }
   }
}
