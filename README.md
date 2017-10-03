vietnamese-t9-ime
=================

[![Join the chat at https://gitter.im/KentVu/vietnamese-t9-ime](https://badges.gitter.im/KentVu/vietnamese-t9-ime.svg)](https://gitter.im/KentVu/vietnamese-t9-ime?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

An old-fashioned keypad based input method (T9) for Vietnamese, support accents

The T9 input method
===================
(From https://en.wikipedia.org/wiki/T9_(predictive_text)#Design )

    T9's objective is to make it easier to type text messages. It allows words to be entered by a single keypress for each letter, as opposed to the multi-tap approach used in conventional mobile phone text entry, in which several letters are associated with each key, and selecting one letter often requires multiple keypresses.

Things to consider
==================
* [x] Which standard to decompose vietnamese (syllable=ascii+accents)
  * http://www.unicode.org/reports/tr15/tr15-23.html#Decomposition
* [x] Follows the English T9 convention of key assignment
number to wordlist query

TODO
====
* [x] ~~Decompose accented character~~.
* [x] Implement number-to-word based on wordlist (saved in database).
* [ ] Support suggestion
* [ ] Flexibility via settings (accents right after character/accent at end of word)
* [ ] Support add new word to dictionary
* [ ] Flick input mode (for registering new word)


References
==========
* Vietnamese wordlist : https://github.com/duyetdev/vietnamese-wordlist
* https://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html
