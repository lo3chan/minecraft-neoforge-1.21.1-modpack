package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.network.chat.Component;

public class BooleanImpl extends SimpleOptionFactory<dev.isxander.yacl3.config.v2.api.autogen.Boolean, Boolean> {
   protected ControllerBuilder<Boolean> createController(
      dev.isxander.yacl3.config.v2.api.autogen.Boolean annotation, ConfigField<Boolean> field, OptionAccess storage, Option<Boolean> option
   ) {
      BooleanControllerBuilder builder = BooleanControllerBuilder.create(option).coloured(annotation.colored());
      switch (annotation.formatter()) {
         case ON_OFF:
            builder.onOffFormatter();
            break;
         case YES_NO:
            builder.yesNoFormatter();
            break;
         case TRUE_FALSE:
            builder.trueFalseFormatter();
            break;
         case CUSTOM:
            builder.formatValue(v -> Component.translatable(this.getTranslationKey(field, "fmt." + v)));
      }

      return builder;
   }
}
