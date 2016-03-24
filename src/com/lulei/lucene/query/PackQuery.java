/** 
**com.lulei.lucene.query.PackQuery 
**/  
 /**   
 *@Description:  创建查询Query   
 */   
package com.lulei.lucene.query;    

import java.io.IOException;  
import java.io.StringReader;  
import java.util.ArrayList;  

import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;  
import org.apache.lucene.index.Term;  
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;  
import org.apache.lucene.queryparser.classic.ParseException;  
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;  
import org.apache.lucene.search.BooleanQuery;  
import org.apache.lucene.search.NumericRangeQuery;  
import org.apache.lucene.search.PhraseQuery;  
import org.apache.lucene.search.PrefixQuery;  
import org.apache.lucene.search.Query;  
import org.apache.lucene.search.TermQuery;  
import org.apache.lucene.search.TermRangeQuery;  
import org.apache.lucene.search.WildcardQuery;  
import org.apache.lucene.util.Version;  

import com.lulei.lucene.index.manager.IndexManager;  


public class PackQuery {  
    //分词器  
    private Analyzer analyzer;  
    //使用索引中的分词器  
    public PackQuery(String indexName) {  
        analyzer = IndexManager.getIndexManager(indexName).getAnalyzer();  
    }  
    //使用自定义分词器  
    public PackQuery(Analyzer analyzer) {  
        this.analyzer = analyzer;  
    }  

    /** 
     * @param key 
     * @param fields 
     * @return Query 
     * @throws ParseException 
     * @Author: lulei   
     * @Description: 查询字符串匹配多个查询域 
     */  
    public Query getMultiFieldQuery(String key, String[] fields) throws ParseException{  
        MultiFieldQueryParser parse = new MultiFieldQueryParser(Version.LUCENE_43, fields, analyzer);  
        Query query = null;  
        query = parse.parse(key);  
        return query;  
    }  
      
    /** 
     * @param key 
     * @param field 
     * @return Query 
     * @throws ParseException 
     * @Author: lulei   
     * @Description: 查询字符串匹配单个查询域 
     */  
    public Query getOneFieldQuery(String key, String field) throws ParseException{  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        QueryParser parse = new QueryParser(Version.LUCENE_43, field, analyzer);  
        Query query = null;  
        query = parse.parse(key);  
        return query;  
    }  
      
    /** 
     * @param key 
     * @param fields 
     * @param occur 
     * @return Query 
     * @throws IOException 
     * @Author: lulei   
     * @Description: 查询字符串、多个查询域以及查询域在查询语句中的关系 
     */  
    public Query getBooleanQuery(String key, String[] fields, Occur[] occur) throws IOException{  
        if (fields.length != occur.length){  
            System.out.println("fields.length isn't equals occur.length, please check params!");  
            return null;  
        }  
        BooleanQuery query = new BooleanQuery();  
        TokenStream tokenStream = analyzer.tokenStream("", new StringReader(key));  
        ArrayList<String> analyzerKeys = new ArrayList<String>();  
        while(tokenStream.incrementToken()){  
            CharTermAttribute term = tokenStream.getAttribute(CharTermAttribute.class);  
            analyzerKeys.add(term.toString());  
        }  
        for(int i = 0; i < fields.length; i++){  
            BooleanQuery queryField = new BooleanQuery();  
            for(String analyzerKey : analyzerKeys){  
                TermQuery termQuery = new TermQuery(new Term(fields[i], analyzerKey));  
                queryField.add(termQuery, Occur.SHOULD);  
            }  
            query.add(queryField, occur[i]);  
        }  
        return query;  
    }  
      
    /** 
     * @param querys 
     * @param occur 
     * @return Query 
     * @Author: lulei   
     * @Description: 组合多个查询，之间的关系由occur确定 
     */  
    public Query getBooleanQuery(ArrayList<Query> querys, ArrayList<Occur> occurs){  
        if (querys.size() != occurs.size()){  
            System.out.println("querys.size() isn't equals occurs.size(), please check params!");  
            return null;  
        }  
        BooleanQuery query = new BooleanQuery();  
        for (int i = 0; i < querys.size(); i++){  
            query.add(querys.get(i), occurs.get(i));  
        }  
        return query;  
    }  
      
