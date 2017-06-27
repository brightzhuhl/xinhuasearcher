package coderz.demo.search;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface Searcher {
	
	JSONArray searchWithDefaultPolicy(String kw);
	
	JSONArray searchWithMustContainPolicy(String kw);
	
	JSONObject getKeyword(int num);
}
