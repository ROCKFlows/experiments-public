/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.datamining;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lupin
 */
public class ResWorkflow {

    public String classifierName = "";
    public int preProcId = -1;
    public List<Double> rankAccuracy = new ArrayList<>();
    public List<Double> accuracy = new ArrayList<>();
    public List<Double> time = new ArrayList<>();
    public List<Double> ram = new ArrayList<>();
    //# times it is compatible with the pattern...
    public double avgAccuracy = 0;
    public double avgAccRank = 0;
    public double avgTime = 0;
    public double avgRAM = 0;
    public double stDevAccuracy = 0;
    public double stDevAccRank = 0;
    public int numBestRank = 0;
    public double percCompatible = 0;
    public boolean compatible = true;

}
