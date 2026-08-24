package net.caffeinemc.mods.sodium.client.platform.windows;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Objects;
import net.caffeinemc.mods.sodium.client.platform.windows.api.Kernel32;
import org.lwjgl.system.MemoryUtil;

public class WindowsCommandLine {
   private static WindowsCommandLine.CommandLineHook ACTIVE_COMMAND_LINE_HOOK;

   public static void setCommandLine(String modifiedCmdline) {
      if (ACTIVE_COMMAND_LINE_HOOK != null) {
         throw new IllegalStateException("Command line is already modified");
      } else {
         long pCmdline = Kernel32.getCommandLine();
         long pCmdlineA = Kernel32.getCommandLineA();
         String cmdline = MemoryUtil.memUTF16(pCmdline);
         int cmdlineLen = MemoryUtil.memLengthUTF16(cmdline, true);
         String cmdlineA = MemoryUtil.memASCII(pCmdlineA);
         int cmdLineLenA = MemoryUtil.memLengthASCII(cmdlineA, true);
         if (MemoryUtil.memLengthUTF16(modifiedCmdline, true) > cmdlineLen) {
            throw new BufferOverflowException();
         } else if (MemoryUtil.memLengthASCII(modifiedCmdline, true) > cmdLineLenA) {
            throw new BufferOverflowException();
         } else {
            ByteBuffer buffer = MemoryUtil.memByteBuffer(pCmdline, cmdlineLen);
            ByteBuffer bufferA = MemoryUtil.memByteBuffer(pCmdlineA, cmdLineLenA);
            MemoryUtil.memUTF16(modifiedCmdline, true, buffer);
            MemoryUtil.memASCII(modifiedCmdline, true, bufferA);
            if (!Objects.equals(modifiedCmdline, MemoryUtil.memUTF16(pCmdline))) {
               throw new RuntimeException("Sanity check failed, the command line arguments did not appear to change");
            } else if (!Objects.equals(modifiedCmdline, MemoryUtil.memASCII(pCmdlineA))) {
               throw new RuntimeException("Sanity check failed, the command line arguments did not appear to change");
            } else {
               ACTIVE_COMMAND_LINE_HOOK = new WindowsCommandLine.CommandLineHook(cmdline, cmdlineA, buffer, bufferA);
            }
         }
      }
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
         } else {
            MemoryUtil.memUTF16(this.cmdline, true, this.cmdlineBuf);
            MemoryUtil.memASCII(this.cmdlineA, true, this.cmdlineBufA);
            this.active = false;
         }
      }
   }
}
