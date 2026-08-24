package com.iafenvoy.origins.data.power;

import com.google.common.collect.ImmutableSet.Builder;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.ActiveComponent;
import com.iafenvoy.origins.util.annotation.Comment;
import com.iafenvoy.origins.util.codec.DefaultedCodec;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class Power {
   public static final Codec<Holder<Power>> CODEC = RegistryFixedCodec.create(PowerRegistries.POWER_KEY);
   public static final Codec<Power> DIRECT_CODEC = DefaultedCodec.registryDispatch(
      PowerRegistries.POWER_TYPE, Power::codec, Function.identity(), Power::createEmpty
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Power>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PowerRegistries.POWER_KEY);
   private final Power.BaseSettings settings;
   private Optional<Power> parent = Optional.empty();

   private static Power createEmpty() {
      return new EmptyPower();
   }

   protected Power(Power.BaseSettings settings) {
      this.settings = settings;
   }

   public Power.BaseSettings getSettings() {
      return this.settings;
   }

   public void setParent(Optional<Power> parent) {
      this.parent = parent;
   }

   public boolean isHidden() {
      return this.settings.hidden || this.parent.isPresent();
   }

   @NotNull
   public abstract MapCodec<? extends Power> codec();

   @Comment("Only one class each is allowed")
   public void createComponents(ComponentCollector collector) {
      collector.add(new ActiveComponent(false));
   }

   public void collectBadges(Builder<Badge> builder) {
      this.settings.badges().forEach(builder::add);
   }

   public boolean isActive(OriginDataHolder holder) {
      return holder.getComponentFor(this, ActiveComponent.class).map(ActiveComponent::isLastActive).orElse(false)
         && this.parent.<Boolean>map(x -> x.isActive(holder)).orElse(true);
   }

   public void grant(@NotNull OriginDataHolder holder) {
      this.tick(holder);
   }

   public void revoke(@NotNull OriginDataHolder holder) {
      if (this.isActive(holder)) {
         this.inactive(holder);
      }
   }

   public void active(@NotNull OriginDataHolder holder) {
   }

   public void inactive(@NotNull OriginDataHolder holder) {
   }

   public void tick(@NotNull OriginDataHolder holder) {
      holder.getComponentFor(this, ActiveComponent.class).ifPresent(x -> x.tick(holder, this));
      if (this.isActive(holder)) {
         this.activeTick(holder);
      }
   }

   public void activeTick(OriginDataHolder holder) {
   }

   @Comment("Interval for tick(), related with active() and inactive()")
   public int tickInterval() {
      return 1;
   }

   public void respawn(OriginDataHolder holder, boolean backFromEnd) {
   }

   public ResourceLocation getId(RegistryAccess access) {
      Registry<Power> registry = access.registryOrThrow(PowerRegistries.POWER_KEY);
      return this.parent.isPresent() && this.parent.get() instanceof MultiplePower multiple
         ? multiple.getId(access)
            .withSuffix("_" + multiple.getPowers().entrySet().stream().filter(x -> x.getValue() == this).findFirst().map(Entry::getKey).orElse("child"))
         : registry.getKey(this);
   }

   public MutableComponent getName(RegistryAccess access) {
      return this.getName(this.getId(access));
   }

   public MutableComponent getName(ResourceLocation id) {
      return this.settings.name().<MutableComponent>map(Component::copy).orElse(Component.translatable(id.toLanguageKey("power", "name")));
   }

   public MutableComponent getDescription(RegistryAccess access) {
      return this.getDescription(this.getId(access));
   }

   public MutableComponent getDescription(ResourceLocation id) {
      return this.settings.description().<MutableComponent>map(Component::copy).orElse(Component.translatable(id.toLanguageKey("power", "description")));
   }

   public record BaseSettings(
      Optional<Component> name, Optional<Component> description, boolean hidden, EntityCondition condition, int loadingPriority, List<Badge> badges
   ) {
      public static final MapCodec<Power.BaseSettings> CODEC = RecordCodecBuilder.mapCodec(
         i -> i.group(
               MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("name").forGetter(Power.BaseSettings::name),
               MiscCodecs.TRANSLATE_FIRST.optionalFieldOf("description").forGetter(Power.BaseSettings::description),
               Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Power.BaseSettings::hidden),
               EntityCondition.optionalCodec("condition").forGetter(Power.BaseSettings::condition),
               Codec.INT.optionalFieldOf("loading_priority", 0).forGetter(Power.BaseSettings::loadingPriority),
               RegistryCodecs.referenceOrDirect(Badge.CODEC, Badge.DIRECT_CODEC)
                  .listOf()
                  .optionalFieldOf("badges", List.of())
                  .forGetter(Power.BaseSettings::badges)
            )
            .apply(i, Power.BaseSettings::new)
      );
   }
}
