package net.Pandarix.block.custom;

import java.util.Optional;
import net.Pandarix.BACommon;
import net.Pandarix.util.ServerPlayerHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootVaseBlock extends Block {
   private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 14.0, 13.0);
   ResourceLocation ADVANCEMENT_ID = BACommon.createResource("loot_vase_broken");

   public LootVaseBlock(Properties settings) {
      super(settings);
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
      try {
         Optional<Reference<Enchantment>> SILK_TOUCH = level.registryAccess()
            .asGetterLookup()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .get(Enchantments.SILK_TOUCH);
         boolean hasSilkTouch = SILK_TOUCH.isPresent() && EnchantmentHelper.getItemEnchantmentLevel((Holder)SILK_TOUCH.get(), player.getMainHandItem()) > 0;
         if (!level.isClientSide()) {
            if (!player.isCreative() && !hasSilkTouch) {
               Entity xpOrb = new ExperienceOrb(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 4);
               level.addFreshEntity(xpOrb);
            }

            if (level.getServer() != null) {
               AdvancementHolder advancement = level.getServer().getAdvancements().get(this.ADVANCEMENT_ID);
               if (advancement != null) {
                  ServerPlayerHelper.tryGetServerPlayer(player).ifPresent(sp -> sp.getAdvancements().award(advancement, "criteria"));
               }
            }
         }

         if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !hasSilkTouch && level.getRandom().nextInt(25) == 1) {
            spawnSilverFish(level, blockPos);
         }
      } catch (Exception var10) {
         BACommon.LOGGER.error("Error in breaking LootVase Block! : ", var10);
      }

      super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
   }

   private static void spawnSilverFish(Level level, BlockPos pos) {
      Silverfish silverfishEntity = (Silverfish)EntityType.SILVERFISH.create(level);
      if (silverfishEntity != null) {
         silverfishEntity.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
         level.addFreshEntity(silverfishEntity);
         silverfishEntity.spawnAnim();
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
      return false;
   }
}
