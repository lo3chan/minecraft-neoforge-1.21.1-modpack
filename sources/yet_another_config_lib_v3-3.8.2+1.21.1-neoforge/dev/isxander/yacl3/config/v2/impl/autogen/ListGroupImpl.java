package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.OptionFactory;
import dev.isxander.yacl3.config.v2.impl.FieldBackedBinding;
import dev.isxander.yacl3.platform.YACLPlatform;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ListGroupImpl<T> implements OptionFactory<ListGroup, List<T>> {
   public Option<List<T>> createOption(ListGroup annotation, ConfigField<List<T>> field, OptionAccess optionAccess) {
      if (field.autoGen().orElseThrow().group().isPresent()) {
         throw new YACLAutoGenException("@ListGroup fields ('%s') cannot be inside a group as lists act as groups.".formatted(field.access().name()));
      } else {
         ListGroup.ValueFactory<T> valueFactory = this.createValueFactory((Class<? extends ListGroup.ValueFactory<T>>)annotation.valueFactory());
         ListGroup.ControllerFactory<T> controllerFactory = this.createControllerFactory(
            (Class<? extends ListGroup.ControllerFactory<T>>)annotation.controllerFactory()
         );
         return ListOption.<T>createBuilder()
            .name(Component.translatable(this.getTranslationKey(field, null)))
            .description(this.description(field))
            .initial(valueFactory::provideNewValue)
            .controller(opt -> controllerFactory.createController(annotation, field, optionAccess, opt))
            .binding(new FieldBackedBinding<>(field.access(), field.defaultAccess()))
            .minimumNumberOfEntries(annotation.minEntries())
            .maximumNumberOfEntries(annotation.maxEntries() == 0 ? 2147483647 : annotation.maxEntries())
            .insertEntriesAtEnd(annotation.addEntriesToBottom())
            .build();
      }
   }

   private OptionDescription description(ConfigField<List<T>> field) {
      OptionDescription.Builder builder = OptionDescription.createBuilder();
      String key = this.getTranslationKey(field, "desc");
      if (Language.getInstance().has(key)) {
         builder.text(Component.translatable(key));
      } else {
         key = key + ".";
         int i = 0;

         while (Language.getInstance().has(key + i++)) {
            builder.text(Component.translatable(key + i));
         }
      }

      String imagePath = "textures/yacl3/" + field.parent().id().getPath() + "/" + field.access().name() + ".webp";
      imagePath = imagePath.toLowerCase().replaceAll("[^a-z0-9/._:-]", "_");
      ResourceLocation imageLocation = YACLPlatform.rl(field.parent().id().getNamespace(), imagePath);
      if (Minecraft.getInstance().getResourceManager().getResource(imageLocation).isPresent()) {
         builder.webpImage(imageLocation);
      }

      return builder.build();
   }

   private ListGroup.ValueFactory<T> createValueFactory(Class<? extends ListGroup.ValueFactory<T>> clazz) {
      Constructor<? extends ListGroup.ValueFactory<T>> constructor;
      try {
         constructor = clazz.getConstructor();
      } catch (NoSuchMethodException var5) {
         throw new YACLAutoGenException("Could not find no-args constructor for `valueFactory` on '%s' for @ListGroup field.".formatted(clazz.getName()), var5);
      }

      try {
         return (ListGroup.ValueFactory<T>)constructor.newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException var4) {
         throw new YACLAutoGenException("Couldn't invoke no-args constructor for `valueFactory` on '%s' for @ListGroup field.".formatted(clazz.getName()), var4);
      }
   }

   private ListGroup.ControllerFactory<T> createControllerFactory(Class<? extends ListGroup.ControllerFactory<T>> clazz) {
      Constructor<? extends ListGroup.ControllerFactory<T>> constructor;
      try {
         constructor = clazz.getConstructor();
      } catch (NoSuchMethodException var5) {
         throw new YACLAutoGenException(
            "Could not find no-args constructor on `controllerFactory`, '%s' for @ListGroup field.".formatted(clazz.getName()), var5
         );
      }

      try {
         return (ListGroup.ControllerFactory<T>)constructor.newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException var4) {
         throw new YACLAutoGenException(
            "Couldn't invoke no-args constructor on `controllerFactory`, '%s' for @ListGroup field.".formatted(clazz.getName()), var4
         );
      }
   }

   private String getTranslationKey(ConfigField<List<T>> field, @Nullable String suffix) {
      String key = "yacl3.config.%s.%s".formatted(field.parent().id().toString(), field.access().name());
      if (suffix != null) {
         key = key + "." + suffix;
      }

      return key;
   }
}
