package tallestegg.guardvillagers.common.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import tallestegg.guardvillagers.GuardDataAttachments;
import tallestegg.guardvillagers.configuration.GuardConfig;

public class RepairGolem extends VillagerHelp {
   private LivingEntity golem;

   public RepairGolem() {
      super(
         ImmutableMap.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT),
         (List<? extends String>)GuardConfig.COMMON.professionsThatRepairGolems.get()
      );
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel worldIn, Villager owner) {
      if (owner.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES)) {
         List<LivingEntity> list = (List<LivingEntity>)owner.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).get();
         if (!list.isEmpty()) {
            for (LivingEntity golem : list) {
               if (!golem.isInvisible() && golem.isAlive() && golem.getType() == EntityType.IRON_GOLEM && golem.getHealth() <= golem.getMaxHealth() * 0.75F) {
                  this.golem = golem;
                  return super.checkExtraStartConditions(worldIn, owner);
               }
            }
         }
      }

      return false;
   }

   @Override
   protected long timeToCheck(LivingEntity owner) {
      return (Long)owner.getData(GuardDataAttachments.LAST_REPAIRED_GOLEM);
   }

   protected boolean canStillUse(ServerLevel level, Villager entity, long gameTime) {
      return (Integer)entity.getData(GuardDataAttachments.TIMES_HEALED_GOLEM) < (Integer)GuardConfig.COMMON.maxGolemRepair.get()
         && this.golem.getHealth() <= this.golem.getMaxHealth();
   }

   protected void stop(ServerLevel worldIn, Villager entityIn, long gameTimeIn) {
      if ((Integer)entityIn.getData(GuardDataAttachments.TIMES_HEALED_GOLEM) >= (Integer)GuardConfig.COMMON.maxGolemRepair.get()) {
         entityIn.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         entityIn.setData(GuardDataAttachments.LAST_REPAIRED_GOLEM.get(), worldIn.getDayTime());
         entityIn.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
         entityIn.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
         entityIn.setData(GuardDataAttachments.TIMES_HEALED_GOLEM.get(), 0);
      }
   }

   protected void start(ServerLevel worldIn, Villager entityIn, long gameTimeIn) {
      if (this.golem != null) {
         ;
      }
   }

   protected void tick(ServerLevel worldIn, Villager entityIn, long gameTimeIn) {
      this.healGolem(entityIn);
   }

   public void healGolem(Villager healer) {
      BehaviorUtils.setWalkAndLookTargetMemories(healer, this.golem, 0.5F, 0);
      if (healer.distanceTo(this.golem) <= 2.0) {
         healer.setData(GuardDataAttachments.TIMES_HEALED_GOLEM.get(), (Integer)healer.getData(GuardDataAttachments.TIMES_HEALED_GOLEM.get()) + 1);
         healer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_INGOT));
         healer.swing(InteractionHand.MAIN_HAND);
         this.golem.heal(15.0F);
         float pitch = 1.0F + (this.golem.getRandom().nextFloat() - this.golem.getRandom().nextFloat()) * 0.2F;
         this.golem.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, pitch);
      }
   }
}
