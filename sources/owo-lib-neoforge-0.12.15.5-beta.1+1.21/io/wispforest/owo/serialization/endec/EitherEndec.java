package io.wispforest.owo.serialization.endec;

import com.mojang.datafixers.util.Either;
import io.wispforest.endec.Deserializer;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SelfDescribedDeserializer;
import io.wispforest.endec.SelfDescribedSerializer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer;
import io.wispforest.endec.Serializer.Struct;

public final class EitherEndec<L, R> implements Endec<Either<L, R>> {
   private final Endec<L> leftEndec;
   private final Endec<R> rightEndec;
   private final boolean exclusive;

   public EitherEndec(Endec<L> leftEndec, Endec<R> rightEndec, boolean exclusive) {
      this.leftEndec = leftEndec;
      this.rightEndec = rightEndec;
      this.exclusive = exclusive;
   }

   public void encode(SerializationContext ctx, Serializer<?> serializer, Either<L, R> either) {
      if (serializer instanceof SelfDescribedSerializer) {
         either.ifLeft(left -> this.leftEndec.encode(ctx, serializer, left)).ifRight(right -> this.rightEndec.encode(ctx, serializer, right));
      } else {
         either.ifLeft(left -> {
            Struct struct = serializer.struct();

            try {
               struct.field("is_left", ctx, Endec.BOOLEAN, true).field("left", ctx, this.leftEndec, left);
            } catch (Throwable var8) {
               if (struct != null) {
                  try {
                     struct.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (struct != null) {
               struct.close();
            }
         }).ifRight(right -> {
            Struct struct = serializer.struct();

            try {
               struct.field("is_left", ctx, Endec.BOOLEAN, false).field("right", ctx, this.rightEndec, right);
            } catch (Throwable var8) {
               if (struct != null) {
                  try {
                     struct.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (struct != null) {
               struct.close();
            }
         });
      }
   }

   public Either<L, R> decode(SerializationContext ctx, Deserializer<?> deserializer) {
      boolean selfDescribing = deserializer instanceof SelfDescribedDeserializer;
      if (selfDescribing) {
         Either<L, R> leftResult = null;

         try {
            leftResult = Either.left(deserializer.tryRead(deserializer1 -> this.leftEndec.decode(ctx, deserializer1)));
         } catch (Exception var8) {
         }

         if (!this.exclusive && leftResult != null) {
            return leftResult;
         } else {
            Either<L, R> rightResult = null;

            try {
               rightResult = Either.right(deserializer.tryRead(deserializer1 -> this.rightEndec.decode(ctx, deserializer1)));
            } catch (Exception var7) {
            }

            if (this.exclusive && leftResult != null && rightResult != null) {
               throw new IllegalStateException(
                  "Both alternatives read successfully, can not pick the correct one; first: " + leftResult + " second: " + rightResult
               );
            } else if (leftResult != null) {
               return leftResult;
            } else if (rightResult != null) {
               return rightResult;
            } else {
               throw new IllegalStateException("Neither alternative read successfully");
            }
         }
      } else {
         io.wispforest.endec.Deserializer.Struct struct = deserializer.struct();
         return struct.field("is_left", ctx, Endec.BOOLEAN)
            ? Either.left(struct.field("left", ctx, this.leftEndec))
            : Either.right(struct.field("right", ctx, this.rightEndec));
      }
   }
}
