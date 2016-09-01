/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.datamining;

/**
 *
 * @author lupin
 */
public class LocalResult {
    
    public double avg = -1;
    public double[] array;
    public int rank;
    public int indexTestResult = -1;
    public Status status = Status.Bad;
    
    public LocalResult(double avg, double[] array, int index){
        this.array = array;
        this.avg = avg;
        this.indexTestResult = index;
    }
}
