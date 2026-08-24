package me.lucko.spark.lib.asyncprofiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;

public class AsyncProfiler implements AsyncProfilerMXBean {
   private static AsyncProfiler instance;

   private AsyncProfiler() {
   }

   public static AsyncProfiler getInstance() {
      return getInstance(null);
   }

   public static synchronized AsyncProfiler getInstance(String libPath) {
      if (instance != null) {
         return instance;
      } else {
         AsyncProfiler profiler = new AsyncProfiler();
         if (libPath != null) {
            System.load(libPath);
         } else {
            try {
               profiler.getVersion();
            } catch (UnsatisfiedLinkError var8) {
               File file = extractEmbeddedLib();
               if (file != null) {
                  try {
                     System.load(file.getPath());
                  } finally {
                     file.delete();
                  }
               } else {
                  System.loadLibrary("asyncProfiler");
               }
            }
         }

         instance = profiler;
         return profiler;
      }
   }

   private static File extractEmbeddedLib() {
      String resourceName = "/" + getPlatformTag() + "/libasyncProfiler.so";
      InputStream in = AsyncProfiler.class.getResourceAsStream(resourceName);
      if (in == null) {
         return null;
      } else {
         File var20;
         try {
            String extractPath = System.getProperty("me.lucko.spark.lib.asyncprofiler.extractPath");
            File file = File.createTempFile("libasyncProfiler-", ".so", extractPath != null && !extractPath.isEmpty() ? new File(extractPath) : null);
            FileOutputStream out = new FileOutputStream(file);

            try {
               byte[] buf = new byte[32000];

               int bytes;
               while ((bytes = in.read(buf)) >= 0) {
                  out.write(buf, 0, bytes);
               }
            } catch (Throwable var17) {
               try {
                  out.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }

               throw var17;
            }

            out.close();
            var20 = file;
         } catch (IOException var18) {
            throw new IllegalStateException(var18);
         } finally {
            try {
               in.close();
            } catch (IOException var15) {
            }
         }

         return var20;
      }
   }

   private static String getPlatformTag() {
      String os = System.getProperty("os.name").toLowerCase();
      String arch = System.getProperty("os.arch").toLowerCase();
      if (os.contains("linux")) {
         if (arch.equals("amd64") || arch.equals("x86_64") || arch.contains("x64")) {
            return "linux-x64";
         }

         if (arch.equals("aarch64") || arch.contains("arm64")) {
            return "linux-arm64";
         }

         if (arch.equals("aarch32") || arch.contains("arm")) {
            return "linux-arm32";
         }

         if (arch.contains("86")) {
            return "linux-x86";
         }

         if (arch.contains("ppc64")) {
            return "linux-ppc64le";
         }
      } else if (os.contains("mac")) {
         return "macos";
      }

      throw new UnsupportedOperationException("Unsupported platform: " + os + "-" + arch);
   }

   @Override
   public void start(String event, long interval) throws IllegalStateException {
      if (event == null) {
         throw new NullPointerException();
      } else {
         this.start0(event, interval, true);
      }
   }

   @Override
   public void resume(String event, long interval) throws IllegalStateException {
      if (event == null) {
         throw new NullPointerException();
      } else {
         this.start0(event, interval, false);
      }
   }

   @Override
   public void stop() throws IllegalStateException {
      this.stop0();
   }

   @Override
   public native long getSamples();

   @Override
   public String getVersion() {
      try {
         return this.execute0("version");
      } catch (IOException var2) {
         throw new IllegalStateException(var2);
      }
   }

   @Override
   public String execute(String command) throws IllegalArgumentException, IllegalStateException, IOException {
      if (command == null) {
         throw new NullPointerException();
      } else {
         return this.execute0(command);
      }
   }

   @Override
   public String dumpCollapsed(Counter counter) {
      try {
         return this.execute0("collapsed," + counter.name().toLowerCase());
      } catch (IOException var3) {
         throw new IllegalStateException(var3);
      }
   }

   @Override
   public String dumpTraces(int maxTraces) {
      try {
         return this.execute0(maxTraces == 0 ? "traces" : "traces=" + maxTraces);
      } catch (IOException var3) {
         throw new IllegalStateException(var3);
      }
   }

   @Override
   public String dumpFlat(int maxMethods) {
      try {
         return this.execute0(maxMethods == 0 ? "flat" : "flat=" + maxMethods);
      } catch (IOException var3) {
         throw new IllegalStateException(var3);
      }
   }

   public void addThread(Thread thread) {
      this.filterThread(thread, true);
   }

   public void removeThread(Thread thread) {
      this.filterThread(thread, false);
   }

   private void filterThread(Thread thread, boolean enable) {
      if (thread != null && thread != Thread.currentThread()) {
         synchronized (thread) {
            State state = thread.getState();
            if (state != State.NEW && state != State.TERMINATED) {
               this.filterThread0(thread, enable);
            }
         }
      } else {
         this.filterThread0(null, enable);
      }
   }

   private native void start0(String var1, long var2, boolean var4) throws IllegalStateException;

   private native void stop0() throws IllegalStateException;

   private native String execute0(String var1) throws IllegalArgumentException, IllegalStateException, IOException;

   private native void filterThread0(Thread var1, boolean var2);
}
