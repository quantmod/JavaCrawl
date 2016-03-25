package com.yuanhai.test;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.steadystate.css.parser.ParseException;

/** 
 * @author 
 * @date 
 * @version 
 *  
 */  
public class TianyaTestByHttpClient {  
    /** 
     *  无法实现js页面的自动跳转，HtmlUnit可以
     */  
    public static void main(String[] args) throws Exception {  
        // 这是一个测试，也是为了让大家看的更清楚，请暂时抛开代码规范性，不要纠结于我多建了一个局部变量等  
        // 得到认证https的浏览器对象  
        HttpClient client = getSSLInsecureClient();  
        // 得到我们需要的post流  
        HttpPost post = getPost();  
        // 使用我们的浏览器去执行这个流,得到我们的结果  
        HttpResponse hr = client.execute(post);  
        // 在控制台输出我们想要的一些信息  
        showResponseInfo(hr);  
    }  

    private static void showResponseInfo(HttpResponse hr) throws ParseException, IOException {  

        System.out.println("响应状态行信息：" + hr.getStatusLine());  
        System.out.println("---------------------------------------------------------------");  

        System.out.println("响应头信息：");  
        Header[] allHeaders = hr.getAllHeaders();  
        for (int i = 0; i < allHeaders.length; i++) {  
            System.out.println(allHeaders[i].getName() + ":" + allHeaders[i].getValue());  
        }  

        System.out.println("---------------------------------------------------------------");  
        System.out.println("响应正文：");  
        System.out.println(EntityUtils.toString(hr.getEntity()));  
        
     /*   <body>
    	<script>
    		location.href="http://passport.tianya.cn:80/online/loginSuccess.jsp?fowardurl=http%3A%2F%2Fwww.tianya.cn%2F110486326&userthird=&regOrlogin=%E7%99%BB%E5%BD%95%E4%B8%AD......&t=1458895519504&k=06d41f547cd05fb5dea1590a60e1ec98&c=669767baea73097dde58423fac777138";
        </script>
        </body>*/
    

    }  

    // 得到一个认证https链接的HttpClient对象（因为我们将要的天涯登录是Https的）  
    // 具体是如何工作的我们后面会提到的  
    private static HttpClient getSSLInsecureClient() throws Exception {  
        // 建立一个认证上下文，认可所有安全链接，当然，这是因为我们仅仅是测试，实际中认可所有安全链接是危险的  
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
                return true;  
            }  
        }).build();  
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);  
        return HttpClients.custom().//  
                setSSLSocketFactory(sslsf)//  
                // .setProxy(new HttpHost("127.0.0.1", 8888))  
                .build();  
    }  

    // 获取我们需要的Post流，如果你是把我的代码复制过去，请记得更改为你的用户名和密码  
    private static HttpPost getPost() {  
        HttpPost post = new HttpPost("https://passport.tianya.cn/login");  

        // 首先我们初始化请求头  
        post.addHeader("Referer", "https://passport.tianya.cn/login.jsp");  
        post.addHeader("Host", "passport.tianya.cn");  
        post.addHeader("Origin", "http://passport.tianya.cn");  

        // 然后我们填入我们想要传递的表单参数（主要也就是传递我们的用户名和密码）  
        // 我们可以先建立一个List，之后通过post.setEntity方法传入即可  
        // 写在一起主要是为了大家看起来方便，大家在正式使用的当然是要分开处理，优化代码结构的  
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();  
        /*  
         * 添加我们要的参数，这些可以通过查看浏览器中的网络看到，如下面我的截图中看到的一样 
         * 不论你用的是firebug,httpWatch或者是谷歌自带的查看器也好,都能查看到（后面会推荐辅助工具来查看） 
         * 要把表单需要的参数都填齐，顺序不影响 
         */  
        paramsList.add(new BasicNameValuePair("Submit", ""));  
        paramsList.add(new BasicNameValuePair("fowardURL", "http://www.tianya.cn"));  
        paramsList.add(new BasicNameValuePair("from", ""));  
        paramsList.add(new BasicNameValuePair("method", "name"));  
        paramsList.add(new BasicNameValuePair("returnURL", ""));  
        paramsList.add(new BasicNameValuePair("rmflag", "1"));  
        paramsList.add(new BasicNameValuePair("__sid", "1#1#1.0#a6c606d9-1efa-4e12-8ad5-3eefd12b8254"));  

        // 你可以申请一个天涯的账号 并在下两行代码中替换为你的用户名和密码  
        paramsList.add(new BasicNameValuePair("vwriter", "u_110486326"));// 替换为你的用户名  
        paramsList.add(new BasicNameValuePair("vpassword", "X0up4d65"));// 你的密码  

        // 将这个参数list设置到post中  
        post.setEntity(new UrlEncodedFormEntity(paramsList, Consts.UTF_8));  
        return post;  
    }  

}  