package io.wispforest.owo.renderdoc;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import io.wispforest.owo.Owo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.lwjgl.system.linux.DynamicLinkLoader;

@Experimental
public final class RenderDoc {
   private static final RenderdocLibrary.RenderdocApi renderdoc;

   private RenderDoc() {
   }

   public static boolean isAvailable() {
      return renderdoc != null;
   }

   public static String getAPIVersion() {
      if (renderdoc == null) {
         return "not connected";
      } else {
         IntByReference major = new IntByReference();
         IntByReference minor = new IntByReference();
         IntByReference patch = new IntByReference();
         renderdoc.GetAPIVersion.call(major, minor, patch);
         return major.getValue() + "." + minor.getValue() + "." + patch.getValue();
      }
   }

   public static <T> boolean setCaptureOption(RenderDoc.CaptureOption<T> option, T value) {
      if (renderdoc == null) {
         return false;
      } else if (value instanceof Boolean bool) {
         return renderdoc.SetCaptureOptionU32.call(option.idx, new RenderdocLibrary.uint32_t(bool ? 1 : 0)) == 1;
      } else if (value instanceof Integer uint) {
         return renderdoc.SetCaptureOptionU32.call(option.idx, new RenderdocLibrary.uint32_t(uint)) == 1;
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public static <T> T getCaptureOption(RenderDoc.CaptureOption<T> option) {
      if (renderdoc == null) {
         return null;
      } else if (option.type == Boolean.class) {
         return (T)renderdoc.GetCaptureOptionU32.call(option.idx).intValue() == 1;
      } else if (option.type == Integer.class) {
         return (T)renderdoc.GetCaptureOptionU32.call(option.idx).intValue();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public static void setCaptureKeys(RenderDoc.Key... keys) {
      if (renderdoc != null) {
         renderdoc.SetCaptureKeys.call(Arrays.stream(keys).mapToInt(value -> value.keycode).toArray(), keys.length);
      }
   }

   public static EnumSet<RenderDoc.OverlayOption> getOverlayOptions() {
      if (renderdoc == null) {
         return null;
      } else {
         int mask = renderdoc.GetOverlayBits.call().intValue();
         EnumSet<RenderDoc.OverlayOption> set = EnumSet.noneOf(RenderDoc.OverlayOption.class);

         for (RenderDoc.OverlayOption option : RenderDoc.OverlayOption.values()) {
            if ((mask & option.mask) != 0) {
               set.add(option);
            }
         }

         return set;
      }
   }

   public static void enableOverlayOptions(RenderDoc.OverlayOption... options) {
      if (renderdoc != null) {
         int mask = 0;

         for (RenderDoc.OverlayOption option : options) {
            mask |= option.mask;
         }

         renderdoc.MaskOverlayBits.call(new RenderdocLibrary.uint32_t(-1), new RenderdocLibrary.uint32_t(mask));
      }
   }

   public static void disableOverlayOptions(RenderDoc.OverlayOption... options) {
      if (renderdoc != null) {
         int mask = 0;

         for (RenderDoc.OverlayOption option : options) {
            mask |= option.mask;
         }

         renderdoc.MaskOverlayBits.call(new RenderdocLibrary.uint32_t(~mask), new RenderdocLibrary.uint32_t(0));
      }
   }

   public static void removeHooks() {
      if (renderdoc != null) {
         renderdoc.RemoveHooks.call();
      }
   }

   public static void unloadCrashHandler() {
      if (renderdoc != null) {
         renderdoc.UnloadCrashHandler.call();
      }
   }

   public static void setCaptureFilePathTemplate(String template) {
      if (renderdoc != null) {
         renderdoc.SetCaptureFilePathTemplate.call(template);
      }
   }

   public static String getCaptureFilePathTemplate() {
      return renderdoc == null ? null : renderdoc.GetCaptureFilePathTemplate.call();
   }

   public static RenderDoc.Capture getCapture(int index) {
      if (renderdoc == null) {
         return null;
      } else {
         IntByReference length = new IntByReference();
         if (renderdoc.GetCapture.call(index, null, length, null).intValue() != 1) {
            return null;
         } else {
            byte[] filename = new byte[length.getValue()];
            LongByReference timestamp = new LongByReference();
            renderdoc.GetCapture.call(index, filename, length, timestamp);
            return new RenderDoc.Capture(new String(filename, 0, filename.length - 1), Instant.ofEpochSecond(timestamp.getValue()));
         }
      }
   }

   public static int getNumCaptures() {
      return renderdoc == null ? -1 : renderdoc.GetNumCaptures.call().intValue();
   }

   public static void triggerCapture() {
      if (renderdoc != null) {
         renderdoc.TriggerCapture.call();
      }
   }

   public static void startFrameCapture() {
      if (renderdoc != null) {
         renderdoc.StartFrameCapture.call(null, null);
      }
   }

   public static boolean isFrameCapturing() {
      return renderdoc == null ? false : renderdoc.IsFrameCapturing.call().intValue() == 1;
   }

   public static void endFrameCapture() {
      if (renderdoc != null) {
         renderdoc.EndFrameCapture.call(null, null);
      }
   }

   public static boolean isReplayUIConnected() {
      return renderdoc == null ? false : renderdoc.IsTargetControlConnected.call().intValue() == 1;
   }

   public static int launchReplayUI(boolean connect) {
      return renderdoc == null ? -1 : renderdoc.LaunchReplayUI.call(new RenderdocLibrary.uint32_t(connect ? 1 : 0), null).intValue();
   }

   public static boolean showReplayUI() {
      return renderdoc == null ? false : renderdoc.ShowReplayUI.call().intValue() == 1;
   }

   public static void setCaptureComments(RenderDoc.Capture capture, String comments) {
      if (renderdoc != null) {
         renderdoc.SetCaptureFileComments.call(capture.path, comments);
      }
   }

   static {
      RenderdocLibrary.RenderdocApi apiInstance = null;
      if (Owo.DEBUG) {
         PointerByReference apiPointer = new PointerByReference();
         OS os = Util.getPlatform();
         if (os == OS.WINDOWS || os == OS.LINUX) {
            try {
               RenderdocLibrary renderdocLibrary;
               if (os == OS.WINDOWS) {
                  renderdocLibrary = (RenderdocLibrary)Native.load("renderdoc", RenderdocLibrary.class);
               } else {
                  int flags = 6;
                  if (DynamicLinkLoader.dlopen("librenderdoc.so", flags) == 0L) {
                     throw new UnsatisfiedLinkError();
                  }

                  renderdocLibrary = (RenderdocLibrary)Native.load("renderdoc", RenderdocLibrary.class, Map.of("open-flags", flags));
               }

               int initResult = renderdocLibrary.RENDERDOC_GetAPI(10500, apiPointer);
               if (initResult != 1) {
                  Owo.LOGGER.error("Could not connect to RenderDoc API, return code: {}", initResult);
               } else {
                  apiInstance = new RenderdocLibrary.RenderdocApi(apiPointer.getValue());
                  IntByReference major = new IntByReference();
                  IntByReference minor = new IntByReference();
                  IntByReference patch = new IntByReference();
                  apiInstance.GetAPIVersion.call(major, minor, patch);
                  Owo.LOGGER.info("Connected to RenderDoc API v" + major.getValue() + "." + minor.getValue() + "." + patch.getValue());
               }
            } catch (UnsatisfiedLinkError var8) {
            }
         }
      }

      renderdoc = apiInstance;
   }

   public record Capture(String path, Instant timestamp) {
   }

   public static final class CaptureOption<T> {
      public static final RenderDoc.CaptureOption<Boolean> ALLOW_VSYNC = new RenderDoc.CaptureOption<>(0, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> ALLOW_FULLSCREEN = new RenderDoc.CaptureOption<>(1, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> API_VALIDATION = new RenderDoc.CaptureOption<>(2, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> CAPTURE_CALLSTACKS = new RenderDoc.CaptureOption<>(3, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> CAPTURE_CALLSTACKS_ONLY_DRAWS = new RenderDoc.CaptureOption<>(4, Boolean.class);
      public static final RenderDoc.CaptureOption<Integer> DELAY_FOR_DEBUGGER = new RenderDoc.CaptureOption<>(5, Integer.class);
      public static final RenderDoc.CaptureOption<Boolean> VERIFY_BUFFER_ACCESS = new RenderDoc.CaptureOption<>(6, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> HOOK_INTO_CHILDREN = new RenderDoc.CaptureOption<>(7, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> REF_ALL_RESOURCES = new RenderDoc.CaptureOption<>(8, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> SAVE_ALL_INITIALS = new RenderDoc.CaptureOption<>(9, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> CAPTURE_ALL_CMD_LISTS = new RenderDoc.CaptureOption<>(10, Boolean.class);
      public static final RenderDoc.CaptureOption<Boolean> DEBUG_OUTPUT_MUTE = new RenderDoc.CaptureOption<>(11, Boolean.class);
      @Deprecated
      public static final RenderDoc.CaptureOption<?> ALLOW_UNSUPPORTED_VENDOR_EXTENSIONS = new RenderDoc.CaptureOption<>(12, Void.class);
      public final int idx;
      private final Class<T> type;

      CaptureOption(int idx, Class<T> type) {
         this.idx = idx;
         this.type = type;
      }
   }

   public static enum Key {
      ZERO(48, 48),
      ONE(49, 49),
      TWO(50, 50),
      THREE(51, 50),
      FOUR(52, 52),
      FIVE(53, 53),
      SIX(54, 54),
      SEVEN(55, 55),
      EIGHT(56, 56),
      NINE(57, 57),
      A(65, 65),
      B(66, 66),
      C(67, 67),
      D(68, 68),
      E(69, 69),
      F(70, 70),
      G(71, 71),
      H(72, 72),
      I(73, 73),
      J(74, 74),
      K(75, 75),
      L(76, 76),
      M(77, 77),
      N(78, 78),
      O(79, 79),
      P(80, 80),
      Q(81, 81),
      R(82, 82),
      S(83, 83),
      T(84, 84),
      U(85, 85),
      V(86, 86),
      W(87, 87),
      X(88, 88),
      Y(89, 89),
      Z(90, 90),
      NON_PRINTABLE(256, -1),
      DIVIDE(257, 331),
      MULTIPLY(258, 332),
      SUBTRACT(259, 333),
      PLUS(260, 334),
      F1(261, 290),
      F2(262, 291),
      F3(263, 292),
      F4(264, 293),
      F5(265, 294),
      F6(266, 295),
      F7(267, 296),
      F8(268, 297),
      F9(269, 298),
      F10(270, 299),
      F11(271, 300),
      F12(272, 301),
      HOME(273, 268),
      END(274, 269),
      INSERT(275, 260),
      DELETE(276, 261),
      PAGE_UP(277, 266),
      PAGE_DOWN(278, 267),
      BACKSPACE(279, 259),
      TAB(280, 258),
      PRINT_SCREEN(281, 283),
      PAUSE(282, 284);

      private final int keycode;
      private final int glfw;
      private static final Int2ObjectMap<RenderDoc.Key> GLFW_MAPPINGS = new Int2ObjectOpenHashMap();

      private Key(int keycode, int glfw) {
         this.keycode = keycode;
         this.glfw = glfw;
      }

      @Nullable
      public static RenderDoc.Key fromGLFW(int glfw) {
         return (RenderDoc.Key)GLFW_MAPPINGS.getOrDefault(glfw, null);
      }

      static {
         for (RenderDoc.Key key : values()) {
            if (key.glfw >= 0) {
               GLFW_MAPPINGS.put(key.glfw, key);
            }
         }
      }
   }

   public static enum OverlayOption {
      ENABLED(1),
      FRAME_RATE(2),
      FRAME_NUMBER(4),
      CAPTURE_LIST(8),
      DEFAULT,
      ALL(-1),
      NONE(0);

      public final int mask;

      private OverlayOption(int mask) {
         this.mask = mask;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         DEFAULT = new RenderDoc.OverlayOption(ENABLED.mask | FRAME_RATE.mask | FRAME_NUMBER.mask | CAPTURE_LIST.mask);
      }
   }
}
