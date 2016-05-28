# !/usr/bin/env python3
# -*- coding: utf-8 -*-



__author__ = 'InG_byr'

SPEED = ''
PERCENT = 0.0
EXIST = False
STOP_THREAD = False


def set_default():
    global SPEED
    global PERCENT
    global EXIST
    global STOP_THREAD
    SPEED = ''
    PERCENT = 0.0
    EXIST = False
    STOP_THREAD = False


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
