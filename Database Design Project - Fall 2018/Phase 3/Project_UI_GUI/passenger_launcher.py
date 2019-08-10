import sys
from PyQt5.QtWidgets import QApplication, QMainWindow
from winds.welcome_ui import Ui_welcome, aaa_set
from winds.passenger_main_ui import Ui_mainWidow
from winds.on_trip import Ui_trip_window
import time
import threading


class Welcome(QMainWindow):

    def __init__(self):
        super().__init__()
        self.title = 'PyQt5 simple window - pythonspot.com'
        self.ui = Ui_welcome()
        self.ui.setupUi(self)
        self.show()

    def change_ui_to_main_window(self):
        self.ui = Ui_mainWidow()
        self.ui.setupUi(self)
        self.show()

    def change_ui_to_on_trip_window(self):
        self.ui = Ui_trip_window()
        self.ui.setupUi(self)
        self.show()


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = Welcome()
    aaa_set(ex)
    # ex.change_ui_to_main_window()
    # ex.change_ui_to_on_trip_window()
    sys.exit(app.exec_())
