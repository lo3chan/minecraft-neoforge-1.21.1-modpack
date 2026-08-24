package net.cibernet.alchemancy.registries;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.cibernet.alchemancy.properties.AbsorbingProperty;
import net.cibernet.alchemancy.properties.AllergicProperty;
import net.cibernet.alchemancy.properties.AnchoredProperty;
import net.cibernet.alchemancy.properties.AncientProperty;
import net.cibernet.alchemancy.properties.AntigravProperty;
import net.cibernet.alchemancy.properties.ArcaneProperty;
import net.cibernet.alchemancy.properties.ArmorPulseProperty;
import net.cibernet.alchemancy.properties.AssembleProperty;
import net.cibernet.alchemancy.properties.AssimilatingProperty;
import net.cibernet.alchemancy.properties.AthleticProperty;
import net.cibernet.alchemancy.properties.AutosmeltProperty;
import net.cibernet.alchemancy.properties.BouncyProperty;
import net.cibernet.alchemancy.properties.BowProperty;
import net.cibernet.alchemancy.properties.BrittleProperty;
import net.cibernet.alchemancy.properties.BrushProperty;
import net.cibernet.alchemancy.properties.BucketingProperty;
import net.cibernet.alchemancy.properties.BuoyantProperty;
import net.cibernet.alchemancy.properties.BurningProperty;
import net.cibernet.alchemancy.properties.CalciumProperty;
import net.cibernet.alchemancy.properties.CapturingProperty;
import net.cibernet.alchemancy.properties.ChanceStrikeProperty;
import net.cibernet.alchemancy.properties.CharmingProperty;
import net.cibernet.alchemancy.properties.CluelessProperty;
import net.cibernet.alchemancy.properties.CompactProperty;
import net.cibernet.alchemancy.properties.ConditionalDamageReductionProperty;
import net.cibernet.alchemancy.properties.ConductiveProperty;
import net.cibernet.alchemancy.properties.CorrosiveProperty;
import net.cibernet.alchemancy.properties.CozyProperty;
import net.cibernet.alchemancy.properties.CrackedProperty;
import net.cibernet.alchemancy.properties.CracklingProperty;
import net.cibernet.alchemancy.properties.CraftyProperty;
import net.cibernet.alchemancy.properties.DamageMultiplierProperty;
import net.cibernet.alchemancy.properties.DeadProperty;
import net.cibernet.alchemancy.properties.DeathrattleProperty;
import net.cibernet.alchemancy.properties.DecayingProperty;
import net.cibernet.alchemancy.properties.DenseProperty;
import net.cibernet.alchemancy.properties.DepthDwellerProperty;
import net.cibernet.alchemancy.properties.DisguisedProperty;
import net.cibernet.alchemancy.properties.DispensingProperty;
import net.cibernet.alchemancy.properties.DivingGearProperty;
import net.cibernet.alchemancy.properties.DrippingProperty;
import net.cibernet.alchemancy.properties.DurabilityMultiplierProperty;
import net.cibernet.alchemancy.properties.EarlyAssemblingProperty;
import net.cibernet.alchemancy.properties.EchoedProperty;
import net.cibernet.alchemancy.properties.EdibleProperty;
import net.cibernet.alchemancy.properties.EncapsulatingProperty;
import net.cibernet.alchemancy.properties.EnchantingProperty;
import net.cibernet.alchemancy.properties.EnderPocketProperty;
import net.cibernet.alchemancy.properties.EnderProperty;
import net.cibernet.alchemancy.properties.EnergizedProperty;
import net.cibernet.alchemancy.properties.EntityPullProperty;
import net.cibernet.alchemancy.properties.EternalProperty;
import net.cibernet.alchemancy.properties.ExperienceBoostProperty;
import net.cibernet.alchemancy.properties.ExperiencedProperty;
import net.cibernet.alchemancy.properties.ExplodingProperty;
import net.cibernet.alchemancy.properties.ExtendedProperty;
import net.cibernet.alchemancy.properties.FeralProperty;
import net.cibernet.alchemancy.properties.FerrousProperty;
import net.cibernet.alchemancy.properties.FireproofProperty;
import net.cibernet.alchemancy.properties.FirestarterProperty;
import net.cibernet.alchemancy.properties.FlammableProperty;
import net.cibernet.alchemancy.properties.FlattenedProperty;
import net.cibernet.alchemancy.properties.FleetingProperty;
import net.cibernet.alchemancy.properties.FlourishingProperty;
import net.cibernet.alchemancy.properties.FragmentedProperty;
import net.cibernet.alchemancy.properties.FrostedProperty;
import net.cibernet.alchemancy.properties.GildedProperty;
import net.cibernet.alchemancy.properties.GliderProperty;
import net.cibernet.alchemancy.properties.GlowRingProperty;
import net.cibernet.alchemancy.properties.GlowingProperty;
import net.cibernet.alchemancy.properties.GrapplingProperty;
import net.cibernet.alchemancy.properties.HardenedProperty;
import net.cibernet.alchemancy.properties.HeadearProperty;
import net.cibernet.alchemancy.properties.HeartyProperty;
import net.cibernet.alchemancy.properties.HeavyProperty;
import net.cibernet.alchemancy.properties.HellbentProperty;
import net.cibernet.alchemancy.properties.HollowProperty;
import net.cibernet.alchemancy.properties.HomingProperty;
import net.cibernet.alchemancy.properties.HydrophobicProperty;
import net.cibernet.alchemancy.properties.ImbuedProperty;
import net.cibernet.alchemancy.properties.ImprovedProperty;
import net.cibernet.alchemancy.properties.IncreaseInfuseSlotsProperty;
import net.cibernet.alchemancy.properties.InfectedProperty;
import net.cibernet.alchemancy.properties.InteractableProperty;
import net.cibernet.alchemancy.properties.JaggedProperty;
import net.cibernet.alchemancy.properties.KineticRechargeProperty;
import net.cibernet.alchemancy.properties.LaunchingProperty;
import net.cibernet.alchemancy.properties.LazyProperty;
import net.cibernet.alchemancy.properties.LeapingProperty;
import net.cibernet.alchemancy.properties.LetsGoGamblingProperty;
import net.cibernet.alchemancy.properties.LevitatingProperty;
import net.cibernet.alchemancy.properties.LightningBoltProperty;
import net.cibernet.alchemancy.properties.LightweightProperty;
import net.cibernet.alchemancy.properties.LooseProperty;
import net.cibernet.alchemancy.properties.LoyalProperty;
import net.cibernet.alchemancy.properties.MagneticProperty;
import net.cibernet.alchemancy.properties.MalleableProperty;
import net.cibernet.alchemancy.properties.MendingProperty;
import net.cibernet.alchemancy.properties.MobEffectEquippedAndHitProperty;
import net.cibernet.alchemancy.properties.MobEffectOnHitProperty;
import net.cibernet.alchemancy.properties.MusicalProperty;
import net.cibernet.alchemancy.properties.NocturnalProperty;
import net.cibernet.alchemancy.properties.NonlethalProperty;
import net.cibernet.alchemancy.properties.OminousProperty;
import net.cibernet.alchemancy.properties.OvergrowthProperty;
import net.cibernet.alchemancy.properties.PhotosyntheticProperty;
import net.cibernet.alchemancy.properties.PristineProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.RandomEffectProperty;
import net.cibernet.alchemancy.properties.ReinforcedProperty;
import net.cibernet.alchemancy.properties.RepelledProperty;
import net.cibernet.alchemancy.properties.ResizedProperty;
import net.cibernet.alchemancy.properties.RestockerProperty;
import net.cibernet.alchemancy.properties.RootedProperty;
import net.cibernet.alchemancy.properties.RotationDataProperty;
import net.cibernet.alchemancy.properties.RustyProperty;
import net.cibernet.alchemancy.properties.SaddledProperty;
import net.cibernet.alchemancy.properties.ScattershotProperty;
import net.cibernet.alchemancy.properties.SculkingProperty;
import net.cibernet.alchemancy.properties.SeethroughProperty;
import net.cibernet.alchemancy.properties.SensitiveProperty;
import net.cibernet.alchemancy.properties.ShatteringProperty;
import net.cibernet.alchemancy.properties.ShearingProperty;
import net.cibernet.alchemancy.properties.ShieldingProperty;
import net.cibernet.alchemancy.properties.ShockDamageProperty;
import net.cibernet.alchemancy.properties.SlipperyProperty;
import net.cibernet.alchemancy.properties.SluggishProperty;
import net.cibernet.alchemancy.properties.SparkingProperty;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.SpikingProperty;
import net.cibernet.alchemancy.properties.SporadicProperty;
import net.cibernet.alchemancy.properties.SporeCloudProperty;
import net.cibernet.alchemancy.properties.SpreadsOnHitProperty;
import net.cibernet.alchemancy.properties.StickyProperty;
import net.cibernet.alchemancy.properties.StonecuttingProperty;
import net.cibernet.alchemancy.properties.SweetProperty;
import net.cibernet.alchemancy.properties.SwiftProperty;
import net.cibernet.alchemancy.properties.ThrowableProperty;
import net.cibernet.alchemancy.properties.TickingProperty;
import net.cibernet.alchemancy.properties.TintedProperty;
import net.cibernet.alchemancy.properties.ToggleableProperty;
import net.cibernet.alchemancy.properties.ToolProperty;
import net.cibernet.alchemancy.properties.TreadmilledProperty;
import net.cibernet.alchemancy.properties.UndeadProperty;
import net.cibernet.alchemancy.properties.UndyingProperty;
import net.cibernet.alchemancy.properties.WardingProperty;
import net.cibernet.alchemancy.properties.WaxedProperty;
import net.cibernet.alchemancy.properties.WayfindingProperty;
import net.cibernet.alchemancy.properties.WeakProperty;
import net.cibernet.alchemancy.properties.WealthyProperty;
import net.cibernet.alchemancy.properties.WetProperty;
import net.cibernet.alchemancy.properties.WildfireProperty;
import net.cibernet.alchemancy.properties.data.modifiers.PropertyModifierType;
import net.cibernet.alchemancy.properties.entangled.ActivationEntangledProperty;
import net.cibernet.alchemancy.properties.entangled.CrouchEntangledProperty;
import net.cibernet.alchemancy.properties.entangled.InteractEntangledProperty;
import net.cibernet.alchemancy.properties.entangled.JumpEntangledProperty;
import net.cibernet.alchemancy.properties.entangled.SprintEntangledProperty;
import net.cibernet.alchemancy.properties.soulbind.EnergySapperProperty;
import net.cibernet.alchemancy.properties.soulbind.HungeringProperty;
import net.cibernet.alchemancy.properties.soulbind.ParasiticProperty;
import net.cibernet.alchemancy.properties.soulbind.RelentlessProperty;
import net.cibernet.alchemancy.properties.soulbind.SoulHarvesterProperty;
import net.cibernet.alchemancy.properties.soulbind.SoulbindProperty;
import net.cibernet.alchemancy.properties.soulbind.SpiritBondProperty;
import net.cibernet.alchemancy.properties.soulbind.VampiricProperty;
import net.cibernet.alchemancy.properties.soulbind.VengefulProperty;
import net.cibernet.alchemancy.properties.special.AirWalkingProperty;
import net.cibernet.alchemancy.properties.special.AuxiliaryProperty;
import net.cibernet.alchemancy.properties.special.BatteryPoweredProperty;
import net.cibernet.alchemancy.properties.special.BindingProperty;
import net.cibernet.alchemancy.properties.special.BlinkingProperty;
import net.cibernet.alchemancy.properties.special.ChromatizeProperty;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.properties.special.DashingProperty;
import net.cibernet.alchemancy.properties.special.DeathWardProperty;
import net.cibernet.alchemancy.properties.special.FlameEmperorProperty;
import net.cibernet.alchemancy.properties.special.FlameWakerProperty;
import net.cibernet.alchemancy.properties.special.FriendlyProperty;
import net.cibernet.alchemancy.properties.special.GustJetProperty;
import net.cibernet.alchemancy.properties.special.HomeRunProperty;
import net.cibernet.alchemancy.properties.special.InfusionCodexProperty;
import net.cibernet.alchemancy.properties.special.ItemMagnetProperty;
import net.cibernet.alchemancy.properties.special.LivingBatteryProperty;
import net.cibernet.alchemancy.properties.special.PhaseRingProperty;
import net.cibernet.alchemancy.properties.special.PhasingProperty;
import net.cibernet.alchemancy.properties.special.QuantumShiftProperty;
import net.cibernet.alchemancy.properties.special.RemoveInfusionsProperty;
import net.cibernet.alchemancy.properties.special.RocketPoweredProperty;
import net.cibernet.alchemancy.properties.special.RotatingProperty;
import net.cibernet.alchemancy.properties.special.RunningStartProperty;
import net.cibernet.alchemancy.properties.special.SoundEffectProperty;
import net.cibernet.alchemancy.properties.special.UnmovableProperty;
import net.cibernet.alchemancy.properties.special.VaultLockpickingProperty;
import net.cibernet.alchemancy.properties.special.WaterWalkingProperty;
import net.cibernet.alchemancy.properties.special.WaywardWarpProperty;
import net.cibernet.alchemancy.properties.voidborn.BigSuckProperty;
import net.cibernet.alchemancy.properties.voidborn.BlockVacuumProperty;
import net.cibernet.alchemancy.properties.voidborn.NullifierProperty;
import net.cibernet.alchemancy.properties.voidborn.TelekineticProperty;
import net.cibernet.alchemancy.properties.voidborn.VoidbornProperty;
import net.cibernet.alchemancy.properties.voidborn.VoidtouchProperty;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public class AlchemancyProperties {
   private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "infusion_properties");
   public static final ResourceKey<Registry<Property>> REGISTRY_KEY = ResourceKey.createRegistryKey(KEY);
   public static final DeferredRegister<Property> REGISTRY = DeferredRegister.create(KEY, "alchemancy");
   private static final Registry<Property> SUPPLIER = REGISTRY.makeRegistry(propertyRegistryBuilder -> propertyRegistryBuilder.defaultKey(KEY).sync(true));
   public static final DeferredHolder<Property, BurningProperty> BURNING = REGISTRY.register("burning", BurningProperty::new);
   public static final DeferredHolder<Property, WetProperty> WET = REGISTRY.register("wet", WetProperty::new);
   public static final DeferredHolder<Property, FrostedProperty> FROSTED = REGISTRY.register("frosted", FrostedProperty::new);
   public static final DeferredHolder<Property, ShockDamageProperty> SHOCKING = REGISTRY.register("shocking", ShockDamageProperty::new);
   public static final DeferredHolder<Property, PhotosyntheticProperty> PHOTOSYNTHETIC = REGISTRY.register("photosynthetic", PhotosyntheticProperty::new);
   public static final DeferredHolder<Property, FlammableProperty> FLAMMABLE = REGISTRY.register("flammable", FlammableProperty::new);
   public static final DeferredHolder<Property, Property> CHARRED = REGISTRY.register("charred", () -> Property.simple(4473410));
   public static final DeferredHolder<Property, DurabilityMultiplierProperty> STURDY = REGISTRY.register(
      "sturdy", () -> new DurabilityMultiplierProperty(10132122, 1.2F)
   );
   public static final DeferredHolder<Property, BrittleProperty> BRITTLE = REGISTRY.register("brittle", BrittleProperty::new);
   public static final DeferredHolder<Property, RustyProperty> RUSTY = REGISTRY.register("rusty", RustyProperty::new);
   public static final DeferredHolder<Property, FerrousProperty> FERROUS = REGISTRY.register("ferrous", FerrousProperty::new);
   public static final DeferredHolder<Property, GildedProperty> GILDED = REGISTRY.register("gilded", GildedProperty::new);
   public static final DeferredHolder<Property, ImprovedProperty> LUSTROUS = REGISTRY.register("lustrous", ImprovedProperty::new);
   public static final DeferredHolder<Property, WealthyProperty> WEALTHY = REGISTRY.register("wealthy", WealthyProperty::new);
   public static final DeferredHolder<Property, ReinforcedProperty> REINFORCED = REGISTRY.register("reinforced", ReinforcedProperty::new);
   public static final DeferredHolder<Property, PristineProperty> PRISTINE = REGISTRY.register("pristine", PristineProperty::new);
   public static final DeferredHolder<Property, HellbentProperty> HELLBENT = REGISTRY.register("hellbent", HellbentProperty::new);
   public static final DeferredHolder<Property, DepthDwellerProperty> DEPTH_DWELLER = REGISTRY.register("depth_dweller", DepthDwellerProperty::new);
   public static final DeferredHolder<Property, OvergrowthProperty> OVERGROWTH = REGISTRY.register("overgrowth", OvergrowthProperty::new);
   public static final DeferredHolder<Property, WildfireProperty> WILDFIRE = REGISTRY.register("wildfire", WildfireProperty::new);
   public static final DeferredHolder<Property, MalleableProperty> MALLEABLE = REGISTRY.register("malleable", MalleableProperty::new);
   public static final DeferredHolder<Property, ClayMoldProperty> CLAY_MOLD = REGISTRY.register("clay_mold", ClayMoldProperty::new);
   public static final DeferredHolder<Property, HardenedProperty> HARDENED = REGISTRY.register("hardened", HardenedProperty::new);
   public static final DeferredHolder<Property, CrackedProperty> CRACKED = REGISTRY.register("cracked", CrackedProperty::new);
   public static final DeferredHolder<Property, EnergizedProperty> ENERGIZED = REGISTRY.register("energized", EnergizedProperty::new);
   public static final DeferredHolder<Property, BouncyProperty> BOUNCY = REGISTRY.register("bouncy", BouncyProperty::new);
   public static final DeferredHolder<Property, StickyProperty> STICKY = REGISTRY.register("sticky", StickyProperty::new);
   public static final DeferredHolder<Property, SlipperyProperty> SLIPPERY = REGISTRY.register("slippery", SlipperyProperty::new);
   public static final DeferredHolder<Property, BuoyantProperty> BUOYANT = REGISTRY.register("buoyant", BuoyantProperty::new);
   public static final DeferredHolder<Property, LightweightProperty> LIGHTWEIGHT = REGISTRY.register("lightweight", LightweightProperty::new);
   public static final DeferredHolder<Property, HeavyProperty> HEAVY = REGISTRY.register("heavy", HeavyProperty::new);
   public static final DeferredHolder<Property, AntigravProperty> ANTIGRAV = REGISTRY.register("antigrav", AntigravProperty::new);
   public static final DeferredHolder<Property, Property> DEXTEROUS = REGISTRY.register("dexterous", () -> Property.simple(60072));
   public static final DeferredHolder<Property, WaterWalkingProperty> WAVE_RIDER = REGISTRY.register("wave_rider", WaterWalkingProperty::new);
   public static final DeferredHolder<Property, AirWalkingProperty> AIR_WALKER = REGISTRY.register("air_walker", AirWalkingProperty::new);
   public static final DeferredHolder<Property, AthleticProperty> ATHLETIC = REGISTRY.register("athletic", AthleticProperty::new);
   public static final DeferredHolder<Property, AnchoredProperty> ANCHORED = REGISTRY.register("anchored", AnchoredProperty::new);
   public static final DeferredHolder<Property, ToolProperty> MINING = REGISTRY.register(
      "mining", () -> new ToolProperty(8947592, BlockTags.MINEABLE_WITH_PICKAXE, ItemAbilities.DEFAULT_PICKAXE_ACTIONS)
   );
   public static final DeferredHolder<Property, ToolProperty> CHOPPING = REGISTRY.register(
      "chopping", () -> new ToolProperty(9531714, BlockTags.MINEABLE_WITH_AXE, ItemAbilities.DEFAULT_AXE_ACTIONS)
   );
   public static final DeferredHolder<Property, ToolProperty> DIGGING = REGISTRY.register(
      "digging", () -> new ToolProperty(12158300, BlockTags.MINEABLE_WITH_SHOVEL, ItemAbilities.DEFAULT_SHOVEL_ACTIONS)
   );
   public static final DeferredHolder<Property, ToolProperty> REAPING = REGISTRY.register(
      "reaping", () -> new ToolProperty(5745983, BlockTags.MINEABLE_WITH_HOE, ItemAbilities.DEFAULT_HOE_ACTIONS)
   );
   public static final DeferredHolder<Property, ShearingProperty> SHEARING = REGISTRY.register(
      "shearing", () -> new ShearingProperty(10899263, ToolProperty.getShearsRules(), ItemAbilities.DEFAULT_SHEARS_ACTIONS)
   );
   public static final DeferredHolder<Property, ToolProperty> SLASHING = REGISTRY.register(
      "slashing", () -> new ToolProperty(14340549, ToolProperty.getSwordRules(), ItemAbilities.DEFAULT_SWORD_ACTIONS)
   );
   public static final DeferredHolder<Property, BowProperty> SHARPSHOOTING = REGISTRY.register("sharpshooting", BowProperty::new);
   public static final DeferredHolder<Property, ShieldingProperty> SHIELDING = REGISTRY.register("shielding", ShieldingProperty::new);
   public static final DeferredHolder<Property, FirestarterProperty> FIRESTARTING = REGISTRY.register(
      "firestarting", () -> new FirestarterProperty(16756817, List.of(), ItemAbilities.DEFAULT_FLINT_ACTIONS)
   );
   public static final DeferredHolder<Property, BrushProperty> BRUSHING = REGISTRY.register("brushing", BrushProperty::new);
   public static final DeferredHolder<Property, Property> SCOPING = REGISTRY.register("scoping", () -> Property.simple(14586426));
   public static final DeferredHolder<Property, WayfindingProperty> WAYFINDING = REGISTRY.register("wayfinding", WayfindingProperty::new);
   public static final DeferredHolder<Property, HeadearProperty> HEADWEAR = REGISTRY.register("headwear", HeadearProperty::new);
   public static final DeferredHolder<Property, SaddledProperty> SADDLED = REGISTRY.register("saddled", SaddledProperty::new);
   public static final DeferredHolder<Property, GliderProperty> GLIDER = REGISTRY.register("glider", GliderProperty::new);
   public static final DeferredHolder<Property, RotationDataProperty> DEATH_TRACKER = REGISTRY.register("death_tracker", () -> new RotationDataProperty() {
      @Override
      public int getColor(ItemStack stack) {
         return ColorUtils.interpolateColorsOverTime(2.0F, -14032917, -16750956);
      }
   });
   public static final DeferredHolder<Property, CraftyProperty> CRAFTY = REGISTRY.register("crafty", CraftyProperty::new);
   public static final DeferredHolder<Property, StonecuttingProperty> STONECUTTING = REGISTRY.register("stonecutting", StonecuttingProperty::new);
   public static final DeferredHolder<Property, EnderPocketProperty> ENDER_POCKET = REGISTRY.register("ender_pocket", EnderPocketProperty::new);
   public static final DeferredHolder<Property, AutosmeltProperty> SMELTING = REGISTRY.register("smelting", AutosmeltProperty::new);
   public static final DeferredHolder<Property, AssimilatingProperty> ASSIMILATING = REGISTRY.register("assimilating", AssimilatingProperty::new);
   public static final DeferredHolder<Property, EarlyAssemblingProperty> ASSEMBLING = REGISTRY.register("assembling", EarlyAssemblingProperty::new);
   public static final DeferredHolder<Property, AssembleProperty> REPLICATING = REGISTRY.register("replicating", AssembleProperty::new);
   public static final DeferredHolder<Property, FragmentedProperty> FRAGMENTED = REGISTRY.register("fragmented", FragmentedProperty::new);
   public static final DeferredHolder<Property, HollowProperty> HOLLOW = REGISTRY.register("hollow", HollowProperty::new);
   public static final DeferredHolder<Property, BucketingProperty> BUCKETING = REGISTRY.register("bucketing", BucketingProperty::new);
   public static final DeferredHolder<Property, EncapsulatingProperty> ENCAPSULATING = REGISTRY.register("encapsulating", EncapsulatingProperty::new);
   public static final DeferredHolder<Property, CapturingProperty> CAPTURING = REGISTRY.register("capturing", CapturingProperty::new);
   public static final DeferredHolder<Property, DrippingProperty> DRIPPING = REGISTRY.register("dripping", DrippingProperty::new);
   public static final DeferredHolder<Property, AbsorbingProperty> ABSORBING = REGISTRY.register("absorbent", AbsorbingProperty::new);
   public static final DeferredHolder<Property, RestockerProperty> RESTOCKER = REGISTRY.register("restocker", RestockerProperty::new);
   public static final DeferredHolder<Property, NullifierProperty> NULLIFIER = REGISTRY.register("nullifier", NullifierProperty::new);
   public static final DeferredHolder<Property, EdibleProperty> EDIBLE = REGISTRY.register("edible", EdibleProperty::new);
   public static final DeferredHolder<Property, JaggedProperty> JAGGED = REGISTRY.register("jagged", JaggedProperty::new);
   public static final DeferredHolder<Property, RootedProperty> ROOTED = REGISTRY.register("rooted", RootedProperty::new);
   public static final DeferredHolder<Property, SensitiveProperty> SENSITIVE = REGISTRY.register("sensitive", SensitiveProperty::new);
   public static final DeferredHolder<Property, InteractableProperty> INTERACTABLE = REGISTRY.register("interactable", InteractableProperty::new);
   public static final DeferredHolder<Property, SporeCloudProperty> MYCELLIC = REGISTRY.register("fungal", SporeCloudProperty::new);
   public static final DeferredHolder<Property, SporadicProperty> SPORADIC = REGISTRY.register("sporadic", SporadicProperty::new);
   public static final DeferredHolder<Property, ShatteringProperty> SHATTERING = REGISTRY.register("shattering", ShatteringProperty::new);
   public static final DeferredHolder<Property, ThrowableProperty> THROWABLE = REGISTRY.register("throwable", ThrowableProperty::new);
   public static final DeferredHolder<Property, ToggleableProperty> TOGGLEABLE = REGISTRY.register("toggleable", ToggleableProperty::new);
   public static final DeferredHolder<Property, TickingProperty> TICKING = REGISTRY.register("ticking", TickingProperty::new);
   public static final DeferredHolder<Property, HydrophobicProperty> HYDROPHOBIC = REGISTRY.register("hydrophobic", HydrophobicProperty::new);
   public static final DeferredHolder<Property, AllergicProperty> ALLERGIC = REGISTRY.register("allergic", AllergicProperty::new);
   public static final DeferredHolder<Property, ArmorPulseProperty> ARMOR_PULSE = REGISTRY.register("armor_pulse", ArmorPulseProperty::new);
   public static final DeferredHolder<Property, RunningStartProperty> RUNNING_START = REGISTRY.register("running_start", RunningStartProperty::new);
   public static final DeferredHolder<Property, ScattershotProperty> SCATTERSHOT = REGISTRY.register("scattershot", ScattershotProperty::new);
   public static final DeferredHolder<Property, DispensingProperty> DISPENSING = REGISTRY.register("dispensing", DispensingProperty::new);
   public static final DeferredHolder<Property, DeathrattleProperty> DEATHRATTLE = REGISTRY.register("deathrattle", DeathrattleProperty::new);
   public static final DeferredHolder<Property, ChanceStrikeProperty> CHANCE_STRIKE = REGISTRY.register("chance_strike", ChanceStrikeProperty::new);
   public static final DeferredHolder<Property, LevitatingProperty> LEVITATING = REGISTRY.register("levitating", LevitatingProperty::new);
   public static final DeferredHolder<Property, SwiftProperty> SWIFT = REGISTRY.register("swift", SwiftProperty::new);
   public static final DeferredHolder<Property, SluggishProperty> SLUGGISH = REGISTRY.register("sluggish", SluggishProperty::new);
   public static final DeferredHolder<Property, MobEffectOnHitProperty> POISONOUS = REGISTRY.register(
      "poisonous", () -> new MobEffectOnHitProperty(new MobEffectInstance(MobEffects.POISON, 100))
   );
   public static final DeferredHolder<Property, DecayingProperty> DECAYING = REGISTRY.register("decaying", DecayingProperty::new);
   public static final DeferredHolder<Property, MobEffectEquippedAndHitProperty> TIPSY = REGISTRY.register(
      "tipsy", () -> new MobEffectEquippedAndHitProperty(new MobEffectInstance(MobEffects.CONFUSION, 200, 1), EquipmentSlotGroup.ANY, true)
   );
   public static final DeferredHolder<Property, MobEffectEquippedAndHitProperty> BLINDING = REGISTRY.register(
      "blinding", () -> new MobEffectEquippedAndHitProperty(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0), EquipmentSlotGroup.HEAD, false)
   );
   public static final DeferredHolder<Property, NocturnalProperty> NOCTURNAL = REGISTRY.register("nocturnal", NocturnalProperty::new);
   public static final DeferredHolder<Property, DivingGearProperty> AQUATIC = REGISTRY.register("aquatic", DivingGearProperty::new);
   public static final DeferredHolder<Property, LeapingProperty> LEAPING = REGISTRY.register("leaping", LeapingProperty::new);
   public static final DeferredHolder<Property, GlowingProperty> GLOWING_AURA = REGISTRY.register("glowing_aura", GlowingProperty::new);
   public static final DeferredHolder<Property, OminousProperty> OMINOUS = REGISTRY.register("ominous", OminousProperty::new);
   public static final DeferredHolder<Property, HeartyProperty> HEARTY = REGISTRY.register("hearty", HeartyProperty::new);
   public static final DeferredHolder<Property, MobEffectEquippedAndHitProperty> INFERNAL = REGISTRY.register(
      "infernal", () -> new MobEffectEquippedAndHitProperty(new MobEffectInstance(AlchemancyMobEffects.INFERNO, 60, 0), EquipmentSlotGroup.ARMOR, false)
   );
   public static final DeferredHolder<Property, ImbuedProperty> IMBUED = REGISTRY.register("imbued", ImbuedProperty::new);
   public static final DeferredHolder<Property, GrapplingProperty> GRAPPLING = REGISTRY.register("grappling", GrapplingProperty::new);
   public static final DeferredHolder<Property, SpikingProperty> SPIKING = REGISTRY.register("spiking", SpikingProperty::new);
   public static final DeferredHolder<Property, LaunchingProperty> LAUNCHING = REGISTRY.register("launching", LaunchingProperty::new);
   public static final DeferredHolder<Property, DamageMultiplierProperty> SHARP = REGISTRY.register("sharp", () -> new DamageMultiplierProperty(15394270, 0.3F));
   public static final DeferredHolder<Property, WeakProperty> WEAK = REGISTRY.register("weak", WeakProperty::new);
   public static final DeferredHolder<Property, DenseProperty> DENSE = REGISTRY.register("dense", DenseProperty::new);
   public static final DeferredHolder<Property, LetsGoGamblingProperty> GAMBLING = REGISTRY.register("gambling", LetsGoGamblingProperty::new);
   public static final DeferredHolder<Property, ArcaneProperty> ARCANE = REGISTRY.register("arcane", ArcaneProperty::new);
   public static final DeferredHolder<Property, ResizedProperty> RESIZED = REGISTRY.register("resized", ResizedProperty::new);
   public static final DeferredHolder<Property, FeralProperty> FERAL = REGISTRY.register("feral", FeralProperty::new);
   public static final DeferredHolder<Property, CracklingProperty> CRACKLING = REGISTRY.register("crackling", CracklingProperty::new);
   public static final DeferredHolder<Property, EchoedProperty> ECHOED = REGISTRY.register("echoed", EchoedProperty::new);
   public static final DeferredHolder<Property, CorrosiveProperty> CORROSIVE = REGISTRY.register("corrosive", CorrosiveProperty::new);
   public static final DeferredHolder<Property, ExplodingProperty> EXPLODING = REGISTRY.register(
      "exploding", () -> new ExplodingProperty(14364442, 3.0F, 5.0F, ExplodingProperty.destroyBlocks())
   );
   public static final DeferredHolder<Property, ExplodingProperty> WIND_CHARGED = REGISTRY.register(
      "wind_charged", () -> new ExplodingProperty(((MobEffect)MobEffects.WIND_CHARGED.value()).getColor(), 3.0F, 5.0F, ExplodingProperty.gust())
   );
   public static final DeferredHolder<Property, LightningBoltProperty> SMITING = REGISTRY.register("smiting", LightningBoltProperty::new);
   public static final DeferredHolder<Property, CozyProperty> COZY = REGISTRY.register("cozy", CozyProperty::new);
   public static final DeferredHolder<Property, WaxedProperty> WAXED = REGISTRY.register("waxed", WaxedProperty::new);
   public static final DeferredHolder<Property, FireproofProperty> FIRE_RESISTANT = REGISTRY.register("fire_resistant", FireproofProperty::new);
   public static final DeferredHolder<Property, ConditionalDamageReductionProperty> BLAST_RESISTANT = REGISTRY.register(
      "blast_resistant", () -> ConditionalDamageReductionProperty.reduceDamageByTag(3876692, DamageTypeTags.IS_EXPLOSION, 0.5F)
   );
   public static final DeferredHolder<Property, ConditionalDamageReductionProperty> INSULATED = REGISTRY.register(
      "insulated", () -> ConditionalDamageReductionProperty.reduceShockDamage(6656401)
   );
   public static final DeferredHolder<Property, WardingProperty> WARDING = REGISTRY.register("warding", WardingProperty::new);
   public static final DeferredHolder<Property, AncientProperty> ANCIENT = REGISTRY.register("ancient", AncientProperty::new);
   public static final DeferredHolder<Property, EternalProperty> ETERNAL = REGISTRY.register("eternal", EternalProperty::new);
   public static final DeferredHolder<Property, Property> MUFFLED = REGISTRY.register("muffled", () -> Property.simple(6198009));
   public static final DeferredHolder<Property, ConditionalDamageReductionProperty> MAGIC_RESISTANT = REGISTRY.register(
      "magic_resistant", () -> ConditionalDamageReductionProperty.reduceDamageByTag(6165759, AlchemancyTags.DamageTypes.AFFECTED_BY_MAGIC_RESISTANT, 0.85F)
   );
   public static final DeferredHolder<Property, ConditionalDamageReductionProperty> SMOOTH = REGISTRY.register(
      "smooth", () -> ConditionalDamageReductionProperty.reduceDamageByTag(10921646, AlchemancyTags.DamageTypes.AFFECTED_BY_SMOOTH, 0.0F)
   );
   public static final DeferredHolder<Property, Property> SOULBIND = REGISTRY.register("soulbind", SoulbindProperty::new);
   public static final DeferredHolder<Property, LoyalProperty> LOYAL = REGISTRY.register("loyal", LoyalProperty::new);
   public static final DeferredHolder<Property, VengefulProperty> VENGEFUL = REGISTRY.register("vengeful", VengefulProperty::new);
   public static final DeferredHolder<Property, VampiricProperty> VAMPIRIC = REGISTRY.register("vampiric", VampiricProperty::new);
   public static final DeferredHolder<Property, EnergySapperProperty> ENERGY_SAPPER = REGISTRY.register("energy_sapper", EnergySapperProperty::new);
   public static final DeferredHolder<Property, RelentlessProperty> RELENTLESS = REGISTRY.register("relentless", RelentlessProperty::new);
   public static final DeferredHolder<Property, SpiritBondProperty> SPIRIT_BOND = REGISTRY.register("spirit_bond", SpiritBondProperty::new);
   public static final DeferredHolder<Property, PhasingProperty> PHASING = REGISTRY.register("phasing", PhasingProperty::new);
   public static final DeferredHolder<Property, HungeringProperty> HUNGERING = REGISTRY.register("hungering", HungeringProperty::new);
   public static final DeferredHolder<Property, ParasiticProperty> PARASITIC = REGISTRY.register("parasitic", ParasiticProperty::new);
   public static final DeferredHolder<Property, SoulHarvesterProperty> SOUL_HARVESTER = REGISTRY.register("soul_harvester", SoulHarvesterProperty::new);
   public static final DeferredHolder<Property, FleetingProperty> FLEETING = REGISTRY.register("fleeting", FleetingProperty::new);
   public static final DeferredHolder<Property, VoidbornProperty> VOIDBORN = REGISTRY.register("voidborn", VoidbornProperty::new);
   public static final DeferredHolder<Property, BigSuckProperty> CEASELESS_VOID = REGISTRY.register("ceaseless_void", BigSuckProperty::new);
   public static final DeferredHolder<Property, VoidtouchProperty> VOIDTOUCH = REGISTRY.register("voidtouch", VoidtouchProperty::new);
   public static final DeferredHolder<Property, TelekineticProperty> KINETIC_GRAB = REGISTRY.register("kinetic_grab", TelekineticProperty::new);
   public static final DeferredHolder<Property, EntityPullProperty<Entity>> VACUUMING = REGISTRY.register(
      "vacuuming", () -> new EntityPullProperty<>(4661933, Entity.class, 8.0F, true, 0.25F)
   );
   public static final DeferredHolder<Property, NonlethalProperty> NONLETHAL = REGISTRY.register("nonlethal", NonlethalProperty::new);
   public static final DeferredHolder<Property, MendingProperty> MENDING = REGISTRY.register("mending", MendingProperty::new);
   public static final DeferredHolder<Property, FlourishingProperty> FLOURISH = REGISTRY.register("flourish", FlourishingProperty::new);
   public static final DeferredHolder<Property, UndeadProperty> UNDEAD = REGISTRY.register("undead", UndeadProperty::new);
   public static final DeferredHolder<Property, UndyingProperty> UNDYING = REGISTRY.register("undying", UndyingProperty::new);
   public static final DeferredHolder<Property, InfectedProperty> INFECTED = REGISTRY.register("infected", InfectedProperty::new);
   public static final DeferredHolder<Property, DeadProperty> DEAD = REGISTRY.register("dead", DeadProperty::new);
   public static final DeferredHolder<Property, Property> SANITIZED = REGISTRY.register("sanitized", () -> Property.simple(12582900));
   public static final DeferredHolder<Property, SweetProperty> SWEET = REGISTRY.register("sweet", SweetProperty::new);
   public static final DeferredHolder<Property, Property> SCARY = REGISTRY.register("scary", () -> Property.simple(8003584));
   public static final DeferredHolder<Property, Property> SEEDED = REGISTRY.register(
      "seeded", () -> SpreadsOnHitProperty.simple(11317304, EquipmentSlotGroup.ARMOR)
   );
   public static final DeferredHolder<Property, Property> PUTRID = REGISTRY.register(
      "putrid", () -> SpreadsOnHitProperty.simple(6839171, EquipmentSlotGroup.ARMOR)
   );
   public static final DeferredHolder<Property, CharmingProperty> CHARMING = REGISTRY.register("charming", CharmingProperty::new);
   public static final DeferredHolder<Property, ConductiveProperty> CONDUCTIVE = REGISTRY.register("conductive", ConductiveProperty::new);
   public static final DeferredHolder<Property, CluelessProperty> CLUELESS = REGISTRY.register("clueless", CluelessProperty::new);
   public static final DeferredHolder<Property, Property> ENCHANTING = REGISTRY.register("enchanting", EnchantingProperty::new);
   public static final DeferredHolder<Property, ExperienceBoostProperty> WISE = REGISTRY.register("wise", ExperienceBoostProperty::new);
   public static final DeferredHolder<Property, ExperiencedProperty> EXPERIENCED = REGISTRY.register("experienced", ExperiencedProperty::new);
   public static final DeferredHolder<Property, EnderProperty> ENDER = REGISTRY.register("ender", EnderProperty::new);
   public static final DeferredHolder<Property, SculkingProperty> SCULKING = REGISTRY.register("sculking", SculkingProperty::new);
   public static final DeferredHolder<Property, LooseProperty> LOOSE = REGISTRY.register("loose", LooseProperty::new);
   public static final DeferredHolder<Property, SparkingProperty> SPARKING = REGISTRY.register("sparking", SparkingProperty::new);
   public static final DeferredHolder<Property, ExtendedProperty> EXTENDED = REGISTRY.register("extended", ExtendedProperty::new);
   public static final DeferredHolder<Property, CalciumProperty> CALCAREOUS = REGISTRY.register("calcareous", CalciumProperty::new);
   public static final DeferredHolder<Property, MusicalProperty> MUSICAL = REGISTRY.register("musical", MusicalProperty::new);
   public static final DeferredHolder<Property, EntityPullProperty<Projectile>> TARGETED = REGISTRY.register(
      "targeted", () -> new EntityPullProperty(14436938, Projectile.class, 16.0F, false, 1.0F)
   );
   public static final DeferredHolder<Property, RepelledProperty<Entity>> REPELLED = REGISTRY.register(
      "repelled", () -> new RepelledProperty<>(4906204, Entity.class, 8.0F, false)
   );
   public static final DeferredHolder<Property, HomingProperty<LivingEntity>> LIGHT_SEEKING = REGISTRY.register(
      "light_seeking",
      () -> new HomingProperty(
         16776960, LivingEntity.class, 24.0F, 1.0F, HomingProperty.EffectType.PROJECTILE_ONLY, e -> e.isOnFire() || e.isCurrentlyGlowing()
      )
   );
   public static final DeferredHolder<Property, Property> FLIMSY = REGISTRY.register("flimsy", () -> Property.simple(12633245));
   public static final DeferredHolder<Property, CompactProperty> COMPACT = REGISTRY.register("compact", CompactProperty::new);
   public static final DeferredHolder<Property, MagneticProperty> MAGNETIC = REGISTRY.register("magnetic", MagneticProperty::new);
   public static final DeferredHolder<Property, KineticRechargeProperty> KINETIC_RECHARGE = REGISTRY.register("kinetic_recharge", KineticRechargeProperty::new);
   public static final DeferredHolder<Property, LazyProperty> LAZY = REGISTRY.register("lazy", LazyProperty::new);
   public static final DeferredHolder<Property, TreadmilledProperty> CONVEYED = REGISTRY.register("conveyed", TreadmilledProperty::new);
   public static final DeferredHolder<Property, Property> REVEALED = REGISTRY.register("revealed", () -> Property.simple(14081535));
   public static final DeferredHolder<Property, Property> REVEALING = REGISTRY.register(
      "revealing", () -> Property.simple(style -> style.withBold(true), () -> 14081535)
   );
   public static final DeferredHolder<Property, Property> SCRAMBLED = REGISTRY.register("scrambled", () -> Property.simple(2695680));
   public static final DeferredHolder<Property, Property> CONCEALED = REGISTRY.register("concealed", () -> Property.simple(6313573));
   public static final DeferredHolder<Property, DisguisedProperty> DISGUISED = REGISTRY.register("disguised", DisguisedProperty::new);
   public static final DeferredHolder<Property, SeethroughProperty> SEETHROUGH = REGISTRY.register("seethrough", SeethroughProperty::new);
   public static final DeferredHolder<Property, TintedProperty> TINTED = REGISTRY.register("tinted", TintedProperty::new);
   public static final DeferredHolder<Property, FlattenedProperty> FLATTENED = REGISTRY.register("flattened", FlattenedProperty::new);
   public static final DeferredHolder<Property, SparklingProperty> SPARKLING = REGISTRY.register("sparkling", SparklingProperty::new);
   public static final DeferredHolder<Property, Property> AWAKENED = REGISTRY.register(
      "awakened", () -> Property.simpleInterpolated(false, 0.5F, -7214365, -7214365, -1182984, -1328165, -1328165, -1182984)
   );
   public static final DeferredHolder<Property, Property> PARADOXICAL = REGISTRY.register(
      "paradoxical", () -> Property.simpleInterpolated(true, 0.2F, -65536, -256, -16711936, -16776961, -6225665)
   );
   public static final DeferredHolder<Property, Property> LIMIT_BREAK = REGISTRY.register(
      "limit_break",
      () -> IncreaseInfuseSlotsProperty.simple(
         1, style -> style.withBold(true), IncreaseInfuseSlotsProperty::limitBreakColors, IncreaseInfuseSlotsProperty::limitBreakCreativeTab
      )
   );
   public static final DeferredHolder<Property, ActivationEntangledProperty> ENTANGLED = REGISTRY.register("entangled", ActivationEntangledProperty::new);
   public static final DeferredHolder<Property, InteractEntangledProperty> USE_ENTANGLED = REGISTRY.register("use_entangled", InteractEntangledProperty::new);
   public static final DeferredHolder<Property, CrouchEntangledProperty> CROUCH_ENTANGLED = REGISTRY.register("crouch_entangled", CrouchEntangledProperty::new);
   public static final DeferredHolder<Property, JumpEntangledProperty> JUMP_ENTANGLED = REGISTRY.register("jump_entangled", JumpEntangledProperty::new);
   public static final DeferredHolder<Property, SprintEntangledProperty> SPRINT_ENTANGLED = REGISTRY.register("sprint_entangled", SprintEntangledProperty::new);
   public static final DeferredHolder<Property, QuantumShiftProperty> QUANTUM_SHIFT = REGISTRY.register("quantum_shift", QuantumShiftProperty::new);
   public static final DeferredHolder<Property, Property> DIRTY = REGISTRY.register("dirty", () -> Property.simple(9853230));
   public static final DeferredHolder<Property, Property> AWKWARD = REGISTRY.register("awkward", () -> Property.simple(10823276));
   public static final DeferredHolder<Property, Property> WARPED = REGISTRY.register("warped", () -> Property.simple(1356933));
   public static final DeferredHolder<Property, RandomEffectProperty> RANDOM = REGISTRY.register("random", RandomEffectProperty::new);
   public static final DeferredHolder<Property, BlockVacuumProperty> WORLD_OBLITERATOR = REGISTRY.register("world_obliterator", BlockVacuumProperty::new);
   public static final DeferredHolder<Property, Property> UNMOVABLE = REGISTRY.register("unmovable", UnmovableProperty::new);
   public static final DeferredHolder<Property, ItemMagnetProperty> ITEM_PULL = REGISTRY.register("item_pull", ItemMagnetProperty::new);
   public static final DeferredHolder<Property, ChromatizeProperty> CHROMATIZE = REGISTRY.register("chromatize", ChromatizeProperty::new);
   public static final DeferredHolder<Property, RotatingProperty> ROTATING = REGISTRY.register("rotating", RotatingProperty::new);
   public static final DeferredHolder<Property, BatteryPoweredProperty> BATTERY_POWERED = REGISTRY.register("battery_powered", BatteryPoweredProperty::new);
   public static final DeferredHolder<Property, LivingBatteryProperty> LIVING_BATTERY = REGISTRY.register("living_battery", LivingBatteryProperty::new);
   public static final DeferredHolder<Property, SoundEffectProperty> BADA_QUIP = REGISTRY.register(
      "bada_quip", () -> new SoundEffectProperty(7506394, (SoundEvent)AlchemancySoundEvents.BADA_QUIP.value(), true)
   );
   public static final DeferredHolder<Property, InfusionCodexProperty> INFUSION_CODEX = REGISTRY.register("infusion_codex", InfusionCodexProperty::new);
   public static final DeferredHolder<Property, AuxiliaryProperty> AUXILIARY = REGISTRY.register("auxiliary", AuxiliaryProperty::new);
   public static final DeferredHolder<Property, GlowRingProperty> ETERNAL_GLOW = REGISTRY.register("eternal_glow", GlowRingProperty::new);
   public static final DeferredHolder<Property, PhaseRingProperty> PHASE_STEP = REGISTRY.register("phase_step", PhaseRingProperty::new);
   public static final DeferredHolder<Property, DeathWardProperty> DEATH_WARD = REGISTRY.register("death_ward", DeathWardProperty::new);
   public static final DeferredHolder<Property, FriendlyProperty> FRIENDLY = REGISTRY.register("friendly", FriendlyProperty::new);
   public static final DeferredHolder<Property, WaywardWarpProperty> WAYWARD_WARP = REGISTRY.register("wayward_warp", WaywardWarpProperty::new);
   public static final DeferredHolder<Property, RocketPoweredProperty> ROCKET_POWERED = REGISTRY.register("rocket_powered", RocketPoweredProperty::new);
   public static final DeferredHolder<Property, BindingProperty> BINDING = REGISTRY.register("binding", BindingProperty::new);
   public static final DeferredHolder<Property, RemoveInfusionsProperty> INFUSION_CLEANSE = REGISTRY.register(
      "infusion_cleanse", () -> new RemoveInfusionsProperty(() -> 5664383)
   );
   public static final DeferredHolder<Property, RemoveInfusionsProperty> DIVINE_CLEANSE = REGISTRY.register(
      "divine_cleanse",
      () -> new RemoveInfusionsProperty(
         () -> ColorUtils.interpolateColorsOverTime(2.0F, 8760260, 16187306), AlchemancyTags.Properties.AFFECTED_BY_DIVINE_CLEANSE
      )
   );
   public static final DeferredHolder<Property, FlameWakerProperty> FLAME_STEP = REGISTRY.register("flame_step", FlameWakerProperty::new);
   public static final DeferredHolder<Property, FlameEmperorProperty> FLAME_EMPEROR = REGISTRY.register("flame_emperor", FlameEmperorProperty::new);
   public static final DeferredHolder<Property, BlinkingProperty> BLINKING = REGISTRY.register("blinking", BlinkingProperty::new);
   public static final DeferredHolder<Property, DashingProperty> CLOUD_DASH = REGISTRY.register(
      "cloud_dash", () -> new DashingProperty(1.3F, 5551359, 16736852)
   );
   public static final DeferredHolder<Property, DashingProperty> CRYSTAL_DASH = REGISTRY.register(
      "crystal_dash", () -> new DashingProperty(1.6F, 14186495, 5551359, 16736852)
   );
   public static final DeferredHolder<Property, HomeRunProperty> HOME_RUN = REGISTRY.register("home_run", HomeRunProperty::new);
   public static final DeferredHolder<Property, VaultLockpickingProperty> VAULTPICKING = REGISTRY.register("vaultpicking", VaultLockpickingProperty::new);
   public static final DeferredHolder<Property, GustJetProperty> GUST_JET = REGISTRY.register("gust_jet", GustJetProperty::new);
   public static final DeferredHolder<Property, Property> TINTED_LENS = REGISTRY.register("tinted_lens", () -> Property.simple(-13290118));

   @Nullable
   public static Holder<Property> getProperty(ResourceLocation key) {
      return (Holder<Property>)SUPPLIER.asLookup().get(ResourceKey.create(REGISTRY.getRegistryKey(), key)).orElse(null);
   }

   private static Holder<Property> getProperty(TagKey<Item> tag) {
      return getProperty(
         ResourceLocation.fromNamespaceAndPath(tag.location().getNamespace(), tag.location().getPath().substring(tag.location().getPath().lastIndexOf(47) + 1))
      );
   }

   public static ResourceLocation getKeyFor(Property property) {
      return SUPPLIER.getKey(property);
   }

   public static List<Holder<Property>> getDormantProperties(ItemStack stack) {
      List<Holder<Property>> res = new ArrayList<>();

      for (TagKey<Item> tag : stack.getTags().filter(t -> t.location().getPath().contains("dormant_properties/")).toList()) {
         Holder<Property> property = getProperty(tag);
         if (property != null) {
            res.add(property);
         }
      }

      return res;
   }

   @Nullable
   public static Holder<Property> getHolder(Property property) {
      return (Holder<Property>)SUPPLIER.asLookup().get(ResourceKey.create(REGISTRY.getRegistryKey(), property.getKey())).orElse(null);
   }

   public static Collection<DeferredHolder<Property, ? extends Property>> getAllAsHolders() {
      return REGISTRY.getEntries();
   }

   public static Collection<Property> getAll() {
      return getAllAsHolders().stream().map(holder -> (Property)holder.value()).toList();
   }

   public static class Modifiers {
      private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath("alchemancy", "property_modifiers");
      public static final DeferredRegister<PropertyModifierType<?>> REGISTRY = DeferredRegister.create(KEY, "alchemancy");
      public static final Registry<PropertyModifierType<?>> SUPPLIER = REGISTRY.makeRegistry(
         propertyRegistryBuilder -> propertyRegistryBuilder.defaultKey(KEY).sync(true)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Boolean>> IGNORE_INFUSED = REGISTRY.register(
         "ignore_infused", PropertyModifierType.build(false, Codec.BOOL, ByteBufCodecs.BOOL)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Boolean>> PREVENT_CONSUMPTION = REGISTRY.register(
         "prevent_consumption", PropertyModifierType.build(false, Codec.BOOL, ByteBufCodecs.BOOL)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Boolean>> ON_RIGHT_CLICK = REGISTRY.register(
         "on_right_click", PropertyModifierType.build(false, Codec.BOOL, ByteBufCodecs.BOOL)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Integer>> DURABILITY_CONSUMPTION = REGISTRY.register(
         "durability_consumption", PropertyModifierType.build(1, Codec.INT, ByteBufCodecs.INT)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> ATTACK_DAMAGE = REGISTRY.register(
         "attack_damage", PropertyModifierType.build(1.0F, Codec.FLOAT, ByteBufCodecs.FLOAT)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> ATTACK_RADIUS = REGISTRY.register(
         "attack_radius", PropertyModifierType.build(1.0F, Codec.FLOAT, ByteBufCodecs.FLOAT)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> EFFECT_RADIUS = REGISTRY.register(
         "effect_radius", PropertyModifierType.build(1.0F, Codec.FLOAT, ByteBufCodecs.FLOAT)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> EFFECT_VALUE = REGISTRY.register(
         "effect_value", PropertyModifierType.build(1.0F, Codec.FLOAT, ByteBufCodecs.FLOAT)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Integer>> BONUS_SLOTS = REGISTRY.register(
         "bonus_slots", PropertyModifierType.build(0, Codec.INT, ByteBufCodecs.INT)
      );
      public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> ROTATION = REGISTRY.register(
         "rotation", PropertyModifierType.build(1.0F, Codec.FLOAT, ByteBufCodecs.FLOAT)
      );

      public static Holder<PropertyModifierType<?>> asHolder(PropertyModifierType<?> modifierType) {
         return (Holder<PropertyModifierType<?>>)SUPPLIER.asLookup().get(ResourceKey.create(REGISTRY.getRegistryKey(), getKey(modifierType))).orElse(null);
      }

      public static ResourceLocation getKey(PropertyModifierType<?> modifierType) {
         return SUPPLIER.getKey(modifierType);
      }
   }
}
