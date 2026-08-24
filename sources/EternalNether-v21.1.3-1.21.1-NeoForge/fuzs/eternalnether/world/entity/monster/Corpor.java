package fuzs.eternalnether.world.entity.monster;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Corpor extends WitherSkeleton {
   public Corpor(EntityType<? extends WitherSkeleton> entityType, Level level) {
      super(entityType, level);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.28)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
         .add(Attributes.ATTACK_DAMAGE, 4.0);
   }

   public void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
   }
}
