package fr.uge.adventure.renderer;

public class Timer {
	private long lastTime;
	private long tick;
	
	public Timer() {
		lastTime = System.nanoTime();
		tick = 0;
	}
	
	public void update() {
		long currentTime = System.nanoTime();
		tick += (currentTime - lastTime);
		lastTime = currentTime;
	}
	
	public void reset() {
		tick = 0;
	}
	
	public long tick() {
		return this.tick;
	}
}
