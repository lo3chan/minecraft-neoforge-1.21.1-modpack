package fuzs.puzzleslib.api.init.v3.registry;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.core.v1.utility.EnvironmentAwareBuilder;
import fuzs.puzzleslib.api.item.v2.ItemEquipmentFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.item.CreativeModeTabHelper;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo.Template;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;

public interface RegistryManager extends EnvironmentAwareBuilder<RegistryManager> {
   static RegistryManager from(String modId) {
      return ModContext.get(modId).getRegistryManager();
   }

   default <T> ResourceKey<T> makeResourceKey(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
      return ResourceKey.create(registryKey, this.makeKey(path));
   }

   ResourceLocation makeKey(String var1);

   default String makeDescriptionId(ResourceKey<? extends Registry<?>> registryKey, String path) {
      return Util.makeDescriptionId(Registries.elementsDirPath(registryKey), this.makeKey(path));
   }

   <T> Reference<T> registerLazily(ResourceKey<? extends Registry<? super T>> var1, String var2);

   <T> Reference<T> register(ResourceKey<? extends Registry<? super T>> var1, String var2, Supplier<T> var3);

   default Reference<Block> registerSimpleBlock(String path, Supplier<Properties> blockPropertiesSupplier) {
      return this.registerBlock(path, Block::new, blockPropertiesSupplier);
   }

   default Reference<Block> registerBlock(String path, Function<Properties, Block> factory, Supplier<Properties> blockPropertiesSupplier) {
      return this.register(Registries.BLOCK, path, () -> factory.apply(blockPropertiesSupplier.get()));
   }

   @Deprecated
   default Reference<Block> registerBlock(String path, Supplier<Block> entry) {
      return this.registerBlock(path, properties -> entry.get(), Properties::of);
   }

   default Reference<Item> registerItem(String path) {
      return this.registerSimpleItem(path, net.minecraft.world.item.Item.Properties::new);
   }

   default Reference<Item> registerSimpleItem(String path, Supplier<net.minecraft.world.item.Item.Properties> itemPropertiesSupplier) {
      return this.registerItem(path, Item::new, itemPropertiesSupplier);
   }

   default Reference<Item> registerItem(String path, Function<net.minecraft.world.item.Item.Properties, Item> factory) {
      return this.registerItem(path, factory, net.minecraft.world.item.Item.Properties::new);
   }

   default Reference<Item> registerItem(
      String path, Function<net.minecraft.world.item.Item.Properties, Item> factory, Supplier<net.minecraft.world.item.Item.Properties> itemPropertiesSupplier
   ) {
      return this.register(Registries.ITEM, path, () -> factory.apply(itemPropertiesSupplier.get()));
   }

   @Deprecated
   default Reference<Item> registerItem(String path, Supplier<Item> entry) {
      return this.registerItem(path, properties -> entry.get());
   }

   default Reference<Item> registerBlockItem(Holder<Block> block) {
      return this.registerBlockItem(block, net.minecraft.world.item.Item.Properties::new);
   }

   default Reference<Item> registerBlockItem(Holder<Block> block, Supplier<net.minecraft.world.item.Item.Properties> itemPropertiesSupplier) {
      return this.registerBlockItem(block, BlockItem::new, itemPropertiesSupplier);
   }

   default Reference<Item> registerBlockItem(Holder<Block> block, BiFunction<Block, net.minecraft.world.item.Item.Properties, ? extends BlockItem> itemFactory) {
      return this.registerBlockItem(block, itemFactory, net.minecraft.world.item.Item.Properties::new);
   }

   default Reference<Item> registerBlockItem(
      Holder<Block> block,
      BiFunction<Block, net.minecraft.world.item.Item.Properties, ? extends BlockItem> factory,
      Supplier<net.minecraft.world.item.Item.Properties> itemPropertiesSupplier
   ) {
      return this.registerItem(
         ((ResourceKey)block.unwrapKey().orElseThrow()).location().getPath(),
         itemProperties -> (Item)factory.apply((Block)block.value(), itemProperties),
         itemPropertiesSupplier
      );
   }

