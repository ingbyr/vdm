# !/usr/bin/env python3
# -*- coding: utf-8 -*-


import sys
from app.ui.icon_rc import *
from PyQt5.QtWidgets import QApplication, QMainWindow, QWidget
from app.ui.main_window import MainWindow
from app.ui.ui_about_form import Ui_AboutForm

__author__ = 'InG_byr'

if __name__ == '__main__':
    app = QApplication(sys.argv)
    mainWindow = QMainWindow()
    ui = MainWindow()
    mainWindow = QMainWindow()
    ui.setupUi(mainWindow)
    ui.set_slot()
    mainWindow.show()
    wd = QWidget()
    about_ui = Ui_AboutForm()
    about_ui.setupUi(wd)
    wd.show()
    sys.exit(app.exec_())
