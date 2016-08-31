package fr.unice.i3s.rockflows.experiments.datamining;

import fr.unice.i3s.rockflows.experiments.Status;

/**
 * @author lupin
 */
public class LocalResult {

    public double avg = -1;
    public double[] array;
    public int rank;
    public int indexTestResult = -1;
    public Status status = Status.Bad;

    public LocalResult(double avg, double[] array, int index) {
        this.array = array;
        this.avg = avg;
        this.indexTestResult = index;
    }
}
