#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""

import logging

__author__ = 'InG_byr'

# logging.basicConfig(filename='GUI-YouGet.log',
#                     level=logging.DEBUG,
#                     format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s >>> %(message)s',
#                     datefmt='%b%d %Y %H:%M:%S',
#                     filemode='w')

logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s >>> %(message)s',
                    datefmt='%b%d %Y %H:%M:%S')

log = logging
