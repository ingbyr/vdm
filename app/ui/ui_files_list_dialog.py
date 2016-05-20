# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'files_list_dialog.ui'
#
# Created by: PyQt5 UI code generator 5.6
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_FilesListDialog(object):
    def setupUi(self, FilesListDialog):
        FilesListDialog.setObjectName("FilesListDialog")
        FilesListDialog.resize(400, 400)
        self.button_box_confirm = QtWidgets.QDialogButtonBox(FilesListDialog)
        self.button_box_confirm.setGeometry(QtCore.QRect(210, 350, 171, 40))
        self.button_box_confirm.setOrientation(QtCore.Qt.Horizontal)
        self.button_box_confirm.setStandardButtons(QtWidgets.QDialogButtonBox.Cancel|QtWidgets.QDialogButtonBox.Ok)
        self.button_box_confirm.setObjectName("button_box_confirm")
        self.text_files_list = QtWidgets.QTextEdit(FilesListDialog)
        self.text_files_list.setGeometry(QtCore.QRect(20, 20, 361, 291))
        self.text_files_list.setReadOnly(True)
        self.text_files_list.setObjectName("text_files_list")
        self.label = QtWidgets.QLabel(FilesListDialog)
        self.label.setGeometry(QtCore.QRect(20, 320, 361, 31))
        self.label.setObjectName("label")
        self.label_2 = QtWidgets.QLabel(FilesListDialog)
        self.label_2.setGeometry(QtCore.QRect(20, 360, 31, 20))
        self.label_2.setObjectName("label_2")
        self.line_edit_itag = QtWidgets.QLineEdit(FilesListDialog)
        self.line_edit_itag.setGeometry(QtCore.QRect(70, 360, 113, 21))
        self.line_edit_itag.setObjectName("line_edit_itag")

        self.retranslateUi(FilesListDialog)
        self.button_box_confirm.accepted.connect(FilesListDialog.accept)
        self.button_box_confirm.rejected.connect(FilesListDialog.reject)
        QtCore.QMetaObject.connectSlotsByName(FilesListDialog)

    def retranslateUi(self, FilesListDialog):
        _translate = QtCore.QCoreApplication.translate
        FilesListDialog.setWindowTitle(_translate("FilesListDialog", "Files List"))
        self.label.setText(_translate("FilesListDialog", "Chose the file by input itag, otherwise will download default "))
        self.label_2.setText(_translate("FilesListDialog", "itag"))

