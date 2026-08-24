package me.lucko.spark.lib.adventure.audience;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import me.lucko.spark.lib.adventure.bossbar.BossBar;
import me.lucko.spark.lib.adventure.chat.ChatType;
import me.lucko.spark.lib.adventure.chat.SignedMessage;
import me.lucko.spark.lib.adventure.identity.Identified;
import me.lucko.spark.lib.adventure.identity.Identity;
import me.lucko.spark.lib.adventure.inventory.Book;
import me.lucko.spark.lib.adventure.pointer.Pointered;
import me.lucko.spark.lib.adventure.resource.ResourcePackInfo;
import me.lucko.spark.lib.adventure.resource.ResourcePackInfoLike;
import me.lucko.spark.lib.adventure.resource.ResourcePackRequest;
import me.lucko.spark.lib.adventure.resource.ResourcePackRequestLike;
import me.lucko.spark.lib.adventure.sound.Sound;
import me.lucko.spark.lib.adventure.sound.SoundStop;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.ComponentLike;
import me.lucko.spark.lib.adventure.title.Title;
import me.lucko.spark.lib.adventure.title.TitlePart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public interface Audience extends Pointered {
   @NotNull
   static Audience empty() {
      return EmptyAudience.INSTANCE;
   }

   @NotNull
   static Audience audience(@NotNull final Audience... audiences) {
      int length = audiences.length;
      if (length == 0) {
         return empty();
      } else {
         return (Audience)(length == 1 ? audiences[0] : audience(Arrays.asList(audiences)));
      }
   }

   @NotNull
   static ForwardingAudience audience(@NotNull final Iterable<? extends Audience> audiences) {
      return () -> audiences;
   }

   @NotNull
   static Collector<? super Audience, ?, ForwardingAudience> toAudience() {
      return Audiences.COLLECTOR;
   }

   @NotNull
   default Audience filterAudience(@NotNull final Predicate<? super Audience> filter) {
      return filter.test(this) ? this : empty();
   }

   default void forEachAudience(@NotNull final Consumer<? super Audience> action) {
      action.accept(this);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull final ComponentLike message) {
      this.sendMessage(message.asComponent());
   }

   default void sendMessage(@NotNull final Component message) {
      this.sendMessage(message, MessageType.SYSTEM);
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   @ScheduledForRemoval(
      inVersion = "5.0.0"
   )
   default void sendMessage(@NotNull final ComponentLike message, @NotNull final MessageType type) {
      this.sendMessage(message.asComponent(), type);
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   @ScheduledForRemoval(
      inVersion = "5.0.0"
   )
   default void sendMessage(@NotNull final Component message, @NotNull final MessageType type) {
      this.sendMessage(Identity.nil(), message, type);
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull final Identified source, @NotNull final ComponentLike message) {
      this.sendMessage(source, message.asComponent());
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull final Identity source, @NotNull final ComponentLike message) {
      this.sendMessage(source, message.asComponent());
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull final Identified source, @NotNull final Component message) {
      this.sendMessage(source, message, MessageType.CHAT);
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull final Identity source, @NotNull final Component message) {
      this.sendMessage(source, message, MessageType.CHAT);
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   @ScheduledForRemoval(
      inVersion = "5.0.0"
   )
   default void sendMessage(@NotNull final Identified source, @NotNull final ComponentLike message, @NotNull final MessageType type) {
      this.sendMessage(source, message.asComponent(), type);
   }

   @Deprecated
   @ForwardingAudienceOverrideNotRequired
   @ScheduledForRemoval(
      inVersion = "5.0.0"
   )
   default void sendMessage(@NotNull final Identity source, @NotNull final ComponentLike message, @NotNull final MessageType type) {
      this.sendMessage(source, message.asComponent(), type);
   }

   @Deprecated
   @ScheduledForRemoval(
      inVersion = "5.0.0"
   )
   default void sendMessage(@NotNull final Identified source, @NotNull final Component message, @NotNull final MessageType type) {
      this.sendMessage(source.identity(), message, type);
   }

   @Deprecated
   @ScheduledForRemoval(
      inVersion = "5.0.0"
   )
   default void sendMessage(@NotNull final Identity source, @NotNull final Component message, @NotNull final MessageType type) {
   }

   default void sendMessage(@NotNull final Component message, @NotNull final ChatType.Bound boundChatType) {
      this.sendMessage(message, MessageType.CHAT);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendMessage(@NotNull final ComponentLike message, @NotNull final ChatType.Bound boundChatType) {
      this.sendMessage(message.asComponent(), boundChatType);
   }

   default void sendMessage(@NotNull final SignedMessage signedMessage, @NotNull final ChatType.Bound boundChatType) {
      Component content = (Component)(signedMessage.unsignedContent() != null ? signedMessage.unsignedContent() : Component.text(signedMessage.message()));
      if (signedMessage.isSystem()) {
         this.sendMessage(content);
      } else {
         this.sendMessage(signedMessage.identity(), content, MessageType.CHAT);
      }
   }

   @ForwardingAudienceOverrideNotRequired
   default void deleteMessage(@NotNull final SignedMessage signedMessage) {
      if (signedMessage.canDelete()) {
         this.deleteMessage(Objects.requireNonNull(signedMessage.signature()));
      }
   }

   default void deleteMessage(@NotNull final SignedMessage.Signature signature) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendActionBar(@NotNull final ComponentLike message) {
      this.sendActionBar(message.asComponent());
   }

   default void sendActionBar(@NotNull final Component message) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendPlayerListHeader(@NotNull final ComponentLike header) {
      this.sendPlayerListHeader(header.asComponent());
   }

   default void sendPlayerListHeader(@NotNull final Component header) {
      this.sendPlayerListHeaderAndFooter(header, Component.empty());
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendPlayerListFooter(@NotNull final ComponentLike footer) {
      this.sendPlayerListFooter(footer.asComponent());
   }

   default void sendPlayerListFooter(@NotNull final Component footer) {
      this.sendPlayerListHeaderAndFooter(Component.empty(), footer);
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendPlayerListHeaderAndFooter(@NotNull final ComponentLike header, @NotNull final ComponentLike footer) {
      this.sendPlayerListHeaderAndFooter(header.asComponent(), footer.asComponent());
   }

   default void sendPlayerListHeaderAndFooter(@NotNull final Component header, @NotNull final Component footer) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void showTitle(@NotNull final Title title) {
      Title.Times times = title.times();
      if (times != null) {
         this.sendTitlePart(TitlePart.TIMES, times);
      }

      this.sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
      this.sendTitlePart(TitlePart.TITLE, title.title());
   }

   default <T> void sendTitlePart(@NotNull final TitlePart<T> part, @NotNull final T value) {
   }

   default void clearTitle() {
   }

   default void resetTitle() {
   }

   default void showBossBar(@NotNull final BossBar bar) {
   }

   default void hideBossBar(@NotNull final BossBar bar) {
   }

   default void playSound(@NotNull final Sound sound) {
   }

   default void playSound(@NotNull final Sound sound, final double x, final double y, final double z) {
   }

   default void playSound(@NotNull final Sound sound, @NotNull final Sound.Emitter emitter) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void stopSound(@NotNull final Sound sound) {
      this.stopSound(Objects.requireNonNull(sound, "sound").asStop());
   }

   default void stopSound(@NotNull final SoundStop stop) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void openBook(@NotNull final Book.Builder book) {
      this.openBook(book.build());
   }

   default void openBook(@NotNull final Book book) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendResourcePacks(@NotNull final ResourcePackInfoLike first, @NotNull final ResourcePackInfoLike... others) {
      this.sendResourcePacks(ResourcePackRequest.addingRequest(first, others));
   }

   @ForwardingAudienceOverrideNotRequired
   default void sendResourcePacks(@NotNull final ResourcePackRequestLike request) {
      this.sendResourcePacks(request.asResourcePackRequest());
   }

   default void sendResourcePacks(@NotNull final ResourcePackRequest request) {
   }

   @ForwardingAudienceOverrideNotRequired
   default void removeResourcePacks(@NotNull final ResourcePackRequestLike request) {
      this.removeResourcePacks(request.asResourcePackRequest());
   }

   @ForwardingAudienceOverrideNotRequired
   default void removeResourcePacks(@NotNull final ResourcePackRequest request) {
      List<ResourcePackInfo> infos = request.packs();
      if (infos.size() == 1) {
         this.removeResourcePacks(infos.get(0).id());
      } else if (infos.isEmpty()) {
         return;
      }

      UUID[] otherReqs = new UUID[infos.size() - 1];

      for (int i = 0; i < otherReqs.length; i++) {
         otherReqs[i] = infos.get(i + 1).id();
      }

      this.removeResourcePacks(infos.get(0).id(), otherReqs);
   }

   @ForwardingAudienceOverrideNotRequired
   default void removeResourcePacks(@NotNull final ResourcePackInfoLike request, @NotNull final ResourcePackInfoLike... others) {
      UUID[] otherReqs = new UUID[others.length];

      for (int i = 0; i < others.length; i++) {
         otherReqs[i] = others[i].asResourcePackInfo().id();
      }

      this.removeResourcePacks(request.asResourcePackInfo().id(), otherReqs);
   }

   default void removeResourcePacks(@NotNull final Iterable<UUID> ids) {
      Iterator<UUID> it = ids.iterator();
      if (it.hasNext()) {
         UUID id = it.next();
         UUID[] others;
         if (!it.hasNext()) {
            others = new UUID[0];
         } else if (ids instanceof Collection) {
            others = new UUID[((Collection)ids).size() - 1];

            for (int i = 0; i < others.length; i++) {
               others[i] = it.next();
            }
         } else {
            List<UUID> othersList = new ArrayList<>();

            while (it.hasNext()) {
               othersList.add(it.next());
            }

            others = othersList.toArray(new UUID[0]);
         }

         this.removeResourcePacks(id, others);
      }
   }

   default void removeResourcePacks(@NotNull final UUID id, @NotNull final UUID... others) {
   }

   default void clearResourcePacks() {
   }
}
