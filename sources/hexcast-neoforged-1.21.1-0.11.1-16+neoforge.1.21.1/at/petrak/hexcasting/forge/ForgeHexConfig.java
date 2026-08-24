package at.petrak.hexcasting.forge;

import at.petrak.hexcasting.api.mod.HexConfig;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.LongValue;

public class ForgeHexConfig implements HexConfig.CommonConfigAccess {
   private static LongValue dustMediaAmount;
   private static LongValue shardMediaAmount;
   private static LongValue chargedCrystalMediaAmount;
   private static DoubleValue mediaToHealthRate;
   private static IntValue cypherCooldown;
   private static IntValue trinketCooldown;
   private static IntValue artifactCooldown;

   public ForgeHexConfig(Builder builder) {
      builder.push("Media Amounts");
      dustMediaAmount = builder.comment("How much media a single Amethyst Dust item is worth").defineInRange("dustMediaAmount", 10000L, 0L, 2147483647L);
      shardMediaAmount = builder.comment("How much media a single Amethyst Shard item is worth").defineInRange("shardMediaAmount", 50000L, 0L, 2147483647L);
      chargedCrystalMediaAmount = builder.comment("How much media a single Charged Amethyst Crystal item is worth")
         .defineInRange("chargedCrystalMediaAmount", 100000L, 0L, 2147483647L);
      mediaToHealthRate = builder.comment("How many points of media a half-heart is worth when casting from HP")
         .defineInRange("mediaToHealthRate", 10000.0, 0.0, 1.0 / 0.0);
      builder.pop();
      builder.push("Cooldowns");
      cypherCooldown = builder.comment("Cooldown in ticks of a cypher").defineInRange("cypherCooldown", 8, 0, 2147483647);
      trinketCooldown = builder.comment("Cooldown in ticks of a trinket").defineInRange("trinketCooldown", 5, 0, 2147483647);
      artifactCooldown = builder.comment("Cooldown in ticks of a artifact").defineInRange("artifactCooldown", 3, 0, 2147483647);
      builder.pop();
   }

   @Override
   public long dustMediaAmount() {
      return (Long)dustMediaAmount.get();
   }

   @Override
   public long shardMediaAmount() {
      return (Long)shardMediaAmount.get();
   }

   @Override
   public long chargedCrystalMediaAmount() {
      return (Long)chargedCrystalMediaAmount.get();
   }

   @Override
   public double mediaToHealthRate() {
      return (Double)mediaToHealthRate.get();
   }

   @Override
   public int cypherCooldown() {
      return (Integer)cypherCooldown.get();
   }

   @Override
   public int trinketCooldown() {
      return (Integer)trinketCooldown.get();
   }

   @Override
   public int artifactCooldown() {
      return (Integer)artifactCooldown.get();
   }

   public static class Client implements HexConfig.ClientConfigAccess {
      private static BooleanValue ctrlTogglesOffStrokeOrder;
      private static BooleanValue invertSpellbookScrollDirection;
      private static BooleanValue invertAbacusScrollDirection;
      private static DoubleValue gridSnapThreshold;

      public Client(Builder builder) {
         ctrlTogglesOffStrokeOrder = builder.comment("Whether the ctrl key will instead turn *off* the color gradient on patterns")
            .define("ctrlTogglesOffStrokeOrder", false);
         invertSpellbookScrollDirection = builder.comment(
               "Whether scrolling up (as opposed to down) will increase the page index of the spellbook, and vice versa"
            )
            .define("invertSpellbookScrollDirection", false);
         invertAbacusScrollDirection = builder.comment("Whether scrolling up (as opposed to down) will increase the value of the abacus, and vice versa")
            .define("invertAbacusScrollDirection", false);
         gridSnapThreshold = builder.comment(
               "When using a staff, the distance from one dot you have to go to snap to the next dot, where 0.5 means 50% of the way."
            )
            .defineInRange("gridSnapThreshold", 0.5, 0.5, 1.0);
      }

      @Override
      public boolean invertSpellbookScrollDirection() {
         return (Boolean)invertSpellbookScrollDirection.get();
      }

