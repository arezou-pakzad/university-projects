# -*- perl -*-
use strict;
use warnings;
use tests::tests;
use tests::random;
check_expected (IGNORE_EXIT_CODES => 1, [<<'EOF']);
(cache-read-count) begin
(cache-read-count) creating cache_test
(cache-read-count) opening cache_test
(cache-read-count) writing 100 kB to cache_test
(cache-read-count) closing cache_test
(cache-read-count) YOUR CACHE IS THE BESTTTTTTT!
(cache-read-count) end
EOF
pass;
