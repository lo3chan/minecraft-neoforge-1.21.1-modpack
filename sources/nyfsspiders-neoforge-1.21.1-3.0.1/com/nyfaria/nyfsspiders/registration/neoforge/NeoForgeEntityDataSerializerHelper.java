package com.nyfaria.nyfsspiders.registration.neoforge;

import com.google.auto.service.AutoService;
import com.nyfaria.nyfsspiders.registration.EntityDataSerializerHelper;
import com.nyfaria.nyfsspiders.registration.RegistrationProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import org.jetbrains.annotations.ApiStatus.Internal;

@ParametersAreNonnullByDefault
@Internal
@AutoService({EntityDataSerializerHelper.class})
public class NeoForgeEntityDataSerializerHelper implements EntityDataSerializerHelper {
   private final Map<String, RegistrationProvider<EntityDataSerializer<?>>> byModId = new ConcurrentHashMap<>();

   @Override
   public <T> EntityDataSerializer<T> register(ResourceLocation key, EntityDataSerializer<T> serializer) {
      this.getProvider(key.getNamespace()).register(key.getPath(), () -> serializer);
      return serializer;
   }

   private RegistrationProvider<EntityDataSerializer<?>> getProvider(String modId) {
      return this.byModId.computeIfAbsent(modId, theId -> RegistrationProvider.get(Keys.ENTITY_DATA_SERIALIZERS, theId));
   }
}
