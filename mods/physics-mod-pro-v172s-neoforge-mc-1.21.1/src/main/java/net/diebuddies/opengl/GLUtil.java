/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.lwjgl.opengl.AMDDebugOutput
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL43
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLDebugMessageAMDCallback
 *  org.lwjgl.opengl.GLDebugMessageAMDCallbackI
 *  org.lwjgl.opengl.GLDebugMessageARBCallback
 *  org.lwjgl.opengl.GLDebugMessageARBCallbackI
 *  org.lwjgl.opengl.GLDebugMessageCallback
 *  org.lwjgl.opengl.GLDebugMessageCallbackI
 *  org.lwjgl.opengl.KHRDebug
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.Callback
 */
package net.diebuddies.opengl;

import java.io.PrintStream;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.AMDDebugOutput;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageAMDCallback;
import org.lwjgl.opengl.GLDebugMessageAMDCallbackI;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Callback;

public class GLUtil {
    public static Callback setupDebugMessageCallback() {
        return GLUtil.setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
    }

    private static void trace(Consumer<String> output) {
        StackTraceElement[] elems = GLUtil.filterStackTrace(new Throwable(), 4).getStackTrace();
        for (int i = 0; i < elems.length; ++i) {
            StackTraceElement ste = elems[i];
            output.accept(ste.toString());
        }
    }

    private static void printTrace(final PrintStream stream) {
        GLUtil.trace(new Consumer<String>(){
            boolean first = true;

            @Override
            public void accept(String str) {
                if (this.first) {
                    GLUtil.printDetail(stream, "Stacktrace", str);
                    this.first = false;
                } else {
                    GLUtil.printDetailLine(stream, "Stacktrace", str);
                }
            }
        });
    }

