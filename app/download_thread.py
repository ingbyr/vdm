# !/usr/bin/env python3
# -*- coding: utf-8 -*-
import time
from PyQt5 import QtCore

from app.custom_you_get.custom_youget import m_get_download_progress, r_obj, m_get_video

__author__ = 'InG_byr'


class GetVideoInfoThread(QtCore.QThread):
    """
    show the information of video
    """

    # a signal, return list
    finish_signal = QtCore.pyqtSignal(list)

    def __init__(self, information_ui, urls, parent=None, **kwargs):
        super(GetVideoInfoThread, self).__init__(parent)
        self.information_ui = information_ui
        self.urls = urls
        self.kwargs = kwargs

    def run(self):
        # download video
        time.sleep(2)
        self.kwargs['info_only'] = True
        m_get_video(self.urls, **self.kwargs)
        show_inf = r_obj.get_buffer() + '\n'

        # when finished, notify the main thread
        self.finish_signal.emit([show_inf])

class DownloadThread(QtCore.QThread):
    """
    start a thread to download the video
    """
    # a signal, return list
    finishSignal = QtCore.pyqtSignal(list)

    def __init__(self, information_ui, urls, parent=None, **kwargs):
        super(DownloadThread, self).__init__(parent)
        self.information_ui = information_ui
        self.urls = urls
        self.kwargs = kwargs

    def run(self):
        # download video
        time.sleep(2)
        self.kwargs['info_only'] = False
        m_get_video(self.urls, **self.kwargs)
        show_inf = r_obj.get_buffer() + '\n'

        # when finished, notify the main thread
        self.finishSignal.emit([show_inf])
