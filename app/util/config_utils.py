# !/usr/bin/env python3
# -*- coding: utf-8 -*-

__author__ = "ingbyr"


def s2b(str):
    if isinstance(str, bool):
        return str

    if str == 'false':
        return False
    elif str == 'true':
        return True
    else:
        return None
