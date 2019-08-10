# -*- perl -*-
use strict;
use warnings;
use tests::tests;
check_expected (IGNORE_USER_FAULTS => 1, [<<'EOF']);
(cache-effective) begin
(cache-effective) creating cache_test
(cache-effective) opening cache_test
(cache-effective) writing to cache_test
(cache-effective) closing cache_test
(cache-effective) reseting buffer-cache
(cache-effective) opening cache_test
(cache-effective) reading from cache_test
(cache-effective) closing cache_test
(cache-effective) opening cache_test
(cache-effective) reading from cache_test
(cache-effective) closing cache_test
(cache-effective) removing cache_test
(cache-effective) YOUR CACHE IS THE BESTTTTTTT!
(cache-effective) end
cache-effective: exit(0)

EOF
pass;
