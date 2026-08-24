package dev.latvian.mods.kubejs.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.Nullable;

public class LivingEntityDropsKubeEvent implements KubeLivingEntityEvent {
   private final LivingDropsEvent event;
   public List<ItemEntity> eventDrops;

   public LivingEntityDropsKubeEvent(LivingDropsEvent e) {
      this.event = e;
   }

   @Override
   public LivingEntity getEntity() {
      return this.event.getEntity();
   }

   public DamageSource getSource() {
      return this.event.getSource();
   }

   public boolean isRecentlyHit() {
      return this.event.isRecentlyHit();
   }

   public List<ItemEntity> getDrops() {
      if (this.eventDrops == null) {
         this.eventDrops = new ArrayList<>(this.event.getDrops());
      }

      return this.eventDrops;
   }

   @Nullable
   public ItemEntity addDrop(ItemStack stack) {
      if (!stack.isEmpty()) {
         LivingEntity e = this.event.getEntity();
         ItemEntity ei = new ItemEntity(e.level(), e.getX(), e.getY(), e.getZ(), stack);
         ei.setPickUpDelay(10);
         this.getDrops().add(ei);
         return ei;
      } else {
         return null;
      }
   }

   @Nullable
   public ItemEntity addDrop(ItemStack stack, float chance) {
      return !(chance >= 1.0F) && !(this.event.getEntity().level().random.nextFloat() <= chance) ? null : this.addDrop(stack);
   }
}
