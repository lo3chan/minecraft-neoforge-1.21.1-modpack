package com.seibel.distanthorizons.core.render.renderer;

import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderObjectFactory;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.util.ArrayList;
import java.util.List;

public class GenericRenderObjectFactory implements IDhApiCustomRenderObjectFactory {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final GenericRenderObjectFactory INSTANCE = new GenericRenderObjectFactory();

   private GenericRenderObjectFactory() {
   }

   @Override
   public IDhApiRenderableBoxGroup createForSingleBox(String resourceLocation, DhApiRenderableBox box) {
      ArrayList<DhApiRenderableBox> list = new ArrayList<>();
      list.add(box);
      return this.createAbsolutePositionedGroup(resourceLocation, list);
   }

   @Override
   public IDhApiRenderableBoxGroup createRelativePositionedGroup(String resourceLocation, DhApiVec3d originBlockPos, List<DhApiRenderableBox> boxList) {
      return new RenderableBoxGroup(resourceLocation, new DhApiVec3d(originBlockPos.x, originBlockPos.y, originBlockPos.z), boxList, true);
   }

   @Override
   public IDhApiRenderableBoxGroup createAbsolutePositionedGroup(String resourceLocation, List<DhApiRenderableBox> boxList) {
      return new RenderableBoxGroup(resourceLocation, new DhApiVec3d(0.0, 0.0, 0.0), boxList, false);
   }
}
