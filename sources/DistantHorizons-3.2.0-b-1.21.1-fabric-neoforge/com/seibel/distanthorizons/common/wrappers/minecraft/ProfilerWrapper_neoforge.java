package com.seibel.distanthorizons.common.wrappers.minecraft;

import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import net.minecraft.util.profiling.ProfilerFiller;

public class ProfilerWrapper_neoforge implements IProfilerWrapper {
   public ProfilerFiller profiler;

   public ProfilerWrapper_neoforge(ProfilerFiller newProfiler) {
      this.profiler = newProfiler;
   }

   @Override
   public IProfilerWrapper.IProfileBlock push(String newSection) {
      this.profiler.push(newSection);
      return new ProfilerWrapper$ProfileBlock_neoforge(this.profiler);
   }

   @Override
   public void popPush(String newSection) {
      this.profiler.popPush(newSection);
   }
}
