# !/usr/bin/env python3
# -*- coding: utf-8 -*-


import sys
from app.ui.icon import *
from PyQt5.QtWidgets import QApplication, QMainWindow
from app.ui.main_window import Ui_MainWindow

__author__ = 'InG_byr'

if __name__ == '__main__':
    app = QApplication(sys.argv)
    mainWindow = QMainWindow()
    ui = Ui_MainWindow()
    mainWindow = QMainWindow()
    ui.setupUi(mainWindow)
    ui.set_slot()
    mainWindow.show()
    sys.exit(app.exec_())
