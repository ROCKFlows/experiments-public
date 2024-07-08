# Repository Description

- **datasets**: contains the datasets tested for writing the article.

- **results**: contains the results obtained on the datasets and the comparisons produced by the code contained in the src folder.

- **src**: contains the projects that run and analyze experiments.
- **doc**: contains the documents produced by Luca Parisi


By Luca Parisi
The research done for this project is the theoretic work that there is behind ROCKFlows, the system that we are implementing that aims to advice non-expert users the best workflows that can solve their machine learning problems. In order to start the project, we focused on the supervised classification problems, by considering 12 pre-processing techniques and 65 classifiers implemented on the Weka platform. At first, we have defined a strategy based on statistical hypothesis tests that groups together workflows that are not significantly different. Then, we have used this strategy in order to answer the three questions we wondered in Section 2.1. We have proved that if we want to compare several classifiers among them, we have to take into account three factors: the choice of the evaluation method, the choice of the pre-processing and the treatment of missing values. Each of these factors have a significant impact on the results obtained by the comparison of the classifier. Moreover, we have proposed a strategy based on data patterns in order to predict the workflows that, allegedly, will have the best accuracy on untested datasets, without doing the evaluation phase and without comparing all the possible workflows. For example, from our experiments we have found one data pattern where the workflow (11, Svm) has an average rank=1, both in case of 4-Fold and 10-Fold cross-validation. This result may suggest that we can expect the workflow (11, Svm) to reach the best accuracy on untested datasets that match the same data pattern, or at least it would be a good candidate workflow to predict. The future related research may concern to study more in depth the data patterns of datasets, in order to find workflows that are supposed to get the best results in terms of accuracy. Finally, we have studied how the time of execution of workflows and the memory required by the trained classifier vary with respect to the structure of datasets. We have proved that at least three factors affect time and memory values: the number of classes, the number of attributes and the number of instances of datasets. Then, we have searched if there exists some dependency among these factors and time and memory performances. For example, from 10 datasets of our experiments, we have found four mathematical functions that approximate time and memory performances of four workflows with respect to the number of instances of datasets. So, we can predict time and memory performances of four workflows on untested datasets only by looking at its number of instances. The future related research may concern to study more in depth the dependencies between the structure of datasets and time and memory performances.
The research presented in this report is not exhaustive, it is limited by the number of datasets, by the number of pre-processing techniques and by the number of classifiers tested. Moreover, it is limited to the Weka platform, which can affect the implementation of classifiers. This work may and should be extended into this direction, in order to fully understand the relations among the nature of datasets and accuracy, time and memory peformances of the execution of workflows. To conclude, a similar work might and should be done on other machine learning problems, such as clustering, regression and anomaly detection.

