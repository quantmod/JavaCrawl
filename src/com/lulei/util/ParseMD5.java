/** 
**com.lulei.util.ParseMD5 
**/  
 /**   
 *@Description: 将字符串转化为MD5 
 */   
package com.lulei.util;    

public class ParseMD5 extends Encrypt{  

    /** 
     * @param str 
     * @return 
     * @Author: lulei   
     * @Description:  32位小写MD5 
     */  
    public static String parseStrToMd5L32(String str){  
        return encrypt(str, MD5);  
    }  
      
    /** 
     * @param str 
     * @return 
     * @Author: lulei   
     * @Description: 32位大写MD5 
     */  
    public static String parseStrToMd5U32(String str){  
        String reStr = parseStrToMd5L32(str);  
        if (reStr != null){  
            reStr = reStr.toUpperCase();  
        }  
        return reStr;  
    }  
      
    /** 
     * @param str 
     * @return 
     * @Author: lulei   
     * @Description: 16位小写MD5 
     */  
    public static String parseStrToMd5U16(String str){  
        String reStr = parseStrToMd5L32(str);  
        if (reStr != null){  
            reStr = reStr.toUpperCase().substring(8, 24);  
        }  
        return reStr;  
    }  
      
    /** 
     * @param str 
     * @return 
     * @Author: lulei   
     * @Description: 16位大写MD5 
     */  
    public static String parseStrToMd5L16(String str){  
        String reStr = parseStrToMd5L32(str);  
        if (reStr != null){  
            reStr = reStr.substring(8, 24);  
        }  
        return reStr;  
    }  
      
    /** 
     * @param md5L16 
     * @return 
     * @Author:lulei   
     * @Description: 将16位的md5转化为long值 
     */  
    public static long parseMd5L16ToLong(String md5L16){  
        if (md5L16 == null) {  
            throw new NumberFormatException("null");  
        }  
        md5L16 = md5L16.toLowerCase();  
        byte[] bA = md5L16.getBytes();  
        long re = 0L;  
        for (int i = 0; i < bA.length; i++) {  
            //加下一位的字符时，先将前面字符计算的结果左移4位  
            re <<= 4;  
            //0-9数组  
            byte b = (byte) (bA[i] - 48);  
            //A-F字母  
            if (b > 9) {  
                b = (byte) (b - 39);  
            }  
            //非16进制的字符  
            if (b > 15 || b < 0) {  
                throw new NumberFormatException("For input string '" + md5L16);  
            }  
            re += b;  
        }  
        return re;  
    }  
      
    /** 
     * @param str16 
     * @return 
     * @Author:lulei   
     * @Description: 将16进制的字符串转化为long值 
     */  
    public static long parseString16ToLong(String str16){  
        if (str16 == null) {  
            throw new NumberFormatException("null");  
        }  
        //先转化为小写  
        str16 = str16.toLowerCase();  
        //如果字符串以0x开头，去掉0x  
        str16 = str16.startsWith("0x") ? str16.substring(2) : str16;  
        if (str16.length() > 16) {  
            throw new NumberFormatException("For input string '" + str16 + "' is to long");  
        }  
        return parseMd5L16ToLong(str16);  
    }  
      
    /** 
     * @param str 
     * @return 
     * @Author:lulei   
     * @Description: 将字符串进行md5 16位加密，并将其转化为long值 
     */  
    public static long parseStringToMd516long(String str) {  
        return parseString16ToLong(parseStrToMd5L16(str));  
    }  
      
    public static void main(String[] args) {  
        System.out.println(ParseMD5.parseStrToMd5L32("nihao"));  
    }  
}  