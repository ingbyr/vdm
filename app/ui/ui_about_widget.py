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
        AboutForm.resize(450, 200)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap(":/res/favicon.ico"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        AboutForm.setWindowIcon(icon)
        self.label_logo = QtWidgets.QLabel(AboutForm)
        self.label_logo.setGeometry(QtCore.QRect(20, 60, 131, 121))
        self.label_logo.setStyleSheet("image: url(:/res/favicon.ico);")
        self.label_logo.setObjectName("label_logo")
        self.label_info = QtWidgets.QLabel(AboutForm)
        self.label_info.setGeometry(QtCore.QRect(180, 50, 251, 141))
        self.label_info.setTextFormat(QtCore.Qt.RichText)
        self.label_info.setObjectName("label_info")
        self.label_title = QtWidgets.QLabel(AboutForm)
        self.label_title.setGeometry(QtCore.QRect(170, 0, 111, 41))
        font = QtGui.QFont()
        font.setPointSize(16)
        self.label_title.setFont(font)
        self.label_title.setTextFormat(QtCore.Qt.PlainText)
        self.label_title.setIndent(-1)
        self.label_title.setObjectName("label_title")
        self.label_version = QtWidgets.QLabel(AboutForm)
        self.label_version.setGeometry(QtCore.QRect(300, 20, 131, 20))
        palette = QtGui.QPalette()
        brush = QtGui.QBrush(QtGui.QColor(121, 121, 121))
        brush.setStyle(QtCore.Qt.SolidPattern)
        palette.setBrush(QtGui.QPalette.Active, QtGui.QPalette.Text, brush)
        brush = QtGui.QBrush(QtGui.QColor(121, 121, 121))
        brush.setStyle(QtCore.Qt.SolidPattern)
        palette.setBrush(QtGui.QPalette.Inactive, QtGui.QPalette.Text, brush)
        brush = QtGui.QBrush(QtGui.QColor(159, 158, 158))
        brush.setStyle(QtCore.Qt.SolidPattern)
        palette.setBrush(QtGui.QPalette.Disabled, QtGui.QPalette.Text, brush)
        self.label_version.setPalette(palette)
        self.label_version.setText("")
        self.label_version.setObjectName("label_version")

        self.retranslateUi(AboutForm)
        QtCore.QMetaObject.connectSlotsByName(AboutForm)

    def retranslateUi(self, AboutForm):
        _translate = QtCore.QCoreApplication.translate
        AboutForm.setWindowTitle(_translate("AboutForm", "About"))
        self.label_logo.setText(_translate("AboutForm", "<html><head/><body><p><br/></p></body></html>"))
        self.label_info.setText(_translate("AboutForm", "<html><head/><body><p>GitHub Repository: <a href=\"https://github.com/ingbyr/GUI-YouGet\"><span style=\" text-decoration: underline; color:#0000ff;\">GUI-YouGet</span></a></p><p>License: <a href=\"https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/LICENSE.txt\"><span style=\" text-decoration: underline; color:#0000ff;\">MIT</span></a> | Based on: <a href=\"https://github.com/soimort/you-get\"><span style=\" text-decoration: underline; color:#0000ff;\">you-get</span></a></p><p>Coder: ingbyr ( <a href=\"http://www.ingbyr.tk\"><span style=\" text-decoration: underline; color:#0000ff;\">Blog</span></a> | <a href=\"http://www.weibo.com/zwkv5\"><span style=\" text-decoration: underline; color:#0000ff;\">Weibo</span></a> )</p><p>UI Designer: Nobody</p></body></html>"))
        self.label_title.setText(_translate("AboutForm", "GUI-YouGet"))
