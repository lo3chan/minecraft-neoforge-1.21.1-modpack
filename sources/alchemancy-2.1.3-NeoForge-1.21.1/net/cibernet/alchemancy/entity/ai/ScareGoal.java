package net.cibernet.alchemancy.entity.ai;

import java.util.function.Predicate;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;

public class ScareGoal extends PanicGoal {
   private static final int RANGE = 10;
   private final TargetingConditions TARGETING;
   private final Predicate<ItemStack> itemStackPredicate;

   public ScareGoal(PathfinderMob mob, double speedModifier, Predicate<ItemStack> itemStackPredicate) {
      super(mob, speedModifier);
      this.itemStackPredicate = itemStackPredicate;
      this.TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight().selector(living -> {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (itemStackPredicate.test(living.getItemBySlot(slot))) {
               return true;
            }
         }

         return false;
      });
   }

   public ScareGoal(PathfinderMob mob, double speedModifier, Holder<Property> property) {
      this(mob, speedModifier, (Predicate<ItemStack>)(stack -> InfusedPropertiesHelper.hasProperty(stack, property)));
   }

   protected boolean shouldPanic() {
      ServerLevel level = (ServerLevel)this.mob.level();
      return level.getNearestPlayer(this.TARGETING, this.mob) != null || this.isNearRootedScary(level);
   }

   protected boolean isNearRootedScary(ServerLevel level) {
      int distSqr = 100;
      return RootedItemBlockEntity.getCachedRoots(level)
         .stream()
         .anyMatch(root -> root.getBlockPos().distSqr(this.mob.blockPosition()) <= distSqr && this.itemStackPredicate.test(root.getItem()));
   }
}
