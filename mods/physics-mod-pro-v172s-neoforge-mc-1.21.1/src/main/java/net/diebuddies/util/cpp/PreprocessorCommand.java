/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public enum PreprocessorCommand {
    PP_DEFINE("define"),
    PP_ELIF("elif"),
    PP_ELSE("else"),
    PP_ENDIF("endif"),
    PP_ERROR("error"),
    PP_IF("if"),
    PP_IFDEF("ifdef"),
    PP_IFNDEF("ifndef"),
    PP_INCLUDE("include"),
    PP_LINE("line"),
    PP_PRAGMA("pragma"),
    PP_UNDEF("undef"),
    PP_WARNING("warning"),
    PP_INCLUDE_NEXT("include_next"),
    PP_IMPORT("import");

    private final String text;

    private PreprocessorCommand(String text) {
        this.text = text;
    }

    @CheckForNull
    public static PreprocessorCommand forText(@Nonnull String text) {
        for (PreprocessorCommand ppcmd : PreprocessorCommand.values()) {
            if (!ppcmd.text.equals(text)) continue;
            return ppcmd;
        }
        return null;
    }
}

