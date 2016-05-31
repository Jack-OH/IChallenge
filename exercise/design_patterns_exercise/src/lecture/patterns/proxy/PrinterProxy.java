package lecture.patterns.proxy;

public class PrinterProxy {

    private String msg;

    
    public PrinterProxy(String msg) {
        this.msg = msg;
    }
    
    @Override
    public void setMessage(String msg) {
        if (printer != null) {
            printer.setMessage(msg);
        }
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public void createMessage(String msg) {
        requestToPrinter();

    }
    
    private void requestToPrinter() {
        if (printer == null) {
            printer = new Printer(msg);
        }
    }

}
