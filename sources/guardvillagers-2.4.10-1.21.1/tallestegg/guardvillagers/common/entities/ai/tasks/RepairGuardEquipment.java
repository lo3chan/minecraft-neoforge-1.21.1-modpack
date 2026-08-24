package tallestegg.guardvillagers.common.entities.ai.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import tallestegg.guardvillagers.GuardDataAttachments;
import tallestegg.guardvillagers.common.entities.Guard;
import tallestegg.guardvillagers.configuration.GuardConfig;

public class RepairGuardEquipment extends VillagerHelp {
   private Guard guard;

   public RepairGuardEquipment() {
      super(
         ImmutableMap.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT),
         (List<? extends String>)GuardConfig.COMMON.professionsThatRepairGuards.get()
      );
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel worldIn, Villager owner) {
      if (owner.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES)) {
         List<LivingEntity> list = (List<LivingEntity>)owner.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).get();
         if (!list.isEmpty()) {
            for (LivingEntity livingEntity : list) {
               if (!livingEntity.isInvisible() && livingEntity.isAlive() && livingEntity instanceof Guard guard) {
                  if (owner.getVillagerData().getProfession() == VillagerProfession.ARMORER) {
                     for (int i = 0; i < guard.guardInventory.getContainerSize() - 2; i++) {
                        ItemStack itemstack = guard.guardInventory.getItem(i);
                        if (itemstack.isDamaged() && itemstack.getItem() instanceof ArmorItem && itemstack.getDamageValue() >= itemstack.getMaxDamage() / 2) {
                           this.guard = guard;
                           return super.checkExtraStartConditions(worldIn, owner);
                        }
                     }
                  } else {
                     for (int ix = 4; ix < 6; ix++) {
                        ItemStack itemstack = guard.guardInventory.getItem(ix);
                        if (itemstack.isDamaged() && itemstack.getDamageValue() >= itemstack.getMaxDamage() / 2) {
                           this.guard = guard;
                           return super.checkExtraStartConditions(worldIn, owner);
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   @Override
   protected long timeToCheck(LivingEntity owner) {
      Long timeLastRepairedGuardEquipment = (Long)owner.getData(GuardDataAttachments.LAST_REPAIRED_GUARD);
      return timeLastRepairedGuardEquipment;
   }

   protected boolean canStillUse(ServerLevel level, Villager entity, long gameTime) {
      return (Integer)entity.getData(GuardDataAttachments.TIMES_REPAIRED_GUARD) < (Integer)GuardConfig.COMMON.maxVillageRepair.get();
   }

   protected void stop(ServerLevel worldIn, Villager entityIn, long gameTimeIn) {
      if ((Integer)entityIn.getData(GuardDataAttachments.TIMES_REPAIRED_GUARD) >= (Integer)GuardConfig.COMMON.maxVillageRepair.get()) {
         entityIn.setData(GuardDataAttachments.LAST_REPAIRED_GUARD, worldIn.getDayTime());
         entityIn.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
         entityIn.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
         entityIn.setData(GuardDataAttachments.TIMES_REPAIRED_GUARD, 0);
         float pitch = 1.0F + (this.guard.getRandom().nextFloat() - this.guard.getRandom().nextFloat()) * 0.2F;
         this.guard.playSound(SoundEvents.ANVIL_USE, 1.0F, pitch);
      }
   }

   protected void start(ServerLevel worldIn, Villager entityIn, long gameTimeIn) {
   }

   protected void tick(ServerLevel worldIn, Villager entityIn, long gameTimeIn) {
      this.repairGuardEquipment(entityIn);
   }

   public void repairGuardEquipment(Villager healer) {
      BehaviorUtils.setWalkAndLookTargetMemories(healer, this.guard, 0.5F, 0);
      if (healer.distanceTo(this.guard) <= 2.0) {
         healer.setData(GuardDataAttachments.TIMES_REPAIRED_GUARD, (Integer)healer.getData(GuardDataAttachments.TIMES_REPAIRED_GUARD) + 1);
         VillagerProfession profession = healer.getVillagerData().getProfession();
         if (profession == VillagerProfession.ARMORER) {
            for (int i = 0; i < this.guard.guardInventory.getContainerSize() - 2; i++) {
               ItemStack itemstack = this.guard.guardInventory.getItem(i);
               if (itemstack.isDamaged()
                  && itemstack.getItem() instanceof ArmorItem
                  && itemstack.getDamageValue() >= itemstack.getMaxDamage() / 2 + this.guard.getRandom().nextInt(5)) {
                  itemstack.setDamageValue(itemstack.getDamageValue() - this.guard.getRandom().nextInt(5));
               }
            }
         } else {
            for (int ix = 4; ix < 6; ix++) {
               ItemStack itemstack = this.guard.guardInventory.getItem(ix);
               if (itemstack.isDamaged() && itemstack.getDamageValue() >= itemstack.getMaxDamage() / 2 + this.guard.getRandom().nextInt(5)) {
                  itemstack.setDamageValue(itemstack.getDamageValue() - this.guard.getRandom().nextInt(5));
               }
            }
         }
      }
   }
}
