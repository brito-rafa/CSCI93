library ieee;
use ieee.std_logic_1164.ALL;
package gates is
   component hex
      port(
          number    : in std_ulogic_vector;
          hex_out  : out  std_ulogic_vector);
   end component hex;

   component and02
      port(
          a    : in std_logic;
          b    : in std_logic;
          q    : out  std_logic);
   end component and02;

   component or02
      port(
          a    : in std_logic;
          b    : in std_logic;
          q    : out  std_logic);
   end component or02;

   component nor02
      port(
          a    : in std_logic;
          b    : in std_logic;
          q    : out  std_logic);
   end component nor02;

   component xor02
      port(
          a    : in std_logic;
          b    : in std_logic;
          q    : out  std_logic);
   end component xor02;

   component xnor02
      port(
          a    : in std_logic;
          b    : in std_logic;
          q    : out  std_logic);
   end component xnor02;

   COMPONENT full_add 
      PORT(
          a      : IN STD_LOGIC;
          b      : IN STD_LOGIC;
          c_in   : IN STD_LOGIC;
          sum    : OUT STD_LOGIC;
          c_out  : OUT STD_LOGIC);
   END COMPONENT;
	
   COMPONENT f_add 
	PORT(
		 x_in      : IN STD_LOGIC_VECTOR;
		 y_in      : IN STD_LOGIC_VECTOR;
		 c_in   : IN STD_ULOGIC;
		 sum    : OUT STD_LOGIC_VECTOR;
		 sum_out    : OUT STD_LOGIC_VECTOR;
		 c_out  : OUT STD_ULOGIC);
   END COMPONENT;

   component shifter
	  port (        
	    x_in        : in  std_logic_vector;
	    y_in        : in  std_logic_vector;
	    sf_out      : out std_logic_vector;
	    shift_lt_rt : in  std_logic);  -- 0=>left_operation 1=>right_operation
   end component;



end gates;
