package joji;

import atlas.engine.Engine;
import atlas.engine.Window;

public class Main {

	public static void main(String[] args) {
		Window w = new Window("Jomi", 600, 400, true);
		Engine e = new Engine(new Joji(), w, 10, 120);
		e.start();
	}

}
