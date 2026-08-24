package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.BlockFamily.Variant;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

public abstract class AbstractLanguageProvider implements DataProvider {
   public static final Map<BlockSetVariant, UnaryOperator<String>> VARIANT_BLOCK_NAMES = ImmutableMap.builder()
      .put(BlockSetVariant.CHISELED, (UnaryOperator)baseName -> "Chiseled " + baseName)
      .put(BlockSetVariant.CRACKED, (UnaryOperator)baseName -> "Cracked " + baseName)
      .put(BlockSetVariant.POLISHED, (UnaryOperator)baseName -> "Polished " + baseName)
      .put(BlockSetVariant.CUT, (UnaryOperator)baseName -> "Cut " + baseName)
      .put(BlockSetVariant.MOSAIC, (UnaryOperator)baseName -> baseName + " Mosaic")
      .put(BlockSetVariant.STAIRS, (UnaryOperator)baseName -> baseName + " Stairs")
      .put(BlockSetVariant.SLAB, (UnaryOperator)baseName -> baseName + " Slab")
      .put(BlockSetVariant.WALL, (UnaryOperator)baseName -> baseName + " Wall")
      .put(BlockSetVariant.FENCE, (UnaryOperator)baseName -> baseName + " Fence")
      .put(BlockSetVariant.FENCE_GATE, (UnaryOperator)baseName -> baseName + " Fence Gate")
      .put(BlockSetVariant.DOOR, (UnaryOperator)baseName -> baseName + " Door")
      .put(BlockSetVariant.TRAPDOOR, (UnaryOperator)baseName -> baseName + " Trapdoor")
      .put(BlockSetVariant.BUTTON, (UnaryOperator)baseName -> baseName + " Button")
      .put(BlockSetVariant.PRESSURE_PLATE, (UnaryOperator)baseName -> baseName + " Pressure Plate")
      .put(BlockSetVariant.SIGN, (UnaryOperator)baseName -> baseName + " Sign")
      .put(BlockSetVariant.HANGING_SIGN, (UnaryOperator)baseName -> baseName + " Hanging Sign")
      .build();
   public static final Map<BlockSetVariant, UnaryOperator<String>> VARIANT_ITEM_NAMES = ImmutableMap.builder()
      .put(BlockSetVariant.BOAT, (UnaryOperator)baseName -> baseName + " Boat")
      .put(BlockSetVariant.CHEST_BOAT, (UnaryOperator)baseName -> baseName + " Chest Boat")
      .build();
   public static final Map<BlockSetVariant, UnaryOperator<String>> VARIANT_ENTITY_NAMES = ImmutableMap.builder()
      .put(BlockSetVariant.BOAT, (UnaryOperator)baseName -> baseName + " Boat")
      .put(BlockSetVariant.CHEST_BOAT, (UnaryOperator)baseName -> baseName + " Chest Boat")
      .build();
   protected final String languageCode;
   protected final String modId;
   protected final PathProvider pathProvider;

   public AbstractLanguageProvider(DataProviderContext context) {
      this(context.getModId(), context.getPackOutput());
   }

   public AbstractLanguageProvider(String languageCode, DataProviderContext context) {
      this(languageCode, context.getModId(), context.getPackOutput());
   }

   public AbstractLanguageProvider(String modId, PackOutput packOutput) {
      this("en_us", modId, packOutput);
   }

   public AbstractLanguageProvider(String languageCode, String modId, PackOutput packOutput) {
      this.languageCode = languageCode;
      this.modId = modId;
      this.pathProvider = packOutput.createPathProvider(Target.RESOURCE_PACK, "lang");
   }

   public abstract void addTranslations(AbstractLanguageProvider.TranslationBuilder var1);

