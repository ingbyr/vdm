# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'proxy_dialog.ui'
#
# Created by: PyQt5 UI code generator 5.6
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_Dialog(object):
    def setupUi(self, Dialog):
        Dialog.setObjectName("Dialog")
        Dialog.resize(400, 300)
        self.button_box = QtWidgets.QDialogButtonBox(Dialog)
        self.button_box.setGeometry(QtCore.QRect(30, 240, 341, 32))
        self.button_box.setOrientation(QtCore.Qt.Horizontal)
        self.button_box.setStandardButtons(QtWidgets.QDialogButtonBox.Cancel|QtWidgets.QDialogButtonBox.Ok)
        self.button_box.setObjectName("button_box")
        self.title = QtWidgets.QLabel(Dialog)
        self.title.setGeometry(QtCore.QRect(10, 10, 371, 31))
        font = QtGui.QFont()
        font.setPointSize(14)
        self.title.setFont(font)
        self.title.setAlignment(QtCore.Qt.AlignCenter)
        self.title.setObjectName("title")
        self.label_ip = QtWidgets.QLabel(Dialog)
        self.label_ip.setGeometry(QtCore.QRect(30, 70, 31, 41))
        self.label_ip.setObjectName("label_ip")
        self.label_port = QtWidgets.QLabel(Dialog)
        self.label_port.setGeometry(QtCore.QRect(30, 134, 31, 31))
        self.label_port.setObjectName("label_port")
        self.http_checkbox = QtWidgets.QCheckBox(Dialog)
        self.http_checkbox.setGeometry(QtCore.QRect(40, 190, 141, 30))
        self.http_checkbox.setObjectName("http_checkbox")
        self.socks5_checkbox = QtWidgets.QCheckBox(Dialog)
        self.socks5_checkbox.setGeometry(QtCore.QRect(200, 190, 141, 30))
        self.socks5_checkbox.setObjectName("socks5_checkbox")
        self.ip_text_edit = QtWidgets.QLineEdit(Dialog)
        self.ip_text_edit.setGeometry(QtCore.QRect(70, 70, 311, 37))
        self.ip_text_edit.setObjectName("ip_text_edit")
        self.port_text_edit = QtWidgets.QLineEdit(Dialog)
        self.port_text_edit.setGeometry(QtCore.QRect(70, 130, 311, 37))
        self.port_text_edit.setObjectName("port_text_edit")

        self.retranslateUi(Dialog)
        self.button_box.accepted.connect(Dialog.accept)
        self.button_box.rejected.connect(Dialog.reject)
        QtCore.QMetaObject.connectSlotsByName(Dialog)

    def retranslateUi(self, Dialog):
        _translate = QtCore.QCoreApplication.translate
        Dialog.setWindowTitle(_translate("Dialog", "Dialog"))
        self.title.setText(_translate("Dialog", "Proxy Setting"))
        self.label_ip.setText(_translate("Dialog", "IP"))
        self.label_port.setText(_translate("Dialog", "Port"))
        self.http_checkbox.setText(_translate("Dialog", "HTTP proxy"))
        self.socks5_checkbox.setText(_translate("Dialog", "Socks5 proxy"))

