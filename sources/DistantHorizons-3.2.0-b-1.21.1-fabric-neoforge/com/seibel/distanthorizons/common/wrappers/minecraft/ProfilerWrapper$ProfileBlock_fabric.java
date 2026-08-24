package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import net.minecraft.class_3695;

public class ProfilerWrapper$ProfileBlock_fabric implements IProfilerWrapper.IProfileBlock {
   public class_3695 profiler;

   public ProfilerWrapper$ProfileBlock_fabric(class_3695 newProfiler) {
      this.profiler = newProfiler;
   }

   @Override
   public void close() {
      this.profiler.method_15407();
   }
}
