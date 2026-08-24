package com.seibel.distanthorizons.common.wrappers.gui.classicConfig;

import com.seibel.distanthorizons.core.wrapperInterfaces.config.IConfigGui;
import java.util.ArrayList;

public class ClassicConfigGUI$ConfigCoreInterface_neoforge implements IConfigGui {
   public final ArrayList<Runnable> onScreenChangeListenerList = new ArrayList<>();

   @Override
   public void addOnScreenChangeListener(Runnable newListener) {
      this.onScreenChangeListenerList.add(newListener);
   }

   @Override
   public void removeOnScreenChangeListener(Runnable oldListener) {
      this.onScreenChangeListenerList.remove(oldListener);
   }
}
