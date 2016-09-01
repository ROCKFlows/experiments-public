package fr.unice.i3s.rockflows.experiments.significance;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import java.util.Arrays;
import java.util.List;
import jdistlib.disttest.NormalityTest;

public class Significance {

    public static boolean isSignificantDifferent(double[] array1, double[] array2, double alpha)
            throws Exception {

        //sort the arrays
        Arrays.sort(array1);
        Arrays.sort(array2);
        
        if (arrayEquals(array1, array2)) {
            return false;
        }

        //test if all the arrays of accuracy follow a normal distribution        
        boolean normal1 = shapiroWilkNormalityTest(array1, alpha);
        boolean normal2 = shapiroWilkNormalityTest(array2, alpha);

        if (normal1 && normal2) {
            TTest ttest = new TTest();
            //Returns true iff the null hypothesis can be rejected with confidence 1 - alpha.
            //null hypothesis = means are equal, the difference of the 2 means is 0
            return ttest.pairedTTest(array1, array2, alpha);
        } else {
            return wilcoxonSignedTest(array1, array2, alpha);
        }
    }

    public static boolean arrayEquals(double[] array1, double[] array2) {
        int num = array1.length;
        for (int iii = 0; iii < num; iii++) {
            if (array1[iii] != array2[iii]) {
                return false;
            }
        }
        return true;
    }

    //N.B: require sorted array
    public static boolean shapiroWilkNormalityTest(double[] array, double alpha){
    
        //if the array contains only one distinct value, it is normal
        double first = array[0];
        boolean stop = true;
        for(int i = 1; i < array.length; i++){
            if(array[i] != first){
                stop = false;
                break;
            }
        }
        if(stop){
            return true;
        }
        
        double www = NormalityTest.shapiro_wilk_statistic(array);
        double pvalue = NormalityTest.shapiro_wilk_pvalue(www, array.length);
        if(pvalue < alpha){
            //null hypothesis is rejected with confidence 1-alpha
            //so the array is not normal
            return false; 
        }
        //we cannot reject the null hypothesis, so we say it follows a normal distribution
        //so the array is normal
        return true; 
    }  

    /**
     * Null hypothesis = the means of the 2 arrays are equal Check the null hypothesis that the 2 arrays passed as input
     * are significant different, via the wilcoxon Signed Test algorithm.
     *
     * @param acc1 the first array to test
     * @param acc2 the second aray to test
     * @param alpha the level of confidence of the test. Between 0 and 0.5
     * @return {@code true} if the array are significant different (null hypothesis rejected) {@code false} otherwise
     */
    public static boolean wilcoxonSignedTest(double[] acc1, double[] acc2, double alpha) {
        WilcoxonSignedRankTest wsrt = new WilcoxonSignedRankTest();
        //preconditions: the 2 arrays have to be ordered
        double[] copy1 = Arrays.copyOf(acc1, acc1.length);
        double[] copy2 = Arrays.copyOf(acc2, acc2.length);
        Arrays.sort(copy1);
        Arrays.sort(copy2);
        double pvalue = wsrt.wilcoxonSignedRankTest(copy1, copy2, false); //not exact pvalue        
        /*//check pvalue, if to reject the null hypothesis or not
A small p-value (≤ 0.05) indicates strong evidence against the null hypothesis, so it is rejected.
A large p-value (> 0.05) indicates weak evidence against the null hypothesis (fail to reject).
p-values very close to the cutoff (~ 0.05) are considered to be marginal (need attention).                
         */
        if (pvalue < alpha) {
            return true; //null hypothesis rejected
        } else {
            return false; //null hypothesis accepted
        }
    }

    /**
     * Null hypothesis = the means of the input arrays are equal Check the null hypothesis that the input arrays passed
     * as input are significant different. The wilcoxon test is done between any possible pair of arrays, if at least
     * one of them is significant different, the arrays are significant different.
     *
     * @param accs list of arrays to be tested
     * @param alpha the level of confidence of the test. Between 0 and 0.5
     * @return {@code true} if the array are significant different (null hypothesis rejected) {@code false} otherwise
     */
    public static boolean friedmanTest(List<double[]> accs, double alpha) {
        int count = accs.size();
        for (int iii = 0; iii < count; iii++) {
            for (int jjj = iii + 1; jjj < count; jjj++) {
                //compare accuracies i and j with Wilcoxon
                boolean wt = wilcoxonSignedTest(accs.get(iii), accs.get(jjj), alpha);
                if (wt) {
                    //if at least one comparison is significative different, 
                    //the null hypothesis is rejected
                    return true;
                }
            }
        }
        return false;
    }
}