   public void generateFor(AbstractLanguageProvider.TranslationBuilder translationBuilder, BlockSetFamily blockSetFamily, String baseName) {
      this.generateFor(translationBuilder::add, blockSetFamily.getBlockVariants(), VARIANT_BLOCK_NAMES, baseName);
      this.generateFor(translationBuilder::add, blockSetFamily.getItemVariants(), VARIANT_ITEM_NAMES, baseName);
      this.generateFor(translationBuilder::add, blockSetFamily.getEntityVariants(), VARIANT_ENTITY_NAMES, baseName);
   }

   public <T> void generateFor(
      BiConsumer<T, String> translationConsumer,
      Map<BlockSetVariant, Reference<T>> variants,
      Map<BlockSetVariant, UnaryOperator<String>> variantNames,
      String baseName
   ) {
      variants.forEach((variant, holder) -> {
         UnaryOperator<String> variantName = variantNames.get(variant);
         if (variantName != null) {
            translationConsumer.accept((T)holder.value(), variantName.apply(baseName));
         }
      });
   }

   public CompletableFuture<?> run(CachedOutput cachedOutput) {
      JsonObject jsonObject = new JsonObject();
      this.addTranslations((translationKey, value) -> {
         Objects.requireNonNull(translationKey, "translation key is null");
         Objects.requireNonNull(value, "value is null");
         if (jsonObject.has(translationKey)) {
            throw new IllegalStateException("Created duplicate translation key: " + translationKey);
         } else {
            jsonObject.addProperty(translationKey, value);
         }
      });
      this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.BLOCK, AbstractLanguageProvider.TranslationBuilder::addBlock);
      this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.ITEM, AbstractLanguageProvider.TranslationBuilder::addItem);
      this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.ENTITY_TYPE, AbstractLanguageProvider.TranslationBuilder::addEntityType);
      this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.ATTRIBUTE, AbstractLanguageProvider.TranslationBuilder::addAttribute);
      this.verifyRequiredTranslationKeys(jsonObject::has, BuiltInRegistries.MOB_EFFECT, AbstractLanguageProvider.TranslationBuilder::addMobEffect);
      ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(this.modId, this.languageCode);
      return DataProvider.saveStable(cachedOutput, jsonObject, this.pathProvider.json(resourceLocation));
   }

   private <T> void verifyRequiredTranslationKeys(
      Predicate<String> predicate, Registry<T> registry, AbstractLanguageProvider.HolderTranslationCollector<T> holderTranslationCollector
   ) {
      registry.holders()
         .filter(holder -> holder.key().location().getNamespace().equals(this.modId))
         .forEach(holder -> holderTranslationCollector.accept((translationKey, value) -> {
            Objects.requireNonNull(translationKey, "translation key is null");
            if (this.mustHaveTranslationKey(holder, translationKey) && !predicate.test(translationKey)) {
               throw new IllegalStateException("Missing translation key '%s' for '%s'".formatted(translationKey, holder));
            }
         }, holder, ""));
   }

   protected boolean mustHaveTranslationKey(Reference<?> holder, String translationKey) {
      return true;
   }

   public String getName() {
      return "Language (%s)".formatted(this.languageCode);
   }

   public static class BlockFamilyBuilder {
      static final Map<Variant, BiFunction<AbstractLanguageProvider.BlockFamilyBuilder, Block, AbstractLanguageProvider.BlockFamilyBuilder>> VARIANT_FUNCTIONS = ImmutableMap.builder()
         .put(Variant.BUTTON, AbstractLanguageProvider.BlockFamilyBuilder::button)
         .put(Variant.CHISELED, AbstractLanguageProvider.BlockFamilyBuilder::chiseled)
         .put(Variant.CRACKED, AbstractLanguageProvider.BlockFamilyBuilder::cracked)
         .put(Variant.CUT, AbstractLanguageProvider.BlockFamilyBuilder::cut)
         .put(Variant.DOOR, AbstractLanguageProvider.BlockFamilyBuilder::door)
         .put(Variant.CUSTOM_FENCE, AbstractLanguageProvider.BlockFamilyBuilder::fence)
         .put(Variant.FENCE, AbstractLanguageProvider.BlockFamilyBuilder::fence)
         .put(Variant.CUSTOM_FENCE_GATE, AbstractLanguageProvider.BlockFamilyBuilder::fenceGate)
         .put(Variant.FENCE_GATE, AbstractLanguageProvider.BlockFamilyBuilder::fenceGate)
         .put(Variant.MOSAIC, AbstractLanguageProvider.BlockFamilyBuilder::mosaic)
         .put(Variant.SIGN, AbstractLanguageProvider.BlockFamilyBuilder::sign)
         .put(Variant.SLAB, AbstractLanguageProvider.BlockFamilyBuilder::slab)
         .put(Variant.STAIRS, AbstractLanguageProvider.BlockFamilyBuilder::stairs)
         .put(Variant.PRESSURE_PLATE, AbstractLanguageProvider.BlockFamilyBuilder::pressurePlate)
         .put(Variant.POLISHED, AbstractLanguageProvider.BlockFamilyBuilder::polished)
         .put(Variant.TRAPDOOR, AbstractLanguageProvider.BlockFamilyBuilder::trapdoor)
         .put(Variant.WALL, AbstractLanguageProvider.BlockFamilyBuilder::wall)
         .build();
      private final BiConsumer<Block, String> valueConsumer;
      private final String blockValue;
      private final String baseBlockValue;

      private BlockFamilyBuilder(BiConsumer<Block, String> valueConsumer, String blockValue) {
         this(valueConsumer, blockValue, blockValue);
      }

      private BlockFamilyBuilder(BiConsumer<Block, String> valueConsumer, String blockValue, String baseBlockValue) {
         this.valueConsumer = valueConsumer;
         this.blockValue = blockValue;
         this.baseBlockValue = baseBlockValue;
      }

      public void generateFor(BlockFamily blockFamily) {
         this.baseBlock(blockFamily.getBaseBlock());
         blockFamily.getVariants()
            .forEach(
               (variant, block) -> {
                  BiFunction<AbstractLanguageProvider.BlockFamilyBuilder, Block, AbstractLanguageProvider.BlockFamilyBuilder> variantFunction = VARIANT_FUNCTIONS.get(
                     variant
                  );
                  if (variantFunction != null) {
                     variantFunction.apply(this, block);
                  }
               }
            );
      }

      public AbstractLanguageProvider.BlockFamilyBuilder baseBlock(Block block) {
         this.valueConsumer.accept(block, this.baseBlockValue);
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder button(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Button");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder chiseled(Block block) {
         this.valueConsumer.accept(block, "Chiseled " + this.blockValue);
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder cracked(Block block) {
         this.valueConsumer.accept(block, "Cracked " + this.blockValue);
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder cut(Block block) {
         this.valueConsumer.accept(block, "Cut " + this.blockValue);
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder door(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Door");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder fence(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Fence");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder fenceGate(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Fence Gate");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder mosaic(Block block) {
         this.valueConsumer.accept(block, "Mosaic " + this.blockValue);
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder sign(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Sign");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder slab(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Slab");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder hangingSign(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Hanging Sign");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder stairs(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Stairs");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder pressurePlate(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Pressure Plate");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder polished(Block block) {
         this.valueConsumer.accept(block, "Polished " + this.blockValue);
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder trapdoor(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Trapdoor");
         return this;
      }

      public AbstractLanguageProvider.BlockFamilyBuilder wall(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Wall");
         return this;
      }
   }

   @FunctionalInterface
   protected interface HolderTranslationCollector<T> {
      void accept(AbstractLanguageProvider.TranslationBuilder var1, Holder<T> var2, String var3);
   }

   @FunctionalInterface
   @NonExtendable
   public interface TranslationBuilder {
      void add(String var1, String var2);

      default void add(String translationKey, String additionalKey, String value) {
         Objects.requireNonNull(additionalKey, "additional key is null");
         this.add(translationKey + (additionalKey.isEmpty() ? "" : "." + additionalKey), value);
      }

      default void add(ResourceLocation resourceLocation, String value) {
         this.add(resourceLocation, "", value);
      }

      default void add(ResourceLocation resourceLocation, String additionalKey, String value) {
         Objects.requireNonNull(resourceLocation, "resource location is null");
         this.add(resourceLocation.toLanguageKey(), additionalKey, value);
      }

      default void add(Component component) {
         Objects.requireNonNull(component, "component is null");
         if (component.getContents() instanceof TranslatableContents contents && contents.getFallback() != null) {
            this.add(contents.getKey(), contents.getFallback());
         } else {
            throw new IllegalArgumentException("Unsupported component: " + component);
         }
      }

      default void add(Component component, String value) {
         Objects.requireNonNull(component, "component is null");
         if (component.getContents() instanceof TranslatableContents contents) {
            this.add(contents.getKey(), value);
         } else {
            throw new IllegalArgumentException("Unsupported component: " + component);
         }
      }

      default void add(Holder<?> holder, String value) {
         this.add(holder, "", value);
      }

      default void add(Holder<?> holder, String additionalKey, String value) {
         Objects.requireNonNull(holder, "holder is null");
         this.add((ResourceKey<?>)holder.unwrapKey().orElseThrow(), additionalKey, value);
      }

      default void add(ResourceKey<?> resourceKey, String value) {
         this.add(resourceKey, "", value);
      }

      default void add(ResourceKey<?> resourceKey, String additionalKey, String value) {
         Objects.requireNonNull(resourceKey, "resource key is null");
         String registry = Registries.elementsDirPath(resourceKey.registryKey());
         this.add(registry, resourceKey.location(), additionalKey, value);
      }

      default void add(String registry, ResourceLocation resourceLocation, String value) {
         this.add(registry, resourceLocation, "", value);
      }

      default void add(String registry, ResourceLocation resourceLocation, String additionalKey, String value) {
         Objects.requireNonNull(registry, "registry is null");
         Objects.requireNonNull(resourceLocation, "resource location is null");
         this.add(resourceLocation.toLanguageKey(registry), additionalKey, value);
      }

      default void add(TagKey<?> tagKey, String value) {
         Objects.requireNonNull(tagKey, "tag key is null");
         String registry = Registries.elementsDirPath(tagKey.registry());
         this.add("tag." + tagKey.location().toLanguageKey(registry), value);
      }

      default AbstractLanguageProvider.BlockFamilyBuilder blockFamily(String blockValue) {
         return new AbstractLanguageProvider.BlockFamilyBuilder(this::add, blockValue);
      }

      default AbstractLanguageProvider.BlockFamilyBuilder blockFamily(String blockValue, String baseBlockValue) {
         return new AbstractLanguageProvider.BlockFamilyBuilder(this::add, blockValue, baseBlockValue);
      }

      @Deprecated
      default void add(String registry, Holder<?> holder, String value) {
         Objects.requireNonNull(registry, "registry is null");
         Objects.requireNonNull(holder, "holder is null");
         this.add(registry, (ResourceKey<?>)holder.unwrapKey().orElseThrow(), value);
      }

      @Deprecated
      default void add(String registry, ResourceKey<?> resourceKey, String value) {
         Objects.requireNonNull(registry, "registry is null");
         Objects.requireNonNull(resourceKey, "resource key is null");
         this.add(registry, resourceKey.location(), value);
      }

      default void addBlock(Holder<Block> block, String value) {
         Objects.requireNonNull(block, "block is null");
         this.add((Block)block.value(), value);
      }

      default void add(Block block, String value) {
         this.add(block, "", value);
      }

      default void add(Block block, String additionalKey, String value) {
         Objects.requireNonNull(block, "block is null");
         this.add(block.getDescriptionId(), additionalKey, value);
      }

      default void addItem(Holder<Item> item, String value) {
         Objects.requireNonNull(item, "item is null");
         this.add((Item)item.value(), value);
      }

      default void add(Item item, String value) {
         this.add(item, "", value);
      }

      default void add(Item item, String additionalKey, String value) {
         Objects.requireNonNull(item, "item is null");
         this.add(item.getDescriptionId(), additionalKey, value);
      }

      default void addSpawnEgg(Item item, String value) {
         Objects.requireNonNull(item, "item is null");
         if (item instanceof SpawnEggItem) {
            this.add(item, value + " Spawn Egg");
         } else {
            throw new IllegalArgumentException("Unsupported item: " + item);
         }
      }

      @Deprecated
      default void addEnchantment(ResourceKey<Enchantment> enchantment, String value) {
         this.addEnchantment(enchantment, "", value);
      }

      @Deprecated
      default void addEnchantment(ResourceKey<Enchantment> enchantment, String additionalKey, String value) {
         Objects.requireNonNull(enchantment, "enchantment is null");
         String translationKey = Util.makeDescriptionId(enchantment.registry().getPath(), enchantment.location());
         this.add(translationKey, additionalKey, value);
      }

      default void addMobEffect(Holder<MobEffect> mobEffect, String value) {
         Objects.requireNonNull(mobEffect, "mob effect is null");
         this.add((MobEffect)mobEffect.value(), value);
      }

      default void add(MobEffect mobEffect, String value) {
         this.add(mobEffect, "", value);
      }

      default void add(MobEffect mobEffect, String additionalKey, String value) {
         Objects.requireNonNull(mobEffect, "mob effect is null");
         this.add(mobEffect.getDescriptionId(), additionalKey, value);
      }

      default void addEntityType(Holder<? extends EntityType<?>> entityType, String value) {
         Objects.requireNonNull(entityType, "entity type is null");
         this.add((EntityType<?>)entityType.value(), value);
      }

      default void add(EntityType<?> entityType, String value) {
         this.add(entityType, "", value);
      }

      default void add(EntityType<?> entityType, String additionalKey, String value) {
         Objects.requireNonNull(entityType, "entity type is null");
         this.add(entityType.getDescriptionId(), additionalKey, value);
      }

      default void addAttribute(Holder<Attribute> attribute, String value) {
         Objects.requireNonNull(attribute, "attribute is null");
         this.add((Attribute)attribute.value(), value);
      }

      default void add(Attribute attribute, String value) {
         this.add(attribute, "", value);
      }

      default void add(Attribute attribute, String additionalKey, String value) {
         Objects.requireNonNull(attribute, "attribute is null");
         this.add(attribute.getDescriptionId(), additionalKey, value);
      }

      default void addStatType(Holder<StatType<?>> statType, String value) {
         Objects.requireNonNull(statType, "stat type is null");
         this.add((StatType<?>)statType.value(), value);
      }

      default void add(StatType<?> statType, String value) {
         this.add(statType, "", value);
      }

      default void add(StatType<?> statType, String additionalKey, String value) {
         Objects.requireNonNull(statType, "stat type is null");
         Objects.requireNonNull(statType.getDisplayName(), "component is null");
         if (statType.getDisplayName().getContents() instanceof TranslatableContents contents) {
            this.add(contents.getKey(), additionalKey, value);
         } else {
            throw new IllegalArgumentException("Unsupported component: " + statType.getDisplayName());
         }
      }

      default void addPotion(Holder<Potion> potion, String value) {
         Objects.requireNonNull(potion, "potion is null");
         Function<Item, String> potionNameGetter = item -> Potion.getName(Optional.of(potion), item.getDescriptionId() + ".effect.");
         this.add(potionNameGetter.apply(Items.TIPPED_ARROW), "Arrow of " + value);
         this.add(potionNameGetter.apply(Items.POTION), "Potion of " + value);
         this.add(potionNameGetter.apply(Items.SPLASH_POTION), "Splash Potion of " + value);
         this.add(potionNameGetter.apply(Items.LINGERING_POTION), "Lingering Potion of " + value);
      }

      default void addSoundEvent(Holder<SoundEvent> soundEvent, String value) {
         Objects.requireNonNull(soundEvent, "sound event is null");
         this.add((SoundEvent)soundEvent.value(), value);
      }

      default void add(SoundEvent soundEvent, String value) {
         Objects.requireNonNull(soundEvent, "sound event is null");
         this.add("subtitles." + soundEvent.getLocation().getPath(), value);
      }

      default void addCreativeModeTab(Holder<CreativeModeTab> creativeModeTab, String value) {
         Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
         this.add((CreativeModeTab)creativeModeTab.value(), value);
      }

      default void add(CreativeModeTab creativeModeTab, String value) {
         Objects.requireNonNull(creativeModeTab, "creative mode tab is null");
         this.add(creativeModeTab.getDisplayName(), value);
      }

      @Deprecated
      default void addCreativeModeTab(String modId, String value) {
         this.addCreativeModeTab(modId, "main", value);
      }

      @Deprecated
      default void addCreativeModeTab(String modId, String tabId, String value) {
         Objects.requireNonNull(modId, "mod id is null");
         Objects.requireNonNull(tabId, "tab id is null");
         this.addCreativeModeTab(ResourceLocationHelper.fromNamespaceAndPath(modId, tabId), value);
      }

      @Deprecated
      default void addCreativeModeTab(ResourceLocation resourceLocation, String value) {
         Objects.requireNonNull(resourceLocation, "resource location is null");
         this.addCreativeModeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, resourceLocation), value);
      }

      @Deprecated
      default void addCreativeModeTab(ResourceKey<CreativeModeTab> resourceKey, String value) {
         Objects.requireNonNull(resourceKey, "resource key is null");
         this.add((CreativeModeTab)BuiltInRegistries.CREATIVE_MODE_TAB.get(resourceKey), value);
      }

      default void addBiome(ResourceKey<Biome> biome, String value) {
         Objects.requireNonNull(biome, "biome is null");
         this.add(biome.location().toLanguageKey("biome"), value);
      }

      default void addGenericDamageType(ResourceKey<DamageType> damageType, String value) {
         Objects.requireNonNull(damageType, "damage type is null");
         this.add("death.attack." + damageType.location().getPath(), value);
      }

      default void addPlayerDamageType(ResourceKey<DamageType> damageType, String value) {
         Objects.requireNonNull(damageType, "damage type is null");
         this.add("death.attack." + damageType.location().getPath() + ".player", value);
      }

      default void addItemDamageType(ResourceKey<DamageType> damageType, String value) {
         Objects.requireNonNull(damageType, "damage type is null");
         this.add("death.attack." + damageType.location().getPath() + ".item", value);
      }

      default void addPaintingVariant(ResourceKey<PaintingVariant> paintingVariant, String title, String author) {
         Objects.requireNonNull(paintingVariant, "painting variant is null");
         this.add(paintingVariant.location().toLanguageKey("painting", "title"), title);
         this.add(paintingVariant.location().toLanguageKey("painting", "author"), author);
      }

      default void add(KeyMapping keyMapping, String value) {
         Objects.requireNonNull(keyMapping, "key mapping is null");
         this.add(keyMapping.getName(), value);
      }

      default void addKeyCategory(String modId, String value) {
         this.add("key.categories." + modId, value);
      }

      default void add(Key<?> gameRule, String value) {
         this.add(gameRule, "", value);
      }

      default void addGameRuleDescription(Key<?> gameRule, String value) {
         this.add(gameRule, "description", value);
      }

      default void add(Key<?> gameRule, String additionalKey, String value) {
         Objects.requireNonNull(gameRule, "game rule is null");
         this.add(gameRule.getDescriptionId(), additionalKey, value);
      }
   }
}
