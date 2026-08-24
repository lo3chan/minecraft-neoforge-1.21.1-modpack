package io.wispforest.owo.renderdoc;

import com.sun.jna.Callback;
import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

interface RenderdocLibrary extends Library {
   int RENDERDOC_GetAPI(int var1, PointerByReference var2);

   @FieldOrder({"GetAPIVersion", "SetCaptureOptionU32", "SetCaptureOptionF32", "GetCaptureOptionU32", "GetCaptureOptionF32", "SetFocusToggleKeys", "SetCaptureKeys", "GetOverlayBits", "MaskOverlayBits", "RemoveHooks", "UnloadCrashHandler", "SetCaptureFilePathTemplate", "GetCaptureFilePathTemplate", "GetNumCaptures", "GetCapture", "TriggerCapture", "IsTargetControlConnected", "LaunchReplayUI", "SetActiveWindow", "StartFrameCapture", "IsFrameCapturing", "EndFrameCapture", "TriggerMultiFrameCapture", "SetCaptureFileComments", "DiscardFrameCapture", "ShowReplayUI"})
   public static class RenderdocApi extends Structure {
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_GetAPIVersion GetAPIVersion;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_SetCaptureOptionU32 SetCaptureOptionU32;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_SetCaptureOptionF32 SetCaptureOptionF32;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_GetCaptureOptionU32 GetCaptureOptionU32;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_GetCaptureOptionF32 GetCaptureOptionF32;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_SetFocusToggleKeys SetFocusToggleKeys;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_SetCaptureKeys SetCaptureKeys;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_GetOverlayBits GetOverlayBits;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_MaskOverlayBits MaskOverlayBits;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_RemoveHooks RemoveHooks;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_UnloadCrashHandler UnloadCrashHandler;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_SetCaptureFilePathTemplate SetCaptureFilePathTemplate;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_GetCaptureFilePathTemplate GetCaptureFilePathTemplate;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_GetNumCaptures GetNumCaptures;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_GetCapture GetCapture;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_TriggerCapture TriggerCapture;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_IsTargetControlConnected IsTargetControlConnected;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_LaunchReplayUI LaunchReplayUI;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_SetActiveWindow SetActiveWindow;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_StartFrameCapture StartFrameCapture;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_IsFrameCapturing IsFrameCapturing;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_EndFrameCapture EndFrameCapture;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_TriggerMultiFrameCapture TriggerMultiFrameCapture;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_SetCaptureFileComments SetCaptureFileComments;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_DiscardFrameCapture DiscardFrameCapture;
      public RenderdocLibrary.RenderdocApi.pRENDERDOC_ShowReplayUI ShowReplayUI;

      public RenderdocApi(Pointer data) {
         super(data);
         this.read();
      }

      public interface pRENDERDOC_DiscardFrameCapture extends Callback {
         void call(Pointer var1, Pointer var2);
      }

      public interface pRENDERDOC_EndFrameCapture extends Callback {
         void call(Pointer var1, Pointer var2);
      }

      public interface pRENDERDOC_GetAPIVersion extends Callback {
         void call(IntByReference var1, IntByReference var2, IntByReference var3);
      }

      public interface pRENDERDOC_GetCapture extends Callback {
         RenderdocLibrary.uint32_t call(int var1, byte[] var2, IntByReference var3, LongByReference var4);
      }

      public interface pRENDERDOC_GetCaptureFilePathTemplate extends Callback {
         String call();
      }

      public interface pRENDERDOC_GetCaptureOptionF32 extends Callback {
         float call(int var1);
      }

      public interface pRENDERDOC_GetCaptureOptionU32 extends Callback {
         RenderdocLibrary.uint32_t call(int var1);
      }

      public interface pRENDERDOC_GetNumCaptures extends Callback {
         RenderdocLibrary.uint32_t call();
      }

      public interface pRENDERDOC_GetOverlayBits extends Callback {
         RenderdocLibrary.uint32_t call();
      }

      public interface pRENDERDOC_IsFrameCapturing extends Callback {
         RenderdocLibrary.uint32_t call();
      }

      public interface pRENDERDOC_IsTargetControlConnected extends Callback {
         RenderdocLibrary.uint32_t call();
      }

      public interface pRENDERDOC_LaunchReplayUI extends Callback {
         RenderdocLibrary.uint32_t call(RenderdocLibrary.uint32_t var1, String var2);
      }

      public interface pRENDERDOC_MaskOverlayBits extends Callback {
         void call(RenderdocLibrary.uint32_t var1, RenderdocLibrary.uint32_t var2);
      }

      public interface pRENDERDOC_RemoveHooks extends Callback {
         void call();
      }

      public interface pRENDERDOC_SetActiveWindow extends Callback {
         void call(Pointer var1, Pointer var2);
      }

      public interface pRENDERDOC_SetCaptureFileComments extends Callback {
         void call(String var1, String var2);
      }

      public interface pRENDERDOC_SetCaptureFilePathTemplate extends Callback {
         void call(String var1);
      }

      public interface pRENDERDOC_SetCaptureKeys extends Callback {
         void call(int[] var1, int var2);
      }

      public interface pRENDERDOC_SetCaptureOptionF32 extends Callback {
         int call(int var1, float var2);
      }

      public interface pRENDERDOC_SetCaptureOptionU32 extends Callback {
         int call(int var1, RenderdocLibrary.uint32_t var2);
      }

      public interface pRENDERDOC_SetFocusToggleKeys extends Callback {
         void call(Pointer var1, int var2);
      }

      public interface pRENDERDOC_ShowReplayUI extends Callback {
         RenderdocLibrary.uint32_t call();
      }

      public interface pRENDERDOC_StartFrameCapture extends Callback {
         void call(Pointer var1, Pointer var2);
      }

      public interface pRENDERDOC_TriggerCapture extends Callback {
         void call();
      }

      public interface pRENDERDOC_TriggerMultiFrameCapture extends Callback {
         void call(RenderdocLibrary.uint32_t var1);
      }

      public interface pRENDERDOC_UnloadCrashHandler extends Callback {
         void call();
      }
   }

   public static class uint32_t extends IntegerType {
      public uint32_t() {
         this(0);
      }

      public uint32_t(int value) {
         super(4, value, true);
      }
   }
}
