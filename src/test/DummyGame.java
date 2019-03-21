package test;

import atlas.engine.Game;

public class DummyGame extends Game{

	@Override
	public void init() {
		this.setScene(new TitleScene());
	}
	
}
