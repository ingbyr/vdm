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
        self.dialog = loadUi(os.path.join("ui", "files_list_dialog.ui"), self)
        self.setAttribute(Qt.WA_QuitOnClose, False)
        self.msg = QMessageBox()
        self.init_ui()

    @property
    def url(self):
        return self._url

    @url.setter
    def url(self, value):
        self._url = value

    def init_ui(self):
        default_item = QListWidgetItem(self.tr("Loading..."))
        self.list_widget.addItem(default_item)
        self.setWindowIcon(QIcon(os.path.join("imgs", "logo.jpg")))

    def get_media_info(self):
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
        self.download_thread = DowloadMediaThread(tag, "-o", config["common"]["out_put_dir"]
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
        log.debug("clean the threads")
        # todo fix this thread quit error
        if self.info_thread.isRunning():
            try:
                log.debug("clean the info thread")
                self.info_thread.quit()
            except Exception as e:
                log.error("Clean info thread failed")
                log.exception(e)
        if self.download_thread.isRunning():
            self.download_thread.quit()
