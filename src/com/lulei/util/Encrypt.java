/** 
**com.lulei.util.Encrypt 
**/  
 /**   
 *@Description: JAVA实现常见加密算法 
 */   
package com.lulei.util;    

import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
    
public class Encrypt {  
    public static final String MD5 = "MD5";  
    public static final String SHA1 = "SHA-1";  
    public static final String SHA256 = "SHA-256";  

    /** 
     * @param str 需要加密的字符串 
     * @param encName 加密种类  MD5 SHA-1 SHA-256 
     * @return 
     * @Author:lulei   
     * @Description: 实现对字符串的加密 
     */  
    public static String encrypt(String str, String encName){  
        String reStr = null;  
        try {  
            MessageDigest digest = MessageDigest.getInstance(encName);  
            byte[] bytes = digest.digest(str.getBytes());  
            StringBuffer stringBuffer = new StringBuffer();  
            for (byte b : bytes){  
                int bt = b&0xff;  
                if (bt < 16){  
                    stringBuffer.append(0);  
                }   
                stringBuffer.append(Integer.toHexString(bt));  
            }  
            reStr = stringBuffer.toString();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return reStr;  
    }  
      
    public static void main(String[] args) {  
        System.out.println(Encrypt.encrypt("nihao", null));  
    }  
      
}  

