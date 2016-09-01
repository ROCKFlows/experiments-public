#Usage of the project:

This project has been used to check what has a significant impact when we want to do a ranking of classifiers.

(Section xxx of the article)

Example from the article:

- evaluation impact
- pre-processing impact
- missing values impact

##Compile the project:

####Requirements:

Install maven3 and java 1.8 on your computer

####Command

Launch this command in souceCode/statisticAnalyzer/

```
mvn clean compile assembly:single
```

##Execute the program:

Launch this command in souceCode/statisticAnalyzer/target/

```

java -jar performance-analyzer.jar  -pef "path input" -out "path output"  -nthread "N" -id1 "indices1" -id2 "indices2"  1> "path log" 2>&1

```

Where:

- "path input" is the folder that contains the results obtained from the execution of the sourcecode/Tester project.

	For example, put /home/user/Desktop/data/ as "path input" if it contains the result of the datasets tested with the sourcecode/Tester project.

- "path output" is the folder that contains the excel files produced by the program. Example of parameter: /home/user/Desktop/data/analysis/
	
- "N" is the number of threads that you want to use. Each thread executes in parallel one dataset contained in the path "path datasets"

	 If the parameter -nthread misses, the datasets are executed sequentially.

- "indices1" is the list of indices that refer to the pre-processing used for building the first ranking. Accepted values: [0,12]. Examples of parameter: 0 / 0,1 / 0,1,2

- "indices2" is the list of indices that refer to the pre-processing used for building the second ranking. Accepted values: [0,12]. Examples of parameter: 6 / 7,8 / 8,9,10

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
