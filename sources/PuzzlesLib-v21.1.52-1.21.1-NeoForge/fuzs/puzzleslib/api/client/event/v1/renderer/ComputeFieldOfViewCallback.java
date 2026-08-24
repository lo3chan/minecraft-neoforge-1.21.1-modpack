package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@FunctionalInterface
public interface ComputeFieldOfViewCallback {
   EventInvoker<ComputeFieldOfViewCallback> EVENT = EventInvoker.lookup(ComputeFieldOfViewCallback.class);

   void onComputeFieldOfView(GameRenderer var1, Camera var2, float var3, MutableDouble var4);
}
