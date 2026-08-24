package com.seibel.distanthorizons.core.dependencyInjection;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IModAccessor;
import com.seibel.distanthorizons.coreapi.DependencyInjection.DependencyInjector;

public class ModAccessorInjector extends DependencyInjector<IModAccessor> {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final ModAccessorInjector INSTANCE = new ModAccessorInjector(IModAccessor.class);

   public ModAccessorInjector(Class<IModAccessor> newBindableInterface) {
      super(newBindableInterface, false);
   }

   public void bind(Class<? extends IModAccessor> interfaceClass, IModAccessor modAccessor) throws IllegalStateException, IllegalArgumentException {
      super.bind(interfaceClass, modAccessor);
      LOGGER.info("Registered mod compatibility accessor for: [" + modAccessor.getModName() + "].");
   }
}
