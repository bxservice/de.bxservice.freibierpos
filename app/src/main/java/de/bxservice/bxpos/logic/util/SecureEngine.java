/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.logic.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * Created by Diego Ruiz on 4/04/16.
 */
public class SecureEngine {

    private static final String TAG = "SecureEngine";
    private static final String ALGORITHM = "SHA1PRNG";
    private static final int ITERATIONS = 1000;

    //Values to be used in the encryption
    private String salt;

    /**
     * Returns the encrypted text and the used salt
     * @param text
     * @return
     */
    public String protectText(String text) {

        String hash = null;

        // Uses a secure Random not a simple Random
        SecureRandom random;
        try {
            random = SecureRandom.getInstance(ALGORITHM);
            // Salt generation 64 bits long
            byte[] bSalt = new byte[8];

            random.nextBytes(bSalt);
            // Digest computation
            hash = getSHA512Hash(ITERATIONS, text, bSalt);

            salt = convertToHexString(bSalt);

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "AES protectText: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "AES protectText: " + e.getMessage());
        }

        return hash;
    }

    public static String getSHA512Hash (int iterations, String value, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(value.getBytes("UTF-8"));
        for (int i = 0; i < iterations; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        digest.reset();
        //
        return convertToHexString(input);
    }	//	getSHA512Hash

    public static String convertToHexString (byte[] bytes)
    {
        //	see also Util.toHex
        int size = bytes.length;
        StringBuilder buffer = new StringBuilder(size*2);
        for (byte aByte : bytes) {
            // convert byte to an int
            int x = aByte;
            // account for int being a signed type and byte being unsigned
            if (x < 0)
                x += 256;
            String tmp = Integer.toHexString(x);
            // pad out "1" to "01" etc.
            if (tmp.length() == 1)
                buffer.append("0");
            buffer.append(tmp);
        }
        return buffer.toString();
    }   //  convertToHexString

    /**
     *  Convert Hex String to Byte Array
     *  @param hexString hex string
     *  @return byte array
     */
    public static byte[] convertHexString (String hexString)
    {
        if (hexString == null || hexString.length() == 0)
            return null;
        int size = hexString.length()/2;
        byte[] retValue = new byte[size];
        String inString = hexString.toLowerCase();

        try
        {
            for (int i = 0; i < size; i++)
            {
                int index = i*2;
                int ii = Integer.parseInt(inString.substring(index, index+2), 16);
                retValue[i] = (byte)ii;
            }
            return retValue;
        }
        catch (Exception e)
        {
            Log.e(TAG, " convertHexString: " + e.getMessage());
        }
        return null;
    }   //  convertToHexString

    /**
    * use salt in hex form and text hashed compare with plan text
    * when has exception in hash, log to server
    * @param hashedText
    * @param hexSalt
    * @param planText
    * @return
            */
    public static boolean isMatchHash (String hashedText, String hexSalt, String planText){
        boolean valid=false;

        // always do calculation to prevent timing based attacks
        if ( hashedText == null )
            hashedText = "0000000000000000";
        if ( hexSalt == null )
            hexSalt = "0000000000000000";

        try {
            valid= getSHA512Hash(ITERATIONS, planText, convertHexString(hexSalt)).equals(hashedText);
        } catch (NoSuchAlgorithmException ignored) {
            Log.e(TAG, "Password hashing not supported by JVM");
        } catch (UnsupportedEncodingException ignored) {
            Log.e(TAG, "Password hashing not supported by JVM");
        }

        return valid;
    }

    public String getSalt() {
        return salt;
    }

}
