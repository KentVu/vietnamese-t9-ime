use warnings;
use strict;
use v5.10;
use utf8;
use Unicode::Collate;

binmode(STDIN, ":encoding(UTF-8)") || die "can't binmode to utf-8: $!";
binmode STDOUT, ":encoding(UTF-8) :utf8"
        || die "can't binmode to UTF–8: $!";
binmode STDERR, ":utf8"
        || die "can't binmode to UTF–8: $!";

my %words;
open WORD, "< :encoding(UTF-8)", "morphemes.txt";
while (<WORD>) {
#    print;
    chomp;
    $words{$_} = undef;
}
close(WORD);

while (<>) {
   next if /^#/;
   my @f=split /\s+/;
   for (@f) {
       if (/[-\w]+/) {
           if (!exists $words{$&}) {
               $words{$&} = undef;
               say STDERR "Added $&"
           }
       }
   }
}

for (sort keys %words) {
#    print "$_\n"
    say;
#    say "$_ => $words{$_}"
}