   @Deprecated
   default Reference<Item> registerBlockItem(Holder<Block> block, net.minecraft.world.item.Item.Properties itemProperties) {
      return this.registerBlockItem(block, (Supplier<net.minecraft.world.item.Item.Properties>)(() -> itemProperties));
   }

   default Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeHolder) {
      return this.registerLegacySpawnEggItem(entityTypeHolder, -1, -1);
   }

   Reference<Item> registerLegacySpawnEggItem(Holder<? extends EntityType<? extends Mob>> var1, int var2);

   Reference<Item> registerLegacySpawnEggItem(Holder<? extends EntityType<? extends Mob>> var1, int var2, int var3);

   @Deprecated
   default Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor) {
      return this.registerLegacySpawnEggItem(entityTypeReference, backgroundColor, highlightColor);
   }

   @Deprecated
   default Reference<Item> registerSpawnEggItem(
      Holder<? extends EntityType<? extends Mob>> entityTypeReference,
      int backgroundColor,
      int highlightColor,
      net.minecraft.world.item.Item.Properties itemProperties
   ) {
      return this.registerLegacySpawnEggItem(entityTypeReference, backgroundColor, highlightColor);
   }

   default Reference<CreativeModeTab> registerCreativeModeTab(Holder<? extends ItemLike> iconHolder) {
      return this.registerCreativeModeTab((Supplier<ItemStack>)(() -> new ItemStack((ItemLike)iconHolder.value())));
   }

   default Reference<CreativeModeTab> registerCreativeModeTab(Supplier<ItemStack> iconSupplier) {
      ResourceLocation resourceLocation = this.makeKey("main");
      return this.registerCreativeModeTab(
         resourceLocation.getPath(), iconSupplier, CreativeModeTabHelper.getDisplayItems(resourceLocation.getNamespace()), false
      );
   }

   default Reference<CreativeModeTab> registerCreativeModeTab(Supplier<ItemStack> iconSupplier, DisplayItemsGenerator displayItems) {
      return this.registerCreativeModeTab("main", iconSupplier, displayItems, false);
   }

   Reference<CreativeModeTab> registerCreativeModeTab(String var1, Supplier<ItemStack> var2, DisplayItemsGenerator var3, boolean var4);

   default <T> Reference<DataComponentType<T>> registerDataComponentType(String path, UnaryOperator<Builder<T>> operator) {
      return this.register(Registries.DATA_COMPONENT_TYPE, path, () -> operator.apply(DataComponentType.builder()).build());
   }

   default Reference<Fluid> registerFluid(String path, Supplier<Fluid> entry) {
      return this.register(Registries.FLUID, path, entry);
   }

   default Reference<MobEffect> registerMobEffect(String path, Supplier<MobEffect> entry) {
      return this.register(Registries.MOB_EFFECT, path, entry);
   }

   default Reference<SoundEvent> registerSoundEvent(String path) {
      return this.register(Registries.SOUND_EVENT, path, () -> SoundEvent.createVariableRangeEvent(this.makeKey(path)));
   }

   default Reference<Potion> registerPotion(String path, Supplier<Potion> potionSupplier) {
      return this.registerPotion(path, name -> potionSupplier.get());
   }

   default Reference<Potion> registerPotion(String path, Function<String, Potion> potionFactory) {
      return this.register(Registries.POTION, path, () -> potionFactory.apply(path));
   }

   default ResourceKey<Enchantment> registerEnchantment(String path) {
      return this.makeResourceKey(Registries.ENCHANTMENT, path);
   }

   default <T> Reference<DataComponentType<T>> registerEnchantmentEffectComponentType(String path, UnaryOperator<Builder<T>> operator) {
      return this.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, path, () -> operator.apply(DataComponentType.builder()).build());
   }

   default <T extends Entity> Reference<EntityType<T>> registerEntityType(String path, Supplier<net.minecraft.world.entity.EntityType.Builder<T>> entry) {
      return this.register(Registries.ENTITY_TYPE, path, () -> entry.get().build(path));
   }

   default <T extends BlockEntity> Reference<BlockEntityType<T>> registerBlockEntityType(
      String path, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Holder<Block> validBlock
   ) {
      return this.registerBlockEntityType(path, blockEntityFactory, (Supplier<Set<Block>>)(() -> Collections.singleton((Block)validBlock.value())));
   }

   default <T extends BlockEntity> Reference<BlockEntityType<T>> registerBlockEntityType(
      String path, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<Set<Block>> validBlocks
   ) {
      return this.register(
         Registries.BLOCK_ENTITY_TYPE,
         path,
         () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(blockEntityFactory::apply, validBlocks.get().toArray(Block[]::new))
            .build(null)
      );
   }

   @Deprecated
   default <T extends BlockEntity> Reference<BlockEntityType<T>> registerBlockEntityType(
      String path, Supplier<net.minecraft.world.level.block.entity.BlockEntityType.Builder<T>> entry
   ) {
      return this.register(Registries.BLOCK_ENTITY_TYPE, path, () -> entry.get().build(null));
   }

   default <T extends AbstractContainerMenu> Reference<MenuType<T>> registerMenuType(String path, MenuSupplier<T> menuSupplier) {
      return this.register(Registries.MENU, path, () -> new MenuType(menuSupplier, FeatureFlags.DEFAULT_FLAGS));
   }

   <T extends AbstractContainerMenu, S> Reference<MenuType<T>> registerMenuType(
      String var1, MenuSupplierWithData<T, S> var2, StreamCodec<? super RegistryFriendlyByteBuf, S> var3
   );

   @Deprecated
   default <T extends AbstractContainerMenu> Reference<MenuType<T>> registerMenuType(String path, Supplier<MenuSupplier<T>> entry) {
      return this.register(Registries.MENU, path, () -> new MenuType(entry.get(), FeatureFlags.DEFAULT_FLAGS));
   }

   @Deprecated
   <T extends AbstractContainerMenu> Reference<MenuType<T>> registerExtendedMenuType(String var1, Supplier<ExtendedMenuSupplier<T>> var2);

   default Reference<PoiType> registerPoiType(String path, Holder<Block> matchingBlock) {
      return this.registerSetPoiType(path, () -> Collections.singleton((Block)matchingBlock.value()));
   }

   default Reference<PoiType> registerSetPoiType(String path, Supplier<Set<Block>> matchingBlocks) {
      return this.registerPoiType(
         path,
         0,
         1,
         () -> matchingBlocks.get().stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(ImmutableSet.toImmutableSet())
      );
   }

   Reference<PoiType> registerPoiType(String var1, int var2, int var3, Supplier<Set<BlockState>> var4);

   @Deprecated
   default Reference<PoiType> registerPoiType(String path, Supplier<Block> matchingBlock) {
      return this.registerSetPoiType(path, () -> Collections.singleton(matchingBlock.get()));
   }

   @Deprecated
   default Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
      return this.registerPoiType(path, maxTickets, validRange, matchingStates);
   }

   default <A extends ArgumentType<?>> Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(
      String path, Class<? extends A> argumentClass, Supplier<A> argumentType
   ) {
      return this.registerArgumentType(path, argumentClass, SingletonArgumentInfo.contextFree(argumentType));
   }

   <A extends ArgumentType<?>, T extends Template<A>> Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(
      String var1, Class<? extends A> var2, ArgumentTypeInfo<A, T> var3
   );

   default <T extends Recipe<?>> Reference<RecipeType<T>> registerRecipeType(String path) {
      return this.register(Registries.RECIPE_TYPE, path, () -> {
         final ResourceLocation resourceLocation = this.makeKey(path);
         return new RecipeType<T>() {
            @Override
            public String toString() {
               return resourceLocation.toString();
            }
         };
      });
   }

   default Reference<GameEvent> registerGameEvent(String path, int notificationRadius) {
      return this.register(Registries.GAME_EVENT, path, () -> new GameEvent(notificationRadius));
   }

   default Reference<SimpleParticleType> registerParticleType(String path) {
      return this.register(Registries.PARTICLE_TYPE, path, () -> new SimpleParticleType(false));
   }

   default <T extends ParticleOptions> Reference<ParticleType<T>> registerParticleType(
      String path,
      boolean overrideLimiter,
      Function<ParticleType<T>, MapCodec<T>> codecGetter,
      Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
   ) {
      return this.register(Registries.PARTICLE_TYPE, path, () -> new ParticleType<T>(overrideLimiter) {
         public MapCodec<T> codec() {
            return (MapCodec<T>)codecGetter.apply(this);
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
            return (StreamCodec<? super RegistryFriendlyByteBuf, T>)streamCodecGetter.apply(this);
         }
      });
   }

   default Reference<Attribute> registerAttribute(String path, double defaultValue, double minValue, double maxValue) {
      return this.registerAttribute(path, defaultValue, minValue, maxValue, true, Sentiment.POSITIVE);
   }

   default Reference<Attribute> registerAttribute(String path, double defaultValue, double minValue, double maxValue, boolean syncable, Sentiment sentiment) {
      Objects.requireNonNull(sentiment, "sentiment is null");
      return this.register(
         Registries.ATTRIBUTE,
         path,
         () -> new RangedAttribute(this.makeDescriptionId(Registries.ATTRIBUTE, path), defaultValue, minValue, maxValue)
            .setSyncable(syncable)
            .setSentiment(sentiment)
      );
   }

   <T> Reference<EntityDataSerializer<T>> registerEntityDataSerializer(String var1, Supplier<EntityDataSerializer<T>> var2);

   @Deprecated
   default Reference<ArmorMaterial> registerArmorMaterial(String path, Holder<Item> repairItem) {
      return this.registerArmorMaterial(path, ItemEquipmentFactories.toArmorTypeMapWithFallback(1), 0, repairItem);
   }

   @Deprecated
   default Reference<ArmorMaterial> registerArmorMaterial(String path, Map<Type, Integer> defense, int enchantmentValue, Holder<Item> repairItem) {
      return this.registerArmorMaterial(
         path, defense, enchantmentValue, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(new ItemLike[]{(ItemLike)repairItem.value()}), 0.0F, 0.0F
      );
   }

   @Deprecated
   default Reference<ArmorMaterial> registerArmorMaterial(
      String path,
      Map<Type, Integer> defense,
      int enchantmentValue,
      Holder<SoundEvent> equipSound,
      Supplier<Ingredient> repairIngredient,
      float toughness,
      float knockbackResistance
   ) {
      return this.registerArmorMaterial(
         path,
         (Supplier<ArmorMaterial>)(() -> new ArmorMaterial(
            defense, enchantmentValue, equipSound, repairIngredient, Collections.singletonList(new Layer(this.makeKey(path))), toughness, knockbackResistance
         ))
      );
   }

   default Reference<ArmorMaterial> registerArmorMaterial(String path, Supplier<ArmorMaterial> armorMaterialSupplier) {
      return this.register(Registries.ARMOR_MATERIAL, path, armorMaterialSupplier);
   }

   default ResourceKey<DamageType> registerDamageType(String path) {
      return this.makeResourceKey(Registries.DAMAGE_TYPE, path);
   }

   default ResourceKey<TrimMaterial> registerTrimMaterial(String path) {
      return this.makeResourceKey(Registries.TRIM_MATERIAL, path);
   }

   default ResourceKey<LootTable> registerLootTable(String path) {
      return this.makeResourceKey(Registries.LOOT_TABLE, path);
   }

   <T> void prepareTag(ResourceKey<? extends Registry<? super T>> var1, TagKey<T> var2);
}
