Gringotts Manual Test Cases
===========================

balance
-------

=== setup
before every test: empty inventory

=== 1
add emerald directly

    /money

verify that balance is 1

=== 2
* add 10 emeralds
* verify balance 10

=== 3
* add stack of emeralds
* verify balance 64

=== 4
* add 2 stacks of emeralds
* verify balance 128

=== 5
* add emerald block
* verify balance 9

=== 6
* add stack of emerald blocks
* verify balance 576

add money
---------

=== 1
    /money
    /moneyadm add 1 player
verify correct, repeat

=== 2
    /money
    /moneyadm add 9 player
verify correct, repeat

=== 3
    /money
    /moneyadm add 10 player
verify correct, repeat

=== 4
    /money
    /moneyadm add 1000 player
verify correct, repeat

=== 5
    /money
    /moneyadm add 1000000 player
should be more than capacity
verify money unchanged


remove money
------------

=== setup
before every test

    /moneyadm add 1000

=== 1
    /money
    /moneyadm rm 1
verify, repeat

=== 2
    /money
    /moneyadm rm 9
verify, repeat

=== 3
    /money
    /moneyadm rm 10
verify, repeat

=== 4
    /money
    /moneyadm rm 1000000
verify balance unchanged
