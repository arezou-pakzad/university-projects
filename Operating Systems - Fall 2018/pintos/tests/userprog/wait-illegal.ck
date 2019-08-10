# -*- perl -*-
use strict;
use warnings;
use tests::tests;
check_expected ([<<'EOF']);
(wait-illegal) begin
(wait-illegal) exec wait for 3
(wait-illegal) exec wait for first_pid
(child-arg-pid) argc = 2
(child-arg-pid) wait returned -1
child-arg-pid: exit(0)
(child-arg-pid) argc = 2
(child-arg-pid) wait returned -1
child-arg-pid: exit(0)
(wait-illegal) wait(first): 0
(wait-illegal) wait(second): 0
(wait-illegal) end
wait-illegal: exit(0)
EOF
pass;
