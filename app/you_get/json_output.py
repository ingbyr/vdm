
import json

from app.you_get.status import write2buf

# save info from common.write2buf_info()
last_info = None

def output(video_extractor, pretty_write2buf=True):
    ve = video_extractor
    out = {}
    out['url'] = ve.url
    out['title'] = ve.title
    out['site'] = ve.name
    out['streams'] = ve.streams
    if pretty_write2buf:
        write2buf(json.dumps(out, indent=4, sort_keys=True, ensure_ascii=False))
    else:
        write2buf(json.dumps(out))

# a fake VideoExtractor object to save info
class VideoExtractor(object):
    pass

def write2buf_info(site_info=None, title=None, type=None, size=None):
    global last_info
    # create a VideoExtractor and save info for download_urls()
    ve = VideoExtractor()
    last_info = ve
    ve.name = site_info
    ve.title = title
    ve.url = None

def download_urls(urls=None, title=None, ext=None, total_size=None, refer=None):
    ve = last_info
    # save download info in streams
    stream = {}
    stream['container'] = ext
    stream['size'] = total_size
    stream['src'] = urls
    if refer:
        stream['refer'] = refer
    stream['video_profile'] = '__default__'
    ve.streams = {}
    ve.streams['__default__'] = stream
    output(ve)

