package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class EtherealSpiritPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.ETHEREAL_SPIRIT.get()
            && (
               world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.CARVED_PUMPKIN
                  || world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == Blocks.JACK_O_LANTERN
                  || world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == BornInChaosV1ModBlocks.EVIL_CARVED_PUMPKIN.get()
                  || world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == BornInChaosV1ModBlocks.FLAMING_EVIL_PUMPKIN.get()
            )
            && world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.HAY_BLOCK
            && (
               world.getBlockState(BlockPos.containing(x, y - 2.0, z)).getBlock() == Blocks.DARK_OAK_LOG
                  || world.getBlockState(BlockPos.containing(x, y - 2.0, z)).getBlock() == Blocks.STRIPPED_DARK_OAK_LOG
                  || world.getBlockState(BlockPos.containing(x, y - 2.0, z)).getBlock() == Blocks.DARK_OAK_WOOD
                  || world.getBlockState(BlockPos.containing(x, y - 2.0, z)).getBlock() == Blocks.STRIPPED_DARK_OAK_WOOD
            )
            && (
               world.getBlockState(BlockPos.containing(x, y - 3.0, z)).getBlock() == Blocks.DARK_OAK_LOG
                  || world.getBlockState(BlockPos.containing(x, y - 3.0, z)).getBlock() == Blocks.STRIPPED_DARK_OAK_LOG
                  || world.getBlockState(BlockPos.containing(x, y - 3.0, z)).getBlock() == Blocks.DARK_OAK_WOOD
                  || world.getBlockState(BlockPos.containing(x, y - 3.0, z)).getBlock() == Blocks.STRIPPED_DARK_OAK_WOOD
            )
            && world.getBlockState(BlockPos.containing(x, y - 4.0, z)).canOcclude()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            if (entity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:scarecrow"));
               if (_adv != null) {
                  AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                  if (!_ap.isDone()) {
                     for (String criteria : _ap.getRemainingCriteria()) {
                        _player.getAdvancements().award(_adv, criteria);
                     }
                  }
               }
            }

            world.destroyBlock(BlockPos.containing(x, y, z), false);
            world.destroyBlock(BlockPos.containing(x, y - 1.0, z), false);
            world.destroyBlock(BlockPos.containing(x, y - 2.0, z), false);
            world.destroyBlock(BlockPos.containing(x, y - 3.0, z), false);
            (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.phantom.ambient")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.phantom.ambient")),
                     SoundSource.NEUTRAL,
                     0.5F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.COMPOSTER, x, y, z, 10, 0.5, 0.5, 0.5, 1.0);
            }

            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PUMPKIN_SPIRIT.get())
                  .spawn(_levelx, BlockPos.containing(x + 0.5, y - 3.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }
         }
      }
   }
}
