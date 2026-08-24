package com.yungnickyoung.minecraft.bettermineshafts.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigOresNeoForge {
   public final ConfigValue<Boolean> enabled;
   public final ConfigValue<Integer> cobble;
   public final ConfigValue<Integer> coal;
   public final ConfigValue<Integer> iron;
   public final ConfigValue<Integer> redstone;
   public final ConfigValue<Integer> gold;
   public final ConfigValue<Integer> lapis;
   public final ConfigValue<Integer> emerald;
   public final ConfigValue<Integer> diamond;

   public ConfigOresNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# Ore deposit settings.\n##########################################################################################################"
         )
         .push("Ore Deposits");
      this.enabled = BUILDER.worldRestart().define("Enable Ore Deposits", true);
      this.cobble = BUILDER.comment(" Chance of an ore deposit being cobblestone only.\n Default: 50")
         .worldRestart()
         .defineInRange("Cobble Spawn Chance (Empty Deposit)", 50, 0, 100);
      this.coal = BUILDER.comment(" Chance of an ore deposit containing coal.\n Default: 20").worldRestart().defineInRange("Coal Spawn Chance", 20, 0, 100);
      this.iron = BUILDER.comment(" Chance of an ore deposit containing iron.\n Default: 9").worldRestart().defineInRange("Iron Spawn Chance", 9, 0, 100);
      this.redstone = BUILDER.comment(" Chance of an ore deposit containing redstone.\n Default: 7")
         .worldRestart()
         .defineInRange("Redstone Spawn Chance", 7, 0, 100);
      this.gold = BUILDER.comment(" Chance of an ore deposit containing gold.\n Default: 7").worldRestart().defineInRange("Gold Spawn Chance", 7, 0, 100);
      this.lapis = BUILDER.comment(" Chance of an ore deposit containing lapis lazuli.\n Default: 3")
         .worldRestart()
         .defineInRange("Lapis Spawn Chance", 3, 0, 100);
      this.emerald = BUILDER.comment(" Chance of an ore deposit containing emerald.\n Default: 3")
         .worldRestart()
         .defineInRange("Emerald Spawn Chance", 3, 0, 100);
      this.diamond = BUILDER.comment(" Chance of an ore deposit containing diamond.\n Default: 1")
         .worldRestart()
         .defineInRange("Diamond Spawn Chance", 1, 0, 100);
      BUILDER.pop();
   }
}
