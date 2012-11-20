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
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.GravityBehavior;

public class TagExplorer extends PApplet {

	Tag_User user = new Tag_User("users", 0, "noname", "no Password");
	SQLhelper SQL;
	PFont font;
	ControlP5 cp5_Promt;
	ControlP5 cp5_Menu;

	ArrayList<Tag> tags = new ArrayList<Tag>();
	ArrayList<Tag> showFiles = null;

	// PFrame f;

	// toxi VerletPhysics
	VerletPhysics physics;

	public void setup() {
		size(400, 400);
		font = createFont("arial", 20);

		SQL = new SQLhelper(this);

		// ControlP5
		cp5_Promt = new ControlP5(this);
		cp5_Menu = new ControlP5(this);

		// cp5_Menu.addToggle("Location").setValue(0)
		// .setPosition(200, 0).setSize(80, 40).getCaptionLabel()
		// .align(ControlP5.CENTER, ControlP5.CENTER);

		// User registration
		user = (Tag_User) SQL.queryTagList("users").get(0);
		showFiles = SQL.queryTagList("files");

		// toxi VerletPhysics
		physics = new VerletPhysics();
		GravityBehavior g = new GravityBehavior(new Vec3D(0, 0, -0.01f));
		physics.addBehavior(g);

		// Display settings
		textFont(font, 14);
	}

	// /////////// draw ////////////////////

	public void draw() {
		background(0);

		// removeController by Button cancel
		if (removeController) {
			removeController();
			removeController = !removeController;
		}

		showFiles();

		fill(150);
		text("User: " + user.name, 5, 16);

		if (p != null) {
			p.showMessages();
		}
		// List l = cp5.getAll();
		// text("pc5 List.size()" + l.size(), 5, 30);
		// text("Location: " + user.getName(), 5, 16);

		physics.update();
	}

	// ///////// display Files ////////////////////
	public void showFiles() {
		if (showFiles != null) {
			for (int i = 0; i < showFiles.size(); i++) {
				text(showFiles.get(i).name, 10, 40 + i * 16);
			}
		}
	}

	// ///////// INPUT ///////////////////
	public void keyPressed() {

		switch (key) {
		case 'o':
			String url = selectInput("Select a file to process:");
			if (url != null) {
				println("Create new File: url: " + url);
				createNewFile("files", url);
			}
			break;
		case 'l':
			createPromt("locations");
			break;
		case 'k':
			createPromt("keywords");
			break;
		case 't':
			Tag_File file = (Tag_File) showFiles.get(0);
			Tag tag = new Tag_Location("locations", 5, "Ort", "coordinaten");
			addTag(file, tag);
			break;

		}
	}

	// ////////// Tag Creation /////////////////////
	public void createNewFile(String tableName, String s) {
		s = s.trim();
		if (SQL.inDataBase(tableName, s)) {
			System.out.println("Tag " + s + " is already imported in "
					+ tableName);
		} else {
			createDbTag(tableName, s);
		}
	}

	public void locationInput(String theText) {
		// System.out.println("function locationInput");
		theText = theText.trim();
		String tableName = "locations";

		if (theText.equals("Type Location name here") || theText.equals("")) {
			p.message = "Enter Locationname";
		} else if (SQL.inDataBase(tableName, theText)) {
			p.message = "Location " + theText + " already exists";
		} else {
			createDbTag(tableName, theText);
			removeController();
		}
	}

	public void keywordInput(String theText) {
		// System.out.println("function locationInput");
		theText = theText.trim();

		String tableName = "keywords";

		if (theText.equals("Keyword") || theText.equals("")) {
			p.message = "Enter Keyword";
		} else if (SQL.inDataBase(tableName, theText)) {
			p.message = "Keyword " + theText + " already exists";
		} else {
			createDbTag(tableName, theText);
			removeController();
		}
	}

	// /////////// Tag in Datenbank eintragen ////////////////
	public void createDbTag(String tableName, String s) {

		if (tableName.equals("keywords")) {
			String keyword = s;
			SQL.msql.execute("INSERT INTO " + tableName + " (name) VALUES (\""
					+ keyword + "\")");
			System.out.println("Keyword " + keyword + " registered");
		} else if (tableName.equals("locations")) {
			String locationName = s;
			String coordinates = "46.39342, 2.2134";
			SQL.msql.execute("INSERT INTO " + tableName
					+ " (name, coordinates) VALUES (\"" + locationName
					+ "\", \" " + coordinates + "\")");
			System.out.println("Location " + locationName + " registered");
		} else if (tableName.equals("files")) {
			int index = s.lastIndexOf("/");
			String fileName = s.substring(index + 1);
			Path file = FileSystems.getDefault().getPath(s);
			BasicFileAttributes attr;
			try {
				attr = Files.readAttributes(file, BasicFileAttributes.class);

				if (!attr.isSymbolicLink()) {
					SQL.msql.execute("INSERT INTO " + tableName
							+ " (name, path, size, creation_time) VALUES (\""
							+ fileName.trim() + "\", \" " + s.trim()
							+ "\", \" " + attr.size() + "\", \" "
							+ new Timestamp(attr.creationTime().toMillis())
							+ "\")");
					System.out.println("File " + fileName + " registered");
				} else {
					System.out.println("File " + fileName
							+ " ist keine Datei, sondern ein Link!");
				}
			} catch (IOException e) {
				e.printStackTrace();
				println("File not saved");
			}
		}
	}

	// ///////////// Tag handling /////////////////////
	public void addTag(Tag_File file, Tag tag) {

		if (tag instanceof Tag_Location) {
			SQL.addTag(file, tag);
		}

	}

	// ///////////// Promt Location ////////////
	Promt p = null;
	Boolean bSaveActive = false;
	Boolean bCancelActive = false;

	public void createPromt(String type) {
		removeController();

		if (type.equals("locations")) {
			p = new Promt(this, cp5_Promt, "locationInput");
			println("locationinput created");
		} else if(type.equals("keywords")) {
			p = new Promt(this, cp5_Promt, "keywordInput");
			println("keywordinput created");
		}
	}

	// nur ein Textfield erlaubt, sonst unterscheidung beim submit!
	public void save(float value) {
		// System.out.println("trigger save!");
		if (bSaveActive) {
			List l = cp5_Promt.getAll();
			for (Object o : l) {
				// System.out.println(o.toString());
				if (o instanceof controlP5.Textfield) {
					Textfield t = (Textfield) o;

					// if locationInput
					//if (t.getLabel().equals("LOCATIONINPUT")) {
						t.submit();
						System.out.println("submitted");
						break;
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

	// public enum Tables
	// {
	// FILES, LOCATIONS, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	// }

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorer.TagExplorer.class.getName() });
	}

}
