package lecture.patterns.proxy;

public class Main {

    public static void main(String[] args) {
        Printable p = new PrinterProxy("message to proxy 1st");
        System.out.println(p.getMessage());

        p.setMessage("message to proxy 2nd");
        System.out.println(p.getMessage());
        
        p.createMessage("message to printer 1st");
        p.createMessage("message to printer 2nd");
    }

}
