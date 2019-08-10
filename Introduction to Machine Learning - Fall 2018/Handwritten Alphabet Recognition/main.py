import pickle
import matplotlib.pyplot as plt
import numpy as np
import keras
from keras.layers import Dense, Flatten
from keras.layers import Conv2D, MaxPooling2D, Dropout, BatchNormalization, Activation
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix
from keras.preprocessing.image import ImageDataGenerator


def print_report(model, x_test, y_test, history, model_name):
    plt.figure(figsize=(15, 15))
    plt.plot(history.history['loss'], color='darkorange', label='Train Loss')
    plt.plot(history.history['val_loss'], color='navy', label='Validation Loss')
    plt.xlabel('Epochs')
    plt.ylabel('loss')
    plt.legend(loc="lower right")
    plt.title('Loss Plot for ' + model_name)
    plt.show()

    plt.figure(figsize=(15, 15))
    plt.plot(history.history['acc'], color='darkorange', label='Train Acc')
    plt.plot(history.history['val_acc'], color='navy', label='Validation Acc')
    plt.xlabel('Epochs')
    plt.ylabel('Acc')
    plt.legend(loc="lower right")
    plt.title('Acc Plot for ' + model_name)
    plt.show()

    print("==================================================")
    print(model_name + ":")
    print("==================================================")

    score = model.evaluate(x_test, y_test, verbose=0)
    print("Accuracy:")
    print(score[1])
    print()

    pred = model.predict(x_test, verbose=0)
    conf_mat = confusion_matrix(y_test.argmax(axis=1), pred.argmax(axis=1))
    print("Confusion Matrix:")
    print(conf_mat)
    print()

    print("Model Summery:")
    print(model.summary())
    print()

    param_cnt = model.count_params()
    print("Parameter Count:")
    print(param_cnt)
    print()
    print()
    print()

    return pred


data_file = open("train.data", 'rb')
data_dict = pickle.load(data_file)
train_data = data_dict['data']
train_lbl = data_dict['labels']


################################################
#       Constructing Neural Networks           #
################################################
# 1
model1 = keras.Sequential()
model1.add(Dense(units=50, activation='sigmoid', input_shape=(28, 28, 1)))
model1.add(Flatten())
model1.add(Dense(units=10, activation='softmax'))

model1.compile(loss=keras.losses.categorical_crossentropy,
               optimizer=keras.optimizers.SGD(lr=0.01),
               metrics=["accuracy"])

# 2
model2 = keras.Sequential()
model2.add(Dense(units=50, activation='tanh', input_shape=(28, 28, 1)))
model2.add(Flatten())
model2.add(Dense(units=10, activation='softmax'))

model2.compile(loss=keras.losses.categorical_crossentropy,
               optimizer=keras.optimizers.SGD(lr=0.01),
               metrics=["accuracy"])

# 3
model3 = keras.Sequential()
model3.add(Dense(units=500, activation='tanh', input_shape=(28, 28, 1)))
model3.add(Flatten())
model3.add(Dense(units=10, activation='softmax'))

model3.compile(loss=keras.losses.categorical_crossentropy,
               optimizer=keras.optimizers.SGD(lr=0.01),
               metrics=["accuracy"])

# 4
model4 = keras.Sequential()
model4.add(Dense(units=100, activation='tanh', input_shape=(28, 28, 1)))
model4.add(Flatten())
model4.add(Dense(units=100, activation='tanh'))
model4.add(Dense(units=10, activation='softmax'))

model4.compile(loss=keras.losses.categorical_crossentropy,
               optimizer=keras.optimizers.SGD(lr=0.01),
               metrics=["accuracy"])


############################################
#       Training Neural Networks           #
############################################
x_train, x_test, y_train, y_test = train_test_split(train_data, train_lbl, random_state=0, test_size=0.20)

1
res1 = model1.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=5, batch_size=200, verbose=1)

2
res2 = model2.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=10, batch_size=200, verbose=1)

# 3
res3 = model3.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=10, batch_size=200, verbose=1)

# 4
res4 = model4.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=10, batch_size=200, verbose=1)


###################################
#       Plots and Scores          #
###################################

# 1
print_report(model1, x_test, y_test, res1, "Part 1")
print_report(model2, x_test, y_test, res2, "Part 2")
print_report(model3, x_test, y_test, res3, "Part 3")
print_report(model4, x_test, y_test, res4, "Part 4")