    public static Callback setupDebugMessageCallback(PrintStream stream) {
        GLCapabilities caps = GL.getCapabilities();
        if (caps.OpenGL43) {
            APIUtil.apiLog((CharSequence)"[GL] Using OpenGL 4.3 for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                String level = severity == 33387 || severity == 37192 ? "info " : (severity == 37191 ? "warn " : "error");
                stream.println("[" + level + "] OpenGL debug message");
                GLUtil.printDetail(stream, "ID", String.format("0x%X", id));
                GLUtil.printDetail(stream, "Source", GLUtil.getDebugSource(source));
                GLUtil.printDetail(stream, "Type", GLUtil.getDebugType(type));
                GLUtil.printDetail(stream, "Severity", GLUtil.getDebugSeverity(severity));
                GLUtil.printDetail(stream, "Message", GLDebugMessageCallback.getMessage((int)length, (long)message));
                GLUtil.printTrace(stream);
            });
            GL43.glDebugMessageCallback((GLDebugMessageCallbackI)proc, (long)0L);
            if ((GL11.glGetInteger((int)33310) & 2) == 0) {
                APIUtil.apiLog((CharSequence)"[GL] Warning: A non-debug context may not produce any debug output.");
                GL11.glEnable((int)37600);
            }
            GL11.glEnable((int)33346);
            return proc;
        }
        if (caps.GL_KHR_debug) {
            APIUtil.apiLog((CharSequence)"[GL] Using KHR_debug for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                String level = severity == 33387 || severity == 37192 ? "info " : (severity == 37191 ? "warn " : "error");
                stream.println("[" + level + "] OpenGL debug message");
                GLUtil.printDetail(stream, "ID", String.format("0x%X", id));
                GLUtil.printDetail(stream, "Source", GLUtil.getDebugSource(source));
                GLUtil.printDetail(stream, "Type", GLUtil.getDebugType(type));
                GLUtil.printDetail(stream, "Severity", GLUtil.getDebugSeverity(severity));
                GLUtil.printDetail(stream, "Message", GLDebugMessageCallback.getMessage((int)length, (long)message));
                GLUtil.printTrace(stream);
            });
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)proc, (long)0L);
            if (caps.OpenGL30 && (GL11.glGetInteger((int)33310) & 2) == 0) {
                APIUtil.apiLog((CharSequence)"[GL] Warning: A non-debug context may not produce any debug output.");
                GL11.glEnable((int)37600);
            }
            GL11.glEnable((int)33346);
            return proc;
        }
        if (caps.GL_ARB_debug_output) {
            APIUtil.apiLog((CharSequence)"[GL] Using ARB_debug_output for error logging.");
            GLDebugMessageARBCallback proc = GLDebugMessageARBCallback.create((source, type, id, severity, length, message, userParam) -> {
                String level = severity == 37192 ? "info " : (severity == 37191 ? "warn " : "error");
                stream.println("[" + level + "] OpenGL debug message");
                GLUtil.printDetail(stream, "ID", String.format("0x%X", id));
                GLUtil.printDetail(stream, "Source", GLUtil.getSourceARB(source));
                GLUtil.printDetail(stream, "Type", GLUtil.getTypeARB(type));
                GLUtil.printDetail(stream, "Severity", GLUtil.getSeverityARB(severity));
                GLUtil.printDetail(stream, "Message", GLDebugMessageARBCallback.getMessage((int)length, (long)message));
                GLUtil.printTrace(stream);
            });
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)proc, (long)0L);
            GL11.glEnable((int)33346);
            return proc;
        }
        if (caps.GL_AMD_debug_output) {
            APIUtil.apiLog((CharSequence)"[GL] Using AMD_debug_output for error logging.");
            GLDebugMessageAMDCallback proc = GLDebugMessageAMDCallback.create((id, category, severity, length, message, userParam) -> {
                String level = severity == 37192 ? "info " : (severity == 37191 ? "warn " : "error");
                stream.println("[" + level + "] OpenGL debug message");
                GLUtil.printDetail(stream, "ID", String.format("0x%X", id));
                GLUtil.printDetail(stream, "Category", GLUtil.getCategoryAMD(category));
                GLUtil.printDetail(stream, "Severity", GLUtil.getSeverityAMD(severity));
                GLUtil.printDetail(stream, "Message", GLDebugMessageAMDCallback.getMessage((int)length, (long)message));
                GLUtil.printTrace(stream);
            });
            AMDDebugOutput.glDebugMessageCallbackAMD((GLDebugMessageAMDCallbackI)proc, (long)0L);
            return proc;
        }
        APIUtil.apiLog((CharSequence)"[GL] No debug output implementation is available.");
        return null;
    }

    private static void printDetail(PrintStream stream, String type, String message) {
        LogManager.getLogger().error(String.format("  %s: %s\n", type, message));
    }

    private static void printDetailLine(PrintStream stream, String type, String message) {
        Object indent = "    ";
        for (int i = 0; i < type.length(); ++i) {
            indent = (String)indent + " ";
        }
        LogManager.getLogger().error((String)indent + message);
    }

    private static String getDebugSource(int source) {
        switch (source) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken((int)source);
    }

    private static String getDebugType(int type) {
        switch (type) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
            case 33384: {
                return "MARKER";
            }
        }
        return APIUtil.apiUnknownToken((int)type);
    }

    private static String getDebugSeverity(int severity) {
        switch (severity) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
            case 33387: {
                return "NOTIFICATION";
            }
        }
        return APIUtil.apiUnknownToken((int)severity);
    }

    private static String getSourceARB(int source) {
        switch (source) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken((int)source);
    }

    private static String getTypeARB(int type) {
        switch (type) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken((int)type);
    }

    private static String getSeverityARB(int severity) {
        switch (severity) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
        }
        return APIUtil.apiUnknownToken((int)severity);
    }

    private static String getCategoryAMD(int category) {
        switch (category) {
            case 37193: {
                return "API ERROR";
            }
            case 37194: {
                return "WINDOW SYSTEM";
            }
            case 37195: {
                return "DEPRECATION";
            }
            case 37196: {
                return "UNDEFINED BEHAVIOR";
            }
            case 37197: {
                return "PERFORMANCE";
            }
            case 37198: {
                return "SHADER COMPILER";
            }
            case 37199: {
                return "APPLICATION";
            }
            case 37200: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken((int)category);
    }

    private static String getSeverityAMD(int severity) {
        switch (severity) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
        }
        return APIUtil.apiUnknownToken((int)severity);
    }

    public static <T extends Throwable> T filterStackTrace(T t, int offset) {
        StackTraceElement[] elems = t.getStackTrace();
        StackTraceElement[] filtered = new StackTraceElement[elems.length];
        int j = 0;
        for (int i = offset; i < elems.length; ++i) {
            String className = elems[i].getClassName();
            if (className == null) {
                className = "";
            }
            if (className.startsWith("org.lwjglx.") && !className.startsWith("org.lwjglx.debug.opengl") && !className.startsWith("org.lwjglx.debug.glfw")) continue;
            filtered[j++] = elems[i];
        }
        StackTraceElement[] newElems = new StackTraceElement[j];
        System.arraycopy(filtered, 0, newElems, 0, j);
        t.setStackTrace(newElems);
        return t;
    }
}

