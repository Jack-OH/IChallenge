package lecture.patterns.proxy;

public class Printer {

    private String msg;

    public Printer(String msg) {
        this.msg = msg;
        initialize("(crating new instance ");
    }
    
    @Override
    public void setMessage(String name) {
        this.msg = name;
    }
    
    @Override
    public String getMessage() {
        return msg;
    }
    
    @Override
    public void createMessage(String msg) {

        
    }
    
    private void initialize(String msg) {
        System.out.print(msg);
        for (int i=0; i<5; i++) {

            
            
            
            System.out.print(".");
        }
        System.out.println(" completed.)");
    }
}
