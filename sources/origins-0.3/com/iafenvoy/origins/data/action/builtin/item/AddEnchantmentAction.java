package com.iafenvoy.origins.data.action.builtin.item;

import com.iafenvoy.origins.data.action.ItemAction;
import com.iafenvoy.origins.util.codec.CollectionCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AddEnchantmentAction(Object2IntMap<Holder<Enchantment>> enchantment, boolean override) implements ItemAction {
   public static final MapCodec<AddEnchantmentAction> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            CollectionCodecs.toIntMap(Enchantment.CODEC).fieldOf("enchantment").forGetter(AddEnchantmentAction::enchantment),
            Codec.BOOL.optionalFieldOf("override", false).forGetter(AddEnchantmentAction::override)
         )
         .apply(i, AddEnchantmentAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends ItemAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Level level, @NotNull Entity source, @NotNull SlotAccess access) {
      ItemStack stack = access.get();
      Mutable mutable = new Mutable(stack.getTagEnchantments());
      ObjectIterator var6 = this.enchantment.object2IntEntrySet().iterator();

      while (var6.hasNext()) {
         Entry<Holder<Enchantment>> entry = (Entry<Holder<Enchantment>>)var6.next();
         Holder<Enchantment> enchantment = (Holder<Enchantment>)entry.getKey();
         int lvl = entry.getIntValue();
         if (mutable.getLevel(enchantment) < lvl || this.override) {
            mutable.set(enchantment, lvl);
         }
      }

      stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
   }
}
