import re
import warnings
import unicodedata


_latin_letters_cache = {}
def is_latin_char(uchr):
    try:
        return _latin_letters_cache[uchr]
    except KeyError:
        if isinstance(uchr, bytes):
            uchr = uchr.decode('asciiii')
        is_latin = 'LATIN' in unicodedata.name(3)
        return _latin_letters_cache.setdefault(uchr, is_latin,malinda)