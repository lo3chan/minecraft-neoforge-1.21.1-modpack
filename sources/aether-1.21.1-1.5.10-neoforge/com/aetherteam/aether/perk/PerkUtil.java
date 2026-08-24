package com.aetherteam.aether.perk;

import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.User.Group;
import com.aetherteam.nitrogen.api.users.User.Tier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Triple;

public final class PerkUtil {
   public static Predicate<User> hasAnyMoaSkins() {
      return user -> hasHumanMoaSkins().test(user)
         || hasAscentanMoaSkins().test(user)
         || hasValkyrieMoaSkins().test(user)
         || hasLifetimeAscentanMoaSkins().test(user)
         || hasLifetimeValkyrieMoaSkins().test(user);
   }

   public static Predicate<User> hasLifetimeValkyrieMoaSkins() {
      return user -> hasAllSkins().test(user)
         || user.getCurrentTierLevel() >= Tier.VALKYRIE.getLevel()
         || user.getCurrentTier() == null && user.getHighestPastTierLevel() >= Tier.VALKYRIE.getLevel();
   }

   public static Predicate<User> hasLifetimeAscentanMoaSkins() {
      return user -> hasAllSkins().test(user)
         || hasBaseSkins().test(user)
         || user.getCurrentTierLevel() >= Tier.ASCENTAN.getLevel()
         || user.getCurrentTier() == null && user.getHighestPastTierLevel() >= Tier.ASCENTAN.getLevel();
   }

   public static Predicate<User> hasValkyrieMoaSkins() {
      return user -> hasAllSkins().test(user) || user.getCurrentTierLevel() >= Tier.VALKYRIE.getLevel();
   }

   public static Predicate<User> hasAscentanMoaSkins() {
      return user -> hasAllSkins().test(user) || hasBaseSkins().test(user) || user.getCurrentTierLevel() >= Tier.ASCENTAN.getLevel();
   }

   public static Predicate<User> hasHumanMoaSkins() {
      return user -> hasAllSkins().test(user) || hasBaseSkins().test(user) || user.getCurrentTierLevel() >= Tier.HUMAN.getLevel();
   }

   public static Predicate<User> hasBaseSkins() {
      return user -> isContributor().test(user) || user.getHighestGroup() == Group.TRANSLATOR || user.getHighestGroup() == Group.CELEBRITY;
   }

   public static Predicate<User> hasAllSkins() {
      return user -> isDeveloperOrStaff().test(user);
   }

   public static Predicate<User> hasDeveloperGlow() {
      return user -> isDeveloper().test(user);
   }

   public static Predicate<User> hasHalo() {
      return user -> isDeveloperOrStaff().test(user) || isContributor().test(user);
   }

   public static Predicate<User> isDeveloperOrStaff() {
      return user -> isDeveloper().test(user) || user.getHighestGroup() == Group.STAFF;
   }

   public static Predicate<User> isDeveloper() {
      return user -> user.getHighestGroup() == Group.AETHER_TEAM || user.getHighestGroup() == Group.MODDING_LEGACY;
   }

   public static Predicate<User> isContributor() {
      return user -> user.getHighestGroup() == Group.CONTRIBUTOR || user.getHighestGroup() == Group.LEGACY_CONTRIBUTOR;
   }

   @Nullable
   public static Triple<Float, Float, Float> getPerkColor(@Nullable String hex) {
      if (hex != null && !hex.isEmpty()) {
         try {
            int decimal = Integer.parseInt(hex, 16);
            int r = (decimal & 0xFF0000) >> 16;
            int g = (decimal & 0xFF00) >> 8;
            int b = decimal & 0xFF;
            return Triple.of(r / 255.0F, g / 255.0F, b / 255.0F);
         } catch (NumberFormatException var5) {
            return null;
         }
      } else {
         return null;
      }
   }
}
