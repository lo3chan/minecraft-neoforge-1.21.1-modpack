package dev.worldgen.deeper.oceans.config;

import dev.worldgen.apollib.client.gui.ApollibConfigScreen;
import dev.worldgen.deeper.oceans.DeeperOceans;
import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenBuilder {
   public static Screen build(Screen parent) {
      return new ApollibConfigScreen(
         "deeper_oceans",
         parent,
         DeeperOceans.CONFIG,
         helper -> {
            ConfigState state = (ConfigState)helper.screen().modifiedState;
            helper.doubleSlider("depth_multiplier", 1.0F, 5.0F, 0.05F, value -> state.depthMultiplier = value.floatValue(), (float)state.depthMultiplier)
               .withTooltip(2.0)
               .addBig();
            helper.intSlider("monument_offset", -60, 0, 1, value -> state.monumentOffset = value.intValue(), state.monumentOffset).withTooltip(-20).addBig();
            helper.booleanButton("disable_deep_ocean_trial_chambers", value -> state.disableDeepOceanTrialChambers = value, state.disableDeepOceanTrialChambers)
               .withTooltip(true)
               .addBig();
         }
      );
   }
}
