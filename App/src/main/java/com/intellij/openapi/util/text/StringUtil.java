/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.util.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;


public class StringUtil extends StringUtilRt {

    @NotNull
    @Contract(pure = true)
    public static String replace(@NonNls @NotNull String text, @NonNls @NotNull String oldS, @NonNls @NotNull String newS) {
        return replace(text, oldS, newS, false);
    }


    @Contract(pure = true)
    public static String replace(@NonNls @NotNull final String text, @NonNls @NotNull final String oldS, @NonNls @NotNull final String newS, final boolean ignoreCase) {
        if (text.length() < oldS.length()) return text;

        StringBuilder newText = null;
        int i = 0;

        while (i < text.length()) {
            final int index = ignoreCase ? indexOfIgnoreCase(text, oldS, i) : text.indexOf(oldS, i);
            if (index < 0) {
                if (i == 0) {
                    return text;
                }

                newText.append(text, i, text.length());
                break;
            } else {
                if (newText == null) {
                    if (text.length() == oldS.length()) {
                        return newS;
                    }
                    newText = new StringBuilder(text.length() - i);
                }

                newText.append(text, i, index);
                newText.append(newS);
                i = index + oldS.length();
            }
        }
        return newText != null ? newText.toString() : "";
    }

    /**
     * Implementation copied from {@link String#indexOf(String, int)} except character comparisons made case insensitive
     */
    @Contract(pure = true)
    public static int indexOfIgnoreCase(@NotNull String where, @NotNull String what, int fromIndex) {
        int targetCount = what.length();
        int sourceCount = where.length();

        if (fromIndex >= sourceCount) {
            return targetCount == 0 ? sourceCount : -1;
        }

        if (fromIndex < 0) {
            fromIndex = 0;
        }

        if (targetCount == 0) {
            return fromIndex;
        }

        char first = what.charAt(0);
        int max = sourceCount - targetCount;

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (!charsEqualIgnoreCase(where.charAt(i), first)) {
                while (++i <= max && !charsEqualIgnoreCase(where.charAt(i), first)) ;
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = 1; j < end && charsEqualIgnoreCase(where.charAt(j), what.charAt(k)); j++, k++) ;

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }

