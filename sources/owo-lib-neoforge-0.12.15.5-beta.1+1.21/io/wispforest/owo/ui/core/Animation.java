package io.wispforest.owo.ui.core;

import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.Mth;

public class Animation<A extends Animatable<A>> {
   private final int duration;
   private float delta = 0.0F;
   private Animation.Direction direction = Animation.Direction.BACKWARDS;
   private boolean looping = false;
   private final Consumer<A> setter;
   private final Easing easing;
   private final A from;
   private final A to;
   private final EventStream<Animation.Finished> finishedEvents = Animation.Finished.newStream();
   private boolean eventInvoked = true;

   public Animation(int duration, Consumer<A> setter, Easing easing, A from, A to) {
      this.duration = duration;
      this.setter = setter;
      this.easing = easing;
      this.from = from;
      this.to = to;
   }

   public static Animation.Composed compose(Animation<?>... elements) {
      return new Animation.Composed(elements);
   }

   public void update(float delta) {
      if (this.delta == this.direction.targetDelta) {
         if (!this.eventInvoked) {
            this.finishedEvents.sink().onFinished(this.direction, this.looping);
            this.eventInvoked = true;
         }

         if (!this.looping) {
            return;
         }

         this.reverse();
      }

      this.delta = Mth.clamp(this.delta + delta * 50.0F / this.duration * this.direction.multiplier, 0.0F, 1.0F);
      this.setter.accept(this.from.interpolate(this.to, this.easing.apply(this.delta)));
   }

   public Animation<A> forwards() {
      this.setDirection(Animation.Direction.FORWARDS);
      return this;
   }

   public Animation<A> backwards() {
      this.setDirection(Animation.Direction.BACKWARDS);
      return this;
   }

   public Animation<A> reverse() {
      this.setDirection(this.direction.reversed());
      return this;
   }

   private void setDirection(Animation.Direction direction) {
      if (this.direction != direction) {
         this.direction = direction;
         this.eventInvoked = false;
      }
   }

   public Animation<A> loop(boolean loop) {
      this.looping = loop;
      return this;
   }

   public boolean looping() {
      return this.looping;
   }

   public Animation.Direction direction() {
      return this.direction;
   }

   public EventSource<Animation.Finished> finished() {
      return this.finishedEvents.source();
   }

   public static class Composed {
      private final List<Animation<?>> elements;

      private Composed(Animation<?>... elements) {
         this.elements = Arrays.asList(elements);
      }

      public void forwards() {
         this.elements.forEach(Animation::forwards);
      }

      public void backwards() {
         this.elements.forEach(Animation::backwards);
      }

      public void reverse() {
         this.elements.forEach(Animation::reverse);
      }

      public void loop(boolean loop) {
         this.elements.forEach(animation -> animation.loop(loop));
      }
   }

   public static enum Direction {
      FORWARDS(1, 1.0F),
      BACKWARDS(-1, 0.0F);

      public final int multiplier;
      public final float targetDelta;

      private Direction(int multiplier, float targetDelta) {
         this.multiplier = multiplier;
         this.targetDelta = targetDelta;
      }

      public Animation.Direction reversed() {
         return switch (this) {
            case FORWARDS -> BACKWARDS;
            case BACKWARDS -> FORWARDS;
         };
      }
   }

   public interface Finished {
      void onFinished(Animation.Direction var1, boolean var2);

      static EventStream<Animation.Finished> newStream() {
         return new EventStream<>(subscribers -> (direction, looping) -> {
            for (Animation.Finished subscriber : subscribers) {
               subscriber.onFinished(direction, looping);
            }
         });
      }
   }
}
