package com.teamresourceful.resourcefulconfig.web.config.validators;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulconfig.web.info.UserJwtPayload;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public record HashedPasswordValidator(HashedPasswordValidator.HashType type, String hash) implements Validator {
   public static final MapCodec<HashedPasswordValidator> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            HashedPasswordValidator.HashType.CODEC.fieldOf("algorithm").forGetter(HashedPasswordValidator::type),
            Codec.STRING.fieldOf("input").forGetter(HashedPasswordValidator::hash)
         )
         .apply(instance, HashedPasswordValidator::new)
   );

   public boolean test(UserJwtPayload userJwtPayload) {
      String password = userJwtPayload.password();

      HashFunction function = switch (this.type) {
         case SHA1 -> Hashing.sha1();
         case SHA256 -> Hashing.sha256();
         case SHA512 -> Hashing.sha512();
         case MD5 -> Hashing.md5();
      };
      return function.hashString(password, StandardCharsets.UTF_8).toString().equals(this.hash);
   }

   @Override
   public String id() {
      return "hashed";
   }

   public static enum HashType {
      SHA1,
      SHA256,
      SHA512,
      MD5;

      public static final Codec<HashedPasswordValidator.HashType> CODEC = Codec.STRING
         .comapFlatMap(HashedPasswordValidator.HashType::fromString, Enum::toString);

      public static DataResult<HashedPasswordValidator.HashType> fromString(String string) {
         String var1 = string.toLowerCase(Locale.ROOT);

         return switch (var1) {
            case "sha1" -> DataResult.success(SHA1);
            case "sha256" -> DataResult.success(SHA256);
            case "sha512" -> DataResult.success(SHA512);
            case "md5" -> DataResult.success(MD5);
            default -> DataResult.error(() -> "Unknown hash type: " + string);
         };
      }
   }
}
