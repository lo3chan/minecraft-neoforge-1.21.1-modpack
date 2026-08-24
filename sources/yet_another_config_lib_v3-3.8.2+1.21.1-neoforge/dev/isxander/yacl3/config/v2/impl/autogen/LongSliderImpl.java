package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.LongSlider;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class LongSliderImpl extends SimpleOptionFactory<LongSlider, Long> {
   protected ControllerBuilder<Long> createController(LongSlider annotation, ConfigField<Long> field, OptionAccess storage, Option<Long> option) {
      return LongSliderControllerBuilder.create(option).formatValue(v -> {
         String key = this.getTranslationKey(field, "fmt." + v);
         if (Language.getInstance().has(key)) {
            return Component.translatable(key);
         } else {
            key = this.getTranslationKey(field, "fmt");
            return Language.getInstance().has(key) ? Component.translatable(key, new Object[]{v}) : Component.literal(Long.toString(v));
         }
      }).range(annotation.min(), annotation.max()).step(annotation.step());
   }
}
