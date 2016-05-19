# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'mainwindow.ui'
#
# Created by: PyQt5 UI code generator 5.6
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName("MainWindow")
        MainWindow.resize(400, 600)
        MainWindow.setLayoutDirection(QtCore.Qt.LeftToRight)
        self.centralWidget = QtWidgets.QWidget(MainWindow)
        self.centralWidget.setObjectName("centralWidget")
        self.url_label = QtWidgets.QLabel(self.centralWidget)
        self.url_label.setGeometry(QtCore.QRect(30, 20, 59, 14))
        self.url_label.setObjectName("url_label")
        self.url_text = QtWidgets.QTextEdit(self.centralWidget)
        self.url_text.setGeometry(QtCore.QRect(50, 60, 291, 70))
        self.url_text.setObjectName("url_text")
        self.download_btn = QtWidgets.QPushButton(self.centralWidget)
        self.download_btn.setGeometry(QtCore.QRect(130, 170, 121, 121))
        self.download_btn.setStyleSheet("border-image: url(:/new/prefix1/res/download.png);\n"
"background-color: rgb(218, 218, 218);")
        self.download_btn.setText("")
        self.download_btn.setObjectName("download_btn")
        MainWindow.setCentralWidget(self.centralWidget)
        self.menuBar = QtWidgets.QMenuBar(MainWindow)
        self.menuBar.setGeometry(QtCore.QRect(0, 0, 400, 19))
        self.menuBar.setObjectName("menuBar")
        self.menuSettings = QtWidgets.QMenu(self.menuBar)
        self.menuSettings.setObjectName("menuSettings")
        self.menuHelp = QtWidgets.QMenu(self.menuBar)
        self.menuHelp.setObjectName("menuHelp")
        MainWindow.setMenuBar(self.menuBar)
        self.mainToolBar = QtWidgets.QToolBar(MainWindow)
        self.mainToolBar.setObjectName("mainToolBar")
        MainWindow.addToolBar(QtCore.Qt.TopToolBarArea, self.mainToolBar)
        self.statusBar = QtWidgets.QStatusBar(MainWindow)
        self.statusBar.setObjectName("statusBar")
        MainWindow.setStatusBar(self.statusBar)
        self.actionLanguages = QtWidgets.QAction(MainWindow)
        self.actionLanguages.setObjectName("actionLanguages")
        self.actionFile_path = QtWidgets.QAction(MainWindow)
        self.actionFile_path.setObjectName("actionFile_path")
        self.actionCheck_for_updates = QtWidgets.QAction(MainWindow)
        self.actionCheck_for_updates.setObjectName("actionCheck_for_updates")
        self.actionReport_bugs = QtWidgets.QAction(MainWindow)
        self.actionReport_bugs.setObjectName("actionReport_bugs")
        self.actionAbout = QtWidgets.QAction(MainWindow)
        self.actionAbout.setObjectName("actionAbout")
        self.menuSettings.addAction(self.actionLanguages)
        self.menuSettings.addAction(self.actionFile_path)
        self.menuHelp.addAction(self.actionCheck_for_updates)
        self.menuHelp.addAction(self.actionReport_bugs)
        self.menuHelp.addAction(self.actionAbout)
        self.menuBar.addAction(self.menuSettings.menuAction())
        self.menuBar.addAction(self.menuHelp.menuAction())

        self.retranslateUi(MainWindow)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

    def retranslateUi(self, MainWindow):
        _translate = QtCore.QCoreApplication.translate
        MainWindow.setWindowTitle(_translate("MainWindow", "MainWindow"))
        self.url_label.setText(_translate("MainWindow", "URL"))
        self.menuSettings.setTitle(_translate("MainWindow", "Settings"))
        self.menuHelp.setTitle(_translate("MainWindow", "Help"))
        self.actionLanguages.setText(_translate("MainWindow", "Languages"))
        self.actionFile_path.setText(_translate("MainWindow", "File path"))
        self.actionCheck_for_updates.setText(_translate("MainWindow", "Check for updates"))
        self.actionReport_bugs.setText(_translate("MainWindow", "Report bugs"))
        self.actionAbout.setText(_translate("MainWindow", "About"))