        return -1;
    }

   @Contract(pure = true)
    public static boolean containsIgnoreCase(@NotNull String where, @NotNull String what) {
        return indexOfIgnoreCase(where, what, 0) >= 0;
    }

    @Contract(pure = true)
    public static boolean endsWithIgnoreCase(@NonNls @NotNull String str, @NonNls @NotNull String suffix) {
        return StringUtilRt.endsWithIgnoreCase(str, suffix);
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static String toLowerCase(@Nullable final String str) {
        //noinspection ConstantConditions
        return str == null ? null : str.toLowerCase();
    }

    @NotNull
    @Contract(pure = true)
    public static String getPackageName(@NotNull String fqName) {
        return getPackageName(fqName, '.');
    }

    /**
     * Given a fqName returns the package name for the type or the containing type.
     * <p/>
     * <ul>
     * <li><code>java.lang.String</code> -> <code>java.lang</code></li>
     * <li><code>java.util.Map.Entry</code> -> <code>java.util.Map</code></li>
     * </ul>
     *
     * @param fqName    a fully qualified type name. Not supposed to contain any type arguments
     * @param separator the separator to use. Typically '.'
     * @return the package name of the type or the declarator of the type. The empty string if the given fqName is unqualified
     */
    @NotNull
    @Contract(pure = true)
    public static String getPackageName(@NotNull String fqName, char separator) {
        int lastPointIdx = fqName.lastIndexOf(separator);
        if (lastPointIdx >= 0) {
            return fqName.substring(0, lastPointIdx);
        }
        return "";
    }

    @Contract(pure = true)
    public static int getLineBreakCount(@NotNull CharSequence text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                count++;
            } else if (c == '\r') {
                if (i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    //noinspection AssignmentToForLoopParameter
                    i++;
                    count++;
                } else {
                    count++;
                }
            }
        }
        return count;
    }




    public static void escapeStringCharacters(int length, @NotNull String str, @NotNull @NonNls StringBuilder buffer) {
        escapeStringCharacters(length, str, "\"", buffer);
    }

    @NotNull
    public static StringBuilder escapeStringCharacters(int length,
                                                       @NotNull String str,
                                                       @Nullable String additionalChars,
                                                       @NotNull @NonNls StringBuilder buffer) {
        return escapeStringCharacters(length, str, additionalChars, true, buffer);
    }

    @NotNull
    public static StringBuilder escapeStringCharacters(int length,
                                                       @NotNull String str,
                                                       @Nullable String additionalChars,
                                                       boolean escapeSlash,
                                                       @NotNull @NonNls StringBuilder buffer) {
        return escapeStringCharacters(length, str, additionalChars, escapeSlash, true, buffer);
    }

    @NotNull
    public static StringBuilder escapeStringCharacters(int length,
                                                       @NotNull String str,
                                                       @Nullable String additionalChars,
                                                       boolean escapeSlash,
                                                       boolean escapeUnicode,
                                                       @NotNull @NonNls StringBuilder buffer) {
        char prev = 0;
        for (int idx = 0; idx < length; idx++) {
            char ch = str.charAt(idx);
            switch (ch) {
                case '\b':
                    buffer.append("\\b");
                    break;

                case '\t':
                    buffer.append("\\t");
                    break;

                case '\n':
                    buffer.append("\\n");
                    break;

                case '\f':
                    buffer.append("\\f");
                    break;

                case '\r':
                    buffer.append("\\r");
                    break;

                default:
                    if (escapeSlash && ch == '\\') {
                        buffer.append("\\\\");
                    } else if (additionalChars != null && additionalChars.indexOf(ch) > -1 && (escapeSlash || prev != '\\')) {
                        buffer.append("\\").append(ch);
                    } else if (escapeUnicode && !isPrintableUnicode(ch)) {
                        CharSequence hexCode = StringUtilRt.toUpperCase(Integer.toHexString(ch));
                        buffer.append("\\u");
                        int paddingCount = 4 - hexCode.length();
                        while (paddingCount-- > 0) {
                            buffer.append(0);
                        }
                        buffer.append(hexCode);
                    } else {
                        buffer.append(ch);
                    }
            }
            prev = ch;
        }
        return buffer;
    }

    @Contract(pure = true)
    public static boolean isPrintableUnicode(char c) {
        int t = Character.getType(c);
        return t != Character.UNASSIGNED && t != Character.LINE_SEPARATOR && t != Character.PARAGRAPH_SEPARATOR &&
                t != Character.CONTROL && t != Character.FORMAT && t != Character.PRIVATE_USE && t != Character.SURROGATE;
    }

    @NotNull
    @Contract(pure = true)
    public static String escapeStringCharacters(@NotNull String s) {
        StringBuilder buffer = new StringBuilder(s.length());
        escapeStringCharacters(s.length(), s, "\"", buffer);
        return buffer.toString();
    }

    @NotNull
    @Contract(pure = true)
    public static String escapeCharCharacters(@NotNull String s) {
        StringBuilder buffer = new StringBuilder(s.length());
        escapeStringCharacters(s.length(), s, "\'", buffer);
        return buffer.toString();
    }


    private static boolean isQuoteAt(@NotNull String s, int ind) {
        char ch = s.charAt(ind);
        return ch == '\'' || ch == '\"';
    }

    @Contract(pure = true)
    public static boolean isQuotedString(@NotNull String s) {
        return s.length() > 1 && isQuoteAt(s, 0) && s.charAt(0) == s.charAt(s.length() - 1);
    }

    @NotNull
    @Contract(pure = true)
    public static String unquoteString(@NotNull String s) {
        if (isQuotedString(s)) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }


    @NotNull
    @Contract(pure = true)
    public static String capitalize(@NotNull String s) {
        if (s.isEmpty()) return s;
        if (s.length() == 1) return StringUtilRt.toUpperCase(s).toString();

        // Optimization
        if (Character.isUpperCase(s.charAt(0))) return s;
        return toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Equivalent to string.startsWith(prefixes[0] + prefixes[1] + ...) but avoids creating an object for concatenation.
     */
    @Contract(pure = true)
    public static boolean startsWithConcatenation(@NotNull String string, @NotNull String... prefixes) {
        int offset = 0;
        for (String prefix : prefixes) {
            int prefixLen = prefix.length();
            if (!string.regionMatches(offset, prefix, 0, prefixLen)) {
                return false;
            }
            offset += prefixLen;
        }
        return true;
    }


    @NotNull
    @Contract(pure = true)
    public static String trimEnd(@NotNull String s, @NonNls @NotNull String suffix) {
        return trimEnd(s, suffix, false);
    }

    @NotNull
    @Contract(pure = true)
    public static String trimEnd(@NotNull String s, @NonNls @NotNull String suffix, boolean ignoreCase) {
        boolean endsWith = ignoreCase ? endsWithIgnoreCase(s, suffix) : s.endsWith(suffix);
        if (endsWith) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    @NotNull
    @Contract(pure = true)
    public static String trimEnd(@NotNull String s, char suffix) {
        if (endsWithChar(s, suffix)) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    @Contract(pure = true)
    public static boolean endsWithChar(@Nullable CharSequence s, char suffix) {
        return StringUtilRt.endsWithChar(s, suffix);
    }

    @NotNull
    @Contract(pure = true)
    public static String trimStart(@NotNull String s, @NonNls @NotNull String prefix) {
        if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }


    @Contract(value = "null -> false", pure = true)
    public static boolean isNotEmpty(@Nullable String s) {
        return s != null && !s.isEmpty();
    }

    @Contract(value = "null -> true", pure = true)
    public static boolean isEmpty(@Nullable String s) {
        return s == null || s.isEmpty();
    }

    @Contract(value = "null -> true", pure = true)
    public static boolean isEmpty(@Nullable CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    @Contract(pure = true)
    public static int length(@Nullable CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    @Contract(value = "null -> true", pure = true)
    // we need to keep this method to preserve backward compatibility
    public static boolean isEmptyOrSpaces(@Nullable String s) {
        return isEmptyOrSpaces((CharSequence) s);
    }

    @Contract(value = "null -> true", pure = true)
    public static boolean isEmptyOrSpaces(@Nullable CharSequence s) {
        if (isEmpty(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Allows to answer if given symbol is white space, tabulation or line feed.
     *
     * @param c symbol to check
     * @return <code>true</code> if given symbol is white space, tabulation or line feed; <code>false</code> otherwise
     */
    @Contract(pure = true)
    public static boolean isWhiteSpace(char c) {
        return c == '\n' || c == '\t' || c == ' ';
    }


    @NotNull
    @Contract(pure = true)
    public static String repeat(@NotNull String s, int count) {
        assert count >= 0 : count;
        StringBuilder sb = new StringBuilder(s.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    @NotNull
    @Contract(pure = true)
    public static List<String> splitHonorQuotes(@NotNull String s, char separator) {
        final List<String> result = new ArrayList<String>();
        final StringBuilder builder = new StringBuilder(s.length());
        boolean inQuotes = false;
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c == separator && !inQuotes) {
                if (builder.length() > 0) {
                    result.add(builder.toString());
                    builder.setLength(0);
                }
                continue;
            }

            if ((c == '"' || c == '\'') && !(i > 0 && s.charAt(i - 1) == '\\')) {
                inQuotes = !inQuotes;
            }
            builder.append(c);
        }

        if (builder.length() > 0) {
            result.add(builder.toString());
        }
        return result;
    }


    @NotNull
    @Contract(pure = true)
    public static List<String> split(@NotNull String s, @NotNull String separator) {
        return split(s, separator, true);
    }

    @NotNull
    @Contract(pure = true)
    public static List<CharSequence> split(@NotNull CharSequence s, @NotNull CharSequence separator) {
        return split(s, separator, true, true);
    }

    @NotNull
    @Contract(pure = true)
    public static List<String> split(@NotNull String s, @NotNull String separator,
                                     boolean excludeSeparator) {
        return split(s, separator, excludeSeparator, true);
    }

    @NotNull
    @Contract(pure = true)
    public static List<String> split(@NotNull String s, @NotNull String separator,
                                     boolean excludeSeparator, boolean excludeEmptyStrings) {
        return (List) split((CharSequence) s, separator, excludeSeparator, excludeEmptyStrings);
    }

    @NotNull
    @Contract(pure = true)
    public static List<CharSequence> split(@NotNull CharSequence s, @NotNull CharSequence separator,
                                           boolean excludeSeparator, boolean excludeEmptyStrings) {
        if (separator.length() == 0) {
            return Collections.singletonList(s);
        }
        List<CharSequence> result = new ArrayList<CharSequence>();
        int pos = 0;
        while (true) {
            int index = indexOf(s, separator, pos);
            if (index == -1) break;
            final int nextPos = index + separator.length();
            CharSequence token = s.subSequence(pos, excludeSeparator ? index : nextPos);
            if (token.length() != 0 || !excludeEmptyStrings) {
                result.add(token);
            }
            pos = nextPos;
        }
        if (pos < s.length() || !excludeEmptyStrings && pos == s.length()) {
            result.add(s.subSequence(pos, s.length()));
        }
        return result;
    }


    @NotNull
    @Contract(pure = true)
    public static String[] surround(@NotNull String[] strings1, String prefix, String suffix) {
        String[] result = new String[strings1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = prefix + strings1[i] + suffix;
        }

        return result;
    }




    @Contract(pure = true)
    public static boolean containsAlphaCharacters(@NotNull String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i))) return true;
        }
        return false;
    }

    @Contract(pure = true)
    public static boolean startsWith(@NotNull CharSequence text, int startIndex, @NotNull CharSequence prefix) {
        int l1 = text.length() - startIndex;
        int l2 = prefix.length();
        if (l1 < l2) return false;

        for (int i = 0; i < l2; i++) {
            if (text.charAt(i + startIndex) != prefix.charAt(i)) return false;
        }

        return true;
    }

    @Contract(pure = true)
    public static boolean endsWith(@NotNull CharSequence text, @NotNull CharSequence suffix) {
        int l1 = text.length();
        int l2 = suffix.length();
        if (l1 < l2) return false;

        for (int i = l1 - 1; i >= l1 - l2; i--) {
            if (text.charAt(i) != suffix.charAt(i + l2 - l1)) return false;
        }

        return true;
    }

    @Contract(pure = true)
    public static int commonPrefixLength(@NotNull CharSequence s1, @NotNull CharSequence s2) {
        int i;
        int minLength = min(s1.length(), s2.length());
        for (i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        return i;
    }

    @Contract(pure = true)
    public static int commonSuffixLength(@NotNull CharSequence s1, @NotNull CharSequence s2) {
        int s1Length = s1.length();
        int s2Length = s2.length();
        if (s1Length == 0 || s2Length == 0) return 0;
        int i;
        for (i = 0; i < s1Length && i < s2Length; i++) {
            if (s1.charAt(s1Length - i - 1) != s2.charAt(s2Length - i - 1)) {
                break;
            }
        }
        return i;
    }

    /**
     * Allows to answer if target symbol is contained at given char sequence at <code>[start; end)</code> interval.
     *
     * @param s     target char sequence to check
     * @param start start offset to use within the given char sequence (inclusive)
     * @param end   end offset to use within the given char sequence (exclusive)
     * @param c     target symbol to check
     * @return <code>true</code> if given symbol is contained at the target range of the given char sequence;
     * <code>false</code> otherwise
     */
    @Contract(pure = true)
    public static boolean contains(@NotNull CharSequence s, int start, int end, char c) {
        return indexOf(s, c, start, end) >= 0;
    }

    @Contract(pure = true)
    public static int indexOf(@NotNull CharSequence s, char c, int start) {
        return indexOf(s, c, start, s.length());
    }

    @Contract(pure = true)
    public static int indexOf(@NotNull CharSequence s, char c, int start, int end) {
        end = min(end, s.length());
        for (int i = max(start, 0); i < end; i++) {
            if (s.charAt(i) == c) return i;
        }
        return -1;
    }

    @Contract(pure = true)
    public static boolean contains(@NotNull CharSequence sequence, @NotNull CharSequence infix) {
        return indexOf(sequence, infix) >= 0;
    }

    @Contract(pure = true)
    public static int indexOf(@NotNull CharSequence sequence, @NotNull CharSequence infix) {
        return indexOf(sequence, infix, 0);
    }

    @Contract(pure = true)
    public static int indexOf(@NotNull CharSequence sequence, @NotNull CharSequence infix, int start) {
        for (int i = start; i <= sequence.length() - infix.length(); i++) {
            if (startsWith(sequence, i, infix)) {
                return i;
            }
        }
        return -1;
    }

    @Contract(pure = true)
    public static int indexOfSubstringEnd(@NotNull String text, @NotNull String subString) {
        int i = text.indexOf(subString);
        if (i == -1) return -1;
        return i + subString.length();
    }


    @NotNull
    @Contract(pure = true)
    public static String first(@NotNull String text, final int maxLength, final boolean appendEllipsis) {
        return text.length() > maxLength ? text.substring(0, maxLength) + (appendEllipsis ? "..." : "") : text;
    }

    @NotNull
    @Contract(pure = true)
    public static CharSequence first(@NotNull CharSequence text, final int length, final boolean appendEllipsis) {
        return text.length() > length ? text.subSequence(0, length) + (appendEllipsis ? "..." : "") : text;
    }

    @NotNull
    @Contract(pure = true)
    public static CharSequence last(@NotNull CharSequence text, final int length, boolean prependEllipsis) {
        return text.length() > length ? (prependEllipsis ? "..." : "") + text.subSequence(text.length() - length, text.length()) : text;
    }

    @NotNull
    @Contract(pure = true)
    public static String firstLast(@NotNull String text, int length) {
        return text.length() > length
                ? text.subSequence(0, length / 2) + "\u2026" + text.subSequence(text.length() - length / 2 - 1, text.length())
                : text;
    }

    @NotNull
    @Contract(pure = true)
    public static String replace(@NotNull String text, @NotNull String[] from, @NotNull String[] to) {
        return replace(text, Arrays.asList(from), Arrays.asList(to));
    }

    @NotNull
    @Contract(pure = true)
    public static String replace(@NotNull String text, @NotNull List<String> from, @NotNull List<String> to) {
        assert from.size() == to.size();
        final StringBuilder result = new StringBuilder(text.length());
        replace:
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < from.size(); j += 1) {
                String toReplace = from.get(j);
                String replaceWith = to.get(j);

                final int len = toReplace.length();
                if (text.regionMatches(i, toReplace, 0, len)) {
                    result.append(replaceWith);
                    i += len - 1;
                    continue replace;
                }
            }
            result.append(text.charAt(i));
        }
        return result.toString();
    }

    @Contract(pure = true)
    public static int countChars(@NotNull CharSequence text, char c) {
        return countChars(text, c, 0, false);
    }

    @Contract(pure = true)
    public static int countChars(@NotNull CharSequence text, char c, int offset, boolean stopAtOtherChar) {
        return countChars(text, c, offset, text.length(), stopAtOtherChar);
    }

    @Contract(pure = true)
    public static int countChars(@NotNull CharSequence text, char c, int start, int end, boolean stopAtOtherChar) {
        int count = 0;
        for (int i = start, len = min(text.length(), end); i < len; ++i) {
            if (text.charAt(i) == c) {
                count++;
            } else if (stopAtOtherChar) {
                break;
            }
        }
        return count;
    }


    @Nullable
    @Contract(pure = true)
    public static String getPropertyName(@NonNls @NotNull String methodName) {
        if (methodName.startsWith("get")) {
            return Introspector.decapitalize(methodName.substring(3));
        }
        if (methodName.startsWith("is")) {
            return Introspector.decapitalize(methodName.substring(2));
        }
        if (methodName.startsWith("set")) {
            return Introspector.decapitalize(methodName.substring(3));
        }
        return null;
    }

    @Contract(pure = true)
    public static String getQualifiedName(@Nullable String packageName, String className) {
        if (packageName == null || packageName.isEmpty()) {
            return className;
        }
        return packageName + '.' + className;
    }

    @Contract(pure = true)
    public static int compareVersionNumbers(@Nullable String v1, @Nullable String v2) {
        // todo duplicates com.intellij.util.text.VersionComparatorUtil.compare
        // todo please refactor next time you make changes here
        if (v1 == null && v2 == null) {
            return 0;
        }
        if (v1 == null) {
            return -1;
        }
        if (v2 == null) {
            return 1;
        }

        String[] part1 = v1.split("[\\.\\_\\-]");
        String[] part2 = v2.split("[\\.\\_\\-]");

        int idx = 0;
        for (; idx < part1.length && idx < part2.length; idx++) {
            String p1 = part1[idx];
            String p2 = part2[idx];

            int cmp;
            if (p1.matches("\\d+") && p2.matches("\\d+")) {
                cmp = new Integer(p1).compareTo(new Integer(p2));
            } else {
                cmp = part1[idx].compareTo(part2[idx]);
            }
            if (cmp != 0) return cmp;
        }

        if (part1.length == part2.length) {
            return 0;
        } else {
            boolean left = part1.length > idx;
            String[] parts = left ? part1 : part2;

            for (; idx < parts.length; idx++) {
                String p = parts[idx];
                int cmp;
                if (p.matches("\\d+")) {
                    cmp = new Integer(p).compareTo(0);
                } else {
                    cmp = 1;
                }
                if (cmp != 0) return left ? cmp : -cmp;
            }
            return 0;
        }
    }

    @NotNull
    @Contract(pure = true)
    public static String sanitizeJavaIdentifier(@NotNull String name) {
        final StringBuilder result = new StringBuilder(name.length());

        for (int i = 0; i < name.length(); i++) {
            final char ch = name.charAt(i);
            if (Character.isJavaIdentifierPart(ch)) {
                if (result.length() == 0 && !Character.isJavaIdentifierStart(ch)) {
                    result.append("_");
                }
                result.append(ch);
            }
        }

        return result.toString();
    }

    @Contract(pure = true)
    public static int compare(@Nullable String s1, @Nullable String s2, boolean ignoreCase) {
        //noinspection StringEquality
        if (s1 == s2) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return ignoreCase ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
    }

    @Contract(pure = true)
    public static boolean equals(@Nullable CharSequence s1, @Nullable CharSequence s2) {
        if (s1 == null ^ s2 == null) {
            return false;
        }

        if (s1 == null) {
            return true;
        }

        if (s1.length() != s2.length()) {
            return false;
        }
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static boolean equalsIgnoreWhitespaces(@Nullable CharSequence s1, @Nullable CharSequence s2) {
        if (s1 == null ^ s2 == null) {
            return false;
        }

        if (s1 == null) {
            return true;
        }

        int len1 = s1.length();
        int len2 = s2.length();

        int index1 = 0;
        int index2 = 0;
        while (index1 < len1 && index2 < len2) {
            if (s1.charAt(index1) == s2.charAt(index2)) {
                index1++;
                index2++;
                continue;
            }

            boolean skipped = false;
            while (index1 != len1 && isWhiteSpace(s1.charAt(index1))) {
                skipped = true;
                index1++;
            }
            while (index2 != len2 && isWhiteSpace(s2.charAt(index2))) {
                skipped = true;
                index2++;
            }

            if (!skipped) return false;
        }

        for (; index1 != len1; index1++) {
            if (!isWhiteSpace(s1.charAt(index1))) return false;
        }
        for (; index2 != len2; index2++) {
            if (!isWhiteSpace(s2.charAt(index2))) return false;
        }

        return true;
    }

    @Contract(pure = true)
    public static int compare(char c1, char c2, boolean ignoreCase) {
        // duplicating String.equalsIgnoreCase logic
        int d = c1 - c2;
        if (d == 0 || !ignoreCase) {
            return d;
        }
        // If characters don't match but case may be ignored,
        // try converting both characters to uppercase.
        // If the results match, then the comparison scan should
        // continue.
        char u1 = StringUtilRt.toUpperCase(c1);
        char u2 = StringUtilRt.toUpperCase(c2);
        d = u1 - u2;
        if (d != 0) {
            // Unfortunately, conversion to uppercase does not work properly
            // for the Georgian alphabet, which has strange rules about case
            // conversion.  So we need to make one last check before
            // exiting.
            d = StringUtilRt.toLowerCase(u1) - StringUtilRt.toLowerCase(u2);
        }
        return d;
    }



    @NotNull
    @Contract(pure = true)
    public static String shortenTextWithEllipsis(@NotNull final String text, final int maxLength, final int suffixLength) {
        return shortenTextWithEllipsis(text, maxLength, suffixLength, false);
    }

    @NotNull
    @Contract(pure = true)
    public static String shortenTextWithEllipsis(@NotNull final String text,
                                                 final int maxLength,
                                                 final int suffixLength,
                                                 @NotNull String symbol) {
        final int textLength = text.length();
        if (textLength > maxLength) {
            final int prefixLength = maxLength - suffixLength - symbol.length();
            assert prefixLength > 0;
            return text.substring(0, prefixLength) + symbol + text.substring(textLength - suffixLength);
        } else {
            return text;
        }
    }

    @NotNull
    @Contract(pure = true)
    public static String shortenTextWithEllipsis(@NotNull final String text,
                                                 final int maxLength,
                                                 final int suffixLength,
                                                 boolean useEllipsisSymbol) {
        String symbol = useEllipsisSymbol ? "\u2026" : "...";
        return shortenTextWithEllipsis(text, maxLength, suffixLength, symbol);
    }

    @NotNull
    @Contract(pure = true)
    public static String shortenPathWithEllipsis(@NotNull final String path, final int maxLength, boolean useEllipsisSymbol) {
        return shortenTextWithEllipsis(path, maxLength, (int) (maxLength * 0.7), useEllipsisSymbol);
    }

    @NotNull
    @Contract(pure = true)
    public static String shortenPathWithEllipsis(@NotNull final String path, final int maxLength) {
        return shortenPathWithEllipsis(path, maxLength, false);
    }


    @Contract(pure = true)
    public static boolean charsEqualIgnoreCase(char a, char b) {
        return StringUtilRt.charsEqualIgnoreCase(a, b);
    }

    @Contract(pure = true)
    public static char toUpperCase(char a) {
        return StringUtilRt.toUpperCase(a);
    }


    @NotNull
    @Contract(pure = true)
    public static String convertLineSeparators(@NotNull String text) {
        return StringUtilRt.convertLineSeparators(text);
    }

    @NotNull
    @Contract(pure = true)
    public static String convertLineSeparators(@NotNull String text, boolean keepCarriageReturn) {
        return StringUtilRt.convertLineSeparators(text, keepCarriageReturn);
    }

    @NotNull
    @Contract(pure = true)
    public static String convertLineSeparators(@NotNull String text, @NotNull String newSeparator) {
        return StringUtilRt.convertLineSeparators(text, newSeparator);
    }

    @NotNull
    public static String convertLineSeparators(@NotNull String text, @NotNull String newSeparator, @Nullable int[] offsetsToKeep) {
        return StringUtilRt.convertLineSeparators(text, newSeparator, offsetsToKeep);
    }

    @NotNull
    public static String convertLineSeparators(@NotNull String text,
                                               @NotNull String newSeparator,
                                               @Nullable int[] offsetsToKeep,
                                               boolean keepCarriageReturn) {
        return StringUtilRt.convertLineSeparators(text, newSeparator, offsetsToKeep, keepCarriageReturn);
    }




    @NotNull
    @Contract(pure = true)
    public static String getShortName(@NotNull Class aClass) {
        return StringUtilRt.getShortName(aClass);
    }

    @NotNull
    @Contract(pure = true)
    public static String getShortName(@NotNull String fqName) {
        return StringUtilRt.getShortName(fqName);
    }

    @NotNull
    @Contract(pure = true)
    public static String getShortName(@NotNull String fqName, char separator) {
        return StringUtilRt.getShortName(fqName, separator);
    }

    /**
     * Strips class name from Object#toString if present.
     * To be used as custom data type renderer for java.lang.Object.
     * To activate just add <code>StringUtil.toShortString(this)</code>
     * expression in <em>Settings | Debugger | Data Views</em>.
     */
    @Contract("null->null;!null->!null")
    @SuppressWarnings("UnusedDeclaration")
    static String toShortString(@Nullable Object o) {
        if (o == null) return null;
        if (o instanceof CharSequence) return o.toString();
        String className = o.getClass().getName();
        String s = o.toString();
        if (!s.startsWith(className)) return s;
        return s.length() > className.length() && !Character.isLetter(s.charAt(className.length())) ?
                trimStart(s, className) : s;
    }


    public static boolean trimEnd(@NotNull StringBuilder buffer, @NotNull CharSequence end) {
        if (endsWith(buffer, end)) {
            buffer.delete(buffer.length() - end.length(), buffer.length());
            return true;
        }
        return false;
    }




    /**
     * @deprecated use {@link #startsWithConcatenation(String, String...)} (to remove in IDEA 15)
     */
    @SuppressWarnings("unused")
    public static boolean startsWithConcatenationOf(@NotNull String string, @NotNull String firstPrefix, @NotNull String secondPrefix) {
        return startsWithConcatenation(string, firstPrefix, secondPrefix);
    }
}