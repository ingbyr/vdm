# !/usr/bin/env python3
# -*- coding: utf-8 -*-
from PyQt5.QtWidgets import QWidget

from app.ui.ui_about_form import Ui_AboutForm
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

    def finish_get_info(self, ls, can_download):
        for item in ls:
            mlog.debug(item)
        mlog.debug('finish_get_info: ' + str(can_download))
