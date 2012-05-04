/*
 *  ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1/GPL 2.0
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is JPasswordHasher, released April 27, 2012.
 * 
 * The Initial Developer of the Original Code is Sven Amann.
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s): Steve Cooper
 * 
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License Version 2 or later (the "GPL"), in which case the
 * provisions of the GPL are applicable instead of those above. If you wish to
 * allow use of your version of this file only under the terms of the GPL and
 * not to allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replacing them with the
 * notice and other provisions required by the GPL. If you do not delete the
 * provisions above, a recipient may use your version of this file under either
 * the MPL or the GPL.
 * 
 * ***** END LICENSE BLOCK *****
 */
package de.svenamann.jph;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

/**
 * A Java implementation of the PassHashCommon class from the original
 * JavaScript implementation of PasswordHasher by Steve Cooper.<br>
 * <br>
 * The API for generating hash words has been taken over. Naturally, all browser
 * based functionality has been omitted.
 * 
 * @author Sven Amann
 */
public class Hasher {

    /**
     * The algorithm used for encoding.
     */
    private static final String ALGORITHM = "HmacSHA1";

    /**
     * The encoder used to encode the encrypted value.
     */
    private static final BASE64Encoder ENCODER = new BASE64Encoder();

    /**
     * The MAC used for encryption.
     */
    private final Mac mac;

    /**
     * Creates a MAC instance for {@link #ALGORITHM}. A
     * {@link NoSuchAlgorithmException} will be suppressed since the constant
     * value of {@link #ALGORITHM} is expected to refer to a valid algorithm.
     * 
     * @return the created instance
     */
    private static Mac createMac() {
        try {
            return Mac.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // cannot happen since algorithm is hardcoded and exists
            return null;
        }
    }

    /**
     * Creates a hasher instance.
     */
    public Hasher() {
        mac = createMac();
    }

    /**
     * Hashes a data string with a given key and the passed parameters. The
     * implementation complies with the original PasswordHasher.
     * 
     * @param key
     *            the key to hash with
     * @param data
     *            the data the hash with
     * @param hashWordSize
     *            the length of the has to generate, must be positive and
     *            smaller than 27
     * @param requireDigit
     *            set to ensure that at least one digit appears in the result
     *            hash
     * @param requirePunctuation
     *            set to ensure that at least one punctuation character appears
     *            in the result hash
     * @param requireMixed
     *            set to ensure that the result hash is mixed case
     * @param restrictSpecial
     *            set to ensure that no special characters are in the result
     *            hash. Overrules requirePunctuation
     * @param restrictDigits
     *            set to ensure that only digits are in the result hash.
     *            Overrules all other flags
     * @return the generated hash
     */
    public String generateHashWord(String key, String data, int hashWordSize, boolean requireDigit,
            boolean requirePunctuation, boolean requireMixed, boolean restrictSpecial,
            boolean restrictDigits) {
        if (hashWordSize < 1 || hashWordSize > 27) {
            throw new IllegalArgumentException("Illegal hash length requested: length = "
                    + hashWordSize + ", required 0 < length <=27");
        }

        String base = encrypt(key, data);
        // PasswordHasher drops base64 padding '=' (and trailing newline) by
        // crypto configuration
        base = base.substring(0, base.length() - 2);

        // Use the checksum of all characters as a pseudo-randomizing seed to
        // avoid making the injected characters easy to guess. Note that it
        // isn't random in the sense of not being deterministic (i.e.
        // repeatable). Must share the same seed between all injected
        // characters so that they are guaranteed unique positions based on
        // their offsets.
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            sum += base.charAt(i);
        }

