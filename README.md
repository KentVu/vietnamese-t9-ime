vietnamese-t9-ime
=================

[![Join the chat at https://gitter.im/KentVu/vietnamese-t9-ime](https://badges.gitter.im/KentVu/vietnamese-t9-ime.svg)](https://gitter.im/KentVu/vietnamese-t9-ime?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/KentVu/vietnamese-t9-ime.svg?branch=master)](https://travis-ci.org/KentVu/vietnamese-t9-ime)
[ ![Download](https://api.bintray.com/packages/kentvu/t9vietnamese/t9-vietnamese-ime/images/download.svg) ](https://bintray.com/kentvu/t9vietnamese/t9-vietnamese-ime/_latestVersion)

An old-fashioned keypad based input method (T9) for Vietnamese, support accents

The T9 input method
===================
(From [Wikipedia](https://en.wikipedia.org/wiki/T9_(predictive_text)#Design) )

> T9's objective is to make it easier to type text messages. It allows words
> to be entered by a single keypress for each letter, as opposed to the
> multi-tap approach used in conventional mobile phone text entry, in which
> several letters are associated with each key, and selecting one letter often
> requires multiple keypresses.

Design goals
============
* Mimic the t9 input!
* Keypad-centric accessibility: make use all the key on the keypad, prevent actions outside of the keypad (dialpad).

Some specs:
===========
* Uppercase should only occur at first letter of words.

Things to consider
==================
* [x] Which standard to decompose vietnamese (syllable=ascii+accents)
  * http://www.unicode.org/reports/tr15/tr15-23.html#Decomposition
* [x] Follows the English T9 convention of key assignment

TODO
====
* [x] ~~Decompose accented character~~.
* [x] Implement number-to-word based on wordlist (saved in database).
* [ ] Support suggestion
* [ ] Flexibility via settings (accents right after character/accent at end of word)
* [ ] Support add new word to dictionary
* [ ] Flick input mode (for registering new word)
* [ ] [Frecency](https://developer.mozilla.org/en-US/docs/Mozilla/Tech/Places/Frecency_algorithm) algorithm


References-Acknowledgement
==========
* Vietnamese wordlist : http://www.informatik.uni-leipzig.de/~duc/software/misc/wordlist.html
* Xu ly Tieng Viet wikia: http://xltiengviet.wikia.com/
* https://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html
