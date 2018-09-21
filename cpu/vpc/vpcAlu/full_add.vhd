-- from Intel - https://www.altera.com/support/support-resources/design-examples/design-software/vhdl/v_f_add8.html
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

ENTITY full_add IS 
    PORT(
        a     : IN    STD_LOGIC;
        b     : IN    STD_LOGIC;
        c_in  : IN    STD_LOGIC;
        sum   : OUT   STD_LOGIC;
        c_out : OUT   STD_LOGIC);
END full_add;

ARCHITECTURE behv OF full_add IS
BEGIN
    sum <= a XOR b XOR c_in;
    c_out <= (a AND b) OR (c_in AND (a OR b));
END behv;
