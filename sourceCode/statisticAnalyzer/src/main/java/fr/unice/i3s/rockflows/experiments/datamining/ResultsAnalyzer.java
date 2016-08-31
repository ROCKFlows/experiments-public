package fr.unice.i3s.rockflows.experiments.datamining;

import fr.unice.i3s.rockflows.experiments.TestResult;
import fr.unice.i3s.rockflows.statistics.Significance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lupin
 */
public class ResultsAnalyzer {

    public static List<TestResult> getResultsPerClassifier(List<TestResult> results, int classifierId) {
        List<TestResult> output = new ArrayList<>();
        for (TestResult res : results) {
            if (res.infoclassifier.id == classifierId) {
                if (res.infoclassifier.properties.compatibleWithDataset) {
                    output.add(res);
                }
            }
        }
        return output;
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
        for (LocalResult result : results) {
            result.rank = 1;
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
                        if (Significance.isSignificantDifferent(first.array, current.array, alpha, true)) {
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
    public static void setRankAccuracy(List<TestResult> results, double alpha)
            throws Exception {

        //init list of local results
        List<LocalResult> res = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            TestResult currentTR = results.get(i);
            LocalResult currentLR;
            currentLR = new LocalResult(currentTR.accuracyAvg, currentTR.accuracies, i);
            res.add(currentLR);
        }

        setRank(res, alpha, true);

        //save rank results into the test result list
        for (int i = 0; i < results.size(); i++) {
            LocalResult currentLR = res.get(i);
            TestResult currentTR = results.get(currentLR.indexTestResult);
            currentTR.rankAccuracy = currentLR.rank;
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

}
