#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os

from PyQt5.QtCore import Qt
from PyQt5.QtGui import QIcon
from PyQt5.QtWidgets import QDialog, QRadioButton
from PyQt5.uic import loadUi

from app import config


class ProxyDialog(QDialog):
    def __init__(self):
        super().__init__()
        self.init_ui()

    def init_ui(self):
        self.proxy_dialog = loadUi(os.path.join("ui", "proxy_dialog.ui"), self)
        self.setWindowIcon(QIcon(os.path.join("imgs", "logo.jpg")))
        self.setAttribute(Qt.WA_QuitOnClose, False)
        self.load_settings()

    def load_settings(self):
        self.ip_text_edit.setText(config["common"]["proxy_ip"])
        self.port_text_edit.setText(config["common"]["proxy_port"])

        # only http proxy is available on windows, need you-get support this
        self.http_checkbox.setChecked(True)
        self.socks5_checkbox.setCheckable(False)
