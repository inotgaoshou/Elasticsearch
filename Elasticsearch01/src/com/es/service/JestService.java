package com.es.service;

import java.util.List;

import com.google.gson.GsonBuilder;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;

public class JestService {

	/**
	 * ��ȡJestClient����
	 * @return
	 */
	public JestClient getJestClient() {  
			
		JestClientFactory factory = new JestClientFactory();
	    factory.setHttpClientConfig(new HttpClientConfig
	                           .Builder("http://localhost:9200")
	                           .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create())
	                           .connTimeout(1500)
	                           .readTimeout(3000)
	                           .multiThreaded(true)
	                           .build());
	    return factory.getObject();
    }
	
	/**
	 * ��������
	 * @param jestClient
	 * @param indexName
	 * @return
	 * @throws Exception
	 */
	public boolean createIndex(JestClient jestClient, String indexName) throws Exception {
	    
		JestResult jr = jestClient.execute(new CreateIndex.Builder(indexName).build());
		return jr.isSucceeded();
	}
	
	/**
	 * Putӳ��
	 * @param jestClient
	 * @param indexName
	 * @param typeName
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public boolean createIndexMapping(JestClient jestClient, String indexName, String typeName, String source) throws Exception {

		PutMapping putMapping = new PutMapping.Builder(indexName, typeName, source).build();
		JestResult jr = jestClient.execute(putMapping);
	    return jr.isSucceeded();
    }
	
	/**
	 * Getӳ��
	 * @param jestClient
	 * @param indexName
	 * @param typeName
	 * @return
	 * @throws Exception
	 */
	public String getIndexMapping(JestClient jestClient, String indexName, String typeName) throws Exception {

		GetMapping getMapping = new GetMapping.Builder().addIndex(indexName).addType(typeName).build();
	    JestResult jr = jestClient.execute(getMapping);
	    return jr.getJsonString();
    }
	
	/**
	 * �����ĵ�
	 * @param jestClient
	 * @param indexName
	 * @param typeName
	 * @param objs
	 * @return
	 * @throws Exception
	 */
	public boolean index(JestClient jestClient, String indexName, String typeName, List<Object> objs) throws Exception {
	    
	    Bulk.Builder bulk = new Bulk.Builder().defaultIndex(indexName).defaultType(typeName);
	    for (Object obj : objs) {
	      Index index = new Index.Builder(obj).build();
	      bulk.addAction(index);
	    }
	    BulkResult br = jestClient.execute(bulk.build());
	    return br.isSucceeded();
    }
	
	/**
	 * �����ĵ�
	 * @param jestClient
	 * @param indexName
	 * @param typeName
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public SearchResult search(JestClient jestClient, String indexName, String typeName, String query) throws Exception {
	    
	    Search search = new Search.Builder(query)
	        .addIndex(indexName)
	        .addType(typeName)
	        .build();
	    return jestClient.execute(search);
    }
	
	/**
	 * Count�ĵ�
	 * @param jestClient
	 * @param indexName
	 * @param typeName
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public Double count(JestClient jestClient, String indexName, String typeName, String query) throws Exception {

	    Count count = new Count.Builder()
	        .addIndex(indexName)
	        .addType(typeName)
	        .query(query)
	        .build();
	    CountResult results = jestClient.execute(count); 
	    return results.getCount();
	}
	
	/**
	 * Get�ĵ�
	 * @param jestClient
	 * @param indexName
	 * @param typeName
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public JestResult get(JestClient jestClient, String indexName, String typeName, String id) throws Exception {
		  
	    Get get = new Get.Builder(indexName, id).type(typeName).build();
	    return jestClient.execute(get);
	}
	
	/**
	 * Delete����
	 * @param jestClient
	 * @param indexName
	 * @return
	 * @throws Exception
	 */
	public boolean delete(JestClient jestClient, String indexName) throws Exception {

	    JestResult jr = jestClient.execute(new DeleteIndex.Builder(indexName).build());
	    return jr.isSucceeded();
	}
	
	/**
	 * Delete�ĵ�
	 * @param jestClient
	 * @param indexName
	 * @param typeName
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean delete(JestClient jestClient, String indexName, String typeName, String id) throws Exception {
	    
	    DocumentResult dr = jestClient.execute(new Delete.Builder(id).index(indexName).type(typeName).build());
	    return dr.isSucceeded();
	}
	
	/**
	 * �ر�JestClient�ͻ���
	 * @param jestClient
	 * @throws Exception
	 */
	public void closeJestClient(JestClient jestClient) throws Exception {
	    
	    if (jestClient != null) {
	      jestClient.shutdownClient();
	    }
    }
}

