package net.joefoxe.hexerei.item.custom;

import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.client.renderer.entity.BroomType;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomMediumSatchelModel;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class BroomBrushItem extends BroomTickableAttachmentItem {
   public Model model = null;
   public ResourceLocation texture;
   public ResourceLocation dye_texture;
   public List<Tuple<ParticleOptions, Integer>> list = null;

   public BroomBrushItem(Properties properties) {
      super(properties);
   }

   @Override
   public void tick(BroomEntity broom, ItemStack stack) {
      super.tick(broom, stack);
   }

   @Override
   public void renderParticles(BroomEntity broom, Level level, BroomEntity.Status status, RandomSource random) {
      if (this.list != null) {
         BroomType broomType = broom.getBroomType();
         if (broomType.item() instanceof BroomItem broomItem) {
            for (Tuple<ParticleOptions, Integer> tuple : this.list) {
               ParticleOptions option = (ParticleOptions)tuple.getA();
               int delay = (Integer)tuple.getB();
               if (random.nextInt(delay) == 0) {
                  float rotOffset = random.nextFloat() * 10.0F - 5.0F;
                  level.addParticle(
                     option,
                     broom.xOld
                        + broomItem.getBrushOffset().x()
                        - Math.sin((broom.getYRot() - 90.0F + rotOffset) / 180.0F * 3.141592653589793) * 1.25
                        + 0.25F * random.nextFloat()
                        - 0.125,
                     broom.yOld + broomItem.getBrushOffset().y() + broom.floatingOffsetOld - broom.deltaMovementOld.y() + 0.25F * random.nextFloat(),
                     broom.zOld
                        + broomItem.getBrushOffset().z()
                        + Math.cos((broom.getYRot() - 90.0F + rotOffset) / 180.0F * 3.141592653589793) * 1.25
                        + 0.25F * random.nextFloat()
                        - 0.125,
                     (random.nextDouble() - 0.5) * 0.015,
                     (random.nextDouble() - 0.5) * 0.015,
                     (random.nextDouble() - 0.5) * 0.015
                  );
               }
            }
         }
      }
   }

   public boolean shouldGlow(@Nullable Level level, ItemStack brushStack) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void bakeModels() {
      EntityModelSet context = Minecraft.getInstance().getEntityModels();
      this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
      this.texture = HexereiUtil.getResource("textures/entity/broom_satchel.png");
      this.dye_texture = HexereiUtil.getResource("textures/entity/broom_satchel_dye.png");
      this.list = new ArrayList<>();
      this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM.get(), 50));
      this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_2.get(), 20));
      this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_3.get(), 80));
      this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_4.get(), 500));
      this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_5.get(), 500));
      this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_6.get(), 500));
   }

   public float getSpeedModifier() {
      return 0.0F;
   }

   public float getSpeedModifier(BroomEntity broom) {
      return this.getSpeedModifier();
   }
}
