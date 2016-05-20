# !/usr/bin/env python3
# -*- coding: utf-8 -*-

from app.ui.ui_files_list_dialog import Ui_FilesListDialog
from app import mlog, mconfig
from app.util.download_thread import DownloadThread
import app.custom_you_get.status as status
from PyQt5.QtGui import *
from PyQt5.QtCore import *
from PyQt5.QtWidgets import *

__author__ = 'InG_byr'


class FilesListDialog(Ui_FilesListDialog):
    def __init__(self):
        super().__init__()
        self.files_list_dialog = QDialog()
        self.setupUi(self.files_list_dialog)
        self.files_list_dialog.show()
        self.msg = QMessageBox()
        self.set_slot()

    def set_slot(self):
        self.push_button_confirm.clicked.connect(self.start_download_files)

    def update_files_list(self, files_list):
        self.text_files_list.insertHtml(files_list)

    def start_download_files(self):
        options = str(self.line_edit_options.text()).strip(' ')
        if (options in mconfig.get_streams()) or options is None or options == '':
            mconfig.set_file_itag(options)
        else:
            self.show_msg(QMessageBox.Warning, 'Bad options', 'The [options] may be in blue text:\n'
                                                              'Option is [options]')
            return

        self.download_thread = DownloadThread(mconfig.get_urls(), **mconfig.kwargs)
        self.download_thread.finish_signal.connect(self.finish_download)
        self.download_thread.start()

        self.show_progress_bar()

    def show_msg(self, icon, title, text):
        self.msg.setWindowTitle(title)
        self.msg.setIcon(icon)
        self.msg.setText(text)
        self.msg.setStandardButtons(QMessageBox.Ok)
        self.msg.show()

    def finish_download(self, is_succeed):
        if is_succeed:
            self.show_msg(QMessageBox.Information, 'completed',
                          'Download completed! Files are in:\n' + mconfig.get_file_path())
        else:
            self.show_msg(QMessageBox.Critical, 'Failed', 'Download failed!')

    def show_progress_bar(self):
        percent = 0
        is_exits = False
        result = ''
        progressDialog = QProgressDialog(self.files_list_dialog)
        progressDialog.setAutoReset(True)
        progressDialog.setWindowModality(Qt.WindowModal)
        progressDialog.setMinimumDuration(5)
        progressDialog.setWindowTitle('Downloading')
        progressDialog.setLabelText('Current speed: ')
        progressDialog.setCancelButtonText('Cancel')
        progressDialog.setRange(0, 100)

        while percent < 100 and not is_exits:
            percent = status.get_percent()
            is_exits = status.get_exist()
            if is_exits:
                result = 'Files already exists'
                percent = 100
            progressDialog.setValue(percent)
            progressDialog.setLabelText('Current speed: ' + str(status.get_speed()))
            QThread.msleep(100)
            if progressDialog.wasCanceled():
                pass
                # todo: can not cancel
                status.set_stop_thread(True)
                self.download_thread.wait()
                mlog.debug('stop the download thread')
                mlog.debug('download_thread.isRunning ' + str(self.download_thread.isRunning()))
                percent = 100
                result = 'stop by user'
