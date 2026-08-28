/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.semver.semver;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.MetadataVersion;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.NormalVersion;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.ParseException;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Parser;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.UnexpectedCharacterException;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.Stream;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.UnexpectedElementException;
import java.util.ArrayList;
import java.util.EnumSet;

class VersionParser
implements Parser<Version> {
    private final Stream<Character> chars;

    VersionParser(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input string is NULL or empty");
        }
        Character[] elements = new Character[input.length()];
        for (int i = 0; i < input.length(); ++i) {
            elements[i] = Character.valueOf(input.charAt(i));
        }
        this.chars = new Stream<Character>(elements);
    }

    @Override
    public Version parse(String input) {
        return this.parseValidSemVer();
    }

    static Version parseValidSemVer(String version) {
        VersionParser parser = new VersionParser(version);
        return parser.parseValidSemVer();
    }

    static NormalVersion parseVersionCore(String versionCore) {
        VersionParser parser = new VersionParser(versionCore);
        return parser.parseVersionCore();
    }

    static MetadataVersion parsePreRelease(String preRelease) {
        VersionParser parser = new VersionParser(preRelease);
        return parser.parsePreRelease();
    }

    static MetadataVersion parseBuild(String build) {
        VersionParser parser = new VersionParser(build);
        return parser.parseBuild();
    }

    private Version parseValidSemVer() {
        NormalVersion normal = this.parseVersionCore();
        MetadataVersion preRelease = MetadataVersion.NULL;
        MetadataVersion build = MetadataVersion.NULL;
        Character next = this.consumeNextCharacter(CharType.HYPHEN, CharType.PLUS, CharType.EOI);
        if (CharType.HYPHEN.isMatchedBy(next)) {
            preRelease = this.parsePreRelease();
            next = this.consumeNextCharacter(CharType.PLUS, CharType.EOI);
            if (CharType.PLUS.isMatchedBy(next)) {
                build = this.parseBuild();
            }
        } else if (CharType.PLUS.isMatchedBy(next)) {
            build = this.parseBuild();
        }
        this.consumeNextCharacter(CharType.EOI);
        return new Version(normal, preRelease, build);
    }

    private NormalVersion parseVersionCore() {
        int major = Integer.parseInt(this.numericIdentifier());
        this.consumeNextCharacter(CharType.DOT);
        int minor = Integer.parseInt(this.numericIdentifier());
        this.consumeNextCharacter(CharType.DOT);
        int patch = Integer.parseInt(this.numericIdentifier());
        return new NormalVersion(major, minor, patch);
    }

    private MetadataVersion parsePreRelease() {
        this.ensureValidLookahead(CharType.DIGIT, CharType.LETTER, CharType.HYPHEN);
        ArrayList<String> idents = new ArrayList<String>();
        while (true) {
            idents.add(this.preReleaseIdentifier());
            if (!this.chars.positiveLookahead(new CharType[]{CharType.DOT})) break;
            this.consumeNextCharacter(CharType.DOT);
        }
        return new MetadataVersion(idents.toArray(new String[idents.size()]));
    }

    private String preReleaseIdentifier() {
        this.checkForEmptyIdentifier();
        CharType boundary = this.nearestCharType(CharType.DOT, CharType.PLUS, CharType.EOI);
        if (this.chars.positiveLookaheadBefore(boundary, new CharType[]{CharType.LETTER, CharType.HYPHEN})) {
            return this.alphanumericIdentifier();
        }
        return this.numericIdentifier();
    }

    private MetadataVersion parseBuild() {
        this.ensureValidLookahead(CharType.DIGIT, CharType.LETTER, CharType.HYPHEN);
        ArrayList<String> idents = new ArrayList<String>();
        while (true) {
            idents.add(this.buildIdentifier());
            if (!this.chars.positiveLookahead(new CharType[]{CharType.DOT})) break;
            this.consumeNextCharacter(CharType.DOT);
        }
        return new MetadataVersion(idents.toArray(new String[idents.size()]));
    }

    private String buildIdentifier() {
        this.checkForEmptyIdentifier();
        CharType boundary = this.nearestCharType(CharType.DOT, CharType.EOI);
        if (this.chars.positiveLookaheadBefore(boundary, new CharType[]{CharType.LETTER, CharType.HYPHEN})) {
            return this.alphanumericIdentifier();
        }
        return this.digits();
    }

    private String numericIdentifier() {
        this.checkForLeadingZeroes();
        return this.digits();
    }

    private String alphanumericIdentifier() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(this.consumeNextCharacter(CharType.DIGIT, CharType.LETTER, CharType.HYPHEN));
        } while (this.chars.positiveLookahead(new CharType[]{CharType.DIGIT, CharType.LETTER, CharType.HYPHEN}));
        return sb.toString();
    }

    private String digits() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(this.consumeNextCharacter(CharType.DIGIT));
        } while (this.chars.positiveLookahead(new CharType[]{CharType.DIGIT}));
        return sb.toString();
    }

    private CharType nearestCharType(CharType ... types) {
        for (Character chr : this.chars) {
            for (CharType type : types) {
                if (!type.isMatchedBy(chr)) continue;
                return type;
            }
        }
        return CharType.EOI;
    }

    private void checkForLeadingZeroes() {
        Character la1 = this.chars.lookahead(1);
        Character la2 = this.chars.lookahead(2);
        if (la1 != null && la1.charValue() == '0' && CharType.DIGIT.isMatchedBy(la2)) {
            throw new ParseException("Numeric identifier MUST NOT contain leading zeroes");
        }
    }

    private void checkForEmptyIdentifier() {
        Character la = this.chars.lookahead(1);
        if (CharType.DOT.isMatchedBy(la) || CharType.PLUS.isMatchedBy(la) || CharType.EOI.isMatchedBy(la)) {
            throw new ParseException("Identifiers MUST NOT be empty", new UnexpectedCharacterException(la, this.chars.currentOffset(), CharType.DIGIT, CharType.LETTER, CharType.HYPHEN));
        }
    }

    private Character consumeNextCharacter(CharType ... expected) {
        try {
            return (Character)this.chars.consume(expected);
        }
        catch (UnexpectedElementException e) {
            throw new UnexpectedCharacterException(e);
        }
    }

    private void ensureValidLookahead(CharType ... expected) {
        if (!this.chars.positiveLookahead(expected)) {
            throw new UnexpectedCharacterException(this.chars.lookahead(1), this.chars.currentOffset(), expected);
        }
    }

    static enum CharType implements Stream.ElementType<Character>
    {
        DIGIT{

            @Override
            public boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr.charValue() >= '0' && chr.charValue() <= '9';
            }
        }
        ,
        LETTER{

            @Override
            public boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr.charValue() >= 'a' && chr.charValue() <= 'z' || chr.charValue() >= 'A' && chr.charValue() <= 'Z';
            }
        }
        ,
        DOT{

            @Override
            public boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr.charValue() == '.';
            }
        }
        ,
        HYPHEN{

            @Override
            public boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr.charValue() == '-';
            }
        }
        ,
        PLUS{

            @Override
            public boolean isMatchedBy(Character chr) {
                if (chr == null) {
                    return false;
                }
                return chr.charValue() == '+';
            }
        }
        ,
        EOI{

            @Override
            public boolean isMatchedBy(Character chr) {
                return chr == null;
            }
        }
        ,
        ILLEGAL{

            @Override
            public boolean isMatchedBy(Character chr) {
                EnumSet<CharType> itself = EnumSet.of(ILLEGAL);
                for (CharType type : EnumSet.complementOf(itself)) {
                    if (!type.isMatchedBy(chr)) continue;
                    return false;
                }
                return true;
            }
        };


        static CharType forCharacter(Character chr) {
            for (CharType type : CharType.values()) {
                if (!type.isMatchedBy(chr)) continue;
                return type;
            }
            return null;
        }
    }
}

