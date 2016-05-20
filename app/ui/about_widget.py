# !/usr/bin/env python3
# -*- coding: utf-8 -*-
from PyQt5.QtWidgets import QWidget

from app.ui.ui_about_widget import Ui_AboutForm

__author__ = 'InG_byr'


class AboutWdiget(Ui_AboutForm):
    def __init__(self):
        super().__init__()
        self.about_widget = QWidget()
        self.setupUi(self.about_widget)
        self.replace_version()

    def replace_version(self):
        pass
