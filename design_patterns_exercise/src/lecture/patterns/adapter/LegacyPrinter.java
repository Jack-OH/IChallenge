package lecture.patterns.adapter;

public class LegacyPrinter {
    


    public LegacyPrinter(String string) {

    }
    
    public void show() {
        System.out.println(string);
    }
    
    public void showWithBracket() {
        System.out.println("[ " + string + " ]");
    }
}

    