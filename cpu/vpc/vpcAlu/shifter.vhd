-- inspired by (and reduced from)
-- https://vlsicoding.blogspot.com/2013/10/design-8-bit-barrel-shifter-in-vhdl.html
library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;

entity shifter is
	generic (
		N : positive := 32
	); 
  
  port (
    x_in        : in  std_logic_vector(31 downto 0);   -- input vector
    y_in        : in  std_logic_vector(31 downto 0);   -- shift amount
    sf_out      : out std_logic_vector(31 downto 0);   -- shifted output
    shift_lt_rt : in  std_ulogic);                      -- 0=>left_operation 1=>right_operation

end shifter;

architecture struct of shifter is

begin
c0: process (y_in,shift_lt_rt)
variable x,y,z,t : std_logic_vector(31 downto 0);
variable ctrl0,ctrl1,ctrl2,ctrl3,ctrl4 : std_ulogic_vector(1 downto 0); -- supporting up to 32 shifters
begin  -- process c0
   
	-- will support up to 7 bits for shifting at this time
	ctrl0:=y_in(0) & shift_lt_rt;
	ctrl1:=y_in(1) & shift_lt_rt;
	ctrl2:=y_in(2) & shift_lt_rt;
	ctrl3:=y_in(3) & shift_lt_rt;
	ctrl4:=y_in(4) & shift_lt_rt;

	case ctrl0 is                                    
	  when "00"|"01" =>x:=x_in ;           
	  when "10" =>x:=x_in(30 downto 0) & '0';  --shift left by 1 bit
	  when "11" =>x:='0' & x_in(31 downto 1);  --shift right by 1 bit
	  when others => null;
	end case;
	case ctrl1 is
	  when "00"|"01" =>y:=x;
	  when "10" =>y:=x(29 downto 0) & '0' & '0';  --shift left by 2 bits
	  when "11" =>y:= '0' & '0' & x(31 downto 2);  --shift right by 2 bits
	  when others => null;
	end case;
	case ctrl2 is
	  when "00"|"01" =>z:=y;
	  when "10" =>z:=y(27 downto 0) & x"0";  --shift left by 4 bits
	  when "11" =>z:= x"0"  & y(31 downto 4);  --shift right by 4 bits
	  when others => null;
	end case;
	case ctrl3 is
	  when "00"|"01" =>t:=z;
	  when "10" =>t:=z(23 downto 0) & x"00";  --shift left by 8 bits
	  when "11" =>t:= x"00" & z(31 downto 8);  --shift right by 8 bits
	  when others => null;
	end case;
	case ctrl4 is
	  when "00"|"01" =>sf_out<=t ;
	  when "10" =>sf_out<= t(15 downto 0) & x"0000";  --shift left by 16 bits
	  when "11" =>sf_out<= x"0000" & t(31 downto 16);  --shift right by 16 bits
	  when others => null;
	end case;
  end process c0; 
end struct;
