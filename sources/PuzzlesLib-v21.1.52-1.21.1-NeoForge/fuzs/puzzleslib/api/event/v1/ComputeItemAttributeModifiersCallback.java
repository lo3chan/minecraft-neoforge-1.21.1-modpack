package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

@FunctionalInterface
public interface ComputeItemAttributeModifiersCallback {
   EventInvoker<ComputeItemAttributeModifiersCallback> EVENT = EventInvoker.lookup(ComputeItemAttributeModifiersCallback.class);

   void onComputeItemAttributeModifiers(Item var1, List<Entry> var2);
}
