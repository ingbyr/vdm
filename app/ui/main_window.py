# !/usr/bin/env python3
# -*- coding: utf-8 -*-
import json
from PyQt5 import QtCore
from urllib import request

import sys

from PyQt5.QtCore import QUrl
from PyQt5.QtGui import QDesktopServices

from app.ui.ui_main_window import Ui_MainWindow
from app.util.download_thread import GetVideoInfoThread
from app import mlog, mconfig

from PyQt5.QtWidgets import QMainWindow, QFileDialog, QMessageBox, QDesktopWidget
from app.ui.about_widget import AboutWdiget
from app.ui.files_list_dialog import FilesListDialog


class MainWindow(Ui_MainWindow):
    def __init__(self):
        super().__init__()
        self.main_window = QMainWindow()
        self.setupUi(self.main_window)
        self.main_window.setFixedSize(self.main_window.width(), self.main_window.height())
        self.msg = QMessageBox()
        self.set_slot()
        self.qr = self.main_window.frameGeometry()  # move to center of screen
        self.cp = QDesktopWidget().availableGeometry().center()
        self.qr.moveCenter(self.cp)
        self.main_window.move(self.qr.topLeft())
        self.text_edit_urls.setAcceptRichText(False)

    def set_slot(self):
        self.button_download.clicked.connect(self.get_info)
        self.action_about.triggered.connect(self.show_about)
        self.action_file_path.triggered.connect(self.set_file_path)
        self.action_check_for_updates.triggered.connect(self.check_for_updates)
        self.action_report_bugs.triggered.connect(self.report_bugs)

    def get_info(self):
        mconfig.set_default()
        self.button_download.setEnabled(False)
        self.urls = (str(self.text_edit_urls.toPlainText())).split(';')
        mlog.debug(self.urls[0])

        self.m_thread = GetVideoInfoThread(self.urls, **mconfig.kwargs)
        self.m_thread.finish_signal.connect(self.finish_get_info)
        self.m_thread.start()

    def finish_get_info(self, ls, can_download):
        mlog.debug('finish_get_info: ' + str(can_download))
        self.button_download.setEnabled(True)
        if can_download:
            self.files_list_dialog = FilesListDialog()
            self.files_list_dialog.update_files_list(ls)
            mconfig.set_urls(self.urls)
        else:
            self.show_msg(QMessageBox.Critical, 'Failed ', 'Can not get the files list (╯°Д°)╯︵ ┻━┻')

    def show_about(self):
        mlog.debug('show about widget')
        self.about_widget = AboutWdiget()
        self.about_widget.about_widget.move(self.qr.topLeft())
        self.about_widget.about_widget.show()

    def set_file_path(self):
        fname = QFileDialog.getExistingDirectory(self.main_window, caption='Select Path', directory='',
                                                 options=QFileDialog.ShowDirsOnly)
        if fname:
            mconfig.set_file_path(fname)
            mlog.debug('changed file path to ' + mconfig.get_file_path())
            self.show_msg(QMessageBox.Information, 'Tip', 'Changed file path to:\n' + str(fname))
        else:
            mconfig.set_file_path(mconfig.base_dir)
            self.show_msg(QMessageBox.Information, 'Tip', 'Default file path:\n' + str(mconfig.base_dir))

    def show_msg(self, icon, title, text):
        self.msg.setWindowTitle(title)
        self.msg.setIcon(icon)
        self.msg.setText(text)
        self.msg.setStandardButtons(QMessageBox.Ok)
        self.msg.show()

    def check_for_updates(self):
        mlog.debug('check_for_updates')
        try:
            with request.urlopen('https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/version.json') as f:
                raw_inf = str(f.read())[2:-1]
                mlog.debug(str(f.read())[2:-1])
                remote_inf = json.loads(raw_inf)
                mlog.debug('remote version is ' + remote_inf['version'])
        except Exception:
            for item in sys.exc_info():
                mlog.error(str(item))
                self.show_msg(QMessageBox.Critical, 'Failed', 'Check for updates failed')
            return

        if mconfig.version >= remote_inf['version']:
            self.show_msg(QMessageBox.Information, 'Check for updates', 'No available updates')
        else:
            self.show_msg(QMessageBox.Information, 'Check for updates', 'There is a new version')
            self.do_updates()

    def do_updates(self):
        QDesktopServices.openUrl(QUrl('http://www.ingbyr.tk/2016/05/16/youget/'))

    def report_bugs(self):
        QDesktopServices.openUrl(QUrl('https://github.com/ingbyr/GUI-YouGet/issues'))
