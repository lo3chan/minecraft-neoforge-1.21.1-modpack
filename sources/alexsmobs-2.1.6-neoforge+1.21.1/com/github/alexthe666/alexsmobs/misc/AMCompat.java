package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.entity.EntityPotoo;
import com.github.alexthe666.alexsmobs.entity.EntitySugarGlider;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.MobSpawnSettings.Builder;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.Tags.Items;

public class AMCompat {
   public static final ResourceLocation BASE_ATTACK_DAMAGE_ID = Item.BASE_ATTACK_DAMAGE_ID;
   public static final ResourceLocation BASE_ATTACK_SPEED_ID = Item.BASE_ATTACK_SPEED_ID;
   private static final ThreadLocal<boolean[]> CANCEL_FLAG = ThreadLocal.withInitial(() -> new boolean[1]);
   private static final AtomicInteger CLIENT_PART_ID = new AtomicInteger();
   public static final String PERSISTED_NBT_TAG = "PlayerPersisted";

   @Nullable
   public static UUID getOwnerUUID(TamableAnimal animal) {
      return animal.getOwnerUUID();
   }

   public static void setOwnerUUID(TamableAnimal animal, @Nullable UUID uuid) {
      animal.setOwnerUUID(uuid);
   }

   public static void addSpawn(Builder b, MobCategory cat, EntityType<?> type, int weight, int min, int max) {
      b.addSpawn(cat, new SpawnerData(type, weight, min, max));
   }

   public static boolean canShieldBlock(ItemStack stack) {
      return stack.canPerformAction(ItemAbilities.SHIELD_BLOCK);
   }

   public static Properties shieldProperties(Properties props) {
      return props;
   }

   public static Properties glider(Properties props) {
      return props;
   }

   public static ResourceLocation rl(String namespace, String path) {
      return ResourceLocation.fromNamespaceAndPath(namespace, path);
   }

   public static ResourceLocation rl(String location) {
      return ResourceLocation.parse(location);
   }

   public static AttributeModifier attributeModifier(ResourceLocation id, String name, double amount, Operation operation) {
      return new AttributeModifier(id, amount, operation);
   }

   public static AttributeModifier attributeModifier(String name, double amount, Operation operation) {
      return new AttributeModifier(nameAsId(name), amount, operation);
   }

   private static ResourceLocation nameAsId(String name) {
      return rl("alexsmobs", name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]+", "_"));
   }

   public static ResourceLocation attrModId(String uuid, String name) {
      return rl("alexsmobs", name);
   }

   public static boolean hasModifier(@Nullable AttributeInstance instance, ResourceLocation id) {
      return instance != null && instance.hasModifier(id);
   }

   @Nullable
   public static CompoundTag getTag(ItemStack stack) {
      CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      return data == null ? null : data.copyTag();
   }

   public static CompoundTag getOrCreateTag(ItemStack stack) {
      CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      return data == null ? new CompoundTag() : data.copyTag();
   }

