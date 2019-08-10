import pandas as pd
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
from sklearn.metrics import roc_curve, auc
from sklearn.utils.class_weight import compute_sample_weight
from scipy import stats
import seaborn as sns
from sklearn.metrics import roc_curve, auc


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


# Reading Datas #
# Train DataFrame
train_df = pd.read_csv('data/train.csv', dtype={'notif_id': int, 'user_id': int, 'interaction': 'B'})

# Users DataFrame
users_df = pd.read_csv('data/users.csv')
users_df2 = pd.read_csv('data/repaired_data/users.csv')
users_df = pd.concat([users_df, users_df2]).drop_duplicates(keep="last").reset_index(drop=True)

# Notifs DataFrame
notifs_df = pd.read_csv('data/notifs.txt', header=None, sep='|', names=['notif_id'])
cols = ['notif_id', 'sending_dow', 'sending_hour', 'sending_min', 'M', 'words']
notifs_df[cols] = notifs_df.notif_id.str.split(' ', n=5, expand=True)
notifs_df[cols[:-1]] = notifs_df[cols[:-1]].astype(int)

notifs_df2 = pd.read_csv('data/repaired_data/notifs.txt', header=None, sep='|', names=['notif_id'])
notifs_df2[cols] = notifs_df2.notif_id.str.split(' ', n=5, expand=True)
notifs_df2[cols[:-1]] = notifs_df2[cols[:-1]].astype(int)

notifs_df['words'] = notifs_df['words'].apply(lambda x: list(map(int, x.split())) if x is not None else [])
notifs_df['length'] = notifs_df['words'].apply(lambda x: len(x) if x is not None else 0)
notifs_df2['words'] = notifs_df2['words'].apply(lambda x: list(map(int, x.split())) if x is not None else [])
notifs_df2['length'] = notifs_df2['words'].apply(lambda x: len(x) if x is not None else 0)

notifs_df = pd.concat([notifs_df, notifs_df2])


# Search Result #
special_user = train_df.loc[train_df.user_id == 12]
notifs_df['notif_id'] = notifs_df.notif_id.astype(int)
special_notif = notifs_df[notifs_df['notif_id'].isin(special_user['notif_id'])]

special_user.to_csv('search_result.txt', header=True, index=False, sep='\t', mode='a')
special_notif.to_csv('search_result.txt', header=True, index=False, sep='\t', mode='a')


# Calculate Statistics #
def calc_stat(df, col):
    mean = df[col].mean()
    variance = df[col].var()
    mode = df[col].mode()[0]
    median = df[col].median()
    print("\n")
    print(col, '\nmean:\t', mean, '\nvariance:\t', variance, '\nmode:\t', mode, '\nmedian:\t', median)


cols = ['delivery_dow', 'delivery_hour', 'delivery_min']
for col in cols:
    calc_stat(train_df, col)

user_mean = train_df.groupby('user_id')['delivery_min', 'delivery_hour', 'delivery_dow'].mean()
user_var = train_df.groupby('user_id')['delivery_min', 'delivery_hour', 'delivery_dow'].var()
user_median = train_df.groupby('user_id')['delivery_min', 'delivery_hour', 'delivery_dow'].median()
# user_mode = stats.mode(train_df.groupby('user_id')['delivery_min', 'delivery_hour', 'delivery_dow'])[0][0]

# Plots #
# Scatter Plot of N1 and N2
plt.plot('N1', 'N2', data=users_df, linestyle='', marker='o', markersize=1)
plt.xlabel('N1')
plt.ylabel('N2')
plt.title('Scatter Plot of N1 and N2', loc='center')
plt.show()

# Box Plot of N3
color = dict(boxes='Purple', whiskers='DarkBlue',
             medians='DarkBlue', caps='Purple')

users_df['N3'].plot.box(color=color, sym="", showmeans=True)
plt.title("Box Plot of N3")
plt.show()

# Distribution Plot of Words
words = []
words_df = notifs_df[['words']].dropna()
for i in range(len(words_df)):
    l = words_df.values[i][0]
    for x in l:
        words.append(int(x))

sns.kdeplot(words, shade=True)
plt.title("Distribution Plot of Words")
plt.xlabel("Words")
plt.show()

# Bar Plot of Delivery Minute
barplot_df = train_df.groupby('delivery_min').agg({'delivery_min': 'count'})
barplot_df.plot.bar(legend=None, title="Bar Plot of Delivery Minute")
plt.tick_params(labelsize=6)
plt.xlabel("Delivery Minute")
plt.show()

# Circular Plot of M
circularplot_df = notifs_df.groupby('M').agg({'M': 'count'})
circularplot_df.plot(kind='pie', subplots=True, figsize=(8, 8), autopct='%.1f%%', startangle=90)
plt.title('Circular Plot of M')
plt.show()

# # Cleaning DataFrames #
# # Information leakage
train_df = train_df.drop('interaction_dow', axis=1)
train_df = train_df.drop('interaction_hour', axis=1)
train_df = train_df.drop('interaction_min', axis=1)
train_df = train_df.drop('delivery_dow', axis=1)
train_df = train_df.drop('delivery_hour', axis=1)
train_df = train_df.drop('delivery_min', axis=1)

# Too many NAs
users_df = users_df.drop('N3', axis=1)
users_df = users_df.drop('N2', axis=1)

# Useless in Learning Algo
notifs_df = notifs_df.drop('sending_dow', axis=1)
notifs_df = notifs_df.drop('sending_hour', axis=1)
notifs_df = notifs_df.drop('sending_min', axis=1)
notifs_df = notifs_df.drop('words', axis=1)
notifs_df2 = notifs_df2.drop('sending_dow', axis=1)
notifs_df2 = notifs_df2.drop('sending_hour', axis=1)
notifs_df2 = notifs_df2.drop('sending_min', axis=1)
notifs_df2 = notifs_df2.drop('words', axis=1)


