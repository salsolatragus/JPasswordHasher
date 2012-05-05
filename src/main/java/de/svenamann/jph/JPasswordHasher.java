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

/**
 * JPasswordHasher is lightweight console application that wraps a Java
 * implementation of Steve Coopers PasswordHasher Firefox Plugin.
 * 
 * @author Sven Amann
 */
public class JPasswordHasher {

    /**
     * Gets the value from a call argument, i.e., the substring behind '='.
     * 
     * @param arg
     *            the complete argument
     * @return the value
     */
    private static String value(String arg) {
        return arg.substring(arg.indexOf("=") + 1);
    }

    /**
     * Gets the value from a call flag.
     * 
     * @param flag
     *            the complete flag
     * @return the value
     */
    private static boolean flagValue(String flag) {
        return Boolean.parseBoolean(value(flag));
    }

    /**
     * Gets the number value from a call argument.
     * 
     * @param arg
     *            the complete argument
     * @return the values
     */
    private static int intValue(String arg) {
        return Integer.parseInt(value(arg));
    }

    /**
     * Starts the password hasher.
     * 
     * @param args
     *            call arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("JPasswordHasher");
            System.out.println();
            System.out.println("Usage: jph [options] tag master");
            System.out.println(" tag    - the tag to use for hashing");
            System.out.println(" master - the master password to use for hashing");
            System.out.println(" options:");
            System.out
                    .println("  --length=(0..27)              - required length of the hash, defaults to 16");
            System.out
                    .println("  --requireDigit=(TRUE|false)   - require hash to contain at least one digit");
            System.out
                    .println("  --requirePunct=(TRUE|false)   - require hash to contain at least one punctuation");
            System.out.println("  --requireMixed=(TRUE|false)   - require hash to be mixed case");
            System.out
                    .println("  --noSpecial=(true|FALSE)      - require hash not to contain special characters");
            System.out
                    .println("  --onlyDigits=(true|FALSE)     - require hash to contain only digits");
        } else {
            String tag = null;
            String master = null;
            int length = 16;
            boolean requireDigit = true;
            boolean requirePunctuation = true;
            boolean requireMixed = true;
            boolean restrictSpecial = false;
            boolean restrictDigits = false;
            for (String arg : args) {
                if (arg.startsWith("--requireDigit")) {
                    requireDigit = flagValue(arg);
                } else if (arg.startsWith("--requirePunct")) {
                    requirePunctuation = flagValue(arg);
                } else if (arg.startsWith("--requireMixed")) {
                    requireMixed = flagValue(arg);
                } else if (arg.startsWith("--noSpecial")) {
                    restrictSpecial = flagValue(arg);
                } else if (arg.startsWith("--onlyDigits")) {
                    restrictDigits = flagValue(arg);
                } else if (arg.startsWith("--length")) {
                    length = intValue(arg);
                } else if (tag == null) {
                    tag = arg;
                } else {
                    master = arg;
                }
            }
            Hasher jph = new Hasher();
            System.out.println(jph.generateHashWord(tag, master, length, requireDigit,
                    requirePunctuation, requireMixed, restrictSpecial, restrictDigits));
        }
    }
}
