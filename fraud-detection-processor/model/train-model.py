# Based on https://www.kaggle.com/currie32/predicting-fraud-with-tensorflow
import os

import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.python.tools import freeze_graph
from tensorflow.python.framework.graph_util import convert_variables_to_constants
# from sklearn.cross_validation import train_test_split
import matplotlib.pyplot as plt
from sklearn.utils import shuffle
from sklearn.metrics import confusion_matrix
import seaborn as sns
import matplotlib.gridspec as gridspec
from sklearn.preprocessing import StandardScaler
from sklearn.manifold import TSNE

df = pd.read_csv("./input/creditcard.csv")

print("Head: \n{}".format(df.head()))

print("Describe: \n{}".format(df.describe()))

print("Nulls: \n{}".format(df.isnull().sum()))

# Let's see how time compares across fraudulent and normal transactions.
print("Fraud")
print(df.Time[df.Class == 1].describe())
print()
print("Normal")
print(df.Time[df.Class == 0].describe())

# f, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=(12,4))
# bins = 50
# ax1.hist(df.Time[df.Class == 1], bins = bins)
# ax1.set_title('Fraud')
# ax2.hist(df.Time[df.Class == 0], bins = bins)
# ax2.set_title('Normal')
# plt.xlabel('Time (in Seconds)')
# plt.ylabel('Number of Transactions')
# plt.show()

# see if the transaction amount differs between the two types.
print("Fraud")
print(df.Amount[df.Class == 1].describe())
print()
print("Normal")
print(df.Amount[df.Class == 0].describe())

# f, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=(12,4))
# bins = 30
# ax1.hist(df.Amount[df.Class == 1], bins = bins)
# ax1.set_title('Fraud')
# ax2.hist(df.Amount[df.Class == 0], bins = bins)
# ax2.set_title('Normal')
# plt.xlabel('Amount ($)')
# plt.ylabel('Number of Transactions')
# plt.yscale('log')
# plt.show()

df['Amount_max_fraud'] = 1
df.loc[df.Amount <= 2125.87, 'Amount_max_fraud'] = 0

# Let's compare Time with Amount and see if we can learn anything new.

# f, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=(12,6))
#
# ax1.scatter(df.Time[df.Class == 1], df.Amount[df.Class == 1])
# ax1.set_title('Fraud')
#
# ax2.scatter(df.Time[df.Class == 0], df.Amount[df.Class == 0])
# ax2.set_title('Normal')
#
# plt.xlabel('Time (in Seconds)')
# plt.ylabel('Amount')
# plt.show()

# take a look at the anonymized features.

# Select only the anonymized features.
# v_features = df.ix[:,1:29].columns

# plt.figure(figsize=(20, 5*2))
# gs = gridspec.GridSpec(5, 6)
# for i, cn in enumerate(df[v_features]):
#     ax = plt.subplot(gs[i])
#     sns.distplot(df[cn][df.Class == 1], bins=50)
#     sns.distplot(df[cn][df.Class == 0], bins=50)
#     ax.set_xlabel('')
#     ax.set_title('histogram of feature: ' + str(cn))
# plt.show()


# Drop all of the features that have very similar distributions between the two types of transactions.
df = df.drop(['V28', 'V27', 'V26', 'V25', 'V24', 'V23', 'V22', 'V20', 'V15', 'V13', 'V8'], axis=1)

# Based on the plots above, these features are created to identify values where fraudulent transaction are more common.
df['V1_'] = df.V1.map(lambda x: 1 if x < -3 else 0)
df['V2_'] = df.V2.map(lambda x: 1 if x > 2.5 else 0)
df['V3_'] = df.V3.map(lambda x: 1 if x < -4 else 0)
df['V4_'] = df.V4.map(lambda x: 1 if x > 2.5 else 0)
df['V5_'] = df.V5.map(lambda x: 1 if x < -4.5 else 0)
df['V6_'] = df.V6.map(lambda x: 1 if x < -2.5 else 0)
df['V7_'] = df.V7.map(lambda x: 1 if x < -3 else 0)
df['V9_'] = df.V9.map(lambda x: 1 if x < -2 else 0)
df['V10_'] = df.V10.map(lambda x: 1 if x < -2.5 else 0)
df['V11_'] = df.V11.map(lambda x: 1 if x > 2 else 0)
df['V12_'] = df.V12.map(lambda x: 1 if x < -2 else 0)
df['V14_'] = df.V14.map(lambda x: 1 if x < -2.5 else 0)
df['V16_'] = df.V16.map(lambda x: 1 if x < -2 else 0)
df['V17_'] = df.V17.map(lambda x: 1 if x < -2 else 0)
df['V18_'] = df.V18.map(lambda x: 1 if x < -2 else 0)
df['V19_'] = df.V19.map(lambda x: 1 if x > 1.5 else 0)
df['V21_'] = df.V21.map(lambda x: 1 if x > 0.6 else 0)

print("Boza")
print(df.loc[1,:])
print("Boza2")
bla = df.loc[df.Class == 1]
print(bla.loc[541,:])

