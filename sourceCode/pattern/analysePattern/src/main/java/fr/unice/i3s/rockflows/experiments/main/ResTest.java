package fr.unice.i3s.rockflows.experiments.main;

import java.util.concurrent.Future;

public class ResTest {

    public Future<Boolean> future;
    public String datasetName = "";

    public ResTest(Future<Boolean> res, String name) {
        this.future = res;
        this.datasetName = name;
    }

}