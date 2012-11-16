/*---------- Helper.java ----------*/
import java.io.*;
import java.util.*;
public class Helper
{
    public static void main(String[] argv) {
	try
	    {
		BufferedReader in =
		    new BufferedReader(new InputStreamReader(System.in));
		
		Document doc = Document.parse(in);
		Node root = doc.getRoot();
		if (!(root.getName().equals("catalog")))
		    throw new Exception("XML nu este Catalog"); 
		Useful use = new Useful(root.getChildren(),root);
	    }
	catch (Exception e) {
	    //e.printStackTrace();
	    System.err.println("fisierul este invalid");
	    System.err.println("stack-trace-ul erorii (pt debugging):");
	    e.printStackTrace(System.err);
	}
    }
    
}
