package fr.unice.i3s.rockflows.experiments.datamining;

public class TestResult {

    public String algoName = "";
    public int algoId = -1;
    public int preProcId = -1;
    public boolean compatible = false;
    public double accuracyAvg = 0;
    public double trainTimeAvg = 0;
    public double testTimeAvg = 0;
    public double totalTimeAvg = 0;
    public double ramAvg = 0;
    public double[] accuracies;
    public double[] times;
    public double[] rams;
    public double accuracyStDev = 0;
    public double[] contPreProc = new double[13];
    public int rankAccuracy = 0;


    public TestResult() {
        //init values array
        for (int i = 0; i < contPreProc.length; i++) {
            contPreProc[i] = 0;
        }
    }


}
