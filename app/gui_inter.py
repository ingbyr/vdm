# !/usr/bin/env python3
# -*- coding: utf-8 -*-

from app.custom_you_get.common import *
from app.custom_you_get import r_obj

__author__ = 'InG_byr'


def main_interface(urls):
    try:
        sys.stdout = r_obj
        download_main(any_download, any_download_playlist, urls=urls, playlist=False,
                      output_dir='../videos', merge=True, info_only=False,
                      json_output=False, caption=True)
        result = r_obj.get_buffer()

    except Exception as e:
        result = str(e)
    finally:
        sys.stdout = sys.__stdout__
        return result

# if __name__ == '__main__':
#     main_interface(['http://www.bilibili.com/video/av4233205/'])
