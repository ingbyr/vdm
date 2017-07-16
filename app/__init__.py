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

# log settings
# todo log to file when finished
# filename='youget.log'
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s >>> %(message)s',
    datefmt='%b%d %Y %H:%M:%S',
    filemode='w')

log = logging


# app config init
# todo config delete options when finished
def save_config(sect, op, v):
    config[sect][op] = v
    with open(config_p, 'w') as f:
        config.write(f)


config = configparser.ConfigParser()
config_p = os.path.join(os.getcwd(), "config.ini")
config.read(config_p)

if not config.has_option("common", "output_dir"):
    save_config("common", "output_dir", os.getcwd())

if not config.has_option("proxy", "type"):
    save_config("proxy", "type", "http")
if not config.has_option("proxy", "enable"):
    save_config("proxy", "enable", "False")
if not config.has_option("proxy", "ip"):
    save_config("proxy", "ip", "127.0.0.1")
if not config.has_option("proxy", "port"):
    save_config("proxy", "port", "1080")
