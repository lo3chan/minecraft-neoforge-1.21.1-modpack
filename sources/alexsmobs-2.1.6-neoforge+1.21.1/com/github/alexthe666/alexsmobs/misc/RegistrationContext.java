package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.resources.ResourceKey;

public final class RegistrationContext {
   public static final ThreadLocal<ResourceKey<?>> CURRENT_ID = new ThreadLocal<>();

   private RegistrationContext() {
   }
}
