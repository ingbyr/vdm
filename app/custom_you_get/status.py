# !/usr/bin/env python3
# -*- coding: utf-8 -*-



__author__ = 'InG_byr'

SPEED = ''
PERCENT = 0.0
EXIST = False


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
