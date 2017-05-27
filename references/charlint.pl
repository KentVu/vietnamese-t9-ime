#!/usr/bin/perl

# Copyright notice:
#    (c) Copyright Keio University 1999-
#    This software is made available under the terms of the
#    W3C Software Licence available at
#    http://www.w3.org/Consortium/Legal/copyright-software.


# Acknowledgements: Mark Davis for various discussions about Unicode TR #15
#                              and for the test suite
#                   Paul Hoffman for suggestions, encouragement, and bug reports
#                   Roland Mas for how to use Storable
#                   James Briggs and Masayasu Ishikawa for error reports
#                   Tim Bray for help with CDATA
#                   Kai Henningsen for finally getting me to clean up
#                                      for 'use strict' and 'perl -w'
#                   Roozbeh Pournader for proposing to deal with HTML &#Xhhhh;
#                   Tim Kempton for nudging me to update to Unicode 5.2.0

# Author:
# MJD Martin J. Du"rst, duerst@w3.org (1999-2005)
#                       duerst@it.aoyama.ac.jp (2005-2009)

my $version = 'Version 0.55';

# History:
# 2009/11/28: 0.55, updated to Unicode Version 5.2.0                 MJD
# 2002/06/24: 0.54, improving -nf16check (compiler warnings, speed)  MJD
# 2002/06/08: 0.53, added -nf16check data file production            MJD
# 2002/08/23: 0.52, changed default file to UnicodeData.txt          MJD
# 2002/05/21: 0.51, added option -nX (use for HTML only!)            MJD
# 2002/04/03: 0.50, updated for 3.2.0; added -F951; added -c         MJD
# 2001/10/03: 0.49, code cleanup for use strict and -w               MJD
# 2001/04/01: 0.48, updated for 3.1.0 (final)                        MJD
# 2001/03/07: 0.47, YOD WITH HIRIQ corrigendum                       MJD
# 2000/12/19: 0.46, updated for 3.1.0 (beta)                         MJD
# 2000/11/12: 0.45, bug fix for CJK extension A                      MJD
# 2000/11/09: 0.44, implemented -s/-S (Storable data)                MJD
# 2000/11/05: 0.43, implemented -K (kompatibility decomposition)     MJD
# 2000/11/05: 0.42, updated for 3.0.1, fixed line ends               MJD
# 2000/11/05: 0.41, added 2000 to copyright, tested CVS commit       MJD
# 2000/08/03: 0.40, added Hangul support and did quite some testing  MJD
# 2000/08/02: 0.37, added -x and -X for decomposition                MJD
# 2000/07/27: 0.36, fixed a bug for non-starter decompositions       MJD
# 2000/07/24: 0.35, adapted exclusions to 3.0.0 final (+Tibetan)     MJD
# 2000/07/24: 0.34, bug fix for $CombClass{ch}; implemented -C       MJD
# 1999/08/16: 0.33, updated for second version of 3.0.0.beta         MJD
# 1999/07/01: 0.32, adapted surrogates/exclusions to 3.0.0.beta      MJD
# 1999/06/25: 0.31, fixed reordering bug, going public               MJD
# 1999/06/23: 0.30, preparation for W3C member test, without Hangul  MJD

# CVS last revised $Date: 2009/12/02 18:12:40 $ by $Author: rishida $

#package CHARLINT;   ## tried these, but need more time to get it right
#use diagnostics;
use strict;
use Storable;

# Global variables (options and data arrays)
use vars qw($OPTB $OPTC $OPTD $OPTE $OPTK
	$OPTN $OPTP $OPTS $OPTU $OPTX $OPTYWH
	$OPTb $OPTc $OPTd $OPTf $OPTF951 $OPTh
        $OPTn $OPTnX $OPTnf16 $OPTo $OPTq $OPTs $OPTv $OPTx
	%CombClass %CompCano %DecoCano %DecoCanoData
	%DecoCanoRest %DecoKompData %DecoKompKind %exists
        %NFCforbid);


#
#   SUBROUTINES
#

sub addtext {
    my ($r, $t) = @_;
    $$r =  $$r ? ($$r."; ".$t) : $t;
}

# Check problems in UTF-8

sub CheckUTF8 {
    my ($s) = @_;
    my ($return) = "";
    my ($st);
    if ($s =~ /[\355][\240-\257][\200-\277][\355][\260-\277][\200-\277]/) {
        &addtext (\$return, "surrogate pair") if !$OPTU;
    }
    if ($s =~ /[\355][\240-\277][\200-\277]/) {
        &addtext (\$return, "single surrogate") if !$OPTU;
    }
    if ($s =~ /[\300-\301][\200-\277]/) {
        &addtext (\$return, "ASCII in 2 bytes") if !$OPTU;
    }
    if ($s =~ /[\340][\200-\237][\200-\277]/) {
        &addtext (\$return, "3 bytes instead of 2 or less") if !$OPTU;
    }
    if ($s =~ /[\360][\200-\217][\200-\277]{2}/) {
        &addtext (\$return, "4 bytes instead of 3 or less") if !$OPTU;
    }
    if ($s =~ /[\370][\200-\207][\200-\277]{3}/) {
        &addtext (\$return, "5 bytes instead of 4 or less") if !$OPTU;
    }
    if ($s =~ /[\374][\200-\203][\200-\277]{4}/) {
        &addtext (\$return, "6 bytes instead of 5 or less") if !$OPTU;
    }
    # non-synchronized cases
    $s =~ s{   [\000-\177]
             | [\300-\337][\200-\277]
             | [\340-\357][\200-\277]{2}
             | [\360-\367][\200-\277]{3}
             | [\370-\373][\200-\277]{4}
             | [\374-\375][\200-\277]{5}
           }{}gx;
    # forbidden bytes
    if ($s =~ /[\376\377]/) {
        &addtext (\$return, "0xFE or 0xFF byte") if !$OPTU;
    }
    if ($s ne "") {
        &addtext (\$return, "synchronization problem") if !$OPTU;
    }
    $return;
} # end CheckUTF8

