package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.effect.ModMobEffects;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.sound.ModSoundEvents;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class MandrakeCropBlock extends CropBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

   public MandrakeCropBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected ItemLike getBaseSeedId() {
      return (ItemLike)ModItems.MANDRAKE_SEEDS.get();
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }

   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      BlockState result = super.playerWillDestroy(level, pos, state, player);
      if (!level.isClientSide()
         && (Integer)state.getValue(AGE) == 3
         && !player.getAbilities().instabuild
         && !player.getItemBySlot(EquipmentSlot.HEAD).is(ModTags.Items.STUN_IMMUNE_HEADWEAR)) {
         player.addEffect(new MobEffectInstance(ModMobEffects.STUNNED, 60, 4));
         level.playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)ModSoundEvents.MANDRAKE_SCREAM.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      }

      return result;
   }
}
