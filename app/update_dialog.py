#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os

from PyQt5.QtCore import Qt, QUrl
from PyQt5.QtGui import QIcon, QDesktopServices
from PyQt5.QtWidgets import QDialog
from PyQt5.uic import loadUi

from app import config, log, save_config
from app.utils import CheckUpdateThread, UpdateCoreThread


class UpdateDialog(QDialog):
    def __init__(self):
        super().__init__()
        self.update_dialog = loadUi(os.path.join(os.getcwd(), "ui", "update_dialog.ui"), self)
        self.setAttribute(Qt.WA_QuitOnClose, False)
        self.init_ui()

    def init_ui(self):
        self.setWindowIcon(QIcon(os.path.join(os.getcwd(), "imgs", "logo.jpg")))

        # disable the update buttons
        self.update_app_button.setDisabled(True)
        self.update_core_button.setDisabled(True)

        # set slots
        self.cancle_button.clicked.connect(self.close)
        self.update_app_button.clicked.connect(self.update_app)
        self.update_core_button.clicked.connect(self.update_core)

    def check_update(self):
        self.check_update_thread = CheckUpdateThread()
        self.check_update_thread.finish_signal.connect(self.finish_checking_update)
        self.check_update_thread.start()

    def finish_checking_update(self, remote_inf):
        if config["app"]["version"] >= remote_inf["version"]:
            self.app_update_label.setText("No available updates")
        else:
            self.app_update_label.setText("new version " + str(remote_inf["version"]))
            self.update_app_button.setDisabled(False)

        if config["core"]["youget_version"] >= remote_inf["you-get-core-version"]:
            self.core_update_label.setText("No available updates")
        else:
            self.remote_youget_core_version = str(remote_inf["you-get-core-version"])
            self.core_update_label.setText("new version " + self.remote_youget_core_version)
            self.update_core_url = remote_inf["core-url"]
            self.update_core_button.setDisabled(False)

    def update_app(self):
        QDesktopServices.openUrl(QUrl("https://github.com/ingbyr/GUI-YouGet/releases"))

    def update_core(self):
        log.debug("start update core thread")
        self.update_core_button.setDisabled(True)
        self.update_core_thread = UpdateCoreThread(os.path.join(os.getcwd(), "core", config["core"]["youget"]),
                                                   self.update_core_url, self.update_core_cbf)
        self.update_core_thread.finish_signal.connect(self.finish_updating_core)
        self.update_core_thread.start()

    def update_core_cbf(self, blocknum, blocksize, totalsize):
        """
        Callback function for the update core thread
        """
        percent = int(100.0 * blocknum * blocksize / totalsize)
        print(str(percent))
        self.progress_bar.setValue(percent)

    def finish_updating_core(self, succeeded):
        if succeeded:
            self.update_core_button.setDisabled(True)
            save_config("core", "youget_version", self.remote_youget_core_version)
        else:
            self.update_core_button.setDisabled(False)
            self.progress_bar.setValue(0)
