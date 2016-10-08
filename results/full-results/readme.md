Each folder corresponds to a dataset. In each folder, the files are:
 * **Test-i.xlsx** : measurements for the algorithms on the dataset pre-processed with the sequence pi for both 4-Fold and 10-Fold cross-validation. 0<=i<=12. There is such a file only if pi could be applied on the dataset.
 
    * First sheet contains average measures for each algorithm
    * The other sheets contain all measures made in both 4-fold and 10-fold validation, from which the averages are computed.
    
 * **Final-Analysis-kfolds** : The ranking of all workflows (pi, cj) on accuracy, execution time as well as model size. (pi the pre-processings described in the paper with 0<=i<=12. cj one of the classifiers descibed in the paper 1<=j<=65). There are two such files, for 4-Fold cross-validation and 10-Fold cross-validation.
 
 You can also download the full archive containing all results.