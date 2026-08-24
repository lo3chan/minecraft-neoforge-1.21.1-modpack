package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;
import org.jetbrains.annotations.NotNull;

public class PhasingPower extends Power {
   public static final MapCodec<PhasingPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("blacklist", false).forGetter(PhasingPower::isBlacklist),
            BlockCondition.optionalCodec("block_condition").forGetter(PhasingPower::getBlockCondition),
            PhasingPower.PhasingRenderType.CODEC
               .optionalFieldOf("render_type", PhasingPower.PhasingRenderType.BLINDNESS)
               .forGetter(PhasingPower::getRenderType),
            Codec.FLOAT.optionalFieldOf("view_distance", 10.0F).forGetter(PhasingPower::getViewDistance),
            EntityCondition.optionalCodec("phase_down_condition").forGetter(PhasingPower::getPhaseDownCondition)
         )
         .apply(i, PhasingPower::new)
   );
   private final boolean blacklist;
   private final BlockCondition blockCondition;
   private final PhasingPower.PhasingRenderType renderType;
   private final float viewDistance;
   private final EntityCondition phaseDownCondition;

   public PhasingPower(
      Power.BaseSettings settings,
      boolean blacklist,
      BlockCondition blockCondition,
      PhasingPower.PhasingRenderType renderType,
      float viewDistance,
      EntityCondition phaseDownCondition
   ) {
      super(settings);
      this.blacklist = blacklist;
      this.blockCondition = blockCondition;
      this.renderType = renderType;
      this.viewDistance = viewDistance;
      this.phaseDownCondition = phaseDownCondition;
   }

   public boolean isBlacklist() {
      return this.blacklist;
   }

   public BlockCondition getBlockCondition() {
      return this.blockCondition;
   }

   public PhasingPower.PhasingRenderType getRenderType() {
      return this.renderType;
   }

   public float getViewDistance() {
      return this.viewDistance;
   }

   public EntityCondition getPhaseDownCondition() {
      return this.phaseDownCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public static boolean shouldPhaseThrough(Entity entity, Level level, BlockPos pos, boolean isAbove) {
      return PowerHelper.get(entity).anyActive(PhasingPower.class, x -> (!isAbove || x.canPhaseDown(entity)) && x.canPhaseThrough(level, pos));
   }

   public static boolean shouldPhaseThrough(Entity entity, Level level, BlockPos pos) {
      return shouldPhaseThrough(entity, level, pos, false);
   }

   public static boolean shouldPhaseThrough(Entity entity, BlockPos pos) {
      return shouldPhaseThrough(entity, entity.level(), pos);
   }

   public static boolean hasRenderMethod(Entity entity, PhasingPower.PhasingRenderType renderType) {
      return PowerHelper.get(entity).anyActive(PhasingPower.class, x -> x.renderType == renderType);
   }

   public static Optional<Float> getRenderMethod(Entity entity, PhasingPower.PhasingRenderType renderType) {
      return PowerHelper.get(entity).streamActive(PhasingPower.class).filter(x -> x.renderType == renderType).map(x -> x.viewDistance).min(Float::compareTo);
   }

   public boolean canPhaseDown(Entity entity) {
      return this.phaseDownCondition == AlwaysTrueCondition.INSTANCE ? entity.isCrouching() : this.phaseDownCondition.test(entity);
   }

   public boolean canPhaseThrough(Level level, BlockPos pos) {
      return this.blacklist ^ this.blockCondition.test(level, pos);
   }

   public static BlockState getInWallBlockState(LivingEntity playerEntity) {
      MutableBlockPos mutable = new MutableBlockPos();

      for (int i = 0; i < 8; i++) {
         double d = playerEntity.getX() + (i % 2 - 0.5F) * playerEntity.getBbWidth() * 0.8F;
         double e = playerEntity.getEyeY() + ((i >> 1) % 2 - 0.5F) * 0.1F;
         double f = playerEntity.getZ() + ((i >> 2) % 2 - 0.5F) * playerEntity.getBbWidth() * 0.8F;
         mutable.set(d, e, f);
         BlockState blockState = playerEntity.level().getBlockState(mutable);
         if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(playerEntity.level(), mutable)) {
            return blockState;
         }
      }

      return null;
   }

   @EventBusSubscriber({Dist.CLIENT})
   public static class ClientEvents {
      @SubscribeEvent(
         priority = EventPriority.HIGHEST
      )
      public static void onBlockOverlay(RenderBlockScreenEffectEvent event) {
         if (PowerHelper.get(event.getPlayer()).anyActive(PhasingPower.class)) {
            event.setCanceled(true);
         }
      }

      @SubscribeEvent
      public static void renderFog(RenderFog event) {
         if (event.getCamera().getEntity() instanceof LivingEntity living) {
            Optional<Float> renderMethod = PhasingPower.getRenderMethod(living, PhasingPower.PhasingRenderType.BLINDNESS);
            if (renderMethod.isPresent() && PhasingPower.getInWallBlockState(living) != null) {
               float view = renderMethod.get();
               float s;
               float v;
               if (event.getMode() == FogMode.FOG_SKY) {
                  s = 0.0F;
                  v = view * 0.8F;
               } else {
                  s = view * 0.25F;
                  v = view;
               }

               RenderSystem.setShaderFogStart(s);
               RenderSystem.setShaderFogEnd(v);
            }
         }
      }

      @SubscribeEvent
      public static void fogColor(ComputeFogColor event) {
         if (event.getCamera().getEntity() instanceof LivingEntity living
            && PhasingPower.hasRenderMethod(living, PhasingPower.PhasingRenderType.BLINDNESS)
            && PhasingPower.getInWallBlockState(living) != null) {
            event.setBlue(0.0F);
            event.setGreen(0.0F);
            event.setRed(0.0F);
         }
      }
   }

   public static enum PhasingRenderType implements StringRepresentable {
      BLINDNESS,
      REMOVE_BLOCKS,
      NONE;

      public static final Codec<PhasingPower.PhasingRenderType> CODEC = StringRepresentable.fromValues(PhasingPower.PhasingRenderType::values);

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
