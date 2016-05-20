# !/usr/bin/env python3
# -*- coding: utf-8 -*-

from app.ui.ui_files_list_dialog import Ui_FilesListDialog
from PyQt5.QtWidgets import QDialog

__author__ = 'InG_byr'


class FilesListDialog(Ui_FilesListDialog):
    def __init__(self):
        super().__init__()
        self.files_list_dialog = QDialog()
        self.setupUi(self.files_list_dialog)
        self.files_list_dialog.show()

    def set_slot(self):
        pass

    def update_files_list(self, files_list):
        self.text_files_list.insertHtml(files_list)
