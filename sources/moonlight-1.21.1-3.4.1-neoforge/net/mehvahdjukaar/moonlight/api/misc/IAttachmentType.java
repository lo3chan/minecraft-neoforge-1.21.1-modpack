package net.mehvahdjukaar.moonlight.api.misc;

import org.jetbrains.annotations.Nullable;

public interface IAttachmentType<A, T> {
   A getOrCreate(T var1);

   @Nullable
   A getOrNull(T var1);

   void set(T var1, @Nullable A var2);

   void sync(T var1);
}
