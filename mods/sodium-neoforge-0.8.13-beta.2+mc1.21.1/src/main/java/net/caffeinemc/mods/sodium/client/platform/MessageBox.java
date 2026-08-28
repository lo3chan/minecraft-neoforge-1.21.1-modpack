/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.caffeinemc.mods.sodium.client.platform;

import java.nio.ByteBuffer;
import java.util.Objects;
import net.caffeinemc.mods.sodium.client.compatibility.environment.OsUtils;
import net.caffeinemc.mods.sodium.client.platform.NativeWindowHandle;
import net.caffeinemc.mods.sodium.client.platform.windows.api.Shell32;
import net.caffeinemc.mods.sodium.client.platform.windows.api.User32;
import net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox.MsgBoxCallback;
import net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox.MsgBoxParamSw;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class MessageBox {
    @Nullable
    private static final MessageBoxImpl IMPL = MessageBoxImpl.chooseImpl();

    public static void showMessageBox(NativeWindowHandle window, IconType icon, String title, String description, @Nullable String helpUrl) {
        if (IMPL != null) {
            IMPL.showMessageBox(window, icon, title, description, helpUrl);
        }
    }

    private static interface MessageBoxImpl {
        @Nullable
        public static MessageBoxImpl chooseImpl() {
            if (OsUtils.getOs() == OsUtils.OperatingSystem.WIN) {
                return new WindowsMessageBoxImpl();
            }
            return null;
        }

        public void showMessageBox(NativeWindowHandle var1, IconType var2, String var3, String var4, @Nullable String var5);
    }

    public static enum IconType {
        INFO,
        WARNING,
        ERROR;

    }

    private static class WindowsMessageBoxImpl
    implements MessageBoxImpl {
        private WindowsMessageBoxImpl() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void showMessageBox(NativeWindowHandle window, IconType icon, String title, String description, @Nullable String helpUrl) {
            Objects.requireNonNull(title);
            Objects.requireNonNull(description);
            Objects.requireNonNull(icon);
            MsgBoxCallback msgBoxCallback = helpUrl != null ? MsgBoxCallback.create(lpHelpInfo -> Shell32.browseUrl(window, helpUrl)) : null;
            long hWndOwner = window != null ? window.getWin32Handle() : 0L;
            try (MemoryStack stack = MemoryStack.stackPush();){
                ByteBuffer lpText = stack.malloc(MemoryUtil.memLengthUTF16((CharSequence)description, (boolean)true));
                MemoryUtil.memUTF16((CharSequence)description, (boolean)true, (ByteBuffer)lpText);
                ByteBuffer lpCaption = stack.malloc(MemoryUtil.memLengthUTF16((CharSequence)title, (boolean)true));
                MemoryUtil.memUTF16((CharSequence)title, (boolean)true, (ByteBuffer)lpCaption);
                MsgBoxParamSw params = MsgBoxParamSw.allocate(stack);
                params.setCbSize(MsgBoxParamSw.SIZEOF);
                params.setHWndOwner(hWndOwner);
                params.setText(lpText);
                params.setCaption(lpCaption);
                params.setStyle(WindowsMessageBoxImpl.getStyle(icon, msgBoxCallback != null));
                params.setCallback(msgBoxCallback);
                User32.callMessageBoxIndirectW(params);
            }
            finally {
                if (msgBoxCallback != null) {
                    msgBoxCallback.free();
                }
            }
        }

        private static int getStyle(IconType icon, boolean showHelp) {
            int style;
            switch (icon.ordinal()) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: {
                    int n = 64;
                    break;
                }
                case 1: {
                    int n = 48;
                    break;
                }
                case 2: {
                    int n = style = 16;
                }
            }
            if (showHelp) {
                style |= 0x4000;
            }
            return style;
        }
    }
}

