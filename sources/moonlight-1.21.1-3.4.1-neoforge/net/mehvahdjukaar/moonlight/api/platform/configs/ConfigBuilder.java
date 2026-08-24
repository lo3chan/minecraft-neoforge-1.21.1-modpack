package net.mehvahdjukaar.moonlight.api.platform.configs;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigNode;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigReloadType;
import net.mehvahdjukaar.moonlight.api.platform.configs.platform.ConfigBuilderImpl;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.api.util.math.Range;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.ConfigLangExporter;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigBuilder {
   protected final Map<String, String> translations = new LinkedHashMap<>();
   private final Map<String, String> moonlightNames = new LinkedHashMap<>();
   protected Runnable changeCallback;
   protected boolean pendingDynamicPacks;
   @Nullable
   private String pendingComment;
   private boolean pendingCommentForwarded;
   @Nullable
   private ConfigBuilder.CommentTarget lastCommentTarget;
   @Nullable
   private String lastCommentKey;
   private final ConfigCategory uiRoot = new ConfigCategory(Component.empty());
   private final Deque<ConfigCategory> uiStack = new ArrayDeque<>();
   private final Deque<Supplier<Boolean>> gateStack = new ArrayDeque<>();
   protected boolean suppressUi = false;
   private final Map<String, Supplier<Boolean>> featureToggles = new LinkedHashMap<>();
   private final Deque<String> categoryPath = new ArrayDeque<>();
   public static final String FEATURE_TOGGLE_NAME = "enabled";
   protected boolean writeObjectsAsJson = false;
   protected ConfigReloadType pendingReload = ConfigReloadType.NONE;
   @Nullable
   private ResourceLocation pendingIcon;
   private final ResourceLocation name;
   protected final ConfigType type;
   public static final Predicate<Object> REGISTRY_ID_CHECK = o -> o instanceof String s && ResourceLocation.tryParse(s) != null;
   public static final Predicate<Object> STRING_CHECK = o -> o instanceof String;
   public static final Predicate<Object> REGEX_CHECK = o -> o instanceof String s && ConfigOption.RegexValue.isValidRegex(s);
   public static final Predicate<Object> LIST_STRING_CHECK = o -> o instanceof List<?> l && l.stream().allMatch(e -> e instanceof String);

   public static ConfigBuilder create(String modId, ConfigType type) {
      return create(ResourceLocation.fromNamespaceAndPath(modId, type.getDefaultName()), type);
   }

   protected ConfigBuilder(ResourceLocation name, ConfigType type) {
      this.name = name;
      this.type = type;
      this.uiStack.push(this.uiRoot);
      this.gateStack.push(() -> true);
      Consumer<AfterLanguageLoadEvent> consumer = e -> {
         this.moonlightNames.forEach((key, rawName) -> {
            String shared = e.getEntry(moonlightNamedKey(rawName));
            if (shared != null) {
               e.addEntry(key, shared);
            }
         });
         if (e.isDefault()) {
            this.translations.forEach(e::addEntry);
         }
      };
      MoonlightEventsHelper.addListener(consumer, AfterLanguageLoadEvent.class);
      Moonlight.addDependent(name.getNamespace());
   }

   public final ModConfigHolder build() {
      this.flushPendingComment();
      ModConfigHolder holder = this.buildHolder();
      holder.setFeatureToggles(this.getFeatureToggles());
      ConfigLangExporter.exportInDev(this.name.getNamespace(), this.translations, this.moonlightNames);
      return holder;
   }

   private static String moonlightNamedKey(String name) {
      return "moonlight.config.common." + name;
   }

   protected abstract ModConfigHolder buildHolder();

   public ResourceLocation getName() {
      return this.name;
   }

   public abstract ConfigBuilder push(String var1);

   public abstract ConfigBuilder pop();

   public <T extends ConfigBuilder> T writeObjectsAsJson() {
      this.writeObjectsAsJson = true;
      return (T)this;
   }

   @Deprecated(
      forRemoval = true
   )
   public <T extends ConfigBuilder> T setWriteJsons() {
      return this.writeObjectsAsJson();
   }

   public <T extends ConfigBuilder> T affectsDynamicPacks() {
      this.pendingDynamicPacks = true;
      return (T)this;
   }

   public abstract Supplier<Boolean> define(String var1, boolean var2);

   public abstract Supplier<Double> define(String var1, double var2, double var4, double var6);

   public abstract Supplier<Float> define(String var1, float var2, float var3, float var4);

   public abstract Supplier<Integer> define(String var1, int var2, int var3, int var4);

   public Supplier<Integer> defineColor(String name, int defaultValue) {
      return this.defineColor(name, defaultValue, true);
   }

   public abstract Supplier<Integer> defineColor(String var1, int var2, boolean var3);

   public abstract Supplier<Integer> defineSlider(String var1, int var2, int var3, int var4);

   public abstract Supplier<Double> defineSlider(String var1, double var2, double var4, double var6);

   public abstract Supplier<Float> defineSlider(String var1, float var2, float var3, float var4);

   public abstract Supplier<Double> definePercentage(String var1, double var2);

   public abstract Supplier<String> define(String var1, String var2, Predicate<Object> var3);

   public Supplier<String> define(String name, String defaultValue) {
      return this.define(name, defaultValue, STRING_CHECK);
   }

   public Supplier<Pattern> defineRegex(String name, String defaultValue) {
      return new ConfigBuilder.RegexPatternValue(this.defineRegexInternal(name, defaultValue));
   }

   protected abstract Supplier<String> defineRegexInternal(String var1, String var2);

   protected abstract Supplier<String> defineChoiceInternal(
      String var1, String var2, Predicate<Object> var3, Supplier<List<String>> var4, @Nullable Function<String, ItemStack> var5
   );

   public Supplier<String> defineDropdown(String name, String defaultValue, List<String> options) {
      List<String> copy = List.copyOf(options);
      return this.defineChoiceInternal(name, defaultValue, o -> o instanceof String s && copy.contains(s), () -> copy, null);
   }

   public Supplier<ResourceLocation> defineRegistry(String name, ResourceLocation defaultValue, Registry<?> registry) {
      Supplier<String> handle = this.defineChoiceInternal(name, defaultValue.toString(), REGISTRY_ID_CHECK, () -> registryIds(registry), null);
      return () -> ResourceLocation.parse(handle.get());
   }

   public Supplier<Item> defineItem(String name, ResourceLocation defaultValue) {
      Supplier<String> handle = this.defineChoiceInternal(
         name,
         defaultValue.toString(),
         REGISTRY_ID_CHECK,
         () -> registryIds(BuiltInRegistries.ITEM),
         id -> new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)))
      );
      return () -> (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse(handle.get()));
   }

   public Supplier<Block> defineBlock(String name, ResourceLocation defaultValue) {
      Supplier<String> handle = this.defineChoiceInternal(
         name,
         defaultValue.toString(),
         REGISTRY_ID_CHECK,
         () -> registryIds(BuiltInRegistries.BLOCK),
         id -> new ItemStack(((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id))).asItem())
      );
      return () -> (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(handle.get()));
   }

   private static List<String> registryIds(Registry<?> registry) {
      return registry.keySet().stream().<String>map(ResourceLocation::toString).sorted().toList();
   }

   private static List<String> idStrings(List<ResourceLocation> ids) {
      return ids.stream().<String>map(ResourceLocation::toString).toList();
   }

   protected abstract Supplier<List<String>> defineListInternal(
      String var1, List<String> var2, Predicate<Object> var3, Supplier<List<String>> var4, @Nullable Function<String, ItemStack> var5
   );

   public Supplier<List<String>> defineList(String name, List<String> defaultValue, List<String> options) {
      List<String> copy = List.copyOf(options);
      return this.defineListInternal(name, defaultValue, o -> o instanceof String s && copy.contains(s), () -> copy, null);
   }

   public Supplier<List<String>> defineSuggestionList(
      String name, List<String> defaultValue, Supplier<List<String>> suggestions, Predicate<Object> entryValidator, @Nullable Function<String, ItemStack> icon
   ) {
      return this.defineListInternal(name, defaultValue, entryValidator, suggestions, icon);
   }

   public Supplier<List<ResourceLocation>> defineRegistryList(String name, List<ResourceLocation> defaultValue, Registry<?> registry) {
      Supplier<List<String>> handle = this.defineListInternal(name, idStrings(defaultValue), REGISTRY_ID_CHECK, () -> registryIds(registry), null);
      return () -> handle.get().stream().<ResourceLocation>map(ResourceLocation::parse).toList();
   }

   public Supplier<List<Item>> defineItemList(String name, List<ResourceLocation> defaultValue) {
      Supplier<List<String>> handle = this.defineListInternal(
         name,
         idStrings(defaultValue),
         REGISTRY_ID_CHECK,
         () -> registryIds(BuiltInRegistries.ITEM),
         id -> new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)))
      );
      return () -> handle.get().stream().map(id -> (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse(id))).toList();
   }

   public Supplier<List<Block>> defineBlockList(String name, List<ResourceLocation> defaultValue) {
      Supplier<List<String>> handle = this.defineListInternal(
         name,
         idStrings(defaultValue),
         REGISTRY_ID_CHECK,
         () -> registryIds(BuiltInRegistries.BLOCK),
         id -> new ItemStack(((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id))).asItem())
      );
      return () -> handle.get().stream().map(id -> (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id))).toList();
   }

   public <T extends String> Supplier<List<String>> define(String name, List<? extends T> defaultValue) {
      return this.define(name, defaultValue, s -> true);
   }

   public abstract String currentCategory();

   public abstract String parentCategory();

   public abstract <T extends String> Supplier<List<String>> define(String var1, List<? extends T> var2, Predicate<Object> var3);

   public abstract <V extends Enum<V>> Supplier<V> define(String var1, V var2);

   public abstract <T> Supplier<T> defineObject(String var1, com.google.common.base.Supplier<T> var2, Codec<T> var3);

   public <T> Supplier<List<T>> defineObjectList(String name, com.google.common.base.Supplier<List<T>> defaultSupplier, Codec<T> codec) {
      return this.defineObject(name, defaultSupplier, codec.listOf());
   }

   public Supplier<Map<String, String>> defineMap(String name, Map<String, String> def) {
      return this.defineObject(name, () -> def, Codec.unboundedMap(Codec.STRING, Codec.STRING));
   }

   public Supplier<Map<ResourceLocation, ResourceLocation>> defineIDMap(String name, Map<ResourceLocation, ResourceLocation> def) {
      return this.defineObject(name, () -> def, Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC));
   }

   public abstract Supplier<JsonElement> defineJson(String var1, JsonElement var2);

   public abstract Supplier<JsonElement> defineJson(String var1, Supplier<JsonElement> var2);

   public <T> Supplier<T> defineBean(String name, T defaultValue) {
      Class<T> type = (Class<T>)defaultValue.getClass();
      this.push(name);

      Supplier var4;
      try {
         var4 = ConfigBeans.define(this, type, defaultValue);
      } finally {
         this.pop();
      }

      return var4;
   }

   public Supplier<ResourceLocation> define(String name, ResourceLocation defaultValue) {
      Supplier<String> handle = this.define(name, defaultValue.toString(), REGISTRY_ID_CHECK);
      return () -> ResourceLocation.parse(handle.get());
   }

   public Component description(String name) {
      return Component.translatable(this.translationKey(name));
   }

   public Component tooltip(String name) {
      return Component.translatable(this.tooltipKey(name));
   }

   public String tooltipKey(String name) {
      return this.name.getNamespace() + ".configuration." + this.currentCategory() + "." + name + ".description";
   }

   public String translationKey(String name) {
      return this.name.getNamespace() + ".configuration." + this.currentCategory() + (name.isEmpty() ? "" : "." + name);
   }

   public ConfigBuilder comment(String comment) {
      if (this.pendingComment != null) {
         this.applyComment(this.pendingComment);
      }

      this.pendingComment = comment;
      this.pendingCommentForwarded = false;
      return this;
   }

   public ConfigBuilder comment(String... comment) {
      return this.comment(String.join("\n", comment));
   }

   public ConfigBuilder icon(ResourceLocation id) {
      this.pendingIcon = id;
      return this;
   }

   public ConfigBuilder icon(String id) {
      return this.icon(id.indexOf(58) >= 0 ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(this.name.getNamespace(), id));
   }

   protected void flushPendingComment() {
      if (!this.suppressUi) {
         if (this.pendingComment != null) {
            this.applyComment(this.pendingComment);
            this.pendingComment = null;
         }
      }
   }

   @Nullable
   protected String pollCommentToForward() {
      if (this.pendingComment != null && !this.pendingCommentForwarded) {
         this.pendingCommentForwarded = true;
         return this.pendingComment;
      } else {
         return null;
      }
   }

   public ConfigBuilder pop(int count) {
      for (int i = 0; i < count; i++) {
         this.pop();
      }

      return this;
   }

   @Deprecated(
      forRemoval = true
   )
   public ConfigBuilder translation(String translationKey) {
      return this;
   }

   private void applyComment(String rawComment) {
      if (this.lastCommentKey != null) {
         this.translations.put(this.lastCommentKey, rawComment);
      }

      if (this.lastCommentTarget != null) {
         this.lastCommentTarget.applyComment(rawComment);
      }

      this.lastCommentTarget = null;
      this.lastCommentKey = null;
   }

   protected void noteDefined(String name, @Nullable ConfigNode uiNode, @Nullable Consumer<String> rawCommentSink) {
      if (!this.suppressUi) {
         String key = this.tooltipKey(name);
         Component description = Component.translatable(key);
         this.lastCommentKey = key;
         this.lastCommentTarget = raw -> {
            if (uiNode != null) {
               uiNode.setDescription(description);
            }

            if (rawCommentSink != null) {
               rawCommentSink.accept(raw);
            }
         };
         if (this.pendingComment != null) {
            this.applyComment(this.pendingComment);
            this.pendingComment = null;
         }

         if (this.pendingIcon != null && uiNode != null) {
            uiNode.setIcon(this.pendingIcon);
            this.pendingIcon = null;
         }
      }
   }

   protected void uiPush(Component title) {
      this.pendingComment = null;
      this.pendingCommentForwarded = false;
      if (!this.suppressUi) {
         ConfigCategory cat = new ConfigCategory(title);
         if (this.pendingIcon != null) {
            cat.setIcon(this.pendingIcon);
            this.pendingIcon = null;
         }

         this.uiStack.peek().add(cat);
         this.uiStack.push(cat);
         this.gateStack.push(this.gateStack.peek());
         this.categoryPath.addLast(this.currentCategory());
      }
   }

   protected void uiPop() {
      if (!this.suppressUi) {
         this.uiStack.pop();
         this.gateStack.pop();
         this.categoryPath.pollLast();
      }
   }

   protected String currentCategoryPath() {
      return String.join(".", this.categoryPath);
   }

   public Supplier<Boolean> mainFeature(boolean defaultEnabled) {
      ConfigCategory cat = this.uiStack.peek();
      if (cat == this.uiRoot) {
         throw new IllegalStateException("mainFeature() must be called inside a category (use push/pushFeature first), not at the config root");
      } else if (cat.gate() != null) {
         throw new IllegalStateException("category '" + this.currentCategory() + "' already has a mainFeature() toggle");
      } else {
         Supplier<Boolean> raw = this.define("enabled", defaultEnabled);
         List<ConfigNode> entries = cat.entries();
         if (!entries.isEmpty() && entries.getLast() instanceof ConfigOption.BooleanValue bv) {
            cat.setGate(bv);
            if (bv.icon() == null) {
               bv.setIcon(cat.icon() != null ? cat.icon() : this.inferFeatureIcon(this.currentCategory()));
            }

            if (cat.icon() == null) {
               cat.setIcon(bv.icon());
            }
         }

         Supplier<Boolean> ancestor = this.gateStack.peek();
         Supplier<Boolean> effective = () -> raw.get() && ancestor.get();
         this.gateStack.pop();
         this.gateStack.push(effective);
         this.registerFeature(this.currentCategory(), this.currentCategoryPath(), effective);
         return effective;
      }
   }

   public Supplier<Boolean> mainFeature() {
      return this.mainFeature(true);
   }

   public Supplier<Boolean> feature(String name, boolean defaultEnabled) {
      Supplier<Boolean> raw = this.define(name, defaultEnabled);
      List<ConfigNode> entries = this.uiStack.peek().entries();
      if (!entries.isEmpty() && entries.getLast() instanceof ConfigOption.BooleanValue bv) {
         bv.setFeature(true);
         if (bv.icon() == null) {
            bv.setIcon(this.inferFeatureIcon(name));
         }
      }

      Supplier<Boolean> ancestor = this.gateStack.peek();
      Supplier<Boolean> effective = () -> raw.get() && ancestor.get();
      String path = this.categoryPath.isEmpty() ? name : this.currentCategoryPath() + "." + name;
      this.registerFeature(name, path, effective);
      return effective;
   }

   public Supplier<Boolean> feature(String name) {
      return this.feature(name, true);
   }

   public Supplier<Boolean> pushFeature(String name, boolean defaultEnabled) {
      this.push(name);
      return this.mainFeature(defaultEnabled);
   }

   @Nullable
   private ResourceLocation inferFeatureIcon(String name) {
      return ResourceLocation.tryBuild(this.name.getNamespace(), name);
   }

   private void registerFeature(String name, String path, Supplier<Boolean> effective) {
      this.featureToggles.put(name, effective);
      this.featureToggles.put(path, effective);
   }

   protected Map<String, Supplier<Boolean>> getFeatureToggles() {
      return this.featureToggles;
   }

   public Supplier<Boolean> pushFeature(String name) {
      return this.pushFeature(name, true);
   }

   protected void recordOption(ConfigOption<?> option) {
      if (!this.suppressUi) {
         this.pendingReload = ConfigReloadType.NONE;
         this.pendingDynamicPacks = false;
         this.uiStack.peek().add(option);
      }
   }

   public ConfigCategory getUiRoot() {
      return this.uiRoot;
   }

   public Supplier<Range> defineRange(String name, Range defaultValue, double min, double max) {
      return this.defineRange(name, defaultValue.min(), defaultValue.max(), min, max);
   }

   public Supplier<Range> defineRange(String name, double defaultMin, double defaultMax, double min, double max) {
      this.suppressUi = true;
      this.push(name);
      Supplier<Double> minHandle = this.define("min", defaultMin, min, max);
      Supplier<Double> maxHandle = this.define("max", defaultMax, min, max);
      this.pop();
      this.suppressUi = false;
      this.putName(this.translationKey(name), name);
      ConfigOption.RangeValue node = new ConfigOption.RangeValue(
         this.description(name), null, minHandle, maxHandle, new Range(defaultMin, defaultMax), min, max
      );
      this.recordOption(node);
      this.noteDefined(name, node, null);
      return () -> new Range(minHandle.get(), maxHandle.get());
   }

   public Supplier<Vec3> defineVec3(String name, Vec3 defaultValue, double min, double max) {
      this.suppressUi = true;
      this.push(name);
      Supplier<Double> xHandle = this.define("x", defaultValue.x, min, max);
      Supplier<Double> yHandle = this.define("y", defaultValue.y, min, max);
      Supplier<Double> zHandle = this.define("z", defaultValue.z, min, max);
      this.pop();
      this.suppressUi = false;
      this.putName(this.translationKey(name), name);
      ConfigOption.Vec3Value node = new ConfigOption.Vec3Value(this.description(name), null, xHandle, yHandle, zHandle, defaultValue, min, max);
      this.recordOption(node);
      this.noteDefined(name, node, null);
      return () -> new Vec3(xHandle.get(), yHandle.get(), zHandle.get());
   }

   public Supplier<Vec3i> defineVec3i(String name, Vec3i defaultValue, int min, int max) {
      this.suppressUi = true;
      this.push(name);
      Supplier<Integer> xHandle = this.define("x", defaultValue.getX(), min, max);
      Supplier<Integer> yHandle = this.define("y", defaultValue.getY(), min, max);
      Supplier<Integer> zHandle = this.define("z", defaultValue.getZ(), min, max);
      this.pop();
      this.suppressUi = false;
      this.putName(this.translationKey(name), name);
      ConfigOption.Vec3iValue node = new ConfigOption.Vec3iValue(this.description(name), null, xHandle, yHandle, zHandle, defaultValue, min, max);
      this.recordOption(node);
      this.noteDefined(name, node, null);
      return () -> new Vec3i(xHandle.get(), yHandle.get(), zHandle.get());
   }

   public ConfigBuilder onChange(Runnable callback) {
      this.changeCallback = callback;
      return this;
   }

   protected Runnable buildChangeCallback() {
      return this.changeCallback;
   }

   public ConfigBuilder worldReload() {
      this.pendingReload = ConfigReloadType.WORLD_RELOAD;
      this.forwardReloadFlag(ConfigReloadType.WORLD_RELOAD);
      return this;
   }

   public ConfigBuilder gameRestart() {
      this.pendingReload = ConfigReloadType.GAME_RESTART;
      this.forwardReloadFlag(ConfigReloadType.GAME_RESTART);
      return this;
   }

   protected void forwardReloadFlag(ConfigReloadType type) {
   }

   protected void addTranslationsAndComments(String name) {
      this.putName(this.translationKey(name), name);
      if (this.currentCategory() == null && PlatHelper.isDev()) {
         throw new AssertionError("Current config category was null. How?");
      }
   }

   protected void noteCategoryName(String category) {
      this.putName(this.translationKey(""), category);
   }

   private void putName(String key, String rawName) {
      this.translations.put(key, TextHelper.getReadableName(rawName));
      if (ConfigLangExporter.BUILTIN_NAMES.contains(rawName)) {
         this.moonlightNames.put(key, rawName);
      }
   }

   public static ConfigBuilder create(ResourceLocation var0, ConfigType var1) {
      return ConfigBuilderImpl.create(var0, var1);
   }

   @FunctionalInterface
   protected interface CommentTarget {
      void applyComment(String var1);
   }

   private static class RegexPatternValue implements Supplier<Pattern> {
      private final Supplier<String> source;
      private String cachedSource;
      private Pattern cached;

      RegexPatternValue(Supplier<String> source) {
         this.source = source;
      }

      public Pattern get() {
         String s = this.source.get();
         if (this.cached == null || !s.equals(this.cachedSource)) {
            this.cachedSource = s;

            try {
               this.cached = Pattern.compile(s);
            } catch (Exception var3) {
               this.cached = Pattern.compile(Pattern.quote(s));
            }
         }

         return this.cached;
      }
   }
}
