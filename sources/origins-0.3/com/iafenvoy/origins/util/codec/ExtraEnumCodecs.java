package com.iafenvoy.origins.util.codec;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;

public final class ExtraEnumCodecs {
   public static final Codec<Dist> DIST = enumCodec(Dist.class);
   public static final Codec<SoundSource> SOUND_SOURCE = enumCodec(SoundSource.class);
   public static final Codec<Block> CLIP_CONTEXT_BLOCK = enumCodec(Block.class);
   public static final Codec<Fluid> CLIP_CONTEXT_FLUID = enumCodec(Fluid.class);
   public static final Codec<LightLayer> LIGHT_LAYER = enumCodec(LightLayer.class);
   public static final Codec<InteractionHand> HAND = enumCodec(InteractionHand.class);
   public static final Codec<InteractionResult> INTERACTION_RESULT = enumCodec(InteractionResult.class);
   public static final Codec<FogType> FOG_TYPE = enumCodec(FogType.class);
   public static final Codec<Axis> AXIS = enumCodec(Axis.class);
   public static final Codec<UseAnim> USE_ANIM = enumCodec(UseAnim.class);
   public static final Codec<ClickAction> CLICK_ACTION = enumCodec(ClickAction.class);

   public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
      return Codec.stringResolver(x -> x.name().toLowerCase(Locale.ROOT), x -> Enum.valueOf(clazz, x.toUpperCase(Locale.ROOT)));
   }
}
