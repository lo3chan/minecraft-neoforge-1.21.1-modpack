package io.wispforest.owo.client.screens;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.SerializationAttribute.Instance;
import io.wispforest.owo.serialization.RegistriesAttribute;
import io.wispforest.owo.util.Observable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus.Internal;

public class SyncedProperty<T> extends Observable<T> {
   private final int index;
   private final Endec<T> endec;
   private final AbstractContainerMenu owner;
   private boolean needsSync;

   @Internal
   public SyncedProperty(int index, Endec<T> endec, T initial, AbstractContainerMenu owner) {
      super(initial);
      this.index = index;
      this.endec = endec;
      this.owner = owner;
   }

   public int index() {
      return this.index;
   }

   @Internal
   public boolean needsSync() {
      return this.needsSync;
   }

   @Internal
   public void write(FriendlyByteBuf buf) {
      this.needsSync = false;
      buf.write(this.serializationContext(), this.endec, this.value);
   }

   @Internal
   public void read(FriendlyByteBuf buf) {
      this.set((T)buf.read(this.serializationContext(), this.endec));
   }

   @Override
   protected void notifyObservers(T value) {
      super.notifyObservers(value);
      this.needsSync = true;
   }

   public void markDirty() {
      this.notifyObservers(this.value);
   }

   private SerializationContext serializationContext() {
      Player player = this.owner.player();
      return player == null ? SerializationContext.empty() : SerializationContext.attributes(new Instance[]{RegistriesAttribute.of(player.registryAccess())});
   }
}
