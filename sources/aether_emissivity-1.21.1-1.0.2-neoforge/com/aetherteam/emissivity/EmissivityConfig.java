package com.aetherteam.emissivity;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class EmissivityConfig {
   public static final ModConfigSpec CLIENT_SPEC;
   public static final EmissivityConfig.Client CLIENT;

   static {
      Pair<EmissivityConfig.Client, ModConfigSpec> clientSpecPair = new Builder().configure(EmissivityConfig.Client::new);
      CLIENT_SPEC = (ModConfigSpec)clientSpecPair.getRight();
      CLIENT = (EmissivityConfig.Client)clientSpecPair.getLeft();
   }

   public static class Client {
      public final ConfigValue<Boolean> emissive_sentry_boots;
      public final ConfigValue<Boolean> emissive_phoenix_armor;
      public final ConfigValue<Boolean> emissive_shield_of_repulsion;

      public Client(Builder builder) {
         builder.push("Emissives");
         this.emissive_sentry_boots = builder.comment("Enables emissivity for Sentry Boots")
            .translation("config.aether_emissivity.client.emissives.emissive_sentry_boots")
            .define("Sentry Boots emissivity", true);
         this.emissive_phoenix_armor = builder.comment("Enables emissivity for Phoenix Armor")
            .translation("config.aether_emissivity.client.emissives.emissive_phoenix_armor")
            .define("Phoenix Armor emissivity", true);
         this.emissive_shield_of_repulsion = builder.comment("Enables emissivity for the Shield of Repulsion")
            .translation("config.aether_emissivity.client.emissives.emissive_shield_of_repulsion")
            .define("Shield of Repulsion emissivity", true);
         builder.pop();
      }
   }
}
