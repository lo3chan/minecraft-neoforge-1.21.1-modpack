package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionAddable;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionAccessImpl;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionFactoryRegistry;
import dev.isxander.yacl3.config.v2.impl.autogen.YACLAutoGenException;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;

public class ConfigClassHandlerImpl<T> implements ConfigClassHandler<T> {
   private final Class<T> configClass;
   private final ResourceLocation id;
   private final boolean supportsAutoGen;
   private final ConfigSerializer<T> serializer;
   private final ConfigFieldImpl<?>[] fields;
   private T instance;
   private final T defaults;
   private final Constructor<T> noArgsConstructor;

   public ConfigClassHandlerImpl(Class<T> configClass, ResourceLocation id, Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory) {
      this.configClass = configClass;
      this.id = id;
      this.supportsAutoGen = id != null && YACLPlatform.getEnvironment().isClient();

      try {
         this.noArgsConstructor = configClass.getDeclaredConstructor();
      } catch (NoSuchMethodException var5) {
         throw new YACLAutoGenException("Failed to find no-args constructor for config class %s.".formatted(configClass.getName()), var5);
      }

      this.instance = this.createNewObject();
      this.defaults = this.createNewObject();
      this.detectOldAnnotation(configClass.getDeclaredFields());
      this.fields = this.discoverFields();
      this.serializer = serializerFactory.apply(this);
   }

   private ConfigFieldImpl<?>[] discoverFields() {
      SerialEntry classSerialEntry = this.configClass.getAnnotation(SerialEntry.class);
      boolean classHasSerialEntry = classSerialEntry != null;
      if (classHasSerialEntry) {
         if (!"".equals(classSerialEntry.value())) {
            throw new IllegalArgumentException(
               "SerialEntry on class '%s' must not have a value. Only `required` and `nullable` are permitted parameters on classes."
                  .formatted(this.configClass.getName())
            );
         }

         if (!"".equals(classSerialEntry.comment())) {
            throw new IllegalArgumentException(
               "SerialEntry on class '%s' must not have a comment. Only `required` and `nullable` are permitted parameters on classes."
                  .formatted(this.configClass.getName())
            );
         }
      }

      return Arrays.stream(this.configClass.getDeclaredFields())
         .peek(field -> field.setAccessible(true))
         .filter(field -> classHasSerialEntry || field.isAnnotationPresent(SerialEntry.class) || field.isAnnotationPresent(AutoGen.class))
         .map(
            field -> new ConfigFieldImpl(
               new ReflectionFieldAccess<>(field, this.instance),
               new ReflectionFieldAccess<>(field, this.defaults),
               this,
               field.getAnnotation(SerialEntry.class),
               classSerialEntry,
               field.getAnnotation(AutoGen.class)
            )
         )
         .toArray(ConfigFieldImpl[]::new);
   }

   @Override
   public T instance() {
      return this.instance;
   }

   @Override
   public T defaults() {
      return this.defaults;
   }

   @Override
   public Class<T> configClass() {
      return this.configClass;
   }

   public ConfigFieldImpl<?>[] fields() {
      return this.fields;
   }

   @Override
   public ResourceLocation id() {
      return this.id;
   }

   @Override
   public boolean supportsAutoGen() {
      return this.supportsAutoGen;
   }

   @Override
   public YetAnotherConfigLib generateGui() {
      if (!this.supportsAutoGen()) {
         throw new YACLAutoGenException(
            "Auto GUI generation is not supported for this config class. You either need to enable it in the builder or you are attempting to create a GUI in a dedicated server environment."
         );
      } else {
         boolean hasAutoGenFields = Arrays.stream(this.fields()).anyMatch(field -> field.autoGen().isPresent());
         if (!hasAutoGenFields) {
            throw new YACLAutoGenException(
               "No fields in this config class are annotated with @AutoGen. You must annotate at least one field with @AutoGen to generate a GUI."
            );
         } else {
            OptionAccessImpl storage = new OptionAccessImpl();
            Map<String, ConfigClassHandlerImpl.CategoryAndGroups> categories = new LinkedHashMap<>();

            for (ConfigField<?> configField : this.fields()) {
               configField.autoGen()
                  .ifPresent(
                     autoGen -> {
                        ConfigClassHandlerImpl.CategoryAndGroups groups = categories.computeIfAbsent(
                           autoGen.category(),
                           k -> new ConfigClassHandlerImpl.CategoryAndGroups(
                              ConfigCategory.createBuilder().name(Component.translatable("yacl3.config.%s.category.%s".formatted(this.id().toString(), k))),
                              new LinkedHashMap<>()
                           )
                        );
                        OptionAddable group = groups.groups()
                           .computeIfAbsent(
                              autoGen.group().orElse(""),
                              k -> (OptionAddable)(k.isEmpty()
                                 ? groups.category()
                                 : OptionGroup.createBuilder()
                                    .name(Component.translatable("yacl3.config.%s.category.%s.group.%s".formatted(this.id().toString(), autoGen.category(), k))))
                           );

                        Option<?> option;
                        try {
                           option = this.createOption(configField, storage);
                        } catch (Exception var9) {
                           throw new YACLAutoGenException("Failed to create option for field '%s'".formatted(configField.access().name()), var9);
                        }

                        storage.putOption(configField.access().name(), option);
                        group.option(option);
                     }
                  );
            }

            storage.checkBadOperations();
            categories.values().forEach(ConfigClassHandlerImpl.CategoryAndGroups::finaliseGroups);
            YetAnotherConfigLib.Builder yaclBuilder = YetAnotherConfigLib.createBuilder()
               .save(this.serializer()::save)
               .title(Component.translatable("yacl3.config.%s.title".formatted(this.id().toString())));
            categories.values().forEach(category -> yaclBuilder.category(category.category().build()));
            return yaclBuilder.build();
         }
      }
   }

