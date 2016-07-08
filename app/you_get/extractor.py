#!/usr/bin/env python

from .common import match1, maybe_write2buf, download_urls, get_filename, parse_host, set_proxy, unset_proxy
from .util import log
from . import json_output
import os

from app.you_get.status import write2buf

class Extractor():
    def __init__(self, *args):
        self.url = None
        self.title = None
        self.vid = None
        self.streams = {}
        self.streams_sorted = []

        if args:
            self.url = args[0]

class VideoExtractor():
    def __init__(self, *args):
        self.url = None
        self.title = None
        self.vid = None
        self.streams = {}
        self.streams_sorted = []
        self.audiolang = None
        self.password_protected = False
        self.dash_streams = {}
        self.caption_tracks = {}

        if args:
            self.url = args[0]

    def download_by_url(self, url, **kwargs):
        self.url = url
        self.vid = None

        if 'extractor_proxy' in kwargs and kwargs['extractor_proxy']:
            set_proxy(parse_host(kwargs['extractor_proxy']))
        self.prepare(**kwargs)
        if 'extractor_proxy' in kwargs and kwargs['extractor_proxy']:
            unset_proxy()

        try:
            self.streams_sorted = [dict([('id', stream_type['id'])] + list(self.streams[stream_type['id']].items())) for stream_type in self.__class__.stream_types if stream_type['id'] in self.streams]
        except:
            self.streams_sorted = [dict([('itag', stream_type['itag'])] + list(self.streams[stream_type['itag']].items())) for stream_type in self.__class__.stream_types if stream_type['itag'] in self.streams]

        self.extract(**kwargs)

        self.download(**kwargs)

    def download_by_vid(self, vid, **kwargs):
        self.url = None
        self.vid = vid

        if 'extractor_proxy' in kwargs and kwargs['extractor_proxy']:
            set_proxy(parse_host(kwargs['extractor_proxy']))
        self.prepare(**kwargs)
        if 'extractor_proxy' in kwargs and kwargs['extractor_proxy']:
            unset_proxy()

        try:
            self.streams_sorted = [dict([('id', stream_type['id'])] + list(self.streams[stream_type['id']].items())) for stream_type in self.__class__.stream_types if stream_type['id'] in self.streams]
        except:
            self.streams_sorted = [dict([('itag', stream_type['itag'])] + list(self.streams[stream_type['itag']].items())) for stream_type in self.__class__.stream_types if stream_type['itag'] in self.streams]

        self.extract(**kwargs)

        self.download(**kwargs)

    def prepare(self, **kwargs):
        pass
        #raise NotImplementedError()

    def extract(self, **kwargs):
        pass
        #raise NotImplementedError()

    def p_stream(self, stream_id):
        if stream_id in self.streams:
            stream = self.streams[stream_id]
        else:
            stream = self.dash_streams[stream_id]

        if 'itag' in stream:
            write2buf("    - itag:          %s" % log.swrite2buf(stream_id, log.NEGATIVE))
        else:
            write2buf("    - format:        %s" % log.swrite2buf(stream_id, log.NEGATIVE))

        if 'container' in stream:
            write2buf("      container:     %s" % stream['container'])

        if 'video_profile' in stream:
            maybe_write2buf("      video-profile: %s" % stream['video_profile'])

        if 'quality' in stream:
            write2buf("      quality:       %s" % stream['quality'])

        if 'size' in stream:
            write2buf("      size:          %s MiB (%s bytes)" % (round(stream['size'] / 1048576, 1), stream['size']))

        if 'itag' in stream:
            write2buf("    # download-with: %s" % log.swrite2buf("you-get --itag=%s [URL]" % stream_id, log.UNDERLINE))
        else:
            write2buf("    # download-with: %s" % log.swrite2buf("you-get --format=%s [URL]" % stream_id, log.UNDERLINE))

        write2buf()

    def p_i(self, stream_id):
        if stream_id in self.streams:
            stream = self.streams[stream_id]
        else:
            stream = self.dash_streams[stream_id]

        maybe_write2buf("    - title:         %s" % self.title)
        write2buf("       size:         %s MiB (%s bytes)" % (round(stream['size'] / 1048576, 1), stream['size']))
        write2buf("        url:         %s" % self.url)
        write2buf()

    def p(self, stream_id=None):
        maybe_write2buf("site:                %s" % self.__class__.name)
        maybe_write2buf("title:               %s" % self.title)
        if stream_id:
            # write2buf the stream
            write2buf("stream:")
            self.p_stream(stream_id)

        elif stream_id is None:
            # write2buf stream with best quality
            write2buf("stream:              # Best quality")
            stream_id = self.streams_sorted[0]['id'] if 'id' in self.streams_sorted[0] else self.streams_sorted[0]['itag']
            self.p_stream(stream_id)

        elif stream_id == []:
            write2buf("streams:             # Available quality and codecs")
            # write2buf DASH streams
            if self.dash_streams:
                write2buf("    [ DASH ] %s" % ('_' * 36))
                itags = sorted(self.dash_streams,
                               key=lambda i: -self.dash_streams[i]['size'])
                for stream in itags:
                    self.p_stream(stream)
            # write2buf all other available streams
            write2buf("    [ DEFAULT ] %s" % ('_' * 33))
            for stream in self.streams_sorted:
                self.p_stream(stream['id'] if 'id' in stream else stream['itag'])

        if self.audiolang:
            write2buf("audio-languages:")
            for i in self.audiolang:
                write2buf("    - lang:          {}".format(i['lang']))
                write2buf("      download-url:  {}\n".format(i['url']))

    def p_playlist(self, stream_id=None):
        maybe_write2buf("site:                %s" % self.__class__.name)
        write2buf("playlist:            %s" % self.title)
        write2buf("videos:")

    def download(self, **kwargs):
        if 'json_output' in kwargs and kwargs['json_output']:
            json_output.output(self)
        elif 'info_only' in kwargs and kwargs['info_only']:
            if 'stream_id' in kwargs and kwargs['stream_id']:
                # Display the stream
                stream_id = kwargs['stream_id']
                if 'index' not in kwargs:
                    self.p(stream_id)
                else:
                    self.p_i(stream_id)
            else:
                # Display all available streams
                if 'index' not in kwargs:
                    self.p([])
                else:
                    stream_id = self.streams_sorted[0]['id'] if 'id' in self.streams_sorted[0] else self.streams_sorted[0]['itag']
                    self.p_i(stream_id)

        else:
            if 'stream_id' in kwargs and kwargs['stream_id']:
                # Download the stream
                stream_id = kwargs['stream_id']
            else:
                # Download stream with the best quality
                stream_id = self.streams_sorted[0]['id'] if 'id' in self.streams_sorted[0] else self.streams_sorted[0]['itag']

            if 'index' not in kwargs:
                self.p(stream_id)
            else:
                self.p_i(stream_id)

            if stream_id in self.streams:
                urls = self.streams[stream_id]['src']
                ext = self.streams[stream_id]['container']
                total_size = self.streams[stream_id]['size']
            else:
                urls = self.dash_streams[stream_id]['src']
                ext = self.dash_streams[stream_id]['container']
                total_size = self.dash_streams[stream_id]['size']

            if not urls:
                log.wtf('[Failed] Cannot extract video source.')
            # For legacy main()
            download_urls(urls, self.title, ext, total_size,
                          output_dir=kwargs['output_dir'],
                          merge=kwargs['merge'],
                          av=stream_id in self.dash_streams)
            if not kwargs['caption']:
                write2buf('Skipping captions.')
                return
            for lang in self.caption_tracks:
                filename = '%s.%s.srt' % (get_filename(self.title), lang)
                write2buf('Saving %s ... ' % filename, end="", flush=True)
                srt = self.caption_tracks[lang]
                with open(os.path.join(kwargs['output_dir'], filename),
                          'w', encoding='utf-8') as x:
                    x.write(srt)
                write2buf('Done.')

            # For main_dev()
            #download_urls(urls, self.title, self.streams[stream_id]['container'], self.streams[stream_id]['size'])

        self.__init__()
