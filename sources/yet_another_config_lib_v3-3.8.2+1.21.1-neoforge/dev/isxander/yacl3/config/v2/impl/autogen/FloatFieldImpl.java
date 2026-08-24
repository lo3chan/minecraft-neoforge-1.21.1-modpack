package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.FloatField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class FloatFieldImpl extends SimpleOptionFactory<FloatField, Float> {
   protected ControllerBuilder<Float> createController(FloatField annotation, ConfigField<Float> field, OptionAccess storage, Option<Float> option) {
      return FloatFieldControllerBuilder.create(option)
         .formatValue(
            v -> {
               String key = null;
               if (v == annotation.min()) {
                  key = this.getTranslationKey(field, "fmt.min");
               } else if (v == annotation.max()) {
                  key = this.getTranslationKey(field, "fmt.max");
               }

               if (key != null && Language.getInstance().has(key)) {
                  return Component.translatable(key);
               } else {
                  key = this.getTranslationKey(field, "fmt");
                  return Language.getInstance().has(key)
                     ? Component.translatable(key, new Object[]{v})
                     : Component.translatable(String.format(annotation.format(), v));
               }
            }
         )
         .range(annotation.min(), annotation.max());
   }
}
