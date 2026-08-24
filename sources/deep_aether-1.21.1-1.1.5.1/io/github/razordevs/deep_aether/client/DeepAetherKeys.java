package io.github.razordevs.deep_aether.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(
   modid = "deep_aether",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class DeepAetherKeys {
   public static final KeyMapping STRATUS_DASH_ABILITY = new KeyMapping("key.deep_aether.stratus_dash_ability.desc", 82, "key.aether.category");
   public static final KeyMapping SLIDER_EYE_SLAM_ABILITY = new KeyMapping("key.deep_aether.slider_eye_ability", 342, "key.aether.category");
   public static final ToggleKeyMapping TOGGLE_SKYJADE_TRANSPARENCY = new ToggleKeyMapping(
      "key.deep_aether.toggle_skyjade_transparency", 86, "key.aether.category", () -> true
   );

   @SubscribeEvent
   public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
      event.register(STRATUS_DASH_ABILITY);
      event.register(SLIDER_EYE_SLAM_ABILITY);
      event.register(TOGGLE_SKYJADE_TRANSPARENCY);
   }
}
