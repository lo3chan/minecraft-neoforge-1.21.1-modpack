package com.seibel.distanthorizons.common.wrappers.misc;

import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IMutableBlockPosWrapper;
import net.minecraft.core.BlockPos.MutableBlockPos;

public class MutableBlockPosWrapper_neoforge implements IMutableBlockPosWrapper {
   public final MutableBlockPos pos = new MutableBlockPos();

   @Override
   public Object getWrappedMcObject() {
      return this.pos;
   }
}