print("Head: \n{}".format(df.head()))

print("Describe: \n{}".format(df.describe()))

# Create a new feature for normal (non-fraudulent) transactions.
df.loc[df.Class == 0, 'Normal'] = 1
df.loc[df.Class == 1, 'Normal'] = 0

# Rename 'Class' to 'Fraud'.
df = df.rename(columns={'Class': 'Fraud'})

# 492 fraudulent transactions, 284,315 normal transactions.
# 0.172% of transactions were fraud.
print(df.Normal.value_counts())
print()
print(df.Fraud.value_counts())

pd.set_option("display.max_columns", 101)
df.head()

# Create dataframes of only Fraud and Normal transactions.
Fraud = df[df.Fraud == 1]
Normal = df[df.Normal == 1]

# Set X_train equal to 80% of the fraudulent transactions.
X_train = Fraud.sample(frac=0.8)
count_Frauds = len(X_train)

# Add 80% of the normal transactions to X_train.
X_train = pd.concat([X_train, Normal.sample(frac=0.8)], axis=0)

# X_test contains all the transaction not in X_train.
X_test = df.loc[~df.index.isin(X_train.index)]

# Shuffle the dataframes so that the training is done in a random order.
X_train = shuffle(X_train)
X_test = shuffle(X_test)

# Add our target features to y_train and y_test.
y_train = X_train.Fraud
y_train = pd.concat([y_train, X_train.Normal], axis=1)

y_test = X_test.Fraud
y_test = pd.concat([y_test, X_test.Normal], axis=1)

# Drop target features from X_train and X_test.
X_train = X_train.drop(['Fraud', 'Normal'], axis=1)
X_test = X_test.drop(['Fraud', 'Normal'], axis=1)

# Check to ensure all of the training/testing dataframes are of the correct length
print("X_train: \n{}".format(len(X_train)))
print("y_train: \n{}".format(len(y_train)))
print("X_test: \n{}".format(len(X_test)))
print("y_test: \n{}".format(len(y_test)))

print("X_train Columns: \n{}".format(X_train.columns))

'''
Due to the imbalance in the data, ratio will act as an equal weighting system for our model. 
By dividing the number of transactions by those that are fraudulent, ratio will equal the value that when multiplied
by the number of fraudulent transactions will equal the number of normal transaction. 
Simply put: # of fraud * ratio = # of normal
'''
ratio = len(X_train) / count_Frauds

y_train.Fraud *= ratio
y_test.Fraud *= ratio

# Names of all of the features in X_train.
features = X_train.columns.values

# Transform each feature in features so that it has a mean of 0 and standard deviation of 1;
# this helps with training the neural network.
for feature in features:
    mean, std = df[feature].mean(), df[feature].std()
    #print("feature: {}, mean: {}, std: {}".format(feature, mean, std))
    print("in[0][{}] = (in[0][{}] - ({}f)) / {}f;".format(feature, feature, mean, std))
    X_train.loc[:, feature] = (X_train[feature] - mean) / std
    X_test.loc[:, feature] = (X_test[feature] - mean) / std

# NN

# Split the testing data into validation and testing sets
split = int(len(y_test) / 2)

inputX = X_train.values
inputY = y_train.values
inputX_valid = X_test.values[:split]
inputY_valid = y_test.values[:split]
inputX_test = X_test.values[split:]
inputY_test = y_test.values[split:]

# Number of input nodes.
input_nodes = 37

# Multiplier maintains a fixed ratio of nodes between each layer.
mulitplier = 1.5

# Number of nodes in each hidden layer
hidden_nodes1 = 18
hidden_nodes2 = round(hidden_nodes1 * mulitplier)
hidden_nodes3 = round(hidden_nodes2 * mulitplier)

# Percent of nodes to keep during dropout.
pkeep = tf.compat.v1.placeholder(tf.float32, name="pkeep")

# input
x = tf.compat.v1.placeholder(tf.float32, [None, input_nodes], "input")

# layer 1
W1 = tf.Variable(tf.random.truncated_normal([input_nodes, hidden_nodes1], stddev=0.15))
b1 = tf.Variable(tf.zeros([hidden_nodes1]))
y1 = tf.nn.sigmoid(tf.matmul(x, W1) + b1)

# layer 2
W2 = tf.Variable(tf.truncated_normal([hidden_nodes1, hidden_nodes2], stddev=0.15))
b2 = tf.Variable(tf.zeros([hidden_nodes2]))
y2 = tf.nn.sigmoid(tf.matmul(y1, W2) + b2)

# layer 3
W3 = tf.Variable(tf.random.truncated_normal([hidden_nodes2, hidden_nodes3], stddev=0.15))
b3 = tf.Variable(tf.zeros([hidden_nodes3]))
y3 = tf.nn.sigmoid(tf.matmul(y2, W3) + b3)
y3 = tf.nn.dropout(y3, pkeep)

# layer 4
W4 = tf.Variable(tf.truncated_normal([hidden_nodes3, 2], stddev=0.15))
b4 = tf.Variable(tf.zeros([2]))
y4 = tf.nn.softmax(tf.matmul(y3, W4) + b4, name="output")

