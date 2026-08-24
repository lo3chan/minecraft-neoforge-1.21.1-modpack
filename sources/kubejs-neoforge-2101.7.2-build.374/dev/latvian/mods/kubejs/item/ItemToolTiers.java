package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.Lazy;
import java.util.Locale;
import java.util.Map;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class ItemToolTiers {
   public static final Lazy<Map<String, Tier>> ALL = Lazy.map(map -> {
      for (Tiers tier : Tiers.values()) {
         map.put(tier.toString().toLowerCase(Locale.ROOT), tier);
      }

      ItemEvents.TOOL_TIER_REGISTRY.post(ScriptType.STARTUP, new ItemToolTierRegistryKubeEvent(map));
   });

   public static Tier wrap(Object o) {
      if (o instanceof Tier tier) {
         return tier;
      } else {
         String asString = String.valueOf(o);
         Tier toolTier = ALL.get().get(asString);
         return toolTier != null ? toolTier : ALL.get().getOrDefault(ID.kjsString(asString), Tiers.IRON);
      }
   }
}
