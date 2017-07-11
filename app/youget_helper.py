#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os
import subprocess

from PyQt5.QtCore import QThread, pyqtSignal

from app import log


def get_output(*args):
    cmd = [os.path.join("core", "you-get-0.4.775-win32.exe")]
    for arg in args:
        cmd.append(arg)
    log.debug(cmd)

    with subprocess.Popen(cmd, stdout=subprocess.PIPE) as proc:
        result = proc.stdout.read()
    return result


class GetVideoInfoThread(QThread):
    finish_signal = pyqtSignal(str)

    def __init__(self, *args):
        super(GetVideoInfoThread, self).__init__()
        self.args = args
        self.result = ""

    def run(self):
        result = get_output(*self.args)
        log.debug(self.result)
        self.finish_signal.emit(result.decode("GBK"))
