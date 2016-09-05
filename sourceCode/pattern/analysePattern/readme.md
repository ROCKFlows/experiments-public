#Usage of the project:

This project has been used to create the file Final-Analysis-4Folds.xlsx and Final-Analysis-10Folds.xlsx inside each dataset folder. At first, each dataset need to be tested with the sourceCode/Tester project.

##Compile the project:

####Requirements:

Install maven3 and java 1.8 on your computer

Install the maven dependency:

mvn install:install-file -Dfile="Absolute path..."/sourceCode/pattern/analysePattern/src/main/resources/jdistlib-0.4.4-bin.jar
 -DgroupId=jdistlib -DartifactId=jdistlib -Dversion=0.4.4 -Dpackaging=jar

####Command

Launch this command in souceCode/pattern/analysePattern/

```
mvn clean compile assembly:single
```

##Execute the program:

Launch this command in souceCode/pattern/analysePattern/target/

```
java -jar workflow-analysis.jar  -pef "path input" -nthread "N" -status 1> "path log"

```

Where:

- "path input" is the folder that contains the results obtained from the execution of the sourcecode/Tester project.

	For example, put /home/user/Desktop/data/ as "path input" if it contains the result of the datasets tested with the sourcecode/Tester project.
	
- "N" is the number of threads that you want to use. Each thread executes in parallel one dataset contained in the path "path datasets"

	 If the parameter -nthread misses, the datasets are executed sequentially.

- status: is an optional parameter. For the purpose of the article it is not used, it is used only to define the Demo of ROCKFlows. From the rank value, it assigns a status of performance to the workflow as: "Low", "Medium" or "High"

- "path log" is the path to the log file that contains the output of the program. For example: /home/user/Desktop/log

####Execute remotely

If you want to execute the program in a server, the following commands may be useful:

```
nohup java -Xms"min" -Xmx"max" -jar workflow-analysis.jar  -pef "path input" -nthread "N" -status 1> "path log" &
```

Where:

- nohup keeps executing the program when you leave the ssh session.

- "min": is the start value of RAM occupied by the java process when it starts the execution. For example, put 4g to say 4Gb.

- "max": is the maximum value of RAM that the java process can allocate during its execution. For example, put 8g to say 8Gb.

- the final & says to execute the process in background, so your terminal will not be blocked during the execution of the program


##Output of the Program

Let's take for example the wine dataset folder, once it has been tested by the sourcecode/Tester project. It contains the following files:

- class
- conxuntos.dat
- conxuntos_kfold.dat
- test-0.arff
- test-0.xlsx
- test-1.arff
- test-1.xlsx
- test-6.arff
- test-6.xlsx
- test-7.arff
- test-7.xlsx
- test-11.arff
- test-11.xlsx
- test-12.arff
- test-12.xlsx

The output of this program are 2 Excel files: Final-Analysis-4Folds.xlsx and Final-Analysis-10Folds.xlsx. Each file contains a ranking of workflows, ordered by the rank Accuracy value. Each workflow is identified by the pair (name of classifier, pre-processing id), where the pre-processing id is the same id reported into the dataset filename. For example, for the dataset "test-0.arff", the pre-processing id is 0. For each workflow, the reported values of average accuracy, training time (ms), test time (ms) and memory usage (bytes) are the same values reported into the file test-"id".xlsx file. In case of the Final-Analysis-4Folds.xlsx file, the results come from the sheet Result (4-Fold cross-validation), while in case of the Final-Analysis-10Folds.xlsx file, the results come from the sheet Result10 (10-Fold cross-validation).

For each value (accuracy, training time, test time, memory usage), the rank value described in Section 4 of the article has been applied among the workflows. The 4 or 10 values (depending on which cross-validation method is used) come from either the sheets accuracy4Folds, train4Folds, test4Folds, size4folds or the sheets accuracy10Folds, train10Folds, test10Folds, size10Folds.

For the purpose of the article, only the rank accuracy value has been taken into account.
