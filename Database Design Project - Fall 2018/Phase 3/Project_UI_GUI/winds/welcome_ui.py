# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'welcome.ui'
#
# Created by: PyQt5 UI code generator 5.10.1
#
# WARNING! All changes made in this file will be lost!
import threading

from PyQt5 import QtCore, QtGui, QtWidgets
import passenger_connections as psg
aaa = None


def aaa_set(ex):
    aaa = ex


class Ui_welcome(object):
    def setupUi(self, welcome):
        # main window
        welcome.setObjectName("welcome")
        welcome.setWindowModality(QtCore.Qt.NonModal)
        welcome.setEnabled(True)
        welcome.resize(1306, 906)
        sizePolicy = QtWidgets.QSizePolicy(QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Expanding)
        sizePolicy.setHorizontalStretch(0)
        sizePolicy.setVerticalStretch(0)
        sizePolicy.setHeightForWidth(welcome.sizePolicy().hasHeightForWidth())
        welcome.setSizePolicy(sizePolicy)
        welcome.setMinimumSize(QtCore.QSize(1306, 906))
        welcome.setMaximumSize(QtCore.QSize(1306, 906))
        welcome.setMouseTracking(True)
        welcome.setFocusPolicy(QtCore.Qt.StrongFocus)
        welcome.setToolTip("")
        welcome.setStyleSheet(" background-image: url(ui/welcome_background.jpg);\n"
                              " background-repeat: norepeat;")
        welcome.setAnimated(True)
        welcome.setTabShape(QtWidgets.QTabWidget.Rounded)
        self.centralwidget = QtWidgets.QWidget(welcome)
        self.centralwidget.setObjectName("centralwidget")
        # loading
        self.loading = QtWidgets.QLabel(self.centralwidget)
        self.loading.setEnabled(True)
        self.loading.setGeometry(QtCore.QRect(-90, 615, 500, 500))
        self.loading.setMouseTracking(False)
        self.loading.setStyleSheet("background: transparent;")
        self.loading.setText("")
        movie = QtGui.QMovie("ui/loading.gif")
        self.loading.setMovie(movie)
        movie.start()
        self.loading.setScaledContents(True)
        self.loading.setObjectName("req_car_eco")
        self.loading.hide()
        # sign up btn
        self.sign_up_btn = QtWidgets.QPushButton(self.centralwidget)
        self.sign_up_btn.setGeometry(QtCore.QRect(180, 720, 470, 71))
        font = QtGui.QFont()
        font.setPointSize(12)
        self.sign_up_btn.setFont(font)
        self.sign_up_btn.setCursor(QtGui.QCursor(QtCore.Qt.PointingHandCursor))
        self.sign_up_btn.setAutoFillBackground(False)
        self.sign_up_btn.setStyleSheet("background:rgba(100, 100, 100, 100); color: white")
        self.sign_up_btn.setIconSize(QtCore.QSize(90, 90))
        self.sign_up_btn.setAutoRepeat(False)
        self.sign_up_btn.setObjectName("sign_up_btn")
        # log in btn
        self.log_in_btn = QtWidgets.QPushButton(self.centralwidget)
        self.log_in_btn.setGeometry(QtCore.QRect(720, 720, 470, 71))
        font = QtGui.QFont()
        font.setPointSize(12)
        self.log_in_btn.setFont(font)
        self.log_in_btn.setCursor(QtGui.QCursor(QtCore.Qt.PointingHandCursor))
        self.log_in_btn.setAutoFillBackground(False)
        self.log_in_btn.setStyleSheet("background:rgba(100, 100, 100, 100); color: white")
        self.log_in_btn.setIconSize(QtCore.QSize(90, 90))
        self.log_in_btn.setAutoRepeat(False)
        self.log_in_btn.setObjectName("log_in_btn")
        # sign up grid
        self.gridLayoutWidget = QtWidgets.QWidget(self.centralwidget)
        self.gridLayoutWidget.setGeometry(QtCore.QRect(180, 100, 471, 611))
        self.gridLayoutWidget.setObjectName("gridLayoutWidget")
        self.gridLayout = QtWidgets.QGridLayout(self.gridLayoutWidget)
        self.gridLayout.setSizeConstraint(QtWidgets.QLayout.SetNoConstraint)
        self.gridLayout.setContentsMargins(0, 0, 0, 0)
        self.gridLayout.setVerticalSpacing(20)
        self.gridLayout.setObjectName("gridLayout")
        self.horizontalLayout = QtWidgets.QHBoxLayout()
        self.horizontalLayout.setObjectName("horizontalLayout")
        # gender check box
        self.woman = QtWidgets.QRadioButton(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.woman.setFont(font)
        self.woman.setStyleSheet("background:transparent")
        self.woman.setObjectName("woman")
        self.horizontalLayout.addWidget(self.woman)
        self.man = QtWidgets.QRadioButton(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.man.setFont(font)
        self.man.setStyleSheet("background:transparent")
        self.man.setObjectName("man")
        self.horizontalLayout.addWidget(self.man)
        self.gridLayout.addLayout(self.horizontalLayout, 5, 0, 1, 1)
        # lbls
        self.label_6 = QtWidgets.QLabel(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_6.setFont(font)
        self.label_6.setStyleSheet("background:transparent")
        self.label_6.setObjectName("label_6")
        self.gridLayout.addWidget(self.label_6, 5, 1, 1, 1)
        self.label_5 = QtWidgets.QLabel(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_5.setFont(font)
        self.label_5.setStyleSheet("background:transparent")
        self.label_5.setObjectName("label_5")
        self.gridLayout.addWidget(self.label_5, 4, 1, 1, 1)
        spacerItem = QtWidgets.QSpacerItem(20, 40, QtWidgets.QSizePolicy.Minimum, QtWidgets.QSizePolicy.Expanding)
        self.gridLayout.addItem(spacerItem, 7, 0, 1, 1)
        # birth date
        self.birthDay = QtWidgets.QLineEdit(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.birthDay.setFont(font)
        self.birthDay.setStyleSheet("background: transparent;\n"
                                    "mouse-over-background-color: yellow;")
        self.birthDay.setText("")
        self.birthDay.setObjectName("birthDay")
        self.gridLayout.addWidget(self.birthDay, 4, 0, 1, 1)
        # first name
        self.fname = QtWidgets.QLineEdit(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.fname.setFont(font)
        self.fname.setStyleSheet("background: transparent;\n"
                                 "mouse-over-background-color: yellow;")
        self.fname.setText("")
        self.fname.setObjectName("fname")
        self.gridLayout.addWidget(self.fname, 0, 0, 1, 1)
        # lbls
        self.label_2 = QtWidgets.QLabel(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_2.setFont(font)
        self.label_2.setStyleSheet("background:transparent")
        self.label_2.setObjectName("label_2")
        self.gridLayout.addWidget(self.label_2, 1, 1, 1, 1)
        self.label_3 = QtWidgets.QLabel(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_3.setFont(font)
        self.label_3.setStyleSheet("background:transparent")
        self.label_3.setObjectName("label_3")
        self.gridLayout.addWidget(self.label_3, 2, 1, 1, 1)
        self.label = QtWidgets.QLabel(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label.setFont(font)
        self.label.setStyleSheet("background:transparent")
        self.label.setObjectName("label")
        self.gridLayout.addWidget(self.label, 0, 1, 1, 1)
        # phone sign up
        self.phone_sign_up = QtWidgets.QLineEdit(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.phone_sign_up.setFont(font)
        self.phone_sign_up.setStyleSheet("background: transparent;\n"
                                         "mouse-over-background-color: yellow;")
        self.phone_sign_up.setText("")
        self.phone_sign_up.setObjectName("phone_sign_up")
        self.gridLayout.addWidget(self.phone_sign_up, 2, 0, 1, 1)
        # last Name
        self.Lname = QtWidgets.QLineEdit(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.Lname.setFont(font)
        self.Lname.setStyleSheet("background: transparent;\n"
                                 "mouse-over-background-color: yellow;")
        self.Lname.setText("")
        self.Lname.setObjectName("Lname")
        self.gridLayout.addWidget(self.Lname, 1, 0, 1, 1)
        # lbls
        self.label_4 = QtWidgets.QLabel(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_4.setFont(font)
        self.label_4.setStyleSheet("background:transparent")
        self.label_4.setObjectName("label_4")
        self.gridLayout.addWidget(self.label_4, 3, 1, 1, 1)
        # mails
        self.email = QtWidgets.QLineEdit(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.email.setFont(font)
        self.email.setStyleSheet("background: transparent;\n"
                                 "mouse-over-background-color: yellow;")
        self.email.setText("")
        self.email.setObjectName("email")
        self.gridLayout.addWidget(self.email, 3, 0, 1, 1)
        # lbls
        self.label_12 = QtWidgets.QLabel(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_12.setFont(font)
        self.label_12.setStyleSheet("background:transparent")
        self.label_12.setObjectName("label_12")
        self.gridLayout.addWidget(self.label_12, 6, 1, 1, 1)
        # pass sign up
        self.pass_sign_up = QtWidgets.QLineEdit(self.gridLayoutWidget)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.pass_sign_up.setFont(font)
        self.pass_sign_up.setStyleSheet("background: transparent;\n"
                                        "mouse-over-background-color: yellow;")
        self.pass_sign_up.setText("")
        self.pass_sign_up.setObjectName("pass_sign_up")
        # layout
        self.gridLayout.addWidget(self.pass_sign_up, 6, 0, 1, 1)
        self.gridLayoutWidget_2 = QtWidgets.QWidget(self.centralwidget)
        self.gridLayoutWidget_2.setGeometry(QtCore.QRect(720, 100, 471, 611))
        self.gridLayoutWidget_2.setObjectName("gridLayoutWidget_2")
        self.gridLayout_2 = QtWidgets.QGridLayout(self.gridLayoutWidget_2)
        self.gridLayout_2.setSizeConstraint(QtWidgets.QLayout.SetNoConstraint)
        self.gridLayout_2.setContentsMargins(0, 0, 0, 0)
        self.gridLayout_2.setVerticalSpacing(20)
        self.gridLayout_2.setObjectName("gridLayout_2")
        spacerItem1 = QtWidgets.QSpacerItem(20, 40, QtWidgets.QSizePolicy.Minimum, QtWidgets.QSizePolicy.Expanding)
        self.gridLayout_2.addItem(spacerItem1, 4, 0, 1, 1)
        # lbls
        self.label_10 = QtWidgets.QLabel(self.gridLayoutWidget_2)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_10.setFont(font)
        self.label_10.setStyleSheet("background:transparent")
        self.label_10.setObjectName("label_10")
        self.gridLayout_2.addWidget(self.label_10, 1, 1, 1, 1)
        # phone log in
        self.phone_log_in = QtWidgets.QLineEdit(self.gridLayoutWidget_2)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.phone_log_in.setFont(font)
        self.phone_log_in.setStyleSheet("background: transparent;\n"
                                        "mouse-over-background-color: yellow;")
        self.phone_log_in.setText("")
        self.phone_log_in.setObjectName("phone_log_in")
        self.gridLayout_2.addWidget(self.phone_log_in, 1, 0, 1, 1)
        # pass log in
        self.pass_log_in = QtWidgets.QLineEdit(self.gridLayoutWidget_2)
        font = QtGui.QFont()
        font.setPointSize(12)
        self.pass_log_in.setFont(font)
        self.pass_log_in.setStyleSheet("background: transparent;\n"
                                       "mouse-over-background-color: yellow;")
        self.pass_log_in.setText("")
        self.pass_log_in.setObjectName("pass_log_in")
        # lbls
        self.gridLayout_2.addWidget(self.pass_log_in, 3, 0, 1, 1)
        self.label_11 = QtWidgets.QLabel(self.gridLayoutWidget_2)
        font = QtGui.QFont()
        font.setPointSize(10)
        self.label_11.setFont(font)
        self.label_11.setStyleSheet("background:transparent")
        self.label_11.setObjectName("label_11")
        self.gridLayout_2.addWidget(self.label_11, 3, 1, 1, 1)
        spacerItem2 = QtWidgets.QSpacerItem(10, 20, QtWidgets.QSizePolicy.Minimum, QtWidgets.QSizePolicy.Fixed)
        self.gridLayout_2.addItem(spacerItem2, 0, 0, 1, 1)
        spacerItem3 = QtWidgets.QSpacerItem(10, 20, QtWidgets.QSizePolicy.Minimum, QtWidgets.QSizePolicy.Fixed)
        self.gridLayout_2.addItem(spacerItem3, 2, 0, 1, 1)

        # connects
        self.log_in_btn.clicked.connect(self.log_in_clicked)
        self.sign_up_btn.clicked.connect(self.sign_up_clicked)

        welcome.setCentralWidget(self.centralwidget)

        self.retranslateUi(welcome)
        QtCore.QMetaObject.connectSlotsByName(welcome)

    def retranslateUi(self, welcome):
        _translate = QtCore.QCoreApplication.translate
        self.sign_up_btn.setText(_translate("welcome", "sign up"))
        self.log_in_btn.setText(_translate("welcome", "log in"))
        self.woman.setText(_translate("welcome", "woman"))
        self.man.setText(_translate("welcome", "man"))
        self.label_6.setText(_translate("welcome", "Gender"))
        self.label_5.setText(_translate("welcome", "Birth day"))
        self.label_2.setText(_translate("welcome", "Lname"))
        self.label_3.setText(_translate("welcome", "Phone No."))
        self.label.setText(_translate("welcome", "Fname"))
        self.label_4.setText(_translate("welcome", "Email"))
        self.label_12.setText(_translate("welcome", "Pass"))
        self.label_10.setText(_translate("welcome", "Phone No."))
        self.label_11.setText(_translate("welcome", "Pass"))
        welcome.setWindowTitle(_translate("welcome", "welcome"))

    def loading_start(self):
        self.loading.show()

    def loading_finished(self):
        self.loading.hide()

    def sign_up_clicked(self):
        gender = None
        if self.man.isChecked():
            gender = 'M'
        if self.woman.isChecked():
            gender = 'F'
        if psg.sign_up(self.fname.text(), self.Lname.text(), self.phone_sign_up.text(),
                       self.email.text(), self.birthDay.text(), gender):
            # todo: user define beshe
            pass
        else:
            self.sign_up_btn.setStyleSheet("background:rgba(200, 100, 100, 150);")

    def log_in_clicked(self):
        if psg.login(self.phone_log_in.text()):
            # todo: user define beshe
            pass
        else:
            self.log_in_btn.setStyleSheet("background:rgba(200, 100, 100, 150);")
