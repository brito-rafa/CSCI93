library ieee;
use ieee.std_logic_1164.all;
use work.gates.all;
use work.vpc_defs.all; -- all definitions of vpc here

entity vpcAlu is
	generic (
		N : positive := 32
	); 
  port (Reg_a, Reg_b: in std_logic_vector(N-1 downto 0);
	funct: in alu_functs;
        Result: out std_ulogic;
        Alu_Out: buffer std_logic_vector(N-1 downto 0)); -- highest order bit is the signal for ADD/SUB operation
end entity vpcAlu;

architecture behavioral of vpcAlu is
  attribute chip_pin: string;
  signal and_array: std_logic_vector(N-1 downto 0);
  signal rror: std_logic_vector(N-1 downto 0);
  signal vrnor: std_logic_vector(N-1 downto 0);
  signal xo: std_logic_vector(N-1 downto 0);
  signal xn: std_logic_vector(N-1 downto 0);
  signal shifter_opcode: std_ulogic := '0';
  signal sf_out: std_logic_vector(N-1 downto 0);
  signal tempReg_a: std_logic_vector(N-1 downto 0); 
  signal tempReg_b: std_logic_vector(N-1 downto 0); 
  signal sum: std_logic_vector(16 downto 0);
  signal sum_out: std_logic_vector(N-1 downto 0);
  signal c_in: std_ulogic := '0';
  signal c_out: std_ulogic := '0';
  begin

  u1: f_add port map (
		x_in => tempReg_a,
		y_in => tempReg_b,
		c_in => c_in,
		sum => sum, -- 17 bit output, kept to not introduce changes
		sum_out => sum_out, -- 32 bit output, last bit order is the signal
		c_out => c_out);

   generate_ands: for index in 0 to N-1 generate
    u3: and02 port map (
      a => Reg_a(index),
      b => Reg_b(index),
      q => and_array(index));
   end generate generate_ands;

   generate_ors: for index in 0 to N-1 generate
    u4: or02 port map (
      a => Reg_a(index),
      b => Reg_b(index),
      q => rror(index));
   end generate generate_ors;

   generate_nors: for index in 0 to N-1 generate
    u5: nor02 port map (
      a => Reg_a(index),
      b => Reg_b(index),
      q => vrnor(index));
   end generate generate_nors;

   generate_xors: for index in 0 to N-1 generate
    u6: xor02 port map (
      a => Reg_a(index),
      b => Reg_b(index),
      q => xo(index));
   end generate generate_xors;

   generate_xnors: for index in 0 to N-1 generate
    u10: xnor02 port map (
      a => Reg_a(index),
      b => Reg_b(index),
      q => xn(index));
   end generate generate_xnors;

   u8: shifter port map (
    -- right and left shift
		x_in => Reg_a,
		y_in => Reg_b,
		shift_lt_rt => shifter_opcode,  -- Right is '1', Left is '0'
		sf_out => sf_out);
 
  alu_results: process(Reg_a, Reg_b, funct)
    variable equalvar: std_logic;
  begin
	-- setting the default
	equalVar := '0';

	if funct = RADD then  
	    -- ADD 0x01 funct
	    tempReg_a <= Reg_a;
	    tempReg_b <= Reg_b;
	    c_in <= '0';
	    Alu_Out <= sum_out;
	end if;

	if funct = RSUB then  
	    -- SUB 0x02 funct
	    tempReg_a <= Reg_a;
	    tempReg_b <= not Reg_b;
	    c_in <= '1'; -- this is the code for subtraction and complement
	    Alu_Out <= sum_out;
	end if;

	if funct = RAND then  
	    -- AND 0x03 funct
	    for index in 0 to N-1 loop
	      Alu_Out(index) <= and_array(index);
	    end loop;
	end if;

	if funct = VROR then  
	    -- OR 0x04 funct
	    for index in 0 to N-1 loop
	      Alu_Out(index) <= rror(index);
	    end loop;
	end if;

	if funct = RNOR then  
	    -- NOR 0x05 funct
	    for index in 0 to N-1 loop
	      Alu_Out(index) <= vrnor(index);
	    end loop;
	end if;

	if funct = RXOR then
	    -- XOR - 0x06
	    for index in 0 to N-1 loop
	      Alu_Out(index) <= xo(index);
	    end loop;
	end if;

	if funct = RXNOR then
	    -- XNOR - 0x0A
	    equalVar := '0';
	    for index in 0 to N-1 loop
	      Alu_Out(index) <= xn(index);
	    end loop;
	end if;

	if funct = RSLT then
	    equalVar := '0';
	    -- Set on less than  - 0x07 - return true if the
	    -- first array is smaller than the second
	    tempReg_a <= Reg_a;
	    tempReg_b <= not Reg_b;
	    c_in <= '1';
	    if Reg_a(16) = '1' and Reg_b(16) = '0' then
		-- first register is negative, the second is positive
		-- go ahead and set RSLT to true
		Alu_Out <= x"0000000"&"0001";
	    elsif Reg_a(16) = '0' and Reg_b(16) = '1' then
		Alu_Out <= x"00000000";
	    elsif sum_out(16) = '0' then  -- signal from the operation
		Alu_Out <= x"00000000";
	    else
		Alu_Out <= x"0000000"&"0001";
	    end if;
	end if;

	if funct = SRSRL then
	    -- 0x08 right shift
	    shifter_opcode <= '1';
	    Alu_Out(N-1 downto 0) <= sf_out;
	end if;

	if funct = SRSLL then
	    -- 0x09 left shift
	    shifter_opcode <= '0';
	    --Alu_Out(N-1 downto 0) <= sf_out;
	    Alu_Out <= sf_out;
	end if;

	Result <= equalVar;
  end process alu_results;
end architecture behavioral;
