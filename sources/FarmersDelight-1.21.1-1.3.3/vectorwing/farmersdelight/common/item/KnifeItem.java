package vectorwing.farmersdelight.common.item;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;

public class KnifeItem extends DiggerItem {
   public static final ItemAbility KNIFE_DIG = ItemAbility.get("knife_dig");
   public static final ItemAbility KNIFE_HARVEST = ItemAbility.get("knife_harvest");
   public static final Set<ItemAbility> KNIFE_ACTIONS = Set.of(ItemAbilities.SHEARS_CARVE, ItemAbilities.SWORD_DIG, KNIFE_DIG, KNIFE_HARVEST);

   public KnifeItem(Tier tier, Properties properties) {
      super(tier, ModTags.Blocks.MINEABLE_WITH_KNIFE, properties);
   }

   public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
      return !player.isCreative();
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      return true;
   }

   public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
   }

   public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
      return enchantment.is(Enchantments.SWEEPING_EDGE) ? false : super.isPrimaryItemFor(stack, enchantment);
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return enchantment.is(Enchantments.SWEEPING_EDGE) ? false : super.supportsEnchantment(stack, enchantment);
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
      return KNIFE_ACTIONS.contains(toolAction);
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      ItemStack toolStack = context.getItemInHand();
      BlockPos pos = context.getClickedPos();
      BlockState state = level.getBlockState(pos);
      Direction facing = context.getClickedFace();
      if (state.getBlock() == Blocks.PUMPKIN && toolStack.is(ModTags.Items.KNIVES)) {
         Player player = context.getPlayer();
         if (player != null && !level.isClientSide) {
            Direction direction = facing.getAxis() == Axis.Y ? player.getDirection().getOpposite() : facing;
            level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlock(pos, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction), 11);
            ItemEntity itemEntity = new ItemEntity(
               level,
               pos.getX() + 0.5 + direction.getStepX() * 0.65,
               pos.getY() + 0.1,
               pos.getZ() + 0.5 + direction.getStepZ() * 0.65,
               new ItemStack(Items.PUMPKIN_SEEDS, 4)
            );
            itemEntity.setDeltaMovement(
               0.05 * direction.getStepX() + level.random.nextDouble() * 0.02, 0.05, 0.05 * direction.getStepZ() + level.random.nextDouble() * 0.02
            );
            level.addFreshEntity(itemEntity);
            toolStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   @EventBusSubscriber(
      modid = "farmersdelight"
   )
   public static class KnifeEvents {
      @SubscribeEvent
      public static void onKnifeKnockback(LivingKnockBackEvent event) {
         LivingEntity attacker = event.getEntity().getKillCredit();
         ItemStack toolStack = attacker != null ? attacker.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
         if (toolStack.getItem() instanceof KnifeItem) {
            event.setStrength(event.getOriginalStrength() - 0.1F);
         }
      }

      @SubscribeEvent
      public static void onCakeInteraction(RightClickBlock event) {
         ItemStack heldStack = event.getEntity().getItemInHand(event.getHand());
         if (ItemUtils.isKnife(heldStack)) {
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = event.getLevel().getBlockState(pos);
            Block block = state.getBlock();
            if (state.is(ModTags.Blocks.DROPS_CAKE_SLICE)) {
               level.setBlock(pos, (BlockState)Blocks.CAKE.defaultBlockState().setValue(CakeBlock.BITES, 1), 3);
               Block.dropResources(state, level, pos);
               ItemUtils.spawnItemEntity(
                  level, new ItemStack((ItemLike)ModItems.CAKE_SLICE.get()), pos.getX(), pos.getY() + 0.2, pos.getZ() + 0.5, -0.05, 0.0, 0.0
               );
               level.playSound(null, pos, ModSounds.BLOCK_FOOD_SLICE.get(), SoundSource.PLAYERS, 0.8F, 0.8F);
               event.getEntity().awardStat(Stats.ITEM_USED.get(heldStack.getItem()));
               event.setCancellationResult(InteractionResult.SUCCESS);
               event.setCanceled(true);
            }

            if (block == Blocks.CAKE) {
               int bites = (Integer)state.getValue(CakeBlock.BITES);
               if (bites < 6) {
                  level.setBlock(pos, (BlockState)state.setValue(CakeBlock.BITES, bites + 1), 3);
               } else {
                  level.removeBlock(pos, false);
               }

               ItemUtils.spawnItemEntity(
                  level, new ItemStack((ItemLike)ModItems.CAKE_SLICE.get()), pos.getX() + bites * 0.1, pos.getY() + 0.2, pos.getZ() + 0.5, -0.05, 0.0, 0.0
               );
               level.playSound(null, pos, ModSounds.BLOCK_FOOD_SLICE.get(), SoundSource.PLAYERS, 0.8F, 0.8F);
               event.getEntity().awardStat(Stats.ITEM_USED.get(heldStack.getItem()));
               event.setCancellationResult(InteractionResult.SUCCESS);
               event.setCanceled(true);
            }
         }
      }
   }
}
