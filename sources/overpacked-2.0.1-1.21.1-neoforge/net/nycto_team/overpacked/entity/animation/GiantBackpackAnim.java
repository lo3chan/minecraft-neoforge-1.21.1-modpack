package net.nycto_team.overpacked.entity.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantBackpackAnim {
   public static final AnimationDefinition big_cell_open = Builder.withLength(1.4583F)
      .addAnimation(
         "big_cell",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5F, KeyframeAnimations.degreeVec(-50.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.75F, KeyframeAnimations.degreeVec(-37.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(1.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .addAnimation(
         "left_belt",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.4583F, KeyframeAnimations.degreeVec(40.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.6667F, KeyframeAnimations.degreeVec(-30.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.9167F, KeyframeAnimations.degreeVec(52.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(1.0417F, KeyframeAnimations.degreeVec(50.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(1.2083F, KeyframeAnimations.degreeVec(37.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(1.375F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .addAnimation(
         "right_belt",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.4583F, KeyframeAnimations.degreeVec(35.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.6667F, KeyframeAnimations.degreeVec(-25.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.8333F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.9583F, KeyframeAnimations.degreeVec(50.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(1.125F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(1.2917F, KeyframeAnimations.degreeVec(42.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(1.4583F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .addAnimation(
         "body",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.3333F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.7917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .build();
   public static final AnimationDefinition big_cell_close = Builder.withLength(0.75F)
      .addAnimation(
         "big_cell",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.4167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .addAnimation(
         "left_belt",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.125F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.375F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5417F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .addAnimation(
         "right_belt",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.125F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.375F, KeyframeAnimations.degreeVec(-0.5F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .addAnimation(
         "body",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.2083F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.3333F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.4167F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .build();
   public static final AnimationDefinition right_cell_open = Builder.withLength(1.0F)
      .addAnimation(
         "right_cell_rot",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -50.0F), Interpolations.CATMULLROM),
               new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -37.5F), Interpolations.CATMULLROM),
               new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -45.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .build();
   public static final AnimationDefinition right_cell_close = Builder.withLength(0.5F)
      .addAnimation(
         "right_cell_rot",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -45.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 5.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .build();
   public static final AnimationDefinition left_cell_open = Builder.withLength(1.0F)
      .addAnimation(
         "left_cell_rot",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 50.0F), Interpolations.CATMULLROM),
               new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 37.5F), Interpolations.CATMULLROM),
               new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 45.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .build();
   public static final AnimationDefinition left_cell_close = Builder.withLength(0.5F)
      .addAnimation(
         "left_cell_rot",
         new AnimationChannel(
            Targets.ROTATION,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 45.0F), Interpolations.CATMULLROM),
               new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), Interpolations.CATMULLROM),
               new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), Interpolations.CATMULLROM)
            }
         )
      )
      .build();
}
