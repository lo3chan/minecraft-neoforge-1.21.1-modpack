package net.blay09.mods.balm.api.config.schema.builder;

import java.util.List;
import java.util.Set;
import net.blay09.mods.balm.api.config.schema.impl.ConfigSchemaImpl;
import net.minecraft.resources.ResourceLocation;

public class ConfigPropertyBuilder {
   protected final ConfigSchemaImpl schema;
   protected final String category;
   protected final String name;
   protected String comment = "";
   protected boolean synced;

   public ConfigPropertyBuilder(ConfigSchemaImpl schema, String name) {
      this.schema = schema;
      this.category = "";
      this.name = name;
   }

   public ConfigPropertyBuilder(ConfigSchemaImpl schema, String category, String name) {
      this.schema = schema;
      this.category = category;
      this.name = name;
   }

   public ConfigPropertyBuilder comment(String comment) {
      this.comment = comment;
      return this;
   }

   public ConfigPropertyBuilder synced() {
      this.synced = true;
      return this;
   }

   public BooleanConfigProperty boolOf(boolean defaultValue) {
      return this.schema.addAndReturn(new BooleanConfigProperty(this, defaultValue));
   }

   public IntConfigProperty intOf(int defaultValue) {
      return this.schema.addAndReturn(new IntConfigProperty(this, defaultValue));
   }

   public LongConfigProperty longOf(long defaultValue) {
      return this.schema.addAndReturn(new LongConfigProperty(this, defaultValue));
   }

   public FloatConfigProperty floatOf(float defaultValue) {
      return this.schema.addAndReturn(new FloatConfigProperty(this, defaultValue));
   }

   public DoubleConfigProperty doubleOf(double defaultValue) {
      return this.schema.addAndReturn(new DoubleConfigProperty(this, defaultValue));
   }

   public StringConfigProperty stringOf(String defaultValue) {
      return this.schema.addAndReturn(new StringConfigProperty(this, defaultValue));
   }

   public <T extends Enum<T>> EnumConfigProperty<T> enumOf(T defaultValue) {
      return this.schema.addAndReturn(new EnumConfigProperty<>(this, defaultValue));
   }

   public <T> ListConfigProperty<T> listOf(Class<T> nestedType, List<T> defaultValue) {
      return this.schema.addAndReturn(new ListConfigProperty<>(this, nestedType, defaultValue));
   }

   public <T> SetConfigProperty<T> setOf(Class<T> nestedType, Set<T> defaultValue) {
      return this.schema.addAndReturn(new SetConfigProperty<>(this, nestedType, defaultValue));
   }

   public ResourceLocationConfigProperty resourceLocationOf(ResourceLocation defaultValue) {
      return this.schema.addAndReturn(new ResourceLocationConfigProperty(this, defaultValue));
   }
}
