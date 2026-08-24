package me.lucko.spark.lib.adventure.resource;

import java.util.List;
import java.util.Objects;
import me.lucko.spark.lib.adventure.builder.AbstractBuilder;
import me.lucko.spark.lib.adventure.examination.Examinable;
import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResourcePackRequest extends Examinable, ResourcePackRequestLike {
   @NotNull
   static ResourcePackRequest addingRequest(@NotNull final ResourcePackInfoLike first, @NotNull final ResourcePackInfoLike... others) {
      return resourcePackRequest().packs(first, others).replace(false).build();
   }

   @NotNull
   static ResourcePackRequest.Builder resourcePackRequest() {
      return new ResourcePackRequestImpl.BuilderImpl();
   }

   @NotNull
   static ResourcePackRequest.Builder resourcePackRequest(@NotNull final ResourcePackRequest existing) {
      return new ResourcePackRequestImpl.BuilderImpl(Objects.requireNonNull(existing, "existing"));
   }

   @NotNull
   List<ResourcePackInfo> packs();

   @NotNull
   ResourcePackRequest packs(@NotNull final Iterable<? extends ResourcePackInfoLike> packs);

   @NotNull
   ResourcePackCallback callback();

   @NotNull
   ResourcePackRequest callback(@NotNull final ResourcePackCallback cb);

   boolean replace();

   @NotNull
   ResourcePackRequest replace(final boolean replace);

   boolean required();

   @Nullable
   Component prompt();

   @NotNull
   @Override
   default ResourcePackRequest asResourcePackRequest() {
      return this;
   }

   public interface Builder extends AbstractBuilder<ResourcePackRequest>, ResourcePackRequestLike {
      @Contract("_, _ -> this")
      @NotNull
      ResourcePackRequest.Builder packs(@NotNull final ResourcePackInfoLike first, @NotNull final ResourcePackInfoLike... others);

      @Contract("_ -> this")
      @NotNull
      ResourcePackRequest.Builder packs(@NotNull final Iterable<? extends ResourcePackInfoLike> packs);

      @Contract("_ -> this")
      @NotNull
      ResourcePackRequest.Builder callback(@NotNull final ResourcePackCallback cb);

      @Contract("_ -> this")
      @NotNull
      ResourcePackRequest.Builder replace(final boolean replace);

      @Contract("_ -> this")
      @NotNull
      ResourcePackRequest.Builder required(final boolean required);

      @Contract("_ -> this")
      @NotNull
      ResourcePackRequest.Builder prompt(@Nullable final Component prompt);

      @NotNull
      @Override
      default ResourcePackRequest asResourcePackRequest() {
         return this.build();
      }
   }
}
