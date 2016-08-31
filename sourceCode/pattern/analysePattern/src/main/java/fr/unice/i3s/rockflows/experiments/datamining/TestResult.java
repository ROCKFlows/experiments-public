package fr.unice.i3s.rockflows.experiments.datamining;

public class TestResult {

    public String algoName = "";
    public int algoId = -1;
    public int preProcId = -1;
    public long preProcTime = 0;
    public boolean compatible = false;
    public String preProcProp = "XXX";
    public String dataProp = "YYY";

    public double accuracyAvg = 0;
    public long trainingTimeAvg = Long.MAX_VALUE; //milliseconds
    public long testTimeAvg = Long.MAX_VALUE; //milliseconds
    public long sumTime = Long.MAX_VALUE; //milliseconds
    public long modelSizeAvg = Long.MAX_VALUE;
    public double accuracyAvg10 = 0;
    public long trainingTimeAvg10 = Long.MAX_VALUE; //milliseconds
    public long testTimeAvg10 = Long.MAX_VALUE; //milliseconds
    public long modelSizeAvg10 = Long.MAX_VALUE;
    public int rankAccuracy = 0; //1 = best rank  
    public int rankTrainingTime = 0; //1 = best rank  
    public int rankTotalTime = 0; //1 = best rank  
    public int rankSize = 0; //1 = best rank    
    public double[] accuracy10f;
    public double[] trainingTime10f; //seconds
    public double[] testTime10f; //seconds
    public double[] totalTime10f; //seconds
    public double[] modelSize10f;
    public double[] accuracy4f;
    public double[] trainingTime4f; //seconds
    public double[] testTime4f; //seconds
    public double[] totalTime4f; //seconds
    public double[] modelSize4f;
    public Status statusAccuracy = Status.Bad;
    public Status statusTrainingTime = Status.Bad;
    public Status statusTotalTime = Status.Bad;
    public Status statusSize = Status.Bad;


    public TestResult() {
    }

    private String getTimeText(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long remainder = seconds - minutes * 60;
        return minutes + " minutes " + remainder + " seconds";
    }

    /**
     * Get the training time as a string in the format: x minutes, y seconds
     *
     * @return the string
     */
    public String getTrainingTimeText() {
        return getTimeText(this.trainingTimeAvg);
    }

    /**
     * Get the test time as a string in the format: x minutes, y seconds
     *
     * @return the string
     */
    public String getTestTimeText() {
        return getTimeText(this.testTimeAvg);
    }

    public String getPreprocessingTimeText() {
        return getTimeText(this.preProcTime);
    }


}
