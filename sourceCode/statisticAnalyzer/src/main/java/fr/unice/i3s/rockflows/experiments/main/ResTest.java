package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.datamining.TestResult;

import java.util.List;
import java.util.concurrent.Future;

public class ResTest {

    public Future<List<TestResult>> future;
    public String datasetName = "";

    public ResTest(Future<List<TestResult>> res, String name) {
        this.future = res;
        this.datasetName = name;
    }

}