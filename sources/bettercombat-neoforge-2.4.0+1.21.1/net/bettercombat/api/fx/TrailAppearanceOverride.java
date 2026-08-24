package net.bettercombat.api.fx;

import java.util.ArrayList;
import java.util.List;
import net.bettercombat.BetterCombatMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TrailAppearanceOverride {
   List<TrailAppearanceOverride> REGISTERED = new ArrayList<>();

   @Nullable
   TrailAppearance override(Player var1, ItemStack var2, TrailAppearance var3);

   static void register(TrailAppearanceOverride override) {
      REGISTERED.add(override);
   }

   @Nullable
   static TrailAppearance apply(Player attacker, ItemStack stack, @Nullable TrailAppearance resolved) {
      if (resolved == null) {
         return null;
      } else {
         for (TrailAppearanceOverride override : REGISTERED) {
            try {
               TrailAppearance overridden = override.override(attacker, stack, resolved);
               if (overridden != null) {
                  resolved = overridden;
               }
            } catch (Exception var6) {
               BetterCombatMod.LOGGER.error("Failed to apply trail appearance override", var6);
            }
         }

         return resolved;
      }
   }
}
