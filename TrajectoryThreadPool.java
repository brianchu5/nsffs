import java.util.*;
import java.util.concurrent.*;


public class TrajectoryThreadPool {

  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(5);
    String Solver = "./runF.sh";
    String dataA = "foxF";
    String dataB = "rorF";
    String dataTFG = "TFG";
    String modelname = "foxror_stochkitF";
    int tfgStart = 500;
    double tfgBegin = 1000.0;
    int  timeint = 2;
    double atimeint = 2.0;
    int stoptime = 3500 ;
    Trajectory root = new Trajectory(dataA, dataB, dataTFG, modelname, 0.0,5.0,0.0,0,1);
    parNSFFS nsffs = new parNSFFS(executor, dataA, dataB,dataTFG, timeint, atimeint,stoptime,modelname,Solver,tfgBegin,tfgStart);
    Runnable tr = new TrajectoryThread(nsffs,root);
    for(int i=0;i<10;i++){
        nsffs.initialTreeCreation(root);
        executor.execute(tr); 
    }
    executor.shutdown();
}

}
