package lecture.patterns.adapter;

public class Main {
    
    public static void main(String[] args) {
       Print p = new PrintAdapter("Hello World");
       p.printNormal();
       p.printStrong();
    }
}
    
    