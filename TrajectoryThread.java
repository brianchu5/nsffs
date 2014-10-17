public class TrajectoryThread implements Runnable{


private parNSFFS nsffs;


private Trajectory trajectory;

public TrajectoryThread(parNSFFS nsffs, Trajectory t) {

      this.trajectory = t;
      this.nsffs = nsffs;
}




public void run() {


nsffs.Branching(this.trajectory);


}
















}
