package com.seibel.distanthorizons.common.wrappers.misc;

import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IMutableBlockPosWrapper;
import net.minecraft.class_2338.class_2339;

public class MutableBlockPosWrapper_fabric implements IMutableBlockPosWrapper {
   public final class_2339 pos = new class_2339();

   @Override
   public Object getWrappedMcObject() {
      return this.pos;
   }
}
