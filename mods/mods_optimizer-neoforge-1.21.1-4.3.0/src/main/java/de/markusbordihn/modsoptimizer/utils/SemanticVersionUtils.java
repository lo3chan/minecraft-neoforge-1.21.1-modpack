/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.utils;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;
import java.util.Locale;
import java.util.regex.Pattern;

public class SemanticVersionUtils {
    public static final Version EMPTY_VERSION = Version.valueOf("0.0.0");
    public static final Pattern MC_NAME_VERSION_PATTERN = Pattern.compile("[+_-]?(mc|minecraft)[1,2]{1,2}\\.\\d{1,2}\\.?[0-9x]?");
    public static final Pattern FORGE_VERSION_PATTERN = Pattern.compile("[+_-]?(forge)[+_-]?");
    public static final Pattern BUILD_VERSION_PATTERN = Pattern.compile("[_.-]?(build)[_.-]?");
    public static final Pattern LEADING_ZEROS_PATTERN = Pattern.compile("\\.0+\\d");
    public static final Pattern RELEASE_VERSION_PATTERN = Pattern.compile("[+_-]?(release)[+_-]?");
    public static final Pattern SNAPSHOT_VERSION_PATTERN = Pattern.compile("[+_-]?(snapshot)[+_-]?");
    public static final Pattern VERSION_CLEANUP_DOUBLE_PATTERN = Pattern.compile("([._+\\-\\s])[._+\\-\\s]");
    public static final Pattern VERSION_CLEANUP_START_PATTERN = Pattern.compile("(^[._+\\-\\s])");
    public static final Pattern VERSION_CLEANUP_END_PATTERN = Pattern.compile("([._+\\-\\s])$");
    public static final Pattern VERSION_PATTERN_1 = Pattern.compile("^\\d+$");
    public static final Pattern VERSION_PATTERN_2 = Pattern.compile("^\\d+\\.\\d+$");
    public static final Pattern VERSION_PATTERN_3 = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.[a-z0-9]+$");
    public static final Pattern VERSION_PATTERN_4 = Pattern.compile("^\\d+\\.\\d+\\+[A-Za-z0-9]+$");
    public static final Pattern VERSION_PATTERN_5 = Pattern.compile("^\\d+\\.\\d+\\.\\d+_[A-Za-z0-9]+$");
    public static final Pattern VERSION_PATTERN_6 = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?-\\d+\\.\\d+\\.\\d+\\.\\d+$");
    public static final Pattern VERSION_PATTERN_7 = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?-\\d+\\.\\d+\\.\\d+$");
    public static final Pattern VERSION_PATTERN_8 = Pattern.compile("^\\d+\\.\\d+-\\d+$");
    public static final Pattern VERSION_PATTERN_9 = Pattern.compile("^\\d+\\.\\d+\\.[a-z]+$");
    private static boolean debugEnabled = false;

    protected SemanticVersionUtils() {
    }

    public static void enableDebug() {
        debugEnabled = true;
    }

    public static void disableDebug() {
        debugEnabled = false;
    }

    public static Version parseVersion(String version) {
        return SemanticVersionUtils.parseVersion(version, EMPTY_VERSION);
    }

    public static Version parseVersion(String versionNumber, Version defaultVersion) {
        block6: {
            if (versionNumber != null && !versionNumber.isEmpty()) {
                try {
                    return Version.valueOf(versionNumber);
                }
                catch (Exception e) {
                    if (!debugEnabled) break block6;
                    Constants.LOG.debug("No valid semantic version {}, will try to normalize version.", (Object)versionNumber);
                }
            }
        }
        String normalizedVersion = SemanticVersionUtils.normalizeVersion(versionNumber);
        try {
            return Version.valueOf(normalizedVersion);
        }
        catch (Exception e) {
            if (debugEnabled) {
                Constants.LOG.error("Unable to parse version {} or {}, because of: {}", new Object[]{versionNumber, normalizedVersion, e});
            }
            return defaultVersion;
        }
    }

