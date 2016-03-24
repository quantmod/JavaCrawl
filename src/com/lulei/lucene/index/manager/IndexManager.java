/** 
**com.lulei.lucene.index.manager.IndexManager 
**/  
 /**   
 *@Description: 索引管理类    
 */   
package com.lulei.lucene.index.manager;    

import java.io.File;  
import java.io.IOException;  
import java.util.Date;  
import java.util.HashMap;  
import java.util.concurrent.TimeUnit;  

import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.index.IndexWriter;  
import org.apache.lucene.index.IndexWriterConfig;  
import org.apache.lucene.index.IndexWriterConfig.OpenMode;  
import org.apache.lucene.search.IndexSearcher;  
 
 
import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.NRTManagerReopenThread;  
import org.apache.lucene.search.SearcherFactory;  
import org.apache.lucene.store.Directory;  
import org.apache.lucene.store.NIOFSDirectory;  
import org.apache.lucene.util.Version;  

import com.lulei.lucene.index.model.ConfigBean;  
import com.lulei.lucene.index.model.IndexConfig;  

public class IndexManager {  

    private IndexWriter indexWriter;  
    //更新索引文件的IndexWriter  
    private TrackingIndexWriter trackingIndexWriter;  
    //索引文件采用的分词器  
    private Analyzer analyzer;  
    //索引管理对象  
    private NRTManager nrtManager;  
    //索引重读线程  
    private NRTManagerReopenThread nrtManagerReopenThread;  
    //索引写入磁盘线程  
    private IndexCommitThread indexCommitThread;  
      
    //索引地址  
    private String indexPath;  
    //索引重读最大、最小时间间隔  
    private double indexReopenMaxStaleSec;  
    private double indexReopenMinStaleSec;  
    //索引commit时间  
    private int indexCommitSeconds;  
    //索引名  
    private String IndexManagerName;  
    //commit时是否输出相关信息  
    private boolean bprint = true;  
      
    /** 
     * Initialization on Demand Holder式初始化IndexManager 
     */  
    private static class LazyLoadIndexManager {  
        private static final HashMap<String, IndexManager> indexManager = new HashMap<String, IndexManager>();  
          
        static {  
            for (ConfigBean configBean : IndexConfig.getConfigBean()) {  
                indexManager.put(configBean.getIndexName(), new IndexManager(configBean));  
            }  
        }  
    }  
      
    /**   
     *@Description: IndexManager私有构造方法 
     *@Author: lulei   
     *@Version: 1.1.0   
     */  
    private IndexManager(ConfigBean configBean){  
        //设置相关属性  
        analyzer = configBean.getAnalyzer();  
        indexPath = configBean.getIndexPath();  
        IndexManagerName = configBean.getIndexName();  
        indexReopenMaxStaleSec = configBean.getIndexReopenMaxStaleSec();  
        indexReopenMinStaleSec = configBean.getIndexReopenMinStaleSec();  
        indexCommitSeconds = configBean.getIndexCommitSeconds();  
        bprint = configBean.isBprint();  
        String indexFile = indexPath + IndexManagerName + "/";  
        //创建或打开索引  
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, analyzer);  
        indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);  
        Directory directory = null;  
        try {  
            directory = NIOFSDirectory.open(new File(indexFile));  
            if (IndexWriter.isLocked(directory)){  
                IndexWriter.unlock(directory);  
            }  
            this.indexWriter = new IndexWriter(directory, indexWriterConfig);  
            this.trackingIndexWriter = new TrackingIndexWriter(this.indexWriter);  
            this.nrtManager = new NRTManager(this.trackingIndexWriter, new SearcherFactory());  
        } catch(IOException e){  
            e.printStackTrace();  
        }  
        //开启守护进程  
        this.setThread();  
    }  
    /** 
     * @Author: lulei   
     * @Description: 创建索引管理线程 
     */  
    private void setThread(){  
        this.nrtManagerReopenThread = new NRTManagerReopenThread(this.nrtManager, indexReopenMaxStaleSec, indexReopenMinStaleSec);  
        this.nrtManagerReopenThread.setName("NRTManager Reopen Thread");  
        this.nrtManagerReopenThread.setPriority(Math.min(Thread.currentThread().getPriority()+2, Thread.MAX_PRIORITY));  
        this.nrtManagerReopenThread.setDaemon(true);  
        this.nrtManagerReopenThread.start();  
          
        this.indexCommitThread = new IndexCommitThread(IndexManagerName + "Index Commit Thread");  
        this.indexCommitThread.setDaemon(true);  
        this.indexCommitThread.start();  
    }  
      
    /** 
     * @return 
     * @Author:lulei   
     * @Description: 重启索引commit线程 
     */  
    public String setCommitThread() {  
        try {  
            if (this.indexCommitThread.isAlive()){  
                return "is alive";  
            }  
            this.indexCommitThread = new IndexCommitThread(IndexManagerName + "Index Commit Thread");  
            this.indexCommitThread.setDaemon(true);  
            this.indexCommitThread.start();  
        } catch (Exception e) {  
            e.printStackTrace();  
            return "failed";  
        }  
        return "reload";  
    }  
      
    /** 
     *@Description: 索引commit线程  
     *@Author: lulei   
     *@Version: 1.1.0 
     */  
    private class IndexCommitThread extends Thread{  
        private boolean flag;  
        public IndexCommitThread(String name){  
            super(name);  
        }  
          
        @SuppressWarnings("deprecation")  
        public void run(){  
            flag = true;  
            while(flag) {  
                try {  
                    indexWriter.commit();  
                    if (bprint) {  
                        System.out.println(new Date().toLocaleString() + "\t" + IndexManagerName + "\tcommit");  
                    }  
                    TimeUnit.SECONDS.sleep(indexCommitSeconds);  
                } catch (IOException e) {  
                    e.printStackTrace();  
                } catch (InterruptedException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
      
      
    /** 
     * @return IndexManager 
     * @Author: lulei   
     * @Description: 获取索引管理类 
     */  
    public static IndexManager getIndexManager(String indexName){  
        return LazyLoadIndexManager.indexManager.get(indexName);  
    }  
      
    /** 
     * @@Description:释放IndexSearcher资源 
     * @param searcher 
     */  
    public void release(IndexSearcher searcher){  
        try {  
            nrtManager.release(searcher);  
        } catch (IOException e) {  
            // TODO Auto-generated catch block    
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * @return IndexSearcher 
     * @Author: lulei   
     * @Description: 返回IndexSearcher对象，使用完之后，调用release方法进行释放 
     */  
    public IndexSearcher getIndexSearcher(){  
        try {  
            return this.nrtManager.acquire();  
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    public NRTManager getNRTManager(){  
        return this.nrtManager;  
    }  
      
    public IndexWriter getIndexWriter(){  
        return this.indexWriter;  
    }  
      
    public TrackingIndexWriter getTrackingIndexWriter(){  
        return this.trackingIndexWriter;  
    }  
      
    public Analyzer getAnalyzer(){  
        return analyzer;  
    }  
      
    /** 
     * @return 
     * @Author: lulei   
     * @Description: 获取索引中的记录条数 
     */  
    public int getIndexNum(){  
        return indexWriter.numDocs();  
    }  
}  