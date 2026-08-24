package net.mehvahdjukaar.moonlight.api.resources.pack;

import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

public interface IEditablePackResources extends PackResources {
   void addNamespaces(String... var1);

   void addRootResource(String var1, byte[] var2);

   void addResource(ResourceLocation var1, byte[] var2);

   void removeResource(ResourceLocation var1);

   void removeRootResource(String var1);

   boolean clearAllResources();

   PackType getPackType();

   boolean isEmpty();

   @Deprecated(
      forRemoval = true
   )
   default void commitChanges(Executor executor) {
      this.commitChanges();
   }

   default void commitChanges() {
   }

   default boolean initializeIfValid() {
      return true;
   }
}
