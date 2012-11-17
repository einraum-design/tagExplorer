package tagexplorer;

public class Tag {
	
	int id;
	String name;
//	String type;
	
	
	
	
	public Tag(){
		
	}
	
	//locations
	public Tag(String tableName, int id, String name){
		
		if(tableName.equals("locations")){
			this.id = id;
			this.name = name;
		}
		
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + "]";
	}
}
