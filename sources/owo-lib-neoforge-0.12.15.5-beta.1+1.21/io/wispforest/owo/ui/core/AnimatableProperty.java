package io.wispforest.owo.ui.core;

import io.wispforest.owo.util.Observable;
import org.jetbrains.annotations.Nullable;

public class AnimatableProperty<A extends Animatable<A>> extends Observable<A> {
   @Nullable
   protected Animation<A> animation;

   protected AnimatableProperty(A initial) {
      super(initial);
   }

   public static <A extends Animatable<A>> AnimatableProperty<A> of(A initial) {
      return new AnimatableProperty<>(initial);
   }

   public Animation<A> animate(int duration, Easing easing, A to) {
      this.animation = new Animation<>(duration, this::set, easing, this.value, to);
      return this.animation;
   }

   @Nullable
   public Animation<A> animation() {
      return this.animation;
   }

   public void update(float delta) {
      if (this.animation != null) {
         this.animation.update(delta);
      }
   }
}
