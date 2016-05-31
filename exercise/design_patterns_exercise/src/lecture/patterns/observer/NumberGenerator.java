package lecture.patterns.observer;

import java.util.ArrayList;

public abstract class NumberGenerator {
	
	private ArrayList<Observer> observers = new ArrayList<Observer>();
	
	public void addObserver(Observer observer) {

	}
	
	public void deleteObserer(Observer observer) {

	}
	
	public void notifyObservers() {

	
	
	}
	
	public abstract int getNumber();
	public abstract void execute();
}
