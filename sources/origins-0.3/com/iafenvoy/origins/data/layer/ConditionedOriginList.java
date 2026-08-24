package com.iafenvoy.origins.data.layer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.data.origin.OriginRegistries;
import com.iafenvoy.origins.util.codec.RegistryCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class ConditionedOriginList {
   public static final Codec<ConditionedOriginList> CODEC = Codec.either(
         RegistryCodecs.holderOrTag(OriginRegistries.ORIGIN_KEY), ConditionedOriginList.ConditionedOrigin.CODEC
      )
      .listOf()
      .xmap(x -> {
         Builder<ConditionedOriginList.ConditionedOrigin> builder = ImmutableList.builder();
         List<Either<Holder<Origin>, TagKey<Origin>>> alwaysApplyOrigins = new LinkedList<>();
         builder.add(new ConditionedOriginList.ConditionedOrigin(AlwaysTrueCondition.INSTANCE, alwaysApplyOrigins));

         for (Either<Either<Holder<Origin>, TagKey<Origin>>, ConditionedOriginList.ConditionedOrigin> either : x) {
            either.ifLeft(alwaysApplyOrigins::add).ifRight(builder::add);
         }

         return builder.build();
      }, x -> x.stream().map(Either::right).toList())
      .xmap(ConditionedOriginList::new, x -> x.origins);
   private final List<ConditionedOriginList.ConditionedOrigin> origins;

   public ConditionedOriginList(List<ConditionedOriginList.ConditionedOrigin> origins) {
      this.origins = origins;
   }

   public Stream<Holder<Origin>> collectOrigins(RegistryAccess access, @Nullable Entity entity) {
      return this.origins.stream().filter(x -> entity == null || x.condition.test(entity)).flatMap(x -> x.streamOrigins(access));
   }

   public record ConditionedOrigin(EntityCondition condition, List<Either<Holder<Origin>, TagKey<Origin>>> origins) {
      public static final Codec<ConditionedOriginList.ConditionedOrigin> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               EntityCondition.CODEC.fieldOf("condition").forGetter(ConditionedOriginList.ConditionedOrigin::condition),
               RegistryCodecs.holderOrTag(OriginRegistries.ORIGIN_KEY).listOf().fieldOf("origins").forGetter(ConditionedOriginList.ConditionedOrigin::origins)
            )
            .apply(i, ConditionedOriginList.ConditionedOrigin::new)
      );

      public Stream<Holder<Origin>> streamOrigins(RegistryAccess access) {
         return RegistryCodecs.listAll(this.origins, access, OriginRegistries.ORIGIN_KEY).stream();
      }
   }
}
