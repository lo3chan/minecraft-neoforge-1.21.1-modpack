package net.bettercombat.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.config.ServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

public class TargetHelper {
   private static Map<TagKey<EntityType<?>>, TargetHelper.Relation> RELATION_TAG_CACHE = null;
   private static final Map<String, TargetHelper.TeamMatcher> TEAM_MATCHERS = new LinkedHashMap<>();

   public static TargetHelper.Relation getRelation(Player attacker, Entity target) {
      ServerConfig config = BetterCombatMod.config;
      if (attacker == target) {
         return config.player_relation_to_self_and_pets;
      } else {
         if (target instanceof OwnableEntity tameable) {
            LivingEntity owner = tameable.getOwner();
            if (owner != null) {
               return getRelation(attacker, owner);
            }
         }

         if (target instanceof HangingEntity) {
            return TargetHelper.Relation.NEUTRAL;
         } else {
            for (TargetHelper.TeamMatcher matcher : TEAM_MATCHERS.values()) {
               TargetHelper.TeamRelation relation = matcher.getRelation(attacker, target);
               if (relation != null) {
                  return relation.areTeammates()
                     ? (relation.friendlyFireAllowed() ? config.player_relation_to_teammates : TargetHelper.Relation.FRIENDLY)
                     : TargetHelper.Relation.HOSTILE;
               }
            }

            Holder<EntityType<?>> targetTypeEntry = BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(target.getType());
            ResourceLocation id = ((ResourceKey)targetTypeEntry.unwrapKey().get()).location();
            TargetHelper.Relation mappedRelation = config.player_relations.get(id.toString());
            if (mappedRelation != null) {
               return mappedRelation;
            } else {
               for (Entry<TagKey<EntityType<?>>, TargetHelper.Relation> entry : getRelationTagsCache().entrySet()) {
                  if (targetTypeEntry.is(entry.getKey())) {
                     return entry.getValue();
                  }
               }

               if (target instanceof AgeableMob) {
                  return TargetHelper.Relation.coalesce(config.player_relation_to_passives, TargetHelper.Relation.HOSTILE);
               } else {
                  return target instanceof Monster
                     ? TargetHelper.Relation.coalesce(config.player_relation_to_hostiles, TargetHelper.Relation.HOSTILE)
                     : TargetHelper.Relation.coalesce(config.player_relation_to_other, TargetHelper.Relation.HOSTILE);
               }
            }
         }
      }
   }

   private static Map<TagKey<EntityType<?>>, TargetHelper.Relation> getRelationTagsCache() {
      if (RELATION_TAG_CACHE == null) {
         RELATION_TAG_CACHE = new HashMap<>();

         for (Entry<String, TargetHelper.Relation> entrySet : BetterCombatMod.config.player_relation_tags.entrySet()) {
            String tagString = entrySet.getKey();
            TargetHelper.Relation relation = entrySet.getValue();
            TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tagString));
            RELATION_TAG_CACHE.put(tag, relation);
         }
      }

      return RELATION_TAG_CACHE;
   }

   public static void registerTeamMatcher(String name, TargetHelper.TeamMatcher matcher) {
      TEAM_MATCHERS.put(name, matcher);
   }

   public static boolean isAttackableMount(Entity entity) {
      return !(entity instanceof Monster) && !isEntityHostileVehicle(entity.getName().getString()) ? BetterCombatMod.config.allow_attacking_mount : true;
   }

   public static boolean isEntityHostileVehicle(String entityName) {
      ServerConfig config = BetterCombatMod.config;
      return config.hostile_player_vehicles != null
         && config.hostile_player_vehicles.length > 0
         && Arrays.asList(config.hostile_player_vehicles).contains(entityName);
   }

   public static boolean isHitAllowed(boolean isDirect, TargetHelper.Relation relation) {
      return isDirect ? relation != TargetHelper.Relation.FRIENDLY : relation == TargetHelper.Relation.HOSTILE;
   }

   static {
      registerTeamMatcher("vanilla", (entity1, entity2) -> {
         PlayerTeam team1 = entity1.getTeam();
         PlayerTeam team2 = entity2.getTeam();
         if (team1 != null && team2 != null) {
            boolean friendlyFire = team1.isAllowFriendlyFire();
            return new TargetHelper.TeamRelation(entity1.isAlliedTo(entity2), friendlyFire);
         } else {
            return null;
         }
      });
   }

   public static enum Relation {
      FRIENDLY,
      NEUTRAL,
      HOSTILE;

      public static TargetHelper.Relation coalesce(TargetHelper.Relation value, TargetHelper.Relation fallback) {
         return value != null ? value : fallback;
      }
   }

   public interface TeamMatcher {
      @Nullable
      TargetHelper.TeamRelation getRelation(Entity var1, Entity var2);
   }

   public record TeamRelation(boolean areTeammates, boolean friendlyFireAllowed) {
   }
}
