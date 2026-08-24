package net.cibernet.alchemancy.properties;

import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.properties.special.AirWalkingProperty;
import net.cibernet.alchemancy.properties.special.BlinkingProperty;
import net.cibernet.alchemancy.properties.special.DashingProperty;
import net.cibernet.alchemancy.properties.special.GustJetProperty;
import net.cibernet.alchemancy.properties.voidborn.BlockVacuumProperty;
import net.cibernet.alchemancy.properties.voidborn.TelekineticProperty;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class SparklingProperty extends Property implements IDataHolder<Holder<Property>> {
   private static final HashMap<Holder<Property>, Supplier<ParticleOptions>> PARTICLE_MAP = new HashMap<Holder<Property>, Supplier<ParticleOptions>>() {
      {
         this.put(AlchemancyProperties.BURNING, () -> ParticleTypes.FLAME);
         this.put(AlchemancyProperties.SPARKING, () -> ParticleTypes.SMALL_FLAME);
         this.put(AlchemancyProperties.FROSTED, () -> ParticleTypes.SNOWFLAKE);
         this.put(AlchemancyProperties.SCULKING, () -> ParticleTypes.SCULK_CHARGE_POP);
         this.put(AlchemancyProperties.SOULBIND, () -> ParticleTypes.SOUL);
         this.put(AlchemancyProperties.SPIRIT_BOND, () -> ParticleTypes.SOUL_FIRE_FLAME);
         this.put(AlchemancyProperties.VENGEFUL, () -> ParticleTypes.ANGRY_VILLAGER);
         this.put(AlchemancyProperties.WEALTHY, () -> ParticleTypes.HAPPY_VILLAGER);
         this.put(AlchemancyProperties.UNDYING, () -> ParticleTypes.TOTEM_OF_UNDYING);
         this.put(AlchemancyProperties.WARPED, () -> ParticleTypes.WARPED_SPORE);
         this.put(AlchemancyProperties.HELLBENT, () -> ParticleTypes.CRIMSON_SPORE);
         this.put(AlchemancyProperties.SHARP, () -> ParticleTypes.CRIT);
         this.put(AlchemancyProperties.ENCHANTING, () -> ParticleTypes.ENCHANTED_HIT);
         this.put(AlchemancyProperties.WISE, () -> ParticleTypes.ENCHANT);
         this.put(AlchemancyProperties.ARCANE, () -> ParticleTypes.DRAGON_BREATH);
         this.put(AlchemancyProperties.WET, () -> ParticleTypes.SPLASH);
         this.put(AlchemancyProperties.MYCELLIC, () -> ParticleTypes.SPORE_BLOSSOM_AIR);
         this.put(AlchemancyProperties.SHOCKING, () -> ParticleTypes.ELECTRIC_SPARK);
         this.put(AlchemancyProperties.ALLERGIC, () -> ParticleTypes.SNEEZE);
         this.put(AlchemancyProperties.ENDER, () -> ParticleTypes.PORTAL);
         this.put(AlchemancyProperties.SPORADIC, () -> ParticleTypes.MYCELIUM);
         this.put(AlchemancyProperties.BLINDING, () -> ParticleTypes.SQUID_INK);
         this.put(AlchemancyProperties.DEXTEROUS, () -> ParticleTypes.GLOW_SQUID_INK);
         this.put(AlchemancyProperties.SENSITIVE, () -> DustColorTransitionOptions.SCULK_TO_REDSTONE);
         this.put(AlchemancyProperties.ENERGIZED, () -> DustParticleOptions.REDSTONE);
         this.put(AlchemancyProperties.COMPACT, () -> ParticleTypes.OMINOUS_SPAWNING);
         this.put(AlchemancyProperties.OMINOUS, () -> ParticleTypes.TRIAL_OMEN);
         this.put(AlchemancyProperties.VAMPIRIC, () -> ParticleTypes.RAID_OMEN);
         this.put(AlchemancyProperties.HEARTY, () -> ParticleTypes.HEART);
         this.put(AlchemancyProperties.WEAK, () -> ParticleTypes.DAMAGE_INDICATOR);
         this.put(AlchemancyProperties.MUSICAL, () -> ParticleTypes.NOTE);
         this.put(AlchemancyProperties.LAUNCHING, () -> LaunchingProperty.PARTICLES);
         this.put(AlchemancyProperties.WAXED, () -> ParticleTypes.WAX_ON);
         this.put(AlchemancyProperties.MAGIC_RESISTANT, () -> ParticleTypes.DRIPPING_OBSIDIAN_TEAR);
         this.put(AlchemancyProperties.BOUNCY, () -> ParticleTypes.ITEM_SLIME);
         this.put(AlchemancyProperties.STICKY, () -> ParticleTypes.ITEM_COBWEB);
         this.put(AlchemancyProperties.FLAMMABLE, () -> ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
         this.put(AlchemancyProperties.EXPLODING, () -> ParticleTypes.EXPLOSION);
         this.put(AlchemancyProperties.WIND_CHARGED, () -> ParticleTypes.SMALL_GUST);
         this.put(AlchemancyProperties.FLOURISH, () -> ParticleTypes.CHERRY_LEAVES);
         this.put(AlchemancyProperties.CRACKLING, () -> ParticleTypes.FIREWORK);
         this.put(AlchemancyProperties.GLOWING_AURA, () -> ParticleTypes.GLOW);
         this.put(AlchemancyProperties.WILDFIRE, () -> ParticleTypes.LARGE_SMOKE);
         this.put(AlchemancyProperties.ECHOED, () -> ParticleTypes.SONIC_BOOM);
         this.put(AlchemancyProperties.ETERNAL_GLOW, () -> GlowRingProperty.PARTICLES);
         this.put(AlchemancyProperties.GUST_JET, () -> GustJetProperty.PARTICLES);
         this.put(AlchemancyProperties.VOIDBORN, () -> BlockVacuumProperty.PARTICLES);
         this.put(AlchemancyProperties.AIR_WALKER, () -> AirWalkingProperty.PARTICLES);
         this.put(AlchemancyProperties.KINETIC_GRAB, () -> TelekineticProperty.PARTICLES);
         this.put(AlchemancyProperties.CLOUD_DASH, () -> DashingProperty.CLOUD_PARTICLES);
         this.put(AlchemancyProperties.CRYSTAL_DASH, () -> DashingProperty.CRYSTAL_PARTICLES);
         this.put(AlchemancyProperties.BLINKING, () -> BlinkingProperty.PARTICLES);
         this.put(AlchemancyProperties.MAGNETIC, () -> Math.random() > 0.5 ? MagneticProperty.PARTICLE_A : MagneticProperty.PARTICLE_B);
         this.put(AlchemancyProperties.ROCKET_POWERED, AlchemancyParticles.WARHAMMER_FLAME::get);
         this.put(AlchemancyProperties.INFERNAL, AlchemancyParticles.INFERNO::get);
         this.put(
            AlchemancyProperties.SWIFT,
            () -> ((MobEffect)MobEffects.MOVEMENT_SPEED.value()).createParticleOptions(new MobEffectInstance(MobEffects.MOVEMENT_SPEED))
         );
         this.put(
            AlchemancyProperties.SLUGGISH,
            () -> ((MobEffect)MobEffects.MOVEMENT_SLOWDOWN.value()).createParticleOptions(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN))
         );
         this.put(AlchemancyProperties.POISONOUS, () -> ((MobEffect)MobEffects.POISON.value()).createParticleOptions(new MobEffectInstance(MobEffects.POISON)));
         this.put(AlchemancyProperties.DECAYING, () -> ((MobEffect)MobEffects.WITHER.value()).createParticleOptions(new MobEffectInstance(MobEffects.WITHER)));
         this.put(
            AlchemancyProperties.TIPSY, () -> ((MobEffect)MobEffects.CONFUSION.value()).createParticleOptions(new MobEffectInstance(MobEffects.CONFUSION))
         );
         this.put(AlchemancyProperties.DIRTY, () -> new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()));
         this.put(AlchemancyProperties.MALLEABLE, () -> new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CLAY.defaultBlockState()));
         this.put(AlchemancyProperties.SEEDED, () -> new ItemParticleOption(ParticleTypes.ITEM, Items.WHEAT_SEEDS.getDefaultInstance()));
         this.put(
            AlchemancyProperties.RANDOM,
            () -> SparklingProperty.PARTICLE_MAP
               .values()
               .stream()
               .skip((int)((SparklingProperty.PARTICLE_MAP.size() - 1) * Math.random()))
               .findFirst()
               .get()
               .get()
         );
      }
   };

   public static Collection<Holder<Property>> getAllParticleProviders() {
      return PARTICLE_MAP.keySet();
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16711816;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      Holder<Property> storedStack = this.getData(stack);
      return (Component)(storedStack != null
         ? Component.translatable("property.detail", new Object[]{name, ((Property)storedStack.value()).getName()}).withColor(this.getColor(stack))
         : name);
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      if (this.getData(propertySource) != null) {
         if (consumeItem) {
            this.setData(stack, this.getData(propertySource));
         }

         return true;
      } else {
         for (Holder<Property> infusedProperty : InfusedPropertiesHelper.getInfusedProperties(propertySource)) {
            if (hasParticles(infusedProperty)) {
               if (consumeItem) {
                  this.setData(stack, infusedProperty);
               }

               return true;
            }
         }

         return super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd, consumeItem);
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!(user.getRandom().nextFloat() > 0.25F)
         && !InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.DISABLES_SPARKLING)
         && (slot.isArmor() || !getEquipmentSlotForItem(stack).isArmor())) {
         ParticleOptions particle = getParticles(stack).orElse(ParticleTypes.END_ROD);
         double y = slot.isArmor() && slot != EquipmentSlot.BODY ? user.getY((user.getRandom().nextDouble() + slot.getIndex()) * 0.25) : user.getRandomY();
         user.level().addParticle(particle, user.getRandomX(1.0), y, user.getRandomZ(1.0), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      ParticleOptions particle = getParticles(stack).orElse(ParticleTypes.END_ROD);
      projectile.level().addParticle(particle, projectile.getX(), projectile.getY(), projectile.getZ(), 0.0, 0.0, 0.0);
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      if (!(itemEntity.getRandom().nextFloat() > 0.25F) && !InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.DISABLES_SPARKLING)) {
         ParticleOptions particle = getParticles(stack).orElse(ParticleTypes.END_ROD);
         itemEntity.level().addParticle(particle, itemEntity.getRandomX(1.0), itemEntity.getRandomY(), itemEntity.getRandomZ(1.0), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
      playRootedParticles(root, randomSource, getParticles(root.getItem()).orElse(ParticleTypes.END_ROD));
   }

   public static boolean hasParticles(Holder<Property> propertyHolder) {
      return PARTICLE_MAP.containsKey(propertyHolder);
   }

   public static Optional<ParticleOptions> getParticles(ItemStack stack) {
      if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SPARKLING)) {
         return Optional.empty();
      } else {
         Holder<Property> result;
         if (PropertyModifierComponent.getOrElse(stack, AlchemancyProperties.SPARKLING, AlchemancyProperties.Modifiers.IGNORE_INFUSED, true)) {
            result = ((SparklingProperty)AlchemancyProperties.SPARKLING.get()).getData(stack);
         } else {
            List<Holder<Property>> infusions = InfusedPropertiesHelper.getInfusedProperties(stack);
            result = infusions.isEmpty() ? null : (Holder)infusions.getFirst();
         }

         return result != null && PARTICLE_MAP.containsKey(result) ? Optional.of(PARTICLE_MAP.get(result).get()) : Optional.of(ParticleTypes.END_ROD);
      }
   }

   public static List<ParticleOptions> getAllParticlesForProperties(ItemStack stack) {
      List<ParticleOptions> result = new ArrayList<>();
      InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
         if (PARTICLE_MAP.containsKey(propertyHolder)) {
            result.add(PARTICLE_MAP.get(propertyHolder).get());
         }
      });
      return result;
   }

   public Holder<Property> readData(CompoundTag tag) {
      if (tag.contains("stored_property")) {
         DataResult<Holder<Property>> data = Property.CODEC
            .parse(CommonUtils.registryAccessStatic().createSerializationContext(NbtOps.INSTANCE), tag.get("stored_property"));
         if (data.isSuccess()) {
            return (Holder<Property>)data.getOrThrow();
         }
      }

      return this.getDefaultData();
   }

   public CompoundTag writeData(final Holder<Property> data) {
      return new CompoundTag() {
         {
            if (data != null) {
               this.put(
                  "stored_property",
                  (Tag)Property.CODEC.encodeStart(CommonUtils.registryAccessStatic().createSerializationContext(NbtOps.INSTANCE), data).getOrThrow()
               );
            }
         }
      };
   }

   public Holder<Property> getDefaultData() {
      return null;
   }
}
