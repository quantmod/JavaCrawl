/**   
 *@Description:      
 */   
package com.lulei.knn.data;    
  
import java.io.IOException;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
  
import com.lulei.crawl.news.BaiduNewList;  
import com.lulei.crawl.news.News;  
import com.lulei.knn.index.KnnIndex;  
import com.lulei.knn.index.KnnSearch;  
import com.lulei.util.ParseMD5;  
    
public class CrawlNews {  
    private static List<Info> infos;  
    private static KnnIndex knnIndex = new KnnIndex();  
    private static KnnSearch knnSearch = new KnnSearch();  
    private static HashMap<String, Integer> result;  
      
    static {  
        infos = new ArrayList<Info>();  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=sportnews&pn=1&from=tab", "体育类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=sportnews&pn=2&from=tab", "体育类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=sportnews&pn=3&from=tab", "体育类"));  
          
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=mil&pn=1&sub=0", "军事类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=mil&pn=2&sub=0", "军事类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=mil&pn=3&sub=0", "军事类"));  
          
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=finannews&pn=1&sub=0", "财经类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=finannews&pn=2&sub=0", "财经类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=finannews&pn=3&sub=0", "财经类"));  
          
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=internet&pn=1&from=tab", "互联网"));  
          
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=housenews&pn=1&sub=0", "房产类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=housenews&pn=2&sub=0", "房产类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=housenews&pn=3&sub=0", "房产类"));  
          
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=gamenews&pn=1&sub=0", "游戏类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=gamenews&pn=2&sub=0", "游戏类"));  
        infos.add(new Info("http://news.baidu.com/n?cmd=4&class=gamenews&pn=3&sub=0", "游戏类"));  
    }  
      
    /** 
     *@Description:  抓取网址信息 
     *@Author:lulei   
     */  
    static class Info{  
        String url;  
        String type;  
        Info(String url, String type) {  
            this.url = url;  
            this.type = type;  
        }  
    }  
      
    /** 
     * @param info 
     * @Author:lulei   
     * @Description: 抓取一个列表页面下的新闻信息 
     */  
    private void crawl(Info info) {  
        if (info == null) {  
            return;  
        }  
        try {  
            BaiduNewList baiduNewList = new BaiduNewList(info.url);  
            List<String> urls = baiduNewList.getPageUrls();  
            for (String url : urls) {  
                News news = new News(url);  
                NewsBean newBean = new NewsBean();  
                newBean.setId(ParseMD5.parseStrToMd5L32(url));  
                newBean.setType(info.type);  
                newBean.setUrl(url);  
                newBean.setTitle(news.getTitle());  
                newBean.setContent(news.getContent());  
                //保存到索引文件中  
                knnIndex.add(newBean);  
                //knn验证  
                if (news.getContent() == null || "".equals(news.getContent())) {  
                    result.put("E", 1+result.get("E"));  
                    continue;  
                }  
                if (info.type.equals(knnSearch.getType(news.getContent()))) {  
                    result.put("R", 1+result.get("R"));  
                } else {  
                    result.put("W", 1+result.get("W"));  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * @Author:lulei   
     * @Description: 启动入口 
     */  
    public void run() {  
        result = new HashMap<String, Integer>();   
        result.put("R", 0);  
        result.put("W", 0);  
        result.put("E", 0);  
        for (Info info : infos) {  
            System.out.println(info.url + "------start");  
            crawl(info);  
            System.out.println(info.url + "------end");  
        }  
        try {  
            knnIndex.commit();  
            System.out.println("R = " + result.get("R"));  
            System.out.println("W = " + result.get("W"));  
            System.out.println("E = " + result.get("E"));  
            System.out.println("精确度：" + (result.get("R") * 1.0 / (result.get("R") + result.get("W"))));  
            System.out.println("-------------finished---------------");  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    public static void main(String[] args) {  
        new CrawlNews().run();  
    }  
}  