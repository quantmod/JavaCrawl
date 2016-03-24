 /**   
 *@Description:   百度新闻滚动列表页，可以获取当前页面上的链接 
 */   
package com.lulei.crawl.news;    

import java.io.IOException;  
import java.util.HashMap;  

import com.lulei.crawl.CrawlListPageBase;  
    
public class BaiduNewList extends CrawlListPageBase{  
    private static HashMap<String, String> params;  
      
    /** 
     * 添加相关头信息，对请求进行伪装 
     */  
    static {  
        params = new HashMap<String, String>();  
        params.put("Referer", "http://www.baidu.com");  
        params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");  
    }  

    public BaiduNewList(String urlStr) throws IOException {  
        super(urlStr, "utf-8", "get", params);    
    }  

    @Override  
    public String getUrlRegexString() {  
        // TODO Auto-generated method stub    
        //新闻列表页中文章链接地址的正则表达式  
        return "&#8226;<a href=\"(.*?)\"";  
        
        
    }  

    @Override  
    public int getUrlRegexStringNum() {  
        // TODO Auto-generated method stub    
        //链接地址在正则表达式中的位置  
        return 1;  
    }  

    /**   
     * @param args 
     * @throws IOException  
     * @Author:lulei   
     * @Description:  测试用例 
     */  
    public static void main(String[] args) throws IOException {  
        // TODO Auto-generated method stub    
        BaiduNewList baidu = new BaiduNewList("http://news.baidu.com/n?cmd=4&class=sportnews&pn=1&from=tab");  
        for (String s : baidu.getPageUrls()) {  
            System.out.println(s);  
        }  
    }  
}  