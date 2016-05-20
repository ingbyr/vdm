# !/usr/bin/env python3
# -*- coding: utf-8 -*-

from app.ui.ui_main_window import Ui_MainWindow
from app.util.download_thread import GetVideoInfoThread
from app import kwargs
from app import mlog

from PyQt5.QtWidgets import QMainWindow
from app.ui.about_widget import AboutWdiget


class MainWindow(Ui_MainWindow):
    def __init__(self):
        super().__init__()
        self.main_window = QMainWindow()
        self.setupUi(self.main_window)
        self.set_slot()

    def set_slot(self):
        self.button_download.clicked.connect(self.get_info)
        self.action_about.triggered.connect(self.show_about)

    def get_info(self):
        urls = (str(self.urls_text_edit.toPlainText())).split(';')
        mlog.debug(urls[0])
        self.m_thread = GetVideoInfoThread(urls, **kwargs)
        self.m_thread.finish_signal.connect(self.finish_get_info)
        self.m_thread.start()

    def finish_get_info(self, ls, can_download):
        for item in ls:
            mlog.debug(item)
        mlog.debug('finish_get_info: ' + str(can_download))

    def show_about(self):
        mlog.debug('show about widget')
        self.about_widget = AboutWdiget()
        self.about_widget.about_widget.show()
