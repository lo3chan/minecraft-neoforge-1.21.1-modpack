package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemTarantulaHawkElytra extends ArmorItem implements IClientExtensionItem {
   public ItemTarantulaHawkElytra(Properties props, AMArmorMaterial mat) {
      super(mat.holder(), Type.CHESTPLATE, props);
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getArmorRenderProperties());
   }

   public static boolean isUsable(ItemStack stack) {
      return stack.getDamageValue() < stack.getMaxDamage() - 1;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      return super.use(worldIn, playerIn, handIn);
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return isUsable(stack);
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      if (!entity.level().isClientSide() && (flightTicks + 1) % 20 == 0) {
         AMCompat.hurtAndBreak(stack, 1, entity, EquipmentSlot.CHEST);
      }

      return true;
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == AMItemRegistry.TARANTULA_HAWK_WING_FRAGMENT.get();
   }

   public EquipmentSlot getEquipmentSlot(ItemStack stack) {
      return EquipmentSlot.CHEST;
   }
}
