package test;

import atlas.engine.AGame;

public class DummyGame extends AGame{

	@Override
	public void init() {
		this.setScene(new TitleScene());
	}
	
}
