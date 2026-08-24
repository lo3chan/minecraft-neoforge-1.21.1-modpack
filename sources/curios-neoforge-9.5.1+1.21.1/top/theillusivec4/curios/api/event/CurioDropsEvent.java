package top.theillusivec4.curios.api.event;

import java.util.Collection;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CurioDropsEvent extends LivingEvent implements ICancellableEvent {
   private final DamageSource source;
   private final Collection<ItemEntity> drops;
   private final int lootingLevel;
   private final boolean recentlyHit;
   private final ICuriosItemHandler curioHandler;

   public CurioDropsEvent(
      LivingEntity entity, ICuriosItemHandler handler, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit
   ) {
      super(entity);
      this.source = source;
      this.drops = drops;
      this.lootingLevel = lootingLevel;
      this.recentlyHit = recentlyHit;
      this.curioHandler = handler;
   }

   public ICuriosItemHandler getCurioHandler() {
      return this.curioHandler;
   }

   public DamageSource getSource() {
      return this.source;
   }

   public Collection<ItemEntity> getDrops() {
      return this.drops;
   }

   public int getLootingLevel() {
      return this.lootingLevel;
   }

   public boolean isRecentlyHit() {
      return this.recentlyHit;
   }
}
