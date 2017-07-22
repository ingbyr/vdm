#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import json
import os
import subprocess

from PyQt5.QtCore import QThread, pyqtSignal

from app import log, config


def core_cli(*args):
    cmd = [os.path.join("core", config["core"]["youtube-dl"])]
    # cmd = ["chcp", "65001", "&&", "you-get"]
    for arg in args:
        cmd.append(arg)
    log.debug(cmd)

    with subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT) as proc:
        result = proc.stdout.read()
    return result


def get_media_tag(msg):
    tag = msg.split(' ')[-1].strip()
    return tag


def options_filter(output):
    """
    generate the file lists
    :param msg:
    :return:
    """
    output = json.loads(output)

    if (config['dev']['d']):
        with open('info.json', 'w') as f:
            json.dump(output, f)

    options = {
        'title': output.get('title', 'No info'),
        'formats': output.get('formats', [])
    }
    return options


def format2str(format):
    size = format.get('filesize', None)
    if size is None or size == 0:
        size_info = 'No file size'
    else:
        size_info = '%.2f Mb' % (int(size) / 1024 / 1024)
    out = 'Size:   ' + size_info
    out += '\nType:    ' + str(format.get('ext', 'No type'))
    out += '\nResolution:   ' + str(format.get('width', None)) + ' X ' + str(format.get('height', None))
    out += '\nFormat ID:   ' + str(format.get('format_id', 'No ID'))
    return out


class GetMediaInfoThread(QThread):
    """
    get the media info
    """
    finish_signal = pyqtSignal(dict)

    def __init__(self, *args):
        super(GetMediaInfoThread, self).__init__()
        self.args = args

    def run(self):
        output = core_cli(*self.args).decode('utf-8')
        options = options_filter(output)
        self.finish_signal.emit(options)


class DowloadMediaThread(QThread):
    """
    download media
    """
    finish_signal = pyqtSignal(str)

    def __init__(self, *args):
        super(DowloadMediaThread, self).__init__()
        self.args = args

    def run(self):
        output = core_cli(*self.args).decode('utf-8')
        self.finish_signal.emit(output)
