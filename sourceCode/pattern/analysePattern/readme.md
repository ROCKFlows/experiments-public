#Usage of the project:

This project has been used to create the file Final-Analysis-4Folds.xlsx and Final-Analysis-10Folds.xlsx inside each dataset folder. At first, each dataset need to be tested with the sourceCode/Tester project

##Compile the project:

####Requirements:

Install maven3 and java 1.8 on your computer

Install the maven dependency:

mvn install:install-file -Dfile="Absolute path..."/sourceCode/pattern/analysePattern/src/main/resources/jdistlib-0.4.4-bin.jar
 -DgroupId=jdistlib -DartifactId=jdistlib -Dversion=0.4.4 -Dpackaging=jar

####Command

Launch this command in souceCode/statisticAnalyzer/

```
mvn clean compile assembly:single
```

##Execute the program:

Launch this command in souceCode/statisticAnalyzer/target/

```
nohup java -jar workflow-analysis.jar  -pef "path input" -nthread "N" -status 1> "path log" &

```

Where:

- "path input" is the folder that contains the results obtained from the execution of the sourcecode/Tester project.

	For example, put /home/user/Desktop/data/ as "path input" if it contains the result of the datasets tested with the sourcecode/Tester project.
	
- "N" is the number of threads that you want to use. Each thread executes in parallel one dataset contained in the path "path datasets"

	 If the parameter -nthread misses, the datasets are executed sequentially.

- status: is an optional parameter. It is not 

- "path log" is the path to the log file that contains the output of the program. For example: /home/user/Desktop/log

####Execute remotely

If you want to execute the program in a server, the following commands may be useful:

```
nohup java -jar performance-analyzer.jar  -pef "path input" -out "path output"  -nthread "N" -id1 "indices1" -id2 "indices2"  1> "path log" 2>&1 &
```

Where:

- nohup keeps executing the program when you leave the ssh session.

- "min": is the start value of RAM occupied by the java process when it starts the execution. For example, put 4g to say 4Gb.

- "max": is the maximum value of RAM that the java process can allocate during its execution. For example, put 8g to say 8Gb.

- the final & says to execute the process in background, so your terminal will not be blocked during the execution of the program
