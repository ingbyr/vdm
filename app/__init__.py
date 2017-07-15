#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
author: ingbyr
website: www.ingbyr.com
"""
import configparser
import logging
import os

__author__ = 'InG_byr'

# Log settings

logging.basicConfig(filename='youget.log',
                    level=logging.DEBUG,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s >>> %(message)s',
                    datefmt='%b%d %Y %H:%M:%S',
                    filemode='w')

log = logging

# app config init
config = configparser.ConfigParser()

config_p = os.path.join(os.getcwd(), "config.ini")

config.read(config_p)

if not config.has_option("common", "out_put_dir"):
    config["common"]["out_put_dir"] = os.getcwd()
    with open(config_p, 'w') as configfile:
        config.write(configfile)
