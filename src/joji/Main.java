package joji;

import atlas.engine.Engine;
import atlas.engine.Window;

public class Main {

	public static void main(String[] args) {
		Window w = new Window("Joji", 800, 600, true);
		Engine e = new Engine(new Joji(), w, 10, 120);
		e.start();
	}

}
