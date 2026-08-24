package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

public class ItemEnchantmentsWrapper {
   public static final TypeInfo MAP_TYPE = TypeInfo.RAW_MAP
      .withParams(new TypeInfo[]{TypeInfo.of(Holder.class).withParams(new TypeInfo[]{TypeInfo.of(Enchantment.class)}), TypeInfo.INT});

   public static ItemEnchantments wrap(Context cx, Object from) {
      if (from instanceof ItemEnchantments e) {
         return e;
      } else if (!(from instanceof Map) && !(from instanceof NativeJavaMap)) {
         return ItemEnchantments.EMPTY;
      } else {
         Map<Holder<Enchantment>, Integer> map = (Map<Holder<Enchantment>, Integer>)cx.jsToJava(from, MAP_TYPE);
         Mutable mutable = new Mutable(ItemEnchantments.EMPTY);

         for (Entry<Holder<Enchantment>, Integer> entry : map.entrySet()) {
            mutable.upgrade(entry.getKey(), entry.getValue());
         }

         return mutable.toImmutable();
      }
   }
}
