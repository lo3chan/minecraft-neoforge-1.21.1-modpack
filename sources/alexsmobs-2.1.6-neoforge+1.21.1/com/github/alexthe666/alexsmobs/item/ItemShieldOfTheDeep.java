package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ItemShieldOfTheDeep extends Item implements IClientExtensionItem {
   public ItemShieldOfTheDeep(Properties group) {
      super(AMCompat.shieldProperties(group));
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
      return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
   }

   public UseAnim getUseAnimation(ItemStack p_77661_1_) {
      return UseAnim.BLOCK;
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return this.getUseDuration(stack);
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   public InteractionResultHolder<ItemStack> use(Level p_77659_1_, Player p_77659_2_, InteractionHand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getItemInHand(p_77659_3_);
      p_77659_2_.startUsingItem(p_77659_3_);
      return AMCompat.consume(lvt_4_1_);
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return AMItemRegistry.SERRATED_SHARK_TOOTH.get() == p_82789_2_.getItem() || super.isValidRepairItem(p_82789_1_, p_82789_2_);
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
   }
}
