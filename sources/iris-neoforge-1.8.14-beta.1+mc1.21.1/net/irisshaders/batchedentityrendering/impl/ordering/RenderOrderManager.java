package net.irisshaders.batchedentityrendering.impl.ordering;

import java.util.List;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.minecraft.client.renderer.RenderType;

public interface RenderOrderManager {
   void begin(RenderType var1);

   void startGroup();

   boolean maybeStartGroup();

   boolean isInGroup();

   void endGroup();

   void reset();

   void resetType(TransparencyType var1);

   List<RenderType> getRenderOrder();
}
