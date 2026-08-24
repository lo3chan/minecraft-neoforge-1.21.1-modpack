package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.entity.condition.AnyCondition;
import corgitaco.corgilib.entity.condition.Condition;
import corgitaco.corgilib.entity.condition.ConditionContext;
import corgitaco.corgilib.entity.condition.FlipCondition;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.AnvilCostModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.BeaconRadiusModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.BlockItemDropModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.BlockSleepingModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.EnchantmentCostModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.EntityDropModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.ExperienceModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.GlowColorModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifierType;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.LunarEventModifierTypes;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MobEffectsModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MobEquipment;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MobSpawnDistancesModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MobSpawnSettingsModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MoonSizeModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MoonTextureColorModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.MoonTextureModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.NameColorModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.SkyLightColorModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.SoundTrackModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.SpawnCategoryMultiplierModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.TextComponentsModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier.VillageSiegeProbabilityModifier;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.LunarEventSpawnRule;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.LunarEventSpawnRuleTypes;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule.SpawnRuleContext;
import dev.corgitaco.enhancedcelestials2core.util.CodecUtil;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.joml.Vector3f;

public class LunarEvent {
   private static final Codec<LunarEvent> BASE_CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            CodecUtil.networkSafeHolder(EnhancedCelestialsRegistry.LUNAR_EVENT_MODIFIER_KEY, LunarEventModifierTypes.CODEC)
               .listOf()
               .fieldOf("modifiers")
               .forGetter(LunarEvent::getModifiers)
         )
         .apply(builder, LunarEvent::new)
   );
   public static final Codec<LunarEvent> DIRECT_CODEC = BASE_CODEC.validate(LunarEvent::validateModifiers);
   private final List<Holder<LunarEventModifier>> modifiers;
   private final Map<LunarEventModifierType<?>, LunarEventModifier> modifiersByType;

   public LunarEvent(List<Holder<LunarEventModifier>> modifiers) {
      this.modifiers = modifiers;
      this.modifiersByType = new IdentityHashMap<>();
   }

   public void setupModifiers() {
      for (Holder<LunarEventModifier> holder : this.modifiers) {
         LunarEventModifier modifier = (LunarEventModifier)holder.value();
         this.modifiersByType.putIfAbsent(modifier.type(), modifier);
      }
   }

   private static DataResult<LunarEvent> validateModifiers(LunarEvent event) {
      Set<LunarEventModifierType<?>> seenTypes = Collections.newSetFromMap(new IdentityHashMap<>());

      for (Holder<LunarEventModifier> holder : event.modifiers) {
         LunarEventModifierType<?> type = ((LunarEventModifier)holder.value()).type();
         if (!seenTypes.add(type)) {
            return DataResult.error(() -> "Duplicate lunar event modifier of type " + type + " in " + event.modifiers);
         }
      }

      event.setupModifiers();
      return DataResult.success(event);
   }

   public <T extends LunarEventModifier> Optional<T> getModifier(LunarEventModifierType<T> type) {
      return Optional.ofNullable((T)this.modifiersByType.get(type));
   }

   public List<Holder<LunarEventModifier>> getModifiers() {
      return this.modifiers;
   }

   public void onBlockItemDrop(ServerLevel world, ItemStack itemStack) {
      this.getModifier(LunarEventModifierTypes.BLOCK_ITEM_DROP).ifPresent(modifier -> {
         for (Pair<Integer, Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>> group : modifier.settings().dropEnhancer()) {
            int minimumCount = (Integer)group.getFirst();
            ((Map)group.getSecond()).forEach((target, multiplier) -> {
               if (itemStack.getCount() >= minimumCount && DropSettings.matches(itemStack, (Either<TagKey<Item>, ResourceKey<Item>>)target)) {
                  itemStack.setCount((int)Math.round(itemStack.getCount() * multiplier));
               }
            });
         }
      });
   }

   public void onEntityItemDrop(ServerLevel world, LivingEntity entity, ItemStack itemStack) {
      this.getModifier(LunarEventModifierTypes.ENTITY_DROP).ifPresent(modifier -> {
         for (Pair<Condition, DropSettings> entry : modifier.drops()) {
            if (((Condition)entry.getFirst()).passes(new ConditionContext(entity.level(), entity, entity.isDeadOrDying(), 0))) {
               for (Pair<Integer, Map<Either<TagKey<Item>, ResourceKey<Item>>, Double>> group : ((DropSettings)entry.getSecond()).dropEnhancer()) {
                  int minimumCount = (Integer)group.getFirst();
                  ((Map)group.getSecond()).forEach((target, multiplier) -> {
                     if (itemStack.getCount() >= minimumCount && DropSettings.matches(itemStack, (Either<TagKey<Item>, ResourceKey<Item>>)target)) {
                        itemStack.setCount((int)Math.round(itemStack.getCount() * multiplier));
                     }
                  });
               }
            }
         }
      });
   }

   public double anvilCostAmplifier() {
      return this.getModifier(LunarEventModifierTypes.ANVIL_COST).map(AnvilCostModifier::amplifier).orElse(1.0);
   }

   public double enchantmentTableCostAmplifier() {
      return this.getModifier(LunarEventModifierTypes.ENCHANTMENT_COST).map(EnchantmentCostModifier::amplifier).orElse(1.0);
   }

   public Optional<Double> siegeProbability() {
      return this.getModifier(LunarEventModifierTypes.VILLAGE_SIEGE_PROBABILITY).map(VillageSiegeProbabilityModifier::probability);
   }

   public double xpAmplifier() {
      return this.getModifier(LunarEventModifierTypes.EXPERIENCE).map(ExperienceModifier::amplifier).orElse(1.0);
   }

   public double beaconRadiusAmplifier() {
      return this.getModifier(LunarEventModifierTypes.BEACON_RADIUS).map(BeaconRadiusModifier::amplifier).orElse(1.0);
   }

   public Optional<LunarTextComponents.Notification> startNotification() {
      return this.getTextComponents().riseNotification();
   }

   public Optional<LunarTextComponents.Notification> endNotification() {
      return this.getTextComponents().setNotification();
   }

   public Optional<Vector3f> getSkyLightColor() {
      return this.getModifier(LunarEventModifierTypes.SKY_LIGHT_COLOR).map(SkyLightColorModifier::getGLColor);
   }

   public Optional<Vector3f> getMoonTextureColor() {
      return this.getModifier(LunarEventModifierTypes.MOON_TEXTURE_COLOR).map(MoonTextureColorModifier::getGLColor);
   }

   public Optional<Vector3f> getGlowColor() {
      return this.getModifier(LunarEventModifierTypes.GLOW_COLOR).map(GlowColorModifier::getGLColor);
   }

   public float getGlowIntensity() {
      return this.getModifier(LunarEventModifierTypes.GLOW_COLOR).map(GlowColorModifier::glowIntensity).orElse(1.0F);
   }

   public float getMoonSize() {
      return this.getModifier(LunarEventModifierTypes.MOON_SIZE).map(MoonSizeModifier::moonSize).orElse(20.0F);
   }

   public ResourceLocation getMoonTextureLocation() {
      return this.getModifier(LunarEventModifierTypes.MOON_TEXTURE)
         .map(MoonTextureModifier::textureLocation)
         .orElseGet(() -> ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png"));
   }

   public Optional<SoundEvent> getSoundTrack() {
      return this.getModifier(LunarEventModifierTypes.SOUND_TRACK).map(SoundTrackModifier::soundTrack);
   }

   public Map<MobCategory, Double> getSpawnCategoryMultiplier() {
      return this.getModifier(LunarEventModifierTypes.SPAWN_CATEGORY_MULTIPLIER).map(SpawnCategoryMultiplierModifier::multipliers).orElse(Map.of());
   }

   public double getSpawnMultiplierForMonsterCategory(MobCategory classification) {
      return this.getSpawnCategoryMultiplier().getOrDefault(classification, 1.0);
   }

   public void livingEntityTick(LivingEntity entity) {
      this.getModifier(LunarEventModifierTypes.MOB_EFFECTS).map(MobEffectsModifier::effects).orElse(List.of()).forEach(entityFilterMapPair -> {
         Condition entityFilter = (Condition)entityFilterMapPair.getFirst();
         if (entityFilter.passes(new ConditionContext(entity.level(), entity, entity.isDeadOrDying(), 0))) {
            MobEffectInstanceBuilder builder = (MobEffectInstanceBuilder)entityFilterMapPair.getSecond();
            entity.addEffect(builder.makeInstance());
         }
      });
   }

   public void equipMobOnSpawn(Mob mob) {
      this.getModifier(LunarEventModifierTypes.MOB_SPAWN_EQUIPMENT).ifPresent(modifier -> MobEquipment.apply(mob, modifier.equipmentCombinations()));
   }

   public void equipExistingMob(Mob mob, Holder<LunarEvent> selfHolder) {
      this.getModifier(LunarEventModifierTypes.EXISTING_MOB_EQUIPMENT).ifPresent(modifier -> {
         String tag = equipmentAppliedTag(selfHolder);
         if (mob.getTags().add(tag)) {
            MobEquipment.apply(mob, modifier.equipmentCombinations());
         }
      });
   }

   private static String equipmentAppliedTag(Holder<LunarEvent> selfHolder) {
      return "enhancedcelestials_equipped:" + selfHolder.unwrapKey().map(key -> key.location().toString()).orElse("unknown");
   }

   public boolean useBiomeSpawnSettings() {
      return this.getModifier(LunarEventModifierTypes.DISABLE_BIOME_SPAWN_SETTINGS).isEmpty();
   }

   public boolean forceSurfaceSpawning() {
      return this.getModifier(LunarEventModifierTypes.FORCE_SURFACE_SPAWNING).isPresent();
   }

   public boolean slimesSpawnEverywhere() {
      return this.getModifier(LunarEventModifierTypes.SLIMES_SPAWN_EVERYWHERE).isPresent();
   }

   public Optional<MobSpawnSettings> mobSpawnSettings() {
      return this.getModifier(LunarEventModifierTypes.MOB_SPAWN_SETTINGS).map(MobSpawnSettingsModifier::spawnSettings);
   }

   public boolean blockSleeping(LivingEntity entity) {
      Condition condition = this.getModifier(LunarEventModifierTypes.BLOCK_SLEEPING)
         .map(BlockSleepingModifier::condition)
         .orElseGet(() -> new FlipCondition(AnyCondition.INSTANCE));
      return condition.passes(new ConditionContext(entity.level(), entity, entity.isDeadOrDying(), 0));
   }

   public LunarTextComponents getTextComponents() {
      return this.getModifier(LunarEventModifierTypes.TEXT_COMPONENTS)
         .map(TextComponentsModifier::components)
         .orElseGet(() -> new LunarTextComponents(Optional.empty(), Optional.empty()));
   }

   public Optional<TextColor> getNameColor() {
      return this.getModifier(LunarEventModifierTypes.NAME_COLOR).map(NameColorModifier::color);
   }

   public static String getTranslationKey(Holder<LunarEvent> holder) {
      return getTranslationKey(((ResourceKey)holder.unwrapKey().orElseThrow()).location());
   }

   public static String getTranslationKey(ResourceLocation location) {
      return "lunar.event." + location.getNamespace() + "." + location.getPath();
   }

   public DropSettings getDropSettings() {
      return this.getModifier(LunarEventModifierTypes.BLOCK_ITEM_DROP).map(BlockItemDropModifier::settings).orElse(DropSettings.EMPTY);
   }

   public List<Pair<Condition, DropSettings>> getEntityDropSettings() {
      return this.getModifier(LunarEventModifierTypes.ENTITY_DROP).map(EntityDropModifier::drops).orElse(List.of());
   }

   public Map<MobCategory, Integer> mobSpawnDistances() {
      return this.getModifier(LunarEventModifierTypes.MOB_SPAWN_DISTANCES).map(MobSpawnDistancesModifier::distances).orElse(Map.of());
   }

   public record SpawnRequirements(int weight, List<Holder<LunarEventSpawnRule>> rules) {
      public static final Codec<LunarEvent.SpawnRequirements> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               Codec.INT.fieldOf("weight").forGetter(LunarEvent.SpawnRequirements::weight),
               CodecUtil.networkSafeHolder(EnhancedCelestialsRegistry.LUNAR_EVENT_SPAWN_RULE_KEY, LunarEventSpawnRuleTypes.CODEC)
                  .listOf()
                  .fieldOf("rules")
                  .forGetter(LunarEvent.SpawnRequirements::rules)
            )
            .apply(builder, LunarEvent.SpawnRequirements::new)
      );

      public boolean passes(SpawnRuleContext context) {
         for (Holder<LunarEventSpawnRule> rule : this.rules) {
            if (!((LunarEventSpawnRule)rule.value()).passes(context)) {
               return false;
            }
         }

         return true;
      }
   }
}