# Keeping categories with max count and label the rest as "other"
# C1
c1_num = users_df['C1'].value_counts(sort=True).to_frame()
c1_list = list(c1_num.index)[:11]
mask = np.logical_not(users_df.C1.isin(c1_list))
users_df.loc[mask, 'C1'] = "other"


# C2
c2_num = users_df['C2'].value_counts(sort=True).to_frame()
c2_list = list(c2_num.index)[:11]
mask = np.logical_not(users_df.C2.isin(c2_list))
users_df.loc[mask, 'C2'] = "other"

# C3
c3_num = users_df['C3'].value_counts(sort=True).to_frame()
c3_list = list(c3_num.index)[:11]
mask = np.logical_not(users_df.C3.isin(c3_list))
users_df.loc[mask, 'C3'] = "other"

# C4
c4_num = users_df['C4'].value_counts(sort=True).to_frame()
c4_list = list(c4_num.index)[:11]
mask = np.logical_not(users_df.C4.isin(c4_list))
users_df.loc[mask, 'C4'] = "other"

# C5
c5_num = users_df['C5'].value_counts(sort=True).to_frame()
c5_list = list(c5_num.index)[:11]
mask = np.logical_not(users_df.C5.isin(c5_list))
users_df.loc[mask, 'C5'] = "other"

# C6
c6_num = users_df['C6'].value_counts(sort=True).to_frame()
c6_list = list(c6_num.index)[:11]
mask = np.logical_not(users_df.C6.isin(c6_list))
users_df.loc[mask, 'C6'] = "other"


# Merging data for learning #
mat = pd.merge(train_df, users_df, on='user_id')
mat = pd.merge(mat, notifs_df, on='notif_id')

mat = mat.dropna()

res = mat['interaction']
train_user_notif = mat[['user_id', 'notif_id']]
mat = mat.drop('interaction', axis=1)
mat = mat.drop('user_id', axis=1)
mat = mat.drop('notif_id', axis=1)
mat = pd.get_dummies(mat, columns=['M', 'C1', 'C2', 'C3', 'C4', 'C5', 'C6'])

X_train, X_test, y_train, y_test = train_test_split(mat, res, random_state=0)
classifier = LogisticRegression(random_state=0)
classifier.fit(X_train, y_train)
y_score = classifier.predict_proba(X_test)[:, 1]

# ROC Plot #
w = compute_sample_weight(class_weight={0: 1, 1: 5}, y=y_test)
fpr, tpr, thresholds = roc_curve(y_test, y_score, sample_weight=w)
roc_auc = auc(fpr, tpr, reorder=True)
plt.figure()
plt.plot(fpr, tpr, color='darkorange', label='ROC curve (area = %0.3f)' % roc_auc)
plt.plot([0, 1], [0, 1], color='navy', linestyle='--')
plt.xlim([0.0, 1.0])
plt.ylim([0.0, 1.05])
plt.xlabel('False Positive Rate')
plt.ylabel('True Positive Rate')
plt.title('Receiver Operating Characteristic')
plt.legend(loc="lower right")
plt.savefig("ROC")
plt.show()

# Set Threshold
threshold = 0.586

# Confusion Matrix
y_pred = np.where(y_score > threshold, 1, 0)
confusion_mat = confusion_matrix(y_test, y_pred)
print("Confusion Matrix:")
print(confusion_mat)
TN = confusion_mat[0][0]
FP = confusion_mat[0][1]
FN = confusion_mat[1][0]
TP = confusion_mat[1][1]

# Results
print("Accuracy:\t", accuracy(TP, TN, FP, FN))
print("Precision:\t", precision(TP, FP))
print("Recall:\t", recall(TP, FN))
print("Specificity:\t", specificity(TN, FP))
print("F1 Score:\t", f1score(TP, FN, FP))

# get test output #
test_df = pd.read_csv('data/test.csv')

mat_test = pd.merge(test_df, users_df2, on="user_id", how='left')
mat_test = pd.merge(mat_test, notifs_df2, on="notif_id", how='left')

mask = np.logical_not(mat_test.C1.isin(c1_list))
mat_test.loc[mask, 'C1'] = "other"

mask = np.logical_not(mat_test.C2.isin(c2_list))
mat_test.loc[mask, 'C2'] = "other"

mask = np.logical_not(mat_test.C3.isin(c3_list))
mat_test.loc[mask, 'C3'] = "other"

mask = np.logical_not(mat_test.C4.isin(c4_list))
mat_test.loc[mask, 'C4'] = "other"

mask = np.logical_not(mat_test.C5.isin(c5_list))
mat_test.loc[mask, 'C5'] = "other"

mask = np.logical_not(mat_test.C6.isin(c6_list))
mat_test.loc[mask, 'C6'] = "other"

# mat_test = mat_test.dropna()
test_output = mat_test[['user_id', 'notif_id']]
mat_test = mat_test.drop('user_id', axis=1, inplace=False)
mat_test = mat_test.drop('notif_id', axis=1, inplace=False)
mat_test = pd.get_dummies(mat_test, columns=['M', 'C1', 'C2', 'C3', 'C4', 'C5', 'C6'])
mat_test = mat_test[X_test.columns]

y_pred = classifier.predict_proba(mat_test)[:, 1]
y_pred = y_pred.astype(int)
interaction = np.where(y_pred > threshold, 1, 0)
test_output['interaction'] = interaction
output = test_output[['user_id', 'notif_id', 'interaction']]
test_output.to_csv('output.csv', header=True, index=False)
