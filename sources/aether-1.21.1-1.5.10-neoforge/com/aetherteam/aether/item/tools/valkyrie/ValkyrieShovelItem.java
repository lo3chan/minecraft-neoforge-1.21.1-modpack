package com.aetherteam.aether.item.tools.valkyrie;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ValkyrieTool;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Item.Properties;

public class ValkyrieShovelItem extends ShovelItem implements ValkyrieTool {
   public ValkyrieShovelItem() {
      super(
         AetherItemTiers.VALKYRIE,
         new Properties()
            .rarity(AetherItems.AETHER_LOOT)
            .attributes(
               ShovelItem.createAttributes(AetherItemTiers.VALKYRIE, 1.5F, -3.3F)
                  .withModifierAdded(
                     Attributes.BLOCK_INTERACTION_RANGE,
                     new AttributeModifier(BLOCK_INTERACTION_RANGE_MODIFIER_UUID, 3.5, Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND
                  )
                  .withModifierAdded(
                     Attributes.ENTITY_INTERACTION_RANGE,
                     new AttributeModifier(ENTITY_INTERACTION_RANGE_MODIFIER_UUID, 3.5, Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND
                  )
            )
      );
   }
}
