# !/usr/bin/env python3
# -*- coding: utf-8 -*-


import sys
from app.ui.icon_rc import *
from PyQt5.QtWidgets import QApplication, QMainWindow, QWidget
from app.ui.main_window import MainWindow
from app.ui.about_widget import AboutWdiget

__author__ = 'InG_byr'

if __name__ == '__main__':
    app = QApplication(sys.argv)
    # mainWindow = QMainWindow()
    # ui = MainWindow()
    # mainWindow = QMainWindow()
    # ui.setupUi(mainWindow)
    # ui.set_slot()
    # mainWindow.show()
    ui = MainWindow()
    ui.main_window.show()
    about_ui = AboutWdiget()
    about_ui.about_widget.show()
    # wd = QWidget()
    # about_ui = AboutWdiget()
    # about_ui.setupUi(wd)
    # wd.show()
    sys.exit(app.exec_())
