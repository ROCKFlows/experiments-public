#Usage of the project:

This project tests the performances of some specified classifiers and pre-processing techniques on the dataset to test.

For the article, this project has been used to test 65 classifiers implemented in Weka and 12 pre-processing techniques on the datasets stored in Resources/datasets/work.tar.gz

(Section 3 (Experiments) of the article).

If you want to add more classifiers to test, you have to add them directly into the source code and then re-compile the project. The method taht contains the definition of the classifiers is IntermediateExecutor.inputClassifier(...)

If you want to add more pre-processing to test, you have to add them directly into the source code and then re-compile the project. The method taht contains the definition of the classifiers is TestExecutor.inputPreprocessers(...)

##Compile the project:

####Requirements:

Install maven3 and java 1.8 on your computer

##Compile the project:

Launch this command in souceCode/Tester/

```
mvn clean compile assembly:single
```

##Execute the program:

Launch this command in souceCode/Tester/target/

```
java -javaagent:../src/main/resources/classmexer.jar -Djava.awt.headless=true -jar tester.jar  -pef "path datasets" -nthread "N"  1> "path log" 2>&1
```

Where:

- "path datasets" is the path to the folder that contains the datasets contained into the archive Resources/datasets/work.tar.gz

	 For example, extract the archive Resources/datasets/work.tar.gz into the folder /home/user/Desktop/data/

	 Then, instead of "path datasets", put /home/user/Desktop/data/

- "N" is the number of threads that you want to use. Each thread executes in parallel one dataset contained in the path "path datasets"

	 If the parameter -nthread misses, the datasets are tested sequentially.

- "path log" is the path to the log file that contains the output of the program. For example: /home/user/Desktop/log

####Execute remotely

If you want to execute the program in a server, the following commands may be useful:

```
nohup java -Xms"min" -Xmx"max" -javaagent:../src/main/resources/classmexer.jar -Djava.awt.headless=true -jar tester.jar  -pef "path datasets" -nthread N  1> "path log" 2>&1 &
```

Where:

- nohup: keeps executing the program when you leave the ssh session.

- "min": is the start value of RAM occupied by the java process when it starts the execution. For example, put 4g to say 4Gb.

- "max": is the maximum value of RAM that the java process can allocate during its execution. For example, put 8g to say 8Gb.

- the final & says to execute the process in background, so your terminal will not be blocked during the execution of the program

##Input of the program:

Each dataset contained into the folder "path datasets" is contained into a folder. For example, in the path /home/user/Desktop/data/ there are three sub-folders:

- annealing
- iris
- wine

corresponding to three datasets of the UCI repository [1]: annealing, iris and wine.

Before starting executing the project, each dataset folder **must** contains the following files:

- test-0.arff: is the original dataset, the one built manually by reading the description of the dataset found in the UCI repository. Each attribute has been defined as nominal if its values are categories (ex: attribute: "size", values: "small", "medium", "large"), while it has been defined as numeric if its values are real numbers (ex: attribute: temperature, values: 22.1, 22.3, 19.9, ...). In the article, this datasets corresponds to pre-processing p0.

- test-11.arff: is the dataset made available by Delgado et al. [2] in their paper, descripted in Section 2 (Related Work) of the article [3]. In the article, this datasets corresponds to pre-processing p11.

- conxuntos.dat: is the file that identifies the validation dataset used to tune the parameters C and Gamma of Svm. It contains the indices 0-based of the instances that are part either of the training set or of the test set. The indices are separated by a white space, and the indices grouped together are defined into the same line, that is, until there is a carriege return. The training set is the first line, while the test set is the second line. The training set contains the 50% of the total number of instances, the test set contains the remaining 50%.

- conxuntos_kfold.dat: is the file that identifies the 4 folds used in the 4-Fold cross-validation, described in Section 3.5 of the article. The file has the same structure of conxuntos.dat, but it contains 8 lines: the first line identifies the training set, while the second line identifies the test set, for 4 times.

- class: is an optional file. If it is present, it contains the index 0-based of the class-attribute, that is, which attribute has to be treated as class-attribute.

##Output of the Program

When the project has finished its execution, from 1 to 12 pre-processed datasets (.arff files) may be created into the dataset folder, depending on the domain of applicability reported in Table 3.1 of the article. For example, pre-processing p1 is applicable to the original dataset (test-0.arff) of the *wine* dataset, so the pre-processed dataset test-1.arff will be created into the dataset folder.

One Excel file (.xlsx) is related to each dataset contained into the dataset folder(.arff file), containing the results of the experiments. It contains the same name of the dataset, for example: test-0.xlsx contains the results found on the test-0.arff dataset. 

Each Excel file contains 8 sheets:

- Result and Result10: in the first rows, they contain the #instances, #attributes, dataset properties, type of pre-processing used on the original dataset and the pre-processing time (ms). If the pre-processing contains an attribute selection, they contain also the indices of the selected attributes. If the Svm classifier is compatible with the dataset, they contain also the best values of C and Gamma, found on the validation dataset. 

- Result: for each classifier, it contains the results found on the 4-Fold cross-validation. If the classifier is compatible with the dataset, it contains the average accuracy, training time (ms), test time (ms) and the average memory occupied by the java object representing the trained classifier (bytes). 

- Result10: for each classifier, it contains the results found on the 10-Fold cross-validation. If the classifier is compatible with the dataset, it contains the average accuracy, training time (ms), test time (ms) and the average memory occupied by the java object representing the trained classifier (bytes).

- accuracy4Folds: for each compatible classifier, it contains the 4 accuracies found on the 4 folds.

- train4Folds: for each compatible classifier, it contains the 4 training times (ms) found on the 4 folds.

- test4Folds: for each compatible classifier, it contains the 4 test times (ms) found on the 4 folds.

- size4Folds: for each compatible classifier, it contains the 4 amount of memory (bytes) occupied by the java object representing the trained classifier.

- accuracy10Folds: for each compatible classifier, it contains the 10 accuracies found on the 4 folds.

- train10Folds: for each compatible classifier, it contains the 10 training times (ms) found on the 4 folds.

- test10Folds: for each compatible classifier, it contains the 10 test times (ms) found on the 4 folds.

- size10Folds: for each compatible classifier, it contains the 10 amounts of memory (bytes) occupied by the java object representing the trained classifier.


#References

[1]: http://mlr.cs.umass.edu/ml/datasets.html

[2]: http://persoal.citius.usc.es/manuel.fernandez.delgado/papers/jmlr/data.tar.gz

[3]: M.F.Delgado: Do we Need Hundreds of Classifiers to Solve Real World Classification Problems? 15(Oct):3133−3181, 2014
