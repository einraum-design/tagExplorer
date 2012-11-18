package tagexplorer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;

public class TagExplorer extends PApplet {

	User user = new User(0, "noname");
	SQLhelper SQL;
	PFont font;
	ControlP5 cp5_Promt;
	ControlP5 cp5_Menu;
	ArrayList<Tag> tags = new ArrayList<Tag>();

	// PFrame f;

	public void setup() {
		size(400, 400);

		SQL = new SQLhelper(this);

		// ControlP5
		font = createFont("arial", 20);

		cp5_Promt = new ControlP5(this);
		cp5_Menu = new ControlP5(this);

		// cp5_Menu.addToggle("Location").setValue(0)
		// .setPosition(200, 0).setSize(80, 40).getCaptionLabel()
		// .align(ControlP5.CENTER, ControlP5.CENTER);

		// User registration
		user = askForUser();

		// location registration

		textFont(font, 14);

		// SQL.queryTagList("files");
		
		
	}

	// public void Location(float val){
	// println("Location " + val);
	// }

	public void draw() {
		background(0);

		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}
		
		
		showFiles();

		fill(150);
		text("User: " + user.getName(), 5, 16);

		if (p != null) {
			p.showMessages();
		}
		// List l = cp5.getAll();
		// text("pc5 List.size()" + l.size(), 5, 30);
		// text("Location: " + user.getName(), 5, 16);
	}
	
	
	ArrayList<Tag> showFiles = null;
	public void readFiles(){
		showFiles = SQL.queryTagList("files");
	}
	
	public void showFiles(){
		if(showFiles != null){
			for(int i = 0; i<showFiles.size(); i++){
				text(showFiles.get(i).name, 10, 40 + i * 16);
			}
		}
	}

	public User askForUser() {
		ArrayList<User> users = SQL.listUsers();
		for (User u : users) {
			println(u.toString());
		}

		User user = users.get(0);
		return user;
	}

	public void keyPressed() {

		switch (key) {
		case 'o':
			String url = selectInput("Select a file to process:");
			if (url != null) {
				println("Create new File: url: " + url);
				createNewFile(url);
			}
			break;
		case 'l':
			createLocation();
			break;
		case 's':
			readFiles();
			break;

		// case 't':
		// List l = cp5.getAll();
		// for (Object o : l) {
		// println(o.toString());
		// println(o.getClass().getName());
		//
		// if (o instanceof controlP5.Textfield) {
		// println("instanceof textfield");
		// Textfield t = (Textfield) o;
		// // t.setValue("value");
		// // t.setLabel("label");
		// // t.setLabelVisible(false);
		// t.setVisible(false);
		// }
		//
		// }
		// break;
		// case 'z':
		// // println("neues Textinput Field");
		//
		// // createTextField("input", "type in here");
		// // createButton("save", 1);
		// break;
		}
	}

	Promt p = null;
	Boolean bSaveActive = false;
	Boolean bCancelActive = false;

	public void createLocation() {
		removeController();
		p = new Promt(this, cp5_Promt, "locationInput");
		println("locationinput created");
	}

	public void locationInput(String theText) {
		// System.out.println("function locationInput");
		theText = theText.trim();
		if (theText.equals("Type Location name here") || theText.equals("")) {
			p.message = "Enter Locationname";
		} else if (inDataBase("locations", theText)) {
			p.message = "Location " + theText + " already exists";
		} else {
			String locationName = theText;
			String coordinates = "46.39342, 2.2134";

			SQL.msql.execute("INSERT INTO " + "locations"
					+ " (name, coordinates) VALUES (\"" + locationName
					+ "\", \" " + coordinates + "\")");
			System.out.println("Location " + locationName + " registered");
			removeController();
		}
	}

	

	public void save(float value) {
		// System.out.println("trigger save!");
		if (bSaveActive) {
			List l = cp5_Promt.getAll();
			for (Object o : l) {
				// System.out.println(o.toString());
				if (o instanceof controlP5.Textfield) {
					Textfield t = (Textfield) o;

					// if locationInput
					if (t.getLabel().equals("LOCATIONINPUT")) {
						t.submit();
						System.out.println("submitted");
						break;
					}
				}
			}
		} else {
			// System.out.println("setActive: save");
			bSaveActive = true;
		}
		// System.out.println("end save");
	}

	boolean removeController = false;

	public void cancel(float value) {
		// System.out.println("trigger cancel!");
		if (bCancelActive) {
			// remove all controller
			removeController = true;
		} else {
			bCancelActive = true;
		}
		// System.out.println("end cancel");
	}

	public void removeController() {
		List l = cp5_Promt.getAll();
		for (Object ob : l) {
			((Controller) ob).remove();
		}
		bSaveActive = false;
		bCancelActive = false;
		p = null;
		System.out.println("removed Controller");
	}

	public void createNewFile(String url) {
		url = url.trim();
		int index = url.lastIndexOf("/");
		String path = url.substring(0, index);
		String fileName = url.substring(index + 1);
		Path file = FileSystems.getDefault().getPath(url);
		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(file, BasicFileAttributes.class);
			// System.out.println("creationTime: " + attr.creationTime());
			// System.out.println("lastAccessTime: " + attr.lastAccessTime());
			// System.out.println("lastModifiedTime: " +
			// attr.lastModifiedTime());
			//
			// System.out.println("isDirectory: " + attr.isDirectory());
			// System.out.println("isOther: " + attr.isOther());
			// System.out.println("isRegularFile: " + attr.isRegularFile());
			// System.out.println("isSymbolicLink: " + attr.isSymbolicLink());
			// System.out.println("size: " + attr.size());

			if (inDataBase("files", url)) {
				System.out.println("File " + fileName + " is already imported");
			} else{
				SQL.msql.execute("INSERT INTO " + "files"
						+ " (name, path, size, creation_time) VALUES (\""
						+ fileName.trim() + "\", \" " + url.trim() + "\", \" " + attr.size()
						+ "\", \" " + new Timestamp(attr.creationTime().toMillis()) + "\")");
				System.out.println("File " + fileName + " registered");
			}
		} catch (IOException e) {
			e.printStackTrace();
			println("File not saved");
		}
	}
	
	public boolean inDataBase(String tableName, String theText) {
		boolean isInDB = false;

		ArrayList<Tag> tagList = SQL.queryTagList(tableName);

		for (Tag t : tagList) {
			if (t instanceof Tag_Location) {
//				System.out.println("Location: " + t.name.trim().toLowerCase() + "\t"
//						+ (theText.trim().toLowerCase()));
				if (t.name.trim().toLowerCase().equals(theText.trim().toLowerCase())) {
					isInDB = true;
					return isInDB;
				}
			} else if (t instanceof Tag_File) {
				// path!
//				System.out.println("File: " + ((Tag_File) t).path.toLowerCase()
//						+ "\t" + (theText.toLowerCase()));
				if (((Tag_File) t).path.trim().toLowerCase().equals(
						theText.trim().toLowerCase())) {
					isInDB = true;
					return isInDB;
				}
			} else{
				println("What kind of Tag is it? in inDataBase()");
			}
			System.out.println(t.toString());
		}
		return isInDB;
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorer.TagExplorer.class.getName() });
	}

}