#############################################################
#       Constructing Convolutional Neural Network           #
#############################################################

# 1
model_cnn = keras.Sequential()
model_cnn.add(Conv2D(64, (5, 5), strides=2, activation='relu', input_shape=(28, 28, 1)))
model_cnn.add(MaxPooling2D(pool_size=(2, 2), strides=1))
model_cnn.add(Conv2D(64, (5, 5), activation='relu', strides=1))
model_cnn.add(MaxPooling2D(pool_size=(2, 2), strides=1))
model_cnn.add(Conv2D(128, (5, 5), activation='relu', strides=1))
model_cnn.add(Conv2D(128, (5, 5), activation='relu', strides=1, border_mode='same'))
model_cnn.add(Flatten())
model_cnn.add(Dense(256, activation='relu'))
model_cnn.add(Dense(10, activation='softmax'))

model_cnn.compile(loss=keras.losses.categorical_crossentropy,
                  optimizer=keras.optimizers.adam(lr=0.001),
                  metrics=["accuracy"])

# 2

augment_size = 5000

image_gen = ImageDataGenerator(
        featurewise_center=False,
        samplewise_center=False,
        featurewise_std_normalization=False,
        samplewise_std_normalization=False,
        zca_whitening=False,
        rotation_range=10,
        zoom_range = 0.1,
        width_shift_range=0.1,
        height_shift_range=0.1,
        horizontal_flip=False,
        vertical_flip=False)

image_gen.fit(x_train, augment=True)

randidx = np.random.randint(len(x_train), size=augment_size)
x_augmented = x_train[randidx].copy()
y_augmented = y_train[randidx].copy()
x_augmented = image_gen.flow(x_augmented, np.zeros(augment_size),
                            batch_size=augment_size, shuffle=False).next()[0]

x_train = np.concatenate((x_train, x_augmented))
y_train = np.concatenate((y_train, y_augmented))

model_cnn2 = keras.Sequential()

model_cnn2.add(Conv2D(32, (3, 3), input_shape=(28, 28, 1)))
model_cnn2.add(Activation('relu'))
BatchNormalization(axis=-1)
model_cnn2.add(Conv2D(32, (3, 3)))
model_cnn2.add(Activation('relu'))
model_cnn2.add(MaxPooling2D(pool_size=(2, 2)))

BatchNormalization(axis=-1)
model_cnn2.add(Conv2D(64, (3, 3)))
model_cnn2.add(Activation('relu'))
BatchNormalization(axis=-1)
model_cnn2.add(Conv2D(64, (3, 3)))
model_cnn2.add(Activation('relu'))
model_cnn2.add(MaxPooling2D(pool_size=(2, 2)))

model_cnn2.add(Flatten())
# Fully connected layer

BatchNormalization()
model_cnn2.add(Dense(512))
model_cnn2.add(Activation('relu'))
BatchNormalization()
model_cnn2.add(Dropout(0.2))
model_cnn2.add(Dense(10))

model_cnn2.add(Activation('softmax'))

model_cnn2.compile(loss=keras.losses.categorical_crossentropy,
                   optimizer=keras.optimizers.adam(lr=0.001),
                   metrics=['accuracy'])
################################
#       Training CNN           #
################################

# 1
res = model_cnn.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=7, batch_size=128, verbose=1)

# 2
res2 = model_cnn2.fit(x_train, y_train, validation_data=(x_test, y_test), epochs=10, batch_size=128, verbose=1)

###################################
#       Plots and Scores          #
###################################

# 1
predictions = print_report(model_cnn, x_test, y_test, res, "CNN1")
# finding some wrong predictions
num = 3

predict = predictions.argmax(axis=1)
real = y_test.argmax(axis=1)

for i in range(10):
    k = 0
    for j in range(len(y_test)):
        if predict[j] != real[j] and real[j] == i:
            imgplot = plt.imshow(x_test[j].reshape((28, 28)))
            plt.title("real :" + chr(ord('A') + i) + "    Predict: " + chr(ord('A') + predict[j]))
            plt.show()

            k += 1

        if k == num:
            break

print()

# 2
print_report(model_cnn2, x_test, y_test, res2, "CNN2")


