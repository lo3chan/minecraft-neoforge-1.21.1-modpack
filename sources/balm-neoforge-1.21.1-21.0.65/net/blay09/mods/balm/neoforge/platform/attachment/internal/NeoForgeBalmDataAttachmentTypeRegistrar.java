package net.blay09.mods.balm.neoforge.platform.attachment.internal;

import java.util.function.BiFunction;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.platform.attachment.BalmDataAttachmentTypeRegistrar;
import net.blay09.mods.balm.platform.attachment.BalmDataAttachmentTypeRegistration;
import net.blay09.mods.balm.platform.attachment.DataAttachmentTypeBuilder;
import net.blay09.mods.balm.platform.attachment.internal.DataAttachmentTypeBuilderImpl;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.AttachmentType.Builder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class NeoForgeBalmDataAttachmentTypeRegistrar implements BalmDataAttachmentTypeRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   public NeoForgeBalmDataAttachmentTypeRegistrar(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public <T> BalmDataAttachmentTypeRegistration<T> register(
      String name, BiFunction<ResourceLocation, DataAttachmentTypeBuilder<T>, DataAttachmentTypeBuilder<T>> constructor
   ) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      DataAttachmentTypeBuilderImpl<T> builder = (DataAttachmentTypeBuilderImpl<T>)constructor.apply(identifier, new DataAttachmentTypeBuilderImpl<>());
      Builder<T> neoForgeBuilder = AttachmentType.builder(builder.getInitializer() != null ? builder.getInitializer() : () -> null);
      if (builder.getPersistentCodec() != null) {
         neoForgeBuilder.serialize(builder.getPersistentCodec());
      }

      if (builder.isCopyOnDeath()) {
         neoForgeBuilder.copyOnDeath();
      }

      ResourceKey<AttachmentType<?>> resourceKey = ResourceKey.create(NeoForgeRegistries.ATTACHMENT_TYPES.key(), identifier);
      Holder<AttachmentType<?>> holder = this.registrar.register(resourceKey, ignored -> neoForgeBuilder.build());
      return new NeoForgeBalmDataAttachmentTypeRegistration<>(holder);
   }
}
