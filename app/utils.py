#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import json
from urllib import request

from PyQt5.QtCore import QThread, pyqtSignal

from app import log


class CheckUpdateThread(QThread):
    finish_signal = pyqtSignal(dict)

    def __init__(self):
        super(CheckUpdateThread, self).__init__()

    def run(self):
        remote_inf = {}
        try:
            with request.urlopen("https://raw.githubusercontent.com/ingbyr/GUI-YouGet/master/version.json") as f:
                raw_inf = str(f.read())[2:-1]
                log.debug("raw_info: " + raw_inf)
                remote_inf = json.loads(raw_inf)
        except Exception as e:
            log.exception(e)
        finally:
            self.finish_signal.emit(remote_inf)

if __name__ == '__main__':
    pass