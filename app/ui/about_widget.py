# !/usr/bin/env python3
# -*- coding: utf-8 -*-
from PyQt5.QtWidgets import QWidget

from app.ui.ui_about_widget import Ui_AboutForm
from app import mconfig

__author__ = 'InG_byr'


class AboutWdiget(Ui_AboutForm):
    def __init__(self):
        super().__init__()
        self.about_widget = QWidget()
        self.setupUi(self.about_widget)
        self.set_version()

    def set_version(self):
        self.label_version.setText('Version ' + mconfig.version)
