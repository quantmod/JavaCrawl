package com.yuanhai.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.lulei.util.CharsetUtil;

public class MyCrawl {
	private static int maxConnectTimes = 3;
	private static HttpClient httpClient = new HttpClient();
	private static Logger log = Logger.getLogger(MyCrawl.class);
	private static Header[] responseHeaders = null;
	private static String pageSourceCode = "";
	// 网页默认编码方式
	private static String charsetName = "iso-8859-1";

	// 正则匹配需要看网页的源码,firebug看的不行
	// 爬虫+建立索引
	public static void main(String[] args) {

		String urlSeed = "http://news.baidu.com/n?cmd=4&class=sportnews&pn=1&from=tab";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("Referer", "http://www.baidu.com");
		params.put(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
		GetMethod getMethod = new GetMethod(urlSeed);

		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			getMethod.setRequestHeader(key, val);
		}

		// 得到网页源码放到pageSourceCode变量中
		try {
			readPage(getMethod, "utf-8", urlSeed);
		} catch (Exception e) {

			e.printStackTrace();
		}

		System.out.println(pageSourceCode);
		String regexStr = "&#8226;<a href=\"(.*?)\"";
		Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		Matcher matcher = pattern.matcher(pageSourceCode);
		int count = 0;
		while (matcher.find()) {
			System.out.println(matcher.group());
			System.out.println(matcher.group(1));
			System.out.println(matcher.groupCount());
			count++;
		}
		System.out.println(count);
	}

	private static boolean readPage(HttpMethod method, String defaultCharset,
			String urlStr) throws HttpException, IOException {
		int n = maxConnectTimes;
		while (n > 0) {
			try {
				if (httpClient.executeMethod(method) != HttpStatus.SC_OK) {
					log.error("can not connect " + urlStr + "\t"
							+ (maxConnectTimes - n + 1) + "\t"
							+ httpClient.executeMethod(method));
					n--;
				} else {
					// 获取头信息
					responseHeaders = method.getResponseHeaders();
					// 获取页面源代码
					InputStream inputStream = method.getResponseBodyAsStream();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(inputStream, charsetName));
					StringBuffer stringBuffer = new StringBuffer();
					String lineString = null;
					while ((lineString = bufferedReader.readLine()) != null) {
						stringBuffer.append(lineString);
						stringBuffer.append("\n");
					}
					pageSourceCode = stringBuffer.toString();
					InputStream in = new ByteArrayInputStream(
							pageSourceCode.getBytes(charsetName));
					String charset = CharsetUtil.getStreamCharset(in,
							defaultCharset);
					// 下面这个判断是为了IP归属地查询特意加上去的
					if ("Big5".equals(charset)) {
						charset = "gbk";
					}
					if (!charsetName.toLowerCase()
							.equals(charset.toLowerCase())) {
						pageSourceCode = new String(
								pageSourceCode.getBytes(charsetName), charset);
					}
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(urlStr + " -- can't connect  "
						+ (maxConnectTimes - n + 1));
				n--;
			}
		}
		return false;
	}

	// 实时搜索
	@Test
	public void search() {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_43, analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		String indexFile = "D:/index/knnik";
		Directory directory = null;
		try {
			directory = NIOFSDirectory.open(new File(indexFile));
			// 创建索引
			IndexWriter indexWriter = new IndexWriter(directory,
					indexWriterConfig);
			TrackingIndexWriter trackingIndexWriter = new TrackingIndexWriter(
					indexWriter);
			NRTManager nrtManager = new NRTManager(trackingIndexWriter,
					new SearcherFactory());

			// 查询索引
			IndexSearcher indexSearch = nrtManager.acquire();
			/*
			 * //一般的获取indexSearch的方法,非实时 IndexReader
			 * indexReader=DirectoryReader.open(directory);
			 * 
			 * IndexSearcher indexSearch=new IndexSearcher(indexReader);
			 */

			Term term = new Term("content", "我们");
			Query query = new TermQuery(term);
			TopDocs topDocs = indexSearch.search(query, 10);
			System.out.println("--------查询结果总数------");
			int totalHits = topDocs.totalHits;
			System.out.println("totalHits" + ":" + totalHits);

			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				// scoreDoc.doc获取的docID
				int docId = scoreDoc.doc;

				System.out.println("docId:" + docId);
				Document document = indexSearch.doc(docId);
				System.out.println(document.get("id"));
				System.out.println(document.get("title"));
				System.out.println(document.get("content"));
				System.out.println(document.get("url"));
			}

			nrtManager.release(indexSearch);
			nrtManager.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
