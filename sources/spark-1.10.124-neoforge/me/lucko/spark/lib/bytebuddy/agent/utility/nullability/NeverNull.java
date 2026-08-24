package me.lucko.spark.lib.bytebuddy.agent.utility.nullability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.TypeQualifierNickname;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Nonnull
@TypeQualifierNickname
public @interface NeverNull {
   @Documented
   @Target({ElementType.PACKAGE})
   @Retention(RetentionPolicy.RUNTIME)
   @Nonnull
   @TypeQualifierDefault({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
   public @interface ByDefault {
   }
}
