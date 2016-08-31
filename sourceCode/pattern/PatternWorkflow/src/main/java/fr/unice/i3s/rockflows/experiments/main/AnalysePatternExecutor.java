/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.experiments.automatictest.AnalyseExcelFile;
import fr.unice.i3s.rockflows.experiments.automatictest.PatternStatisticExc;
import fr.unice.i3s.rockflows.experiments.datamining.ResWorkflow;
import fr.unice.i3s.rockflows.statistics.StatisticsUtils;
import fr.unice.i3s.rockflows.tools.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author lupin
 */
public class AnalysePatternExecutor implements Callable<Boolean> {

    private String dir = "";
    private List<String> names;

    public AnalysePatternExecutor(String patternDirectory, List<String> names) throws Exception {
        this.dir = patternDirectory;
        this.names = names;
    }

    public void executeTest() throws Exception {
        //collect results for each dataset (Test-0.arff, Test-1.arff, ...) of each database
        //(adult, abalone, ...)

        exec4Folds();
        exec10Folds();
    }

    public void exec4Folds() throws Exception {

        List<ResWorkflow> workflows = inputWorkflows(names);

        //get all folders of this pattern
        List<String> folders = FileUtils.getListDirectories(dir);

        //for each folder, read analysis of each dataset that is present
        int num = folders.size();
        //if pattern is empty, that is, it doesn't contain any result:
        if (num == 0) {
            return;
        }
        for (String folder : folders) {
            String pathFolder = dir + "/" + folder;
            String analysis = pathFolder + "/" + "Final-Analysis-4Folds.xlsx";
            AnalyseExcelFile exc = new AnalyseExcelFile(analysis);
            exc.readValues(workflows);
        }

        //compute avg, st dev      
        computeAvgStd(workflows);

        //and write file analysis for the entire pattern
        PatternStatisticExc exc = new PatternStatisticExc(dir + "/analysis-4Folds.xlsx");

        //compute percentage of compatibility
        this.computePercCompatibility(workflows, num);

        List<ResWorkflow> ordered = this.sortCompatibility(workflows);
        exc.writeValues(ordered, num);
    }

    public void exec10Folds() throws Exception {

        List<ResWorkflow> workflows = inputWorkflows(names);

        //get all folders of this pattern
        List<String> folders = FileUtils.getListDirectories(dir);

        //for each folder, read analysis of each dataset that is present
        int num = folders.size();
        //if pattern is empty, that is, it doesn't contain any result:
        if (num == 0) {
            return;
        }
        for (String folder : folders) {
            String pathFolder = dir + "/" + folder;
            String analysis = pathFolder + "/" + "Final-Analysis-10Folds.xlsx";
            AnalyseExcelFile exc = new AnalyseExcelFile(analysis);
            exc.readValues(workflows);
        }

        //compute avg, st dev      
        computeAvgStd(workflows);

        //and write file analysis for the entire pattern
        PatternStatisticExc exc = new PatternStatisticExc(dir + "/analysis-10Folds.xlsx");
        //sort classifiers, the first one is the best one for this pattern:

        //compute percentage of compatibility
        this.computePercCompatibility(workflows, num);

        List<ResWorkflow> ordered = this.sortCompatibility(workflows);
        exc.writeValues(ordered, num);
    }

    public List<ResWorkflow> sortCompatibility(List<ResWorkflow> res) {
        //get workflows with compatibility < 100%
        List<ResWorkflow> notFull = res.stream()
                .filter((ResWorkflow work) -> {
                    return work.percCompatible < 1;
                })
                .collect(Collectors.toList());

        //get workflows with compatibility = 100%
        List<ResWorkflow> full = res.stream()
                .filter((ResWorkflow work) -> {
                    return work.percCompatible == 1;
                })
                .collect(Collectors.toList());

        List<ResWorkflow> out = new ArrayList<>();
        out.addAll(sortResults(full));
        out.addAll(sortResults(notFull));

        return out;
    }

    public void computePercCompatibility(List<ResWorkflow> values, double cont) {
        for (ResWorkflow res : values) {
            res.percCompatible = (double) res.accuracy.size() / cont;
        }
    }

