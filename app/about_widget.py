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

from app import config


class AboutWiget(QWidget):
    def __init__(self):
        super().__init__()
        self.about_widget = loadUi(os.path.join(os.getcwd(), "ui", "about_widget.ui"), self)
        self.setAttribute(Qt.WA_QuitOnClose, False)
        self.init_ui()

    def init_ui(self):
        self.setWindowIcon(QIcon(os.path.join(os.getcwd(), "imgs", "logo.jpg")))
        self.label_logo.setScaledContents(True)
        self.label_logo.setPixmap(QPixmap(os.path.join(os.getcwd(), "imgs", "logo.jpg")))
        self.label_version.setText(config["app"]["version"])
        self.label_info.setOpenExternalLinks(True)
