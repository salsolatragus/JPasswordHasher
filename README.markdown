# JPasswordHasher

A Java implementation of [Steve Cooper's PasswordHasher Firefox plugin](https://addons.mozilla.org/de/firefox/addon/password-hasher/ "PasswordHasher Firefox plugin").
This project's goal is to make the PasswordHasher available outside of Firefox. It is motivated by the increasing number of services that can be used by both browsers
and native applications, both of which then base on the same account and password. Instead of having to open Firefox, and invoking the PasswordHasher plugin it should
be possible to access PasswordHasher directly.

As a starting point a standalone Java application saves the need to start Firefox. Further ideas include developing plugins for Quicksilver, Quicklaunch, ... to access
the PasswordHasher with even less effort.

Feel free to comment and [share ideas](https://github.com/salsolatragus/JPasswordHasher/issues "JPasswordHasher issue list") of how to improove the ease of usage even further!

## Command line interface

The application has a simple command line interface:

    java de.svenamann.jph.JPasswordHasher [options] tag master
    
    arguments:
      tag    - the (site-)tag to use for hashing
      master - the master password to use for hashing
    options:
      --length=(0..27)              - required length of the hash, defaults to 16
      --requireDigit=(TRUE|false)   - require hash to contain at least one digit
      --requirePunct=(TRUE|false)   - require hash to contain at least one punctuation
      --requireMixed=(TRUE|false)   - require hash to be mixed case
      --noSpecial=(true|FALSE)      - require hash not to contain special characters
      --onlyDigits=(true|FALSE)     - require hash to contain only digits

## Licence block

Version: MPL 1.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version
1.1 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is JPasswordHasher, released April 27, 2012.

The Initial Developer of the Original Code is Sven Amann.  
Portions created by the Initial Developer are Copyright (C) 2012  
the Initial Developer. All Rights Reserved.

Contributor(s): Steve Cooper

Alternatively, the contents of this file may be used under the terms of the
GNU General Public License Version 2 or later (the "GPL"), in which case the
provisions of the GPL are applicable instead of those above. If you wish to
allow use of your version of this file only under the terms of the GPL and
not to allow others to use your version of this file under the MPL, indicate
your decision by deleting the provisions above and replacing them with the
notice and other provisions required by the GPL. If you do not delete the
provisions above, a recipient may use your version of this file under either
the MPL or the GPL.
