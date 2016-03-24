package com.lulei.knn.index;    
  
import org.apache.lucene.queryparser.classic.ParseException;  
import org.apache.lucene.search.Query;  
  
import com.lulei.lucene.index.model.SearchResultBean;  
import com.lulei.lucene.index.operation.NRTSearch;  
import com.lulei.lucene.query.PackQuery;  
import com.lulei.lucene.util.LuceneKey;  
    
public class KnnSearch extends NRTSearch{  
    private static PackQuery packQuery = new PackQuery(KnnIndex.indexName);  
      
    public KnnSearch() {  
        super(KnnIndex.indexName);    
    }  
      
    public String getType(String content){  
        content = LuceneKey.escapeLuceneKey(content);  
        try {  
            Query query = packQuery.getOneFieldQuery(content, "content");  
            SearchResultBean result = search(query, 0, 3);  
            if (result == null || result.getCount() == 0) {  
                return "未知";  
            }  
            if (result.getCount() < 3) {  
                return result.getDatas().get(0).get("type");  
            }  
            if (result.getDatas().get(1).get("type").equals(result.getDatas().get(2).get("type"))) {  
                return result.getDatas().get(1).get("type");  
            }   
            return result.getDatas().get(0).get("type");  
        } catch (ParseException e) {  
            // TODO Auto-generated catch block    
            e.printStackTrace();  
        }  
        return "未知";  
    }  
  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub    
        System.out.println(new KnnSearch().getType("科比"));  
    }  
  
}  