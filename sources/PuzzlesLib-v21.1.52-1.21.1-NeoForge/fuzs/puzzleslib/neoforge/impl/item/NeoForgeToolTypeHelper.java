package fuzs.puzzleslib.neoforge.impl.item;

import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.Tags.Items;

public final class NeoForgeToolTypeHelper implements ToolTypeHelper {
   @Override
   public boolean isSword(ItemStack itemStack) {
      return ToolTypeHelper.super.isSword(itemStack) || itemStack.canPerformAction(ItemAbilities.SWORD_SWEEP);
   }

   @Override
   public boolean isShears(ItemStack itemStack) {
      return ToolTypeHelper.super.isShears(itemStack) || itemStack.is(Items.TOOLS_SHEAR) || itemStack.canPerformAction(ItemAbilities.SHEARS_DIG);
   }

   @Override
   public boolean isShield(ItemStack itemStack) {
      return ToolTypeHelper.super.isShield(itemStack) || itemStack.is(Items.TOOLS_SHIELD);
   }

   @Override
   public boolean isBow(ItemStack itemStack) {
      return ToolTypeHelper.super.isBow(itemStack) || itemStack.is(Items.TOOLS_BOW);
   }

   @Override
   public boolean isCrossbow(ItemStack itemStack) {
      return ToolTypeHelper.super.isCrossbow(itemStack) || itemStack.is(Items.TOOLS_CROSSBOW);
   }

   @Override
   public boolean isFishingRod(ItemStack itemStack) {
      return ToolTypeHelper.super.isFishingRod(itemStack)
         || itemStack.is(Items.TOOLS_FISHING_ROD)
         || itemStack.canPerformAction(ItemAbilities.FISHING_ROD_CAST);
   }

   @Override
   public boolean isTridentLike(ItemStack itemStack) {
      return ToolTypeHelper.super.isTridentLike(itemStack) || itemStack.is(Items.TOOLS_SPEAR) || itemStack.canPerformAction(ItemAbilities.TRIDENT_THROW);
   }

   @Override
   public boolean isBrush(ItemStack itemStack) {
      return ToolTypeHelper.super.isBrush(itemStack) || itemStack.is(Items.TOOLS_BRUSH) || itemStack.canPerformAction(ItemAbilities.BRUSH_BRUSH);
   }

   @Override
   public boolean isMace(ItemStack itemStack) {
      return ToolTypeHelper.super.isMace(itemStack) || itemStack.is(Items.TOOLS_MACE);
   }

   @Override
   public boolean isMeleeWeapon(ItemStack itemStack) {
      return ToolTypeHelper.super.isMeleeWeapon(itemStack) || itemStack.is(Items.MELEE_WEAPON_TOOLS);
   }

   @Override
   public boolean isRangedWeapon(ItemStack itemStack) {
      return ToolTypeHelper.super.isRangedWeapon(itemStack) || itemStack.is(Items.RANGED_WEAPON_TOOLS);
   }

   @Override
   public boolean isMiningTool(ItemStack itemStack) {
      return ToolTypeHelper.super.isMiningTool(itemStack) || itemStack.is(Items.MINING_TOOL_TOOLS);
   }

   @Override
   public boolean isTool(ItemStack itemStack) {
      return ToolTypeHelper.super.isTool(itemStack) || itemStack.is(Items.TOOLS);
   }

   @Override
   public boolean isArmor(ItemStack itemStack) {
      return ToolTypeHelper.super.isArmor(itemStack) || itemStack.is(Items.ARMORS);
   }
}
