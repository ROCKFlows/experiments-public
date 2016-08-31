package fr.unice.i3s.rockflows.experiments;

import fr.unice.i3s.rockflows.datamining.Dataset;

public class TestResult {

    //n:m InfoClassifier - Dataset
    public Dataset dataset;
    public InfoClassifier infoclassifier;

    // TODO cleanup
    public String preProcProp = "XXX";
    public String dataProp = "YYY";

    public boolean alreadyTested = false;
    public double accuracyAvg = 0;
    public long trainingTimeAvg = Long.MAX_VALUE; //milliseconds
    public long testTimeAvg = Long.MAX_VALUE; //milliseconds
    public long modelSizeAvg = Long.MAX_VALUE;
    public double accuracyAvg10 = 0;
    public long trainingTimeAvg10 = Long.MAX_VALUE; //milliseconds
    public long testTimeAvg10 = Long.MAX_VALUE; //milliseconds
    public long modelSizeAvg10 = Long.MAX_VALUE;
    public long totalTimeAvg;
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
    //results of specific test
    public double accuracyTest;
    public double trainingTimeTest; //seconds
    public double testTimeTest; //seconds
    public double modelSizeTest;
    //ranking
    public int rankAccuracy = 0;
    public int rankTrainingTime;
    public int rankTotalTime;
    public int rankSize;


    public double accuracyStDev = 0;
    public double[] contPreProc = new double[13];

    public double[] accuracies;
    public double[] times;
    public double[] rams;


    //status
    public Status statusAccuracy = Status.Bad;
    public Status statusTrainingTime = Status.Bad;
    public Status statusTotalTime = Status.Bad;
    public Status statusSize = Status.Bad;
    public double sumTime;


    public TestResult(Dataset data, InfoClassifier ic) {
        this.dataset = data;
        this.infoclassifier = ic;
    }

    private String getTimeText(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long remainder = seconds - minutes * 60;
        return minutes + " minutes " + remainder + " seconds";
    }

    /**
     * Get the training time as a string in the format: x minutes, y seconds.
     *
     * @return the string
     */
    public String getTrainingTimeText() {
        return getTimeText(this.trainingTimeAvg);
    }

    /**
     * Get the test time as a string in the format: x minutes, y seconds.
     *
     * @return the string
     */
    public String getTestTimeText() {
        return getTimeText(this.testTimeAvg);
    }

    public String getPreprocessingTimeText() {
        return getTimeText(this.dataset.preprocessingTime);
    }


}
