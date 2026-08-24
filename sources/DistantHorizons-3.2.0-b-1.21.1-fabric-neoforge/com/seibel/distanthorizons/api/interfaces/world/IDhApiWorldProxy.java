package com.seibel.distanthorizons.api.interfaces.world;

public interface IDhApiWorldProxy {
   boolean worldLoaded();

   void setReadOnly(boolean bl) throws IllegalStateException;

   boolean getReadOnly() throws IllegalStateException;

   IDhApiLevelWrapper getSinglePlayerLevel() throws IllegalStateException;

   Iterable<IDhApiLevelWrapper> getAllLoadedLevelWrappers() throws IllegalStateException;

   Iterable<IDhApiLevelWrapper> getAllLoadedLevelsForDimensionType(IDhApiDimensionTypeWrapper iDhApiDimensionTypeWrapper) throws IllegalStateException;

   Iterable<IDhApiLevelWrapper> getAllLoadedLevelsWithDimensionNameLike(String string) throws IllegalStateException;
}
