package io.wispforest.owo.client.screens;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface OwoScreenHandler {
   default ReflectiveEndecBuilder endecBuilder() {
      throw new UnsupportedOperationException("Implemented in ScreenHandlerMixin");
   }

   default <T> SyncedProperty<T> createProperty(Class<T> clazz, Endec<T> endec, T initial) {
      throw new UnsupportedOperationException("Implemented in ScreenHandlerMixin");
   }

   default <T> SyncedProperty<T> createProperty(Class<T> clazz, T initial) {
      return this.createProperty(clazz, this.endecBuilder().get(clazz), initial);
   }

   default <R extends Record> void addServerboundMessage(Class<R> messageClass, Endec<R> endec, Consumer<R> handler) {
      throw new UnsupportedOperationException("Implemented in ScreenHandlerMixin");
   }

   default <R extends Record> void addServerboundMessage(Class<R> messageClass, Consumer<R> handler) {
      this.addServerboundMessage(messageClass, this.endecBuilder().get(messageClass), handler);
   }

   default <R extends Record> void addClientboundMessage(Class<R> messageClass, Endec<R> endec, Consumer<R> handler) {
      throw new UnsupportedOperationException("Implemented in ScreenHandlerMixin");
   }

   default <R extends Record> void addClientboundMessage(Class<R> messageClass, Consumer<R> handler) {
      this.addClientboundMessage(messageClass, this.endecBuilder().get(messageClass), handler);
   }

   default <R extends Record> void sendMessage(@NotNull R message) {
      throw new UnsupportedOperationException("Implemented in ScreenHandlerMixin");
   }

   default Player player() {
      throw new UnsupportedOperationException("Implemented in ScreenHandlerMixin");
   }
}
