import java.util.UUID;

public class Trajectory {


private String id;

private double lambda;

private double A;

private double B;

private int time;

private double weight;

private double tfg;

private String dataA;

private String dataB;

private String dataTFG;

private String modelname;

private String idname;

public Trajectory(String dataA, String dataB, String dataTFG, String modelname, double A, double B,double tfg, int time,  double weight){
UUID id = UUID.randomUUID();
this.A = A;
this.B = B;
this.time=time;
this.lambda = B-A;
this.weight = weight; 
this.tfg = tfg;
this.id=String.valueOf(id);
this.dataA = this.id + "_" +  dataA;
this.dataB = this.id + "_" + dataB; 
this.dataTFG = this.id + "_" + dataTFG;
this.modelname = this.id + "_" + modelname;
this.idname = this.id + "_.txt";

} 

public String getmodelname(){
 return this.modelname;
}
public String getdataA(){
return this.dataA;
}

public String getdataB(){
return this.dataB;
}

public String getdataTFG(){
return this.dataTFG;
}

public double getA(){
return this.A;
}

public double getB(){

return this.B;
}


public double getTFG(){
return this.tfg;
}

public int getTime(){
return this.time;
}



public double getWeight(){

return this.weight;

}  


public void setA(double A){
this.A = A;
}

public void setB(double B){
this.B = B;
}

public void setTFG(double TFG){
this.tfg = TFG;
}

public void setLambda(){

this.lambda = B-A; 

}

public double getLambda(){
return this.lambda;
}

public void setWeight(double weight){

this.weight = weight; 

}

public void incrementTime(int time){
this.time+=time;

}


public String getIDname(){
return this.idname;
    }



}
