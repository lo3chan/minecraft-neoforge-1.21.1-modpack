package at.petrak.hexcasting.api.mod;

import at.petrak.hexcasting.api.HexAPI;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class HexConfig {
   private static HexConfig.CommonConfigAccess common = null;
   private static HexConfig.ClientConfigAccess client = null;
   private static HexConfig.ServerConfigAccess server = null;

   public static boolean anyMatch(List<? extends String> keys, ResourceLocation key) {
      for (String s : keys) {
         if (ResourceLocation.tryParse(s) != null) {
            ResourceLocation rl = ResourceLocation.parse(s);
            if (rl.equals(key)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean noneMatch(List<? extends String> keys, ResourceLocation key) {
      return !anyMatch(keys, key);
   }

   public static boolean anyMatchResLoc(List<? extends ResourceLocation> keys, ResourceLocation key) {
      return keys.stream().anyMatch(key::equals);
   }

   public static HexConfig.CommonConfigAccess common() {
      return common;
   }

   public static HexConfig.ClientConfigAccess client() {
      return client;
   }

   public static HexConfig.ServerConfigAccess server() {
      return server;
   }

   public static void setCommon(HexConfig.CommonConfigAccess access) {
      if (common != null) {
         HexAPI.LOGGER.warn("CommonConfigAccess was replaced! Old {} New {}", common.getClass().getName(), access.getClass().getName());
      }

      common = access;
   }

   public static void setClient(HexConfig.ClientConfigAccess access) {
      if (client != null) {
         HexAPI.LOGGER.warn("ClientConfigAccess was replaced! Old {} New {}", client.getClass().getName(), access.getClass().getName());
      }

      client = access;
   }

   public static void setServer(HexConfig.ServerConfigAccess access) {
      if (server != null) {
         HexAPI.LOGGER.warn("ServerConfigAccess was replaced! Old {} New {}", server.getClass().getName(), access.getClass().getName());
      }

      server = access;
   }

   public interface ClientConfigAccess {
      boolean DEFAULT_CTRL_TOGGLES_OFF_STROKE_ORDER = false;
      boolean DEFAULT_INVERT_SPELLBOOK_SCROLL = false;
      boolean DEFAULT_INVERT_ABACUS_SCROLL = false;
      double DEFAULT_GRID_SNAP_THRESHOLD = 0.5;

      boolean ctrlTogglesOffStrokeOrder();

      boolean invertSpellbookScrollDirection();

      boolean invertAbacusScrollDirection();

      double gridSnapThreshold();
   }

   public interface CommonConfigAccess {
      long DEFAULT_DUST_MEDIA_AMOUNT = 10000L;
      long DEFAULT_SHARD_MEDIA_AMOUNT = 50000L;
      long DEFAULT_CHARGED_MEDIA_AMOUNT = 100000L;
      double DEFAULT_MEDIA_TO_HEALTH_RATE = 10000.0;
      int DEFAULT_CYPHER_COOLDOWN = 8;
      int DEFAULT_TRINKET_COOLDOWN = 5;
      int DEFAULT_ARTIFACT_COOLDOWN = 3;

      long dustMediaAmount();

      long shardMediaAmount();

      long chargedCrystalMediaAmount();

      double mediaToHealthRate();

      int cypherCooldown();

      int trinketCooldown();

      int artifactCooldown();
   }

   public interface ServerConfigAccess {
      int DEFAULT_MAX_OP_COUNT = 1000000;
      int DEFAULT_MAX_SPELL_CIRCLE_LENGTH = 1024;
      int DEFAULT_OP_BREAK_HARVEST_LEVEL = 3;
      boolean DEFAULT_VILLAGERS_DISLIKE_MIND_MURDER = true;
      List<String> DEFAULT_DIM_TP_DENYLIST = List.of("twilightforest:twilight_forest");

      int opBreakHarvestLevelBecauseForgeThoughtItWasAGoodIdeaToImplementHarvestTiersUsingAnHonestToGodTopoSort();

      int maxOpCount();

      int maxSpellCircleLength();

      boolean isActionAllowed(ResourceLocation var1);

      boolean isActionAllowedInCircles(ResourceLocation var1);

      boolean doVillagersTakeOffenseAtMindMurder();

      boolean canTeleportInThisDimension(ResourceKey<Level> var1);

      default Tier opBreakHarvestLevel() {
         return switch (this.opBreakHarvestLevelBecauseForgeThoughtItWasAGoodIdeaToImplementHarvestTiersUsingAnHonestToGodTopoSort()) {
            case 0 -> Tiers.WOOD;
            case 1 -> Tiers.STONE;
            case 2 -> Tiers.IRON;
            case 3 -> Tiers.DIAMOND;
            case 4 -> Tiers.NETHERITE;
            default -> throw new RuntimeException("please only return a value in 0<=x<=4");
         };
      }
   }
}
