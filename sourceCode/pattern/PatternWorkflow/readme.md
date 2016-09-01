#Usage of the project:

This project is used to calculate the average rank of the workflows that match a specific data pattern.

*N.B.* For the moment, data patterns are defined directly into the source code, in the MainExperiment.java file.

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
nohup java -jar workflow-avg-rank.jar  -src "path input" -dest "pat output" -names ../src/main/resources/names  -nthread "N"  1> "path log" 2>&1 &
```

Where:

- "path input" is the folder that contains the results obtained from the execution of the sourcecode/Tester project.

	For example, put /home/user/Desktop/data/ as "path input" if it contains the result of the datasets tested with the sourcecode/Tester project.
	
- "N" is the number of threads that you want to use. Each thread executes in parallel one dataset contained in the path "path datasets"

	 If the parameter -nthread misses, the datasets are executed sequentially.

- "path output " is the output folder that contains the data patterns specified into the source code. Example of parameter: /home/user/Desktop/patterns/

- -names: it contains the name of all the classifiers tested in our experiments. It is used to retrieve the results of the same workflow among the different Excel files, where the name of the classifier is always the same

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
