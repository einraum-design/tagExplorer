package tagexplorer;

import java.util.ArrayList;

import processing.core.PApplet;
import de.bezier.data.sql.MySQL;

public class SQLhelper {
	PApplet p5;

	MySQL msql;
	String user = "root";
	String pass = "root";
	String database = "files_db";
	String host = "localhost:8889";

	public SQLhelper(PApplet p5) {
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		System.out.println("SQL connection: " + checkConnection());
	}

	public SQLhelper(PApplet p5, String user, String pass, String database,
			String host) {
		this.user = user;
		this.pass = pass;
		this.database = database;
		this.host = host;
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		System.out.println("SQL connection: " + checkConnection());
	}

	boolean checkConnection() {
		boolean connected = false;
		if (msql.connect()) {
			connected = true;
			System.out.println("SQL connected");
		}
		return connected;
	}

	public ArrayList<User> listUsers() {
		ArrayList<User> userList = new ArrayList<User>();

		if (checkConnection()) {
			msql.query("SELECT * FROM users");
			while (msql.next()) {
				User user = new User(msql.getInt("ID"), msql.getString("name"));
				userList.add(user);
			}
		} else {
			System.out.println("not Connected listUsers()");
		}
		return userList;
	}
}
