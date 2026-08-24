package net.mcreator.undeadrevamp.item;

import java.util.List;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ToothmaceItem extends SwordItem {
   private static final Tier TOOL_TIER = new Tier() {
      public int getUses() {
         return 1800;
      }

      public float getSpeed() {
         return 4.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_STONE_TOOL;
      }

      public int getEnchantmentValue() {
         return 2;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)UndeadRevamp2ModItems.HEAVYTOOTH.get())});
      }
   };

   public ToothmaceItem() {
      super(TOOL_TIER, new Properties().attributes(SwordItem.createAttributes(TOOL_TIER, 15.0F, -3.8F)));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.toothmace.description_0"));
      list.add(Component.translatable("item.undead_revamp2.toothmace.description_1"));
   }
}