sub exists {
    my $s = shift;
    return ($exists{$s}
        or ("\344\270\200" le $s and $s le "\351\277\213")          # CJK
        or ("\352\260\200" le $s and $s le "\355\236\243")          # Hangul
        or ("\343\220\200" le $s and $s le "\344\266\265")          # CJK Extension A
        or ("\xF0\xA0\x80\x80" le $s and $s le "\xF0\xAA\x9B\x96")  # CJK Extension B
        or ("\xF0\xAA\x9C\x80" le $s and $s le "\xF0\xAB\x9C\xB4")) # CJK Extension C
}

sub CheckExists {
    my ($s) = @_;
    my $news = join "",
       grep (&exists($_), $s =~ m/([\000-\177]|[\300-\377][\200-\277]+)/go);
    return ($s ne $news);
} # end CheckExists

sub CheckPrivate {
    my ($s) = @_;
    if ($s =~ /[\356][\200-\277]{2}|[\357][\200-\237][\200-\277]/) {
        return "BMP";
    }
    if ($s =~ /[\363][\260-\277][\200-\277]{2}/) {
        return "plane 15";
    }
    if ($s =~ /[\364][\200-\217][\200-\277]{2}/) {
        return "plane 16";
    }
} # end CheckPrivate


#### convert hex to UTF-8

sub hex2utf8 {
    num2utf8 (hex($_[0]));
} # end hex2utf8

sub Xhex2utf8 {      #### avoid to covert <>&"
    my ($t) = hex($_[0]);
    if ($t < 0x40)  { return "&#x".$_[0].";"; }
    num2utf8 ($t);
} # end Xhex2utf8

sub Xnum2utf8 {      #### avoid to covert <>&"
    my ($t) = @_;
    if ($t < 64)  { return "&#".$t.";"; }
    num2utf8 ($t);
} # end Xnum2utf8

#### convert number to UTF-8

sub num2utf8 {
    my ($t) = @_;
	my ($trail, $firstbits, @result);

    if    ($t<0x00000080) { $firstbits=0x00; $trail=0; }
    elsif ($t<0x00000800) { $firstbits=0xC0; $trail=1; }
    elsif ($t<0x00010000) { $firstbits=0xE0; $trail=2; }
    elsif ($t<0x00200000) { $firstbits=0xF0; $trail=3; }
    elsif ($t<0x04000000) { $firstbits=0xF8; $trail=4; }
    elsif ($t<0x80000000) { $firstbits=0xFC; $trail=5; }
    else {
        die "Too large scalar value, cannot be converted to UTF-8.\n";
    }
    for (1 .. $trail) {
        unshift (@result, ($t & 0x3F) | 0x80);
        $t >>= 6;         # slight danger of non-portability
    }
    unshift (@result, $t | $firstbits);
    pack ("C*", @result);
} # end num2utf8


sub utf82ncr {   # works for more than one character
    my $r;
    foreach my $c (splitutf8(shift)) {
        $r .= ($c =~ /[\040-\177]/) ? $c : sprintf ("&#x%lX;",&utf82num($c));
    }
    $r;
} # end utf82ncr


#### convert UTF-8 to number

