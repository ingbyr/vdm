# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'about_widget.ui'
#
# Created by: PyQt5 UI code generator 5.6
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_AboutForm(object):
    def setupUi(self, AboutForm):
        AboutForm.setObjectName("AboutForm")
        AboutForm.resize(500, 250)
        self.label_logo = QtWidgets.QLabel(AboutForm)
        self.label_logo.setGeometry(QtCore.QRect(10, 70, 131, 121))
        self.label_logo.setStyleSheet("image: url(:/res/favicon.ico);")
        self.label_logo.setObjectName("label_logo")
        self.label_info = QtWidgets.QLabel(AboutForm)
        self.label_info.setGeometry(QtCore.QRect(170, 50, 311, 191))
        self.label_info.setTextFormat(QtCore.Qt.RichText)
        self.label_info.setObjectName("label_info")
        self.label_title = QtWidgets.QLabel(AboutForm)
        self.label_title.setGeometry(QtCore.QRect(170, 10, 121, 41))
        font = QtGui.QFont()
        font.setPointSize(16)
        self.label_title.setFont(font)
        self.label_title.setTextFormat(QtCore.Qt.PlainText)
        self.label_title.setIndent(-1)
        self.label_title.setObjectName("label_title")
        self.label_version = QtWidgets.QLabel(AboutForm)
        self.label_version.setGeometry(QtCore.QRect(309, 25, 131, 20))
        self.label_version.setText("")
        self.label_version.setObjectName("label_version")

        self.retranslateUi(AboutForm)
        QtCore.QMetaObject.connectSlotsByName(AboutForm)

    def retranslateUi(self, AboutForm):
        _translate = QtCore.QCoreApplication.translate
        AboutForm.setWindowTitle(_translate("AboutForm", "About"))
        self.label_logo.setText(_translate("AboutForm", "<html><head/><body><p><br/></p></body></html>"))
        self.label_info.setText(_translate("AboutForm", "<html><head/><body><p><a href=\"https://github.com/ingbyr/GUI-YouGet\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">GUI-YouGet </span></a><span style=\" font-size:11pt;\">is a video download software </span></p><p><span style=\" font-size:11pt;\">Follow open source License </span><a href=\"https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">MIT</span></a></p><p><span style=\" font-size:11pt;\">Based on the program </span><a href=\"https://github.com/soimort/you-get\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">you-get</span></a></p><p><span style=\" font-size:11pt;\">Coder: InG_byr ( </span><a href=\"http://www.ingbyr.tk\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">Blog </span></a><span style=\" font-size:11pt;\">/ </span><a href=\"http://www.weibo.com/zwkv5\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">Weibo</span></a><span style=\" font-size:11pt;\"> )</span></p><p><span style=\" font-size:11pt;\">Designer: InG_byr ( </span><a href=\"http://www.ingbyr.tk\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">Blog </span></a><span style=\" font-size:11pt;\">/ </span><a href=\"http://www.weibo.com/zwkv5\"><span style=\" font-size:11pt; text-decoration: underline; color:#0000ff;\">Weibo</span></a><span style=\" font-size:11pt;\">)</span></p></body></html>"))
        self.label_title.setText(_translate("AboutForm", "GUI-YouGet"))

