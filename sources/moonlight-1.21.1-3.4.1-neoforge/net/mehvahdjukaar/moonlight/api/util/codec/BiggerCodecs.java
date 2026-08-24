package net.mehvahdjukaar.moonlight.api.util.codec;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.Kind1;
import com.mojang.datafixers.kinds.Kind1.Mu;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;

public class BiggerCodecs {
   public static <Mu extends Mu, F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> BiggerCodecs.P17<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> group(
      Kind1<F, Mu> kind,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17
   ) {
      return new BiggerCodecs.P17<>(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17);
   }

   public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> App<F, R> ap17(
      Applicative<F, ?> instance,
      App<F, BiggerCodecs.Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> func,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17
   ) {
      return instance.ap8(
         instance.ap9(instance.map(BiggerCodecs.Function17::curry9, func), t1, t2, t3, t4, t5, t6, t7, t8, t9), t10, t11, t12, t13, t14, t15, t16, t17
      );
   }

   public static <Mu extends Mu, F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> BiggerCodecs.P18<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> group(
      Kind1<F, Mu> kind,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18
   ) {
      return new BiggerCodecs.P18<>(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18);
   }

   public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> App<F, R> ap18(
      Applicative<F, ?> instance,
      App<F, BiggerCodecs.Function18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>> func,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18
   ) {
      return instance.ap9(
         instance.ap9(instance.map(BiggerCodecs.Function18::curry9, func), t1, t2, t3, t4, t5, t6, t7, t8, t9), t10, t11, t12, t13, t14, t15, t16, t17, t18
      );
   }

   public static <Mu extends Mu, F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> BiggerCodecs.P19<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> group(
      Kind1<F, Mu> kind,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18,
      App<F, T19> t19
   ) {
      return new BiggerCodecs.P19<>(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19);
   }

   public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> App<F, R> ap19(
      Applicative<F, ?> instance,
      App<F, BiggerCodecs.Function19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>> func,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18,
      App<F, T19> t19
   ) {
      return instance.ap8(
         instance.ap11(instance.map(BiggerCodecs.Function19::curry11, func), t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11),
         t12,
         t13,
         t14,
         t15,
         t16,
         t17,
         t18,
         t19
      );
   }

   public static <Mu extends Mu, F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> BiggerCodecs.P20<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> group(
      Kind1<F, Mu> kind,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18,
      App<F, T19> t19,
      App<F, T20> t20
   ) {
      return new BiggerCodecs.P20<>(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20);
   }

   public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> App<F, R> ap20(
      Applicative<F, ?> instance,
      App<F, BiggerCodecs.Function20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>> func,
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18,
      App<F, T19> t19,
      App<F, T20> t20
   ) {
      return instance.ap9(
         instance.ap11(instance.map(BiggerCodecs.Function20::curry11, func), t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11),
         t12,
         t13,
         t14,
         t15,
         t16,
         t17,
         t18,
         t19,
         t20
      );
   }

   public interface Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> {
      R apply(
         T1 var1,
         T2 var2,
         T3 var3,
         T4 var4,
         T5 var5,
         T6 var6,
         T7 var7,
         T8 var8,
         T9 var9,
         T10 var10,
         T11 var11,
         T12 var12,
         T13 var13,
         T14 var14,
         T15 var15,
         T16 var16,
         T17 var17
      );

      default Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Function8<T10, T11, T12, T13, T14, T15, T16, T17, R>> curry9() {
         return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> (t10, t11, t12, t13, t14, t15, t16, t17) -> this.apply(
            (T1)t1,
            (T2)t2,
            (T3)t3,
            (T4)t4,
            (T5)t5,
            (T6)t6,
            (T7)t7,
            (T8)t8,
            (T9)t9,
            (T10)t10,
            (T11)t11,
            (T12)t12,
            (T13)t13,
            (T14)t14,
            (T15)t15,
            (T16)t16,
            (T17)t17
         );
      }
   }

   public interface Function18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> {
      R apply(
         T1 var1,
         T2 var2,
         T3 var3,
         T4 var4,
         T5 var5,
         T6 var6,
         T7 var7,
         T8 var8,
         T9 var9,
         T10 var10,
         T11 var11,
         T12 var12,
         T13 var13,
         T14 var14,
         T15 var15,
         T16 var16,
         T17 var17,
         T18 var18
      );

      default Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Function9<T10, T11, T12, T13, T14, T15, T16, T17, T18, R>> curry9() {
         return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> (t10, t11, t12, t13, t14, t15, t16, t17, t18) -> this.apply(
            (T1)t1,
            (T2)t2,
            (T3)t3,
            (T4)t4,
            (T5)t5,
            (T6)t6,
            (T7)t7,
            (T8)t8,
            (T9)t9,
            (T10)t10,
            (T11)t11,
            (T12)t12,
            (T13)t13,
            (T14)t14,
            (T15)t15,
            (T16)t16,
            (T17)t17,
            (T18)t18
         );
      }
   }

   public interface Function19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> {
      R apply(
         T1 var1,
         T2 var2,
         T3 var3,
         T4 var4,
         T5 var5,
         T6 var6,
         T7 var7,
         T8 var8,
         T9 var9,
         T10 var10,
         T11 var11,
         T12 var12,
         T13 var13,
         T14 var14,
         T15 var15,
         T16 var16,
         T17 var17,
         T18 var18,
         T19 var19
      );

