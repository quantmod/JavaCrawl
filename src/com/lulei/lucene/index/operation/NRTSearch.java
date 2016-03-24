/** 
**com.lulei.lucene.index.operation.NRTSearch 
**/  
/** 
 * @Description:  索引的查询操作 
 */  
package com.lulei.lucene.index.operation;  

import java.util.ArrayList;  
import java.util.List;  

import org.apache.lucene.document.Document;  
import org.apache.lucene.search.IndexSearcher;  
import org.apache.lucene.search.Query;  
import org.apache.lucene.search.Sort;  
import org.apache.lucene.search.TopDocs;  

import com.lulei.lucene.index.manager.IndexManager;  
import com.lulei.lucene.index.model.SearchResultBean;  

public class NRTSearch {  
    private IndexManager indexManager;  
      
    /** 
     * @param indexName 索引名 
     */  
    public NRTSearch(String indexName) {  
        indexManager = IndexManager.getIndexManager(indexName);  
    }  
      
    /** 
     * @return 
     * @Author:lulei   
     * @Description: 索引中的记录数量 
     */  
    public int getIndexNum() {  
        return indexManager.getIndexNum();  
    }  
      
    /** 
     * @param query 查询字符串 
     * @param start 起始位置 
     * @param end 结束位置 
     * @author lulei 
     * @return 查询结果 
     */  
    public SearchResultBean search(Query query, int start, int end) {  
        start = start < 0 ? 0 : start;  
        end = end < 0 ? 0 : end;  
        if (indexManager == null || query == null || start >= end) {  
            return null;  
        }  
        SearchResultBean result = new SearchResultBean();  
        List<Document> datas = new ArrayList<Document>();  
        result.setDatas(datas);  
        IndexSearcher searcher = indexManager.getIndexSearcher();  
        try {  
            TopDocs docs = searcher.search(query, end);  
            result.setCount(docs.totalHits);  
            end = end > docs.totalHits ? docs.totalHits : end;  
            for (int i = start; i < end; i++) {  
                datas.add(searcher.doc(docs.scoreDocs[i].doc));  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            indexManager.release(searcher);  
        }  
        return result;  
    }  
      
    /** 
     * @param query 查询字符串 
     * @param start 起始位置 
     * @param end 结束位置 
     * @param sort 排序条件 
     * @return 查询结果 
     */  
    public SearchResultBean search(Query query, int start, int end, Sort sort) {  
        start = start < 0 ? 0 : start;  
        end = end < 0 ? 0 : end;  
        if (indexManager == null || query == null || start >= end) {  
            return null;  
        }  
        SearchResultBean result = new SearchResultBean();  
        List<Document> datas = new ArrayList<Document>();  
        result.setDatas(datas);  
        IndexSearcher searcher = indexManager.getIndexSearcher();  
        try {  
            TopDocs docs = searcher.search(query, end, sort);  
            result.setCount(docs.totalHits);  
            end = end > docs.totalHits ? docs.totalHits : end;  
            for (int i = start; i < end; i++) {  
                datas.add(searcher.doc(docs.scoreDocs[i].doc));  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            indexManager.release(searcher);  
        }  
        return result;  
    }  
      
    /** 
     * @param start 
     * @param count 
     * @return 
     * @Author:lulei   
     * @Description: 按序号检索 
     */  
    public SearchResultBean search(int start, int count) {  
        start = start < 0 ? 0 : start;  
        count = count < 0 ? 0 : count;  
        if (indexManager == null) {  
            return null;  
        }  
        SearchResultBean result = new SearchResultBean();  
        List<Document> datas = new ArrayList<Document>();  
        result.setDatas(datas);  
        IndexSearcher searcher = indexManager.getIndexSearcher();  
        result.setCount(count);  
        try {  
            for (int i = 0; i < count; i++) {  
                datas.add(searcher.doc((start + i) % getIndexNum()));  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            indexManager.release(searcher);  
        }  
        return result;  
    }  
}  