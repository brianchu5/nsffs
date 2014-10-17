import java.util.*;
import java.io.*;


public class copymodel{


public void copy(String oldmodelname, String newmodelname) throws Exception
{

    String command = "cp"; 

    try
        {
            Process process = new ProcessBuilder(command,oldmodelname, newmodelname).start();
            final int exitStatus = process.waitFor();
            //System.out.println("Processed finished with status: " + exitStatus);
        }
            catch(IOException ioe)
                {
                            ioe.printStackTrace();
                                }
}



public static void main (String[] args){

    String fx = "fx.xml";
    String fold = "foxror_stochkitF.xml";
    copymodel cm = new copymodel();
    try{
    cm.copy(fold,fx);
}  catch (Exception e){
    e.printStackTrace();
}

}


}
