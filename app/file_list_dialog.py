#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os

from PyQt5.QtWidgets import QDialog, QListWidgetItem, QMessageBox
from PyQt5.uic import loadUi

from app import log
from app.youget_helper import GetMediaInfoThread, get_media_args, DowloadMediaThread


class FileListDialog(QDialog):
    def __init__(self, url):
        super().__init__()
        self.url = url
        self.dialog = loadUi(os.path.join("ui", "files_list_dialog.ui"), self)
        self.init_ui()

    def init_ui(self):
        default_item = QListWidgetItem(self.tr("Loading..."))
        self.list_widget.addItem(default_item)
        self.show()

        self.info_thread = GetMediaInfoThread("-i", self.url)
        self.info_thread.finish_signal.connect(self.show_info)
        self.info_thread.start()

    def show_info(self, result):
        self.list_widget.clear()
        for op in result:
            item = QListWidgetItem(self.tr(op))
            self.dialog.list_widget.addItem(item)
        self.list_widget.itemClicked.connect(self.start_download)

    def start_download(self, item):
        self.show_msg(item.text())
        tag, size = get_media_args(item.text())
        log.debug(tag)
        log.debug(size)
        # self.download_thread = DowloadMediaThread(tag, self.url)
        # self.download_thread.finish_signal.connect(self.downloaded)
        # self.download_thread.start()

    def downloaded(self, output):
        self.show_msg(output)

    def show_msg(self, text, title="Debug"):
        self.msg = QMessageBox()
        self.msg.setWindowTitle(title)
        self.msg.setText(text)
        self.msg.setStandardButtons(QMessageBox.Ok)
        self.msg.show()
