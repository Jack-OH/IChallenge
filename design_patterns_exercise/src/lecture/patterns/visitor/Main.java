package lecture.patterns.visitor;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
    	Item rootDir = new Folder("DesignPatterns");
        Item settingsDir = new Folder(".settings");
        Item resDir = new Folder("res");
        Item srcDir = new Folder("src");
        Item patternsDir = new Folder("patterns");
        Item compositeDir = new Folder("composite");
        
        rootDir.add(settingsDir);
        rootDir.add(resDir);
        rootDir.add(srcDir);
        srcDir.add(patternsDir);
        patternsDir.add(compositeDir);
        
        rootDir.add(new File(".classpath", 1));
        rootDir.add(new File(".project", 1));
        
        settingsDir.add(new File("org.eclipse.core.resources.prefs", 1));
        settingsDir.add(new File("org.eclipse.jdt.core.prefs", 1));
        
        compositeDir.add(new File("Directory.java", 1));
        compositeDir.add(new File("File.java", 1));
        compositeDir.add(new File("Item.java", 1));
        compositeDir.add(new File("Main.java", 2));
        rootDir.accept(new ListVisitor());
        
        System.out.println("");
        
//        FileFinderVisitor fileTypeVisitor = new FileFinderVisitor(".java"); 
//        rootDir.accept(fileTypeVisitor);
//        Iterator<Item> it = fileTypeVisitor.getFoundFiles();
//        while(it.hasNext()) {
//        	File f = (File)it.next();
//        	System.out.println(f.toString());
//        }
       
        System.out.print(rootDir.getName() + ": " + rootDir.getSize() + "KB");     
    }

}