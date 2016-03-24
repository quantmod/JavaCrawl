/**   
 *@Description:   新闻类网站新闻内容  
 */   
package com.lulei.crawl.news;    
  
import java.io.IOException;  
import java.util.HashMap;  
  


import org.apache.commons.httpclient.HttpException;  
  


import com.lulei.crawl.CrawlBase;  
import com.lulei.util.DoRegex;
  
    
public class News extends CrawlBase{  
    private String url;  
    private String content;  
    private String title;  
    private String type;  
      
    private static String contentRegex = "<p.*?>(.*?)</p>";  
    private static String titleRegex = "<title>(.*?)</title>";  
    private static int maxLength = 300;  
      
    private static HashMap<String, String> params;  
    /** 
     * 添加相关头信息，对请求进行伪装 
     */  
    static {  
        params = new HashMap<String, String>();  
        params.put("Referer", "http://www.baidu.com");  
        params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");  
    }  
      
    /** 
     * @Author:lulei   
     * @Description: 默认p标签内的内容为正文，如果正文长度查过设置的最大长度，则截取前半部分 
     */  
    private void setContent() {  
        String content = DoRegex.getString(getPageSourceCode(), contentRegex, 1);  
        content = content.replaceAll("\n", "")  
                                      .replaceAll("<script.*?/script>", "")  
                                      .replaceAll("<style.*?/style>", "")  
                                      .replaceAll("<.*?>", "");  
        this.content = content.length() > maxLength ? content.substring(0, maxLength) : content;  
    }  
      
    /** 
     * @Author:lulei   
     * @Description: 默认title标签内的内容为标题 
     */  
    private void setTitle() {  
        this.title = DoRegex.getString(getPageSourceCode(), titleRegex, 1);;  
    }  
      
    public News(String url) throws HttpException, IOException {  
        this.url = url;  
        readPageByGet(url, "utf-8", params);  
        setContent();  
        setTitle();  
    }  
  
    public String getUrl() {  
        return url;  
    }  
  
    public void setUrl(String url) {  
        this.url = url;  
    }  
  
    public String getContent() {  
        return content;  
    }  
  
    public String getTitle() {  
        return title;  
    }  
  
    public String getType() {  
        return type;  
    }  
  
    public void setType(String type) {  
        this.type = type;  
    }  
  
    public static void setMaxLength(int maxLength) {  
        News.maxLength = maxLength;  
    }  
  
    /** 
     * @param args 
     * @throws HttpException 
     * @throws IOException 
     * @Author:lulei   
     * @Description: 测试用例 
     */  
    public static void main(String[] args) throws HttpException, IOException {  
        // TODO Auto-generated method stub    
        News news = new News("http://we.sportscn.com/viewnews-1634777.html");  
        System.out.println(news.getContent());  
        System.out.println(news.getTitle());  
    }  
  
}  