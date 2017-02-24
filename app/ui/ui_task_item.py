# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'tast_item.ui'
#
# Created by: PyQt5 UI code generator 5.5.1
#
# WARNING! All changes made in this file will be lost!

from PyQt5 import QtCore, QtGui, QtWidgets

class Ui_TaskItem(object):
    def setupUi(self, TaskItem):
        TaskItem.setObjectName("TaskItem")
        TaskItem.resize(330, 100)
        self.task_name = QtWidgets.QLabel(TaskItem)
        self.task_name.setGeometry(QtCore.QRect(50, 10, 261, 21))
        font = QtGui.QFont()
        font.setPointSize(14)
        font.setBold(True)
        font.setWeight(75)
        self.task_name.setFont(font)
        self.task_name.setObjectName("task_name")
        self.task_progress_bar = QtWidgets.QProgressBar(TaskItem)
        self.task_progress_bar.setGeometry(QtCore.QRect(50, 40, 211, 23))
        self.task_progress_bar.setProperty("value", 24)
        self.task_progress_bar.setObjectName("task_progress_bar")
        self.task_speed = QtWidgets.QLabel(TaskItem)
        self.task_speed.setGeometry(QtCore.QRect(270, 40, 51, 21))
        self.task_speed.setObjectName("task_speed")
        self.task_format = QtWidgets.QLabel(TaskItem)
        self.task_format.setGeometry(QtCore.QRect(50, 70, 91, 16))
        self.task_format.setObjectName("task_format")
        self.task_time = QtWidgets.QLabel(TaskItem)
        self.task_time.setGeometry(QtCore.QRect(150, 70, 141, 16))
        self.task_time.setObjectName("task_time")

        self.retranslateUi(TaskItem)
        QtCore.QMetaObject.connectSlotsByName(TaskItem)

    def retranslateUi(self, TaskItem):
        _translate = QtCore.QCoreApplication.translate
        TaskItem.setWindowTitle(_translate("TaskItem", "Form"))
        self.task_name.setText(_translate("TaskItem", "media name"))
        self.task_speed.setText(_translate("TaskItem", "speed"))
        self.task_format.setText(_translate("TaskItem", "fomat"))
        self.task_time.setText(_translate("TaskItem", "time"))

