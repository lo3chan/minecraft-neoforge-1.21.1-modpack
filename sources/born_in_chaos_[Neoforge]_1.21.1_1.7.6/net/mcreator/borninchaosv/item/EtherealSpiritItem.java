package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.EtherealSpiritPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class EtherealSpiritItem extends Item {
   public EtherealSpiritItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.EAT;
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 30;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
      entity.startUsingItem(hand);
      return ar;
   }

   public InteractionResult useOn(UseOnContext context) {
      super.useOn(context);
      EtherealSpiritPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure.execute(
         context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer()
      );
      return InteractionResult.SUCCESS;
   }
}
