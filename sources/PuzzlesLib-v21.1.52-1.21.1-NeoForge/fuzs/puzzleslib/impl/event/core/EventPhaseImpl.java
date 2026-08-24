package fuzs.puzzleslib.impl.event.core;

import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;

public record EventPhaseImpl(ResourceLocation resourceLocation, EventPhase parent, EventPhaseImpl.Ordering ordering) implements EventPhase {
   @Override
   public void applyOrdering(ResourceLocation resourceLocation, BiConsumer<ResourceLocation, ResourceLocation> phaseOrderingConsumer) {
      Objects.requireNonNull(this.parent, "parent is null");
      Objects.requireNonNull(this.ordering, "ordering is null");
      this.ordering.apply(phaseOrderingConsumer, resourceLocation, this.parent.resourceLocation());
   }

   @Override
   public int getOrderingValue() {
      Objects.requireNonNull(this.ordering, "ordering is null");
      return this.ordering.value;
   }

   public static enum Ordering {
      BEFORE(-1) {
         @Override
         public void apply(BiConsumer<ResourceLocation, ResourceLocation> consumer, ResourceLocation first, ResourceLocation second) {
            consumer.accept(first, second);
         }
      },
      AFTER(1) {
         @Override
         public void apply(BiConsumer<ResourceLocation, ResourceLocation> consumer, ResourceLocation first, ResourceLocation second) {
            consumer.accept(second, first);
         }
      };

      public final int value;

      private Ordering(int value) {
         this.value = value;
      }

      public abstract void apply(BiConsumer<ResourceLocation, ResourceLocation> var1, ResourceLocation var2, ResourceLocation var3);
   }
}
