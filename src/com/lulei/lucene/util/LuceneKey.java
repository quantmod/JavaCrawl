/** 
**com.lulei.lucene.util.LuceneKey 
**/  
 /**   
 *@Description:  字符串中lucene特殊字符处理 
 */   
package com.lulei.lucene.util;    
    
public class LuceneKey {  
      
    private static final String luceneKey = "+-!&|(){}[]^\"~*?:\\/";  
      
    /** 
     * @param str 
     * @param removelSpace 是否移除空格 
     * @return 
     * @Author: lulei   
     * @Description: 处理输入字符串中的lucene特殊字符 
     */  
    public static String escapeLuceneKey(String str, boolean removelSpace){  
        if (str == null) {  
            return null;  
        }  
        StringBuffer stringBuffer = new StringBuffer();  
        for (int i = 0; i < str.length(); i++) {  
            char c = str.charAt(i);  
            if (removelSpace && c == ' '){  
                continue;  
            }  
            stringBuffer.append(escapeLuceneKey(c));  
        }  
        return stringBuffer.toString();  
    }  
      
    /** 
     * @param str 
     * @return 
     * @Author: lulei   
     * @Description: 处理输入字符串中的lucene特殊字符 不移除空格 
     */  
    public static String escapeLuceneKey(String str) {  
        return escapeLuceneKey(str, false);  
    }  
      
    /** 
     * @param c 
     * @return 
     * @Author: lulei   
     * @Description: 转义字符 
     */  
    private static String escapeLuceneKey(char c){  
        if (luceneKey.indexOf(c) < 0) {  
            return c + "";  
        }  
        return "\\" + c;  
    }  

    public static void main(String[] args) {  
        // TODO Auto-generated method stub    
        System.out.println(LuceneKey.escapeLuceneKey("你   好[参加+"));  
    }  

}  

