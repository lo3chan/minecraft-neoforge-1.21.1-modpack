package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class PostLoadModuleNeoForge {
   public static List<Method> METHODS = new ArrayList<>();

   public static void init() {
      YungsApiNeoForge.loadingContextEventBus.addListener(PostLoadModuleNeoForge::commonSetup);
   }

   private static void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(
         () -> METHODS.forEach(
            m -> {
               try {
                  m.invoke(null);
               } catch (InvocationTargetException | IllegalAccessException var3) {
                  YungsApiCommon.LOGGER.error("Unable to invoke AutoRegister method {}", m.getName());
                  YungsApiCommon.LOGGER.error("Make sure the method is static and has no parameters!");
                  throw new RuntimeException(var3);
               } catch (NullPointerException var4) {
                  String message = String.format(
                     "Attempted to invoke AutoRegister method with null object. Did you forget to include a 'static' modifier for method '%s'?", m.getName()
                  );
                  YungsApiCommon.LOGGER.error(message);
                  throw new RuntimeException(message);
               }
            }
         )
      );
      CompostModuleNeoForge.registerCompostables();
   }
}
