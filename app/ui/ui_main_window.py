# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'main_window.ui'
#
# Created by: PyQt5 UI code generator 5.5.1
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName("MainWindow")
        MainWindow.resize(453, 180)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap(":/res/favicon.ico"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        MainWindow.setWindowIcon(icon)
        MainWindow.setLayoutDirection(QtCore.Qt.LeftToRight)
        MainWindow.setAutoFillBackground(False)
        self.central_widget = QtWidgets.QWidget(MainWindow)
        self.central_widget.setObjectName("central_widget")
        self.label_url = QtWidgets.QLabel(self.central_widget)
        self.label_url.setGeometry(QtCore.QRect(20, 10, 31, 31))
        self.label_url.setObjectName("label_url")
        self.button_download = QtWidgets.QPushButton(self.central_widget)
        self.button_download.setGeometry(QtCore.QRect(370, 50, 61, 61))
        self.button_download.setStyleSheet("border-image: url(:/res/download.png);\n"
"")
        self.button_download.setText("")
        self.button_download.setObjectName("button_download")
        self.text_edit_urls = QtWidgets.QTextEdit(self.central_widget)
        self.text_edit_urls.setGeometry(QtCore.QRect(20, 50, 331, 71))
        self.text_edit_urls.setObjectName("text_edit_urls")
        MainWindow.setCentralWidget(self.central_widget)
        self.menu_bar = QtWidgets.QMenuBar(MainWindow)
        self.menu_bar.setGeometry(QtCore.QRect(0, 0, 453, 25))
        self.menu_bar.setObjectName("menu_bar")
        self.menu_settings = QtWidgets.QMenu(self.menu_bar)
        self.menu_settings.setObjectName("menu_settings")
        self.menu_help = QtWidgets.QMenu(self.menu_bar)
        self.menu_help.setObjectName("menu_help")
        MainWindow.setMenuBar(self.menu_bar)
        self.status_bar = QtWidgets.QStatusBar(MainWindow)
        self.status_bar.setEnabled(True)
        self.status_bar.setObjectName("status_bar")
        MainWindow.setStatusBar(self.status_bar)
        self.action_file_path = QtWidgets.QAction(MainWindow)
        self.action_file_path.setObjectName("action_file_path")
        self.action_report_bugs = QtWidgets.QAction(MainWindow)
        self.action_report_bugs.setObjectName("action_report_bugs")
        self.action_about = QtWidgets.QAction(MainWindow)
        self.action_about.setObjectName("action_about")
        self.action_check_for_updates = QtWidgets.QAction(MainWindow)
        self.action_check_for_updates.setObjectName("action_check_for_updates")
        self.action_supported_sites = QtWidgets.QAction(MainWindow)
        self.action_supported_sites.setObjectName("action_supported_sites")
        self.menu_settings.addAction(self.action_file_path)
        self.menu_help.addAction(self.action_check_for_updates)
        self.menu_help.addAction(self.action_report_bugs)
        self.menu_help.addAction(self.action_about)
        self.menu_help.addAction(self.action_supported_sites)
        self.menu_bar.addAction(self.menu_settings.menuAction())
        self.menu_bar.addAction(self.menu_help.menuAction())

        self.retranslateUi(MainWindow)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

    def retranslateUi(self, MainWindow):
        _translate = QtCore.QCoreApplication.translate
        MainWindow.setWindowTitle(_translate("MainWindow", "YouGet"))
        self.label_url.setText(_translate("MainWindow", "URL"))
        self.button_download.setStatusTip(_translate("MainWindow", "Here we go!"))
        self.button_download.setWhatsThis(_translate("MainWindow", "<html><head/><body><p>Download button</p></body></html>"))
        self.text_edit_urls.setWhatsThis(_translate("MainWindow", "<html><head/><body><p>Input the target\'s url</p></body></html>"))
        self.menu_settings.setTitle(_translate("MainWindow", "Settings"))
        self.menu_help.setTitle(_translate("MainWindow", "Help"))
        self.action_file_path.setText(_translate("MainWindow", "File path"))
        self.action_report_bugs.setText(_translate("MainWindow", "Report bugs"))
        self.action_about.setText(_translate("MainWindow", "About"))
        self.action_check_for_updates.setText(_translate("MainWindow", "Check for updates"))
        self.action_supported_sites.setText(_translate("MainWindow", "Supported Sites"))
