package com.teamresourceful.resourcefulconfig.web.config.validators;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulconfig.web.info.UserJwtPayload;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record IfValidator(Set<UUID> uuids, Validator ifTrue, Optional<Validator> ifFalse) implements Validator {
   public static final Codec<Set<UUID>> UUID_SET_CODEC = UUIDUtil.AUTHLIB_CODEC.listOf().xmap(HashSet::new, Lists::newArrayList);
   public static final MapCodec<IfValidator> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            UUID_SET_CODEC.fieldOf("uuids").forGetter(IfValidator::uuids),
            Validators.CODEC.fieldOf("if").forGetter(IfValidator::ifTrue),
            Validators.CODEC.optionalFieldOf("else").forGetter(IfValidator::ifFalse)
         )
         .apply(instance, IfValidator::new)
   );

   public boolean test(UserJwtPayload userJwtPayload) {
      return this.uuids.contains(userJwtPayload.uuid())
         ? this.ifTrue.test(userJwtPayload)
         : this.ifFalse.<Boolean>map(validator -> validator.test(userJwtPayload)).orElse(false);
   }

   @Override
   public String id() {
      return "if";
   }
}
