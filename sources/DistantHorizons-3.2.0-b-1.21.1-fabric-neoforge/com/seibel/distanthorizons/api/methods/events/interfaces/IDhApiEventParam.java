package com.seibel.distanthorizons.api.methods.events.interfaces;

import com.seibel.distanthorizons.api.interfaces.util.IDhApiCopyable;

public interface IDhApiEventParam extends IDhApiCopyable {
   default boolean getCopyBeforeFire() {
      return true;
   }
}
