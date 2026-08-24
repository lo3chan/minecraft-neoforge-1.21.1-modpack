package dev.isxander.yacl3.api;

@FunctionalInterface
public interface OptionEventListener<T> {
   void onEvent(Option<T> var1, OptionEventListener.Event var2);

   public static enum Event {
      INITIAL,
      STATE_CHANGE,
      AVAILABILITY_CHANGE,
      OTHER;
   }
}
