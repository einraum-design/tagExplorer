package tagexplorer;

import java.sql.Timestamp;
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

		// queries.put("files",
		// "ID, name, size, path, creation_time, expiration_time, origin_ID, score");
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

		// queries.put("files",
		// "ID, name, size, path, creation_time, expiration_time, origin_ID, score");
	}

	boolean checkConnection() {
		boolean connected = false;
		if (msql.connect()) {
			connected = true;
			// System.out.println("SQL connected");
		}
		return connected;
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
			// String queryFields = queries.get(tableName);
			// msql.query("SELECT (" + queryFields + ") FROM " + tableName);
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
							msql.getString("name"),
							msql.getString("coordinates"));
					tags.add(tag);
				} else if (tableName.equals("users")) {
					Tag_User user = new Tag_User("users", msql.getInt("ID"),
							msql.getString("name"), msql.getString("password"));
					tags.add(user);
				}
			}
		} else {
			System.out.println("not Connected queryTagList()");
		}
		return tags;
	}

	// not finished, get Ids and types
	public ArrayList<Tag> queryConnectedTagList(String tableName, Tag_File t) {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		if (checkConnection()) {
			msql.query("SELECT tag_ID, type FROM " + tableName
					+ " WHERE file_ID = " + t.id);

			ArrayList tagIds = new ArrayList();
			while (msql.next()) {
				tagIds.add(msql.getInt("tag_ID"));
				tagIds.add(msql.getString("type"));
			}
		} else {
			System.out.println("not Connected queryTagList()");
		}
		return tags;
	}

	public void addTag(Tag_File file, Tag tag) {
		if (checkConnection()) {
			// ask if COUNT der connection == 0 -> binding exists
			msql.query("SELECT COUNT(*) FROM tag_binding WHERE file_ID = \""
					+ file.id + "\" AND tag_ID = \"" + tag.id + "\"");
			msql.next();
			System.out.println("number of rows: " + msql.getInt(1));

			if (msql.getInt(1) == 0) {

				msql.execute("INSERT INTO tag_binding (file_ID, type, tag_ID, time) VALUES (\""
						+ file.id
						+ "\", \""
						+ tag.type
						+ "\", \""
						+ tag.id
						+ "\", \""
						+ new Timestamp(System.currentTimeMillis())
						+ "\")");
				System.out.println("Added Tag Binding");
			} else{
				System.out.println("File-Tag binding exists already");
			}
		}
	}
}
