package fr.unice.i3s.rockflows.experiments.datamining;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class TestResult {

    //n:m InfoClassifier - Dataset
    public Dataset dataset;
    public InfoClassifier infoclassifier;
    public boolean alreadyTested = false; //default    
    public double accuracyAvg = 0;
    public long trainingTimeAvg = Long.MAX_VALUE; //milliseconds
    public long testTimeAvg = Long.MAX_VALUE; //milliseconds
    public long modelSizeAvg = Long.MAX_VALUE;
    public double accuracyAvg10 = 0;
    public long trainingTimeAvg10 = Long.MAX_VALUE; //milliseconds
    public long testTimeAvg10 = Long.MAX_VALUE; //milliseconds
    public long modelSizeAvg10 = Long.MAX_VALUE;    
    public double[] accuracy10f;
    public double[] trainingTime10f; //seconds
    public double[] testTime10f; //seconds
    public double[] modelSize10f;
    public double[] accuracy4f;
    public double[] trainingTime4f; //seconds
    public double[] testTime4f; //seconds
    public double[] modelSize4f;
    

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
        return getTimeText(this.dataset.preprocessingTime);
    }


}
