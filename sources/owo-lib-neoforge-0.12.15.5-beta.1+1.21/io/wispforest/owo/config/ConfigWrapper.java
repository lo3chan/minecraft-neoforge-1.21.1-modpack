package io.wispforest.owo.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.Jankson.Builder;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.jankson.impl.POJODeserializer;
import blue.endless.jankson.magic.TypeMagic;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.RegexConstraint;
import io.wispforest.owo.config.annotation.Sync;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.util.NumberReflection;
import io.wispforest.owo.util.Observable;
import io.wispforest.owo.util.ReflectionUtils;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigWrapper<C> {
   private static final Map<String, Class<?>> KNOWN_CONFIG_CLASSES = new HashMap<>();
   protected final String name;
   protected final C instance;
   protected boolean loading = false;
   protected final Jankson jankson;
   protected final Map<Option.Key, Option> options = new LinkedHashMap<>();
   protected final Map<Option.Key, Option> optionsView = Collections.unmodifiableMap(this.options);
   protected final ReflectiveEndecBuilder builder = MinecraftEndecs.addDefaults(new ReflectiveEndecBuilder());

   protected ConfigWrapper(Class<C> clazz) {
      this(clazz, builder -> {});
   }

   protected ConfigWrapper(Class<C> clazz, Consumer<Builder> janksonBuilder) {
      ReflectionUtils.requireZeroArgsConstructor(clazz, s -> "Config model class " + s + " must provide a zero-args constructor");
      this.instance = ReflectionUtils.tryInstantiateWithNoArgs(clazz);
      Builder builder = Jankson.builder()
         .registerSerializer(ResourceLocation.class, (identifier, marshaller) -> new JsonPrimitive(identifier.toString()))
         .registerDeserializer(JsonPrimitive.class, ResourceLocation.class, (primitive, m) -> ResourceLocation.tryParse(primitive.asString()))
         .registerSerializer(Color.class, (color, marshaller) -> new JsonPrimitive(color.asHexString(true)))
         .registerDeserializer(
            JsonPrimitive.class, Color.class, (primitive, m) -> Color.ofArgb(Integer.parseUnsignedInt(primitive.asString().substring(1), 16))
         );
      janksonBuilder.accept(builder);
      this.jankson = builder.build();
      Config configAnnotation = clazz.getAnnotation(Config.class);
      this.name = configAnnotation.name();
      if (KNOWN_CONFIG_CLASSES.put(this.name, this.getClass()) != null) {
         throw new IllegalStateException(
            "Config name '" + this.name + "' is already taken an by instance of class '" + KNOWN_CONFIG_CLASSES.get(this.name).getName() + "'"
         );
      } else {
         if (FMLLoader.getDist() == Dist.CLIENT && clazz.isAnnotationPresent(Modmenu.class)) {
            Modmenu modmenuAnnotation = clazz.getAnnotation(Modmenu.class);
            ConfigScreenProviders.registerOwoConfigScreen(
               modmenuAnnotation.modId(), screen -> ConfigScreen.createWithCustomModel(ResourceLocation.parse(modmenuAnnotation.uiModelId()), this, screen)
            );
         }

         try {
            this.initializeOptions(configAnnotation.saveOnModification());

            for (Option option : this.options.values()) {
               if (!option.syncMode().isNone()) {
                  ConfigSynchronizer.register(this);
                  break;
               }
            }
         } catch (NoSuchMethodException | IllegalAccessException var7) {
            throw new RuntimeException("Failed to initialize config " + this.name, var7);
         }
      }
   }

   public void save() {
      if (!this.loading) {
         try {
            this.fileLocation().getParent().toFile().mkdirs();
            Files.writeString(this.fileLocation(), this.jankson.toJson(this.instance).toJson(JsonGrammar.JANKSON), StandardCharsets.UTF_8);
         } catch (IOException var2) {
            Owo.LOGGER.warn("Could not save config {}", this.name, var2);
         }
      }
   }

   public void load() {
      if (!Files.exists(this.fileLocation())) {
         this.save();
      } else {
         try {
            this.loading = true;
            JsonObject configObject = this.jankson.load(Files.readString(this.fileLocation(), StandardCharsets.UTF_8));

            for (Option option : this.options.values()) {
               Class clazz = option.clazz();
               JsonElement element = (JsonElement)configObject.recursiveGet(JsonElement.class, option.key().asString());
               if (element == null) {
                  option.set(option.defaultValue());
               } else {
                  Object newValue;
                  if (Map.class.isAssignableFrom(clazz)) {
                     Field field = option.backingField().field();
                     newValue = TypeMagic.createAndCast(clazz);
                     POJODeserializer.unpackMap(
                        (Map)newValue,
                        ReflectionUtils.getTypeArgument(field.getGenericType(), 0),
                        ReflectionUtils.getTypeArgument(field.getGenericType(), 1),
                        element,
                        this.jankson.getMarshaller()
                     );
                  } else if (!List.class.isAssignableFrom(clazz) && !Set.class.isAssignableFrom(clazz)) {
                     newValue = configObject.getMarshaller().marshall(clazz, element);
                  } else {
                     newValue = TypeMagic.createAndCast(clazz);
                     POJODeserializer.unpackCollection(
                        (Collection)newValue,
                        ReflectionUtils.getTypeArgument(option.backingField().field().getGenericType(), 0),
                        element,
                        this.jankson.getMarshaller()
                     );
                  }

                  if (option.verifyConstraint(newValue)) {
                     option.set(newValue == null ? option.defaultValue() : newValue);
                  }
               }
            }
         } catch (SyntaxError | DeserializationException | IOException var11) {
            Owo.LOGGER.warn("Could not load config {}", this.name, var11);
         } finally {
            this.loading = false;
         }
      }
   }

   @Nullable
   public Field fieldForKey(Option.Key key) {
      try {
         ArrayList<String> path = new ArrayList<>(List.of(key.path()));
         Class<?> clazz = this.instance.getClass();

         while (path.size() > 1) {
            clazz = clazz.getDeclaredField(path.remove(0)).getType();
         }

         return clazz.getField(path.get(0));
      } catch (NoSuchFieldException var4) {
         return null;
      }
   }

   public String name() {
      return this.name;
   }

   public Path fileLocation() {
      return FMLLoader.getGamePath().resolve(FMLPaths.CONFIGDIR.relative()).resolve(this.name + ".json5");
   }

   @Nullable
   public <T> Option<T> optionForKey(Option.Key key) {
      return this.options.get(key);
   }

   public Map<Option.Key, Option<?>> allOptions() {
      return this.optionsView;
   }

   public void forEachOption(Consumer<Option<?>> action) {
      for (Option option : this.options.values()) {
         action.accept(option);
      }
   }

   private void initializeOptions(boolean hookSave) throws IllegalAccessException, NoSuchMethodException {
      LinkedHashMap<Option.Key, Option.BoundField<Object>> fields = new LinkedHashMap<>();
      this.collectFieldValues(Option.Key.ROOT, this.instance, fields);
      Option.SyncMode instanceSyncMode = this.instance.getClass().isAnnotationPresent(Sync.class)
         ? this.instance.getClass().getAnnotation(Sync.class).value()
         : Option.SyncMode.NONE;

      for (Entry<Option.Key, Option.BoundField<Object>> entry : fields.entrySet()) {
         Option.Key key = entry.getKey();
         Option.BoundField<Object> boundField = entry.getValue();
         Field field = boundField.field();
         Class<?> fieldType = field.getType();
         ConfigWrapper.Constraint constraint = null;
         if (field.isAnnotationPresent(RangeConstraint.class)) {
            RangeConstraint annotation = field.getAnnotation(RangeConstraint.class);
            if (!NumberReflection.isNumberType(fieldType)) {
               throw new IllegalStateException("@RangeConstraint can only be applied to numeric fields");
            }

            Predicate<?> predicate;
            if (fieldType != long.class && fieldType != Long.class) {
               predicate = o -> o != null && ((Number)o).doubleValue() >= annotation.min() && ((Number)o).doubleValue() <= annotation.max();
            } else {
               predicate = o -> o != null && ((Long)o).longValue() >= annotation.min() && ((Long)o).longValue() <= annotation.max();
            }

            constraint = new ConfigWrapper.Constraint("Range from " + annotation.min() + " to " + annotation.max(), predicate);
         }

         if (field.isAnnotationPresent(RegexConstraint.class)) {
            RegexConstraint annotationx = field.getAnnotation(RegexConstraint.class);
            if (!CharSequence.class.isAssignableFrom(fieldType)) {
               throw new IllegalStateException("@RegexConstraint can only be applied to fields with a string representation");
            }

            Pattern pattern = Pattern.compile(annotationx.value());
            constraint = new ConfigWrapper.Constraint("Regex " + annotationx.value(), o -> o != null && pattern.matcher((CharSequence)o).matches());
         }

         if (field.isAnnotationPresent(PredicateConstraint.class)) {
            PredicateConstraint annotationx = field.getAnnotation(PredicateConstraint.class);
            Method method = boundField.owner().getClass().getMethod(annotationx.value(), fieldType);
            if (method.getReturnType() != boolean.class) {
               throw new NoSuchMethodException("Return type of predicate implementation '" + annotationx.value() + "' must be 'boolean'");
            }

            if (!Modifier.isStatic(method.getModifiers())) {
               throw new IllegalStateException("Predicate implementation '" + annotationx.value() + "' must be static");
            }

            MethodHandle handle = MethodHandles.publicLookup().unreflect(method);
            constraint = new ConfigWrapper.Constraint("Predicate method " + annotationx.value(), o -> this.invokePredicate(handle, o));
         }

         Object defaultValue = boundField.getValue();
         Observable<Object> observable = Observable.of(defaultValue);
         if (hookSave) {
            observable.observe(o -> this.save());
         }

         Option.SyncMode syncMode = instanceSyncMode;
         if (field.isAnnotationPresent(Sync.class)) {
            syncMode = field.getAnnotation(Sync.class).value();
         } else {
            for (Option.Key parentKey = key.parent(); !parentKey.isRoot(); parentKey = parentKey.parent()) {
               Field parentField = this.fieldForKey(parentKey);
               if (parentField.isAnnotationPresent(Sync.class)) {
                  syncMode = parentField.getAnnotation(Sync.class).value();
               }
            }
         }

         this.options.put(key, new Option<>(this.name, key, defaultValue, observable, boundField, constraint, syncMode, this.builder));
      }
   }

   private void collectFieldValues(Option.Key parent, Object instance, Map<Option.Key, Option.BoundField<Object>> fields) throws IllegalAccessException {
      for (Field field : instance.getClass().getDeclaredFields()) {
         if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
            if (field.isAnnotationPresent(Nest.class)) {
               Object fieldValue = field.get(instance);
               if (fieldValue == null) {
                  throw new IllegalStateException("Nested config option containers must never be null");
               }

               this.collectFieldValues(parent.child(field.getName()), fieldValue, fields);
            } else {
               fields.put(parent.child(field.getName()), new Option.BoundField<>(instance, field));
            }
         }
      }
   }

   private boolean invokePredicate(MethodHandle predicate, Object value) {
      try {
         return (boolean)predicate.invoke((Object)value);
      } catch (Throwable var4) {
         throw new RuntimeException("Could not invoke predicate", var4);
      }
   }

   public record Constraint(String formatted, Predicate predicate) {
      public boolean test(Object value) {
         return this.predicate.test(value);
      }
   }
}
