package net.cibernet.alchemancy.registries;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.cibernet.alchemancy.util.EchoEffect;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AlchemancyDataAttachments {
   public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "alchemancy");
   public static final Supplier<AttachmentType<List<Integer>>> ENTITY_TINT = register("entity_tint", ArrayList::new, Codec.INT.listOf());
   public static final Supplier<AttachmentType<List<EchoEffect>>> ECHO_EFFECTS = register("echo_effects", ArrayList::new, EchoEffect.CODEC.listOf());

   private static <T> Supplier<AttachmentType<T>> register(String key, Supplier<T> defaultValue, Codec<T> codec) {
      return REGISTRY.register(key, () -> AttachmentType.builder(defaultValue).serialize(codec).build());
   }
}
