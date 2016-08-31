package fr.unice.i3s.rockflows.experiments.main;

import java.util.concurrent.Future;

public class Res {

    public Future<String> datasetPath;
    public boolean best = false;

    public Res(Future<String> res) {
        this.datasetPath = res;
    }

}