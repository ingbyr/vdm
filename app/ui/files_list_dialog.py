# !/usr/bin/env python3
# -*- coding: utf-8 -*-

from app.ui.ui_files_list_dialog import Ui_FilesListDialog
from PyQt5.QtWidgets import QDialog, QMessageBox
from app import mlog, mconfig
from app.util.download_thread import DownloadThread

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
        if (options in mconfig.streams) or options is None or options == '':
            mconfig.set_file_itag(options)
        else:
            self.show_msg(QMessageBox.Warning, 'Bad options', 'The [options] may be in blue text:\n'
                                                              'Option is [options]')
            return

        self.download_thread = DownloadThread(mconfig.get_urls(), **mconfig.kwargs)
        self.download_thread.finish_signal.connect(self.finish_download)
        self.download_thread.start()

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
