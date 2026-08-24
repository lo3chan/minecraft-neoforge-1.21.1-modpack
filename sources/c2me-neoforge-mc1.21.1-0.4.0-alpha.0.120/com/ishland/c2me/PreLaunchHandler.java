package com.ishland.c2me;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.MixinService;

public class PreLaunchHandler {
   static void onPreLaunch() {
      if (Boolean.getBoolean("com.ishland.c2me.mixin.doAudit")) {
         Logger auditLogger = LoggerFactory.getLogger("C2ME Mixin Audit");

         try {
            Class<?> transformerClazz = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
            if (transformerClazz.isInstance(MixinEnvironment.getCurrentEnvironment().getActiveTransformer())) {
               Field processorField = transformerClazz.getDeclaredField("processor");
               processorField.setAccessible(true);
               Object processor = processorField.get(MixinEnvironment.getCurrentEnvironment().getActiveTransformer());
               Class<?> processorClazz = Class.forName("org.spongepowered.asm.mixin.transformer.MixinProcessor");
               Field configsField = processorClazz.getDeclaredField("configs");
               configsField.setAccessible(true);
               List<?> configs = (List<?>)configsField.get(processor);
               Class<?> configClazz = Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig");
               Method getUnhandledTargetsMethod = configClazz.getDeclaredMethod("getUnhandledTargets");
               getUnhandledTargetsMethod.setAccessible(true);
               Set<String> unhandled = new HashSet<>();

               for (Object config : configs) {
                  Set<String> unhandledTargets = (Set<String>)getUnhandledTargetsMethod.invoke(config);
                  unhandled.addAll(unhandledTargets);
               }

               for (String s : unhandled) {
                  auditLogger.info("Loading class {}", s);
                  MixinService.getService().getClassProvider().findClass(s, false);
               }

               for (Object config : configs) {
                  for (String unhandledTarget : (Set)getUnhandledTargetsMethod.invoke(config)) {
                     auditLogger.error("{} is already classloaded", unhandledTarget);
                  }
               }
            }
         } catch (Throwable var14) {
            throw new RuntimeException("Failed to audit mixins", var14);
         }
      }
   }
}
