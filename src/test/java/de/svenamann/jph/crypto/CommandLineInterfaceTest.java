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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import de.svenamann.jph.JPasswordHasher;

/**
 * Tests the {@link JPasswordHasher} command line interface. The interface is
 * expected to produce the same hashing results as the sole hasher. Arguments
 * are expected to be independent of their order and a help message should be
 * displayed if insufficient arguments are given.
 * 
 * @author Sven Amann
 */
public class CommandLineInterfaceTest {

    /**
     * Calls the {@link JPasswordHasher} with the given arguments and returns
     * the output as a string.
     * 
     * @param args
     *            the arguments to pass to the hasher
     * @return the printed output
     */
    private String hash(String... args) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        JPasswordHasher.hash(args, ps);
        String hash = baos.toString();
        return hash.substring(0, hash.length() - 1);
    }

    /**
     * Tests basic hashing without all options deactivated, varying the
     * requested output length. A length of 16 should be the default.
     */
    @Test
    public void testHashingWithoutModifiers() {
        assertEquals("tLFCSJSpqQLcgNrt",
                hash("sven-amann.de", "topsecret", "--length=16", "--requireDigit=false",
                        "--requireMixed=false", "--requirePunct=false",
                        "--onlyDigits=false", "--noSpecial=false"));
        assertEquals("tLFCSJSpqQLcgNrt",
                hash("sven-amann.de", "topsecret", "--requireDigit=false",
                        "--requireMixed=false", "--requirePunct=false",
                        "--onlyDigits=false", "--noSpecial=false"));
        assertEquals("tLFCSJSpqQ",
                hash("sven-amann.de", "topsecret", "--length=10", "--requireDigit=false",
                        "--requireMixed=false", "--requirePunct=false",
                        "--onlyDigits=false", "--noSpecial=false"));
    }

    /**
     * Tests that with the option "digits only" set, the resulting hash consists
     * of only numbers independent of the other arguments. This option is should
     * be deactivated by default.
     */
    @Test
    public void testHashingToDigitsOnly() {
        assertEquals("22639098972594023583506343",
                hash("sven-amann.de", "topsecret", "--length=26", "--onlyDigits=true"));
        // option is not set by default
        assertEquals(
                "tLFCSJSpqQLcgNrtkEHCwhnCX/",
                hash("sven-amann.de", "topsecret", "--length=26", "--requireDigit=false",
                        "--requirePunct=false", "--requireMixed=false",
                        "--onlyDigits=false", "--noSpecial=false"));
    }

    /**
     * Tests that with the "require digit" option set, the resulting is adapted
     * accordingly. Tests further that the option is activated by default.
     */
    @Test
    public void testHashingEnforcingDigits() {
        // should add digit if non in the original hash
        assertEquals(
                "tLFC9JSpqQLcgNrtkEHCwhnCX/",
                hash("sven-amann.de", "topsecret", "--length=26", "--requireDigit=true",
                        "--requirePunct=false", "--requireMixed=false",
                        "--onlyDigits=false", "--noSpecial=false"));
        // since the option is activated by default, stating it is not required
        assertEquals(
                "tLFC9JSpqQLcgNrtkEHCwhnCX/",
                hash("sven-amann.de", "topsecret", "--length=26",
                        "--requirePunct=false", "--requireMixed=false",
                        "--onlyDigits=false", "--noSpecial=false"));
    }

    /**
     * Tests hashing with the "restrict special char" flag set. This should
     * overrule the require punctuation flag. This should not be actived by
     * default.
     */
    @Test
    public void testHashingRestrictingSpecialChars() {
        // should remove special character
        assertEquals("tLFCSJSpqQLcgNrtkEHCwhnCXE",
                hash("sven-amann.de", "topsecret", "--length=26", "--requireDigit=false",
                        "--requirePunct=true", "--requireMixed=false",
                        "--onlyDigits=false", "--noSpecial=true"));
        // the option is deactivated by default
        assertEquals("tLFCSJSpqQLcgNrtkEHCwhnCX/",
                hash("sven-amann.de", "topsecret", "--length=26", "--requireDigit=false",
                        "--requirePunct=true", "--requireMixed=false",
                        "--onlyDigits=false"));
    }

    /**
     * Tests hashing with the require punctuation flag set. This option should
     * be activated by default.
     */
    @Test
    public void testHashingEnforcingPunctuation() {
        // should insert punctuation if non in the original
        assertEquals("JgPxvt7hpPw4D4CbbAzmYWeH*k",
                hash("sven-amann.de", "test", "--length=26", "--requireDigit=false",
                        "--requirePunct=true", "--requireMixed=false",
                        "--onlyDigits=false", "--noSpecial=false"));
        // should be activated by default, hence, can be omitted
        assertEquals("JgPxvt7hpPw4D4CbbAzmYWeH*k",
                hash("sven-amann.de", "test", "--length=26", "--requireDigit=false",
                        "--requireMixed=false",
                        "--onlyDigits=false", "--noSpecial=false"));
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
                hash("sven-amann.de", "topsecret", "--length=26", "--requireDigit=false",
                        "--requirePunct=false", "--requireMixed=true",
                        "--onlyDigits=false", "--noSpecial=false"));
    }

    /**
     * Tests hashing requiring digits, punctuation and mixed case. This
     * configuration should be the default.
     */
    @Test
    public void testHashingEnforcingDigitsPunctuationAndMixedCase() {
        // should place number
        assertEquals("tLFC9JSpqQLcgNrtkEHCwhnCX/",
                hash("sven-amann.de", "topsecret", "--length=26", "--requireDigit=true",
                        "--requirePunct=true", "--requireMixed=true",
                        "--onlyDigits=false", "--noSpecial=false"));
        // should be default
        assertEquals("tLFC9JSpqQLcgNrtkEHCwhnCX/",
                hash("sven-amann.de", "topsecret", "--length=26"));
    }

    /**
     * Tests that calling with insufficient arguments terminates gracefully. The
     * expected help message is not tested here.
     */
    @Test
    public void testHelpMessage() {
        JPasswordHasher.main(new String[0]);
    }
}
