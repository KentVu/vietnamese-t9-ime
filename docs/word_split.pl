use v5.10;
use warnings;
use strict;
use utf8;
use Unicode::Collate;
use Getopt::Long;

binmode(STDIN, ":encoding(UTF-8)") || die "can't binmode to utf-8: $!";
binmode STDOUT, ":encoding(UTF-8) :utf8"
        || die "can't binmode to UTF–8: $!";
binmode STDERR, ":utf8"
        || die "can't binmode to UTF–8: $!";

Getopt::Long::Configure ("bundling");
my @inputs;
my $output;

GetOptions('input|i=s' => \@inputs, 'output|o=s' => \$output);
#GetOptions();

my %words;

for (@inputs) {
    my $inputfh;
    if ($_ eq '-') {
        $inputfh = *STDIN;
    } else {
        open $inputfh, "< :encoding(UTF-8)", $_ or die "can't open $_: $!";
    }

    while (<$inputfh>) {
        next if /^#/;
        chomp;
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
}

my $outfh;
if ($output eq '-') {
    $outfh = *STDOUT;
} else {
    open $outfh, '> :utf8', $output;
}
#for (sort keys %words) {
for (Unicode::Collate->new->sort(keys %words)) {
#    print "$_\n"
    say $outfh $_;
#    say "$_ => $words{$_}"
}
