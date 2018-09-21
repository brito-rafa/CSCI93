-- From Intel - - https://www.altera.com/support/support-resources/design-examples/design-software/vhdl/v_f_add8.html
LIBRARY altera;
USE altera.maxplus2.carry;

LIBRARY ieee;
USE ieee.std_logic_1164.ALL;

LIBRARY WORK;
USE work.gates.ALL;

ENTITY f_add IS    
	generic (
		N : positive := 16
	); 
    PORT(
        x_in    :    IN STD_LOGIC_VECTOR(31 DOWNTO 0);
        y_in    :    IN STD_LOGIC_VECTOR(31 DOWNTO 0);
        c_in    :    IN STD_ULOGIC;  --op code: if 0, it is an add. if 1, it is a sub
        sum     :    BUFFER STD_LOGIC_VECTOR(N DOWNTO 0); -- one extra bit for signal
        sum_out :    BUFFER STD_LOGIC_VECTOR(31 DOWNTO 0); -- the size of vpc register
        c_out   :    BUFFER STD_ULOGIC);
END f_add;

ARCHITECTURE struct OF f_add IS
SIGNAL im  :    STD_LOGIC_VECTOR(N+2 DOWNTO 0);
SIGNAL imi :    STD_LOGIC_VECTOR(N+2 DOWNTO 0);
BEGIN
    c0   : full_add 
           PORT MAP (x_in(0),y_in(0),c_in,sum(0),im(0));
    c01  : carry 
           PORT MAP (im(0),imi(0));
    c    : FOR i IN 1 TO N-2 GENERATE
            c1to2:  full_add PORT MAP (x_in(i),y_in(i),
            imi(i-1),sum(i),im(i));
            c11to16: carry PORT MAP (im(i),imi(i));
           END GENERATE;
    c3   : full_add PORT MAP (x_in(N-1),y_in(N-1),
           imi(N-2),sum(N-1),c_out);

set_signal: process (x_in, y_in, c_in, sum, sum_out, c_out)
begin
	if (c_in = '1') then
		-- this operation was invoked as subtraction
		-- checking if the return was negative
		if (c_out = '0') then
			-- yes, the return was negative, setting the signal to negative
			sum(N) <= '1';
		else
			sum(N) <= '0';
		end if;
	else
			sum(N) <= '0';
	end if;
	-- sum_out(16) is the signal
	sum_out(31 downto 17) <= "000"&x"000"; -- padding with bytes
	sum_out(16 downto 0) <= sum(N downto 0);

end process set_signal; 

END struct;
