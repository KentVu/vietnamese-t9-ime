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

Specifications (Stories):
===========
### Definitions:
* Program states: These are the program states:
  1. Inputting
  1. Confirmed
* Candidates: ...
* numSeq: number sequence: the sequence of input keys' digit/char(?). (What should we call the Star/Sharp key?)

### Specs:
1. [ ] User should interact mainly with the keypad (other means of interacting is optionally supported).
        (Keypad-centric accessibility: make use all the key on the keypad, prevent actions outside of the keypad (dialpad)).
2. [ ] Uppercase should only occur at first letter of words.
3. [ ] User should be able to select from a set of displayed candidates.
4. [x] When inputting, the possible candidates should be displayed.
5. [ ] When confirm button is pressed, the selected candidate should be outputted.  
    1. [ ] If no candidate is being selected then the first candidate will be used.
    2. [ ] If no candidates found, return the entered numSeq.
    3. [ ] The numSeq should be one of the candidates.
6. [ ] If dawg has been built, use that dawg to minimize app start time.

Basic Design:
===========
The UI will follow the MVP design pattern:
* Make the Activity implement the [View interface](lib/src/main/java/com/vutrankien/t9vietnamese/lib/View.kt)
* [Presenter](lib/src/main/java/com/vutrankien/t9vietnamese/lib/Presenter.kt): Receive UI event,
 wiring action with the UI(View)
* Model? - Everything else?

### Design philosophy:
This project *honor* [7 virtues of good object](https://www.yegor256.com/2014/11/20/seven-virtues-of-good-object.html) by yegor256.

Things to consider
==================
* [x] Which standard to decompose vietnamese (syllable=ascii+accents)
  * http://www.unicode.org/reports/tr15/tr15-23.html#Decomposition
* [x] Follows the English T9 convention of key assignment

### Why I chose dawg for implementation of trie?
Because it's the only implementation that support persisting (serializing), and fast!

TODO
====
* [x] ~~Decompose accented character~~.
* [x] Implement number-to-word based on wordlist (saved in database).
* [ ] Implement select candidate key.
* [ ] Support suggestion
* [ ] Flexibility via settings (accents right after character/accent at end of word)
* [ ] Support add new word to dictionary
* [ ] Flick input mode (for registering new word)
* [ ] [Frecency](https://developer.mozilla.org/en-US/docs/Mozilla/Tech/Places/Frecency_algorithm) algorithm?


References-Acknowledgement
==========
* Vietnamese wordlist : http://www.informatik.uni-leipzig.de/~duc/software/misc/wordlist.html
* Xu ly Tieng Viet wikia: http://xltiengviet.wikia.com/
* https://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html
* syllables https://gist.github.com/hieuthi/0f5adb7d3f79e7fb67e0e499004bf558
* spell check dictionary https://saomaicenter.org/en/blog/access-tech/custom-dictionary-for-word
* https://github.com/yweweler/c-t9
