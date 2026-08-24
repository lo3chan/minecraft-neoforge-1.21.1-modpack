package fuzs.puzzleslib.api.event.v1.core;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.event.core.EventPhaseImpl;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface EventPhase {
   EventPhase DEFAULT = new EventPhaseImpl(ResourceLocationHelper.fromNamespaceAndPath("fabric", "default"), null, null);
   EventPhase BEFORE = new EventPhaseImpl(PuzzlesLibMod.id("before"), DEFAULT, EventPhaseImpl.Ordering.BEFORE);
   EventPhase AFTER = new EventPhaseImpl(PuzzlesLibMod.id("after"), DEFAULT, EventPhaseImpl.Ordering.AFTER);
   EventPhase FIRST = new EventPhaseImpl(PuzzlesLibMod.id("first"), BEFORE, EventPhaseImpl.Ordering.BEFORE);
   EventPhase LAST = new EventPhaseImpl(PuzzlesLibMod.id("last"), AFTER, EventPhaseImpl.Ordering.AFTER);

   ResourceLocation resourceLocation();

   EventPhase parent();

   default void applyOrdering(BiConsumer<ResourceLocation, ResourceLocation> phaseOrderingConsumer) {
      this.applyOrdering(this.resourceLocation(), phaseOrderingConsumer);
   }

   void applyOrdering(ResourceLocation var1, BiConsumer<ResourceLocation, ResourceLocation> var2);

   int getOrderingValue();

   static EventPhase early(EventPhase eventPhase) {
      return new EventPhaseImpl(PuzzlesLibMod.id("early_" + eventPhase.resourceLocation().getPath()), eventPhase, EventPhaseImpl.Ordering.BEFORE);
   }

   static EventPhase late(EventPhase eventPhase) {
      return new EventPhaseImpl(PuzzlesLibMod.id("late_" + eventPhase.resourceLocation().getPath()), eventPhase, EventPhaseImpl.Ordering.AFTER);
   }
}
