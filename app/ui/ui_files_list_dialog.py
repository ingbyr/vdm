# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'files_list_dialog.ui'
#
# Created by: PyQt5 UI code generator 5.5.1
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_FilesListDialog(object):
    def setupUi(self, FilesListDialog):
        FilesListDialog.setObjectName("FilesListDialog")
        FilesListDialog.resize(400, 400)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap(":/res/favicon.ico"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        FilesListDialog.setWindowIcon(icon)
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
        self.push_button_confirm = QtWidgets.QPushButton(FilesListDialog)
        self.push_button_confirm.setGeometry(QtCore.QRect(300, 360, 80, 22))
        self.push_button_confirm.setObjectName("push_button_confirm")
        self.push_button_cancel = QtWidgets.QPushButton(FilesListDialog)
        self.push_button_cancel.setGeometry(QtCore.QRect(210, 360, 80, 22))
        self.push_button_cancel.setObjectName("push_button_cancel")
        self.combo_box_options = QtWidgets.QComboBox(FilesListDialog)
        self.combo_box_options.setGeometry(QtCore.QRect(78, 360, 101, 22))
        self.combo_box_options.setObjectName("combo_box_options")

        self.retranslateUi(FilesListDialog)
        QtCore.QMetaObject.connectSlotsByName(FilesListDialog)

    def retranslateUi(self, FilesListDialog):
        _translate = QtCore.QCoreApplication.translate
        FilesListDialog.setWindowTitle(_translate("FilesListDialog", "Files List"))
        self.label.setText(_translate("FilesListDialog", "Choose one to download."))
        self.label_2.setText(_translate("FilesListDialog", "Select"))
        self.push_button_confirm.setText(_translate("FilesListDialog", "Download"))
        self.push_button_cancel.setText(_translate("FilesListDialog", "Cancel"))

