package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber
public class HewnScorchedLogPProcedure {
   @SubscribeEvent
   public static void onRightClickBlock(RightClickBlock event) {
      if (event.getHand() == event.getEntity().getUsedItemHand()) {
         execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof AxeItem
            && (
               BornInChaosV1ModBlocks.SCORCHED_LOG.get() == world.getBlockState(BlockPos.containing(x, y, z)).getBlock()
                  || BornInChaosV1ModBlocks.SMOLDERING_SCORCHED_LOG.get() == world.getBlockState(BlockPos.containing(x, y, z)).getBlock()
            )) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            if (world instanceof ServerLevel _level) {
               (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(1, _level, null, _stkprov -> {});
            }

            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockState _bs = ((Block)BornInChaosV1ModBlocks.STRIPPED_SCORCHED_LOG.get()).defaultBlockState();
            BlockState _bso = world.getBlockState(_bp);

            for (Property<?> _propertyOld : _bso.getProperties()) {
               Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
               if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                  try {
                     _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
                  } catch (Exception var20) {
                  }
               }
            }

            BlockEntity _be = world.getBlockEntity(_bp);
            CompoundTag _bnbt = null;
            if (_be != null) {
               _bnbt = _be.saveWithFullMetadata(world.registryAccess());
               _be.setRemoved();
            }

            world.setBlock(_bp, _bs, 3);
            if (_bnbt != null) {
               _be = world.getBlockEntity(_bp);
               if (_be != null) {
                  try {
                     _be.loadWithComponents(_bnbt, world.registryAccess());
                  } catch (Exception var19) {
                  }
               }
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.axe.strip")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.axe.strip")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof AxeItem
            && (
               BornInChaosV1ModBlocks.SCORCHED_WOOD.get() == world.getBlockState(BlockPos.containing(x, y, z)).getBlock()
                  || BornInChaosV1ModBlocks.SMOLDERING_SCORCHED_WOOD.get() == world.getBlockState(BlockPos.containing(x, y, z)).getBlock()
            )) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            if (world instanceof ServerLevel _levelx) {
               (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(1, _levelx, null, _stkprov -> {});
            }

            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockState _bs = ((Block)BornInChaosV1ModBlocks.STRIPPED_SCORCHED_WOOD.get()).defaultBlockState();
            BlockState _bso = world.getBlockState(_bp);

            for (Property<?> _propertyOldx : _bso.getProperties()) {
               Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOldx.getName());
               if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                  try {
                     _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOldx));
                  } catch (Exception var18) {
                  }
               }
            }

            BlockEntity _bex = world.getBlockEntity(_bp);
            CompoundTag _bnbtx = null;
            if (_bex != null) {
               _bnbtx = _bex.saveWithFullMetadata(world.registryAccess());
               _bex.setRemoved();
            }

            world.setBlock(_bp, _bs, 3);
            if (_bnbtx != null) {
               _bex = world.getBlockEntity(_bp);
               if (_bex != null) {
                  try {
                     _bex.loadWithComponents(_bnbtx, world.registryAccess());
                  } catch (Exception var17) {
                  }
               }
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.axe.strip")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.axe.strip")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }
      }
   }
}
