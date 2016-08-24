# !/usr/bin/env python3
# -*- coding: utf-8 -*-

from app import mlog
from ..version import script_name


def sprint(text, *colors):
    """Format text with color or other effects into ANSI escaped string."""
    # return "\33[{}m{content}\33[{}m".format(";".join([str(color) for color in colors]), RESET, content=text) if IS_ANSI_TERMINAL and colors else text
    return text


def println(text, *colors):
    """Print text to standard output."""
    # sys.stdout.write(sprint(text, *colors) + "\n")
    mlog.info(text)


def print_err(text, *colors):
    """Print text to standard error."""
    # sys.stderr.write(sprint(text, *colors) + "\n")
    mlog.error(text)


def print_log(text, *colors):
    """Print a log message to standard error."""
    # sys.stderr.write(sprint("{}: {}".format(script_name, text), *colors) + "\n")
    mlog.info("{}: {}".format(script_name, text))


def i(message):
    """Print a normal log message."""
    # print_log(message)
    mlog.info(message)


def d(message):
    """Print a debug log message."""
    # print_log(message, BLUE)
    mlog.debug(message)


def w(message):
    """Print a warning log message."""
    # print_log(message, YELLOW)
    mlog.debug(message)


def e(message, exit_code=None):
    """Print an error log message."""
    # print_log(message, YELLOW, BOLD)
    mlog.error(message)
    if exit_code is not None:
        exit(exit_code)


def wtf(message, exit_code=1):
    """What a Terrible Failure!"""
    # print_log(message, RED, BOLD)
    mlog.error(message)
    if exit_code is not None:
        exit(exit_code)
