package fr.unice.i3s.rockflows.experiments.automatictest;

import java.util.List;

public class Statistics {
    
    List<Double> data;
    int size;   

    public Statistics(List<Double> data) {
        this.data = data;
        size = data.size();
    }   

    public double getMean(){
        
        double sum = 0.0;
        for(Double a : data){
            sum += a;    
        }        
        return sum/size;
    }

    public double getVariance(){
        
        double mean = getMean();
        double temp = 0;
        for(Double a :data){
            temp += (mean-a)*(mean-a);        
        }        
        return temp/size;
    }

    public double getStdDev(){
        
        return Math.sqrt(getVariance());
    }

    /*
    public double median() 
    {
       data.sort((Double d1, Double d2) -> {           
           return Double.compare(d1, d2);
       });       

       if (data.length % 2 == 0) 
       {
          return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
       } 
       else 
       {
          return data[data.length / 2];
       }
    }
    */
}

