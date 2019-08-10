# -*- perl -*-
use strict;
use warnings;
use tests::tests;
check_expected ([<<'EOF']);
(read-illegal) begin
(read-illegal) open "sample.txt"
(child-arg-fd) argc = 2
(child-arg-fd) read returned -1
child-arg-fd: exit(0)
(read-illegal) wait(exec()) = 0
(read-illegal) end
read-illegal: exit(0)
EOF
pass;
