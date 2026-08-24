package com.seibel.distanthorizons.common.render.openGl.glObject;

import com.seibel.distanthorizons.api.enums.config.EDhApiGLErrorHandlingMode;
import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.jar.EPlatform;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.objects.GLMessages.EGLMessageSeverity;
import com.seibel.distanthorizons.core.util.objects.GLMessages.EGLMessageType;
import com.seibel.distanthorizons.core.util.objects.GLMessages.GLMessage;
import com.seibel.distanthorizons.core.util.objects.GLMessages.GLMessageBuilder;
import com.seibel.distanthorizons.core.util.objects.GLMessages.GLMessageOutputStream;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IIrisAccessor;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;

public class GLProxy {
   private static final IIrisAccessor IRIS_ACCESSOR = ModAccessorInjector.INSTANCE.get(IIrisAccessor.class);
   private static final IMinecraftClientWrapper MC_CLIENT = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
   private static final AbstractDhRenderApiDefinition RENDER_API_DEF = SingletonInjector.INSTANCE.get(AbstractDhRenderApiDefinition.class);
   public static final DhLogger LOGGER;
   private static final boolean CHECK_GL_VERSION_ON_STARTUP = false;
   public static final Set<String> LOGGED_GL_MESSAGES;
   private static GLProxy instance;
   public final GLCapabilities glCapabilities;
   public boolean namedObjectSupported = false;
   public boolean bufferStorageSupported = false;
   public boolean vertexAttributeBufferBindingSupported = false;
   public boolean instancedArraysSupported = false;
   public boolean vertexAttribDivisorSupported = false;
   private final EDhApiGpuUploadMethod preferredUploadMethod;
   public final GLMessageBuilder vanillaDebugMessageBuilder = new GLMessageBuilder(type -> {
      if (type == EGLMessageType.POP_GROUP) {
         return false;
      } else if (type == EGLMessageType.PUSH_GROUP) {
         return false;
      } else {
         return type == EGLMessageType.MARKER ? false : true;
      }
   }, severity -> severity == EGLMessageSeverity.NOTIFICATION ? false : true, null);

   private GLProxy() throws IllegalStateException {
      if (RENDER_API_DEF.getRenderApi() != EDhApiRenderingApi.OPEN_GL) {
         throw new IllegalStateException(
            "[" + GLProxy.class.getSimpleName() + "] was created with the wrong Rendering API [" + RENDER_API_DEF.getRenderApi() + "]!"
         );
      } else if (GLFW.glfwGetCurrentContext() == 0L) {
         String message = "[" + GLProxy.class.getSimpleName() + "] was created outside the render thread!";
         IllegalStateException exception = new IllegalStateException(message);
         MC_CLIENT.crashMinecraft(message, exception);
         throw exception;
      } else {
         LOGGER.info("Creating [" + GLProxy.class.getSimpleName() + "]... If this is the last message you see there must have been an OpenGL error.");
         LOGGER.info("Lod Render OpenGL version [" + GL33.glGetString(7938) + "].");
         this.glCapabilities = GL.getCapabilities();
         LOGGER.info("minecraftGlCapabilities:\n" + this.versionInfoToString(this.glCapabilities));
         if (Config.Client.Advanced.Debugging.OpenGl.overrideVanillaGLLogger.get()) {
            GLUtil.setupDebugMessageCallback(new PrintStream(new GLMessageOutputStream(GLProxy::logMessage, this.vanillaDebugMessageBuilder), true));
         }

         this.namedObjectSupported = this.glCapabilities.glNamedBufferData != 0L;
         this.bufferStorageSupported = this.glCapabilities.glBufferStorage != 0L;
         if (!this.bufferStorageSupported) {
            LOGGER.info("This GPU doesn't support Buffer Storage (OpenGL 4.4), falling back to using other methods.");
         }

         this.vertexAttributeBufferBindingSupported = this.glCapabilities.glBindVertexBuffer != 0L;
         this.vertexAttribDivisorSupported = this.glCapabilities.OpenGL33;
         this.instancedArraysSupported = this.glCapabilities.GL_ARB_instanced_arrays;
         String vendor = GL33.glGetString(7936).toUpperCase();
         if (EPlatform.get() != EPlatform.MACOS) {
            if (!vendor.contains("NVIDIA") && !vendor.contains("GEFORCE")) {
               this.preferredUploadMethod = this.bufferStorageSupported ? EDhApiGpuUploadMethod.BUFFER_STORAGE : EDhApiGpuUploadMethod.DATA;
            } else {
               this.preferredUploadMethod = this.bufferStorageSupported ? EDhApiGpuUploadMethod.BUFFER_STORAGE : EDhApiGpuUploadMethod.SUB_DATA;
            }
         } else {
            this.preferredUploadMethod = EDhApiGpuUploadMethod.DATA;
         }

         LOGGER.info(
            "GPU Vendor [" + vendor + "] with OS [" + EPlatform.get().getName() + "], Preferred upload method is [" + this.preferredUploadMethod + "]."
         );
         LOGGER.info(GLProxy.class.getSimpleName() + " creation successful. OpenGL smiles upon you this day.");
      }
   }

