package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;

@FunctionalInterface
public interface FinalizeItemComponentsCallback {
   EventInvoker<FinalizeItemComponentsCallback> EVENT = EventInvoker.lookup(FinalizeItemComponentsCallback.class);

   void onFinalizeItemComponents(Item var1, Consumer<Function<DataComponentMap, DataComponentPatch>> var2);
}
