package com.nyfaria.nyfsspiders.registration;

import com.nyfaria.nyfsspiders.registration.util.$InternalRegUtils;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
public interface EntityDataSerializerHelper {
   EntityDataSerializerHelper INSTANCE = $InternalRegUtils.getOneAndOnlyService(EntityDataSerializerHelper.class);

   <T> EntityDataSerializer<T> register(ResourceLocation var1, EntityDataSerializer<T> var2);
}
