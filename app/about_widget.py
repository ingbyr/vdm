#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os

from PyQt5.QtCore import Qt
from PyQt5.QtGui import QPixmap, QIcon
from PyQt5.QtWidgets import QWidget
from PyQt5.uic import loadUi


class AboutWiget(QWidget):
    def __init__(self):
        super().__init__()
        self.init_ui()

    def init_ui(self):
        self.about_widget = loadUi(os.path.join("ui", "about_widget.ui"), self)
        self.setWindowIcon(QIcon(os.path.join("imgs", "logo.jpg")))
        self.label_logo.setScaledContents(True)
        self.label_logo.setPixmap(QPixmap(os.path.join("imgs", "logo.jpg")))
        self.setAttribute(Qt.WA_QuitOnClose, False)