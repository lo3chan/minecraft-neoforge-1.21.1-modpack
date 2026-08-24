package net.raphimc.immediatelyfast.feature.core;

public class ImmediatelyFastRuntimeConfig {
   public boolean hud_batching;
   public boolean font_atlas_resizing;
   public boolean fast_buffer_upload;
   public boolean experimental_screen_batching;

   public ImmediatelyFastRuntimeConfig(ImmediatelyFastConfig config) {
      this.hud_batching = config.hud_batching;
      this.font_atlas_resizing = config.font_atlas_resizing;
      this.fast_buffer_upload = config.fast_buffer_upload;
      this.experimental_screen_batching = config.experimental_screen_batching;
   }
}
