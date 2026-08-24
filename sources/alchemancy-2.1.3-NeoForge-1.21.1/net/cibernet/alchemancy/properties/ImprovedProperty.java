package net.cibernet.alchemancy.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Tool.Rule;

public class ImprovedProperty extends Property {
   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      if (dataType == DataComponents.TOOL && data instanceof Tool tool) {
         List<Rule> rules = new ArrayList<>();

         for (Rule rule : tool.rules()) {
            if (rule.correctForDrops().isPresent() && (Boolean)rule.correctForDrops().get()) {
               rules.add(
                  new Rule(rule.blocks(), Optional.of(rule.speed().isPresent() ? Math.max((Float)rule.speed().get(), 8.0F) : 8.0F), rule.correctForDrops())
               );
            }
         }

         rules.add(Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL));
         return new Tool(rules, tool.defaultMiningSpeed(), tool.damagePerBlock());
      } else {
         return dataType == DataComponents.MAX_DAMAGE && data instanceof Integer i && i < 1600
            ? Math.max((Integer)data, Math.min(1600, (Integer)data * 2))
            : super.modifyDataComponent(stack, dataType, data);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 4910553;
   }

   @Override
   public int getPriority() {
      return 100;
   }
}
