package API;
import java.util.List;
import java.util.Map;

public interface clientAPI {
	
	 public Map<String, String> getSet (String key);
	 public String addSet (String key, Map<String, String> set);
	 public  boolean removeSet (String key);

	 public String addElement (String key,String field, String element);
	 
	 public void incr(String key, String field);
	 public void incrBy(String key, String field, int value);
	 public int sum (String key1, String field, String key2);
	 public int multConst(String key, String field, int constant);

	 public int mult (String key1, String field, String key2);


	 
	 public List<String> searchElement (String field, String value);
	 public List<String> searchEntry (Map<String, String> set);

	 public List<Map<String, String>> orderEntrys (String field);//crescente ou acrescentar flag
	 public List<Map<String, String>> searchGreaterThan (String field, int value);
	 public List<Map<String, String>> searchLesserThan (String field, int value);
	 
	 public boolean valuegreaterThan(String key1, String field, String key2);
	 



}
