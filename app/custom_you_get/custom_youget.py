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


# if __name__ == '__main__':
#     urls = ['https://www.youtube.com/watch?v=mM3dSR_qxcY']
#     kwargs = {'output_dir': './tmpVideos',
#               'merge': True,
#               'json_output': False,
#               'caption': True}
#     kwargs['info_only'] = False
#     m_get_video(urls=urls, **kwargs)

