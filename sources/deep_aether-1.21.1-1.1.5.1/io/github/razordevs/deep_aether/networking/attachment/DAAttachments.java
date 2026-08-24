package io.github.razordevs.deep_aether.networking.attachment;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DAAttachments {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "deep_aether");
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<DAPlayerAttachment>> PLAYER = ATTACHMENTS.register(
      "player", () -> AttachmentType.builder(DAPlayerAttachment::new).serialize(DAPlayerAttachment.CODEC).copyOnDeath().build()
   );
   public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> PLAYER_BOSS_FIGHT = ATTACHMENTS.register(
      "player_boss_fight", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
   );
}
