package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.attachment.OriginDataHolder;

public interface ResourceHelper {
   int getMinValue();

   int getMaxValue();

   int getValue(OriginDataHolder var1);

   void setValue(OriginDataHolder var1, int var2);
}
