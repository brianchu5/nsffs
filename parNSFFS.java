import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.*;
import java.util.concurrent.ExecutorService;



public class parNSFFS{

  private String DataTFG;

  private String solver;

  private Stack<Trajectory> stack;

  private Bins bins;

  private TreeCounter treecounter;

  private double wmin;

  private double wmax;

  private String DataA;

  private String DataB;

  private String modelname;

  private int timeinterval;


  private double stopTime;

  private copymodel copymodel;

  private double startingTFG;

  private int tfgTime;

  private ExecutorService executor;

  private double atimeint;


public parNSFFS(ExecutorService executor, String DataA, String DataB, String DataTFG, int timeinterval, double atimeint, int stopTime, String modelname, String solver, double startingTFG, int tfgTime){

   this.stack = new Stack<Trajectory>();
   this.treecounter = new TreeCounter();
   this.wmax = 1;
   this.DataA = DataA;
   this.DataB = DataB;
   this.timeinterval = timeinterval;
   this.stopTime = stopTime;
   this.modelname = modelname;
   this.solver = solver;
   this.bins = new Bins(treecounter);
   this.startingTFG = startingTFG;
   this.DataTFG = DataTFG;
   this.executor = executor;
   this.tfgTime = tfgTime;
   this.copymodel = new copymodel();
   this.atimeint = atimeint;
}


public void initialTreeCreation(Trajectory root) {

   treecounter.incrementTreeCounter();
   double lambda = root.getLambda();
   double incomingweight = root.getWeight();
   int time = root.getTime();
   bins.updateH(lambinning((int)lambda), time,incomingweight);
   bins.updateJli(lambinning((int)lambda), time);
   // create new model file with unique id
}


public static Integer lambinning(Integer l){
    if (l>=-50 && l < -40){

        return -45;

    } else if (l >=-40 && l < -30){

        return -35;

    } else if (l >=-30 && l <-20){

        return -25;

    } else if (l >=-20 && l <-10){

        return -15;

    }else if (l >=-10 && l <0){

        return -5;
    } else if (l >=0 && l < 10){

        return 5;

    } else if (l >=10 && l <20){

        return 15;

    } else if (l >=20 && l <30){

        return 25;

    }else if (l >=30 && l <40){

        return 35;

    } else if (l>=40 && l <=50){

        return 45;
    }
    return 100;
}


public boolean WeightIsWithinRange(Trajectory tr){

   if (tr.getWeight() >= wmin && tr.getWeight() <= wmax) {
      return true;
      } else{
               return false;

      }
   }



public void Branching(Trajectory tr) {

    while (tr.getTime() < stopTime) { //simulate until end time

    if (tr.getTime()==tfgTime){  // induce signal at specified time
        tr.setTFG(startingTFG);
    }

    wmin = bins.calculateWmin();  // store value of minimum weight
     
    simulateForward(tr);        // simulate forward for a timestep

    double A = tr.getA();     // perform updates 
    double B = tr.getB();   
    double tfg = tr.getTFG();    
    int time = tr.getTime();
    double lambda = tr.getLambda();
    double incomingweight = tr.getWeight();
    bins.updateH((int)lambda,time,incomingweight);
    bins.updateJli((int)lambda,time);
    double jli = bins.getJli(lambinning((int)lambda),time);

    if (WeightIsWithinRange(tr)){   // create children
        double n = bins.calculateChildNumber(lambinning((int)lambda),time,incomingweight);
        BnDistribution bn = new BnDistribution(n);
        int nchildren = (int) bn.drawFromBn();
        for (int i=0; i<nchildren; i++){
            Trajectory t = new Trajectory(DataA, DataB,DataTFG, modelname,A,B,tfg,time,jli);
            Runnable traj = new TrajectoryThread(this,t);
            try{ 
            copymodel.copy(modelname, t.getmodelname());// create new model file with id
            } catch (Exception e){
            e.printStackTrace();
            }
            executor.execute(traj);
            }
    } else {
        Trajectory t = new Trajectory(DataA, DataB, DataTFG, modelname,A,B,tfg,time,jli);
        Runnable traj = new TrajectoryThread(this,t);
        try{
        copymodel.copy(modelname, t.getmodelname());
        } catch (Exception e){
            e.printStackTrace();
        }
        executor.execute(traj);
        // create new model file with id
    }

  }
}


public void simulateForward(Trajectory tr) {

    double A = tr.getA();
    double B = tr.getB();
    double tfg = tr.getTFG();
    String datA = tr.getdataA();
    String datB = tr.getdataB();
    String datTFG = tr.getdataTFG();
    String modname = tr.getmodelname();
    String idname = tr.getIDname();

    double lambda;
    
    ModelChange(modname , String.valueOf(B), String.valueOf(A), String.valueOf(tfg));
    try{
        callStochKit(idname,datA, datB, datTFG,timeinterval,1,1);
    } catch (Exception e){
        e.printStackTrace();
    }
    try{
        A = readLines(tr.getdataA());
    } catch (IOException e){
        e.printStackTrace();
        }
    try{
        B = readLines(tr.getdataB());
    } catch (IOException e) {
        e.printStackTrace();   
    }
    try{
        tfg = readLines(tr.getdataTFG());
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    tr.incrementTime(timeinterval);
    tr.setA(A);
    tr.setB(B);
    tr.setLambda();
    tr.setTFG(tfg);

}


public static double readLines(String filename) throws IOException
{
    FileReader fileReader = new FileReader(filename);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    java.util.List <String> lines = new ArrayList<String>();
    String line = null;

    while ((line = bufferedReader.readLine()) != null){
                lines.add(line);
    }

    bufferedReader.close();

    String[] strlines = lines.toArray(new String[lines.size()]);

    double[] doublelines = new double[strlines.length];

    for (int i = 0; i<strlines.length; i++){

        doublelines[i] = Double.parseDouble(strlines[i]);
    }

    return doublelines[0];
}

public void callStochKit(String idname,String dataA, String dataB, String dataTFG, double simtime, int realizations,int interval) throws Exception
{
    String st = Double.toString(simtime);
    String real = Integer.toString(realizations);
    int Interval=interval;
    String ival = Integer.toString(Interval);
    try
        {
            Process process = new ProcessBuilder(solver,st,dataA, dataB, dataTFG, real,ival,idname).start();
            final int exitStatus = process.waitFor();
            //System.out.println("Processed finished with status: " + exitStatus);
        }
            catch(IOException ioe)
                {
                            ioe.printStackTrace();
                                }
}


public void ModelChange(String filename,String FOX, String ROR, String TFG){
    try{
                File model = new File(filename);
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                        Document doc = dBuilder.parse(model);
                                                doc.getDocumentElement().normalize();
                                                        updateElementValue(doc,FOX,ROR,TFG);
                                                               TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                                                           Transformer transformer = transformerFactory.newTransformer();
                                                                                       DOMSource source = new DOMSource(doc);
                                                                                                   StreamResult result = new StreamResult(new File(filename));
                                                                                                           transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                                                                                                       transformer.transform(source, result);
                                                                                                                                  // System.out.println("XML file updated successfully");
    }catch (SAXException | ParserConfigurationException|IOException | TransformerException  e1)
    {
                e1.printStackTrace();
                        }
}
//helper method for modelchange
public static void updateElementValue(Document doc, String FOX,String ROR, String TFG){
    NodeList cytokines = doc.getElementsByTagName("InitialPopulation");
    Node fox = (Node) cytokines.item(0);
    Node ror = (Node) cytokines.item(1);
    Node tfg = (Node) cytokines.item(2);
    //System.out.println(fox.getTextContent());
    fox.setTextContent(FOX);
    ror.setTextContent(ROR);
    tfg.setTextContent(TFG);
    //System.out.println(fox.getTextContent());
}




}

