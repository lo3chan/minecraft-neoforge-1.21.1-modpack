package com.yungnickyoung.minecraft.bettermineshafts.config;

import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigSpawnRatesNeoForge {
   public final ConfigValue<Double> lanternSpawnRate;
   public final ConfigValue<Double> torchSpawnRate;
   public final ConfigValue<Double> workstationSpawnRate;
   public final ConfigValue<Double> workstationDungeonSpawnRate;
   public final ConfigValue<Double> smallShaftSpawnRate;
   public final ConfigValue<Double> cobwebSpawnRate;
   public final ConfigValue<Double> smallShaftChestMinecartSpawnRate;
   public final ConfigValue<Double> smallShaftTntMinecartSpawnRate;
   public final ConfigValue<Double> mainShaftChestMinecartSpawnRate;
   public final ConfigValue<Double> mainShaftTntMinecartSpawnRate;
   public final ConfigValue<Integer> zombieVillagerRoomSpawnRate;
   public final ConfigValue<Integer> smallShaftPieceChainLength;

   public ConfigSpawnRatesNeoForge(Builder BUILDER) {
      BUILDER.comment(
            "##########################################################################################################\n# Spawn rates for various mineshaft parts and decorations.\n##########################################################################################################"
         )
         .push("Spawn Rates & More");
      this.lanternSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for lanterns in the main shaft.\n Default: .0067")
         .defineInRange("Lantern Spawn Rate", 0.0067, 0.0, 1.0);
      this.torchSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for torches in small shafts.\n Default: .02")
         .defineInRange("Torch Spawn Rate", 0.02, 0.0, 1.0);
      this.workstationSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for workstation side rooms along the main shaft.\n Default: .025")
         .defineInRange("Workstation Spawn Rate", 0.025, 0.0, 1.0);
      this.workstationDungeonSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for workstation cellars below workstations along the main shaft.\n Default: .25")
         .defineInRange("Workstation Cellar Spawn Rate", 0.25, 0.0, 1.0);
      this.smallShaftSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for smaller tunnels that generate along the main shaft.\n Default: .07")
         .defineInRange("Small Shaft Spawn Rate", 0.07, 0.0, 1.0);
      this.cobwebSpawnRate = BUILDER.worldRestart().comment(" The spawn rate for cobwebs.\n Default: .15").defineInRange("Cobweb Spawn Rate", 0.15, 0.0, 1.0);
      this.smallShaftChestMinecartSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for minecarts holding chests in small shafts.\n Default: .00125")
         .defineInRange("Small Shaft Chest Minecart Spawn Rate", 0.00125, 0.0, 1.0);
      this.smallShaftTntMinecartSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for minecarts holding TNT in small shafts.\n Default: .0025")
         .defineInRange("Small Shaft TNT Minecart Spawn Rate", 0.0025, 0.0, 1.0);
      this.mainShaftChestMinecartSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for minecarts holding chests in the main shaft.\n Default: .01")
         .defineInRange("Main Shaft Chest Minecart Spawn Rate", 0.01, 0.0, 1.0);
      this.mainShaftTntMinecartSpawnRate = BUILDER.worldRestart()
         .comment(" The spawn rate for minecarts holding TNT in the main shaft.\n Default: .0025")
         .defineInRange("Main Shaft TNT Minecart Spawn Rate", 0.0025, 0.0, 1.0);
      this.zombieVillagerRoomSpawnRate = BUILDER.worldRestart()
         .comment(" Percent chance of an Abandoned Miners' Outpost to spawn at the end of a small mineshaft tunnel.\n Default: 2")
         .defineInRange("Abandoned Miners' Outpost Spawn Chance", 2, 0, 100);
      this.smallShaftPieceChainLength = BUILDER.worldRestart()
         .comment(
            "The number of \"pieces\" (e.g. straight, turn, ladder, intersection, etc.) in a single small shaft.\nThis determines the overall length of small shafts.\nDefault: 9"
               .indent(1)
         )
         .defineInRange("Small Shaft Piece Chain Length", 9, 0, 1000);
      BUILDER.pop();
   }
}
