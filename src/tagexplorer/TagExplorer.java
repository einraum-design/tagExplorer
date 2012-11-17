package tagexplorer;

import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.ControllerGroup;
import controlP5.Textfield;

import processing.core.PApplet;
import processing.core.PFont;

public class TagExplorer extends PApplet {

	User user = new User(0, "noname");
	SQLhelper SQL;
	PFont font;
	ControlP5 cp5;
	ArrayList<Tag> tags = new ArrayList<Tag>();

	// PFrame f;

	public void setup() {
		size(400, 400);

		SQL = new SQLhelper(this);

		// ControlP5
		font = createFont("arial", 20);

		cp5 = new ControlP5(this);

		// User registration
		user = askForUser();

		// location registration

		textFont(font, 14);

	}

	public void draw() {
		background(0);
		
		if(removeController){
			removeController();
			removeController = !removeController;
		}

		fill(150);
		text("User: " + user.getName(), 5, 16);

		// List l = cp5.getAll();
		// text("pc5 List.size()" + l.size(), 5, 30);
		// text("Location: " + user.getName(), 5, 16);
	}

	// controlP5 INPUT

	// eventListener
	// public void controlEvent(ControlEvent theEvent) {
	//
	// println("Active Controller: " + theEvent.getController().getName());
	//
	// println(theEvent.toString());
	//
	// if (theEvent.isAssignableFrom(Textfield.class)) {
	// println("controlEvent: accessing a string from controller '"
	// + theEvent.getName() + "': " + theEvent.getStringValue());
	// }
	// }

	// Direktaufruf der Funktion Ÿber control name
	// public void input(String theText) {
	// // automatically receives results from controller input
	// println("a textfield event for controller 'input' : " + theText);
	// }

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

	Promt p;
	Boolean bSaveActive = false;
	Boolean bCancelActive = false;
	public void createLocation() {
		// createButton("save", 0, 240, 100);
		// createButton("cancel", 0, 300, 100);
		// createTextField("locationInput", "Type Location name here");
		removeController();
		p = new Promt(this, cp5, "locationInput");

		println("locationinput created");
	}

	

	public void locationInput(String theText) {
//		System.out.println("function locationInput");

		if (!theText.equals("Type Location name here") && !theText.equals("")) {
			String locationName = theText;
			String coordinates = "46.39342, 2.2134";

			// SQL.msql.execute("INSERT INTO " + "locations"
			// + " (name, coordinates) VALUES (\"" + locationName
			// + "\", \" " + coordinates + "\")");
			System.out.println("Location " + locationName + " registered");
			removeController();
		} else {
			System.out.println("no Textinput");
		}
	}

	

	public void save(float value) {
		System.out.println("trigger save!");
		if (bSaveActive) {
			List l = cp5.getAll();
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
//			System.out.println("setActive: save");
			bSaveActive = true;
		}
//		System.out.println("end save");
	}
	
	boolean removeController = false;
	public void cancel(float value) {
		System.out.println("trigger cancel!");
		if (bCancelActive) {
			// remove all controller
			removeController = true;
		} else {
			bCancelActive = true;
		}
//		System.out.println("end cancel");
	}
	
	public void removeController(){
		List l = cp5.getAll();
		for (Object ob : l) {
			((Controller) ob).remove();
		}
		bSaveActive = false;
		bCancelActive = false;
	}

	public void createNewFile(String url) {
		int index = url.lastIndexOf("/");
		String path = url.substring(0, index - 1);
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

			SQL.msql.execute("INSERT INTO " + "files"
					+ " (name, path, size, creation_time) VALUES (\""
					+ fileName + "\", \" " + url + "\", \" " + attr.size()
					+ "\", \" " + attr.creationTime() + "\")");
			System.out.println("File " + fileName + " registered");
		} catch (IOException e) {
			e.printStackTrace();
			println("File not saved");
		}
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { tagexplorer.TagExplorer.class.getName() });
	}

}