      default Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Function8<T12, T13, T14, T15, T16, T17, T18, T19, R>> curry11() {
         return (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) -> (t12, t13, t14, t15, t16, t17, t18, t19) -> this.apply(
            (T1)t1,
            (T2)t2,
            (T3)t3,
            (T4)t4,
            (T5)t5,
            (T6)t6,
            (T7)t7,
            (T8)t8,
            (T9)t9,
            (T10)t10,
            (T11)t11,
            (T12)t12,
            (T13)t13,
            (T14)t14,
            (T15)t15,
            (T16)t16,
            (T17)t17,
            (T18)t18,
            (T19)t19
         );
      }
   }

   public interface Function20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> {
      R apply(
         T1 var1,
         T2 var2,
         T3 var3,
         T4 var4,
         T5 var5,
         T6 var6,
         T7 var7,
         T8 var8,
         T9 var9,
         T10 var10,
         T11 var11,
         T12 var12,
         T13 var13,
         T14 var14,
         T15 var15,
         T16 var16,
         T17 var17,
         T18 var18,
         T19 var19,
         T20 var20
      );

      default Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Function9<T12, T13, T14, T15, T16, T17, T18, T19, T20, R>> curry11() {
         return (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) -> (t12, t13, t14, t15, t16, t17, t18, t19, t20) -> this.apply(
            (T1)t1,
            (T2)t2,
            (T3)t3,
            (T4)t4,
            (T5)t5,
            (T6)t6,
            (T7)t7,
            (T8)t8,
            (T9)t9,
            (T10)t10,
            (T11)t11,
            (T12)t12,
            (T13)t13,
            (T14)t14,
            (T15)t15,
            (T16)t16,
            (T17)t17,
            (T18)t18,
            (T19)t19,
            (T20)t20
         );
      }
   }

   public record P17<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>(
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17
   ) {
      public <R> App<F, R> apply(
         Applicative<F, ?> instance, BiggerCodecs.Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> function
      ) {
         return this.apply(instance, instance.point(function));
      }

      public <R> App<F, R> apply(
         Applicative<F, ?> instance, App<F, BiggerCodecs.Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>> function
      ) {
         return BiggerCodecs.ap17(
            instance,
            function,
            this.t1,
            this.t2,
            this.t3,
            this.t4,
            this.t5,
            this.t6,
            this.t7,
            this.t8,
            this.t9,
            this.t10,
            this.t11,
            this.t12,
            this.t13,
            this.t14,
            this.t15,
            this.t16,
            this.t17
         );
      }
   }

   public record P18<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>(
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18
   ) {
      public <R> App<F, R> apply(
         Applicative<F, ?> instance, BiggerCodecs.Function18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> function
      ) {
         return this.apply(instance, instance.point(function));
      }

      public <R> App<F, R> apply(
         Applicative<F, ?> instance,
         App<F, BiggerCodecs.Function18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>> function
      ) {
         return BiggerCodecs.ap18(
            instance,
            function,
            this.t1,
            this.t2,
            this.t3,
            this.t4,
            this.t5,
            this.t6,
            this.t7,
            this.t8,
            this.t9,
            this.t10,
            this.t11,
            this.t12,
            this.t13,
            this.t14,
            this.t15,
            this.t16,
            this.t17,
            this.t18
         );
      }
   }

   public record P19<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>(
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18,
      App<F, T19> t19
   ) {
      public <R> App<F, R> apply(
         Applicative<F, ?> instance, BiggerCodecs.Function19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> function
      ) {
         return this.apply(instance, instance.point(function));
      }

      public <R> App<F, R> apply(
         Applicative<F, ?> instance,
         App<F, BiggerCodecs.Function19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>> function
      ) {
         return BiggerCodecs.ap19(
            instance,
            function,
            this.t1,
            this.t2,
            this.t3,
            this.t4,
            this.t5,
            this.t6,
            this.t7,
            this.t8,
            this.t9,
            this.t10,
            this.t11,
            this.t12,
            this.t13,
            this.t14,
            this.t15,
            this.t16,
            this.t17,
            this.t18,
            this.t19
         );
      }
   }

   public record P20<F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>(
      App<F, T1> t1,
      App<F, T2> t2,
      App<F, T3> t3,
      App<F, T4> t4,
      App<F, T5> t5,
      App<F, T6> t6,
      App<F, T7> t7,
      App<F, T8> t8,
      App<F, T9> t9,
      App<F, T10> t10,
      App<F, T11> t11,
      App<F, T12> t12,
      App<F, T13> t13,
      App<F, T14> t14,
      App<F, T15> t15,
      App<F, T16> t16,
      App<F, T17> t17,
      App<F, T18> t18,
      App<F, T19> t19,
      App<F, T20> t20
   ) {
      public <R> App<F, R> apply(
         Applicative<F, ?> instance,
         BiggerCodecs.Function20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> function
      ) {
         return this.apply(instance, instance.point(function));
      }

      public <R> App<F, R> apply(
         Applicative<F, ?> instance,
         App<F, BiggerCodecs.Function20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>> function
      ) {
         return BiggerCodecs.ap20(
            instance,
            function,
            this.t1,
            this.t2,
            this.t3,
            this.t4,
            this.t5,
            this.t6,
            this.t7,
            this.t8,
            this.t9,
            this.t10,
            this.t11,
            this.t12,
            this.t13,
            this.t14,
            this.t15,
            this.t16,
            this.t17,
            this.t18,
            this.t19,
            this.t20
         );
      }
   }
}
