package com.iafenvoy.origins.data.action.builtin.entity;

import com.iafenvoy.origins.data._common.RaycastSettings;
import com.iafenvoy.origins.data._common.helper.CommandHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.BlockAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;

public record RaycastAction(
   RaycastSettings settings,
   EntityAction beforeAction,
   BiEntityCondition biEntityCondition,
   RaycastAction.CommandInfo commandInfo,
   RaycastAction.HitAction action
) implements EntityAction, CommandHelper {
   public static final MapCodec<RaycastAction> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            RaycastSettings.CODEC.forGetter(RaycastAction::settings),
            EntityAction.optionalCodec("before_action").forGetter(RaycastAction::beforeAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(RaycastAction::biEntityCondition),
            RaycastAction.CommandInfo.MAP_CODEC.forGetter(RaycastAction::commandInfo),
            RaycastAction.HitAction.MAP_CODEC.forGetter(RaycastAction::action)
         )
         .apply(instance, RaycastAction::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityAction> codec() {
      return CODEC;
   }

   @Override
   public void execute(@NotNull Entity source) {
      this.beforeAction().execute(source);
      Vec3 direction = source.getViewVector(1.0F);
      Vec3 origin = new Vec3(source.getX(), source.getEyeY(), source.getZ());
      HitResult hitResult = this.settings().perform(source, origin, direction, this.biEntityCondition());
      RaycastAction.CommandInfo commandInfo = this.commandInfo();
      RaycastAction.HitAction actions = this.action();
      if (hitResult.getType() != Type.MISS) {
         if (commandInfo.commandAtHit().isPresent()) {
            Vec3 offsetDirection = direction;
            double offset = 0.0;
            Vec3 hitPos = hitResult.getLocation();
            if (commandInfo.commandHitOffset().isPresent()) {
               offset = commandInfo.commandHitOffset().get();
            } else {
               if (hitResult instanceof BlockHitResult bhr) {
                  if (bhr.getDirection() == Direction.DOWN) {
                     offset = source.getBbHeight();
                  } else if (bhr.getDirection() == Direction.UP) {
                     offset = 0.0;
                  } else {
                     offset = source.getBbWidth() / 2.0F;
                     offsetDirection = new Vec3(-bhr.getDirection().getStepX(), -bhr.getDirection().getStepY(), -bhr.getDirection().getStepZ()).reverse();
                  }
               }

               offset += 0.05;
            }

            Vec3 at = hitPos.subtract(offsetDirection.scale(offset));
            this.executeCommand(source, at, commandInfo.commandAtHit().get());
         }

         if (commandInfo.commandAlongRay().isPresent()) {
            this.executeStepCommands(source, origin, hitResult.getLocation(), commandInfo.commandAlongRay().get(), commandInfo.commandStep());
         }

         if (hitResult instanceof BlockHitResult bhrx) {
            actions.blockAction().execute(source.level(), bhrx.getBlockPos(), Optional.of(bhrx.getDirection()));
         }

         if (hitResult instanceof EntityHitResult ehr) {
            actions.biEntityAction().execute(source, ehr.getEntity());
         }

         actions.hitAction().execute(source);
      } else {
         if (commandInfo.commandAlongRay().isPresent() && !commandInfo.commandAlongRayOnlyOnHit()) {
            this.executeStepCommands(source, origin, hitResult.getLocation(), commandInfo.commandAlongRay().get(), commandInfo.commandStep());
         }

         actions.missAction().execute(source);
      }
   }

   private void executeStepCommands(Entity entity, Vec3 origin, Vec3 target, String command, double step) {
      Vec3 direction = target.subtract(origin).normalize();
      double length = origin.distanceTo(target);
      double current = 0.0;

      while (current < length) {
         this.executeCommand(entity, origin.add(direction.scale(current)), command);
         current += step;
      }
   }

   public record CommandInfo(
      Optional<String> commandAtHit, Optional<Double> commandHitOffset, Optional<String> commandAlongRay, double commandStep, boolean commandAlongRayOnlyOnHit
   ) {
      private static final MapCodec<RaycastAction.CommandInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.optionalFieldOf("command_at_hit").forGetter(RaycastAction.CommandInfo::commandAtHit),
               Codec.DOUBLE.optionalFieldOf("command_hit_offset").forGetter(RaycastAction.CommandInfo::commandHitOffset),
               Codec.STRING.optionalFieldOf("command_along_ray").forGetter(RaycastAction.CommandInfo::commandAlongRay),
               Codec.DOUBLE.optionalFieldOf("command_step", 1.0).forGetter(RaycastAction.CommandInfo::commandStep),
               Codec.BOOL.optionalFieldOf("command_along_ray_only_on_hit", false).forGetter(RaycastAction.CommandInfo::commandAlongRayOnlyOnHit)
            )
            .apply(instance, RaycastAction.CommandInfo::new)
      );
   }

   public record HitAction(BlockAction blockAction, EntityAction hitAction, EntityAction missAction, BiEntityAction biEntityAction) {
      private static final MapCodec<RaycastAction.HitAction> MAP_CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               BlockAction.optionalCodec("block_action").forGetter(RaycastAction.HitAction::blockAction),
               EntityAction.optionalCodec("hit_action").forGetter(RaycastAction.HitAction::hitAction),
               EntityAction.optionalCodec("miss_action").forGetter(RaycastAction.HitAction::missAction),
               BiEntityAction.optionalCodec("bientity_action").forGetter(RaycastAction.HitAction::biEntityAction)
            )
            .apply(instance, RaycastAction.HitAction::new)
      );
   }
}