   private <U> Option<U> createOption(ConfigField<U> configField, OptionAccess storage) {
      return OptionFactoryRegistry.createOption(((ReflectionFieldAccess)configField.access()).field(), configField, storage)
         .orElseThrow(() -> new YACLAutoGenException("Failed to create option for field %s".formatted(configField.access().name())));
   }

   @Override
   public ConfigSerializer<T> serializer() {
      return this.serializer;
   }

   @Override
   public boolean load() {
      T newInstance = this.createNewObject();
      Map<ConfigFieldImpl<?>, ReflectionFieldAccess<?>> accessBufferImpl = Arrays.stream(this.fields())
         .map(fieldx -> new SimpleImmutableEntry<>(fieldx, new ReflectionFieldAccess(fieldx.access().field(), newInstance)))
         .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      Map<ConfigField<?>, FieldAccess<?>> accessBuffer = accessBufferImpl.entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      ConfigSerializer.LoadResult loadResult = ConfigSerializer.LoadResult.FAILURE;
      Throwable error = null;

      try {
         loadResult = this.serializer().loadSafely(accessBuffer);
      } catch (Throwable var10) {
         error = var10;
      }

      switch (loadResult) {
         case DIRTY:
         case SUCCESS:
            this.instance = newInstance;

            for (ConfigFieldImpl<?> field : this.fields()) {
               field.setFieldAccess(accessBufferImpl.get(field));
            }

            if (loadResult == ConfigSerializer.LoadResult.DIRTY) {
               this.save();
            }
         case NO_CHANGE:
            return true;
         case FAILURE:
            YACLConstants.LOGGER
               .error(
                  "Unsuccessful load of config class '{}'. The load will be abandoned and config remains unchanged.", this.configClass.getSimpleName(), error
               );
         default:
            return false;
      }
   }

   @Override
   public void save() {
      this.serializer().save();
   }

   private T createNewObject() {
      try {
         return this.noArgsConstructor.newInstance();
      } catch (Exception var2) {
         throw new YACLAutoGenException("Failed to create instance of config class '%s' with no-args constructor.".formatted(this.configClass.getName()), var2);
      }
   }

   private void detectOldAnnotation(Field[] fields) {
      boolean hasOldConfigEntry = Arrays.stream(fields).anyMatch(field -> field.isAnnotationPresent(ConfigEntry.class));
      Validate.isTrue(
         !hasOldConfigEntry,
         "At least one field in %s is still annotated with the deprecated @ConfigEntry annotation. This is incorrect. Use @SerialEntry."
            .formatted(this.configClass.getName()),
         new Object[0]
      );
   }

   public static class BuilderImpl<T> implements ConfigClassHandler.Builder<T> {
      private final Class<T> configClass;
      private ResourceLocation id;
      private Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory;

      public BuilderImpl(Class<T> configClass) {
         this.configClass = configClass;
      }

      @Override
      public ConfigClassHandler.Builder<T> id(ResourceLocation id) {
         this.id = id;
         return this;
      }

      @Override
      public ConfigClassHandler.Builder<T> serializer(Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory) {
         this.serializerFactory = serializerFactory;
         return this;
      }

      @Override
      public ConfigClassHandler<T> build() {
         Validate.notNull(this.serializerFactory, "serializerFactory must not be null", new Object[0]);
         Validate.notNull(this.configClass, "configClass must not be null", new Object[0]);
         return new ConfigClassHandlerImpl<>(this.configClass, this.id, this.serializerFactory);
      }
   }

   private record CategoryAndGroups(ConfigCategory.Builder category, Map<String, OptionAddable> groups) {
      private void finaliseGroups() {
         this.groups.forEach((name, group) -> {
            if (group instanceof OptionGroup.Builder groupBuilder) {
               this.category.group(groupBuilder.build());
            }
         });
      }
   }
}
