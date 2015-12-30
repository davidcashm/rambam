my @htms = `ls *.htm`;

for my $htm (@htms) {
   chomp $htm;
   print "mv $htm r_$htm\n";
   `mv $htm r_$htm`;
}
