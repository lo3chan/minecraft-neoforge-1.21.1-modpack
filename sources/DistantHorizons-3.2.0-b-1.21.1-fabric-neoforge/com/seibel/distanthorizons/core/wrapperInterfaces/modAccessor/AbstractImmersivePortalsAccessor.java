package com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBeforeRenderEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiCancelableEventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractImmersivePortalsAccessor implements IImmersivePortalsAccessor {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static MethodHandle isRenderingMethodHandle;

   public AbstractImmersivePortalsAccessor() {
      LOGGER.info("Immersive Portals detected: some DH features will be disabled or may only partially function.");
      AbstractImmersivePortalsAccessor.BeforeRenderEvent event = new AbstractImmersivePortalsAccessor.BeforeRenderEvent(this);
      DhApi.events.bind(DhApiBeforeRenderEvent.class, event);
   }

   private static Class<?> getPortalRenderingClass() {
      try {
         return Class.forName("qouteall.imm_ptl.core.render.context_management.PortalRendering");
      } catch (ClassNotFoundException var4) {
         try {
            return Class.forName("com.qouteall.immersive_portals.render.context_management.PortalRendering");
         } catch (ClassNotFoundException var3) {
            RuntimeException err = new RuntimeException(var4);
            err.addSuppressed(var3);
            throw err;
         }
      }
   }

   @Override
   public String getModName() {
      return "Immersive Portals";
   }

   @Override
   public boolean isRenderingPortal() {
      try {
         if (isRenderingMethodHandle == null) {
            isRenderingMethodHandle = MethodHandles.lookup().findStatic(getPortalRenderingClass(), "isRendering", MethodType.methodType(boolean.class));
         }

         return (boolean)isRenderingMethodHandle.invoke();
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   private static class BeforeRenderEvent extends DhApiBeforeRenderEvent {
      @NotNull
      private final IImmersivePortalsAccessor immersivePortals;

      public BeforeRenderEvent(@NotNull IImmersivePortalsAccessor portalAccessor) {
         this.immersivePortals = portalAccessor;
      }

      @Override
      public void beforeRender(DhApiCancelableEventParam<DhApiRenderParam> event) {
         if (this.immersivePortals.isRenderingPortal()) {
            event.cancelEvent();
         }
      }
   }
}
