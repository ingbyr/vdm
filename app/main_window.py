#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""

import os
import sys

from PyQt5.QtWidgets import QApplication, QMainWindow
from PyQt5.uic import loadUi

from app.file_list_dialog import FileListDialog
from app.youget_helper import GetVideoInfoThread


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.main_window = loadUi(os.path.join("ui", "main_window.ui"), self)
        self.init_ui()

    def init_ui(self):
        self.main_window.show()
        self.main_window.button_download.clicked.connect(self.download_media)

    def download_media(self):
        url = self.main_window.text_edit_urls.toPlainText()
        self.file_list_dialog = FileListDialog(url)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    mainw = MainWindow()
    sys.exit(app.exec_())
