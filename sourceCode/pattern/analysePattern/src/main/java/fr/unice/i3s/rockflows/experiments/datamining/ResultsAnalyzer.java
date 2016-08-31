/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.datamining;

import fr.unice.i3s.rockflows.experiments.significance.Significance;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author lupin
 */
public class ResultsAnalyzer {

    public static List<TestResult> getResultsPerClassifier(List<TestResult> results, int classifierId) {
        List<TestResult> output = new ArrayList<>();
        for (TestResult res : results) {
            if (res.algoId == classifierId) {
                if (res.compatible) {
                    output.add(res);
                }
            }
        }
        return output;
    }

    public static List<TestResult> removeDuplicates(List<TestResult> results) {
        TreeSet<TestResult> ts = new TreeSet<>((TestResult r1, TestResult r2) -> {
            if (r1.accuracyAvg == r2.accuracyAvg && r1.trainingTimeAvg == r2.trainingTimeAvg
                    && r1.testTimeAvg == r2.testTimeAvg) {
                return 0;
            }
            return 1;
        });

        ts.addAll(results);
        results.clear();
        results.addAll(ts);
        return results;
    }

    public static void initRank(List<LocalResult> results, boolean invert) throws Exception {

        //sort results according to the average
        if (invert) {
            //sort the list according to the accuracies in decreasing order
            results.sort((LocalResult tr1, LocalResult tr2) -> {
                return Double.compare(tr2.avg, tr1.avg);
            });
        } else {
            //sort the list according to the accuracies in decreasing order
            results.sort((LocalResult tr1, LocalResult tr2) -> {
                return Double.compare(tr1.avg, tr2.avg);
            });
        }

        //set rank = 1 to each result
        int size = results.size();
        for (int i = 0; i < size; i++) {
            results.get(i).rank = 1;
        }

    }

    public static void setRank(List<LocalResult> results, double alpha, boolean invert) throws Exception {

        initRank(results, invert);

        int resultSize = results.size();
        int maxRank = 1;
        boolean stop = false;
        while (!stop) {
            stop = true;
            for (int i = 1; i <= maxRank; i++) {
                //List<LocalResult> toAnalyse = getOrderedByAvg(i, results, invert);
                //List<LocalResult> toAnalyse = getResultsByRank(i, results);                
                List<Integer> toAnalyse = getIndicesByRank(i, results);
                int size = toAnalyse.size();
                for (int f = 0; f < size; f++) {
                    LocalResult first = results.get(toAnalyse.get(f));
                    for (int j = f + 1; j < size; j++) {
                        //check statistic different of each element with the first one
                        //by starting from the uppest one
                        int currentIndex = toAnalyse.get(j);
                        LocalResult current = results.get(currentIndex);
                        //if the average is the same, they have the same rank
                        if (current.avg == first.avg) {
                            continue; //check the next one
                        }
                        //if the avg is different, it is checked if the difference is significant
                        if (Significance.isSignificantDifferent(first.array, current.array, alpha)) {
                            //if significant different, increase of 1 the rank from the current element
                            //until the last one
                            current.rank++;
                            for (int index = currentIndex + 1; index < resultSize; index++) {
                                results.get(index).rank++;
                            }
                            //check if the results before the current have the same average of the current one
                            //in this case, since the results that have the same avg have also the same rank,
                            //we modify the rank of the previous ones
                            for (int index = currentIndex - 1; index >= 0; index--) {
                                if (results.get(index).avg == current.avg) {
                                    results.get(index).rank++;
                                }
                            }

                            stop = false;
                            break;
                        }
                    }
                    if (!stop) {
                        //increment rank for the elements coming next
                        maxRank++;
                        break;
                    }
                }
            }
        }
    }

