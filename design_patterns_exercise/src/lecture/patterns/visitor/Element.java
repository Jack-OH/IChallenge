package lecture.patterns.visitor;

public interface Element {
	public abstract void accept(Visitor v);
}

