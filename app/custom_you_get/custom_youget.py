# !/usr/bin/env python3
# -*- coding: utf-8 -*-

from app.custom_you_get.common import *
from app.custom_you_get.util import custom_tools as ct

__author__ = 'InG_byr'


def m_get_video(urls, **kwargs):
    m_urls = ct.url2http(urls)
    for url in m_urls:
        m, url = url_to_module(url)
        m.download(url, **kwargs)


def m_get_download_progress(urls, **kwargs):
    pass

# if __name__ == '__main__':
#     urls = ['http://www.bilibili.com/video/av1950041/']
#     # download_main(any_download, any_download_playlist, urls=urls, playlist=False,
#     #               output_dir='../videos', merge=True, info_only=True,
#     #               json_output=False, caption=True)
#
#     m_get_video(urls=urls,
#                      output_dir='../videos', merge=True,
#                      json_output=False, caption=True)
