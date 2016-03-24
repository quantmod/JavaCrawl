/** 
**com.lulei.lucene.index.operation.NRTIndex 
**/  
 /**   
 *@Description:  索引管理操作类，增删改三种操作 
 */   
package com.lulei.lucene.index.operation;    

import java.io.IOException;  

import org.apache.lucene.document.Document;  
import org.apache.lucene.index.Term;  
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.Query;  

import com.lulei.lucene.index.manager.IndexManager;  
    
public class NRTIndex {  
      
    private TrackingIndexWriter indexWriter;  
    private String indexName;  
      
    //直接使用IndexManager中的indexWriter，将索引的修改操作委托给TrackingIndexWriter实现  
    public NRTIndex(String indexName){  
        this.indexName = indexName;  
        indexWriter = IndexManager.getIndexManager(indexName).getTrackingIndexWriter();  
    }  
      
    /** 
     * @param doc 
     * @return boolean 
     * @Author: lulei   
     * @Description:  增加Document至索引 
     */  
    public boolean addDocument(Document doc){  
        try {  
            indexWriter.addDocument(doc);  
            return true;  
        } catch (IOException e){  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * @param query 
     * @return boolean 
     * @Author: lulei   
     * @Description: 按照Query条件从索引中删除Document 
     */  
    public boolean deleteDocument(Query query){  
        try {  
            indexWriter.deleteDocuments(query);  
            return true;  
        } catch (IOException e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * @return 
     * @Author: lulei   
     * @Description:  清空索引 
     */  
    public boolean deleteAll(){  
        try {  
            indexWriter.deleteAll();  
            return true;  
        } catch (IOException e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * @param term 
     * @param doc 
     * @return 
     * @Author: lulei   
     * @Description: 按照Term条件修改索引中Document 
     */  
    public boolean updateDocument(Term term, Document doc){  
        try {  
            indexWriter.updateDocument(term, doc);  
            return true;  
        } catch (IOException e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * @throws IOException 
     * @Author:lulei   
     * @Description: 合并索引 
     */  
    public void commit() throws IOException {  
        IndexManager.getIndexManager(indexName).getIndexWriter().commit();  
    }  
}  

