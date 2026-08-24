package dev.kosmx.playerAnim.core.util;

import java.util.function.Function;

public enum Ease {
   LINEAR(0, arg -> easeIn(f -> f)),
   CONSTANT(1, arg -> easeIn(f -> 0.0F)),
   INSINE(6, arg -> easeIn(Easing::sine)),
   OUTSINE(7, arg -> easeOut(Easing::sine)),
   INOUTSINE(8, arg -> easeInOut(Easing::sine)),
   INCUBIC(9, arg -> easeIn(Easing::cubic)),
   OUTCUBIC(10, arg -> easeOut(Easing::cubic)),
   INOUTCUBIC(11, arg -> easeInOut(Easing::cubic)),
   INQUAD(12, arg -> easeIn(Easing::quadratic)),
   OUTQUAD(13, arg -> easeOut(Easing::quadratic)),
   INOUTQUAD(14, arg -> easeInOut(Easing::quadratic)),
   INQUART(15, arg -> easeIn(Easing.pow(4.0F))),
   OUTQUART(16, arg -> easeOut(Easing.pow(4.0F))),
   INOUTQUART(17, arg -> easeInOut(Easing.pow(4.0F))),
   INQUINT(18, arg -> easeIn(Easing.pow(5.0F))),
   OUTQUINT(19, arg -> easeOut(Easing.pow(5.0F))),
   INOUTQUINT(20, arg -> easeInOut(Easing.pow(5.0F))),
   INEXPO(21, arg -> easeIn(Easing::exp)),
   OUTEXPO(22, arg -> easeOut(Easing::exp)),
   INOUTEXPO(23, arg -> easeInOut(Easing::exp)),
   INCIRC(24, arg -> easeIn(Easing::circle)),
   OUTCIRC(25, arg -> easeOut(Easing::circle)),
   INOUTCIRC(26, arg -> easeInOut(Easing::circle)),
   INBACK(27, arg -> easeIn(Easing.back(arg))),
   OUTBACK(28, arg -> easeOut(Easing.back(arg))),
   INOUTBACK(29, arg -> easeInOut(Easing.back(arg))),
   INELASTIC(30, arg -> easeIn(Easing.elastic(arg))),
   OUTELASTIC(31, arg -> easeOut(Easing.elastic(arg))),
   INOUTELASTIC(32, arg -> easeInOut(Easing.elastic(arg))),
   INBOUNCE(33, arg -> easeIn(Easing.bounce(arg))),
   OUTBOUNCE(34, arg -> easeOut(Easing.bounce(arg))),
   INOUTBOUNCE(35, arg -> easeInOut(Easing.bounce(arg))),
   CATMULLROM(36, arg -> easeInOut(Easing::catmullRom)),
   STEP(37, arg -> easeIn(Easing.step(arg)));

   final byte id;
   private final Function<Float, Function<Float, Float>> impl;

   private Ease(byte id, Function<Float, Function<Float, Float>> impl) {
      this.id = id;
      this.impl = impl;
   }

   private Ease(int id, Function<Float, Function<Float, Float>> impl) {
      this((byte)id, impl);
   }

   public float invoke(float f) {
      return this.invoke(f, null);
   }

   public float invoke(float t, Float n) {
      return this.impl.apply(n).apply(t);
   }

   public static Ease getEase(byte b) {
      for (Ease ease : values()) {
         if (ease.id == b) {
            return ease;
         }
      }

      return LINEAR;
   }

   public static Function<Float, Float> easeIn(Function<Float, Float> function) {
      return function;
   }

   public static Function<Float, Float> easeOut(Function<Float, Float> function) {
      return time -> 1.0F - function.apply(1.0F - time);
   }

   public static Function<Float, Float> easeInOut(Function<Float, Float> function) {
      return time -> time < 0.5F ? function.apply(time * 2.0F) / 2.0F : 1.0F - function.apply((1.0F - time) * 2.0F) / 2.0F;
   }

   public byte getId() {
      return this.id;
   }
}
