/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.Callback
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox;

import net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox.MsgBoxCallbackI;
import org.lwjgl.system.Callback;

public abstract class MsgBoxCallback
extends Callback
implements MsgBoxCallbackI {
    public static MsgBoxCallback create(MsgBoxCallbackI instance) {
        if (instance instanceof MsgBoxCallback) {
            MsgBoxCallback callback = (MsgBoxCallback)instance;
            return callback;
        }
        return new Container(instance.address(), instance);
    }

    private MsgBoxCallback(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends MsgBoxCallback {
        private final MsgBoxCallbackI delegate;

        Container(long functionPointer, MsgBoxCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long lpHelpInfo) {
            this.delegate.invoke(lpHelpInfo);
        }
    }
}

