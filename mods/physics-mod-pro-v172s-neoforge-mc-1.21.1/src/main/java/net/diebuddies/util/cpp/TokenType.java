/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnegative
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

class TokenType {
    private static final List<TokenType> TYPES = new ArrayList<TokenType>();
    private final String name;
    private final String text;

    private static void addTokenType(@Nonnegative int type, @Nonnull String name, @CheckForNull String text) {
        while (TYPES.size() <= type) {
            TYPES.add(null);
        }
        TYPES.set(type, new TokenType(name, text));
    }

    private static void addTokenType(@Nonnegative int type, @Nonnull String name) {
        TokenType.addTokenType(type, name, null);
    }

    @CheckForNull
    public static TokenType getTokenType(@Nonnegative int type) {
        try {
            return TYPES.get(type);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Nonnull
    public static String getTokenName(@Nonnegative int type) {
        if (type < 0) {
            return "Invalid" + type;
        }
        TokenType tokenType = TokenType.getTokenType(type);
        if (tokenType == null) {
            return "Unknown" + type;
        }
        return tokenType.getName();
    }

    @CheckForNull
    public static String getTokenText(@Nonnegative int type) {
        TokenType tokenType = TokenType.getTokenType(type);
        if (tokenType == null) {
            return null;
        }
        return tokenType.getText();
    }

    TokenType(@Nonnull String name, @CheckForNull String text) {
        this.name = name;
        this.text = text;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @CheckForNull
    public String getText() {
        return this.text;
    }

    static {
        for (int i = 0; i < 255; ++i) {
            String text = String.valueOf((char)i);
            TokenType.addTokenType(i, text, text);
        }
        TokenType.addTokenType(257, "AND_EQ", "&=");
        TokenType.addTokenType(258, "ARROW", "->");
        TokenType.addTokenType(259, "CHARACTER");
        TokenType.addTokenType(260, "CCOMMENT");
        TokenType.addTokenType(261, "CPPCOMMENT");
        TokenType.addTokenType(262, "DEC", "--");
        TokenType.addTokenType(263, "DIV_EQ", "/=");
        TokenType.addTokenType(264, "ELLIPSIS", "...");
        TokenType.addTokenType(265, "EOF");
        TokenType.addTokenType(266, "EQ", "==");
        TokenType.addTokenType(267, "GE", ">=");
        TokenType.addTokenType(268, "HASH", "#");
        TokenType.addTokenType(269, "HEADER");
        TokenType.addTokenType(270, "IDENTIFIER");
        TokenType.addTokenType(271, "INC", "++");
        TokenType.addTokenType(272, "NUMBER");
        TokenType.addTokenType(273, "LAND", "&&");
        TokenType.addTokenType(274, "LAND_EQ", "&&=");
        TokenType.addTokenType(275, "LE", "<=");
        TokenType.addTokenType(276, "LITERAL");
        TokenType.addTokenType(277, "LOR", "||");
        TokenType.addTokenType(278, "LOR_EQ", "||=");
        TokenType.addTokenType(279, "LSH", "<<");
        TokenType.addTokenType(280, "LSH_EQ", "<<=");
        TokenType.addTokenType(281, "MOD_EQ", "%=");
        TokenType.addTokenType(282, "MULT_EQ", "*=");
        TokenType.addTokenType(283, "NE", "!=");
        TokenType.addTokenType(284, "NL");
        TokenType.addTokenType(285, "OR_EQ", "|=");
        TokenType.addTokenType(286, "PASTE", "##");
        TokenType.addTokenType(287, "PLUS_EQ", "+=");
        TokenType.addTokenType(288, "RANGE", "..");
        TokenType.addTokenType(289, "RSH", ">>");
        TokenType.addTokenType(290, "RSH_EQ", ">>=");
        TokenType.addTokenType(291, "SQSTRING");
        TokenType.addTokenType(292, "STRING");
        TokenType.addTokenType(293, "SUB_EQ", "-=");
        TokenType.addTokenType(294, "WHITESPACE");
        TokenType.addTokenType(295, "XOR_EQ", "^=");
        TokenType.addTokenType(296, "M_ARG");
        TokenType.addTokenType(297, "M_PASTE");
        TokenType.addTokenType(298, "M_STRING");
        TokenType.addTokenType(299, "P_LINE");
        TokenType.addTokenType(300, "INVALID");
    }
}

