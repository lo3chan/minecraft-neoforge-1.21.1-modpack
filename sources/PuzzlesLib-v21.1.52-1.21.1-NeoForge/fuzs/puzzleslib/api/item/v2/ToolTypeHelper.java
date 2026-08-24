package fuzs.puzzleslib.api.item.v2;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.ArmorItem.Type;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public interface ToolTypeHelper {
   ToolTypeHelper INSTANCE = ProxyImpl.get().getToolTypeHelper();

   @MustBeInvokedByOverriders
   default boolean isSword(ItemStack itemStack) {
      return itemStack.is(ItemTags.SWORDS);
   }

   @MustBeInvokedByOverriders
   default boolean isAxe(ItemStack itemStack) {
      return itemStack.getItem() instanceof AxeItem || itemStack.is(ItemTags.AXES);
   }

   @MustBeInvokedByOverriders
   default boolean isHoe(ItemStack itemStack) {
      return itemStack.getItem() instanceof HoeItem || itemStack.is(ItemTags.HOES);
   }

   @MustBeInvokedByOverriders
   default boolean isPickaxe(ItemStack itemStack) {
      return itemStack.is(ItemTags.PICKAXES);
   }

   @MustBeInvokedByOverriders
   default boolean isShovel(ItemStack itemStack) {
      return itemStack.getItem() instanceof AxeItem || itemStack.is(ItemTags.SHOVELS);
   }

   @MustBeInvokedByOverriders
   default boolean isShears(ItemStack itemStack) {
      return itemStack.getItem() instanceof ShearsItem;
   }

   @MustBeInvokedByOverriders
   default boolean isShield(ItemStack itemStack) {
      return itemStack.getItem() instanceof ShieldItem;
   }

   @MustBeInvokedByOverriders
   default boolean isBow(ItemStack itemStack) {
      return itemStack.getItem() instanceof BowItem;
   }

   @MustBeInvokedByOverriders
   default boolean isCrossbow(ItemStack itemStack) {
      return itemStack.getItem() instanceof CrossbowItem;
   }

   @MustBeInvokedByOverriders
   default boolean isFishingRod(ItemStack itemStack) {
      return itemStack.getItem() instanceof FishingRodItem;
   }

   @MustBeInvokedByOverriders
   default boolean isTridentLike(ItemStack itemStack) {
      return itemStack.getItem() instanceof TridentItem;
   }

   @MustBeInvokedByOverriders
   default boolean isBrush(ItemStack itemStack) {
      return itemStack.getItem() instanceof BrushItem;
   }

   @MustBeInvokedByOverriders
   default boolean isMace(ItemStack itemStack) {
      return itemStack.getItem() instanceof MaceItem;
   }

   @MustBeInvokedByOverriders
   default boolean isMeleeWeapon(ItemStack itemStack) {
      return this.isSword(itemStack) || this.isAxe(itemStack) || this.isTridentLike(itemStack) || this.isMace(itemStack);
   }

   @MustBeInvokedByOverriders
   default boolean isRangedWeapon(ItemStack itemStack) {
      return this.isBow(itemStack) || this.isCrossbow(itemStack) || this.isTridentLike(itemStack);
   }

   @MustBeInvokedByOverriders
   default boolean isWeapon(ItemStack itemStack) {
      return this.isMeleeWeapon(itemStack) || this.isRangedWeapon(itemStack);
   }

   @MustBeInvokedByOverriders
   default boolean isMiningTool(ItemStack itemStack) {
      return this.isAxe(itemStack) || this.isHoe(itemStack) || this.isPickaxe(itemStack) || this.isShovel(itemStack);
   }

   @MustBeInvokedByOverriders
   default boolean isTool(ItemStack itemStack) {
      return this.isMiningTool(itemStack)
         || this.isWeapon(itemStack)
         || this.isShears(itemStack)
         || this.isShield(itemStack)
         || this.isFishingRod(itemStack)
         || this.isBrush(itemStack);
   }

   @MustBeInvokedByOverriders
   default boolean isHeadArmor(ItemStack itemStack) {
      return this.isArmor(itemStack, Type.HELMET) || itemStack.is(ItemTags.HEAD_ARMOR);
   }

   @MustBeInvokedByOverriders
   default boolean isChestArmor(ItemStack itemStack) {
      return this.isArmor(itemStack, Type.CHESTPLATE) || itemStack.is(ItemTags.CHEST_ARMOR);
   }

   @MustBeInvokedByOverriders
   default boolean isLegArmor(ItemStack itemStack) {
      return this.isArmor(itemStack, Type.LEGGINGS) || itemStack.is(ItemTags.LEG_ARMOR);
   }

   @MustBeInvokedByOverriders
   default boolean isFootArmor(ItemStack itemStack) {
      return this.isArmor(itemStack, Type.BOOTS) || itemStack.is(ItemTags.FOOT_ARMOR);
   }

   @MustBeInvokedByOverriders
   default boolean isBodyArmor(ItemStack itemStack) {
      return this.isArmor(itemStack, Type.BODY);
   }

   @MustBeInvokedByOverriders
   private boolean isArmor(ItemStack itemStack, Type armorType) {
      return itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == armorType.getSlot();
   }

   @MustBeInvokedByOverriders
   default boolean isArmor(ItemStack itemStack) {
      return this.isHeadArmor(itemStack)
         || this.isChestArmor(itemStack)
         || this.isLegArmor(itemStack)
         || this.isFootArmor(itemStack)
         || this.isBodyArmor(itemStack);
   }
}