    public static void initRankv2(List<LocalResult> results, double alpha,
                                  boolean invert) throws Exception {

        if (invert) {
            //sort the list according to the accuracies in decreasing order
            results.sort((LocalResult tr1, LocalResult tr2) -> {
                return Double.compare(tr2.avg, tr1.avg);
            });
        } else {
            //sort the list according to the accuracies in decreasing order
            results.sort((LocalResult tr1, LocalResult tr2) -> {
                return Double.compare(tr1.avg, tr2.avg);
            });
        }

        //the best accuracy has rank = 1
        LocalResult previous = results.get(0);
        previous.rank = 1;

        //check significant difference between the arrays of 10 folds pre-calculated
        List<Integer> toJump = new ArrayList<>();
        int num = results.size();
        for (int iii = 1; iii < num; iii++) {
            if (isToJump(iii, toJump)) {
                continue;
            }
            LocalResult current = results.get(iii);
            //check if the difference between the previous and the current is statistically different
            boolean different = Significance.isSignificantDifferent(previous.array,
                    current.array, alpha);

            if (different && (current.avg == previous.avg)) {
                //check if later there are other results with the same average
                int diff = num - iii;
                List<LocalResult> later = new ArrayList<>();
                for (int j = iii + 1; j < diff; j++) {
                    LocalResult toCheck = results.get(j);
                    if (toCheck.avg != current.avg) {
                        break;
                    }
                    later.add(toCheck);
                }
                //check if in the list later, the results are significant different 
                //wrt to the "previous" result.
                for (int j = 0; j < later.size(); j++) {
                    LocalResult toCheck = later.get(j);
                    boolean dif = Significance.isSignificantDifferent(previous.array,
                            toCheck.array, alpha);
                    if (!dif) {
                        //this results has to have the same rank of "previous"
                        toCheck.rank = previous.rank;
                        toJump.add(iii + 1 + j);
                    }
                }
            }
            if (different) {
                current.rank = previous.rank + 1;
            } else {
                current.rank = previous.rank;
            }
            previous = current;
        }
    }

    public static void lowerRank(List<LocalResult> results, double alpha, boolean invert) throws Exception {

        int maxRank = getmaxRank(results);
        boolean stop = false;
        while (!stop) {
            stop = true;
            for (int i = 1; i <= maxRank; i++) {
                List<LocalResult> toAnalyse = getOrderedByAvg(i, results, invert);
                int size = toAnalyse.size();
                if (size > 2) {
                    LocalResult last = toAnalyse.get(size - 1);
                    for (int j = size - 2; j >= 0; j--) {
                        //check statistic different of each element with the last one
                        //by starting from the lowest one
                        LocalResult current = toAnalyse.get(j);
                        if (Significance.isSignificantDifferent(last.array, current.array, alpha)) {
                            //if these are different, the maxRank increase of 1
                            //so also the others increase
                            for (int k = maxRank; k > i; k--) {
                                List<LocalResult> toIncrement = getResultsByRank(k, results);
                                for (int p = 0; p < toIncrement.size(); p++) {
                                    toIncrement.get(p).rank++;
                                }
                            }
                            //set a rank higher for all elements, from the current+1 one until the last one
                            for (int k = j + 1; k < size; k++) {
                                //toAnalyse.get(k).rank = last.rank + 1;
                                toAnalyse.get(k).rank++;
                            }
                            stop = false;
                            break;
                        }
                    }
                    if (!stop) {
                        //increment rank for the elements coming next
                        maxRank++;
                        break;
                    }
                }
            }
        }
    }

    public static void upperRankv2(List<LocalResult> results, double alpha, boolean invert) throws Exception {
        int maxRank = getmaxRank(results);
        boolean stop = false;
        while (!stop) {
            stop = true;
            for (int i = 1; i <= maxRank; i++) {
                List<LocalResult> toAnalyse = getOrderedByAvg(i, results, invert);
                LocalResult first = toAnalyse.get(0);
                int size = toAnalyse.size();
                if (size > 2) {
                    for (int j = 1; j < size; j++) {
                        //check statistic different of each element with the last one
                        //by starting from the lowest one
                        LocalResult current = toAnalyse.get(j);
                        if (Significance.isSignificantDifferent(first.array, current.array, alpha)) {
                            for (int k = maxRank; k > i; k--) {
                                List<LocalResult> toIncrement = getResultsByRank(k, results);
                                for (int p = 0; p < toIncrement.size(); p++) {
                                    toIncrement.get(p).rank++;
                                }
                            }
                            //set a rank higher for all elements, from the current one until the last one
                            current.rank = first.rank + 1;
                            for (int k = j + 1; k < size; k++) {
                                //toAnalyse.get(k).rank = first.rank + 1;
                                toAnalyse.get(k).rank++;
                            }
                            stop = false;
                            break;
                        }
                    }
                    if (!stop) {
                        //increment rank for the elements coming next
                        maxRank++;
                        break;
                    }
                }
            }
        }
    }

