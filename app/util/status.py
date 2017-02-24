# !/usr/bin/env python3
# -*- coding: utf-8 -*-
from app.config import add_stream

__author__ = "ingbyr"

SPEED = ''
PERCENT = 0.0
EXIST = False
STOP_THREAD = False

buffer = []


def set_default():
    global SPEED
    global PERCENT
    global EXIST
    global STOP_THREAD
    SPEED = ''
    PERCENT = 0.0
    EXIST = False
    STOP_THREAD = False

    global buffer
    buffer = []


def set_percent(data):
    global PERCENT
    PERCENT = data


def get_percent():
    global PERCENT
    return PERCENT


def set_exist(data):
    global EXIST
    EXIST = data


def get_exist():
    global EXIST
    return EXIST


def set_stop_thread(data):
    global STOP_THREAD
    STOP_THREAD = data


def get_stop_thread():
    global STOP_THREAD
    return STOP_THREAD


def set_speed(data):
    global SPEED
    SPEED = data


def get_speed():
    global SPEED
    return SPEED


def print_gui(*arg, **kwargs):
    global buffer
    if arg:
        for item in arg:
            if str(item).startswith('    # download-with:'):
                index = item.find('=')
                options = item[(index + 1):-6]
                add_stream(options)
                item = '<font color=blue>The option is [ ' + options + ' ]</p></font>'
            buffer.append(str(item) + '<br>')
    else:
        buffer.append('<br>')


def get_buffer():
    global buffer
    return buffer

def clear_buffer():
    global buffer
    buffer = []
