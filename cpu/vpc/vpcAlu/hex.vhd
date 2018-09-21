library ieee;
use ieee.std_logic_1164.all;
use work.gates.all;


entity hex is
  port (
	number: in std_ulogic_vector(3 downto 0);
        hex_out: buffer std_ulogic_vector(0 to 6) );
end entity hex;

-- The pushbuttons are active-low (i.e., the signals are normally high and
--      become low when the pushbuttons are pressed).  They *are* debounced.

-- The segments of the seven-segment LEDs are illuminated when driven with a
--      low signal.

-- Assign pins as follows on the DE2-115:
--      Signal        Pin       Device
--      reset         PIN_R24   KEY3 leftmost pushbutton
--      shift         PIN_N21   KEY2 second to leftmost pushbutton
--      segment7[6]   PIN_AA14    HEX7_D[6] leftmost digit, segment 6
--      segment7[5]   PIN_AG18    HEX7_D[5] leftmost digit, segment 5
--      segment7[4]   PIN_AF17    HEX7_D[4] leftmost digit, segment 4
--      segment7[3]   PIN_AH17    HEX7_D[3] leftmost digit, segment 3
--      segment7[2]    PIN_AG17    HEX7_D[2] leftmost digit, segment 2
--      segment7[1]    PIN_AE17    HEX7_D[1] leftmost digit, segment 1
--      segment7[0]    PIN_AD17    HEX7_D[0] leftmost digit, segment 0

--         0
--       -----
--      |     |
--    5 |     | 1
--      |  6  |
--       -----
--      |     |
--    4 |     | 2
--      |     |
--       -----  
--         3

architecture behavioral of hex is
--  attribute chip_pin: string;
--  attribute chip_pin of reset: signal is "R24";
  begin
  mysegment: process (number, hex_out)
  begin
	case number is
		when "0000"=> hex_out <="0000001";  -- '0'
		when "0001"=> hex_out <="1001111";  -- '1'
		when "0010"=> hex_out <="0010010";  -- '2'
		when "0011"=> hex_out <="0000110";  -- '3'
		when "0100"=> hex_out <="1001100";  -- '4' 
		when "0101"=> hex_out <="0100100";  -- '5'
		when "0110"=> hex_out <="0100000";  -- '6'
		when "0111"=> hex_out <="0001111";  -- '7'
		when "1000"=> hex_out <="0000000";  -- '8'
		when "1001"=> hex_out <="0000100";  -- '9'
		when "1010"=> hex_out <="0001000";  -- 'a'
		when "1011"=> hex_out <="1100000";  -- 'b'
		when "1100"=> hex_out <="0110001";  -- 'c'
		when "1101"=> hex_out <="1000010";  -- 'd'
		when "1110"=> hex_out <="0110000";  -- 'e'
		when "1111"=> hex_out <="0111000";  -- 'f' 
		when others=> hex_out <="1111111"; 
	end case;
  end process mysegment;
end architecture behavioral;
