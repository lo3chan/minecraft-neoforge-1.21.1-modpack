package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import net.minecraft.util.profiling.ProfilerFiller;

public class ProfilerWrapper$ProfileBlock_neoforge implements IProfilerWrapper.IProfileBlock {
   public ProfilerFiller profiler;

   public ProfilerWrapper$ProfileBlock_neoforge(ProfilerFiller newProfiler) {
      this.profiler = newProfiler;
   }

   @Override
   public void close() {
      this.profiler.pop();
   }
}
