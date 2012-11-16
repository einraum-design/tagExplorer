package tagexplorer;

public class User {
	
	private int userId;
	private String name;
	
	public User(int userId, String name){
		this.name = name;
		this.userId = userId;
	}

	
	public String getName() {
		return name;
	}
	public int getUserId() {
		return userId;
	}


	public String toString() {
		return "User [userId=" + userId + ", name=" + name + "]";
	}
}
