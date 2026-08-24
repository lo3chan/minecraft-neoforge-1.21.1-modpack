package at.petrak.hexcasting.api.casting;

import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import net.minecraft.resources.ResourceKey;

public abstract sealed class PatternShapeMatch
   permits PatternShapeMatch.Nothing,
   PatternShapeMatch.Normal,
   PatternShapeMatch.PerWorld,
   PatternShapeMatch.Special {
   public static final class Normal extends PatternShapeMatch {
      public final ResourceKey<ActionRegistryEntry> key;

      public Normal(ResourceKey<ActionRegistryEntry> key) {
         this.key = key;
      }
   }

   public static final class Nothing extends PatternShapeMatch {
   }

   public static final class PerWorld extends PatternShapeMatch {
      public final ResourceKey<ActionRegistryEntry> key;
      public final boolean certain;

      public PerWorld(ResourceKey<ActionRegistryEntry> key, boolean certain) {
         this.key = key;
         this.certain = certain;
      }
   }

   public static final class Special extends PatternShapeMatch {
      public final ResourceKey<SpecialHandler.Factory<?>> key;
      public final SpecialHandler handler;

      public Special(ResourceKey<SpecialHandler.Factory<?>> key, SpecialHandler handler) {
         this.key = key;
         this.handler = handler;
      }
   }
}
