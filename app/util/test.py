# !/usr/bin/env python3
# -*- coding: utf-8 -*-
import os

from app.you_get.common import download_main, any_download, any_download_playlist

if __name__ == '__main__':
    urls = ['http://v.youku.com/v_show/id_XMTc0Njg2MjIwOA==.html']
    download_main(any_download, any_download_playlist, urls, False,
                  output_dir=os.getcwd(), merge=True, info_only=True,
                  json_output=False, caption=True)


