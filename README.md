vietnamese-t9-ime
=================

An old-fashioned keypad based input method (T9) for Vietnamese, support accents

The T9 input method
===================
(From https://en.wikipedia.org/wiki/T9_(predictive_text)#Design )

    T9's objective is to make it easier to type text messages. It allows words to be entered by a single keypress for each letter, as opposed to the multi-tap approach used in conventional mobile phone text entry, in which several letters are associated with each key, and selecting one letter often requires multiple keypresses.

Things to consider
==================
* [ ] Which standard to decompose vietnamese (syllable=ascii+accents)
  * http://www.unicode.org/reports/tr15/tr15-23.html#Decomposition
- [x] Follows the English T9 convention of key assignment

TODO
====
* [ ] Decompose accented character.
- [ ] Build a number-to-syllable dictionary.
- [ ] Support suggestion
- [ ] Flexibility via settings (accents right after character/accent at end of word) 