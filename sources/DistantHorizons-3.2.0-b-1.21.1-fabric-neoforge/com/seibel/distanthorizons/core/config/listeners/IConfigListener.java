package com.seibel.distanthorizons.core.config.listeners;

public interface IConfigListener {
   default void onConfigValueSet() {
   }

   default void onUiModify() {
   }
}