   public static boolean hasInstance() {
      return instance != null;
   }

   public static GLProxy getInstance() throws IllegalStateException {
      if (instance == null) {
         instance = new GLProxy();
      }

      return instance;
   }

   public EDhApiGpuUploadMethod getGpuUploadMethod() {
      return this.preferredUploadMethod;
   }

   public static boolean runningOnRenderThread() {
      long currentContext = GLFW.glfwGetCurrentContext();
      return currentContext != 0L;
   }

   private static void logMessage(GLMessage glMessage) {
      EDhApiGLErrorHandlingMode errorHandlingMode = Config.Client.Advanced.Debugging.OpenGl.glErrorHandlingMode.get();
      if (errorHandlingMode != EDhApiGLErrorHandlingMode.IGNORE) {
         boolean onlyLogOnce = Config.Client.Advanced.Debugging.OpenGl.onlyLogGlErrorsOnce.get();
         if (!onlyLogOnce || LOGGED_GL_MESSAGES.add(glMessage.message)) {
            String errorMessage = "GL ERROR [" + glMessage.id + "] from [" + glMessage.source + "]: [" + glMessage.message + "].";
            if (onlyLogOnce) {
               errorMessage = errorMessage + " This message will only be logged once.";
               errorMessage = errorMessage
                  + " Note: Distant Horizons will catch and log OpenGL errors from other mods, not just DH itself; if everything is rendering correctly these errors can probably be ignored.";
            }

            RuntimeException exception = new RuntimeException(errorMessage);
            if (glMessage.type != EGLMessageType.ERROR && glMessage.type != EGLMessageType.UNDEFINED_BEHAVIOR) {
               EGLMessageSeverity severity = glMessage.severity;
               if (severity == null) {
                  severity = EGLMessageSeverity.LOW;
               }

               switch (severity) {
                  case HIGH:
                     LOGGER.error(exception.getMessage(), exception);
                     break;
                  case MEDIUM:
                     LOGGER.warn(exception.getMessage(), exception);
                     break;
                  case LOW:
                     LOGGER.info(exception.getMessage(), exception);
                     break;
                  case NOTIFICATION:
                     LOGGER.debug(exception.getMessage(), exception);
               }
            } else {
               LOGGER.error(exception.getMessage(), exception);
               if (errorHandlingMode == EDhApiGLErrorHandlingMode.LOG_THROW) {
                  throw exception;
               }
            }
         }
      }
   }

   private String getFailedVersionInfo(GLCapabilities c) {
      return "Your OpenGL support:\nopenGL version 3.3+: ["
         + c.OpenGL33
         + "] <- REQUIRED\nVertex Attribute Buffer Binding: ["
         + (c.glVertexAttribBinding != 0L)
         + "] <- optional improvement\nBuffer Storage: ["
         + (c.glBufferStorage != 0L)
         + "] <- optional improvement\nIf you noticed that your computer supports higher OpenGL versions but not the required version, try running the game in compatibility mode. (How you turn that on, I have no clue~)";
   }

   private String versionInfoToString(GLCapabilities c) {
      return "Your OpenGL support:\nopenGL version 3.3+: ["
         + c.OpenGL33
         + "] <- REQUIRED\nVertex Attribute Buffer Binding: ["
         + (c.glVertexAttribBinding != 0L)
         + "] <- optional improvement\nBuffer Storage: ["
         + (c.glBufferStorage != 0L)
         + "] <- optional improvement\n";
   }

   static {
      DhLoggerBuilder loggerBuilder = new DhLoggerBuilder();
      loggerBuilder.fileLevelConfig(Config.Common.Logging.logRendererGLEventToFile);
      boolean irisPresent = IRIS_ACCESSOR != null;
      if (!irisPresent) {
         loggerBuilder.chatLevelConfig(Config.Common.Logging.logRendererGLEventToChat);
      }

      LOGGER = loggerBuilder.build();
      if (irisPresent) {
         LOGGER.info(
            "Iris detected, Distant Horizons OpenGL error logging won't be sent in the chat due to Iris throwing known (harmless) OpenGL errors. This is a bug with Iris, not Distant Horizons."
         );
      }

      LOGGED_GL_MESSAGES = Collections.newSetFromMap(new ConcurrentHashMap<>());
      instance = null;
   }
}
