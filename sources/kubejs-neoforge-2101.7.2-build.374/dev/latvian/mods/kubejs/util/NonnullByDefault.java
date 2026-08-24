package dev.latvian.mods.kubejs.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.jetbrains.annotations.NotNull;

@Documented
@Retention(RetentionPolicy.CLASS)
@NotNull
public @interface NonnullByDefault {
}
