#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os

from PyQt5.QtCore import QUrl
from PyQt5.QtGui import QDesktopServices, QIcon
from PyQt5.QtWidgets import QMainWindow, QFileDialog, QMessageBox
from PyQt5.uic import loadUi

from app.about_widget import AboutWiget
from app.file_list_dialog import FileListDialog
from app import config, log
from app.utils import save_config, CheckUpdateThread


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.main_window = loadUi(os.path.join("ui", "main_window.ui"), self)
        self.msg_box = QMessageBox()
        self.init_ui()

    def init_ui(self):
        # read the settings
        self.file_path_label.setText(config["common"]["out_put_dir"])
        self.setWindowIcon(QIcon(os.path.join("imgs", "logo.jpg")))

        self.show()

        # Set slot
        self.button_download.clicked.connect(self.download_media)
        self.set_path_button.clicked.connect(self.set_file_path)
        self.check_update_button.clicked.connect(self.check_update)
        self.about_button.clicked.connect(self.show_about)
        # self.set_proxy_button.clicked.connect(self.show_proxy_dialog)
        # self.proxy_checkBox.stateChanged.connect(self.save_config)
        self.action_about.triggered.connect(self.show_about)
        self.action_file_path.triggered.connect(self.set_file_path)
        self.action_check_for_updates.triggered.connect(self.check_update)
        self.action_report_bugs.triggered.connect(self.report_bugs)
        self.action_supported_sites.triggered.connect(self.get_supported_sites)

    def download_media(self):
        url = self.main_window.text_edit_urls.toPlainText()
        self.file_list_dialog = FileListDialog(url)

    def set_file_path(self):
        fname = QFileDialog.getExistingDirectory(self.main_window, caption="Select Path", directory="",
                                                 options=QFileDialog.ShowDirsOnly)
        if fname:
            config["common"]["out_put_dir"] = fname
            save_config(config)
            self.file_path_label.setText(fname)
        else:
            self.file_path_label.setText(config["common"]["out_put_dir"])

    def get_supported_sites(self):
        QDesktopServices.openUrl(QUrl("https://github.com/ingbyr/GUI-YouGet/wiki/Supported-Sites"))

    def show_about(self):
        self.about_widget = AboutWiget()
        self.about_widget.about_widget.show()

    def check_update(self):
        self.show_msg(QMessageBox.Information, "Check for updates", "Checking...")

        # start thread for checking updates
        self.check_update_thread = CheckUpdateThread()
        self.check_update_thread.finish_signal.connect(self.finish_checking_update)
        self.check_update_thread.start()

    def finish_checking_update(self, remote_inf):
        log.debug("local_version: " + config["common"]["version"])
        log.debug("remote_inf: " + remote_inf["version"])
        if config["common"]["version"] >= remote_inf["version"]:
            self.msg_box.setText("No available updates")
        else:
            self.msg_box.setText("There is a new version : " + remote_inf["version"])
            self.do_updates()

    def do_updates(self):
        # todo auto download latest version auto
        QDesktopServices.openUrl(QUrl("https://github.com/ingbyr/GUI-YouGet/releases"))

    def report_bugs(self):
        QDesktopServices.openUrl(QUrl("https://github.com/ingbyr/GUI-YouGet/issues"))

    def get_more_infomation(self):
        QDesktopServices.openUrl(QUrl("https://github.com/ingbyr/GUI-YouGet"))

    def get_supported_sites(self):
        QDesktopServices.openUrl(QUrl("https://github.com/ingbyr/GUI-YouGet/wiki/Supported-Sites"))

    def show_msg(self, icon, title, text):
        self.msg_box.setWindowTitle(title)
        self.msg_box.setWindowIcon(QIcon(os.path.join("imgs", "logo.jpg")))
        self.msg_box.setIcon(icon)
        self.msg_box.setText(text)
        self.msg_box.setStandardButtons(QMessageBox.Ok)
        self.msg_box.show()
