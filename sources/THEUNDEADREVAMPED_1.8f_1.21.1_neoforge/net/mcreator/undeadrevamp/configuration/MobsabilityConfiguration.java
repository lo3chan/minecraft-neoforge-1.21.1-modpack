package net.mcreator.undeadrevamp.configuration;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class MobsabilityConfiguration {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static final ConfigValue<Double> PREG_BABE = BUILDER.comment("Maximum number of babies a pregnant could spawn")
      .define("Maximum numbers of summonings pregnant can spawn", 10.0);
   public static final ConfigValue<Boolean> ABORTION_NEEDLE = BUILDER.comment("disable the bility entirely").define("Disable spawning of summonings", false);
   public static final ConfigValue<Double> PREG_BIDY = BUILDER.comment(
         "Example: 80 meaning 20 percent of sucker spawing and 80 of bidys. (by setting to 100 no sucker willl spawn)"
      )
      .define("Bidy spawning percentage pool", 80.0);
   public static final ConfigValue<Boolean> HUNT_FEN = BUILDER.comment("Can also be turned off with griefing gamemode")
      .define("Allows hunter to destroy fence or doors", true);
   public static final ConfigValue<Boolean> HUNT_ANI = BUILDER.define("Allows hunter to hunt animals", true);
   public static final ConfigValue<Boolean> HUNT_HEAL = BUILDER.define("Allows hunter to life steal", true);
   public static final ConfigValue<Boolean> HUNT_EAT = BUILDER.comment("Hunters will usaully eat any dropped meat they find")
      .define("Allows hunter to eat dropped food", true);
   public static final ConfigValue<Boolean> HUNT_SOAR = BUILDER.comment("hunter will usaully fly and despawn from sunlight")
      .define("Hunter soar off from sunlight", true);
   public static final ConfigValue<Double> BOMBRAD = BUILDER.comment("bomber explosion radius").define("Bomber explosion in block area", 9.0);
   public static final ConfigValue<Boolean> CLOGFLEE = BUILDER.comment("Clogger will usaully not tolererate player cheesing in a fight")
      .define("Will clogger dig away(despawn) from cowardice enemy?", true);
   public static final ConfigValue<Boolean> SUCK_CH = BUILDER.comment("To disable/enable this machanic")
      .define("allow chances for sucker to spawn with something riding on them", true);
   public static final ConfigValue<Double> SUCK_TEEM = BUILDER.comment("Per sucker, what at what percentage it would spawn with something")
      .define("chances for sucker to have mob spawn riding on them, upon spawning", 15.0);
   public static final ConfigValue<Double> SUC_BIDY = BUILDER.comment(
         "Example: 80 meaning 20 percent of baby zombie spawning and 80 of bidys. (by setting to 100 no baby zombie will spawn)"
      )
      .define("Bidy spawning percentage pool", 80.0);
   public static final ConfigValue<Double> SUCK_MAIN = BUILDER.comment("default at 5").define("How many suckers spawn per Big Sucker?", 5.0);
   public static final ConfigValue<Boolean> STROMY = BUILDER.comment("They usaully spawn blade like mobs to attack target")
      .define("Can the wolf spawn stroms", true);
   public static final ConfigValue<Double> AXE_DMG = BUILDER.comment("Somehow it cant be chnaged via attributes so here it is. (exclusively for this mob)")
      .define("The wolf swinging attack Damage", 4.0);
   public static final ConfigValue<Boolean> INDUCER = BUILDER.comment("This has to do with world ore generation")
      .define("Do Lechery spawn as ore blocks", true);
   public static final ConfigValue<Boolean> DUNZHONG = BUILDER.comment("Can The Dungeon mob and its structures spawn in thiss world?")
      .define("Enable or Disabling dungeons", true);

   static {
      BUILDER.push("pregnantbabies");
      BUILDER.pop();
      BUILDER.push("hunter");
      BUILDER.pop();
      BUILDER.push("bomber");
      BUILDER.pop();
      BUILDER.push("clogger");
      BUILDER.pop();
      BUILDER.push("suckers");
      BUILDER.pop();
      BUILDER.push("Big sucker");
      BUILDER.pop();
      BUILDER.push("The Wolf");
      BUILDER.pop();
      BUILDER.push("The Lechery");
      BUILDER.pop();
      BUILDER.push("The Dungeon");
      BUILDER.pop();
   }
}
