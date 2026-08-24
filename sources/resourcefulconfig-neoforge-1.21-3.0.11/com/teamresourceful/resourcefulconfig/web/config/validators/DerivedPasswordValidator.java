package com.teamresourceful.resourcefulconfig.web.config.validators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulconfig.web.info.UserJwtPayload;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public record DerivedPasswordValidator(SecretKeySpec spec) implements Validator {
   private static final Codec<SecretKeySpec> SPEC_CODEC = Codec.STRING
      .xmap(s -> new SecretKeySpec(s.getBytes(StandardCharsets.UTF_8), "HmacSHA256"), s -> new String(s.getEncoded()));
   public static final MapCodec<DerivedPasswordValidator> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(SPEC_CODEC.fieldOf("secret").forGetter(DerivedPasswordValidator::spec)).apply(instance, DerivedPasswordValidator::new)
   );

   public boolean test(UserJwtPayload userJwtPayload) {
      String password = userJwtPayload.password();
      String uuid = userJwtPayload.uuid().toString();

      try {
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(this.spec);
         byte[] hash = mac.doFinal(uuid.getBytes(StandardCharsets.UTF_8));
         return Objects.equals(Base64.getEncoder().encodeToString(hash), password);
      } catch (Exception var6) {
         return false;
      }
   }

   @Override
   public String id() {
      return "derived";
   }
}
