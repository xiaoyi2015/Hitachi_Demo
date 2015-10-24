package ac.airconditionsuit.app.util;

import ac.airconditionsuit.app.Constant;

import java.util.Arrays;

/**
 * Created by ac on 9/19/15.
 * base64 util
 */
public class MyBase64Util {

    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();
    private static final int[] IA = new int[256];
    private static String TAG = "MyBase64Util";

    static {
        Arrays.fill(IA, -1);
        for (int i = 0, iS = CA.length; i < iS; i++) {
            IA[CA[i]] = i;
        }
        IA['='] = 0;
    }

    /**
     * Decodes a BASE64 encoded string that is known to be resonably well
     * formatted.
     * The preconditions are:<br>
     * + The array must have a line length of 76 chars OR no line separators at
     * all (one line).<br>
     * + Line separator must be "\r\n", as specified in RFC 2045 + The array
     * must not contain illegal characters within the encoded string<br>
     * + The array CAN have illegal characters at the beginning and end, those
     * will be dealt with appropriately.<br>
     *
     * @param bytes The source bytes. Length 0 will return an empty array.
     *          <code>null</code> will throw an exception.
     * @return The decoded array of bytes. May be of length 0.
     */
    public static byte[] decodeToByte(byte[] bytes) {
        String s = new String(bytes);

        // Check special case
        int sLen = s.length();
        if (sLen == 0)
            return new byte[0];

        int sIx = 0, eIx = sLen - 1; // Start and end index after trimming.

        // Trim illegal chars from start
        while (sIx < eIx && IA[s.charAt(sIx) & 0xff] < 0)
            sIx++;

        // Trim illegal chars from end
        while (eIx > 0 && IA[s.charAt(eIx) & 0xff] < 0)
            eIx--;

        // get the padding count (=) (0, 1 or 2)
        int pad = s.charAt(eIx) == '=' ? (s.charAt(eIx - 1) == '=' ? 2 : 1) : 0; // Count
        // '='
        // at
        // end.
        int cCnt = eIx - sIx + 1; // Content count including possible separators
        int sepCnt = sLen > 76 ? (s.charAt(76) == '\r' ? cCnt / 78 : 0) << 1
                : 0;

        int len = ((cCnt - sepCnt) * 6 >> 3) - pad; // The number of decoded
        // bytes
        byte[] dArr = new byte[len]; // Preallocate byte[] of exact length

        // Decode all but the last 0 - 2 bytes.
        int d = 0;
        for (int cc = 0, eLen = (len / 3) * 3; d < eLen; ) {
            // Assemble three bytes into an int from four "valid" characters.
            int i = IA[s.charAt(sIx++)] << 18 | IA[s.charAt(sIx++)] << 12
                    | IA[s.charAt(sIx++)] << 6 | IA[s.charAt(sIx++)];

            // Add the bytes
            dArr[d++] = (byte) (i >> 16);
            dArr[d++] = (byte) (i >> 8);
            dArr[d++] = (byte) i;

            // If line separator, jump over it.
            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }

        if (d < len) {
            // Decode last 1-3 bytes (incl '=') into 1-3 bytes
            int i = 0;
            for (int j = 0; sIx <= eIx - pad; j++)
                i |= IA[s.charAt(sIx++)] << (18 - j * 6);

            for (int r = 16; d < len; r -= 8)
                dArr[d++] = (byte) (i >> r);
        }

        for (int i = 0; i < dArr.length; i++) {
            dArr[i] = (byte) (~(dArr[i] - Constant.FILE_DECODE_ENCODE_KEY));
        }
        return dArr;
    }



    /**
     * Encodes a raw byte array into a BASE64 <code>byte[]</code> representation
     * i accordance with RFC 2045.
     *
     * @param sArr
     *            The bytes to convert. If <code>null</code> or length 0 an
     *            empty array will be returned.
     * @param lineSep
     *            Optional "\r\n" after 76 characters, unless end of file.<br>
     *            No line separator will be in breach of RFC 2045 which
     *            specifies max 76 per line but will be a little faster.
     * @return A BASE64 encoded array. Never <code>null</code>.
     */
    public final static byte[] encodeToByte(byte[] sArr, boolean lineSep) {
        // Check special case
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0)
            return new byte[0];

        for (int i = 0; i < sArr.length; i++) {
            sArr[i] = (byte)(~sArr[i]+Constant.FILE_DECODE_ENCODE_KEY);
        }

        int eLen = (sLen / 3) * 3; // Length of even 24-bits.
        int cCnt = ((sLen - 1) / 3 + 1) << 2; // Returned character count
        int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0); // Length of
        // returned
        // array
        byte[] dArr = new byte[dLen];

        // Encode even 24-bits
        for (int s = 0, d = 0, cc = 0; s < eLen;) {
            // Copy next three bytes into lower 24 bits of int, paying attension
            // to sign.
            int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8
                    | (sArr[s++] & 0xff);

            // Encode the int into four chars
            dArr[d++] = (byte) CA[(i >>> 18) & 0x3f];
            dArr[d++] = (byte) CA[(i >>> 12) & 0x3f];
            dArr[d++] = (byte) CA[(i >>> 6) & 0x3f];
            dArr[d++] = (byte) CA[i & 0x3f];

            // Add optional line separator
            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        }

        // Pad and encode last bits if source isn't an even 24 bits.
        int left = sLen - eLen; // 0 - 2.
        if (left > 0) {
            // Prepare the int
            int i = ((sArr[eLen] & 0xff) << 10)
                    | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

            // Set last four chars
            dArr[dLen - 4] = (byte) CA[i >> 12];
            dArr[dLen - 3] = (byte) CA[(i >>> 6) & 0x3f];
            dArr[dLen - 2] = left == 2 ? (byte) CA[i & 0x3f] : (byte) '=';
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }
}
