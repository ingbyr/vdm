# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import time
from PyQt5 import QtCore
from PyQt5.QtCore import QSettings

from app import mlog, mconfig
from app.util.config_utils import s2b
from app.util.proxy import m_set_http_proxy, m_set_socks_proxy, disable_proxy
from app.util.status import get_buffer

from app.you_get.common import any_download_playlist, any_download, download_main

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
        self.config = QSettings('config.ini', QSettings.IniFormat)

        if s2b(self.config.value('enable_proxy', 'false')):
            if s2b(self.config.value('is_http_proxy', 'false')):
                m_set_http_proxy(self.config.value('ip', '127.0.0.1'), self.config.value('port', '1080'))
                mlog.debug("enable http proxy")
            if s2b(self.config.value('is_socks_proxy', 'false')):
                m_set_socks_proxy(self.config.value('ip', '127.0.0.1'), self.config.value('port', '1080'))
                mlog.debug("enable socks proxy")
        else:
            mlog.debug('disable the proxy')
            disable_proxy()

    def run(self):
        try:
            self.kwargs['info_only'] = True
            download_main(any_download, any_download_playlist, self.urls, **self.kwargs)
            result = ''.join(get_buffer())
            can_download = True
        except Exception as e:
            mlog.exception(e)
            result = "Get information failed."
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
        self.config = QSettings('config.ini', QSettings.IniFormat)

        if s2b(self.config.value('enable_proxy', 'false')):
            if s2b(self.config.value('is_http_proxy', 'false')):
                m_set_http_proxy(self.config.value('ip', '127.0.0.1'), self.config.value('port', '1080'))
                mlog.debug("enable http proxy")
            if s2b(self.config.value('is_socks_proxy', 'false')):
                m_set_socks_proxy(self.config.value('ip', '127.0.0.1'), self.config.value('port', '1080'))
                mlog.debug("enable socks proxy")
        else:
            mlog.debug('disable the proxy')
            disable_proxy()

    def run(self):
        """
        Download the video
        :return: nothing
        """
        is_succeed = False
        try:
            self.kwargs['info_only'] = False
            mlog.debug(mconfig.get_file_itag)
            download_main(any_download, any_download_playlist, self.urls, **self.kwargs)
            is_succeed = True
        except Exception as e:
            mlog.exception(e)
            is_succeed = False
        finally:
            self.finish_signal.emit(is_succeed)
