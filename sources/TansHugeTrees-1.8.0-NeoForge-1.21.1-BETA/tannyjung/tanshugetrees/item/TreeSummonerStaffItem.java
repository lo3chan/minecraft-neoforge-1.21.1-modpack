package tannyjung.tanshugetrees.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import tannyjung.tanshugetrees.procedures.TreeSummonerStaffClickProcedure;

public class TreeSummonerStaffItem extends Item {
   public TreeSummonerStaffItem() {
      super(new Properties().stacksTo(1));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.BOW;
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 20;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.tanshugetrees.tree_summoner_staff.description_0"));
      list.add(Component.translatable("item.tanshugetrees.tree_summoner_staff.description_1"));
      list.add(Component.translatable("item.tanshugetrees.tree_summoner_staff.description_2"));
      list.add(Component.translatable("item.tanshugetrees.tree_summoner_staff.description_3"));
      list.add(Component.translatable("item.tanshugetrees.tree_summoner_staff.description_4"));
      list.add(Component.translatable("item.tanshugetrees.tree_summoner_staff.description_5"));
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
      entity.startUsingItem(hand);
      TreeSummonerStaffClickProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
      return ar;
   }
}
