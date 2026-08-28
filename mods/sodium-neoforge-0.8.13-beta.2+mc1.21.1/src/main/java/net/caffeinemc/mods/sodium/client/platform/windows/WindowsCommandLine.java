/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package net.caffeinemc.mods.sodium.client.platform.windows;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Objects;
import net.caffeinemc.mods.sodium.client.platform.windows.api.Kernel32;
import org.lwjgl.system.MemoryUtil;

public class WindowsCommandLine {
    private static CommandLineHook ACTIVE_COMMAND_LINE_HOOK;

    public static void setCommandLine(String modifiedCmdline) {
        if (ACTIVE_COMMAND_LINE_HOOK != null) {
            throw new IllegalStateException("Command line is already modified");
        }
        long pCmdline = Kernel32.getCommandLine();
        long pCmdlineA = Kernel32.getCommandLineA();
        String cmdline = MemoryUtil.memUTF16((long)pCmdline);
        int cmdlineLen = MemoryUtil.memLengthUTF16((CharSequence)cmdline, (boolean)true);
        String cmdlineA = MemoryUtil.memASCII((long)pCmdlineA);
        int cmdLineLenA = MemoryUtil.memLengthASCII((CharSequence)cmdlineA, (boolean)true);
        if (MemoryUtil.memLengthUTF16((CharSequence)modifiedCmdline, (boolean)true) > cmdlineLen) {
            throw new BufferOverflowException();
        }
        if (MemoryUtil.memLengthASCII((CharSequence)modifiedCmdline, (boolean)true) > cmdLineLenA) {
            throw new BufferOverflowException();
        }
        ByteBuffer buffer = MemoryUtil.memByteBuffer((long)pCmdline, (int)cmdlineLen);
        ByteBuffer bufferA = MemoryUtil.memByteBuffer((long)pCmdlineA, (int)cmdLineLenA);
        MemoryUtil.memUTF16((CharSequence)modifiedCmdline, (boolean)true, (ByteBuffer)buffer);
        MemoryUtil.memASCII((CharSequence)modifiedCmdline, (boolean)true, (ByteBuffer)bufferA);
        if (!Objects.equals(modifiedCmdline, MemoryUtil.memUTF16((long)pCmdline))) {
            throw new RuntimeException("Sanity check failed, the command line arguments did not appear to change");
        }
        if (!Objects.equals(modifiedCmdline, MemoryUtil.memASCII((long)pCmdlineA))) {
            throw new RuntimeException("Sanity check failed, the command line arguments did not appear to change");
        }
        ACTIVE_COMMAND_LINE_HOOK = new CommandLineHook(cmdline, cmdlineA, buffer, bufferA);
    }

    public static void resetCommandLine() {
        if (ACTIVE_COMMAND_LINE_HOOK != null) {
            ACTIVE_COMMAND_LINE_HOOK.uninstall();
            ACTIVE_COMMAND_LINE_HOOK = null;
        }
    }

    private static class CommandLineHook {
        private final String cmdline;
        private final String cmdlineA;
        private final ByteBuffer cmdlineBuf;
        private final ByteBuffer cmdlineBufA;
        private boolean active = true;

        private CommandLineHook(String cmdline, String cmdlineA, ByteBuffer cmdlineBuf, ByteBuffer cmdlineBufA) {
            this.cmdline = cmdline;
            this.cmdlineA = cmdlineA;
            this.cmdlineBuf = cmdlineBuf;
            this.cmdlineBufA = cmdlineBufA;
        }

        public void uninstall() {
            if (!this.active) {
                throw new IllegalStateException("Hook was already uninstalled");
            }
            MemoryUtil.memUTF16((CharSequence)this.cmdline, (boolean)true, (ByteBuffer)this.cmdlineBuf);
            MemoryUtil.memASCII((CharSequence)this.cmdlineA, (boolean)true, (ByteBuffer)this.cmdlineBufA);
            this.active = false;
        }
    }
}

