package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.rhino.WrappedException;
import org.jetbrains.annotations.Nullable;

public class EventHandlerContainer {
   public final Object target;
   public final IEventHandler handler;
   public final String source;
   public final int line;
   EventHandlerContainer child;

   public static boolean isEmpty(@Nullable EventHandlerContainer[] array) {
      if (array == null) {
         return true;
      } else {
         for (EventHandlerContainer c : array) {
            if (c != null) {
               return false;
            }
         }

         return true;
      }
   }

   public EventHandlerContainer(Object target, IEventHandler handler, String source, int line) {
      this.target = target;
      this.handler = handler;
      this.source = source;
      this.line = line;
   }

   public EventResult handle(ConsoleJS console, EventHandler handler, KubeEvent event) throws EventExit {
      EventHandlerContainer itr = this;

      do {
         try {
            itr.handler.onEvent(event);
         } catch (EventExit var8) {
            if (handler.getResult() != null) {
               throw var8;
            }

            console.error("Error in '" + this + "': Event returned result when it's not cancellable");
         } catch (Throwable var9) {
            Throwable throwable = var9;

            while (throwable instanceof WrappedException) {
               WrappedException e = (WrappedException)throwable;
               throwable = e.getWrappedException();
            }

            if (throwable instanceof EventExit exit) {
               if (handler.getResult() != null) {
                  throw exit;
               }

               console.error("Error in '" + this + "': Event returned result when it's not cancellable");
            }

            if (handler.exceptionHandler == null || (throwable = handler.exceptionHandler.handle(event, itr, throwable)) != null) {
               console.error("Error in '" + handler + "'", throwable);
               if (DevProperties.get().logEventErrorStackTrace) {
                  throwable.printStackTrace();
               }
            }
         }

         itr = itr.child;
      } while (itr != null);

      return EventResult.PASS;
   }

   public void add(Object extraId, IEventHandler handler, String source, int line) {
      EventHandlerContainer itr = this;

      while (itr.child != null) {
         itr = itr.child;
      }

      itr.child = new EventHandlerContainer(extraId, handler, source, line);
   }

   @Override
   public String toString() {
      return "Event Handler (" + this.source + ":" + this.line + ")";
   }
}
