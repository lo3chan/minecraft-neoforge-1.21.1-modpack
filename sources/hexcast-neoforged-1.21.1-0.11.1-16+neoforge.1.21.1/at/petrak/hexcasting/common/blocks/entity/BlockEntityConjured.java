package at.petrak.hexcasting.common.blocks.entity;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.blocks.BlockConjured;
import at.petrak.hexcasting.common.blocks.BlockConjuredLight;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockEntityConjured extends HexBlockEntity {
   private static final Random RANDOM = new Random();
   private FrozenPigment colorizer = FrozenPigment.DEFAULT.get();
   public static final String TAG_COLORIZER = "tag_colorizer";

   public BlockEntityConjured(BlockPos pos, BlockState state) {
      super(HexBlockEntities.CONJURED_TILE, pos, state);
   }

   public void walkParticle(Entity pEntity) {
      if (this.getBlockState().getBlock() instanceof BlockConjured conjured && !(conjured instanceof BlockConjuredLight)) {
         ColorProvider colProvider = this.colorizer.getColorProvider();

         for (int i = 0; i < 3; i++) {
            int color = colProvider.getColor(
               pEntity.tickCount, pEntity.position().add(new Vec3(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()).scale(RANDOM.nextFloat() * 3.0F))
            );

            assert this.level != null;

            this.level
               .addParticle(
                  new ConjureParticleOptions(color),
                  pEntity.getX() + RANDOM.nextFloat() * 0.6 - 0.3,
                  this.getBlockPos().getY() + RANDOM.nextFloat() * 0.05 + 0.95,
                  pEntity.getZ() + RANDOM.nextFloat() * 0.6 - 0.3,
                  RANDOM.nextFloat(-0.02F, 0.02F),
                  RANDOM.nextFloat(0.02F),
                  RANDOM.nextFloat(-0.02F, 0.02F)
               );
         }
      }
   }

   public void particleEffect() {
      if (this.getBlockState().getBlock() instanceof BlockConjured) {
         int color = this.colorizer
            .getColorProvider()
            .getColor(RANDOM.nextFloat() * 16384.0F, new Vec3(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()).scale(RANDOM.nextFloat() * 3.0F));

         assert this.level != null;

         if (this.getBlockState().getBlock() instanceof BlockConjuredLight) {
            if (RANDOM.nextFloat() < 0.5) {
               this.level
                  .addParticle(
                     new ConjureParticleOptions(color),
                     this.getBlockPos().getX() + 0.45 + RANDOM.nextFloat() * 0.1,
                     this.getBlockPos().getY() + 0.45 + RANDOM.nextFloat() * 0.1,
                     this.getBlockPos().getZ() + 0.45 + RANDOM.nextFloat() * 0.1,
                     RANDOM.nextFloat(-0.005F, 0.005F),
                     RANDOM.nextFloat(-0.002F, 0.02F),
                     RANDOM.nextFloat(-0.005F, 0.005F)
                  );
            }
         } else if (RANDOM.nextFloat() < 0.2) {
            this.level
               .addParticle(
                  new ConjureParticleOptions(color),
                  (double)this.getBlockPos().getX() + RANDOM.nextFloat(),
                  (double)this.getBlockPos().getY() + RANDOM.nextFloat(),
                  (double)this.getBlockPos().getZ() + RANDOM.nextFloat(),
                  RANDOM.nextFloat(-0.02F, 0.02F),
                  RANDOM.nextFloat(-0.02F, 0.02F),
                  RANDOM.nextFloat(-0.02F, 0.02F)
               );
         }
      }
   }

   public void landParticle(Entity entity, int number) {
      ColorProvider colProvider = this.colorizer.getColorProvider();

      for (int i = 0; i < number * 2; i++) {
         int color = colProvider.getColor(
            entity.tickCount, entity.position().add(new Vec3(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()).scale(RANDOM.nextFloat() * 3.0F))
         );

         assert this.level != null;

         this.level
            .addParticle(
               new ConjureParticleOptions(color),
               entity.getX() + RANDOM.nextFloat() * 0.8 - 0.2,
               this.getBlockPos().getY() + RANDOM.nextFloat() * 0.05 + 0.95,
               entity.getZ() + RANDOM.nextFloat() * 0.8 - 0.2,
               0.0,
               0.0,
               0.0
            );
      }
   }

   @Override
   protected void saveModData(CompoundTag tag) {
      tag.put("tag_colorizer", this.colorizer.serializeToNBT());
   }

   @Override
   protected void loadModData(CompoundTag tag) {
      this.colorizer = FrozenPigment.fromNBT(tag.getCompound("tag_colorizer"));
   }

   public FrozenPigment getColorizer() {
      return this.colorizer;
   }

   public void setColorizer(FrozenPigment colorizer) {
      this.colorizer = colorizer;
      this.sync();
   }
}
