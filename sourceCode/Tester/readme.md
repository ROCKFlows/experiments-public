##Compile the project:

Launch this command in souceCode/Tester/

```
mvn clean compile assembly:single
```

##Execute the program:

Launch this command in souceCode/Tester/target/

```
java -javaagent:../src/main/resources/classmexer.jar -Djava.awt.headless=true -jar tester.jar  -pef "path datasets" -nthread N  1> "path log" 2>&1
```

Where:

- "path datasets" is the folder that contains the datasets contained into the archive Resources/datasets/work.tar.gz

	 For example, extract the archive Resources/datasets/work.tar.gz into the folder /home/user/Desktop/data/

	 Then, instead of "path datasets", put /home/user/Desktop/data/

- N is the number of threads that you want to use. Each thread executes in parallel one dataset contained in the path "path datasets"

	 If the parameter -nthread misses, the datasets are executed sequentially.

- "path log" is the path to the log file that contains the output of the program. For example: /home/user/Desktop/log

####Execute remotely

If you want to execute the program in a server, the following commands may be useful:

```
nohup java -Xms"min" -Xmx"max" -javaagent:../src/main/resources/classmexer.jar -Djava.awt.headless=true -jar tester.jar  -pef "path datasets" -nthread N  1> "path log" 2>&1 &
```

Where:

- nohup keeps executing the program when you leave the ssh session.

- "min": is the start value of RAM occupied by the java process when it starts the execution. For example, put 4g to say 4Gb.

- "max": is the maximum value of RAM that the java process can allocate during its execution. For example, put 8g to say 8Gb.

- the final & says to execute the process in background, so your terminal will not be blocked during the execution of the program
