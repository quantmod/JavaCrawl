/**   
  *@Description: 获取页面链接地址信息基类   
 */   
package com.lulei.crawl;    
  
import java.io.IOException;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  

import com.lulei.util.DoRegex;
  

  
  
public abstract class CrawlListPageBase extends CrawlBase {  
    private String pageurl;  
      
    /** 
    * @param urlStr 
    * @param charsetName 
    * @throws IOException 
     */  
    public CrawlListPageBase(String urlStr, String charsetName) throws IOException{  
        readPageByGet(urlStr, charsetName);  
        pageurl = urlStr;  
    }  
      
    /** 
    * @param urlStr 
    * @param charsetName 
    * @param method 
    * @param params 
    * @throws IOException 
     */  
    public CrawlListPageBase(String urlStr, String charsetName, String method, HashMap<String, String> params) throws IOException{  
        readPage(urlStr, charsetName, method, params);    
        pageurl = urlStr;  
    }  
      
    /** 
     * @return List<String> 
     * @Author: lulei   
     * @Description: 返回页面上需求的链接地址 
     */  
    public List<String> getPageUrls(){  
        List<String> pageUrls = new ArrayList<String>(); 
        System.out.println("PageSourceCode"+":"+getPageSourceCode());
        System.out.println("UrlRegexString"+":"+getUrlRegexString());
        System.out.println("UrlRegexStringNum"+":"+getUrlRegexStringNum());
      
        pageUrls = DoRegex.getArrayList(getPageSourceCode(), getUrlRegexString(), pageurl, getUrlRegexStringNum());  
        return pageUrls;  
    }  
      
    /** 
     * @return String 
     * @Author: lulei   
     * @Description: 返回页面上需求的网址连接的正则表达式 
     */  
    public abstract String getUrlRegexString();  
      
    /** 
     * @return int 
     * @Author: lulei   
     * @Description: 正则表达式中要去的字段位置 
     */  
    public abstract int getUrlRegexStringNum();   
}  