    public static String normalizeVersion(String version) {
        if (version == null || version.isEmpty()) {
            return "";
        }
        version = version.toLowerCase(Locale.ROOT);
        version = SemanticVersionUtils.removeUnnecessaryVersionParts(version);
        if (VERSION_PATTERN_1.matcher(version = SemanticVersionUtils.removeLeadingZeros(version)).matches()) {
            return version + ".0.0";
        }
        if (VERSION_PATTERN_2.matcher(version).matches()) {
            return version + ".0";
        }
        if (VERSION_PATTERN_3.matcher(version).matches()) {
            return version.substring(0, version.lastIndexOf(46)) + "-" + version.substring(version.lastIndexOf(46) + 1);
        }
        if (VERSION_PATTERN_4.matcher(version).matches()) {
            return version.substring(0, version.lastIndexOf(43)) + "." + version.substring(version.lastIndexOf(43) + 1);
        }
        if (VERSION_PATTERN_5.matcher(version).matches()) {
            return version.substring(0, version.lastIndexOf(95)) + "-" + version.substring(version.lastIndexOf(95) + 1);
        }
        if (VERSION_PATTERN_6.matcher(version).matches()) {
            version = version.substring(version.lastIndexOf(45) + 1);
            return version.substring(0, version.lastIndexOf(46)) + "-" + version.substring(version.lastIndexOf(46) + 1);
        }
        if (VERSION_PATTERN_7.matcher(version).matches()) {
            return version.substring(version.lastIndexOf(45) + 1);
        }
        if (VERSION_PATTERN_8.matcher(version).matches()) {
            return version.replace('-', '.');
        }
        if (VERSION_PATTERN_9.matcher(version).matches()) {
            Object lastVersionPart = version.substring(version.lastIndexOf(46) + 1);
            lastVersionPart = "" + Character.getNumericValue(((String)lastVersionPart).charAt(0));
            return version.substring(0, version.lastIndexOf(46)) + "." + (String)lastVersionPart;
        }
        return version;
    }

    static String removeUnnecessaryVersionParts(String version) {
        if (version == null || version.isEmpty()) {
            return "";
        }
        if (version.startsWith("1.21.1")) {
            version = version.substring("1.21.1".length());
        }
        if (MC_NAME_VERSION_PATTERN.matcher(version).find()) {
            version = version.replaceAll(MC_NAME_VERSION_PATTERN.pattern(), "");
        }
        if (FORGE_VERSION_PATTERN.matcher(version).find()) {
            version = version.replaceAll(FORGE_VERSION_PATTERN.pattern(), "");
        }
        if (BUILD_VERSION_PATTERN.matcher(version).find()) {
            version = version.replaceAll(BUILD_VERSION_PATTERN.pattern(), "-");
        }
        if (RELEASE_VERSION_PATTERN.matcher(version).find()) {
            version = version.replaceAll(RELEASE_VERSION_PATTERN.pattern(), "");
        }
        if (SNAPSHOT_VERSION_PATTERN.matcher(version).find()) {
            version = version.replaceAll(SNAPSHOT_VERSION_PATTERN.pattern(), "-");
        }
        version = version.replaceAll(VERSION_CLEANUP_START_PATTERN.pattern(), "");
        version = version.replaceAll(VERSION_CLEANUP_END_PATTERN.pattern(), "");
        version = version.replaceAll(VERSION_CLEANUP_DOUBLE_PATTERN.pattern(), "$1");
        return version;
    }

    static String removeLeadingZeros(String version) {
        if (version == null || version.isEmpty()) {
            return "";
        }
        if (version.startsWith("0.")) {
            version = version.substring(2);
        }
        if (version.startsWith("0")) {
            version = version.substring(1);
        }
        if (LEADING_ZEROS_PATTERN.matcher(version).find()) {
            CharSequence[] parts = version.split("\\.");
            for (int i = 0; i < parts.length; ++i) {
                parts[i] = SemanticVersionUtils.removeLeadingZerosFromPart((String)parts[i]);
            }
            return String.join((CharSequence)".", parts);
        }
        return version;
    }

    static String removeLeadingZerosFromPart(String part) {
        return part.replaceFirst("^0+(?!$)", "");
    }
}