    /** 
     * @param fieldName 
     * @param value 
     * @return 
     * @Author: lulei   
     * @Description: StringField属性的搜索 
     */  
    public Query getStringFieldQuery(String value, String fieldName){  
        Query query = null;  
        query = new TermQuery(new Term(fieldName, value));  
        return query;  
    }  
      
    /** 
     * @param fields 
     * @param values 
     * @return 
     * @Author: lulei   
     * @Description: 多个StringField属性的搜索 
     */  
    public Query getStringFieldQuery(String[] values, String[] fields, Occur occur){  
        if (fields == null || values == null || fields.length != values.length){  
            return null;  
        }  
        ArrayList<Query> querys = new ArrayList<Query>();  
        ArrayList<Occur> occurs = new ArrayList<Occur>();  
        for (int i = 0; i < fields.length; i++){  
            querys.add(getStringFieldQuery(values[i], fields[i]));  
            occurs.add(occur);  
        }  
        return getBooleanQuery(querys, occurs);  
    }  
      
    /** 
     * @param key 
     * @param field 
     * @param lucene43 
     * @return 
     * @throws ParseException 
     * @Author: lulei   
     * @Description: 查询字符串和单个查询域 QueryParser是否使用4.3 
     */  
    public Query getOneFieldQuery(String key, String field, boolean lucene43) throws ParseException{  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        if (lucene43){  
            return getOneFieldQuery(key, field);  
        }  
        @SuppressWarnings("deprecation")  
        QueryParser parse = new QueryParser(Version.LUCENE_30, field, analyzer);  
        Query query = null;  
        query = parse.parse(key);  
        return query;  
    }  
      
    /** 
     * @param key 
     * @param field 
     * @Author: lulei   
     * @Description: key开头的查询字符串，和单个域匹配 
     */  
    public Query getStartQuery(String key, String field) {  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        Query query = new PrefixQuery(new Term(field, key));  
        return  query;  
    }  
      
    /** 
     * @param key 
     * @param fields 
     * @param occur 
     * @Author: lulei   
     * @Description: key开头的查询字符串，和多个域匹配，每个域之间的关系由occur确定 
     */  
    public Query getStartQuery(String key, String []fields, Occur occur){  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        ArrayList<Query> querys = new ArrayList<Query>();  
        ArrayList<Occur> occurs = new ArrayList<Occur>();   
        for (String field : fields) {  
            querys.add(getStartQuery(key, field));  
            occurs.add(occur);  
        }  
        return getBooleanQuery(querys, occurs);  
    }  
      
    /** 
     * @param key 
     * @param fields 
     * @Author: lulei   
     * @Description: key开头的查询字符串，和多个域匹配，每个域之间的关系Occur.SHOULD 
     */  
    public Query getStartQuery(String key, String []fields) {  
        return getStartQuery(key, fields, Occur.SHOULD);  
    }  
      
    /** 
     * @param key 
     * @param field 
     * @param slop 
     * @return 
     * @Author:lulei   
     * @Description: 自定每个词元之间的最大距离 
     */  
    public Query getPhraseQuery(String key, String field, int slop) {  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        StringReader reader = new StringReader(key);  
        PhraseQuery query = new PhraseQuery();  
        query.setSlop(slop);  
        try {  
            TokenStream  tokenStream  = this.analyzer.tokenStream(field, reader);  
            tokenStream.reset();  
            CharTermAttribute  term = tokenStream.getAttribute(CharTermAttribute.class);  
            while(tokenStream.incrementToken()){    
                query.add(new Term(field, term.toString()));  
            }   
            reader.close();   
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;  
        }  
        return query;  
    }  
      
