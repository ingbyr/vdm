#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import json
import os
import re
import subprocess

from PyQt5.QtCore import QThread, pyqtSignal

from app import log, config


def youget(*args):
    cmd = [os.path.join("core", config["app"]["youget_core"])]
    for arg in args:
        cmd.append(arg)
    log.debug(cmd)

    # with open("files.txt", "w") as f:
    #     proc = subprocess.Popen(cmd, shell=True, stdout=f,
    #                             stderr=subprocess.STDOUT,
    #                             stdin=subprocess.PIPE)
    #     ret = proc.wait()
    #     log.debug("you-get core run result code: " + str(ret))

    with subprocess.Popen(cmd, stdout=subprocess.PIPE) as proc:
        result = proc.stdout.read()
    return result

    # return b"file.txt test"
    return result


def get_media_args(msg):
    tag = re.search(r"--\w*=\w*", msg).group()
    size = re.search(r"\d*\sbytes", msg).group()
    return tag, size


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
        output = youget(*self.args).decode("GBK")
        log.debug(output)
        self.finish_signal.emit(output)


if __name__ == '__main__':
    with open(os.path.join(os.path.pardir, "version.json"), "w") as f:
        v = {
            "version": config["app"]["version"],
            "you-get-core-version": "0.4.775",
            "core-url": "https://github.com/soimort/you-get/releases/download/v0.4.775/youget-core.exe"
        }
        f.write(json.dumps(v))
