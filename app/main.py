#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import sys

from PyQt5.QtGui import QFont
from PyQt5.QtWidgets import QApplication

from app.main_window import MainWindow

if __name__ == '__main__':
    app = QApplication(sys.argv)
    font = QFont("Century Gothic", 10)
    app.setFont(font)
    main_window = MainWindow()
    sys.exit(app.exec_())
