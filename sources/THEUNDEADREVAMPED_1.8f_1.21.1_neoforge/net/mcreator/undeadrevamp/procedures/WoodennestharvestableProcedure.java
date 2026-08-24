package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class WoodennestharvestableProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.GLASS_BOTTLE
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
               == Type.BLOCK) {
            if (!(entity instanceof Player _plr && _plr.getAbilities().instabuild) && entity instanceof LivingEntity _entity) {
               ItemStack _setstack = new ItemStack(Items.GLASS_BOTTLE).copy();
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
               ItemStack _setstack = new ItemStack((ItemLike)UndeadRevamp2ModItems.THE_SOMNOLENCEEXTRACT.get()).copy();
               _setstack.setCount(1);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (Math.random() < 0.25 && world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolenceambt")),
                     SoundSource.NEUTRAL,
                     0.1F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:somnolenceambt")),
                     SoundSource.NEUTRAL,
                     0.1F,
                     1.0F,
                     false
                  );
               }
            }

            BlockPos _bp = BlockPos.containing(x, y, z);
            BlockState _bs = ((net.minecraft.world.level.block.Block)UndeadRevamp2ModBlocks.WOODENNEST.get()).defaultBlockState();
            BlockState _bso = world.getBlockState(_bp);

            for (Property<?> _propertyOld : _bso.getProperties()) {
               Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
               if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                  try {
                     _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
                  } catch (Exception var16) {
                  }
               }
            }

            world.setBlock(_bp, _bs, 3);
            if (world instanceof ServerLevel _levelx) {
               _levelx.addFreshEntity(new ExperienceOrb(_levelx, x, y, z, 4));
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
                     x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.bottle.fill")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
                  );
               }
            }
         }
      }
   }
}
