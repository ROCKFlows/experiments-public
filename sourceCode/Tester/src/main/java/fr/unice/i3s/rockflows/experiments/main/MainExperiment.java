package fr.unice.i3s.rockflows.experiments.main;

import fr.unice.i3s.rockflows.tools.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Luca
 */
public class MainExperiment {

    public static void main(String[] args) throws Exception {

        String pathFolder = "";
        // default value
        int nthread = 2;
        boolean parallel = false;

        int numParameters = args.length;
        for (int iii = 0; iii < numParameters; iii++) {
            switch (args[iii]) {
                case "-pef": {
                    pathFolder = args[++iii];
                    break;
                }
                case "-nthread": {
                    nthread = Integer.parseInt(args[++iii]);
                    parallel = true;
                    break;
                }
            }
        }

        //input list datasets
        List<String> datasets = FileUtils.getListDirectories(pathFolder);

        ExecutorService exec = Executors.newFixedThreadPool(nthread);

        for (String dsName : datasets) {
            String currentDataset = pathFolder + dsName;
            String classIndexPath = currentDataset + "/class";
            int classIndex = -1; //if -1, the class index is the last attribute of the dataset
            //check if exists file
            File classIndexFile = new File(classIndexPath);
            if (classIndexFile.exists()) {
                classIndex = getClassIndex(classIndexPath);
            }

            TestExecutor test = new TestExecutor(classIndex, currentDataset, parallel);
            exec.submit(test);
        }
        exec.shutdown();
        exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);

    }

    private static int getClassIndex(String path) throws Exception {
        InputStream fis = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        return Integer.parseInt(line);
    }
}
