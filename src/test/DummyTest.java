package test;

import atlas.engine.*;

public class DummyTest {

	static Engine engine;
	
	public static void main(String[] args) {
		AGame test = new DummyGame();
		engine = new Engine(test, new Window("test", 800, 600, true), 20, 60);
		engine.start();
	}

}
