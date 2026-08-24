package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.regular.ScareCreepersPower;
import com.iafenvoy.origins.mixin.accessor.MobAccessor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Mob.class})
public abstract class ScareCreepersMixin extends LivingEntity {
   private ScareCreepersMixin(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(
      method = {"<init>"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Mob;registerGoals()V",
         shift = Shift.AFTER
      )}
   )
   private void origins$modifyGoals(EntityType<?> entityType, Level level, CallbackInfo ci) {
      Mob var5 = (Mob)this;
      if (var5 instanceof Creeper thisAsCreeper) {
         origins$modifyCreeperGoals(thisAsCreeper);
      }
   }

   @Unique
   private static void origins$modifyCreeperGoals(Creeper creeper) {
      MobAccessor accessor = (MobAccessor)creeper;
      GoalSelector targetSelector = accessor.origins$getTargetSelector();
      GoalSelector goalSelector = accessor.origins$getGoalSelector();
      Predicate<LivingEntity> hasScarePower = e -> PowerHelper.get(e).anyActive(ScareCreepersPower.class);
      Iterator<WrappedGoal> oldTargetGoals = targetSelector.getAvailableGoals().iterator();
      Set<WrappedGoal> newTargetGoals = new HashSet<>();

      while (oldTargetGoals.hasNext()) {
         WrappedGoal wrappedGoal = oldTargetGoals.next();
         if (wrappedGoal.getGoal() instanceof NearestAttackableTargetGoal<?> oldGoal) {
            NearestAttackableTargetGoal<LivingEntity> newGoal = new NearestAttackableTargetGoal(
               creeper,
               oldGoal.targetType,
               oldGoal.randomInterval,
               oldGoal.mustSee,
               oldGoal.mustReach,
               e -> !hasScarePower.test(e) && (oldGoal.targetConditions.selector == null || oldGoal.targetConditions.selector.test(e))
            );
            newTargetGoals.add(new WrappedGoal(wrappedGoal.getPriority(), newGoal));
            oldTargetGoals.remove();
         }
      }

      goalSelector.addGoal(3, new AvoidEntityGoal(creeper, LivingEntity.class, hasScarePower, 6.0F, 1.0, 1.2, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
      newTargetGoals.forEach(pg -> targetSelector.addGoal(pg.getPriority(), pg.getGoal()));
   }
}