sub utf82num {
    my (@t, $t, $result);
	my $trail;
    @t = unpack ("C*", $_[0]);
    $t = shift (@t);
    if    ($t<0x80) { $result= $t       ; $trail=0; }
    elsif ($t<0xC0) { die "Illegal leading byte in UTF-8.\n"; }
    elsif ($t<0xE0) { $result= $t & 0x1F; $trail=1; }
    elsif ($t<0xF0) { $result= $t & 0x0F; $trail=2; }
    elsif ($t<0xF8) { $result= $t & 0x07; $trail=3; }
    elsif ($t<0xFC) { $result= $t & 0x03; $trail=4; }
    elsif ($t<0xFE) { $result= $t & 0x01; $trail=5; }
    else            { die "Illegal byte in UTF-8.\n"; }

    if ($trail != $#t + 1) { die "Not right number of trailing bytes.\n"; }
    while ($trail--) {
        # maybe check for 01xxxxxx
        $result <<= 6;
        $result += 0x3F & shift (@t);
    }
    return $result;
} # end utf82num

#### variant of hex2utf8 to get rid of spaces
sub spacehex2utf8 {
    my ($t) = @_;
    return "" if ($t eq " ");
    hex2utf8($t);
} # end spacehex2utf8

#### split an utf-8 string into codepoints
sub splitutf8 {
    split (/(?=[\000-\177\300-\377])/, shift);
} # end splitutf8


#### canonical sort of combining marks
# input: utf-8 string
# return: utf-8 string
sub sortCano {
    my @a = @_;
    my ($i, $ccHere, $ccPrev, $temp);

    return @a  if (@a <= 1);
    for ($i=1; $i < @a; $i++) {
        $ccHere = $CombClass{$a[$i]};
        $ccPrev = $CombClass{$a[$i-1]};
        $ccHere = 0  if (!defined($ccHere));
        $ccPrev = 0  if (!defined($ccPrev));
        if ($ccHere != 0  &&  $ccPrev > $ccHere) {
            $temp    = $a[$i];     # exchange
            $a[$i]   = $a[$i-1];
            $a[$i-1] = $temp;
            $i -= 2  if ($i > 1);  # backtrack and check again
        }
    }
    return @a;
} # end sortCano

#
# add algorithmic Hangul and unity transform to DecoCano lookup
#
sub DecoCano {
    my ($s) = @_;
    my $h = utf82num($s);
    if ($h >= 0xAC00 && $h < 0xD7A4) {
        my $hindex = $h - 0xAC00;
        my $l = 0x1100 + $hindex/(21*28);
        my $v = 0x1161 + ($hindex % (21*28)) / 28;
        my $t = $hindex % 28;
        if ($t) {
            return join "", num2utf8($l), num2utf8($v), num2utf8(0x11A7 + $t);
        }
        else {
            return join "", num2utf8($l), num2utf8($v);
        }
    }
    else {
	my $r = $DecoCano{$s};
        return $r if defined $r;
	return $s;
    }
} # end DecoCano

#
# add algorithmic Hangul and unity transform to DecoKomp lookup
#
sub DecoKomp {
    my ($s) = @_;
    my $h = utf82num($s);
    if ($h >= 0xAC00 && $h < 0xD7A4) {
	return DecoCano($s);  #refer to DecoCano for Hangul decomposition
    }
    else {
	my $r = $DecoKompData{$s};
        return $r if defined $r;
	return $s;
    }
} # end DecoKomp

#
# add algorithmic Hangul to CompCano lookup
#
sub CompCano {
    my ($starterCh, $ch) = @_;
    my $s = utf82num($starterCh);
    my $c = utf82num($ch);
    if ($s >= 0x1100 && $s < 0x1113 && $c >= 0x1161 && $c < 0x1176) {
        return num2utf8((($s-0x1100)*21+$c-0x1161) * 28 + 0xAC00);
    }
    elsif ($s >= 0xAC00 && $s < 0xD7A4 && !(($s-0xAC00)%28) && $c >= 0x11A8 && $c < 0x11C3) {
        return num2utf8($s + $c - 0x11A7);
    }
    else {
        return $CompCano{join "", ($starterCh, $ch)};
    }
} # end CompCano

sub printoctal {
    my $s = shift;
    $s =~ s/([\200-\377])/sprintf("\\%lo",ord($1))/ge;
    print STDERR $s;
} # end printoctal

#### output data considering all relevant options
sub printOPT {
    my $t = shift;
    if ($OPTN) {
        # hexadecimal numeric character references
        $t =~ s/([\300-\377][\200-\277]+)/utf82ncr($1)/eg;
    }
    elsif ($OPTo) {
        $t =~ s/([\200-\377])/sprintf("\\%lo",ord($1))/eg;
    }
    print $t;
}


#
# read in base data file
#

sub ReadCharacterDataFile {
	my ($dataFile) = @_;
	my $line = 0;
    open (BASE, $dataFile)
        or die "Cannot open character data file $dataFile.\n";
  BASE:
    while (<BASE>) {
        print STDERR "Reading data file, line $line\n"
			if !(($line % 1000) || $OPTq); $line++;
        chop;
        my ($hex, $name, $category, $combClass, $t4, $dec) = split(/;/);
        #### Check ranges for consistency with handcoded pieces, then skip
        if ($name =~ /^<(.*), (.*)/) {
            if (!(   /^3400;<CJK Ideograph Extension A, First>;Lo;0;L;;;;;N;;;;;$/
                  or /^4DB5;<CJK Ideograph Extension A, Last>;Lo;0;L;;;;;N;;;;;$/
                  or /^4E00;<CJK Ideograph, First>;Lo;0;L;;;;;N;;;;;$/
                  or /^9FCB;<CJK Ideograph, Last>;Lo;0;L;;;;;N;;;;;$/
                  or /^AC00;<Hangul Syllable, First>;Lo;0;L;;;;;N;;;;;$/
                  or /^D7A3;<Hangul Syllable, Last>;Lo;0;L;;;;;N;;;;;$/
                  or /^D800;<Non Private Use High Surrogate, First>;Cs;0;L;;;;;N;;;;;$/
                  or /^DB7F;<Non Private Use High Surrogate, Last>;Cs;0;L;;;;;N;;;;;$/
                  or /^DB80;<Private Use High Surrogate, First>;Cs;0;L;;;;;N;;;;;$/
                  or /^DBFF;<Private Use High Surrogate, Last>;Cs;0;L;;;;;N;;;;;$/
                  or /^DC00;<Low Surrogate, First>;Cs;0;L;;;;;N;;;;;$/
                  or /^DFFF;<Low Surrogate, Last>;Cs;0;L;;;;;N;;;;;$/
                  or /^E000;<Private Use, First>;Co;0;L;;;;;N;;;;;$/
                  or /^F8FF;<Private Use, Last>;Co;0;L;;;;;N;;;;;$/
                  or /^20000;<CJK Ideograph Extension B, First>;Lo;0;L;;;;;N;;;;;$/
                  or /^2A6D6;<CJK Ideograph Extension B, Last>;Lo;0;L;;;;;N;;;;;$/
                  or /^2A700;<CJK Ideograph Extension C, First>;Lo;0;L;;;;;N;;;;;$/
                  or /^2B734;<CJK Ideograph Extension C, Last>;Lo;0;L;;;;;N;;;;;$/
                  or /^F0000;<Plane 15 Private Use, First>;Co;0;L;;;;;N;;;;;$/
                  or /^FFFFD;<Plane 15 Private Use, Last>;Co;0;L;;;;;N;;;;;$/
                  or /^100000;<Plane 16 Private Use, First>;Co;0;L;;;;;N;;;;;$/
                  or /^10FFFD;<Plane 16 Private Use, Last>;Co;0;L;;;;;N;;;;;$/
                 )) {
                die "Problem with data file consistency, line $line: \n\t$_.\n";
            }
        }
        else { # normal line processing
            my $u = &hex2utf8($hex);
                $exists{$u} = 1; # to check characters that exist

            #### Decompositions
            if ($dec eq "") { }  # no decomposition
            elsif ($dec =~ /^<(.*)>(.*)/) { # compatibility
                my $decKind = $1;
                $dec = $2;
                $DecoKompKind{$u} = $decKind;
                $dec =~ s/([0-9a-fA-F]+|\040)/spacehex2utf8($1)/eg;
                $DecoKompData{$u} = $dec;
            }
            else { # canonical decomposition
				$dec =~ s/([0-9a-fA-F]+|\040)/spacehex2utf8($1)/eg;
				$DecoCanoData{$u} = $dec;
				$DecoKompData{$u} = $dec; # add to Komp, to expand everything
            }

            #### Canonical Combining Class
            $CombClass{$u} = $combClass  if ($combClass);
        }
    }
    close (BASE);
    print STDERR "Finished reading character database.\n" if (!$OPTq);

    if ($OPTF951) {
	$DecoCanoData{"\xEF\xA5\x91"} =
            $DecoKompData{"\xEF\xA5\x91"} = "\xE9\x9B\xBB";
    }
    %DecoCanoRest = %DecoCano = %DecoCanoData;    # keep original data as is, and
                                                  # copy to restrict for composition

# list of compatibility kinds for later work
# the idea is to group them (e.g. sub and super) and allow
# normalization by group
#Kompatibility Kind: circle
#Kompatibility Kind: compat
#Kompatibility Kind: final
#Kompatibility Kind: font
#Kompatibility Kind: fraction
#Kompatibility Kind: initial
#Kompatibility Kind: isolated
#Kompatibility Kind: medial
#Kompatibility Kind: narrow
#Kompatibility Kind: noBreak
#Kompatibility Kind: small
#Kompatibility Kind: square
#Kompatibility Kind: sub
#Kompatibility Kind: super
#Kompatibility Kind: vertical
#Kompatibility Kind: wide

    # fully expand canonical decompositions
    my $fixpoint = 0;  # set to false
    while (!$fixpoint) {
        $fixpoint = 1;  # set to true
        print "Fixpoint\n"  if ($OPTd);
        foreach my $key  (sort keys %DecoCano) {
            my @s = splitutf8($DecoCano{$key});
            my $i = 0;
            foreach my $c (@s) {
                my $d;
                if ($d = $DecoCano{$c}) {
                    print "replacing ", utf82ncr($c), " with ",
                                    utf82ncr($d), " in ", utf82ncr($key), "\n"
                                if $OPTd;
                    if ($i > 0) {
                        print STDERR "Rear expansion, against assumptions (use data from V3.0 upwards)!\n";
                        die "Giving up!\n";
                    }
                    $c = $d;
                    $fixpoint = 0;  # changed something; need one more pass 
                }
                $i++;
            }
            $DecoCano{$key} = join "", @s;
        }
    }

    # fully expand kompatibility decompositions
    $fixpoint = 0;  # set to false
    while (!$fixpoint) {
        $fixpoint = 1;  # set to true
        print "Fixpoint\n"  if ($OPTd);
        foreach my $key  (sort keys %DecoKompData) {
            my @s = splitutf8($DecoKompData{$key});
            my $i = 0;
            foreach my $c (@s) {
                my $d;
                if ($d = $DecoKompData{$c}) {
                    print "replacing ", utf82ncr($c), " with ",
                                    utf82ncr($d), " in ", utf82ncr($key), "\n"
                                if $OPTd;
                    if ($i > 0 && splitutf8($d) > 1) {
                        # print STDERR "Rear expansion, against assumptions (use data from V3.0 upwards)!\n";
                        # die "Giving up!\n";
                    }
                    $c = $d;
                    $fixpoint = 0;  # changed something; need one more pass 
                }
                $i++;
            }
            $DecoKompData{$key} = join "", @s;
        }
    }

    # reorder combining marks for canonical decomposition
    foreach my $key  (sort keys %DecoCano) {   # sort to sort the output
        my $s = $DecoCano{$key};
        my $t = join "", sortCano(splitutf8($s));
            if ($s ne $t) {
                print STDERR "Error: Had to reorder ", utf82ncr($key), " from ",
                             utf82ncr($s), " to ", utf82ncr($t), "\n";
                die "Giving up!\n";
        }
        $DecoCano{$key} = $t;
    }

    # reorder combining marks for kompatibility decomposition
    foreach my $key  (sort keys %DecoKompData) {   # sort to sort the output
        my $s = $DecoKompData{$key};
        my $t = join "", sortCano(splitutf8($s));
            if ($s ne $t) {
                print STDERR "Error: Had to reorder ", utf82ncr($key), " from ",
                             utf82ncr($s), " to ", utf82ncr($t), "\n";
                die "Giving up!\n";
        }
        $DecoKompData{$key} = $t;
    }

    # detect singular compositions, add to %NFCforbid
    foreach my $key  (sort keys %DecoCanoRest) {   # sort to sort output
        if (1 == scalar(splitutf8($DecoCanoRest{$key}))) {
            print 'Singular composition: ', utf82ncr($key), ' from ',
                          utf82ncr($DecoCanoRest{$key}), ", removed.\n"
                if ($OPTd);
            delete $DecoCanoRest{$key};
            $NFCforbid{$key} = 1;
        }
    }

    # detect 'non-zero' compositions, add to %NFCforbid
    foreach my $key  (sort keys %DecoCanoRest) {   # sort to sort output
        my @a = splitutf8($DecoCanoRest{$key});
        if ($CombClass{shift @a}) {
                print 'Non-zero composition: ', utf82ncr($key), ' from ',
                        utf82ncr($DecoCanoRest{$key}), ", removed.\n"
                    if ($OPTd);
            delete $DecoCanoRest{$key};
            $NFCforbid{$key} = 1;
        }
    }

    # detect 'all-zero' compositions
    foreach my $key  (sort keys %DecoCanoRest) {   # sort to sort output
        my $allzero = 1;
        my @a = splitutf8($DecoCanoRest{$key});
        foreach my $c (@a) {
            $allzero = 0  if ($CombClass{$c});
        }
        if ($allzero) {
            print 'All-zero composition: ', utf82ncr($key), ' from ',
                          utf82ncr($DecoCanoRest{$key}), ".\n"
                    if ($OPTd);
        }
    }


    my @NoRecomp = (  # Script-specific and post composition, table-based
                   # according to http://www.unicode.org/Public/3.1-Update/CompositionExclusions-3.txt
        '0958',  # DEVANAGARI LETTER QA
        '0959',  # DEVANAGARI LETTER KHHA
        '095A',  # DEVANAGARI LETTER GHHA
        '095B',  # DEVANAGARI LETTER ZA
        '095C',  # DEVANAGARI LETTER DDDHA
        '095D',  # DEVANAGARI LETTER RHA
        '095E',  # DEVANAGARI LETTER FA
        '095F',  # DEVANAGARI LETTER YYA
        '09DC',  # BENGALI LETTER RRA
        '09DD',  # BENGALI LETTER RHA
        '09DF',  # BENGALI LETTER YYA
        '0A33',  # GURMUKHI LETTER LLA
        '0A36',  # GURMUKHI LETTER SHA
        '0A59',  # GURMUKHI LETTER KHHA
        '0A5A',  # GURMUKHI LETTER GHHA
        '0A5B',  # GURMUKHI LETTER ZA
        '0A5E',  # GURMUKHI LETTER FA
        '0B5C',  # ORIYA LETTER RRA
        '0B5D',  # ORIYA LETTER RHA
        '0F43',  # TIBETAN LETTER GHA
        '0F4D',  # TIBETAN LETTER DDHA
        '0F52',  # TIBETAN LETTER DHA
        '0F57',  # TIBETAN LETTER BHA
        '0F5C',  # TIBETAN LETTER DZHA
        '0F69',  # TIBETAN LETTER KSSA
        '0F76',  # TIBETAN VOWEL SIGN VOCALIC R
        '0F78',  # TIBETAN VOWEL SIGN VOCALIC L
        '0F93',  # TIBETAN SUBJOINED LETTER GHA
        '0F9D',  # TIBETAN SUBJOINED LETTER DDHA
        '0FA2',  # TIBETAN SUBJOINED LETTER DHA
        '0FA7',  # TIBETAN SUBJOINED LETTER BHA
        '0FAC',  # TIBETAN SUBJOINED LETTER DZHA
        '0FB9',  # TIBETAN SUBJOINED LETTER KSSA
        # 'FB1D' # HEBREW LETTER YOD WITH HIRIQ:  see below for $OPTYWH
        'FB1F',  # HEBREW LIGATURE YIDDISH YOD YOD PATAH
        'FB2A',  # HEBREW LETTER SHIN WITH SHIN DOT
        'FB2B',  # HEBREW LETTER SHIN WITH SIN DOT
        'FB2C',  # HEBREW LETTER SHIN WITH DAGESH AND SHIN DOT
        'FB2D',  # HEBREW LETTER SHIN WITH DAGESH AND SIN DOT
        'FB2E',  # HEBREW LETTER ALEF WITH PATAH
        'FB2F',  # HEBREW LETTER ALEF WITH QAMATS
        'FB30',  # HEBREW LETTER ALEF WITH MAPIQ
        'FB31',  # HEBREW LETTER BET WITH DAGESH
        'FB32',  # HEBREW LETTER GIMEL WITH DAGESH
        'FB33',  # HEBREW LETTER DALET WITH DAGESH
        'FB34',  # HEBREW LETTER HE WITH MAPIQ
        'FB35',  # HEBREW LETTER VAV WITH DAGESH
        'FB36',  # HEBREW LETTER ZAYIN WITH DAGESH
        'FB38',  # HEBREW LETTER TET WITH DAGESH
        'FB39',  # HEBREW LETTER YOD WITH DAGESH
        'FB3A',  # HEBREW LETTER FINAL KAF WITH DAGESH
        'FB3B',  # HEBREW LETTER KAF WITH DAGESH
        'FB3C',  # HEBREW LETTER LAMED WITH DAGESH
        'FB3E',  # HEBREW LETTER MEM WITH DAGESH
        'FB40',  # HEBREW LETTER NUN WITH DAGESH
        'FB41',  # HEBREW LETTER SAMEKH WITH DAGESH
        'FB43',  # HEBREW LETTER FINAL PE WITH DAGESH
        'FB44',  # HEBREW LETTER PE WITH DAGESH
        'FB46',  # HEBREW LETTER TSADI WITH DAGESH
        'FB47',  # HEBREW LETTER QOF WITH DAGESH
        'FB48',  # HEBREW LETTER RESH WITH DAGESH
        'FB49',  # HEBREW LETTER SHIN WITH DAGESH
        'FB4A',  # HEBREW LETTER TAV WITH DAGESH
        'FB4B',  # HEBREW LETTER VAV WITH HOLAM
        'FB4C',  # HEBREW LETTER BET WITH RAFE
        'FB4D',  # HEBREW LETTER KAF WITH RAFE
        'FB4E',  # HEBREW LETTER PE WITH RAFE
        ## post composition exclusion
        '2ADC',  #  FORKING
        '1D15E', # MUSICAL SYMBOL HALF NOTE
        '1D15F', # MUSICAL SYMBOL QUARTER NOTE
	'1D160', # MUSICAL SYMBOL EIGHTH NOTE
	'1D161', # MUSICAL SYMBOL SIXTEENTH NOTE
	'1D162', # MUSICAL SYMBOL THIRTY-SECOND NOTE
	'1D163', # MUSICAL SYMBOL SIXTY-FOURTH NOTE
	'1D164', # MUSICAL SYMBOL ONE HUNDRED TWENTY-EIGHTH NOTE
	'1D1BB', # MUSICAL SYMBOL MINIMA
	'1D1BC', # MUSICAL SYMBOL MINIMA BLACK
	'1D1BD', # MUSICAL SYMBOL SEMIMINIMA WHITE
	'1D1BE', # MUSICAL SYMBOL SEMIMINIMA BLACK
	'1D1BF', # MUSICAL SYMBOL FUSA WHITE
	'1D1C0'  # MUSICAL SYMBOL FUSA BLACK
    );
    
    if (!$OPTYWH) {
		push @NoRecomp, 'FB1D';   # HEBREW LETTER YOD WITH HIRIQ
    }   # see http://www.unicode.org/unicode/uni2errata/Normalization_Corrigendum.html

    # remove recomposition exclusions, add to %NFCforbid
    foreach my $hex  (@NoRecomp) {
        my $key = &hex2utf8($hex);
        print 'Non-recomposing composition: ', utf82ncr($key), ' from ',
                      utf82ncr($DecoCanoRest{$key}), ", removed.\n"
                if ($OPTd);
        delete $DecoCanoRest{$key};
        $NFCforbid{$key} = 1;
    }

    # replace with fully expanded decompositions
    foreach my $key  (keys %DecoCanoRest) {
        $DecoCanoRest{$key} =  $DecoCano{$key}
            if ($DecoCanoRest{$key} ne $DecoCano{$key});
    }

    # detect duplicate compositions ## should not find any
    if ($OPTd) {
        print "Checking duplicates, takes some time.\n";
        foreach my $key  (sort keys %DecoCanoRest) {   # sort to sort output
            my $s = $DecoCanoRest{$key};
            foreach my $key2  (keys %DecoCanoRest) {
                if (($key lt $key2)   # don't compare with itself, don't warn twice
                    && ($DecoCanoRest{$key2} eq $s)) { # duplicate compositions
                    print STDERR 'Duplicate composition: ', utf82ncr($key),
                                        ' and ', utf82ncr($key2),
                                                 ' to ', utf82ncr($s), "\n";
                            die "Giving up!\n";
                }
                else { next; }  # shortcut loop
            }
        }
    }

    # invert for composition
    foreach my $key  (keys %DecoCanoRest) {   # use reduced decomps for selection
        if (defined $CompCano{$DecoCanoData{$key}})  # use original data
        {                                            # (strictly binary)
            die "Duplicate compositions, giving up.\n";
        }
        $CompCano{$DecoCanoData{$key}} = $key;
    }

    if ($OPTd) {
        foreach my $key  (sort keys %DecoCano) {   # sort to sort output
            print 'Final canonical decomposition: ', utf82ncr($key),
                  ' to ', utf82ncr($DecoCano{$key}), "\n";
        }
        foreach my $key  (sort keys %CompCano) {   # sort to sort output
            print 'Final composition: ', utf82ncr($key),
                  ' to ', utf82ncr($CompCano{$key}), "\n";
        }
        foreach my $key  (sort keys %DecoKompData) {   # sort to sort output
            print 'Final kompatibility decomposition: ', utf82ncr($key),
                  ' to ', utf82ncr($DecoKompData{$key}), "\n";
        }
    }

    print STDERR "Finished processing character data file(s).\n" if (!$OPTq);

} # end ReadCharacterDataFile

#
# store data to file for fast reread
#

sub StoreData {
	my ($dataFile) = @_;
	my %all_data = ();
    
    $all_data{exists} = \%exists;
    $all_data{DecoCano} = \%DecoCano;
    $all_data{CompCano} = \%CompCano;
    $all_data{DecoKompData} = \%DecoKompData;
    $all_data{CombClass} = \%CombClass;

	require Storable;    # in line, to not require module if not needed
    &Storable::nstore (\%all_data, $dataFile);
}

#
# read data from file
#

sub ReadStoredData {
	my ($dataFile) = @_;
	require Storable;    # in line, to not require module if not needed
    my %all_data = %{&Storable::retrieve ($dataFile)};
    
    %exists = %{$all_data{exists}};
    %DecoCano = %{$all_data{DecoCano}};
    %CompCano = %{$all_data{CompCano}};
    %DecoKompData = %{$all_data{DecoKompData}};
    %CombClass = %{$all_data{CombClass}};
}

#
# Print instructions (-h)
#

sub PrintInstructions {
    print STDERR <<EOF;

charlint (code name Charly)
Character Check and Normalization
According to W3C and Unicode Specifications
===========================================

$version

(c) Keio University 1999, see perl source or
    http://www.w3.org/International/charlint for details

Available options:

(options prefixed by # are currently not available)
-b: Remove initial 'Byte Order Mark'
-B: Supress warning about initial 'Byte Order Mark'
-c: Detect non-normalized data (but do not normalize)
-C: Do not normalize
-d: Debug: Thoroughly check character data table input
-D: Leave after reading in character data
-e: # remove undefined codepoints
-E: Do not warn about undefined codepoints
-f file: Read data from file (default is UnicodeData.txt;
         please use newest V3.2.0 datafiles)
-F951: Use old (wrong) mapping for U+F951 (use this option
	  if you really need 3.1.0 behaviour)
-h: Prints out this short description
-k: # Warn about compatibility codepoints
-K: Normalize out (i.e. decompose) compatibility codepoints
-n: Accept &#ddddd; and &#xhhhh; on input
        (beware of <![CDATA[, <SCRIPT>, <STYLE>)
-nX: same as -n, plus &#Xhhhh; (use for HTML only!)
-N: Produce &#xhhhh; on output
-nf16check: Produce nf16check data tables
-o: Print out 'unprintable' bytes as \\octal
-p: # Remove stuff in private use areas
-P: Supress checking private use areas
-q: Quiet, don't output progress messages
-s file: Read data from file produced with -S
-S file: Write data to file for fast reload with -s
-u: # Fix UTF-8 (convert or remove)
-U: Supress checking correctness of UTF-8
-v: Print version
-x: Do decomposition only
-X: Don't do decomposition (assume input is decomposed)
-YWH: Treat YOD WITH HIRIQ as precomposed (use this option
	  if you really need 3.0.0 behaviour)

EOF
# end of raw in-place text

# ideas for more options:
# * allow to do kompatibility processing by category
# * warn/remove plane 14 language tag codes and other crap
# * convert crap to what it's supposed to be (difficult)
# * directionality control codes

} # end &PrintInstructions


#
# Print instructions (-h)
#

sub initialize {
	my ($readStoreFile, $writeStoreFile, $dataFile);

	# Read options

	OPTIONS:
	while (@ARGV and $ARGV[0] =~ /^-/) {
		$_ = shift(@ARGV);
		$OPTb= 1, next OPTIONS  if /^-b$/;
		$OPTB= 1, next OPTIONS  if /^-B$/;
		$OPTc= 1, next OPTIONS  if /^-c$/;
		$OPTC= 1, next OPTIONS  if /^-C$/;
		$OPTd= 1, next OPTIONS  if /^-d$/;
		$OPTD= 1, next OPTIONS  if /^-D$/;
		$OPTE= 1, next OPTIONS  if /^-E$/;
		if (/^-f$/) {
			$OPTf = 1;
			$dataFile = shift(@ARGV);
			print STDERR "Using character data file $dataFile.",
				" Maybe not what you intend.\n" if ($dataFile =~ /^-.$/ && !$OPTq);
			next OPTIONS;
		}
		$OPTF951= 1, next OPTIONS  if /^-F951$/;
		$OPTh= 1, next OPTIONS  if /^-h$/;
		$OPTK= 1, next OPTIONS  if /^-K$/;
		$OPTn= 1, next OPTIONS  if /^-n$/;
		$OPTnX=1, next OPTIONS  if /^-nX$/;
		$OPTN= 1, next OPTIONS  if /^-N$/;
                $OPTnf16= 1, next OPTIONS  if /^-nf16check$/;
		$OPTo= 1, next OPTIONS  if /^-o$/;
		$OPTP= 1, next OPTIONS  if /^-P$/;
		$OPTq= 1, next OPTIONS  if /^-q$/;
		if (/^-s$/) {
			$OPTs = 1;
			$readStoreFile = shift(@ARGV);
			    print STDERR "Reading from store file $readStoreFile.",
				" Maybe not what you intend.\n"
                        if ($readStoreFile =~ /^-.$/ && !$OPTq);
			next OPTIONS;
		}
		if (/^-S$/) {
			$OPTS = 1;
			$writeStoreFile = shift(@ARGV);
			    print STDERR "Writing to store file $writeStoreFile.",
				" Maybe not what you intend.\n"
                        if ($writeStoreFile =~ /^-.$/ && !$OPTq);
			next OPTIONS;
		}
		$OPTU= 1, next OPTIONS  if /^-U$/;
		$OPTv= 1, next OPTIONS  if /^-v$/;
		$OPTx= 1, next OPTIONS  if /^-x$/;
		$OPTX= 1, next OPTIONS  if /^-X$/;
		$OPTYWH= 1, next OPTIONS  if /^-YWH$/;    
		print STDERR "Unrecognized argument: \"", $_, "\"; ignored.\n";
	}

	&PrintInstructions() if $OPTh;

	if ($OPTv) {
		print $version, ",\nfor more information, use 'charlint -h'.\n";
	}

	# Read/write data files

        if ($OPTs && $OPTS) {
		die "Cannot read and store at the same time (-s/-S).\n";
	}
        if ($OPTf) {
             if ($OPTs) {
 		die "Conflicting character data sources (-f/-s).\n";
	     }
             else {
                 ReadCharacterDataFile($dataFile);
             }
        }
        else {
             if ($OPTs) {
 		 ReadStoredData ($readStoreFile);
	     }
             else {
		# default amounts to -f UnicodeData.txt
		ReadCharacterDataFile("UnicodeData.txt");
             }
        }
		
	if ($OPTS) {
		StoreData ($writeStoreFile);
	}

	exit 0  if ($OPTD);
}


#
# Produce data for nf16check
#

# Helper function: check whether a combining character attached
# to some other character recombines (and thus, whether the sequence
# is not normalized
sub recombines
{
    my ($start, $end) = @_;
    my $orig = $start . $end;
    my @t = splitutf8(DecoCano($start));
    push @t, $end;
    @t = sortCano(@t);

    my $lastClass = -1;     # this eliminates a special check
    for (my $targetPos = 1; $targetPos < @t; ) {
        my $chClass = $CombClass{$t[$targetPos]};
        $chClass = 0  if (!defined($chClass));
        my $composite = CompCano($t[0], $t[$targetPos]);
        if (defined($composite) && $lastClass < $chClass) {
            $t[0] = $composite;
            splice @t, $targetPos, 1;
        }
        elsif ($chClass == 0) {
            last;
        }
        else {
            $lastClass = $chClass;
            $targetPos++;
        }
    }
    return 0 if ($orig eq (join "", @t));
    return 1;
}

sub produceNF16check 
{
    print "/* produced by charlint.pl, with option -nf16check          */\n";
    print "/* charlint.pl by Martin J. Du\"rst, W3C, 1999-2003          */\n";
    print "/* data derived from Unicode Character Database, see        */\n";
    print "/* http://www.unicode.org/Public/UNIDATA/UCD.html#UCD_Terms */\n";
    
    print "\n#include \"nf16data.h\"\n\n";

    #### combining classes
    print "combiningClass combiningClasses[] = {\n    "; # header
    my $count = 0;
    foreach my $key  (sort keys %CombClass) {
        printf "{0x%05X, %3d}", utf82num($key), $CombClass{$key};
        print (!(++$count % 4) ? ",\n    " : ",  ");
    }
    print "\n  };\nint combiningClassCount = $count;\n"; # ending

    #### recombiners
    # collect all characters potentially relevant for recombination
    my %Recomguesses;
    my %Recombiners;
    foreach my $key (keys %DecoCanoRest) {
        $Recomguesses{$key} = 1;
        my @deco = splitutf8($DecoCanoRest{$key});
        $Recomguesses{shift @deco} = 1;
        foreach my $k (@deco) {
            $Recombiners{$k} = 1;
        }
    }
    # try recombination
    print "\n\nrecombining recombiners[] = {\n    "; # header
    my %Recombases;
    $count = 0;
    foreach my $key (sort keys %Recomguesses) {
        foreach my $key2 (sort keys %Recombiners) {
            if (recombines($key, $key2)) {
                $Recombases{$key} = 1;
                printf "{0x%04X, 0x%04X}", utf82num($key), utf82num($key2);
                print (!(++$count % 4) ? ",\n    " : ",  ");
            }
            if ($key eq $key2) {
                print STDERR "ERROR: assumption that recombases and recombiners ";
                print STDERR "do not overlap is violated: base=", utf82ncr($key),
                             ", combiner=", utf82ncr($key2), "\n";
            }
        }
    }
    print "\n    {0xFFFF, 0xFFFF} /* cieling off */\n";  $count++;
    print "  };\nint recombinerCount = $count;\n";


    #### flags
    print "\n\nunsigned char flags[] = {\n"; # header

    for (my $c=0; $c < 0x1D800; $c++) {
        next  if ($c >= 0x10900 && $c < 0x1D000);  # jump a big piece
        printf "    /* U+%04X */ ", $c  if (!($c % 8));
        my $cu = num2utf8($c);
        # surrogates don't exist, but are separate anyway, so do them first
        if    ($c>=0xD800 && $c<0xDC00 ) { print "HIGH"; }  # high surrogates
        elsif ($c>=0xDC00 && $c<0xE000 ) { print "loww"; }  # low surrogate
        elsif (!&exists($cu))            { print "NoNo"; }  # does not exist
        elsif ($NFCforbid{$cu}==1)       { print "NOFC"; }  # singleton/excluded
        elsif ($CombClass{$cu}) {			    # class > 0
            if ($Recombiners{$cu})       { print "ReCo"; }  # recombining
            else                         { print "NoRe"; }  # not recombining
	}
        elsif ($Recombiners{$cu})        { print "COM0"; }  # class==0, but composing
        elsif ($c>=0x1100 && $c<=0x1112) { print "Hang"; }  # hangul initial consonants
        elsif ($c>=0x1161 && $c<=0x1175) { print "hAng"; }  # hangul medial vowel
        elsif ($c>=0x11A8 && $c<=0x11C2) { print "haNG"; }  # hangul trailing consonants
        elsif ($c>=0xAC00 && $c<=0xD7A3
               && !(($c-0xAC00) % 28))   { print "HAng"; }  # initial/medial syllable
        elsif ($Recombases{$cu})         { print "Base"; }  # base that combines
        else                             { print "simp"; }  # nothing special

        if (!($c % 2))     { print "*16+"; }
        elsif (($c+1) % 8) { print ",  "; }
        else               { printf ",\n", $c+1; }
    }
    print "\n  };\n\n"; # ending
}


#
# Start of main program
#

#
# initialize: read options, read/write character data files
#

&initialize;

if ($OPTnf16) {
    produceNF16check();
    exit; # won't do anything else
}

#
# PROCESS ACTUAL FILE(S)
#

my $line = 0;

LINE:
while (<>) {
    $line++;

    # Convert NCRs on input
    if ($OPTn || $OPTnX) {
        # decimal numeric character references
        s/&#([0-9]+)\;/Xnum2utf8($1)/eg;
        # hexadecimal numeric character references
        s/&#x([0-9a-fA-F]+)\;/Xhex2utf8($1)/eg;
        if ($OPTnX) {
            # hexadecimal numeric character references (HTML only)
            s/&#X([0-9a-fA-F]+)\;/Xhex2utf8($1)/eg;
        }
    }

    # Check BOM
    if ($line == 1) {
        if (!$OPTb && /^\357\273\277/) {
            print STDERR "Initial BOM.\n";
        }
        if ($OPTB) {
            s/^\357\273\277//    # remove initial BOM
        }
    }

    # Check UTF-8
    my $r;
    if (!$OPTU && ($r = CheckUTF8 ($_))) {
		print STDERR "Line $line: Non-UTF-8 ($r).\n";
        die "Giving up!\n";
    }

    # Check nonexisting characters
    if (!$OPTE && CheckExists ($_)) {
        die "Line $line: Non-Existing codepoints.\nGiving up!\n";
    }

    # Check private characters
    if (!$OPTP && ($r = CheckPrivate ($_))) {
		die "Line $line: Private charaters ($r).\nGiving up!\n";
    }

    my @line = splitutf8($_);
    my @lineoriginal = @line;
    my @line2 = ();

    if (!$OPTC) {
        if ($OPTX) {
	    @line2 = @line;
	}
	else {  # decompose
            while (defined(my $s = shift @line)) {
		if ($OPTK) {
	            push @line2, splitutf8(DecoKomp($s));
		}
		else {
	            push @line2, splitutf8(DecoCano($s));
		}
            }
        }

        # canonical reordering
        @line = sortCano(@line2);

        # recompose
        if (!$OPTx && length (@line)) {
            my $lastClass = -1;     # this eliminates a special check
            my $starterPos = 0;
            my $sourceLength = @line;
            my $targetPos = 1;
            my $starterCh = $line[0];
            for (my $sourcePos = 1; $sourcePos < $sourceLength; $sourcePos++) {
                my $ch = $line[$sourcePos];
                my $chClass = $CombClass{$ch};
                $chClass = 0  if (!defined($chClass));
                my $composite = CompCano($starterCh, $ch);
                if (defined($composite) && $lastClass < $chClass) {
                    $line[$starterPos] = $composite;
                    $starterCh = $composite;
                }
                elsif ($chClass == 0) {
                    $starterPos = $targetPos;
                    $starterCh  = $ch;
                    $lastClass  = -1;
                    $line[$targetPos++] = $ch;
                }
                else {
                    $lastClass = $chClass;
                    $line[$targetPos++] = $ch;
                }
            }
            $#line = $targetPos-1;
        } # end of recomposition
        if ($OPTc && join("",@line) ne join("",@lineoriginal)) {
            die "Line $line: Non-normalized data.\nGiving up!\n";
        }
    } #if (!$OPTC)

    printOPT (join "", @line);

} # end while <>
