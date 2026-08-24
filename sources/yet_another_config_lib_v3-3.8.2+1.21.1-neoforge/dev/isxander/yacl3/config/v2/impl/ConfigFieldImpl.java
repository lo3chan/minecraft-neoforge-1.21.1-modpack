package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.SerialField;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGenField;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class ConfigFieldImpl<T> implements ConfigField<T> {
   private ReflectionFieldAccess<T> field;
   private final ReflectionFieldAccess<T> defaultField;
   private final ConfigClassHandler<?> parent;
   private final Optional<SerialField> serial;
   private final Optional<AutoGenField> autoGen;

   public ConfigFieldImpl(
      ReflectionFieldAccess<T> field,
      ReflectionFieldAccess<T> defaultField,
      ConfigClassHandler<?> parent,
      @Nullable SerialEntry config,
      @Nullable SerialEntry inheritedConfig,
      @Nullable AutoGen autoGen
   ) {
      this.field = field;
      this.defaultField = defaultField;
      this.parent = parent;
      if (config != null) {
         this.serial = Optional.of(
            new ConfigFieldImpl.SerialFieldImpl(
               "".equals(config.value()) ? field.name() : config.value(),
               "".equals(config.comment()) ? Optional.empty() : Optional.of(config.comment()),
               config.required(),
               config.nullable()
            )
         );
      } else if (inheritedConfig != null) {
         this.serial = Optional.of(new ConfigFieldImpl.SerialFieldImpl(field.name(), Optional.empty(), inheritedConfig.required(), inheritedConfig.nullable()));
      } else {
         this.serial = Optional.empty();
      }

      this.autoGen = autoGen != null
         ? Optional.of(new ConfigFieldImpl.AutoGenFieldImpl(autoGen.category(), "".equals(autoGen.group()) ? Optional.empty() : Optional.of(autoGen.group())))
         : Optional.empty();
   }

   public ReflectionFieldAccess<T> access() {
      return this.field;
   }

   public void setFieldAccess(ReflectionFieldAccess<T> field) {
      this.field = field;
   }

   public ReflectionFieldAccess<T> defaultAccess() {
      return this.defaultField;
   }

   @Override
   public ConfigClassHandler<?> parent() {
      return this.parent;
   }

   @Override
   public Optional<SerialField> serial() {
      return this.serial;
   }

   @Override
   public Optional<AutoGenField> autoGen() {
      return this.autoGen;
   }

   private record AutoGenFieldImpl<T>(String category, Optional<String> group) implements AutoGenField {
   }

   private record SerialFieldImpl(String serialName, Optional<String> comment, boolean required, boolean nullable) implements SerialField {
   }
}
