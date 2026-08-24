package net.bettercombat.mixin.client;

import java.util.function.Consumer;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.WeaponAttributeTooltip;
import net.bettercombat.logic.EntityAttributeHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemStack.class})
public class ItemStackTooltipMixin {
   @Inject(
      method = {"addModifierTooltip(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void appendAttributeModifierTooltip_BetterCombat_Range(
      Consumer<Component> textConsumer, Player player, Holder<Attribute> attribute, AttributeModifier modifier, CallbackInfo ci
   ) {
      if (BetterCombatClientMod.config.isTooltipAttackRangeReformat && attribute.value() == Attributes.ENTITY_INTERACTION_RANGE.value() && player != null) {
         ItemStack itemStack = (ItemStack)this;
         if (WeaponRegistry.getAttributes(itemStack) != null && EntityAttributeHelper.rangeModifierCount(itemStack) == 1) {
            ci.cancel();
            double value = modifier.amount() + player.getAttributeBaseValue(Attributes.ENTITY_INTERACTION_RANGE);
            textConsumer.accept(WeaponAttributeTooltip.attackRangeLine(value));
         }
      }
   }
}
