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
        self.text_files_list = QtWidgets.QTextEdit(FilesListDialog)
        self.text_files_list.setGeometry(QtCore.QRect(20, 20, 361, 291))
        self.text_files_list.setReadOnly(True)
        self.text_files_list.setObjectName("text_files_list")
        self.label = QtWidgets.QLabel(FilesListDialog)
        self.label.setGeometry(QtCore.QRect(20, 310, 361, 51))
        self.label.setObjectName("label")
        self.label_2 = QtWidgets.QLabel(FilesListDialog)
        self.label_2.setGeometry(QtCore.QRect(20, 360, 51, 20))
        self.label_2.setObjectName("label_2")
        self.line_edit_options = QtWidgets.QLineEdit(FilesListDialog)
        self.line_edit_options.setGeometry(QtCore.QRect(82, 360, 111, 21))
        self.line_edit_options.setObjectName("line_edit_options")
        self.push_button_confirm = QtWidgets.QPushButton(FilesListDialog)
        self.push_button_confirm.setGeometry(QtCore.QRect(220, 360, 80, 22))
        self.push_button_confirm.setObjectName("push_button_confirm")
        self.push_button_cancel = QtWidgets.QPushButton(FilesListDialog)
        self.push_button_cancel.setGeometry(QtCore.QRect(310, 360, 80, 22))
        self.push_button_cancel.setObjectName("push_button_cancel")

        self.retranslateUi(FilesListDialog)
        QtCore.QMetaObject.connectSlotsByName(FilesListDialog)

    def retranslateUi(self, FilesListDialog):
        _translate = QtCore.QCoreApplication.translate
        FilesListDialog.setWindowTitle(_translate("FilesListDialog", "Files List"))
        self.label.setText(_translate("FilesListDialog", "Choose spicifed files to download by inputting options\n"
"Without correct options, you will get default files"))
        self.label_2.setText(_translate("FilesListDialog", "options"))
        self.push_button_confirm.setText(_translate("FilesListDialog", "Confirm"))
        self.push_button_cancel.setText(_translate("FilesListDialog", "Cancel"))

