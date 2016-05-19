# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import logging
import os

__author__ = 'InG_byr'

logging.basicConfig(filename='dev_log.log',
                    level=logging.DEBUG,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                    datefmt='%b%d %Y %H:%M:%S',
                    filemode='w')
mlog = logging

base_dir = os.getcwd()

kwargs = {'output_dir': base_dir,
          'merge': True,
          'json_output': False,
          'caption': True}
