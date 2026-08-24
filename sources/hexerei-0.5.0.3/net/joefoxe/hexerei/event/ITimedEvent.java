package net.joefoxe.hexerei.event;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public interface ITimedEvent {
   default void tickEvent(Post event) {
      this.tick(false);
   }

   default void tickEvent(ServerTickEvent event) {
      this.tick(true);
   }

   void tick(boolean var1);

   boolean isExpired();

   default CompoundTag serialize(CompoundTag tag) {
      if (this.getID().isEmpty()) {
         throw new IllegalStateException("Serialize without ID");
      } else {
         tag.putString("id", this.getID());
         return tag;
      }
   }

   default Void onPacketHandled() {
      EventQueue.getClientQueue().addEvent(this);
      return null;
   }

   default String getID() {
      return "";
   }
}
