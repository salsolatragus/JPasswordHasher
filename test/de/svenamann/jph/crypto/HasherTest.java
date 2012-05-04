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
package de.svenamann.jph.crypto;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.svenamann.jph.Hasher;

/**
 * Tests the {@link Hasher} implementation for compliance with the original
 * PasswordHasher by Steve Cooper with regard to the hash generation. Reference
 * output has been taken from the PasswordHasher Firefox Plugin.
 * 
 * @author Sven Amann
 */
public class HasherTest {

    /**
     * The unit under test.
     */
    private Hasher hasher;

    /**
     * Creates a hasher.
     */
    @Before
    public void setUp() {
        hasher = new Hasher();
    }

    /**
     * Tests basic hashing without options, varying the requested output length.
     */
    @Test
    public void testHashingWithoutModifiers() {
        assertEquals("tLFCSJSpqQLcgNrtkEHCwhnCX/",
                hasher.generateHashWord("topsecret", "sven-amann.de", 26, false, false, false,
                        false, false));
        assertEquals("tLFCSJSpqQLcgNrt",
                hasher.generateHashWord("topsecret", "sven-amann.de", 16, false, false, false,
                        false, false));
        assertEquals("tLFCSJSpqQ",
                hasher.generateHashWord("topsecret", "sven-amann.de", 10, false, false, false,
                        false, false));
        assertEquals("tLFC",
                hasher.generateHashWord("topsecret", "sven-amann.de", 4, false, false, false,
                        false, false));
    }

    /**
     * Tests hashing with the option "digits only" set. The result should
     * contain only digits and all other requirement flags should be ignored.
     */
    @Test
    public void testHashingToDigitsOnly() {
        // should replace all chars by digits
        assertEquals("22639098972594023583506343",
                hasher.generateHashWord("topsecret", "sven-amann.de", 26, true, true, true, true,
                        true));
        // should keep numbers of the original hash
        // should mimic the offset bug of the original implementation
        assertEquals("54119776310434399630082398",
                hasher.generateHashWord("test", "sven-amann.de", 26, true, true, true, true, true));
        assertEquals(
                "15920821951377634946848393",
                hasher.generateHashWord("foobar", "sven-amann.de", 26, true, true, true, true, true));
    }

    /**
     * Tests hashing with the require digits flag set.
     */
    @Test
    public void testHashingEnforcingDigits() {
        // should add digit if non in the original hash
        assertEquals("tLFC9JSpqQLcgNrtkEHCwhnCX/",
                hasher.generateHashWord("topsecret", "sven-amann.de", 26, true, false, false,
                        false, false));
        // should not add digit if already contained
        assertEquals("JgPxvt7hpPw4D4CbbAzmYWeHXk",
                hasher.generateHashWord("test", "sven-amann.de", 26, true, false, false, false,
                        false));
    }

    /**
     * Tests hashing with the restrict special char flag set. This should
     * overrule the require punctuation flag.
     */
    @Test
    public void testHashingRestrictingSpecialChars() {
        // should remove special character
        // should mimic the index-of-char bug of the original implementation
        // should mimic the offset bug of the original implementation
        assertEquals("tLFCSJSpqQLcgNrtkEHCwhnCXE",
                hasher.generateHashWord("topsecret", "sven-amann.de", 26, false, true, false, true,
                        false));
        assertEquals("1TGVT8kUIYU3JoZkbSbPRXfk9J",
                hasher.generateHashWord("foobar", "sven-amann.de", 26, false, true, false, true,
                        false));
        // should not change hash without special characters
        assertEquals("JgPxvt7hpPw4D4CbbAzmYWeHXk",
                hasher.generateHashWord("test", "sven-amann.de", 26, false, true, false, true,
                        false));
    }

    /**
     * Tests hashing with the require punctuation flag set.
     */
    @Test
    public void testHashingEnforcingPunctuation() {
        // should insert punctuation if non in the original
        assertEquals("JgPxvt7hpPw4D4CbbAzmYWeH*k",
                hasher.generateHashWord("test", "sven-amann.de", 26, false, true, false, false,
                        false));
        // should not change hash with punctuation
        assertEquals("1T+VT8kUIYU3JoZkbSbPRXfk9J",
                hasher.generateHashWord("foobar", "sven-amann.de", 26, false, true, false, false,
                        false));
    }

    /**
     * Tests hashing with the mixed case flag set. Since I could not find an
     * example were this option actually would change the output, only the happy
     * path is tested here...
     */
    @Test
    public void testHashingEnforcingMixedCase() {
        // should not change hash with mixed case
        assertEquals("tLFCSJSpqQLcgNrtkEHCwhnCX/",
                hasher.generateHashWord("topsecret", "sven-amann.de", 26, false, false, true,
                        false, false));
    }

    /**
     * Tests hashing requiring digits, punctuation and mixed case.
     */
    @Test
    public void testHashingEnforcingDigitsPunctuationAndMixedCase() {
        // should place number
        assertEquals("tLFC9JSpqQLcgNrtkEHCwhnCX/",
                hasher.generateHashWord("topsecret", "sven-amann.de", 26, true, true, true, false,
                        false));
        // should place special char
        assertEquals("JgPxvt7hpPw4D4CbbAzmYWeH*k",
                hasher.generateHashWord("test", "sven-amann.de", 26, true, true, true, false,
                        false));
        // should change nothing
        assertEquals("1T+VT8kUIYU3JoZkbSbPRXfk9J",
                hasher.generateHashWord("foobar", "sven-amann.de", 26, true, true, true, false,
                        false));
        // should place number and special char
        assertEquals("tLFCSJSpqQLc9/rt",
                hasher.generateHashWord("topsecret", "sven-amann.de", 16, true, true, true, false,
                        false));
    }
}
