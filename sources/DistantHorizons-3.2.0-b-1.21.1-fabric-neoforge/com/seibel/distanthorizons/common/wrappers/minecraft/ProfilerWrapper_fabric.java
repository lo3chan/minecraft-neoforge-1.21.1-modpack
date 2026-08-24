package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import net.minecraft.class_3695;

public class ProfilerWrapper_fabric implements IProfilerWrapper {
   public class_3695 profiler;

   public ProfilerWrapper_fabric(class_3695 newProfiler) {
      this.profiler = newProfiler;
   }

   @Override
   public IProfilerWrapper.IProfileBlock push(String newSection) {
      this.profiler.method_15396(newSection);
      return new ProfilerWrapper$ProfileBlock_fabric(this.profiler);
   }

   @Override
   public void popPush(String newSection) {
      this.profiler.method_15405(newSection);
   }
}
