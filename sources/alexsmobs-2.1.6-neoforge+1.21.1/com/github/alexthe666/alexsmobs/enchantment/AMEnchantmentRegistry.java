package com.github.alexthe666.alexsmobs.enchantment;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class AMEnchantmentRegistry {
   public static final Supplier<ResourceKey<Enchantment>> STRADDLE_JUMP = key("straddle_jump");
   public static final Supplier<ResourceKey<Enchantment>> STRADDLE_LAVAWAX = key("lavawax");
   public static final Supplier<ResourceKey<Enchantment>> STRADDLE_SERPENTFRIEND = key("serpentfriend");
   public static final Supplier<ResourceKey<Enchantment>> STRADDLE_BOARDRETURN = key("board_return");

   private static Supplier<ResourceKey<Enchantment>> key(String name) {
      ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, AMCompat.rl("alexsmobs", name));
      return () -> key;
   }
}