      @Override
      public boolean invertAbacusScrollDirection() {
         return (Boolean)invertAbacusScrollDirection.get();
      }

      @Override
      public boolean ctrlTogglesOffStrokeOrder() {
         return (Boolean)ctrlTogglesOffStrokeOrder.get();
      }

      @Override
      public double gridSnapThreshold() {
         return (Double)gridSnapThreshold.get();
      }
   }

   public static class Server implements HexConfig.ServerConfigAccess {
      private static IntValue opBreakHarvestLevel;
      private static IntValue maxOpCount;
      private static IntValue maxSpellCircleLength;
      private static ConfigValue<List<? extends String>> actionDenyList;
      private static ConfigValue<List<? extends String>> circleActionDenyList;
      private static BooleanValue villagersOffendedByMindMurder;
      private static ConfigValue<List<? extends String>> tpDimDenyList;
      private static ConfigValue<List<? extends String>> fewScrollTables;
      private static ConfigValue<List<? extends String>> someScrollTables;
      private static ConfigValue<List<? extends String>> manyScrollTables;

      public Server(Builder builder) {
         builder.push("Spells");
         maxOpCount = builder.comment("The maximum number of actions that can be executed in one tick, to avoid hanging the server.")
            .defineInRange("maxOpCount", 1000000, 0, 2147483647);
         opBreakHarvestLevel = builder.comment(
               new String[]{"The harvest level of the Break Block spell.", "0 = wood, 1 = stone, 2 = iron, 3 = diamond, 4 = netherite."}
            )
            .defineInRange("opBreakHarvestLevel", 3, 0, 4);
         builder.pop();
         builder.push("Spell Circles");
         maxSpellCircleLength = builder.comment("The maximum number of slates in a spell circle").defineInRange("maxSpellCircleLength", 1024, 4, 2147483647);
         circleActionDenyList = builder.comment(
               "Resource locations of disallowed actions within circles. Trying to cast one of these in a circle will result in a mishap. For example: hexcasting:get_caster will prevent Mind's Reflection."
            )
            .defineList("circleActionDenyList", List.of(), ForgeHexConfig.Server::isValidReslocArg);
         builder.pop();
         actionDenyList = builder.comment("Resource locations of disallowed actions. Trying to cast one of these will result in a mishap.")
            .defineList("actionDenyList", List.of(), ForgeHexConfig.Server::isValidReslocArg);
         villagersOffendedByMindMurder = builder.comment("Should villagers take offense when you flay the mind of their fellow villagers?")
            .define("villagersOffendedByMindMurder", true);
         tpDimDenyList = builder.comment("Resource locations of dimensions you can't Blink or Greater Teleport in.")
            .defineList("tpDimDenyList", DEFAULT_DIM_TP_DENYLIST, ForgeHexConfig.Server::isValidReslocArg);
      }

      @Override
      public int opBreakHarvestLevelBecauseForgeThoughtItWasAGoodIdeaToImplementHarvestTiersUsingAnHonestToGodTopoSort() {
         return (Integer)opBreakHarvestLevel.get();
      }

      @Override
      public int maxOpCount() {
         return (Integer)maxOpCount.get();
      }

      @Override
      public int maxSpellCircleLength() {
         return (Integer)maxSpellCircleLength.get();
      }

      @Override
      public boolean isActionAllowed(ResourceLocation actionID) {
         return HexConfig.noneMatch((List<? extends String>)actionDenyList.get(), actionID);
      }

      @Override
      public boolean isActionAllowedInCircles(ResourceLocation actionID) {
         return HexConfig.noneMatch((List<? extends String>)circleActionDenyList.get(), actionID);
      }

      @Override
      public boolean doVillagersTakeOffenseAtMindMurder() {
         return (Boolean)villagersOffendedByMindMurder.get();
      }

      @Override
      public boolean canTeleportInThisDimension(ResourceKey<Level> dimension) {
         return HexConfig.noneMatch((List<? extends String>)tpDimDenyList.get(), dimension.location());
      }

      private static boolean isValidReslocArg(Object o) {
         return o instanceof String s && ResourceLocation.tryParse(s) != null;
      }
   }
}
