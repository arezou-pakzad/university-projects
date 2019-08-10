import glob
import imageio
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy import ndimage
from sklearn.metrics import roc_curve, auc
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix
from sklearn import metrics
from sklearn.decomposition import PCA
from sklearn import svm
from sklearn.neighbors import KNeighborsClassifier
from sklearn.ensemble import RandomForestClassifier
import pickle

image = pd.DataFrame(columns=['target', 'data'])


def accuracy(TP, TN, FP, FN):
    return (TP + TN) * 1.0 / (TP + TN + FP + FN)


def precision(TP, FP):
    return TP * 1.0 / (TP + FP)


def recall(TP, FN):
    return TP * 1.0 / (TP + FN)


def specificity(TN, FP):
    return TN * 1.0 / (TN + FP)


def f1score(TP, FN, FP):
    return 2.0 / ((1 / recall(TP, FN)) + (1 / (precision(TP, FP))))


def load_data(target):
    global image
    img_path = 'training/' + str(target) + '/*.png'
    for image_path in glob.glob(img_path):
        img = imageio.imread(image_path)
        img = np.reshape(img, 28 * 28)
        img = pd.DataFrame([[target, img]], columns=['target', 'data'])
        image = image.append(img, ignore_index=True)


def print_report(model_name, test_lbl, pred_lbl, mean_accuracy):
    print("==================================================")
    print(model_name + ":")
    print("==================================================")
    conf_mat = confusion_matrix(list(test_lbl), list(pred_lbl))
    print("Confusion Matrix:")
    print(conf_mat)

    TP = np.diag(conf_mat)
    FP = np.sum(conf_mat, axis=0) - TP
    FN = np.sum(conf_mat, axis=1) - TP
    num_classes = 10
    TN = []
    for i in range(num_classes):
        temp = np.delete(conf_mat, i, 0)  # delete ith row
        temp = np.delete(temp, i, 1)  # delete ith column
        TN.append(sum(sum(temp)))

    print()
    print("True Positive:", TP)
    print("True Negative:", TN)
    print("False Positive:", FP)
    print("False Negative:", FN)

    print()
    print("Accuracy:", accuracy(TP, TN, FP, FN))
    print("Precision:", precision(TP, FP))
    print("Recall:", recall(TP, FN))
    print("F1 Score:", f1score(TP, FN, FP))

    print()
    print("Classification Report for: " + model_name)
    print(metrics.classification_report(list(test_lbl), list(pred_lbl)))

    print()
    print("Mean Accuracy: ", mean_accuracy)

    print()
    print()

    fpr, tpr, thresholds = roc_curve(test_lbl, pred_lbl, pos_label=9)
    roc_auc = auc(fpr, tpr, reorder=True)
    plt.figure()
    plt.plot(fpr, tpr, color='darkorange', label='ROC curve (area = %0.3f)' % roc_auc)
    plt.plot([0, 1], [0, 1], color='navy', linestyle='--')
    plt.xlim([0.0, 1.0])
    plt.ylim([0.0, 1.05])
    plt.xlabel('False Positive Rate')
    plt.ylabel('True Positive Rate')
    plt.title('ROC Curve for '+model_name)
    plt.legend(loc="lower right")
    plt.savefig("ROC for "+model_name)
    plt.show()


# load data from file
for i in range(10):
    load_data(i)

# Applying Gaussian Filter on images for Noise Removal
for i in range(len(image)):
    image.data[i] = ndimage.gaussian_filter(image.data[i], 2)

# Splitting Train and Validation sets
train_img, test_img, train_lbl, test_lbl = train_test_split(image.data / 255.,
                                                            image.target,
                                                            test_size=1 / 7.0,
                                                            random_state=0)
# Applying PCA for Dimension Reduction
pca = PCA(n_components=28)
pca.fit(list(train_img))
pca_train_img = pca.transform(list(train_img))
pca_test_img = pca.transform(list(test_img))


#####################################
#       SVM Classifier           #
#####################################
clf = svm.SVC(gamma="scale", C=5, decision_function_shape='ovr')
clf.fit(list(pca_train_img), list(train_lbl))
svm_pred = clf.predict(list(pca_test_img))
svm_score = clf.score(list(pca_test_img), list(test_lbl))

print_report("SVM Model", list(test_lbl), list(svm_pred), svm_score)

# save the model to disk
filename = 'svm_model.sav'
pickle.dump(clf, open(filename, 'wb'))


###################################
#       K-NN Classifier           #
###################################
knn = KNeighborsClassifier(n_neighbors=5)
knn.fit(list(pca_train_img), list(train_lbl))
knn_score = knn.score(list(pca_test_img), list(test_lbl))
knn_pred = knn.predict(list(pca_test_img))

print_report("KNN Model", list(test_lbl), list(knn_pred), knn_score)

# save the model to disk
filename = 'knn_model.sav'
pickle.dump(knn, open(filename, 'wb'))


############################################
#       Random Forest Classifier           #
############################################
rf = RandomForestClassifier(max_features=28, random_state=0, n_estimators=150)
rf.fit(list(train_img), list(train_lbl))
rf_pred = rf.predict(list(test_img))
rf_score = rf.score(list(test_img), list(test_lbl))

print_report("Random Forest Model", list(test_lbl), list(rf_pred), rf_score)

# save the model to disk
filename = 'rf_model.sav'
pickle.dump(rf, open(filename, 'wb'))


#####################################
#       Voting Classifier           #
#####################################
df_predict = pd.DataFrame({"svm_pred": svm_pred,
                           "knn_pred": knn_pred,
                           "rf_pred": rf_pred,
                           "actual": test_lbl})

df_predict["predicted"] = df_predict.mode(axis=1)[0]
incorrect = df_predict[df_predict["actual"] != df_predict["predicted"]]
accuracy_score = 1 - incorrect.shape[0] / df_predict.shape[0]

print_report("Voting Predictor", list(df_predict["actual"]), list(df_predict["predicted"]), accuracy_score)
