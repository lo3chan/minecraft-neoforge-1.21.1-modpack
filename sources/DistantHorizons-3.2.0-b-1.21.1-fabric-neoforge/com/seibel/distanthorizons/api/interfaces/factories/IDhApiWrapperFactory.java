package com.seibel.distanthorizons.api.interfaces.factories;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import java.io.IOException;

public interface IDhApiWrapperFactory {
   IDhApiBiomeWrapper getBiomeWrapper(Object[] objects, IDhApiLevelWrapper iDhApiLevelWrapper) throws ClassCastException;

   IDhApiBlockStateWrapper getBlockStateWrapper(Object[] objects, IDhApiLevelWrapper iDhApiLevelWrapper) throws ClassCastException;

   IDhApiBlockStateWrapper getAirBlockStateWrapper();

   IDhApiBiomeWrapper getBiomeWrapper(String string, IDhApiLevelWrapper iDhApiLevelWrapper) throws IOException, ClassCastException;

   IDhApiBlockStateWrapper getDefaultBlockStateWrapper(String string, IDhApiLevelWrapper iDhApiLevelWrapper) throws IOException, ClassCastException;
}
