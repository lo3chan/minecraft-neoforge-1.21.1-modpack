package com.anthonyhilyard.legendarytooltips.mixin;

import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemStack.class})
public class ItemStackMixin {
   @Redirect(
      method = {"addModifierTooltip(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"},
      require = 0,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getAttributeBaseValue(Lnet/minecraft/core/Holder;)D"
      )
   )
   public double getAttributeBaseValueProxy(
      Player player,
      Holder<Attribute> holder,
      Consumer<Component> consumer,
      @Nullable Player player2,
      Holder<Attribute> holder2,
      AttributeModifier attributeModifier
   ) {
      if (LegendaryTooltipsConfig.getInstance().fixMC271840.get() && attributeModifier.is(Item.BASE_ATTACK_DAMAGE_ID)) {
         ItemStack instance = (ItemStack)this;
         float f = (float)player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
         ItemEnchantments itemEnchantments = (ItemEnchantments)instance.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

         for (Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
            Enchantment enchantment = (Enchantment)((Holder)entry.getKey()).value();
            List<ConditionalEffect<EnchantmentValueEffect>> effects = enchantment.getEffects(EnchantmentEffectComponents.DAMAGE);
            if (!effects.isEmpty()) {
               for (ConditionalEffect<EnchantmentValueEffect> effect : effects) {
                  if (effect.requirements().isEmpty()) {
                     f = ((EnchantmentValueEffect)effect.effect()).process(entry.getIntValue(), player.getRandom(), f);
                  }
               }
            }
         }

         return f;
      } else {
         return player.getAttributeBaseValue(holder);
      }
   }
}
