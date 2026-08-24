package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class AciddicsackbowlRightclickedOnBlockProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == UndeadRevamp2ModItems.ACIDDICSACKBOWL.get()
            && entity.level()
                  .clip(
                     new ClipContext(
                        entity.getEyePosition(1.0F),
                        entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                        Block.OUTLINE,
                        Fluid.SOURCE_ONLY,
                        entity
                     )
                  )
                  .getType()
               == Type.BLOCK
            && world.getFluidState(
                     new BlockPos(
                        entity.level()
                           .clip(
                              new ClipContext(
                                 entity.getEyePosition(1.0F),
                                 entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                 Block.OUTLINE,
                                 Fluid.SOURCE_ONLY,
                                 entity
                              )
                           )
                           .getBlockPos()
                           .getX(),
                        entity.level()
                           .clip(
                              new ClipContext(
                                 entity.getEyePosition(1.0F),
                                 entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                 Block.OUTLINE,
                                 Fluid.SOURCE_ONLY,
                                 entity
                              )
                           )
                           .getBlockPos()
                           .getY(),
                        entity.level()
                           .clip(
                              new ClipContext(
                                 entity.getEyePosition(1.0F),
                                 entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                 Block.OUTLINE,
                                 Fluid.SOURCE_ONLY,
                                 entity
                              )
                           )
                           .getBlockPos()
                           .getZ()
                     )
                  )
                  .createLegacyBlock()
                  .getBlock()
               == Blocks.WATER) {
            if (!(entity instanceof Player _plr && _plr.getAbilities().instabuild) && entity instanceof LivingEntity _entity) {
               ItemStack _setstack = new ItemStack((ItemLike)UndeadRevamp2ModItems.ACIDDICSACKBOWL.get()).copy();
               _setstack.setCount((entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getCount() - 1);
               _entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
               if (_entity instanceof Player _player) {
                  _player.getInventory().setChanged();
               }
            }

            if (entity instanceof LivingEntity _entityx) {
               _entityx.swing(InteractionHand.MAIN_HAND, true);
            }

            if (entity instanceof Player _player) {
               ItemStack _setstack = new ItemStack((ItemLike)UndeadRevamp2ModItems.SPITTEA.get()).copy();
               _setstack.setCount(1);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.bottle.fill")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.bottle.fill")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == UndeadRevamp2ModItems.ACIDDICSACKBOWL.get()
            && entity.level()
                  .clip(
                     new ClipContext(
                        entity.getEyePosition(1.0F),
                        entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                        Block.OUTLINE,
                        Fluid.SOURCE_ONLY,
                        entity
                     )
                  )
                  .getType()
               == Type.BLOCK
            && (
               world.getBlockState(
                           new BlockPos(
                              entity.level()
                                 .clip(
                                    new ClipContext(
                                       entity.getEyePosition(1.0F),
                                       entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                       Block.OUTLINE,
                                       Fluid.SOURCE_ONLY,
                                       entity
                                    )
                                 )
                                 .getBlockPos()
                                 .getX(),
                              entity.level()
                                 .clip(
                                    new ClipContext(
                                       entity.getEyePosition(1.0F),
                                       entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                       Block.OUTLINE,
                                       Fluid.SOURCE_ONLY,
                                       entity
                                    )
                                 )
                                 .getBlockPos()
                                 .getY(),
                              entity.level()
                                 .clip(
                                    new ClipContext(
                                       entity.getEyePosition(1.0F),
                                       entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                       Block.OUTLINE,
                                       Fluid.SOURCE_ONLY,
                                       entity
                                    )
                                 )
                                 .getBlockPos()
                                 .getZ()
                           )
                        )
                        .getBlock()
                     == Blocks.WATER_CAULDRON
                  || world.getBlockState(
                           new BlockPos(
                              entity.level()
                                 .clip(
                                    new ClipContext(
                                       entity.getEyePosition(1.0F),
                                       entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                       Block.OUTLINE,
                                       Fluid.SOURCE_ONLY,
                                       entity
                                    )
                                 )
                                 .getBlockPos()
                                 .getX(),
                              entity.level()
                                 .clip(
                                    new ClipContext(
                                       entity.getEyePosition(1.0F),
                                       entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                       Block.OUTLINE,
                                       Fluid.SOURCE_ONLY,
                                       entity
                                    )
                                 )
                                 .getBlockPos()
                                 .getY(),
                              entity.level()
                                 .clip(
                                    new ClipContext(
                                       entity.getEyePosition(1.0F),
                                       entity.getEyePosition(1.0F).add(entity.getViewVector(1.0F).scale(5.0)),
                                       Block.OUTLINE,
                                       Fluid.SOURCE_ONLY,
                                       entity
                                    )
                                 )
                                 .getBlockPos()
                                 .getZ()
                           )
                        )
                        .getBlock()
                     == Blocks.CAULDRON
            )) {
            if ((
                  blockstate.getBlock().getStateDefinition().getProperty("level") instanceof IntegerProperty _getip29
                     ? (Integer)blockstate.getValue(_getip29)
                     : -1
               )
               > 1) {
               int _value = (
                     blockstate.getBlock().getStateDefinition().getProperty("level") instanceof IntegerProperty _getip31
                        ? (Integer)blockstate.getValue(_getip31)
                        : -1
                  )
                  - 1;
               BlockPos _pos = BlockPos.containing(x, y, z);
               BlockState _bs = world.getBlockState(_pos);
               if (_bs.getBlock().getStateDefinition().getProperty("level") instanceof IntegerProperty _integerProp
                  && _integerProp.getPossibleValues().contains(_value)) {
                  world.setBlock(_pos, (BlockState)_bs.setValue(_integerProp, _value), 3);
               }

               if (!(entity instanceof Player _plrx && _plrx.getAbilities().instabuild) && entity instanceof LivingEntity _entityx) {
                  ItemStack _setstack = new ItemStack((ItemLike)UndeadRevamp2ModItems.SPITTEA.get()).copy();
                  _setstack.setCount((entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getCount() - 1);
                  _entityx.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                  if (_entityx instanceof Player _player) {
                     _player.getInventory().setChanged();
                  }
               }

               if (entity instanceof LivingEntity _entityxx) {
                  _entityxx.swing(InteractionHand.MAIN_HAND, true);
               }

               if (entity instanceof Player _player) {
                  ItemStack _setstack = new ItemStack((ItemLike)UndeadRevamp2ModItems.SPITTEA.get()).copy();
                  _setstack.setCount(1);
                  ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.bottle.fill")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.bottle.fill")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }
            }

            if ((
                  blockstate.getBlock().getStateDefinition().getProperty("level") instanceof IntegerProperty _getip41
                     ? (Integer)blockstate.getValue(_getip41)
                     : -1
               )
               == 1) {
               BlockPos _bp = BlockPos.containing(x, y, z);
               BlockState _bsx = Blocks.CAULDRON.defaultBlockState();
               BlockState _bso = world.getBlockState(_bp);

               for (Property<?> _propertyOld : _bso.getProperties()) {
                  Property _propertyNew = _bsx.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
                  if (_propertyNew != null && _bsx.getValue(_propertyNew) != null) {
                     try {
                        _bsx = (BlockState)_bsx.setValue(_propertyNew, _bso.getValue(_propertyOld));
                     } catch (Exception var18) {
                     }
                  }
               }

               world.setBlock(_bp, _bsx, 3);
               if (!(entity instanceof Player _plrxx && _plrxx.getAbilities().instabuild) && entity instanceof LivingEntity _entityxx) {
                  ItemStack _setstack = new ItemStack((ItemLike)UndeadRevamp2ModItems.SPITTEA.get()).copy();
                  _setstack.setCount((entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getCount() - 1);
                  _entityxx.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                  if (_entityxx instanceof Player _player) {
                     _player.getInventory().setChanged();
                  }
               }

               if (entity instanceof LivingEntity _entityxxx) {
                  _entityxxx.swing(InteractionHand.MAIN_HAND, true);
               }

               if (entity instanceof Player _player) {
                  ItemStack _setstack = new ItemStack((ItemLike)UndeadRevamp2ModItems.SPITTEA.get()).copy();
                  _setstack.setCount(1);
                  ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
               }

               if (world instanceof Level _levelxx) {
                  if (!_levelxx.isClientSide()) {
                     _levelxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.bottle.fill")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _levelxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.bottle.fill")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }
            }
         }
      }
   }
}
