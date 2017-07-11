#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os

from PyQt5.QtWidgets import QDialog
from PyQt5.uic import loadUi

from app.youget_helper import GetVideoInfoThread


class FileListDialog(QDialog):
    def __init__(self, url):
        super().__init__()
        self.url = url
        self.dialog = loadUi(os.path.join("ui", "files_list_dialog.ui"), self)
        self.init_ui()

    def init_ui(self):
        self.dialog.text_files_list.setPlaceholderText("Loading...")
        self.dialog.show()

        self.m_thread = GetVideoInfoThread("-i", self.url)
        self.m_thread.finish_signal.connect(self.show_info)
        self.m_thread.start()

    def show_info(self, result):
        self.dialog.text_files_list.setText(result)
