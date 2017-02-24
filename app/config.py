# !/usr/bin/env python3
# -*- coding: utf-8 -*-
import configparser
import json
from datetime import date
import os

from PyQt5.QtCore import QSettings

from app.you_get.version import script_name, __version__

__author__ = 'InG_byr'

version = '0.1.2'

buildtime = date.today()

data = {'version': version,
        'build time': str(buildtime),
        script_name: __version__}

base_dir = os.getcwd()

kwargs = {'output_dir': base_dir,
          'merge': True,
          'json_output': False,
          'caption': True,
          'playlist': False}

urls = ''

streams = []

file_path = 'file_path'


def set_default():
    global kwargs
    global urls
    global streams
    streams = []
    kwargs['stream_id'] = ''
    urls = ''


def set_file_path(path):
    global kwargs
    kwargs['output_dir'] = path


def get_file_path():
    global kwargs
    return kwargs['output_dir']


def set_file_itag(stream_id):
    global kwargs
    kwargs['stream_id'] = stream_id


def get_file_itag():
    global kwargs
    return kwargs['stream_id']


def get_urls():
    global urls
    return urls


def set_urls(data):
    global urls
    urls = data


def add_stream(data):
    global streams
    streams.append(data)


def get_streams():
    global streams
    return streams


if __name__ == '__main__':
    # 保存版本信息
    with open('../version.json', 'w') as f:
        f.write(json.dumps(data))

    config = 'config.ini'

    file = open(config, 'w')

    setting = configparser.ConfigParser()
    setting.read('config.ini')
    setting.add_section('General')
    setting.set('General', 'version', version)
    setting.set('General', script_name, __version__)

    setting.write(file)
    file.close()