        if (restrictDigits) {
            base = convertToDigits(base, sum, hashWordSize);
        } else {
            if (requireDigit) {
                base = injectCharacter(base, sum, hashWordSize, 4, 0, 48, 10);
            }
            if (restrictSpecial) {
                base = removeSpecial(base, sum, hashWordSize);
            } else if (requirePunctuation) {
                base = injectCharacter(base, sum, hashWordSize, 4, 1, 33, 15);
            }
            if (requireMixed) {
                base = injectCharacter(base, sum, hashWordSize, 4, 2, 65, 26);
                base = injectCharacter(base, sum, hashWordSize, 4, 3, 97, 26);
            }
        }
        // Trim it to size.
        return base.substring(0, hashWordSize);
    }

    /**
     * Encrypts the given data using the given key. The {@link #ALGORITHM} is
     * used and the result encoded using {@link #ENCODER}.
     * 
     * @param key
     *            the crypto key
     * @param data
     *            the value to encrypt
     * @return the encrypted, encoded value
     */
    private String encrypt(String key, String data) {
        try {
            mac.init(new SecretKeySpec(key.getBytes(), ALGORITHM));
            byte[] publicBytes = mac.doFinal(data.getBytes());
            return ENCODER.encodeBuffer(publicBytes);
        } catch (InvalidKeyException ike) {
            // impossible since valid key is created here
            return null;
        }
    }

    /**
     * Converts all non-number characters on the first hashWordSize positions of
     * data to number characters (char code modulo 10).<br>
     * <br>
     * Note: This implementation takes over the index offset bug from
     * PasswordHasher in order to generate corresponding results.
     * 
     * @param data
     *            the string to reduce to number characters
     * @param seed
     * @param length
     * @return
     */
    private String convertToDigits(final String data, final int seed, final int length) {
        String result = "";
        // whenever a sequence of chars is kept (because they are already
        // numbers) the first non-number char afterwards is replaced using the
        // first char of the sequence which is then treated as a non-number
        // char. This is an offset bug in PasswordHasher which we keep here to
        // achieve same output.
        boolean charKept = false;
        int firstKeptIndex = 0;
        for (int i = 0; i < data.length() && i < length; i++) {
            int cur = data.charAt(i);
            // keep numbers already present
            if (48 <= cur && cur <= 57) {
                if (!charKept) {
                    charKept = true;
                    firstKeptIndex = i;
                }
                result += (char) cur;
            } else {
                if (charKept) {
                    charKept = false;
                    cur = data.charAt(firstKeptIndex);
                }
                result += (cur + seed) % 10;
            }
        }
        return result;
    }

    /**
     * Injects a character from a certain char range into the data string if
     * there is no char from the respective range already present outside of a
     * reserved block. The reserved block will be "randomly" placed using the
     * seed and have the given width. The injection will happen with a given
     * offset from the start of the reserved block.
     * 
     * @param data
     *            the data string to inject into
     * @param seed
     *            random information
     * @param length
     *            the number of chars from data to consider
     * @param reservedWith
     *            the width of the reserved block
     * @param offset
     *            offset of the injection from the start of the reserved block
     * @param charStart
     *            the first char code of the range to inject from
     * @param charWidth
     *            the width of the range to inject from
     * @return the injection result
     */
    private String injectCharacter(final String data, final int seed,
            final int length, final int reservedWith, final int offset, final int charStart,
            final int charWidth) {
        // determine "random" position of the reserved block
        int reservedStart = seed % length;
        int reservedEnd = reservedStart + reservedWith;
        // check for special character outside the reserved block
        // return unmodified input if one is found
        for (int i = 0; i < length - reservedWith; i++) {
            int c = data.charAt((reservedEnd + i) % length);
            if (inRange(c, charStart, charStart + charWidth)) {
                return data;
            }
        }
        // replace the character at pos by a "random" character
        int pos = (reservedStart + offset) % length;
        StringBuffer buf = new StringBuffer(data.length());
        buf.append((pos > 0 ? data.substring(0, pos) : ""));
        buf.append((char) (((seed + data.charAt(pos)) % charWidth) + charStart));
        buf.append((pos + 1 < data.length() ? data.substring(pos + 1) : ""));
        return buf.toString();
    }

    /**
     * Removes special characters, i.e., non alpha-numerical characters, from
     * the given data string.<br>
     * <br>
     * Note: This implementation takes over the index offset bug and the index
     * for char bug from PasswordHasher in order to generate corresponding
     * results.
     * 
     * @param data
     *            the data string to free from special chars
     * @param seed
     *            random seed, used to determine replacements
     * @param length
     *            the number of characters from data to consider
     * @return the generated string
     */
    private String removeSpecial(final String data, final int seed, final int length) {
        String result = "";
        // whenever a sequence of chars is kept (because they are already
        // non-special) the first special char afterwards is replaced using the
        // index of the first non-special of the sequence. This is an offset and
        // a index-for-char bug in PasswordHasher which we keep here to achieve
        // same output.
        boolean charKept = false;
        int firstKeptIndex = 0;
        for (int i = 0; i < length; i++) {
            int cur = data.charAt(i);
            if (inRange(cur, 48, 57) || inRange(cur, 65, 90) || inRange(cur, 97, 122)) {
                if (!charKept) {
                    charKept = true;
                    firstKeptIndex = i;
                }
                result += Character.toString((char) cur);
            } else {
                // when replace the index is used to determine the replacement
                // this is a bug in the original implementation
                if (charKept) {
                    charKept = false;
                    cur = firstKeptIndex;
                } else {
                    cur = i;
                }
                result += Character.toString((char) (((cur + seed) % 26) + 65));
            }
        }
        return result;
    }

    /**
     * Checks l <= c <= h.
     * 
     * @param c
     *            value
     * @param l
     *            lower bound
     * @param h
     *            upper bound
     * @return <code>true</code> if contained, <code>false</code> otherwise
     */
    private boolean inRange(int c, int l, int h) {
        return (l <= c && c <= h);
    }
}
