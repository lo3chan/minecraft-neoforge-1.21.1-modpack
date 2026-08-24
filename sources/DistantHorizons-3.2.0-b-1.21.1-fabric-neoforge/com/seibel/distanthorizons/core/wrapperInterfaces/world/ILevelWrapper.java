package com.seibel.distanthorizons.core.wrapperInterfaces.world;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import org.jetbrains.annotations.Nullable;

public interface ILevelWrapper extends IDhApiLevelWrapper, IBindable {
   IDimensionTypeWrapper getDimensionType();

   @Override
   String getDimensionName();

   long getHashedSeed();

   default String getHashedSeedEncoded() {
      String encoded = BaseEncoding.base32Hex().encode(Longs.toByteArray(this.getHashedSeed()));
      return encoded.substring(0, 13).toLowerCase();
   }

   @Override
   String getDhIdentifier();

   @Override
   boolean hasCeiling();

   @Override
   boolean hasSkyLight();

   @Override
   int getMaxHeight();

   @Override
   int getMinHeight();

   void onUnload();

   void setDhLevel(IDhLevel iDhLevel);

   @Nullable
   IDhLevel getDhLevel();
}
