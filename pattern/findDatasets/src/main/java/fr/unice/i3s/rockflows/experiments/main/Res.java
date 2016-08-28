package fr.unice.i3s.rockflows.experiments.main;

import java.util.concurrent.Future;
import fr.unice.i3s.rockflows.experiments.datamining.TestResult;
import java.util.List;

public class Res{

    public Future<String> datasetPath;
    public boolean best = false;
        
    public Res(Future<String> res){
        this.datasetPath = res;
    }
        
}