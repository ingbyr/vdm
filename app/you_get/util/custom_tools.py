# !/usr/bin/env python3
# -*- coding: utf-8 -*-

__author__ = 'InG_byr'


def url2http(urls):
    real_url = []
    for url in urls:
        if url.startswith('https://'):
            url = url[8:]
        if not url.startswith('http://'):
            url = 'http://' + url
        real_url.append(url)
    return real_url
