/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.vines;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface Adjustable {
    public String id() default "";

    public double min() default 0.0;

    public double max() default 1.0;

    public double step() default 0.01;

    public String maxTranslationId() default "";

    public String translationId();
}