    /** 
     * @param key 
     * @param fields 
     * @param slop 
     * @param occur 
     * @return 
     * @Author:lulei   
     * @Description: 自定每个词元之间的最大距离，查询多个域，每个域之间的关系由occur确定 
     */  
    public Query getPhraseQuery(String key, String[] fields, int slop, Occur occur) {  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        ArrayList<Query> querys = new ArrayList<Query>();  
        ArrayList<Occur> occurs = new ArrayList<Occur>();   
        for (String field : fields) {  
            querys.add(getPhraseQuery(key, field, slop));  
            occurs.add(occur);  
        }  
        return getBooleanQuery(querys, occurs);  
    }  
      
    /** 
     * @param key 
     * @param fields 
     * @param slop 
     * @return 
     * @Author:lulei   
     * @Description:  自定每个词元之间的最大距离，查询多个域，每个域之间的关系是Occur.SHOULD 
     */  
    public Query getPhraseQuery(String key, String[] fields, int slop) {  
        return getPhraseQuery(key, fields, slop, Occur.SHOULD);  
    }  
      
    /** 
     * @param key 
     * @param field 
     * @return 
     * @Author:lulei   
     * @Description: 通配符检索 eg:getWildcardQuery("a*thor", "field") 
     */  
    public Query getWildcardQuery(String key, String field) {  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        return new WildcardQuery(new Term(field, key));  
    }  
      
    /** 
     * @param key 
     * @param fields 
     * @param occur 
     * @return 
     * @Author:lulei   
     * @Description: 通配符检索，域之间的关系为occur 
     */  
    public Query getWildcardQuery(String key, String[] fields, Occur occur) {  
        if (key == null || key.length() < 1){  
            return null;  
        }  
        ArrayList<Query> querys = new ArrayList<Query>();  
        ArrayList<Occur> occurs = new ArrayList<Occur>();   
        for (String field : fields) {  
            querys.add(getWildcardQuery(key, field));  
            occurs.add(occur);  
        }  
        return getBooleanQuery(querys, occurs);  
    }  
      
    /** 
     * @param key 
     * @param fields 
     * @return 
     * @Author:lulei   
     * @Description: 通配符检索，域之间的关系为Occur.SHOULD 
     */  
    public Query getWildcardQuery(String key, String[] fields) {  
        return getWildcardQuery(key, fields, Occur.SHOULD);  
    }  
      
    /** 
     * @param keyStart 
     * @param keyEnd 
     * @param field 
     * @param includeStart 
     * @param includeEnd 
     * @return 
     * @Author:lulei   
     * @Description: 范围搜索 
     */  
    public Query getRangeQuery (String keyStart, String keyEnd, String field, boolean includeStart, boolean includeEnd) {  
        return TermRangeQuery.newStringRange(field, keyStart, keyEnd, includeStart, includeEnd);  
    }  
      
    /** 
     * @param min 
     * @param max 
     * @param field 
     * @param includeMin 
     * @param includeMax 
     * @return 
     * @Author:lulei   
     * @Description: 范围搜索 
     */  
    public Query getRangeQuery (int min, int max, String field, boolean includeMin, boolean includeMax) {  
        return NumericRangeQuery.newIntRange(field, min, max, includeMin, includeMax);  
    }  
      
    /** 
     * @param min 
     * @param max 
     * @param field 
     * @param includeMin 
     * @param includeMax 
     * @return 
     * @Author:lulei   
     * @Description: 范围搜索 
     */  
    public Query getRangeQuery (float min, float max, String field, boolean includeMin, boolean includeMax) {  
        return NumericRangeQuery.newFloatRange(field, min, max, includeMin, includeMax);  
    }  
      
    /** 
     * @param min 
     * @param max 
     * @param field 
     * @param includeMin 
     * @param includeMax 
     * @return 
     * @Author:lulei   
     * @Description: 范围搜索 
     */  
    public Query getRangeQuery (double min, double max, String field, boolean includeMin, boolean includeMax) {  
        return NumericRangeQuery.newDoubleRange(field, min, max, includeMin, includeMax);  
    }  
      
    public static void main(String[] args) throws IOException {  
    }  
}  

