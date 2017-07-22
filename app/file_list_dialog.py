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
from app.core_cli_helper import GetMediaInfoThread, get_media_tag, DowloadMediaThread, format2str


class FileListDialog(QDialog):
    def __init__(self):
        super().__init__()
        self.dialog = loadUi(os.path.join(os.getcwd(), "ui", "files_list_dialog.ui"), self)
        self.setAttribute(Qt.WA_QuitOnClose, False)
        self.init_ui()

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
        self.info_thread = GetMediaInfoThread("-j", self._url)
        self.info_thread.finish_signal.connect(self.show_info)
        self.info_thread.start()

    def show_info(self, options):
        self.list_widget.clear()
        item = QListWidgetItem(self.tr('Title: ' + options['title']))
        self.dialog.list_widget.addItem(item)

        for format in options['formats']:
            item = QListWidgetItem()
            item.setText(format2str(format))
            self.dialog.list_widget.addItem(item)

        self.list_widget.itemClicked.connect(self.start_download)

    def start_download(self, item):
        tag = get_media_tag(item.text())
        self.download_thread = DowloadMediaThread('-f', tag,
                                                  "-o", config["common"]["output_dir"] + '/%(title)s-%(id)s.%(ext)s'
                                                  , self.url)
        self.download_thread.finish_signal.connect(self.downloaded)
        self.download_thread.start()

    def downloaded(self, output):
        print(output)
        print('Downloaded!!!')

    def closeEvent(self, QCloseEvent):
        if self.info_thread and self.info_thread.isRunning():
            self.info_thread.quit()
            log.debug("clean the info_thread")
        if self.download_thread and self.download_thread.isRunning():
            self.download_thread.quit()
            log.debug("clean the download_thread")
