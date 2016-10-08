package fr.unice.i3s.rockflows.experiments.significance;

import java.util.List;

public class Statistics {    

    public static double getMean(List<Double> data){
        
        double sum = 0.0;
        for(Double a : data){
            sum += a;    
        }        
        return sum/data.size();
    }

    public static double getVariance(List<Double> data){
        
        double mean = getMean(data);
        double temp = 0;
        for(Double a :data){
            temp += (mean-a)*(mean-a);        
        }        
        return temp/data.size();
    }

    public static double getStdDev(List<Double> data){
        
        return Math.sqrt(getVariance(data));
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

