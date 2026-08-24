package net.cibernet.alchemancy.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.network.S2CTriggerEchoPacket;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public record EchoEffect(
   Optional<ResourceKey<Level>> sourceLevel,
   Optional<UUID> sourceId,
   ItemStack sourceItem,
   Vec3 relativePos,
   float damage,
   EchoEffect.EffectType type,
   long startTime,
   long triggerAtTimestamp
) {
   static ResourceKey<DamageType> ECHO_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("alchemancy", "echo"));
   public static final Codec<EchoEffect> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("source_level").forGetter(EchoEffect::sourceLevel),
            Codec.STRING.xmap(UUID::fromString, UUID::toString).optionalFieldOf("source_id").forGetter(EchoEffect::sourceId),
            ItemStack.CODEC.fieldOf("source_item").forGetter(EchoEffect::sourceItem),
            Vec3.CODEC.fieldOf("relative_position").forGetter(EchoEffect::relativePos),
            Codec.FLOAT.fieldOf("damage").forGetter(EchoEffect::damage),
            Codec.STRING.xmap(EchoEffect.EffectType::valueOf, Enum::name).fieldOf("type").forGetter(EchoEffect::type),
            Codec.LONG.fieldOf("start_time").forGetter(EchoEffect::startTime),
            Codec.LONG.fieldOf("trigger_at").forGetter(EchoEffect::triggerAtTimestamp)
         )
         .apply(instance, EchoEffect::new)
   );

   public static void applyBlockEffectToEntity(Entity target, Level level, BlockPos source, ItemStack sourceItem, int duration) {
      ArrayList<EchoEffect> currentEffects = new ArrayList<>((Collection<? extends EchoEffect>)target.getData(AlchemancyDataAttachments.ECHO_EFFECTS));
      Vec3 relative = source.getCenter();
      currentEffects.add(
         new EchoEffect(
            Optional.of(level.dimension()),
            Optional.empty(),
            sourceItem.copy(),
            relative,
            0.0F,
            EchoEffect.EffectType.BLOCK_ACTIVATE,
            AlchemancyServerData.getGlobalTimer(),
            AlchemancyServerData.getGlobalTimer() + duration
         )
      );
      target.setData(AlchemancyDataAttachments.ECHO_EFFECTS, currentEffects);
   }

   public static void applyEffectToEntity(
      Entity target, @Nullable Entity source, ItemStack sourceItem, Vec3 sourcePosition, float damage, int duration, EchoEffect.EffectType type
   ) {
      if (!target.level().isClientSide() && !sourceItem.isEmpty()) {
         ArrayList<EchoEffect> currentEffects = new ArrayList<>((Collection<? extends EchoEffect>)target.getData(AlchemancyDataAttachments.ECHO_EFFECTS));
         Vec3 relative = sourcePosition.subtract(target.position());
         EchoEffect echo = new EchoEffect(
            source == null ? Optional.empty() : Optional.of(source.level().dimension()),
            source == null ? Optional.empty() : Optional.of(source.getUUID()),
            sourceItem.copy(),
            relative,
            damage,
            type,
            AlchemancyServerData.getGlobalTimer(),
            AlchemancyServerData.getGlobalTimer() + duration
         );
         currentEffects.add(echo);
         target.setData(AlchemancyDataAttachments.ECHO_EFFECTS, currentEffects);
      }
   }

   public void triggerServerside(Entity target) {
      if (target.level() instanceof ServerLevel level) {
         ServerLevel sourceLevel = this.sourceLevel().map(key -> level.getServer().getLevel(key)).orElse(null);
         Entity user = sourceLevel == null ? null : this.sourceId.<Entity>map(sourceLevel::getEntity).orElse(null);
         AtomicReference<Float> damageRef = new AtomicReference<>(this.damage());
         trigger(target, level, user, this.sourceItem(), this.relativePos(), this.type(), damageRef);
         float damage = damageRef.get();
         if (damage > 0.0F) {
            target.hurt(damageSource(user, target, this.relativePos()), damage);
         }

         if (target instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(
               player,
               new S2CTriggerEchoPacket(
                  Optional.ofNullable(user == null ? null : user.getId()), this.sourceLevel(), this.sourceItem(), this.relativePos(), this.type()
               ),
               new CustomPacketPayload[0]
            );
         }
      }
   }

   public static DamageSource damageSource(Entity user, Entity target, Vec3 relativePos) {
      return new DamageSource(target.damageSources().damageTypes.getHolderOrThrow(ECHO_DAMAGE_KEY), user, user, target.position().add(relativePos));
   }

   public static void trigger(
      Entity target, @Nullable Level sourceLevel, @Nullable Entity user, ItemStack sourceItem, Vec3 relativePos, EchoEffect.EffectType type
   ) {
      trigger(target, sourceLevel, user, sourceItem, relativePos, type, new AtomicReference<>(0.0F));
   }

   public static void trigger(
      Entity target,
      @Nullable Level sourceLevel,
      @Nullable Entity user,
      ItemStack sourceItem,
      Vec3 relativePos,
      EchoEffect.EffectType type,
      AtomicReference<Float> damage
   ) {
      InfusedPropertiesHelper.forEachProperty(
         sourceItem,
         propertyHolder -> {
            if (!propertyHolder.equals(AlchemancyProperties.ECHOED)) {
               switch (type) {
                  case ATTACK:
                     if (target instanceof LivingEntity living) {
                        ((Property)propertyHolder.value())
                           .onAttack(user, sourceItem, Property.activationDamageSource(target.level(), user, target.position().add(relativePos)), living);
                     }
                     break;
                  case DAMAGE:
                     if (target instanceof LivingEntity living) {
                        Pre damageEvent = new Pre(living, new DamageContainer(damageSource(user, target, relativePos), damage.get()));
                        ((Property)propertyHolder.value()).modifyAttackDamage(user, sourceItem, damageEvent);
                        damage.set(damageEvent.getNewDamage());
                     }
                     break;
                  case CRIT:
                     ((Property)propertyHolder.value()).onCriticalAttack(user instanceof Player player ? player : null, sourceItem, target);
                     break;
                  case ACTIVATE:
                     ((Property)propertyHolder.value())
                        .onActivation(
                           user,
                           target,
                           sourceItem,
                           Property.activationDamageSource(sourceLevel == null ? target.level() : sourceLevel, user, target.position().add(relativePos))
                        );
                     break;
                  case BLOCK_ACTIVATE:
                     if (sourceLevel != null) {
                        ((Property)propertyHolder.value())
                           .onActivationByBlock(sourceLevel, new BlockPos((int)relativePos.x(), (int)relativePos.y(), (int)relativePos.z()), target, sourceItem);
                     }
               }
            }
         }
      );
   }

   public static enum EffectType {
      ATTACK,
      DAMAGE,
      CRIT,
      ACTIVATE,
      BLOCK_ACTIVATE;
   }
}
