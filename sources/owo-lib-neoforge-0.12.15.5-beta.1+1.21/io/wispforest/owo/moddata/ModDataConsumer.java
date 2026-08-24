package io.wispforest.owo.moddata;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public interface ModDataConsumer {
   String getDataSubdirectory();

   void acceptParsedFile(ResourceLocation var1, JsonObject var2);
}
