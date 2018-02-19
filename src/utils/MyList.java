package utils;

import java.io.Serializable;
import java.util.List;

public class MyList implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private List<String> list;
	
	public MyList(List<String> list) {
		super();
		this.list = list;
	}
	
	public MyList() {

	}
	
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}


}
