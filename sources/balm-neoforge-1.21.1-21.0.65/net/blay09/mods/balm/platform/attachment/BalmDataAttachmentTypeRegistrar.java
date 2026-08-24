package net.blay09.mods.balm.platform.attachment;

import com.mojang.serialization.Codec;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public interface BalmDataAttachmentTypeRegistrar {
   default <T> BalmDataAttachmentTypeRegistration<T> register(String name, Codec<T> codec) {
      return this.register(
         name, (BiFunction<ResourceLocation, DataAttachmentTypeBuilder<T>, DataAttachmentTypeBuilder<T>>)((id, builder) -> builder.persistent(codec))
      );
   }

   default <T> BalmDataAttachmentTypeRegistration<T> register(String name, Codec<T> codec, Supplier<T> initializer) {
      return this.register(
         name,
         (BiFunction<ResourceLocation, DataAttachmentTypeBuilder<T>, DataAttachmentTypeBuilder<T>>)((id, builder) -> builder.persistent(codec)
            .initializer(initializer))
      );
   }

   default <T> BalmDataAttachmentTypeRegistration<T> register(String name, Codec<T> codec, Supplier<T> initializer, boolean copyOnDeath) {
      return this.register(name, (BiFunction<ResourceLocation, DataAttachmentTypeBuilder<T>, DataAttachmentTypeBuilder<T>>)((id, builder) -> {
         builder.persistent(codec).initializer(initializer);
         if (copyOnDeath) {
            builder.copyOnDeath();
         }

         return builder;
      }));
   }

   default <T> BalmDataAttachmentTypeRegistration<T> register(String name, Function<DataAttachmentTypeBuilder<T>, DataAttachmentTypeBuilder<T>> builderConsumer) {
      return this.register(
         name, (BiFunction<ResourceLocation, DataAttachmentTypeBuilder<T>, DataAttachmentTypeBuilder<T>>)((id, builder) -> builderConsumer.apply(builder))
      );
   }

   <T> BalmDataAttachmentTypeRegistration<T> register(
      String var1, BiFunction<ResourceLocation, DataAttachmentTypeBuilder<T>, DataAttachmentTypeBuilder<T>> var2
   );
}