# output
y = y4
y_ = tf.compat.v1.placeholder(tf.float32, [None, 2])

# Parameters
training_epochs = 2500  # should be 2000, it will timeout when uploading
training_dropout = 0.9
display_step = 1  # 10
n_samples = y_train.shape[0]
batch_size = 2048
learning_rate = 0.005

# Cost function: Cross Entropy
cost = -tf.reduce_sum(y_ * tf.math.log(y))

# We will optimize our model via AdamOptimizer
optimizer = tf.compat.v1.train.AdamOptimizer(learning_rate).minimize(cost)

# Correct prediction if the most likely value (Fraud or Normal) from softmax equals the target value.
correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

# Note: some code will be commented out below that relate to saving/checkpointing your model.

accuracy_summary = []  # Record accuracy values for plot
cost_summary = []  # Record cost values for plot
valid_accuracy_summary = []
valid_cost_summary = []
stop_early = 0  # To keep track of the number of epochs before early stopping

# Save the best weights so that they can be used to make the final predictions
checkpoint = "/tmp/mymodel/best_model.ckpt"
saver = tf.compat.v1.train.Saver(max_to_keep=1)

# Initialize variables and tensorflow session
with tf.compat.v1.Session() as sess:
    sess.run(tf.compat.v1.global_variables_initializer())

    for epoch in range(training_epochs):
        for batch in range(int(n_samples / batch_size)):
            batch_x = inputX[batch * batch_size: (1 + batch) * batch_size]
            batch_y = inputY[batch * batch_size: (1 + batch) * batch_size]

            sess.run([optimizer], feed_dict={x: batch_x, y_: batch_y, pkeep: training_dropout})

        # Display logs after every 10 epochs
        if (epoch) % display_step == 0:
            train_accuracy, newCost = sess.run([accuracy, cost], feed_dict={x: inputX, y_: inputY, pkeep: training_dropout})

            valid_accuracy, valid_newCost = sess.run([accuracy, cost], feed_dict={x: inputX_valid, y_: inputY_valid, pkeep: 1})

            print("Epoch:", epoch,
                  "Acc =", "{:.5f}".format(train_accuracy),
                  "Cost =", "{:.5f}".format(newCost),
                  "Valid_Acc =", "{:.5f}".format(valid_accuracy),
                  "Valid_Cost = ", "{:.5f}".format(valid_newCost))

            # Save the weights if these conditions are met.
            if epoch > 0 and valid_accuracy > max(valid_accuracy_summary) and valid_accuracy > 0.999:
                print("\nBingo {} !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n".format(valid_accuracy))
                saver.save(sess, checkpoint)

                SAVE_DIR = os.path.abspath(os.path.curdir)
                gd = sess.graph_def
                minimal_graph = convert_variables_to_constants(sess, sess.graph.as_graph_def(), ['output'])
                tf.train.write_graph(minimal_graph, SAVE_DIR, './model/fraud_detection_graph.pb', as_text=False)
                tf.train.write_graph(minimal_graph, SAVE_DIR, './model/fraud_detection_graph.txt', as_text=True)

                #tf.compat.v1.train.write_graph(sess.graph.as_graph_def(), '/tmp/mymodel', 'tensorflowModel.pbtxt', as_text=True)
                #freeze_graph.freeze_graph('/tmp/mymodel/tensorflowModel.pbtxt', "", False, '/tmp/mymodel/best_model.ckpt', "output_y", "", "", './model/frozentensorflowModel.pb', True, "")
                break

            # Record the results of the model
            accuracy_summary.append(train_accuracy)
            cost_summary.append(newCost)
            valid_accuracy_summary.append(valid_accuracy)
            valid_cost_summary.append(valid_newCost)

            # If the model does not improve after 15 logs, stop the training.
            if valid_accuracy < max(valid_accuracy_summary) and epoch > 2500:
                stop_early += 1
                if stop_early == 15:
                    break
            else:
                stop_early = 0

    print()
    print("Optimization Finished!")
    print()

with tf.compat.v1.Session() as sess:
    #Load the best weights and show its results
    saver.restore(sess, checkpoint)
    training_accuracy = sess.run(accuracy, feed_dict={x: inputX, y_: inputY, pkeep: training_dropout})
    validation_accuracy = sess.run(accuracy, feed_dict={x: inputX_valid, y_: inputY_valid, pkeep: 1})

    print("Results using the best Valid_Acc:")
    print()
    print("Training Accuracy =", training_accuracy)
    print("Validation Accuracy =", validation_accuracy)


# Plot the accuracy and cost summaries
f, (ax1, ax2) = plt.subplots(2, 1, sharex=True, figsize=(10, 4))

ax1.plot(accuracy_summary)  # blue
ax1.plot(valid_accuracy_summary)  # green
ax1.set_title('Accuracy')

ax2.plot(cost_summary)
ax2.plot(valid_cost_summary)
ax2.set_title('Cost')

plt.xlabel('Epochs (x10)')
plt.show()
