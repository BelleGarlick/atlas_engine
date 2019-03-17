package atlas.engine;

import java.util.ArrayList;

import atlas.audio.Audio;
import atlas.userInput.UserInput;

public class Engine implements Runnable{
	
	private AGame gameLogic;
	
	private Window window;
	
	private Thread gameLoopThread;
	private Timer timer;
	private java.util.Timer fixedLoopTimer = new java.util.Timer();
	
	private final int MAX_FPS;
	private final int targetUPS;

	public static boolean showFPSinWindowTitle = false;
    public static boolean renderTerrainWireFrame = false;
    public static boolean renderEntityWireFrame = false;

		
    public Engine(AGame gameLogic, Window window, int targetUPS, int maxFPS) {
    	this.targetUPS = targetUPS;
    	this.MAX_FPS = maxFPS;
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = window;
        this.timer = new Timer();
        this.gameLogic = gameLogic;
    }

    public final void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
        	System.out.println("Add '-XstartOnFirstThread' to VM run configurations");
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            fixedLoopTimer.schedule(new java.util.TimerTask() {
                @Override public void run() {
                	gameLogic.fixedUpdate();
                }
            }, 0l,1000l/targetUPS);
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            fixedLoopTimer.cancel();
            cleanup();
        }
    }

    private final void init() throws Exception {
        window.init();
        UserInput.init(window);
        timer.init();
        Audio.init();
        gameLogic.init(window);
    }

    private final void gameLoop() {
        float elapsedTime = timer.getElapsedTime();

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            float deltaTime = timer.getElapsedTime() - elapsedTime;

                UserInput.updateControllers();
                input(deltaTime);
                update(deltaTime);

        	elapsedTime = timer.getElapsedTime();
            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanUp();
        Audio.cleanUp();
    }
    
    private void sync() {
        float loopSlot = 1f / MAX_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
	            try {
	                Thread.sleep(1);
	            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input(float interval) {
        UserInput.input(window);
    }

    protected void update(float interval) {
    	try{
        	if (showFPSinWindowTitle) {
        		window.setWindowTitle(window.getTitle() + " " + calcAverageFPS(1/interval));
        	} else {
        		window.setWindowTitle(window.getTitle());
        	}
    		gameLogic.update(interval);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    private ArrayList<Float> lastFloats = new ArrayList<>();
    private double calcAverageFPS(float interval) {
    	lastFloats.add(interval);
    	while (lastFloats.size() > 60) {
    		lastFloats.remove(0);
    	}

    	float total = 0;
    	for (float f : lastFloats) {
    		total += f;
    	}
    	return Math.floor(total / lastFloats.size());
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }
	
}
