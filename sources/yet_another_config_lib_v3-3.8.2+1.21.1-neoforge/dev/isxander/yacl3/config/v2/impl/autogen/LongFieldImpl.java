package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.LongFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.LongField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class LongFieldImpl extends SimpleOptionFactory<LongField, Long> {
   protected ControllerBuilder<Long> createController(LongField annotation, ConfigField<Long> field, OptionAccess storage, Option<Long> option) {
      return LongFieldControllerBuilder.create(option).formatValue(v -> {
         String key = this.getTranslationKey(field, "fmt." + v);
         if (Language.getInstance().has(key)) {
            return Component.translatable(key);
         } else {
            key = this.getTranslationKey(field, "fmt");
            return Language.getInstance().has(key) ? Component.translatable(key, new Object[]{v}) : Component.literal(Long.toString(v));
         }
      }).range(annotation.min(), annotation.max());
   }
}
