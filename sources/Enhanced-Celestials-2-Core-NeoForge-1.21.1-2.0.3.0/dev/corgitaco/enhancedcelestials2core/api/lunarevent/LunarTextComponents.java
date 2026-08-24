package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.util.CustomTranslationTextComponent;
import dev.corgitaco.enhancedcelestials2core.util.Description;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public record LunarTextComponents(Optional<LunarTextComponents.Notification> riseNotification, Optional<LunarTextComponents.Notification> setNotification)
   implements Description {
   public static final Codec<LunarTextComponents> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            LunarTextComponents.Notification.CODEC
               .optionalFieldOf("start_notification")
               .orElse(Optional.empty())
               .forGetter(clientSettings -> clientSettings.riseNotification),
            LunarTextComponents.Notification.CODEC
               .optionalFieldOf("end_notification")
               .orElse(Optional.empty())
               .forGetter(clientSettings -> clientSettings.setNotification)
         )
         .apply(builder, LunarTextComponents::new)
   );

   public LunarTextComponents(CustomTranslationTextComponent riseNotification, CustomTranslationTextComponent setNotification) {
      this(
         Optional.of(new LunarTextComponents.Notification(riseNotification, LunarTextComponents.NotificationType.CHAT)),
         Optional.of(new LunarTextComponents.Notification(setNotification, LunarTextComponents.NotificationType.CHAT))
      );
   }

   public LunarTextComponents(LunarTextComponents.Notification riseNotification, LunarTextComponents.Notification setNotification) {
      this(Optional.of(riseNotification), Optional.of(setNotification));
   }

   @Override
   public Component description() {
      Component rise = this.riseNotification
         .map(LunarTextComponents.Notification::description)
         .orElseGet(() -> Component.translatable("enhancedcelestials2core.description.none"));
      Component set = this.setNotification
         .map(LunarTextComponents.Notification::description)
         .orElseGet(() -> Component.translatable("enhancedcelestials2core.description.none"));
      return Component.translatable("enhancedcelestials2core.lunar_text_components", new Object[]{rise, set});
   }

   public record Notification(CustomTranslationTextComponent customTranslationTextComponent, LunarTextComponents.NotificationType notificationType)
      implements Description {
      public static final Codec<LunarTextComponents.Notification> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               CustomTranslationTextComponent.CODEC.fieldOf("component").forGetter(notification -> notification.customTranslationTextComponent),
               LunarTextComponents.NotificationType.CODEC.fieldOf("type").forGetter(notification -> notification.notificationType)
            )
            .apply(builder, LunarTextComponents.Notification::new)
      );

      @Override
      public Component description() {
         return Component.translatable(
            "enhancedcelestials2core.lunar_text_components.notification",
            new Object[]{this.notificationType.getSerializedName(), this.customTranslationTextComponent.getComponent()}
         );
      }
   }

   public static enum NotificationType implements StringRepresentable {
      CHAT,
      NONE,
      HOT_BAR;

      public static final Codec<LunarTextComponents.NotificationType> CODEC = StringRepresentable.fromEnum(LunarTextComponents.NotificationType::values);

      public static LunarTextComponents.NotificationType byName(String name) {
         return valueOf(name.toUpperCase());
      }

      public String getSerializedName() {
         return this.name();
      }
   }
}
