/**   
 *@Description: 获取网页信息基类 
 */   
package com.lulei.crawl;    
  
import java.io.BufferedReader;  
import java.io.ByteArrayInputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.Map;  
import java.util.Map.Entry;  
  
import org.apache.commons.httpclient.Header;  
import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.HttpException;  
import org.apache.commons.httpclient.HttpMethod;  
import org.apache.commons.httpclient.HttpStatus;  
import org.apache.commons.httpclient.methods.GetMethod;  
import org.apache.commons.httpclient.methods.PostMethod;  
import org.apache.log4j.Logger;  
  
import com.lulei.util.CharsetUtil;  
  
  
public abstract class CrawlBase {  
    private static Logger log = Logger.getLogger(CrawlBase.class);  
      
    //链接源代码  
    private String pageSourceCode = "";  
    //返回头信息  
    private Header[] responseHeaders = null;  
    //连接超时时间  
    private static int connectTimeout = 3500;  
    //连接读取时间  
    private static int readTimeout = 3500;  
    //默认最大访问次数  
    private static int maxConnectTimes = 3;  
    //网页默认编码方式  
    private static String charsetName = "iso-8859-1";  
    private static HttpClient httpClient = new HttpClient();  
      
    static {  
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectTimeout);  
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);  
    }  
      
    /** 
     * @param urlStr 
     * @param charsetName 
     * @param method 
     * @param params 
     * @return 
     * @throws HttpException 
     * @throws IOException 
     * @Author: lulei   
     * @Description: method方式访问页面 
     */  
    public boolean readPage(String urlStr, String charsetName, String method, HashMap<String, String> params) throws HttpException, IOException {  
        if ("post".equals(method) || "POST".equals(method)) {  
            return readPageByPost(urlStr, charsetName, params);  
        } else {  
            return readPageByGet(urlStr, charsetName, params);    
        }  
    }  
      
    /** 
     * @param urlStr 
     * @param charsetName 
     * @param params 
     * @return 访问是否成功 
     * @throws HttpException 
     * @throws IOException 
     * @Author: lulei   
     * @Description: Get方式访问页面 
     */  
    public boolean readPageByGet(String urlStr, String charsetName, HashMap<String, String> params) throws HttpException, IOException {  
        GetMethod getMethod = createGetMethod(urlStr, params);  
        return readPage(getMethod, charsetName, urlStr);  
    }  
      
    /** 
     * @param urlStr 
     * @param charsetName 
     * @param params 
     * @return 访问是否成功 
     * @throws HttpException 
     * @throws IOException 
     * @Author: lulei   
     * @Description: Post方式访问页面 
     */  
    public boolean readPageByPost(String urlStr, String charsetName, HashMap<String, String> params) throws HttpException, IOException{  
        PostMethod postMethod = createPostMethod(urlStr, params);  
        return readPage(postMethod, charsetName, urlStr);  
    }  
      
    /** 
     * @param method 
     * @param defaultCharset 
     * @param urlStr 
     * @return 访问是否成功 
     * @throws HttpException 
     * @throws IOException 
     * @Author: lulei   
     * @Description: 读取页面信息和头信息 
     */  
    private boolean readPage(HttpMethod method, String defaultCharset, String urlStr) throws HttpException, IOException{  
        int n = maxConnectTimes;  
        while (n > 0) {  
            try {  
                if (httpClient.executeMethod(method) != HttpStatus.SC_OK){  
                    log.error("can not connect " + urlStr + "\t" + (maxConnectTimes - n + 1) + "\t" + httpClient.executeMethod(method));  
                    n--;  
                } else {  
                    //获取头信息  
                    responseHeaders = method.getResponseHeaders();  
                    //获取页面源代码  
                    InputStream inputStream = method.getResponseBodyAsStream();  
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charsetName));  
                    StringBuffer stringBuffer = new StringBuffer();  
                    String lineString = null;  
                    while ((lineString = bufferedReader.readLine()) != null){  
                        stringBuffer.append(lineString);  
                        stringBuffer.append("\n");  
                    }  
                    pageSourceCode = stringBuffer.toString();  
                    InputStream in =new  ByteArrayInputStream(pageSourceCode.getBytes(charsetName));  
                    String charset = CharsetUtil.getStreamCharset(in, defaultCharset);  
                    //下面这个判断是为了IP归属地查询特意加上去的  
                    if ("Big5".equals(charset)) {  
                        charset = "gbk";  
                    }  
                    if (!charsetName.toLowerCase().equals(charset.toLowerCase())) {  
                        pageSourceCode = new String(pageSourceCode.getBytes(charsetName), charset);  
                    }  
                    return true;  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
                System.out.println(urlStr + " -- can't connect  " + (maxConnectTimes - n + 1));  
                n--;  
            }  
        }  
        return false;  
    }  
      
    /** 
     * @param urlStr 
     * @param params 
     * @return GetMethod 
     * @Author: lulei   
     * @Description: 设置get请求参数 
     */  
    @SuppressWarnings("rawtypes")  
    private GetMethod createGetMethod(String urlStr, HashMap<String, String> params){  
        GetMethod getMethod = new GetMethod(urlStr);  
        if (params == null){  
            return getMethod;  
        }  
        Iterator iter = params.entrySet().iterator();  
        while (iter.hasNext()) {  
            Map.Entry entry = (Map.Entry) iter.next();  
            String key = (String) entry.getKey();  
            String val = (String) entry.getValue();  
            getMethod.setRequestHeader(key, val);  
        }  
        return getMethod;  
    }  
      
    /** 
     * @param urlStr 
     * @param params 
     * @return PostMethod 
     * @Author: lulei   
     * @Description: 设置post请求参数 
     */  
    private PostMethod createPostMethod(String urlStr, HashMap<String, String> params){  
        PostMethod postMethod = new PostMethod(urlStr);  
        if (params == null){  
            return postMethod;  
        }  
        Iterator<Entry<String, String>> iter = params.entrySet().iterator();  
        while (iter.hasNext()) {  
            Map.Entry<String, String> entry =  iter.next();  
            String key = (String) entry.getKey();  
            String val = (String) entry.getValue();  
            postMethod.setParameter(key, val);  
        }  
        return postMethod;  
    }  
      
    /** 
     * @param urlStr 
     * @param charsetName 
     * @return 访问是否成功 
     * @throws IOException 
     * @Author: lulei   
     * @Description: 不设置任何头信息直接访问网页 
     */  
    public boolean readPageByGet(String urlStr, String charsetName) throws IOException{  
        return this.readPageByGet(urlStr, charsetName, null);  
    }  
      
    /** 
     * @return String 
     * @Author: lulei   
     * @Description: 获取网页源代码 
     */  
    public String getPageSourceCode(){  
        return pageSourceCode;  
    }  
      
    /** 
     * @return Header[] 
     * @Author: lulei   
     * @Description: 获取网页返回头信息 
     */  
    public Header[] getHeader(){  
        return responseHeaders;  
    }  
      
    /** 
     * @param timeout 
     * @Author: lulei   
     * @Description: 设置连接超时时间 
     */  
    public void setConnectTimeout(int timeout){  
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);  
    }  
      
    /** 
     * @param timeout 
     * @Author: lulei   
     * @Description: 设置读取超时时间 
     */  
    public void setReadTimeout(int timeout){  
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);  
    }  
      
    /** 
     * @param maxConnectTimes 
     * @Author: lulei   
     * @Description: 设置最大访问次数，链接失败的情况下使用 
     */  
    public static void setMaxConnectTimes(int maxConnectTimes) {  
        CrawlBase.maxConnectTimes = maxConnectTimes;  
    }  
  
    /** 
     * @param connectTimeout 
     * @param readTimeout 
     * @Author: lulei   
     * @Description: 设置连接超时时间和读取超时时间 
     */  
    public void setTimeout(int connectTimeout, int readTimeout){  
        setConnectTimeout(connectTimeout);  
        setReadTimeout(readTimeout);  
    }  
  
    /** 
     * @param charsetName 
     * @Author: lulei   
     * @Description: 设置默认编码方式 
     */  
    public static void setCharsetName(String charsetName) {  
        CrawlBase.charsetName = charsetName;  
    }  
}  