    public List<ResWorkflow> sortResults(List<ResWorkflow> workflows) {
        //first put only compatible in another list
        //in the end of the sorted list, add the not compatible algorithm
        List<ResWorkflow> notCompatible = workflows.stream()
                .filter((ResWorkflow res) -> res.accuracy.isEmpty())
                .collect(Collectors.toList());
        //set not compatible value
        notCompatible.forEach((ResWorkflow res) -> res.compatible = false);

        List<ResWorkflow> compatible = workflows.stream()
                .filter((ResWorkflow res) -> !res.accuracy.isEmpty())
                .collect(Collectors.toList());

        List<ResWorkflow> output = new ArrayList<>();

        //sort avg rank asc
        //get list of distinct values of avg Rank
        List<Double> distinct = removeDuplicates(compatible);
        //sort avgRank asc
        distinct.sort(Double::compare);

        for (Double currentAvgAccRank : distinct) {
            //get results s.t. avgAccRank == current rankAcc
            List<ResWorkflow> tmp = getResultsNumBest(compatible, currentAvgAccRank);
            //sort list according to rankAccAvg
            tmp.sort((ResWorkflow r1, ResWorkflow r2) ->
                    Double.compare(r2.avgAccuracy, r1.avgAccuracy));
            output.addAll(tmp);
        }

        //in the end add not compatible results
        output.addAll(notCompatible);
        return output;
    }

    public List<ResWorkflow> getResultsNumBest(List<ResWorkflow> values, double value) {
        List<ResWorkflow> out = new ArrayList<>();
        for (ResWorkflow current : values) {
            if (current.avgAccRank == value) {
                out.add(current);
            }
        }
        return out;
    }

    //avgAccRank
    public List<Double> removeDuplicates(List<ResWorkflow> values) {
        List<Double> distinct = new ArrayList<>();
        for (ResWorkflow current : values) {
            if (!distinct.contains(current.avgAccRank)) {
                distinct.add(current.avgAccRank);
            }
        }
        return distinct;
    }

    public void computeAvgStd(List<ResWorkflow> workflows) throws Exception {
        for (ResWorkflow res : workflows) {
            //compute avg accuracy
            int size = res.accuracy.size();
            if (size == 0) {
                res.avgAccuracy = 0;
                res.avgAccRank = 0;
                res.avgRAM = 0;
                res.avgTime = 0;
            } else {
                double s1 = 0;
                double s2 = 0;
                double s3 = 0;
                double s4 = 0;
                for (int k = 0; k < size; k++) {
                    s1 += res.accuracy.get(k);
                    double rank = res.rankAccuracy.get(k);
                    s2 += rank;
                    if (rank == 1) {
                        res.numBestRank++;
                    }
                    s3 += res.time.get(k);
                    s4 += res.ram.get(k);
                }
                res.avgAccuracy = s1 / size;
                res.avgAccRank = s2 / size;
                res.avgTime = s3 / size;
                res.avgRAM = s4 / size;
            }
            //compute standard deviation:
            computeStDev(res);
        }
    }

    public void computeStDev(ResWorkflow res) {
        res.stDevAccuracy = StatisticsUtils.getStdDev(res.accuracy);
        res.stDevAccRank = StatisticsUtils.getStdDev(res.rankAccuracy);
    }

    @Override
    public Boolean call() throws Exception {
        try {
            this.executeTest();
        } catch (Exception ex) {
            File fff = new File(dir + "error");
            Writer www = new FileWriter(fff);
            www.append(" :BEGIN, ");
            ex.printStackTrace(new PrintWriter(www));
            www.append(",:END ");
            www.append(ex.getMessage());
            www.flush();
            www.close();
            return false;
        }
        return true;
    }

    public List<ResWorkflow> inputWorkflows(List<String> names) {

        List<ResWorkflow> workflows = new ArrayList<>();
        int num = names.size();
        for (int i = 0; i < num; i++) {
            for (int p = 0; p <= 12; p++) {
                ResWorkflow cls = new ResWorkflow();
                cls.classifierName = names.get(i);
                cls.preProcId = p;
                cls.accuracy = new ArrayList<>();
                cls.rankAccuracy = new ArrayList<>();
                workflows.add(cls);
            }
        }
        return workflows;
    }

}
