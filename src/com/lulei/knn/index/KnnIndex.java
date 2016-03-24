package com.lulei.knn.index;    
  
import java.util.HashSet;  
  
import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.document.Field.Store;  
import org.apache.lucene.document.StringField;  
import org.apache.lucene.document.TextField;  
import org.apache.lucene.index.Term;  
import org.wltea.analyzer.lucene.IKAnalyzer;  
  
import com.lulei.knn.data.NewsBean;  
import com.lulei.lucene.index.manager.IndexManager;  
import com.lulei.lucene.index.model.ConfigBean;  
import com.lulei.lucene.index.model.IndexConfig;  
import com.lulei.lucene.index.operation.NRTIndex;  
    
public class KnnIndex extends NRTIndex{  
    public final static String indexName = "knnik";  
      
    //当作系统加载的时候，请将下面的static配置放到系统初始化中  
    static {  
        //索引初始化  
        HashSet<ConfigBean> configBeanHS = new HashSet<ConfigBean>();  
        Analyzer analyzer = new IKAnalyzer(true);  
        ConfigBean configBean = new ConfigBean();  
        configBean.setIndexPath("d:/index");  
        configBean.setIndexName(indexName);  
        configBean.setAnalyzer(analyzer);  
        configBeanHS.add(configBean);  
        IndexConfig.setConfigBean(configBeanHS);  
        IndexManager.getIndexManager(indexName);  
    }  
      
    public KnnIndex() {  
        super(indexName);    
    }  
      
    /** 
     * @param news 
     * @Author:lulei   
     * @Description: 添加至索引 
     */  
    public void add(NewsBean news) {  
        Document doc = parse(news);  
        Term term  = new Term("id", news.getId());  
        updateDocument(term, doc);  
    }  
      
    /** 
     * @param news 
     * @return 
     * @Author:lulei   
     * @Description: 将newsbean 转化为 document 
     */  
    private Document parse(NewsBean news) {  
        if (news == null) {  
            return null;  
        }  
        Document doc = new Document();  
        doc.add(new StringField("id", news.getId(), Store.YES));  
        doc.add(new StringField("url", news.getUrl(), Store.YES));  
        doc.add(new StringField("type", news.getType(), Store.YES));  
        TextField title = new TextField("title", news.getTitle(), Store.YES);  
        title.setBoost(2.0f);  
        doc.add(title);  
        TextField content = new TextField("content", news.getContent(), Store.YES);  
        content.setBoost(1.0f);  
        doc.add(content);  
        return doc;  
    }  
}  