    public static List<LocalResult> getResultsByRank(int rank, List<LocalResult> res) {
        List<LocalResult> out = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            LocalResult loc = res.get(i);
            if (loc.rank == rank) {
                out.add(loc);
            }
        }
        return out;
    }

    public static List<Integer> getIndicesByRank(int rank, List<LocalResult> res) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            LocalResult loc = res.get(i);
            if (loc.rank == rank) {
                out.add(i);
            }
        }
        return out;
    }

    public static int getmaxRank(List<LocalResult> res) {
        int max = 0;
        for (int i = 0; i < res.size(); i++) {
            int current = res.get(i).rank;
            if (current > max) {
                max = current;
            }
        }
        return max;
    }
    
    /*
    public static void setRank(List<LocalResult> results, double alpha,
            boolean invert) throws Exception {
        
        initRank(results, alpha, invert);
        lowerRank(results, alpha,invert);
        upperRank(results, alpha, invert);
        
    }
    */

    //statistic test, find cluster of algorithms
    public static void setRankAccuracy(List<TestResult> results, double alpha, boolean folds4)
            throws Exception {

        //init list of local results
        List<LocalResult> res = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            TestResult currentTR = results.get(i);
            LocalResult currentLR;
            if (folds4) {
                currentLR = new LocalResult(currentTR.accuracyAvg,
                        currentTR.accuracy4f, i);
            } else {
                currentLR = new LocalResult(currentTR.accuracyAvg10,
                        currentTR.accuracy10f, i);
            }
            res.add(currentLR);
        }

        setRank(res, alpha, true);
        //setStatus(res, true, 0.9, 0.8);        
        setStatusDemo(res, true, 0.9, 0.8);

        //save rank results into the test result list
        for (int i = 0; i < results.size(); i++) {
            LocalResult currentLR = res.get(i);
            TestResult currentTR = results.get(currentLR.indexTestResult);
            currentTR.rankAccuracy = currentLR.rank;
            currentTR.statusAccuracy = currentLR.status;
        }
    }

    //statistic test, find cluster of algorithms
    public static void setRankTotalTime(List<TestResult> results, double alpha, boolean folds4)
            throws Exception {

        //sum time
        if (folds4) {
            results.stream().forEach((TestResult tr) -> {
                tr.sumTime = tr.trainingTimeAvg + tr.testTimeAvg + tr.preProcTime;
                for (int i = 0; i < 4; i++) {
                    tr.totalTime4f[i] = tr.preProcTime + tr.trainingTime4f[i] + tr.testTime4f[i];
                }
            });
        } else {
            results.stream().forEach((TestResult tr) -> {
                tr.sumTime = tr.trainingTimeAvg10 + tr.testTimeAvg10 + tr.preProcTime;
                for (int i = 0; i < 10; i++) {
                    tr.totalTime10f[i] = tr.preProcTime + tr.trainingTime10f[i] + tr.testTime10f[i];
                }
            });
        }

        //init list of local results
        List<LocalResult> res = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            TestResult currentTR = results.get(i);
            LocalResult currentLR;
            if (folds4) {
                currentLR = new LocalResult(currentTR.sumTime,
                        currentTR.totalTime4f, i);
            } else {
                currentLR = new LocalResult(currentTR.sumTime,
                        currentTR.totalTime10f, i);
            }
            res.add(currentLR);
        }

        setRank(res, alpha, false);
        //setStatus(res, false, 60000, 600000);
        setStatusDemo(res, false, 60000, 600000);

        //save rank results into the test result list
        for (int i = 0; i < results.size(); i++) {
            LocalResult currentLR = res.get(i);
            TestResult currentTR = results.get(currentLR.indexTestResult);
            currentTR.rankTotalTime = currentLR.rank;
            currentTR.statusTotalTime = currentLR.status;
        }
    }

    //statistic test, find cluster of algorithms
    public static void setRankTrainTime(List<TestResult> results, double alpha, boolean folds4)
            throws Exception {

        //init list of local results
        List<LocalResult> res = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            TestResult currentTR = results.get(i);
            LocalResult currentLR;
            if (folds4) {
                currentLR = new LocalResult(currentTR.trainingTimeAvg,
                        currentTR.trainingTime4f, i);
            } else {
                currentLR = new LocalResult(currentTR.trainingTimeAvg10,
                        currentTR.trainingTime10f, i);
            }
            res.add(currentLR);
        }

        setRank(res, alpha, false);
        //setStatus(res, false, 60000, 600000);
        setStatusDemo(res, false, 60000, 600000);

        //save rank results into the test result list
        for (int i = 0; i < results.size(); i++) {
            LocalResult currentLR = res.get(i);
            TestResult currentTR = results.get(currentLR.indexTestResult);
            currentTR.rankTrainingTime = currentLR.rank;
            currentTR.statusTrainingTime = currentLR.status;
        }
    }

    //statistic test, find cluster of algorithms
    public static void setRankModelSize(List<TestResult> results, double alpha, boolean folds4)
            throws Exception {

        //init list of local results
        List<LocalResult> res = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            TestResult currentTR = results.get(i);
            LocalResult currentLR;
            if (folds4) {
                currentLR = new LocalResult(currentTR.modelSizeAvg,
                        currentTR.modelSize4f, i);
            } else {
                currentLR = new LocalResult(currentTR.modelSizeAvg10,
                        currentTR.modelSize10f, i);
            }
            res.add(currentLR);
        }

        setRank(res, alpha, false);
        //setStatus(res, false, 10000000, 50000000);
        setStatusDemo(res, false, 10000000, 50000000);

        //save rank results into the test result list
        for (int i = 0; i < results.size(); i++) {
            LocalResult currentLR = res.get(i);
            TestResult currentTR = results.get(currentLR.indexTestResult);
            currentTR.rankSize = currentLR.rank;
            currentTR.statusSize = currentLR.status;
        }
    }

    public static List<LocalResult> getOrderedByAvg(int rank, List<LocalResult> res, boolean invert) {

        List<LocalResult> out = getResultsByRank(rank, res);
        if (invert) {
            //sort results according to the accuracy
            out.sort((LocalResult tr1, LocalResult tr2) -> {
                return Double.compare(tr2.avg, tr1.avg);
            });
        } else {
            //sort results according to the accuracy
            out.sort((LocalResult tr1, LocalResult tr2) -> {
                return Double.compare(tr1.avg, tr2.avg);
            });
        }

        return out;
    }

    public static boolean isToJump(int index, List<Integer> indices) {

        if (indices.contains(index)) {
            return true;
        }
        return false;
    }

    public static void setStatusDemo(List<LocalResult> results, boolean invert,
                                     double limitVeryGood, double limitGood) {

        int size = results.size();
        for (int i = 0; i < size; i++) {
            LocalResult res = results.get(i);
            if (res.rank <= 2) {
                res.status = Status.VeryGood;
                continue;
            }
            if (res.rank <= 5) {
                res.status = Status.Good;
                continue;
            }
            res.status = Status.Bad;
            continue;
        }

    }

    public static void setStatus(List<LocalResult> results, boolean invert,
                                 double limitVeryGood, double limitGood) {

        //read max rank
        int maxRank = getmaxRank(results);
        List<LocalResult> ordered = new ArrayList<>();
        for (int i = 1; i <= maxRank; i++) {
            ordered.addAll(getOrderedByAvg(i, results, invert));
        }
        //if accuracy < 0.9, only rank = 1 is VeryGood
        ordered.get(0).status = Status.VeryGood;
        //find rank of first accuracy < 0.9
        int rank1 = maxRank;
        for (int index = 1; index < ordered.size(); index++) {
            LocalResult current = ordered.get(index);
            if (invert) {
                if (current.avg < limitVeryGood) {
                    rank1 = current.rank;
                    break;
                }
            } else {
                if (current.avg > limitVeryGood) {
                    rank1 = current.rank;
                    break;
                }
            }
        }
        if (rank1 == maxRank) {
            //all the results are VeryGood
            for (int index = 1; index < ordered.size(); index++) {
                ordered.get(index).status = Status.VeryGood;
            }
            return;
        }
        //first index of Good result
        int firstGood = 0;
        //set status VeryGood from rankAccuracy = 1 to rank
        for (int index = 1; index < ordered.size(); index++) {
            LocalResult current = ordered.get(index);
            if (current.rank <= rank1) {
                current.status = Status.VeryGood;
            } else {
                firstGood = index;
                current.status = Status.Good;
                break;
            }
        }
        //find rank of first accuracy < 0.8
        int rank2 = maxRank;
        for (int index = firstGood + 1; index < ordered.size(); index++) {
            LocalResult current = ordered.get(index);
            if (invert) {
                if (current.avg < limitGood) {
                    rank2 = current.rank;
                    break;
                }
            } else {
                if (current.avg > limitGood) {
                    rank2 = current.rank;
                    break;
                }
            }
        }
        if (rank2 == maxRank) {
            //the Good is the last result
            for (int index = firstGood + 1; index < ordered.size(); index++) {
                ordered.get(index).status = Status.Good;
            }
            return;
        }
        //first bad index
        int firstBad = ordered.size();
        //set status Good from first rank Good to rank
        for (int index = firstGood + 1; index < ordered.size(); index++) {
            LocalResult current = ordered.get(index);
            if (current.rank <= rank2) {
                current.status = Status.Good;
            } else {
                firstBad = index;
                current.status = Status.Bad;
                break;
            }
        }
        //set the others to bad
        for (int index = firstBad + 1; index < ordered.size(); index++) {
            ordered.get(index).status = Status.Bad;
        }
    }


}
