#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os

from PyQt5.QtCore import Qt
from PyQt5.QtGui import QIcon
from PyQt5.QtWidgets import QDialog, QListWidgetItem, QMessageBox
from PyQt5.uic import loadUi

from app import log, config
from app.youget_helper import GetMediaInfoThread, get_media_args, DowloadMediaThread


class FileListDialog(QDialog):
    def __init__(self):
        super().__init__()
        self.dialog = loadUi(os.path.join(os.getcwd(), "ui", "files_list_dialog.ui"), self)
        self.setAttribute(Qt.WA_QuitOnClose, False)
        self.init_ui()

        self.msg = QMessageBox()
        self.info_thread = None
        self.download_thread = None

    @property
    def url(self):
        return self._url

    @url.setter
    def url(self, value):
        self._url = value

    def init_ui(self):
        default_item = QListWidgetItem(self.tr("Loading..."))
        self.list_widget.addItem(default_item)
        self.setWindowIcon(QIcon(os.path.join(os.getcwd(), "imgs", "logo.jpg")))

    def get_media_info(self):
        self.list_widget.clear()
        default_item = QListWidgetItem(self.tr("Loading..."))
        self.list_widget.addItem(default_item)
        self.info_thread = GetMediaInfoThread("-i", self._url)
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
        log.debug("tag: " + tag + "size: " + size)
        self.download_thread = DowloadMediaThread(tag, "-o", config["common"]["output_dir"]
                                                  , self.url)
        self.download_thread.finish_signal.connect(self.downloaded)
        self.download_thread.start()

    def downloaded(self, output):
        self.show_msg(output)

    def show_msg(self, text, title="Debug"):
        self.msg.setWindowTitle(title)
        self.msg.setText(text)
        self.msg.setStandardButtons(QMessageBox.Ok)
        self.msg.show()

    def closeEvent(self, QCloseEvent):
        if self.info_thread and self.info_thread.isRunning():
            self.info_thread.quit()
            log.debug("clean the info_thread")
        if self.download_thread and self.download_thread.isRunning():
            self.download_thread.quit()
            log.debug("clean the download_thread")
