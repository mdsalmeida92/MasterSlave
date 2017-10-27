package utils;

import java.util.List;

public class MyList {
	
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	List<String> list;

	public MyList(List<String> list) {
		super();
		this.list = list;
	}
	
	public MyList() {

	}

}
