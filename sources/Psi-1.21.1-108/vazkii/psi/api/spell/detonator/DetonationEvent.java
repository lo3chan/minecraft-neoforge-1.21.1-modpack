package vazkii.psi.api.spell.detonator;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class DetonationEvent extends Event implements ICancellableEvent {
   private final Player player;
   private final Entity focalPoint;
   private final double range;
   private final List<IDetonationHandler> charges;

   public DetonationEvent(Player player, Entity focalPoint, double range, List<IDetonationHandler> charges) {
      this.player = player;
      this.focalPoint = focalPoint;
      this.range = range;
      this.charges = charges;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Entity getFocalPoint() {
      return this.focalPoint;
   }

   public double getRange() {
      return this.range;
   }

   public List<IDetonationHandler> getCharges() {
      return this.charges;
   }

   public void addCharge(IDetonationHandler charge) {
      this.charges.add(charge);
   }

   public void removeCharge(IDetonationHandler charge) {
      this.charges.remove(charge);
   }
}
