/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.diebuddies.bridge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.Function;
import net.diebuddies.bridge.ArrayBackedEvent;
import net.diebuddies.bridge.Event;

public final class EventFactoryImpl {
    private static final List<ArrayBackedEvent<?>> ARRAY_BACKED_EVENTS = new ObjectArrayList();

    private EventFactoryImpl() {
    }

    public static void invalidate() {
        ARRAY_BACKED_EVENTS.forEach(ArrayBackedEvent::update);
    }

    public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
        ArrayBackedEvent<? super T> event = new ArrayBackedEvent<T>(type, invokerFactory);
        ARRAY_BACKED_EVENTS.add(event);
        return event;
    }

    private static <T> T buildEmptyInvoker(Class<T> handlerClass, Function<T[], T> invokerSetup) {
        Method funcIfMethod = null;
        for (Method m : handlerClass.getMethods()) {
            if ((m.getModifiers() & 0x802) != 0) continue;
            if (funcIfMethod != null) {
                throw new IllegalStateException("Multiple virtual methods in " + String.valueOf(handlerClass) + "; cannot build empty invoker!");
            }
            funcIfMethod = m;
        }
        if (funcIfMethod == null) {
            throw new IllegalStateException("No virtual methods in " + String.valueOf(handlerClass) + "; cannot build empty invoker!");
        }
        Object defValue = null;
        try {
            MethodHandle target = MethodHandles.lookup().unreflect(funcIfMethod);
            MethodType type = target.type().dropParameterTypes(0, 1);
            if (type.returnType() != Void.TYPE) {
                MethodType objTargetType = MethodType.genericMethodType(type.parameterCount()).changeReturnType((Class<?>)type.returnType()).insertParameterTypes(0, new Class[]{target.type().parameterType(0)});
                MethodHandle objTarget = MethodHandles.explicitCastArguments(target, objTargetType);
                Object[] args2 = new Object[target.type().parameterCount()];
                args2[0] = invokerSetup.apply((Object[][])((Object[])Array.newInstance(handlerClass, 0)));
                defValue = objTarget.invokeWithArguments(args2);
            }
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
        Object returnValue = defValue;
        return (T)Proxy.newProxyInstance(EventFactoryImpl.class.getClassLoader(), new Class[]{handlerClass}, (proxy, method, args) -> returnValue);
    }
}

