package me.lucko.spark.common.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum MacosSysctl {
   SYSCTL("sysctl", "-a");

   private static final boolean SUPPORTED = System.getProperty("os.name").toLowerCase(Locale.ROOT).replace(" ", "").equals("macosx");
   private final String[] cmdArgs;

   private MacosSysctl(String... cmdArgs) {
      this.cmdArgs = cmdArgs;
   }

   @NonNull
   public List<String> read() {
      if (SUPPORTED) {
         ProcessBuilder process = new ProcessBuilder(this.cmdArgs).redirectErrorStream(true);

         try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.start().getInputStream()));

            Object var5;
            try {
               List<String> lines = new ArrayList<>();

               String line;
               while ((line = buf.readLine()) != null) {
                  lines.add(line);
               }

               var5 = lines;
            } catch (Throwable var7) {
               try {
                  buf.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            buf.close();
            return (List<String>)var5;
         } catch (Exception var8) {
         }
      }

      return Collections.emptyList();
   }
}
