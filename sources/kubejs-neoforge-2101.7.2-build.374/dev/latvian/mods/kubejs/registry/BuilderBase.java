package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@ReturnsSelf
public abstract class BuilderBase<T> implements Supplier<T> {
   public final ResourceLocation id;
   public SourceLine sourceLine;
   public ResourceKey<Registry<T>> registryKey;
   protected T object;
   public String translationKey;
   public Component displayName;
   public boolean formattedDisplayName;
   public transient boolean dummyBuilder;
   public transient Set<ResourceLocation> defaultTags;

   public BuilderBase(ResourceLocation id) {
      this.id = id;
      this.sourceLine = SourceLine.UNKNOWN;
      this.object = null;
      this.translationKey = "";
      this.displayName = null;
      this.formattedDisplayName = false;
      this.dummyBuilder = false;
      this.defaultTags = new HashSet<>();
   }

   @HideFromJS
   public abstract T createObject();

   @HideFromJS
   public T transformObject(T obj) {
      return obj;
   }

   @Override
   public final T get() {
      try {
         return this.object;
      } catch (Exception var2) {
         if (this.dummyBuilder) {
            throw new KubeRuntimeException(
                  "Object '" + this.id + "' of registry '" + this.registryKey.location() + "' is from a dummy builder and doesn't have a value!"
               )
               .source(this.sourceLine);
         } else {
            throw new KubeRuntimeException("Object '" + this.id + "' of registry '" + this.registryKey.location() + "' hasn't been registered yet!", var2)
               .source(this.sourceLine);
         }
      }
   }

   @HideFromJS
   public void createAdditionalObjects(AdditionalObjectRegistry registry) {
   }

   public String getTranslationKeyGroup() {
      return this.registryKey == null ? "unknown_registry" : this.registryKey.location().getPath().replace('/', '.');
   }

   @Info("Sets the translation key for this object, e.g. `block.minecraft.stone`.\n")
   public BuilderBase<T> translationKey(String key) {
      this.translationKey = key;
      return this;
   }

   @Info("Sets the display name for this object, e.g. `Stone`.\n\nThis will be overridden by a lang file if it exists.\n")
   public BuilderBase<T> displayName(Component name) {
      this.displayName = name;
      return this;
   }

   @Info("Makes displayName() override language files.\n")
   public BuilderBase<T> formattedDisplayName() {
      this.formattedDisplayName = true;
      return this;
   }

   @Info("Combined method of formattedDisplayName().displayName(name).\n")
   public BuilderBase<T> formattedDisplayName(Component name) {
      return this.formattedDisplayName().displayName(name);
   }

   @Info("Adds a tag to this object, e.g. `minecraft:stone`.\n")
   public BuilderBase<T> tag(ResourceLocation[] tag) {
      this.defaultTags.addAll(Arrays.asList(tag));
      return this;
   }

   @HideFromJS
   public ResourceLocation newID(String pre, String post) {
      return pre.isEmpty() && post.isEmpty() ? this.id : this.id.withPath(pre + this.id.getPath() + post);
   }

   @HideFromJS
   public void generateData(KubeDataGenerator generator) {
   }

   @HideFromJS
   public void generateAssets(KubeAssetGenerator generator) {
   }

   public String getBuilderTranslationKey() {
      return this.translationKey.isEmpty() ? Util.makeDescriptionId(this.getTranslationKeyGroup(), this.id) : this.translationKey;
   }

   @HideFromJS
   public void generateLang(LangKubeEvent lang) {
      if (this.displayName != null) {
         lang.add(this.id.getNamespace(), this.getBuilderTranslationKey(), this.displayName.getString());
      } else {
         lang.add(this.id.getNamespace(), this.getBuilderTranslationKey(), StringUtilsWrapper.snakeCaseToTitleCase(this.id.getPath()));
      }
   }

   @HideFromJS
   public T createTransformedObject() {
      this.object = this.transformObject(this.createObject());
      return this.object;
   }

   @Override
   public String toString() {
      String n = this.getClass().getName();
      int i = n.lastIndexOf(46);
      if (i != -1) {
         n = n.substring(i + 1);
      }

      return n + "[" + this.id + "]";
   }
}
