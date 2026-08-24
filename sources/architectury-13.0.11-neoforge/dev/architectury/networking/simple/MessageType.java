package dev.architectury.networking.simple;

import dev.architectury.networking.NetworkManager;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public final class MessageType {
   private final SimpleNetworkManager manager;
   private final ResourceLocation id;
   private final NetworkManager.Side side;

   MessageType(SimpleNetworkManager manager, ResourceLocation id, NetworkManager.Side side) {
      this.manager = manager;
      this.id = id;
      this.side = side;
   }

   public SimpleNetworkManager getManager() {
      return this.manager;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public NetworkManager.Side getSide() {
      return this.side;
   }

   @Override
   public String toString() {
      return this.id.toString() + ":" + this.side.name().toLowerCase();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MessageType messageType = (MessageType)o;
         return this.id.equals(messageType.id) && this.side == messageType.side;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id, this.side);
   }
}
