#Usage of the project:

This project can be used to calculate the average rank of the workflows that match a specific data pattern.

*N.B.* For the moment, data patterns are defined directly into the source code, in the MainExperiment.java file.

##Compile the project:

####Requirements:

Install maven3 and java 1.8 on your computer

####Command

Launch this command in souceCode/pattern/patternWorkflow/

```
mvn clean compile assembly:single
```

##Execute the program:

Launch this command in souceCode/pattern/patternWorkflow/target/

```
java -jar workflow-avg-rank.jar  -src "path input" -dest "path output" -names ../src/main/resources/names  -nthread "N"  1> "path log" 2>&1
```

Where:

- "path input" is the folder that contains the results obtained from the execution of the sourcecode/Tester project.

	For example, put /home/user/Desktop/data/ as "path input" if it contains the result of the datasets tested with the sourcecode/Tester project.
	
- "N" is the number of threads that you want to use. Each thread executes in parallel one dataset contained in the path "path datasets"

	 If the parameter -nthread misses, the datasets are executed sequentially.

- "path output" is the output folder that contains the data patterns specified into the source code. Example of parameter: /home/user/Desktop/patterns/

- -names: it contains the name of all the classifiers tested in our experiments. It is used to retrieve the results of the same workflow among the different Excel files, where the name of the classifier is always the same

- "path log" is the path to the log file that contains the output of the program. For example: /home/user/Desktop/log

####Execute remotely

If you want to execute the program in a server, the following commands may be useful:

```
nohup java -Xms"min" -Xmx"max" -jar workflow-avg-rank.jar  -src "path input" -dest "path output" -names ../src/main/resources/names  -nthread "N"  1> "path log" 2>&1 &
```

Where:

- nohup keeps executing the program when you leave the ssh session.

- "min": is the start value of RAM occupied by the java process when it starts the execution. For example, put 4g to say 4Gb.

- "max": is the maximum value of RAM that the java process can allocate during its execution. For example, put 8g to say 8Gb.

- the final & says to execute the process in background, so your terminal will not be blocked during the execution of the program

##Input of the Program

The dataset folders treated by the analysePattern project, which contains the files Final-Analysis-4Folds.xlsx and Final-Analysis-10Folds.xlsx

##Output of the Program

In the "path output", the one folder for each pattern specified into the source code is created. Each pattern folder contains the folder of the datasets "test-0.arff" that match the specified data pattern. Inside each pattern folder, beyond the folder of the datasets that match the patern, 3 files are created: 

- readme: it contains the description of the data pattern. For example, it says what the range of attributes is, the range of instances, ...

- analysis-4Folds.xlsx and analysis-10Folds.xlsx: they contain the following values for each workflow: average rank accuracy, standard deviation rank accuracy, average accuracy, standard deviation average accuracy and #times the workflow has rank=1 on the datasets that match the data pattern.

- analysis-4Folds.xlsx: the average values have been calculated from the results reported in the file Final-Analysis-4Folds.xlsx of each dataset folder.

- analysis-10Folds.xlsx: the average values have been calculated from the results reported in the file Final-Analysis-10Folds.xlsx of each dataset folder.
