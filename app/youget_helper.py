#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import os
import re
import subprocess

from PyQt5.QtCore import QThread, pyqtSignal

from app import log, config


def youget(*args):
    # todo utf-8 设置后没有作用
    # todo core换为youtube-dl，youget编码问题严重
    cmd = [os.path.join("core", config["core"]["youget"])]
    # cmd = ["chcp", "65001", "&&", "you-get"]
    for arg in args:
        cmd.append(arg)
    log.debug(cmd)

    with subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT) as proc:
        result = proc.stdout.read()
    return result


def get_media_tag(msg):
    tag = re.search(r"--\w*=\w*", msg).group()
    return tag


def options_filter(msg):
    """
    generate the file lists
    :param msg:
    :return:
    """
    options = re.split(r'\n\s*\n', msg)
    return options


class GetMediaInfoThread(QThread):
    """
    get the media info
    """
    finish_signal = pyqtSignal(list)

    def __init__(self, *args):
        super(GetMediaInfoThread, self).__init__()
        self.args = args

    def run(self):
        # todo 不应该只支持GBK编码
        output = youget(*self.args).decode("GBK")
        log.debug(output)
        result = options_filter(output)
        for res in result:
            log.debug(res)
        self.finish_signal.emit(result)


class DowloadMediaThread(QThread):
    """
    download media
    """
    finish_signal = pyqtSignal(str)

    def __init__(self, *args):
        super(DowloadMediaThread, self).__init__()
        self.args = args

    def run(self):
        # todo 不应该只支持GBK编码
        output = youget(*self.args).decode("GBK")
        log.debug(output)
        self.finish_signal.emit(output)
