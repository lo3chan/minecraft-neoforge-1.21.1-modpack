package com.yungnickyoung.minecraft.bettermineshafts;

import com.yungnickyoung.minecraft.bettermineshafts.module.ConfigModule;
import com.yungnickyoung.minecraft.bettermineshafts.services.Services;
import com.yungnickyoung.minecraft.yungsapi.api.YungAutoRegister;
import io.netty.util.internal.ConcurrentSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterMineshaftsCommon {
   public static final String MOD_ID = "bettermineshafts";
   public static final String MOD_NAME = "YUNG's Better Mineshafts";
   public static final Logger LOGGER = LogManager.getLogger("bettermineshafts");
   public static final ConfigModule CONFIG = new ConfigModule();
   public static ConcurrentSet<Integer> surfaceEntrances = new ConcurrentSet();
   public static AtomicInteger count = new AtomicInteger(0);
   public static final boolean DEBUG_LOG = false;

   public static void init() {
      YungAutoRegister.scanPackageForAnnotations("com.yungnickyoung.minecraft.bettermineshafts.module");
      Services.MODULES.loadModules();
   }
}
