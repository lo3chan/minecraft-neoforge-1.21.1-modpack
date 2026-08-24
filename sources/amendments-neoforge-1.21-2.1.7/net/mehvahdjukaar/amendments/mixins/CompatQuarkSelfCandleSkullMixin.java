package net.mehvahdjukaar.amendments.mixins;

import java.util.List;
import net.mehvahdjukaar.amendments.common.block.AbstractCandleSkullBlock;
import net.mehvahdjukaar.amendments.common.tile.CandleSkullBlockTile;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.misc.OptionalMixin;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.violetmoon.quark.addons.oddities.module.MatrixEnchantingModule;
import org.violetmoon.quark.addons.oddities.util.Influence;
import org.violetmoon.quark.api.IEnchantmentInfluencer;

@OptionalMixin("org.violetmoon.quark.api.IEnchantmentInfluencer")
@Mixin({AbstractCandleSkullBlock.class})
public abstract class CompatQuarkSelfCandleSkullMixin implements IEnchantmentInfluencer {
   @Unique
   private DyeColor amendments$getColor(BlockState s, BlockGetter level, BlockPos pos) {
      if ((Boolean)s.getValue(CandleBlock.LIT) && level.getBlockEntity(pos) instanceof CandleSkullBlockTile tile) {
         BlockState state = tile.getCandle();
         if (state.getBlock() instanceof CandleBlock) {
            return BlocksColorAPI.getColor(state.getBlock());
         }
      }

      return null;
   }

   public float[] getEnchantmentInfluenceColor(BlockGetter world, BlockPos pos, BlockState state) {
      DyeColor color = this.amendments$getColor(state, world, pos);
      return color == null ? null : ColorUtils.unpack(color.getTextureDiffuseColor());
   }

   @Nullable
   public ParticleOptions getExtraParticleOptions(BlockGetter world, BlockPos pos, BlockState state) {
      return state.getValue(CandleBlock.LIT)
            && world.getBlockEntity(pos) instanceof CandleSkullBlockTile tile
            && tile.getParticle() != ParticleTypes.SMALL_FLAME
         ? (ParticleOptions)tile.getParticle()
         : null;
   }

   public double getExtraParticleChance(BlockGetter world, BlockPos pos, BlockState state) {
      return 0.25;
   }

   public int getInfluenceStack(BlockGetter world, BlockPos pos, BlockState state) {
      return state.getValue(CandleBlock.LIT) ? (Integer)state.getValue(CandleBlock.CANDLES) + 1 : 0;
   }

   public boolean influencesEnchantment(BlockGetter world, BlockPos pos, BlockState state, Enchantment enchantment) {
      DyeColor color = this.amendments$getColor(state, world, pos);
      if (color == null) {
         return false;
      } else {
         Influence influence = (Influence)MatrixEnchantingModule.candleInfluences.get(color);
         List<Enchantment> boosts = this.amendments$isSoul(state.getBlock()) ? influence.dampen() : influence.boost();
         return boosts.contains(enchantment);
      }
   }

   public boolean dampensEnchantment(BlockGetter world, BlockPos pos, BlockState state, Enchantment enchantment) {
      DyeColor color = this.amendments$getColor(state, world, pos);
      if (color == null) {
         return false;
      } else {
         Influence influence = (Influence)MatrixEnchantingModule.candleInfluences.get(color);
         List<Enchantment> dampens = this.amendments$isSoul(state.getBlock()) ? influence.boost() : influence.dampen();
         return dampens.contains(enchantment);
      }
   }

   @Unique
   private boolean amendments$isSoul(Block block) {
      return block == ModRegistry.SKULL_CANDLE_SOUL.get() || block == ModRegistry.SKULL_CANDLE_SOUL_WALL.get();
   }
}
