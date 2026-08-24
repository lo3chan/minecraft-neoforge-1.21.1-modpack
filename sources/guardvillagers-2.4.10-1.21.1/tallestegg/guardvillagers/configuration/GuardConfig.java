package tallestegg.guardvillagers.configuration;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class GuardConfig {
   public static final ModConfigSpec COMMON_SPEC;
   public static final GuardConfig.CommonConfig COMMON;
   public static final ModConfigSpec CLIENT_SPEC;
   public static final GuardConfig.ClientConfig CLIENT;
   public static final ModConfigSpec STARTUP_SPEC;
   public static final GuardConfig.StartUpConfig STARTUP;

   static {
      Pair<GuardConfig.CommonConfig, ModConfigSpec> specPair = new Builder().configure(GuardConfig.CommonConfig::new);
      COMMON = (GuardConfig.CommonConfig)specPair.getLeft();
      COMMON_SPEC = (ModConfigSpec)specPair.getRight();
      Pair<GuardConfig.ClientConfig, ModConfigSpec> specPair1 = new Builder().configure(GuardConfig.ClientConfig::new);
      CLIENT = (GuardConfig.ClientConfig)specPair1.getLeft();
      CLIENT_SPEC = (ModConfigSpec)specPair1.getRight();
      Pair<GuardConfig.StartUpConfig, ModConfigSpec> specPair2 = new Builder().configure(GuardConfig.StartUpConfig::new);
      STARTUP = (GuardConfig.StartUpConfig)specPair2.getLeft();
      STARTUP_SPEC = (ModConfigSpec)specPair2.getRight();
   }

   public static class ClientConfig {
      public final BooleanValue GuardSteve;
      public final BooleanValue bigHeadBabyVillager;
      public final BooleanValue guardInventoryNumbers;

      public ClientConfig(Builder builder) {
         this.GuardSteve = builder.comment("Textures not included, make your own textures by making a resource pack that adds guard_steve_0 - 6")
            .translation("guardvillagers.config.steveModel")
            .define("Have guards use the steve model?", false);
         this.bigHeadBabyVillager = builder.define("Have baby villagers have big heads like in bedrock?", true);
         this.guardInventoryNumbers = builder.comment("Note that this option will automatically activate if a guard has more hearts than default")
            .define("Display guard health in icons", true);
      }
   }

   public static class CommonConfig {
      public final BooleanValue RaidAnimals;
      public final BooleanValue WitchesVillager;
      public final BooleanValue AttackAllMobs;
      public final BooleanValue MobsAttackGuards;
      public final BooleanValue VillagersRunFromPolarBears;
      public final BooleanValue IllagersRunFromPolarBears;
      public final BooleanValue GuardsRunFromPolarBears;
      public final BooleanValue GuardsOpenDoors;
      public final BooleanValue GuardRaiseShield;
      public final BooleanValue GuardFormation;
      public final BooleanValue FriendlyFire;
      public final BooleanValue ConvertVillagerIfHaveHOTV;
      public final BooleanValue guardTeleport;
      public final BooleanValue BlacksmithHealing;
      public final BooleanValue ClericHealing;
      public final BooleanValue guardArrowsHurtVillagers;
      public final BooleanValue armorersRepairGuardArmor;
      public final BooleanValue giveGuardStuffHOTV;
      public final BooleanValue setGuardPatrolHotv;
      public final BooleanValue followHero;
      public final BooleanValue golemFloat;
      public final BooleanValue multiFollow;
      public final BooleanValue guardPatrolVillageAi;
      public final BooleanValue convertGuardOnDeath;
      public final BooleanValue guardSinkToFightUnderWater;
      public final BooleanValue guardPatrolAroundVillageWorkstations;
      public final ConfigValue<List<? extends String>> MobBlackList;
      public final ConfigValue<List<? extends String>> MobWhiteList;
      public final ConfigValue<List<? extends String>> convertibleProfessions;
      public final ConfigValue<List<? extends String>> professionsThatHeal;
      public final ConfigValue<List<? extends String>> professionsThatRepairGolems;
      public final ConfigValue<List<? extends String>> professionsThatRepairGuards;
      public final ConfigValue<List<? extends String>> structuresThatSpawnGuards;
      public final ConfigValue<List<? extends String>> mobsGuardsProtectTargeted;
      public final ConfigValue<List<? extends String>> mobsGuardsProtectHurt;
      public final IntValue reputationRequirement;
      public final IntValue reputationRequirementToBeAttacked;
      public final IntValue guardSpawnInVillage;
      public final IntValue maxClericHeal;
      public final IntValue maxGolemRepair;
      public final IntValue maxVillageRepair;
      public final DoubleValue chanceToDropEquipment;
      public final DoubleValue chanceToBreakEquipment;
      public final DoubleValue guardCrossbowAttackRadius;
      public final DoubleValue GuardVillagerHelpRange;
      public final DoubleValue amountOfHealthRegenerated;
      public final DoubleValue friendlyFireCheckValue;
      public final IntValue depthGuardHuntUnderwater;

      public CommonConfig(Builder builder) {
         builder.push("raids and illagers");
         this.RaidAnimals = builder.comment("Illagers In Raids Attack Animals?")
            .translation("guardvillagers.config.RaidAnimals")
            .define("Illagers in raids attack animals?", true);
         this.WitchesVillager = builder.comment("Witches Attack Villagers?")
            .translation("guardvillagers.config.WitchesVillager")
            .define("Witches attack villagers?", true);
         this.IllagersRunFromPolarBears = builder.comment("This makes Illagers run from polar bears, as anyone with common sense would.")
            .translation("guardvillagers.config.IllagersRunFromPolarBears")
            .define("Have Illagers have some common sense?", true);
         builder.pop();
         builder.push("mob ai in general");
         this.AttackAllMobs = builder.comment(
               "Guards will attack all hostiles with this option, when set to false guards will only attack zombies and illagers."
            )
            .translation("guardvillagers.config.AttackAllMobs")
            .define("Guards attack all mobs?", true);
         this.MobsAttackGuards = builder.comment(
               "Hostiles attack guards, by default only illagers and zombies will attack guards, the mob blacklist below will effect this option"
            )
            .define("All mobs attack guards", false);
         this.MobBlackList = builder.comment(
               "Guards won't attack mobs in this list at all, for example, putting \"minecraft:creeper\" in this list will make guards ignore creepers."
            )
            .defineListAllowEmpty(
               "Mob Blacklist",
               ImmutableList.of(
                  "minecraft:villager", "minecraft:iron_golem", "minecraft:wandering_trader", "guardvillagers:guard", "minecraft:creeper", "minecraft:enderman"
               ),
               () -> "",
               obj -> true
            );
         this.MobWhiteList = builder.comment(
               "Guards will additionally attack mobs ids put in this list, for example, putting \"minecraft:cow\" in this list will make guards attack cows."
            )
            .defineListAllowEmpty("Mob Whitelist", new ArrayList(), () -> "", obj -> true);
         builder.pop();
         builder.push("villager stuff");
         this.professionsThatHeal = builder.defineListAllowEmpty(
            "Profession Whitelist for healing ai for clerics", ImmutableList.of("cleric"), () -> "", obj -> true
         );
         this.professionsThatRepairGolems = builder.defineListAllowEmpty(
            "Profession Whitelist for golem repair ai", ImmutableList.of("armorer", "weaponsmith"), () -> "", obj -> true
         );
         this.professionsThatRepairGuards = builder.defineListAllowEmpty(
            "Profession Whitelist for guard weaponry repair ai", ImmutableList.of("weaponsmith", "armorer", "toolsmith"), () -> "", obj -> true
         );
         this.maxClericHeal = builder.defineInRange("How many times a cleric can heal a guard in one day", 3, 0, 1000000);
         this.maxGolemRepair = builder.defineInRange("How many times a smith villager can heal a golem in one day", 3, 0, 1000000);
         this.maxVillageRepair = builder.defineInRange("How many times a villager can heal a guard's equipment in one day", 3, 0, 1000000);
         this.armorersRepairGuardArmor = builder.translation("guardvillagers.config.armorvillager")
            .define("Allow armorers and weaponsmiths repair guard items when down below half durability?", true);
         this.ConvertVillagerIfHaveHOTV = builder.comment(
               "This will make it so villagers will only be converted into guards if the player has hero of the village"
            )
            .translation("guardvillagers.config.hotv")
            .define("Make it so players have to have hero of the village to convert villagers into guards?", false);
         this.BlacksmithHealing = builder.translation("guardvillagers.config.blacksmith").define("Have it so blacksmiths heal golems under 60 health?", true);
         this.ClericHealing = builder.translation("guardvillagers.config.cleric")
            .define("Have it so clerics heal guards and players with hero of the village?", true);
         this.VillagersRunFromPolarBears = builder.comment("This makes villagers run from polar bears, as anyone with common sense would.")
            .translation("guardvillagers.config.VillagersRunFromPolarBears")
            .define("Have Villagers have some common sense?", true);
         this.convertibleProfessions = builder.comment("Professions that can be converted into guards")
            .defineListAllowEmpty("Profession Whitelist for guard conversion", ImmutableList.of("nitwit", "none"), () -> "", obj -> true);
         builder.pop();
         builder.push("golem stuff");
         this.golemFloat = builder.define("Allow Iron Golems to float on water?", false);
         builder.pop();
         builder.push("guard stuff");
         this.guardSinkToFightUnderWater = builder.define("Allow guards to sink temporarily to fight mobs that are under water?", true);
         this.depthGuardHuntUnderwater = builder.comment(
               "If a guard is fighting a mob underwater and the vertical distance between that mob and the guard is larger than this, the guard will instead float up to not take the risk of drowning"
            )
            .defineInRange("Depth value for guards fighting underwater mobs", 5, 0, 100000000);
         this.mobsGuardsProtectTargeted = builder.defineListAllowEmpty(
            "Mobs that guards actively protect when they get targeted",
            ImmutableList.of("minecraft:villager", "guardvillagers:guard", "minecraft:iron_golem"),
            () -> "",
            obj -> true
         );
         this.mobsGuardsProtectHurt = builder.comment(
               "Mobs in this list also won't get hurt by a guard's arrow if the config option to disable guard arrows hurting villagers is enabled."
            )
            .defineListAllowEmpty(
               "Mobs that guards actively protect when they get hurt",
               ImmutableList.of("minecraft:villager", "guardvillagers:guard", "minecraft:iron_golem"),
               () -> "",
               obj -> true
            );
         this.guardCrossbowAttackRadius = builder.defineInRange("Guard crossbow attack radius", 8.0, 0.0, 1.0E8);
         this.structuresThatSpawnGuards = builder.comment("Guards are placed in the middle, thus more advanced placement should be done via datapacks")
            .defineListAllowEmpty("Structure pieces that spawn guards", ImmutableList.of("minecraft:village/common/iron_golem"), () -> "", obj -> true);
         this.guardSpawnInVillage = builder.defineInRange("How many guards should spawn in a village?", 6, 0, 100000000);
         this.convertGuardOnDeath = builder.define("Allow guards to convert to zombie villagers upon being killed by zombies?", true);
         this.multiFollow = builder.translation("guardvillagers.config.multifollow")
            .define("Allow the player to right click on bells to mass order guards to follow them?", true);
         this.chanceToDropEquipment = builder.defineInRange("Chance to drop equipment", 100.0, -999.9000244140625, 999.0);
         this.GuardsRunFromPolarBears = builder.comment("This makes Guards run from polar bears, as anyone with common sense would.")
            .translation("guardvillagers.config.IllagersRunFromPolarBears")
            .define("Have Guards have some common sense?", false);
         this.GuardsOpenDoors = builder.comment("This lets Guards open doors.")
            .translation("guardvillagers.config.GuardsOpenDoors")
            .define("Have Guards open doors?", true);
         this.GuardRaiseShield = builder.comment(
               "This will make guards raise their shields all the time, on default they will only raise their shields under certain conditions"
            )
            .translation("guardvillagers.config.GuardRaiseShield")
            .define("Have Guards raise their shield all the time?", false);
         this.chanceToBreakEquipment = builder.defineInRange("Chance for guards to lose durability", 1.0, -999.9000244140625, 999.0);
         this.guardTeleport = builder.define("Allow guards to teleport if following the player", true);
         this.GuardFormation = builder.comment("This makes guards form a phalanx")
            .translation("guardvillagers.config.GuardFormation")
            .define("Have guards form a phalanx?", true);
         this.friendlyFireCheckValue = builder.comment(
               "Angle is determined by taking the arccos of the inputted value, for example -1 is a straight 180 degree angle thus if that value is inputted guards will only check straight ahead to see if any friendly mobs are in the way."
            )
            .defineInRange("Angle of how ranged guards determine if a friendly mob is infront of them before firing", -0.9, -1000000.0, 1000000.0);
         this.FriendlyFire = builder.translation("guardvillagers.config.FriendlyFire")
            .define("Have guards attempt to avoid firing into other friendlies?", true);
         this.GuardVillagerHelpRange = builder.translation("guardvillagers.config.range")
            .comment(
               "This is the range in which the guards will be aggroed to mobs that are attacking villagers. Higher values are more resource intensive, and setting this to zero will disable the goal."
            )
            .defineInRange("Range", 50.0, -500.0, 500.0);
         this.amountOfHealthRegenerated = builder.translation("guardvillagers.config.amountofHealthRegenerated")
            .comment("How much health a guard regenerates.")
            .defineInRange("Guard health regeneration amount", 1.0, -500.0, 500.0);
         this.guardArrowsHurtVillagers = builder.translation("guardvillagers.config.guardArrows")
            .define(
               "Allow guard arrows to damage villagers, iron golems, or other guards? The i-frames will still be shown for them but they won't lose any health if this is set to false",
               true
            );
         this.giveGuardStuffHOTV = builder.translation("guardvillagers.config.hotvArmor")
            .define("Allow players to give guards stuff only if they have the hero of the village effect?", false);
         this.setGuardPatrolHotv = builder.translation("guardvillagers.config.hotvPatrolPoint")
            .define("Allow players to set guard patrol points only if they have hero of the village", false);
         this.reputationRequirement = builder.defineInRange(
            "Minimum reputation requirement for guards to give you access to their inventories", 15, -2147483648, 2147483647
         );
         this.followHero = builder.define("Have guards only follow the player if they have hero of the village?", true);
         this.reputationRequirementToBeAttacked = builder.defineInRange(
            "How low of a reputation of a player should have to be instantly aggroed upon by guards and golems?", -100, -9999, 9999
         );
         this.guardPatrolVillageAi = builder.define(
            "Allow guards to naturally patrol villages? This feature can cause lag if a lot of guards are spawned", false
         );
         this.guardPatrolAroundVillageWorkstations = builder.define("Allow guards to patrol around villager workstations like golems?", true);
         builder.pop();
      }
   }

   public static class StartUpConfig {
      public final DoubleValue healthModifier;
      public final DoubleValue speedModifier;
      public final DoubleValue followRangeModifier;

      public StartUpConfig(Builder builder) {
         this.healthModifier = builder.defineInRange("Guard health", 20.0, -500.0, 900.0);
         this.speedModifier = builder.defineInRange("Guard speed", 0.5, -500.0, 900.0);
         this.followRangeModifier = builder.defineInRange("Guard follow range", 20.0, 0.0, 900.0);
      }
   }
}
