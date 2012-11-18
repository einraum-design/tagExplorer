package tagexplorer;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import de.bezier.data.sql.MySQL;

public class SQLhelper {
	PApplet p5;

	MySQL msql;
	String user = "root";
	String pass = "root";
	String database = "files_db";
	String host = "localhost:8889";

	HashMap<String, String> queries = new HashMap<String, String>();

	public SQLhelper(PApplet p5) {
		this.p5 = p5;
		msql = new MySQL(p5, host, database, user, pass);
		System.out.println("SQL connection: " + checkConnection());

//		queries.put("files",
//				"ID, name, size, path, creation_time, expiration_time, origin_ID, score");
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
		
//		queries.put("files",
//		"ID, name, size, path, creation_time, expiration_time, origin_ID, score");
	}

	boolean checkConnection() {
		boolean connected = false;
		if (msql.connect()) {
			connected = true;
			// System.out.println("SQL connected");
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


	// ArrayList<SQLTableInfo> tableInfo = getTableFields("files");
	// for (SQLTableInfo info : tableInfo) {
	// queryFields += " " + info.name;
	// }
	public ArrayList<SQLTableInfo> getTableFields(String tableName) {
		ArrayList<SQLTableInfo> tableInfo = new ArrayList<SQLTableInfo>();

		// String query =
		// "SELECT 'COLUMN_NAME' FROM 'INFORMATION_SCHEMA'.'COLUMNS' WHERE 'TABLE_SCHEMA'='files_db' AND 'TABLE_NAME'='"
		// + tableName + "'";
		String query = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='files_db' AND TABLE_NAME='"
				+ tableName + "'";

		if (checkConnection()) {
			msql.query(query);
			while (msql.next()) {
				tableInfo.add(new SQLTableInfo(msql.getString(1), msql
						.getString(2)));
				// System.out.println(msql.getString(1)); //COLUMN_NAME
				// System.out.println(msql.getString(2)); //DATA_TYPE
			}
		} else {
			System.out.println("not Connected listUsers()");
		}

		return tableInfo;
	}

	public ArrayList<Tag> queryTagList(String tableName) {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {
			//String queryFields = queries.get(tableName);
			//msql.query("SELECT (" + queryFields + ") FROM " + tableName);
			msql.query("SELECT * FROM " + tableName);
			
			while (msql.next()) {

				if (tableName.equals("files")) {
					Tag tag = new Tag_File(tableName, msql.getInt("ID"),
							msql.getString("name"), msql.getFloat("size"),
							msql.getString("path"),
							msql.getInt("creation_time"),
							msql.getInt("expiration_time"),
							msql.getInt("origin_ID"), msql.getInt("score"));
					tags.add(tag);
				} else if (tableName.equals("locations")) {
					Tag tag = new Tag_Location(tableName, msql.getInt("ID"),
							msql.getString("name"), msql.getString("coordinates"));
					tags.add(tag);
				}
			}
		} else {
			System.out.println("not Connected queryTagList()");
		}
		return tags;
	}
}
