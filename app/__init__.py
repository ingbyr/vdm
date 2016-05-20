# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import logging

__author__ = 'InG_byr'


logging.basicConfig(filename='dev.log',
                    level=logging.DEBUG,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s >>> %(message)s',
                    datefmt='%b%d %Y %H:%M:%S',
                    filemode='w')
mlog = logging
