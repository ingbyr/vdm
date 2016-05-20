# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import logging
import os

__author__ = 'InG_byr'

base_dir = os.getcwd()

logging.basicConfig(filename='dev.log',
                    level=logging.DEBUG,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s >>> %(message)s',
                    datefmt='%b%d %Y %H:%M:%S',
                    filemode='w')
mlog = logging

kwargs = {'output_dir': base_dir,
          'merge': True,
          'json_output': False,
          'caption': True}
