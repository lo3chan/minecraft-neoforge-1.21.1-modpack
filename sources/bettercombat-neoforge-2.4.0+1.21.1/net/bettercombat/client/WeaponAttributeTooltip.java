package net.bettercombat.client;

import java.util.List;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.EntityAttributeHelper;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class WeaponAttributeTooltip {
   public static void modifyTooltip(ItemStack itemStack, List<Component> lines) {
      WeaponAttributes attributes = WeaponRegistry.getAttributes(itemStack);
      if (attributes != null) {
         int lastAttributeLine = 0;
         int firstHandLine = 0;
         Integer lastGreenAttributeIndex = null;
         String attributePrefix = "attribute.modifier";
         String attributeEqualsPrefix = "attribute.modifier.equals.0";
         String handPrefix = "item.modifiers";

         for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            if (line.getContents() instanceof TranslatableContents translatableText) {
               String key = translatableText.getKey();
               if (key.startsWith(attributePrefix)) {
                  lastAttributeLine = i;
               }

               if (firstHandLine == 0 && key.startsWith(handPrefix)) {
                  firstHandLine = i;
               }
            } else {
               for (Component part : line.getSiblings()) {
                  if (part.getContents() instanceof TranslatableContents translatableText) {
                     if (translatableText.getKey().contains(attributeEqualsPrefix)) {
                        lastGreenAttributeIndex = i;
                     }

                     if (translatableText.getKey().startsWith(attributePrefix)) {
                        lastAttributeLine = i;
                     }
                  }
               }
            }
         }

         double range = 0.0;
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null && !EntityAttributeHelper.itemHasRangeAttribute(itemStack)) {
            range = PlayerAttackHelper.getStaticRange(player, itemStack);
         }

         if (BetterCombatClientMod.config.isTooltipAttackRangeEnabled && attributes.attacks() != null && attributes.attacks().length > 0 && range > 0.0) {
            Component rangeLine = attackRangeLine(range);
            int index = lastGreenAttributeIndex != null ? lastGreenAttributeIndex : lastAttributeLine;
            lines.add(index + 1, rangeLine);
         }

         if (attributes.isTwoHanded() && firstHandLine > 0) {
            MutableComponent handLine = Component.translatable("item.held.two_handed").withStyle(ChatFormatting.GRAY);
            lines.add(firstHandLine, handLine);
         }
      }
   }

   public static Component attackRangeLine(double range) {
      int operationId = Operation.ADD_VALUE.id();
      String rangeTranslationKey = "attribute.name.generic.attack_range";
      return CommonComponents.space()
         .append(
            Component.translatable(
               "attribute.modifier.equals." + operationId,
               new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(range), Component.translatable(rangeTranslationKey)}
            )
         )
         .withStyle(ChatFormatting.DARK_GREEN);
   }
}
