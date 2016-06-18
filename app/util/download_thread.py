# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import time
from PyQt5 import QtCore

from app import mlog, mconfig
from app.you_get.custom_you_get import m_get_video
from app.you_get.status import get_buffer

__author__ = 'InG_byr'


class GetVideoInfoThread(QtCore.QThread):
    """
    show the information of video
    """

    finish_signal = QtCore.pyqtSignal(str, bool)

    def __init__(self, urls, parent=None, **kwargs):
        super(GetVideoInfoThread, self).__init__(parent)
        self.urls = urls
        self.kwargs = kwargs

    def run(self):
        try:
            self.kwargs['info_only'] = True
            m_get_video(self.urls, **self.kwargs)
            result = ''.join(get_buffer())
            can_download = True
        except Exception as e:
            mlog.exception(e)
            result = 'Get information of files failed'
            can_download = False
        finally:
            self.finish_signal.emit(result, can_download)


class DownloadThread(QtCore.QThread):
    """
    start a thread to download the video
    """

    finish_signal = QtCore.pyqtSignal(bool)

    def __init__(self, urls, parent=None, **kwargs):
        super(DownloadThread, self).__init__(parent)
        self.urls = urls
        self.kwargs = kwargs

    def run(self):
        """
        Download the video
        :return: nothing
        """
        is_succeed = False
        try:
            self.kwargs['info_only'] = False
            mlog.debug(mconfig.get_file_itag)
            m_get_video(self.urls, **self.kwargs)
            is_succeed = True
        except Exception as e:
            mlog.exception(e)
            is_succeed = False
        finally:
            self.finish_signal.emit(is_succeed)
