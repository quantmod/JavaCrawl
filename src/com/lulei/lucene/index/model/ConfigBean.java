/** 
**com.lulei.lucene.index.model.ConfigBean 
**/  
/**   
 *@Description:  索引基础配置属性 
 */  
package com.lulei.lucene.index.model;  

import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.standard.StandardAnalyzer;  
import org.apache.lucene.util.Version;  

public class ConfigBean {  
    // 分词器  
    private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);  
    // 索引地址  
    private String indexPath = "/index/";  
    private double indexReopenMaxStaleSec = 10;  
    private double indexReopenMinStaleSec = 0.025;  
    // 索引commit时间  
    private int indexCommitSeconds = 60;  
    // 索引名称  
    private String indexName = "index";  
    //commit时是否输出相关信息  
    private boolean bprint = true;  
      
    public Analyzer getAnalyzer() {  
        return analyzer;  
    }  
    public void setAnalyzer(Analyzer analyzer) {  
        this.analyzer = analyzer;  
    }  
    public String getIndexPath() {  
        return indexPath;  
    }  
    public void setIndexPath(String indexPath) {  
        if (!(indexPath.endsWith("\\") || indexPath.endsWith("/"))) {  
            indexPath += "/";  
        }  
        this.indexPath = indexPath;  
    }  
    public double getIndexReopenMaxStaleSec() {  
        return indexReopenMaxStaleSec;  
    }  
    public void setIndexReopenMaxStaleSec(double indexReopenMaxStaleSec) {  
        this.indexReopenMaxStaleSec = indexReopenMaxStaleSec;  
    }  
    public double getIndexReopenMinStaleSec() {  
        return indexReopenMinStaleSec;  
    }  
    public void setIndexReopenMinStaleSec(double indexReopenMinStaleSec) {  
        this.indexReopenMinStaleSec = indexReopenMinStaleSec;  
    }  
    public int getIndexCommitSeconds() {  
        return indexCommitSeconds;  
    }  
    public void setIndexCommitSeconds(int indexCommitSeconds) {  
        this.indexCommitSeconds = indexCommitSeconds;  
    }  
    public String getIndexName() {  
        return indexName;  
    }  
    public void setIndexName(String indexName) {  
        this.indexName = indexName;  
    }  
    public boolean isBprint() {  
        return bprint;  
    }  
    public void setBprint(boolean bprint) {  
        this.bprint = bprint;  
    }  
}  