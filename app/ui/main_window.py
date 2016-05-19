# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'mainwindow.ui'
#
# Created by: PyQt5 UI code generator 5.6
#
# WARNING! All changes made in this file will be lost!

from app.ui.ui_main_window import Ui_MainWindow
from app.util.download_thread import GetVideoInfoThread
from app import kwargs
from app import mlog


class MainWindow(Ui_MainWindow):
    def set_slot(self):
        self.button_download.clicked.connect(self.get_info)

    def get_info(self):
        urls = (str(self.urls_text_edit.toPlainText())).split(';')
        mlog.debug(urls[0])
        self.m_thread = GetVideoInfoThread(urls, **kwargs)
        self.m_thread.finish_signal.connect(self.finish_get_info)
        self.m_thread.start()

    def finish_get_info(self, ls ,can_download):
        for item in ls:
            mlog.debug(item)
        mlog.debug(__name__ + 'finish_get_info: '+str(can_download))