   public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
      if (tag == null) {
         stack.remove(DataComponents.CUSTOM_DATA);
      } else {
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }
   }

   public static boolean hasTag(ItemStack stack) {
      CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      return data != null && !data.isEmpty();
   }

   @Nullable
   public static CompoundTag getTagElement(ItemStack stack, String key) {
      CompoundTag tag = getTag(stack);
      return tag != null && contains(tag, key, 10) ? getCompound(tag, key) : null;
   }

   public static void addTagElement(ItemStack stack, String key, Tag value) {
      CompoundTag tag = getOrCreateTag(stack);
      tag.put(key, value);
      setTag(stack, tag);
   }

   public static ItemStack setHoverName(ItemStack stack, @Nullable Component name) {
      stack.set(DataComponents.CUSTOM_NAME, name);
      return stack;
   }

   public static boolean isEdible(Item item) {
      return item.components().has(DataComponents.FOOD);
   }

   public static boolean isEdible(ItemStack stack) {
      return stack.has(DataComponents.FOOD);
   }

   @Nullable
   public static FoodProperties getFoodProperties(Item item) {
      return (FoodProperties)item.components().get(DataComponents.FOOD);
   }

   public static boolean shouldRiderSit(Entity vehicle) {
      return vehicle.shouldRiderSit();
   }

   public static boolean isMeat(Item item) {
      return AMCompat.Meats.SET.contains(item);
   }

   public static LootTable lootTable(MinecraftServer server, ResourceLocation id) {
      return server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
   }

   public static ResourceKey<LootTable> lootKey(ResourceLocation id) {
      return ResourceKey.create(Registries.LOOT_TABLE, id);
   }

   public static LootTable fishingLoot(MinecraftServer server) {
      return server.reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
   }

   public static ItemStack loadItem(Provider provider, CompoundTag tag) {
      return ItemStack.parseOptional(provider, tag);
   }

   public static CompoundTag saveItem(Provider provider, ItemStack stack) {
      return (CompoundTag)stack.saveOptional(provider);
   }

   public static void saveAllItems(Provider provider, CompoundTag tag, NonNullList<ItemStack> items) {
      ContainerHelper.saveAllItems(tag, items, provider);
   }

   public static void loadAllItems(Provider provider, CompoundTag tag, NonNullList<ItemStack> items) {
      ContainerHelper.loadAllItems(tag, items, provider);
   }

   public static ListTag createTag(Provider provider, SimpleContainer container) {
      return container.createTag(provider);
   }

   public static void fromTag(Provider provider, SimpleContainer container, ListTag list) {
      container.fromTag(list, provider);
   }

   public static int getDyedColor(ItemStack stack, int fallback) {
      return DyedItemColor.getOrDefault(stack, fallback);
   }

   public static boolean hasCustomColor(ItemStack stack) {
      return stack.has(DataComponents.DYED_COLOR);
   }

   @Nullable
   public static BlockPos readBlockPos(CompoundTag tag, String key) {
      return (BlockPos)NbtUtils.readBlockPos(tag, key).orElse(null);
   }

   public static int getInt(CompoundTag t, String k) {
      return t.getInt(k);
   }

   public static boolean getBoolean(CompoundTag t, String k) {
      return t.getBoolean(k);
   }

   public static float getFloat(CompoundTag t, String k) {
      return t.getFloat(k);
   }

   public static double getDouble(CompoundTag t, String k) {
      return t.getDouble(k);
   }

   public static String getString(CompoundTag t, String k) {
      return t.getString(k);
   }

   public static byte getByte(CompoundTag t, String k) {
      return t.getByte(k);
   }

   public static long getLong(CompoundTag t, String k) {
      return t.getLong(k);
   }

   public static CompoundTag getCompound(CompoundTag t, String k) {
      return t.getCompound(k);
   }

   public static CompoundTag getCompound(ListTag t, int i) {
      return t.getCompound(i);
   }

   public static String getString(ListTag t, int i) {
      return t.getString(i);
   }

   public static double getDouble(ListTag t, int i) {
      return t.getDouble(i);
   }

   public static float getFloat(ListTag t, int i) {
      return t.getFloat(i);
   }

   public static int getInt(ListTag t, int i) {
      return t.getInt(i);
   }

   public static ListTag getList(CompoundTag t, String k, int type) {
      return t.getList(k, type);
   }

   public static boolean contains(CompoundTag t, String k, int type) {
      return t.contains(k, type);
   }

   @Nullable
   public static UUID getUUID(CompoundTag t, String k) {
      return t.getUUID(k);
   }

   public static void putUUID(CompoundTag t, String k, UUID uuid) {
      t.putUUID(k, uuid);
   }

   public static boolean hasUUID(CompoundTag t, String k) {
      return t.hasUUID(k);
   }

   public static Tag createUUID(UUID uuid) {
      return NbtUtils.createUUID(uuid);
   }

   public static UUID loadUUID(Tag tag) {
      return NbtUtils.loadUUID(tag);
   }

   public static Tag writeBlockPos(BlockPos pos) {
      return NbtUtils.writeBlockPos(pos);
   }

   public static boolean contains(CompoundTag t, String k) {
      return t.contains(k);
   }

   public static void put(CompoundTag t, String k, Tag v) {
      t.put(k, v);
   }

   @Nullable
   public static Tag getTag(CompoundTag t, String k) {
      return t.get(k);
   }

   public static void saveAdditionalTo(LivingEntity e, CompoundTag tag) {
      ((IEntitySaveDataAccessor)e).am_writeSaveData(tag);
   }

   public static void readAdditionalFrom(LivingEntity e, CompoundTag tag) {
      ((IEntitySaveDataAccessor)e).am_readSaveData(tag);
   }

   public static boolean saveEntity(Entity e, CompoundTag tag) {
      return e.save(tag);
   }

   public static void loadEntity(Entity e, CompoundTag tag) {
      e.load(tag);
   }

   public static boolean canBeCollidedWith(Entity target, Entity collider) {
      return target.canBeCollidedWith();
   }

   public static boolean isFullyConstructed(Entity entity) {
      return entity.getEntityData() != null;
   }

   public static void loadCustomOnly(BlockEntity be, CompoundTag tag, Provider provider) {
      be.loadCustomOnly(tag, provider);
   }

   public static Provider lookupOf(BlockEntity be) {
      return (Provider)(be.getLevel() != null ? be.getLevel().registryAccess() : RegistryAccess.EMPTY);
   }

   public static EquipmentSlot equipmentSlotFor(ItemStack stack) {
      Equipable equipable = Equipable.get(stack);
      return equipable == null ? EquipmentSlot.MAINHAND : equipable.getEquipmentSlot();
   }

   public static boolean isArmor(ItemStack stack) {
      return stack.getItem() instanceof ArmorItem;
   }

   public static void addCooldown(ItemCooldowns cooldowns, Item item, int ticks) {
      cooldowns.addCooldown(item, ticks);
   }

   public static boolean isOnCooldown(ItemCooldowns cooldowns, Item item) {
      return cooldowns.isOnCooldown(item);
   }

   public static boolean isUndead(LivingEntity entity) {
      return entity.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);
   }

   public static boolean isArthropod(LivingEntity entity) {
      return entity.getType().builtInRegistryHolder().is(EntityTypeTags.ARTHROPOD);
   }

   public static boolean isAquatic(LivingEntity entity) {
      return entity.getType().builtInRegistryHolder().is(EntityTypeTags.AQUATIC);
   }

   public static float getDamageBonus(ItemStack weapon, LivingEntity target) {
      return 0.0F;
   }

   public static int enchantLevel(ResourceKey<Enchantment> key, ItemStack stack, LevelReader level) {
      return EnchantmentHelper.getItemEnchantmentLevel(enchantHolder(key, level), stack);
   }

   public static int enchantLevel(ResourceKey<Enchantment> key, LivingEntity entity) {
      return EnchantmentHelper.getEnchantmentLevel(enchantHolder(key, entity.level()), entity);
   }

   private static Holder<Enchantment> enchantHolder(ResourceKey<Enchantment> key, LevelReader level) {
      return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
   }

   public static void enchantDamageEffects(LivingEntity attacker, Entity target) {
      if (attacker.level() instanceof ServerLevel serverLevel) {
         EnchantmentHelper.doPostAttackEffects(serverLevel, target, attacker.damageSources().mobAttack(attacker));
      }
   }

   public static Holder<MobEffect> effect(MobEffect effect) {
      return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
   }

   public static MobEffect rawEffect(MobEffectInstance instance) {
      return (MobEffect)instance.getEffect().value();
   }

   public static void setTame(TamableAnimal animal, boolean tame) {
      animal.setTame(tame, true);
   }

   public static boolean startRiding(Entity rider, Entity vehicle, boolean force) {
      return rider.startRiding(vehicle, force);
   }

   public static boolean ridesUnsaveableVehicles(Entity rider) {
      return rider instanceof EntityCrimsonMosquito
         || rider instanceof EntityEnderiophage
         || rider instanceof EntityBaldEagle
         || rider instanceof EntityCrow
         || rider instanceof EntityCapuchinMonkey
         || rider instanceof EntityPotoo
         || rider instanceof EntitySugarGlider;
   }

   public static boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
      return state.isPathfindable(type);
   }

   public static float width(EntityDimensions dimensions) {
      return dimensions.width();
   }

   public static float height(EntityDimensions dimensions) {
      return dimensions.height();
   }

   public static void setMaxUpStep(Entity entity, float value) {
      if (entity instanceof LivingEntity living) {
         AttributeInstance instance = living.getAttribute(Attributes.STEP_HEIGHT);
         if (instance != null) {
            instance.setBaseValue(value);
         }
      }
   }

   public static PathType pathTypeStatic(Mob mob, BlockPos pos) {
      return WalkNodeEvaluator.getPathTypeStatic(mob, pos);
   }

   public static double attackDamageOf(ItemStack stack, EquipmentSlot slot) {
      double[] total = new double[]{0.0};
      stack.forEachModifier(slot, (attribute, modifier) -> {
         if (attribute == Attributes.ATTACK_DAMAGE) {
            total[0] += modifier.amount();
         }
      });
      return total[0];
   }

   public static double armorOf(ItemStack stack, EquipmentSlot slot) {
      double[] total = new double[]{0.0};
      stack.forEachModifier(slot, (attribute, modifier) -> {
         if (attribute == Attributes.ARMOR) {
            total[0] += modifier.amount();
         }
      });
      return total[0];
   }

   public static void removeItemModifiers(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
      stack.forEachModifier(slot, (attribute, modifier) -> {
         AttributeInstance instance = entity.getAttributes().getInstance(attribute);
         if (instance != null) {
            instance.removeModifier(modifier.id());
         }
      });
   }

   public static void addItemModifiers(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
      stack.forEachModifier(slot, (attribute, modifier) -> {
         AttributeInstance instance = entity.getAttributes().getInstance(attribute);
         if (instance != null) {
            instance.removeModifier(modifier.id());
            instance.addTransientModifier(modifier);
         }
      });
   }

   public static void hurtAndBreak(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot) {
      stack.hurtAndBreak(amount, entity, slot);
   }

   public static void hurtAndBreak(ItemStack stack, int amount, LivingEntity entity, InteractionHand hand) {
      hurtAndBreak(stack, amount, entity, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
   }

   public static <T extends Mob & Shearable> boolean shearWithShears(T mob, Player player, InteractionHand hand, ItemStack stack) {
      if (!stack.is(Items.TOOLS_SHEAR) || !mob.readyForShearing()) {
         return false;
      } else if (mob.level().isClientSide()) {
         return true;
      } else {
         mob.shear(SoundSource.PLAYERS);
         hurtAndBreak(stack, 1, player, hand);
         return true;
      }
   }

   public static void hurtItem(ItemStack stack, int amount, RandomSource random, @Nullable ServerPlayer player) {
      if (player != null) {
         stack.hurtAndBreak(amount, player.serverLevel(), player, item -> {});
      }
   }

   @Nullable
   public static CompoundTag getBlockEntityData(ItemStack stack) {
      CompoundTag stashed = getTagElement(stack, "BlockEntityTag");
      if (stashed != null) {
         return stashed;
      } else {
         CustomData data = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
         return data != null && !data.isEmpty() ? data.copyTag() : null;
      }
   }

   public static ItemStack readItem(FriendlyByteBuf buf) {
      return (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf);
   }

   public static void writeItem(FriendlyByteBuf buf, ItemStack stack) {
      ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf, stack);
   }

   public static void saveInto(Provider provider, ItemStack stack, CompoundTag tag) {
      tag.merge((CompoundTag)stack.save(provider, new CompoundTag()));
   }

   public static void stopRunningGoals(GoalSelector selector) {
      for (WrappedGoal wrapped : List.copyOf(selector.getAvailableGoals())) {
         if (wrapped.isRunning()) {
            wrapped.stop();
         }
      }
   }

   public static int nutrition(FoodProperties food) {
      return food.nutrition();
   }

   public static boolean isSpawnPositionOnGround(LevelReader level, BlockPos pos, EntityType<?> type) {
      return SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk(level, pos, type);
   }

   @Nullable
   private static ServerLevel serverLevel(Entity entity) {
      return entity.level() instanceof ServerLevel level ? level : null;
   }

   public static boolean isInvulnerableTo(Entity entity, DamageSource source) {
      return entity.isInvulnerableTo(source);
   }

   public static InteractionResultHolder<ItemStack> sidedSuccess(ItemStack stack, boolean isClientSide) {
      return InteractionResultHolder.sidedSuccess(stack, isClientSide);
   }

   public static InteractionResultHolder<ItemStack> success(ItemStack stack) {
      return InteractionResultHolder.success(stack);
   }

   public static InteractionResultHolder<ItemStack> consume(ItemStack stack) {
      return InteractionResultHolder.consume(stack);
   }

   public static InteractionResultHolder<ItemStack> pass(ItemStack stack) {
      return InteractionResultHolder.pass(stack);
   }

   public static InteractionResultHolder<ItemStack> fail(ItemStack stack) {
      return InteractionResultHolder.fail(stack);
   }

   public static InteractionResultHolder<ItemStack> holder(InteractionResult result, ItemStack stack) {
      return new InteractionResultHolder(result, stack);
   }

   public static InteractionResult sidedSuccess(boolean isClientSide) {
      return InteractionResult.sidedSuccess(isClientSide);
   }

   public static ItemInteractionResult itemResult(InteractionResult result) {
      if (result == InteractionResult.SUCCESS) {
         return ItemInteractionResult.SUCCESS;
      } else if (result == InteractionResult.CONSUME) {
         return ItemInteractionResult.CONSUME;
      } else {
         return result == InteractionResult.FAIL ? ItemInteractionResult.FAIL : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public static int minBuildHeight(LevelHeightAccessor level) {
      return level.getMinBuildHeight();
   }

   public static int maxBuildHeight(LevelHeightAccessor level) {
      return level.getMaxBuildHeight();
   }

   public static boolean hasCraftingRemainder(ItemStack stack) {
      return stack.hasCraftingRemainingItem();
   }

   public static ItemStack craftingRemainder(ItemStack stack) {
      return stack.getCraftingRemainingItem();
   }

   public static Supplier<Ingredient> lazyIngredient(final Supplier<Ingredient> factory) {
      return new Supplier<Ingredient>() {
         private Ingredient cached;

         public Ingredient get() {
            if (this.cached == null) {
               this.cached = factory.get();
            }

            return this.cached;
         }
      };
   }

   public static Ingredient ingredientOf(TagKey<Item> tag) {
      return Ingredient.of(tag);
   }

   public static Properties repairableWith(Properties props, String name) {
      return props;
   }

   @SafeVarargs
   public static Ingredient ingredientOfTags(TagKey<Item>... tags) {
      return Ingredient.fromValues(Arrays.stream(tags).map(TagValue::new));
   }

   public static ItemStack[] ingredientStacks(Ingredient ingredient) {
      return ingredient.isEmpty() ? new ItemStack[0] : ingredient.getItems();
   }

   public static boolean gameRule(Level level, AMCompat.Rule rule) {
      return switch (rule) {
         case MOB_LOOT -> level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
         case MOB_GRIEFING -> level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
         case BLOCK_DROPS -> level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
         case MOB_SPAWNING -> level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
         case UNIVERSAL_ANGER -> level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER);
         case WEATHER_CYCLE -> level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE);
      };
   }

   public static float timeOfDay(LevelAccessor level, BlockPos pos) {
      return level.getTimeOfDay(1.0F);
   }

   public static DifficultyInstance difficultyAt(LevelAccessor level, BlockPos pos) {
      return level.getCurrentDifficultyAt(pos);
   }

   @Nullable
   public static Holder<DamageType> damageTypeHolder(Level level, String id) {
      ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, rl(id));
      return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(key).map(h -> (Holder<DamageType>)h).orElse(null);
   }

   public static boolean doHurtTarget(Mob mob, Entity target) {
      return mob.doHurtTarget(target);
   }

   public static boolean hurt(Entity entity, DamageSource source, float amount) {
      return entity.hurt(source, amount);
   }

   public static void kill(Entity entity) {
      entity.kill();
   }

   @Nullable
   public static ItemEntity spawnAtLocation(Entity entity, ItemLike item) {
      return entity.spawnAtLocation(item);
   }

   @Nullable
   public static ItemEntity spawnAtLocation(Entity entity, ItemStack stack) {
      return entity.spawnAtLocation(stack);
   }

   @Nullable
   public static ItemEntity spawnAtLocation(Entity entity, ItemStack stack, float yOffset) {
      return entity.spawnAtLocation(stack, yOffset);
   }

   @Nullable
   public static <T extends Entity> T create(EntityType<T> type, Level level) {
      return (T)type.create(level);
   }

   @Nullable
   public static <T extends Entity> T createForDisplay(EntityType<T> type, Level level) {
      T entity = create(type, level);
      if (entity != null) {
         entity.setId(-1);
      }

      return entity;
   }

   public static <T extends LivingEntity> Predicate<T> selector(Predicate<T> predicate) {
      return predicate;
   }

   @Nullable
   public static Player getNearestPlayer(Level level, TargetingConditions conditions, LivingEntity around) {
      return level.getNearestPlayer(conditions, around);
   }

   public static <T extends LivingEntity> List<T> getNearbyEntities(Level level, Class<T> type, TargetingConditions conditions, LivingEntity around, AABB box) {
      return level.getNearbyEntities(type, conditions, around, box);
   }

   public static void addParticle(Level level, ParticleOptions particle, boolean force, double x, double y, double z, double dx, double dy, double dz) {
      level.addParticle(particle, force, x, y, z, dx, dy, dz);
   }

   public static SpawnEggItem spawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int bg, int fg, Properties props) {
      return new DeferredSpawnEggItem(type, bg, fg, props);
   }

   public static boolean cancelIf(Runnable body) {
      boolean[] cell = CANCEL_FLAG.get();
      boolean prev = cell[0];
      cell[0] = false;

      boolean var3;
      try {
         body.run();
         var3 = cell[0];
      } finally {
         cell[0] = prev;
      }

      return var3;
   }

   public static void cancelEvent() {
      CANCEL_FLAG.get()[0] = true;
   }

   public static SoundEvent catEatSound() {
      return SoundEvents.CAT_EAT;
   }

   public static SoundEvent cowStepSound() {
      return SoundEvents.COW_STEP;
   }

   public static void displayClientMessage(Player player, Component message, boolean overlay) {
      player.displayClientMessage(message, overlay);
   }

   public static ItemParticleOption itemParticle(ParticleType<ItemParticleOption> type, ItemStack stack) {
      return new ItemParticleOption(type, stack);
   }

   public static ItemStack asItemStack(ItemStack stack) {
      return stack;
   }

   public static boolean isRainingOrThundering(LevelAccessor worldIn) {
      return worldIn.getLevelData() != null && (worldIn.getLevelData().isThundering() || worldIn.getLevelData().isRaining());
   }

   public static void knockback(LivingEntity entity, double power, double xd, double zd) {
      entity.knockback(power, xd, zd);
   }

   public static float riderForward(LivingEntity player) {
      return player.zza;
   }

   public static boolean isMultipart(Entity entity) {
      return entity.getParts() != null;
   }

   public static void assignClientPartId(Entity part) {
      if (part.level().isClientSide()) {
         part.setId(CLIENT_PART_ID.decrementAndGet());
      }
   }

   public static float riderStrafe(LivingEntity player) {
      return player.xxa;
   }

   public static boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
      return state.isLadder(world, pos, entity);
   }

   public static boolean isScaffolding(BlockState state, LivingEntity entity) {
      return state.isScaffolding(entity);
   }

   public static CompoundTag getPersistentData(Entity entity) {
      return entity.getPersistentData();
   }

   private static final class Meats {
      static final Set<Item> SET = Set.of(
         net.minecraft.world.item.Items.BEEF,
         net.minecraft.world.item.Items.COOKED_BEEF,
         net.minecraft.world.item.Items.PORKCHOP,
         net.minecraft.world.item.Items.COOKED_PORKCHOP,
         net.minecraft.world.item.Items.CHICKEN,
         net.minecraft.world.item.Items.COOKED_CHICKEN,
         net.minecraft.world.item.Items.MUTTON,
         net.minecraft.world.item.Items.COOKED_MUTTON,
         net.minecraft.world.item.Items.RABBIT,
         net.minecraft.world.item.Items.COOKED_RABBIT,
         net.minecraft.world.item.Items.COD,
         net.minecraft.world.item.Items.COOKED_COD,
         net.minecraft.world.item.Items.SALMON,
         net.minecraft.world.item.Items.COOKED_SALMON,
         net.minecraft.world.item.Items.TROPICAL_FISH,
         net.minecraft.world.item.Items.PUFFERFISH,
         net.minecraft.world.item.Items.ROTTEN_FLESH,
         net.minecraft.world.item.Items.SPIDER_EYE
      );
   }

   public static enum Rule {
      MOB_LOOT,
      MOB_GRIEFING,
      BLOCK_DROPS,
      MOB_SPAWNING,
      UNIVERSAL_ANGER,
      WEATHER_CYCLE;
   }
}
