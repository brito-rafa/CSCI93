library ieee;
use ieee.std_logic_1164.all;
library cscie93;
use cscie93.all;
library vpcAlu;
use work.vpc_defs.all; -- all definitions of vpc here
-- This file should be used for the DE2-115 board ONLY
entity vpc is
   port (
	-- vpc input
       -- CLOCK
       clk50mhz : in std_logic;
       -- PS/2 PORT
       ps2_clk : in std_logic;
       ps2_data : in std_logic;
       -- LCD
       lcd_en : out std_logic;
       lcd_on : out std_logic;
       lcd_rs : out std_logic;
       lcd_rw : out std_logic;
       lcd_db : inout std_logic_vector(7 downto 0);
       -- RS232
       rs232_rxd : in std_logic;
       rs232_txd : out std_logic;
       rs232_cts : out std_logic;
       -- SSRAM interface
       sram_dq : inout std_logic_vector (15 downto 0);
       sram_addr : out std_logic_vector(19 downto 0);
       sram_ce_N : out std_logic;
       sram_oe_N : out std_logic;
       sram_we_N : out std_logic;
       sram_ub_N : out std_logic;
       sram_lb_N : out std_logic;

	--- vpc parameters
	seg7, seg6, seg5, seg4, seg3, seg2, seg1, seg0 : buffer std_ulogic_vector(0 to 6);
	ledr0, ledr1, ledr2, ledr3, ledr4, ledr5, ledr6, ledr7, ledr8, ledr9, ledr10, ledr11, ledr12, ledr13, ledr14, ledr15, ledr16, ledr17, ledg0, ledg1, ledg2, ledg3, ledg4, ledg5, ledg6, ledg7 : buffer std_ulogic;
       clock_hold : in std_ulogic;
       clock_step : in std_ulogic;
       mem_suspend : in std_ulogic;
       key_mem_reset : in std_ulogic;
       throttle : in std_logic_vector(0 to 5);
	-- cpu
	ir_suspend : in std_ulogic;
	key_cpu_reset : in std_ulogic;
	drivesegment : in std_logic_vector(0 to 7) 

); end vpc;

architecture default of vpc is
   attribute chip_pin : string;
   attribute chip_pin of clk50mhz : signal is "Y2";
   attribute chip_pin of ps2_clk : signal is "G6";
   attribute chip_pin of ps2_data : signal is "H5";
   attribute chip_pin of lcd_on : signal is "L5";
   attribute chip_pin of lcd_en : signal is "L4";
   attribute chip_pin of lcd_rw : signal is "M1";
   attribute chip_pin of lcd_rs : signal is "M2";
   attribute chip_pin of lcd_db : signal is "M5,M3,K2,K1,K7,L2,L1,L3";
                                                 
   attribute chip_pin of rs232_rxd : signal is "G12";
   attribute chip_pin of rs232_txd : signal is "G9";
   attribute chip_pin of rs232_cts : signal is "G14";
   attribute chip_pin of sram_dq : signal is "AG3,AF3,AE4,AE3,AE1,AE2,AD2,AD1,AF7,AH6,AG6,AF6,AH4,AG4,AF4,AH3";
   attribute chip_pin of sram_addr : signal is "T8,AB8,AB9,AC11,AB11,AA4,AC3,AB4, AD3, AF2, T7, AF5, AC5, AB5, AE6, AB6, AC7, AE7, AD7, AB7";
   attribute chip_pin of sram_ce_N : signal is "AF8";
   attribute chip_pin of sram_oe_N : signal is "AD5";
   attribute chip_pin of sram_we_N : signal is "AE8";
   attribute chip_pin of sram_ub_N : signal is "AC4";
   attribute chip_pin of sram_lb_N : signal is "AD4";

   attribute chip_pin of clock_hold  : signal is "AB28"; -- SW00
   attribute chip_pin of mem_suspend : signal is "AC28"; -- SW01
   attribute chip_pin of ir_suspend : signal is "AC27"; -- SW02
   attribute chip_pin of drivesegment : signal is "AB24, AC24, AB25, AC25, AB26, AD26, AC26, AB27";  -- SW11-SW4
   attribute chip_pin of key_mem_reset   : signal is "N21";  -- KEY2
   attribute chip_pin of clock_step : signal is "M21";  -- KEY1
   attribute chip_pin of key_cpu_reset : signal is "M23";  -- KEY0
   attribute chip_pin of throttle : signal is "Y23, Y24, AA22, AA23, AA24, AB23";  -- SW17-SW12

   -- segments: will display the 32-bit word
  attribute chip_pin of seg7: signal is "AD17, AE17, AG17, AH17, AF17, AG18, AA14";
  attribute chip_pin of seg6: signal is "AA17, AB16, AA16, AB17, AB15, AA15, AC17";
  attribute chip_pin of seg5: signal is "AD18, AC18, AB18, AH19, AG19, AF18, AH18";
  attribute chip_pin of seg4: signal is "AB19, AA19, AG21, AH21, AE19, AF19, AE18";
  attribute chip_pin of seg3: signal is "V21, U21, AB20, AA21, AD24, AF23, Y19";
  attribute chip_pin of seg2: signal is "AA25, AA26, Y25, W26, Y26, W27, W28";
  attribute chip_pin of seg1: signal is "M24, Y22, W21, W22, W25, U23, U24";
  attribute chip_pin of seg0: signal is "G18, F22, E17, L26, L25, J22, H22";
  signal seg2display7: std_ulogic_vector(0 to 6);
  signal seg2display6: std_ulogic_vector(0 to 6);
  signal seg2display5: std_ulogic_vector(0 to 6);
  signal seg2display4: std_ulogic_vector(0 to 6);
  signal seg2display3: std_ulogic_vector(0 to 6);
  signal seg2display2: std_ulogic_vector(0 to 6);
  signal seg2display1: std_ulogic_vector(0 to 6);
  signal seg2display0: std_ulogic_vector(0 to 6);

  -- Red leds - will signal the FSM state
   attribute chip_pin of ledr0  : signal is "G19"; -- LEDR[0]
   attribute chip_pin of ledr1  : signal is "F19"; -- LEDR[1]
   attribute chip_pin of ledr2  : signal is "E19"; -- LEDR[2]
   attribute chip_pin of ledr3  : signal is "F21"; -- LEDR[3]
   attribute chip_pin of ledr4  : signal is "F18"; -- LEDR[4]
   attribute chip_pin of ledr5  : signal is "E18"; -- LEDR[5]
   attribute chip_pin of ledr6  : signal is "J19"; -- LEDR[6]
   attribute chip_pin of ledr7  : signal is "H19"; -- LEDR[7]
   attribute chip_pin of ledr8  : signal is "J17"; -- LEDR[8]
   attribute chip_pin of ledr9  : signal is "G17"; -- LEDR[9]
   attribute chip_pin of ledr10  : signal is "J15"; -- LEDR[10]
   attribute chip_pin of ledr11  : signal is "H16"; -- LEDR[11]
   attribute chip_pin of ledr12  : signal is "J16"; -- LEDR[12]
   attribute chip_pin of ledr13  : signal is "H17"; -- LEDR[13]
   attribute chip_pin of ledr14  : signal is "F15"; -- LEDR[14]
   attribute chip_pin of ledr15  : signal is "G15"; -- LEDR[15]
   attribute chip_pin of ledr16  : signal is "G16"; -- LEDR[16]
   attribute chip_pin of ledr17  : signal is "H15"; -- LEDR[17]

   -- Green LEDs - show some variables
   attribute chip_pin of ledg0  : signal is "E21"; -- LEDG[0]
   attribute chip_pin of ledg1  : signal is "E22"; -- LEDG[1]
   attribute chip_pin of ledg2  : signal is "E25"; -- LEDG[2]
   attribute chip_pin of ledg3  : signal is "E24"; -- LEDG[3]
   attribute chip_pin of ledg4  : signal is "H21"; -- LEDG[4]
   attribute chip_pin of ledg5  : signal is "G20"; -- LEDG[5]
   attribute chip_pin of ledg6  : signal is "G22"; -- LEDG[6]
   attribute chip_pin of ledg7  : signal is "G21"; -- LEDG[7]


   signal CurrentState : States := Initial;
   signal mem_addr : std_logic_vector(20 downto 0); 
   signal mem_data_read : vpcregister; 
   signal mem_data_write : vpcregister; 
   signal mem_addr_in: IMem_addr;

   -- control lines
   signal mem_reset : std_logic := '0'; 
   signal mem_rw : std_logic; 
   signal mem_sixteenbit : std_logic := '0'; 
   signal mem_thirtytwobit : std_logic := '1'; 
   signal mem_addressready : std_logic; 
   signal mem_dataready_inv : std_logic; 
   signal clock_divide_limit : std_logic_vector(19 downto 0) := "00000000000000000000";
   signal sysclk1 : std_logic; 
   signal sysclk2 : std_logic; 
   signal serial_character_ready : std_logic; 
   signal ps2_character_ready : std_logic; 
   signal reg_a0, reg_a1, reg_g0 : vpcregister;
   signal en_reg_a1 : std_ulogic;
   signal en_reg_g0 : std_ulogic;

   -- cpu
   signal InstructionExecuted : std_ulogic := '0';
   signal PC: IMem_addr := x"0000"; -- PC start point to zero address
   signal PriorExecPC: IMem_addr;
   signal AfterExecPC: IMem_addr;
   signal IR: vpcword; -- record with Instruction
   signal vpcRegisters: registerFile; -- array of 32 registers
   signal PriorExecvpcRegisters: registerFile; -- array of 32 registers
   signal AfterExecvpcRegisters: registerFile; -- array of 32 registers
   signal cpu_reset : std_ulogic := '0';
   signal exec_enable : std_logic := '0';
   signal memmux_enable : std_logic := '0';
   signal MemMux_out: IMem_addr;

begin

   mem : cscie93.memory_controller port map (
                clk50mhz => clk50mhz,
		mem_addr => mem_addr,
		mem_data_write => mem_data_write,
		mem_rw => mem_rw,
		mem_sixteenbit => mem_sixteenbit,
		mem_thirtytwobit => mem_thirtytwobit,
		mem_addressready => mem_addressready,
		mem_reset => mem_reset,
		ps2_clk => ps2_clk,
		ps2_data => ps2_data,
		clock_hold => clock_hold,
		clock_step => clock_step,
		clock_divide_limit => clock_divide_limit,
		mem_suspend => mem_suspend,
		lcd_en => lcd_en,
		lcd_on => lcd_on,
		lcd_rs => lcd_rs,
		lcd_rw => lcd_rw,
		lcd_db => lcd_db,
		mem_data_read => mem_data_read ,
		mem_dataready_inv => mem_dataready_inv,
		sysclk1 => sysclk1,
		sysclk2 => sysclk2,
		rs232_rxd => rs232_rxd,
		rs232_txd => rs232_txd,
		rs232_cts => rs232_cts,
             sram_dq => sram_dq,
             sram_addr => sram_addr,
             sram_ce_N => sram_ce_N,
             sram_oe_N => sram_oe_N,
             sram_we_N => sram_we_N,
             sram_ub_N => sram_ub_N,
             sram_lb_N => sram_lb_N,
		serial_character_ready => serial_character_ready,
		ps2_character_ready => ps2_character_ready
	 );

   exec : work.vpcExec port map (
                clk50mhz => clk50mhz,
                sysclk1 => sysclk1,
                --PriorExecPC => PriorExecPC,
                PriorExecPC => PC,
                AfterExecPC => AfterExecPC,
		exec_enable => exec_enable,
                mem_addr => mem_addr_in,
                mem_data_read_in => reg_g0,
                ExecInstruction   => IR,
                ExecRegisters => vpcRegisters,
                AfterExecRegisters => AfterExecvpcRegisters,
                InstructionExecuted => InstructionExecuted
        );

  IRreg : work.regstd port map (
	clk => sysclk1,
	en => en_reg_a1,
	clear => mem_reset,
	d => mem_data_read(15 downto 0) & mem_data_read(31 downto 16), -- solving the endianess
	q => reg_a1
  );

  UserDatareg : work.regstd port map (
	clk => sysclk1,
	en => en_reg_g0,
	clear => mem_reset,
	d => mem_data_read(15 downto 0) & mem_data_read(31 downto 16), -- solving the endianess
	q => reg_g0
  );

  MemMux : work.vpcMemMux port map (
	clk => sysclk1,
	PC_addr => PC,
	register_addr => vpcRegisters(IR.instr.vR2)(15 downto 0),
	ExecInstruction => IR,  
	memmux_enable => memmux_enable,
	MemMux_out => MemMux_out
  );


  -- Combinational logic

   clock_divide_limit <= (throttle(0 to 5) & "00011111111110");
   mem_sixteenbit <= '0'; -- no byte addressable
   mem_thirtytwobit <= '1'; -- only double word
   mem_reset <= not key_mem_reset; -- "0" when pressed
   cpu_reset <= not key_cpu_reset; -- "0" when pressed


  with CurrentState select exec_enable <=
	'1' when ExecuteInstruction2,
	'0' when others;

  with CurrentState select memmux_enable <=
	'1' when ParseInstruction|FetchAwaitMemoryUserData|FetchReadingUserData|WriteBackAwaitMemory|WriteBackWriting|Written|FetchedUserData,
	'0' when others;

  with CurrentState select
          PC <= x"0000" when Initial,
                AfterExecPC when others; 

  with CurrentState select
          vpcRegisters <= zero_vpcregister(vpcRegisters) when Initial,
                        AfterExecvpcRegisters when others; 

  with CurrentState select
	mem_addressready <= '1' when FetchReadingInstruction | ParseInstruction | WriteBackWriting | FetchReadingUserData | FetchedUserData,
				'0' when others;

  -- this memory address is for memory operations (20 bits)
  with CurrentState select mem_addr <=
			('0'&x"0"&MemMux_out) when WriteBackAwaitMemory|WriteBackWriting|Written|FetchAwaitMemoryUserData|FetchReadingUserData,
			('0'&x"0"&PC) when others;

  -- this memory address is just to update registers after a user memory operation is done
  -- this variable is *not* used to fetch memory 
  mem_addr_in <= IR.instr.immediate;

  with CurrentState select
  	mem_rw <= 	'1' when WriteBackAwaitMemory|WriteBackWriting,
			'0' when others;

  with CurrentState select en_reg_a1 <=
	'1' when ParseInstruction,
	'0' when others;

  with CurrentState select en_reg_g0 <=
	'1' when FetchedUserData,
	'0' when others;

  -- data to write in memory. both VPC instructions - WSR and SW - get the data from RS register (vR1)
  -- the endianess of a double word : the bits 15-0 from the registers must go first
  mem_data_write <= vpcRegisters(IR.instr.vR1)(15 downto 0) & vpcRegisters(IR.instr.vR1)(31 downto 16);

  IR.raw_instruction <= reg_a1;
  IR.instr.opcode <= get_opcode(IR.raw_instruction(31 downto 26));
  IR.instr.alu_funct <= get_alufunct(IR.raw_instruction(5 downto 0)) when IR.instr.opcode = ALU;

  with IR.instr.opcode select IR.instr.vR1 <=
		 return_int(IR.raw_instruction(15 downto 11)) when ALU,
		 return_int(IR.raw_instruction(15 downto 11)) when JEQ,
		 return_int(IR.raw_instruction(15 downto 11)) when JNEQ,
                 return_int(IR.raw_instruction(20 downto 16)) when others;

  IR.instr.vR2 <= return_int(IR.raw_instruction(25 downto 21));
  IR.instr.vR3 <= return_int(IR.raw_instruction(20 downto 16));
  IR.instr.immediate <= IR.raw_instruction(15 downto 0); -- there is a special case for shifter, will treat at the entity vpcExec

  FSM:
    process is
	variable NewState: States;
    begin
	if mem_reset'event and mem_reset = '1' then
		CurrentState <= Initial;
	end if;
	wait until rising_edge(sysclk1);
		case CurrentState is
			when Initial =>	
				NewState := FetchAwaitMemoryInstruction;
			when FetchAwaitMemoryInstruction =>
				if mem_dataready_inv = '1' then
					NewState := FetchReadingInstruction;
				else
					NewState := FetchAwaitMemoryInstruction; -- stay in the same state
				end if;
			when FetchReadingInstruction =>
				if mem_dataready_inv = '0' then
					NewState := ParseInstruction;
				else
					NewState := FetchReadingInstruction; -- stay in the same state
				end if;
			when ParseInstruction =>
                                -- parse word, get the opcode, registers, etc
                                -- read/write data if it is a memory instruction
                                -- Copy RegisterFile for Exec state

				if IR.instr.opcode = WSR or IR.instr.opcode = SW then
					NewState := WriteBackAwaitMemory;
				elsif IR.instr.opcode = LR or IR.instr.opcode = LW then
					NewState := FetchAwaitMemoryUserData;
				else
					NewState := ExecuteInstruction;
				end if;

			when ExecuteInstruction =>
				NewState := ExecuteInstruction2;

			when ExecuteInstruction2 =>
				NewState := FetchAwaitMemoryInstruction;

			when WriteBackAwaitMemory =>
				if mem_dataready_inv = '1' then
					NewState := WriteBackWriting;
				else	
					NewState := WriteBackAwaitMemory; -- stay in the same state
				end if;
			when WriteBackWriting =>
				if mem_dataready_inv = '0' then
					NewState := Written;
				else	
					NewState := WriteBackWriting; -- stay in the same state
				end if;
			when Written =>
				if mem_dataready_inv = '1' then
					NewState := ExecuteInstruction;
				else
					NewState := Written;
				end if;
			when FetchAwaitMemoryUserData =>
				if mem_dataready_inv = '1' then
					NewState := FetchReadingUserData;
				else
					NewState := FetchAwaitMemoryUserData; -- stay in the same state
				end if;
			when FetchReadingUserData =>
				if mem_dataready_inv = '0' then
					NewState := FetchedUserData;
				else
					NewState := FetchReadingUserData; -- stay in the same state
				end if;
			when FetchedUserData =>
				NewState := ExecuteInstruction;
		end case;
		CurrentState <= NewState;
    end process; 

  -- Diag stuff

  with drivesegment select
        reg_a0 <=       x"0000" & PC            	when "00000100",
                        IR.raw_instruction       	when "00000010",
                        x"00"&'0'&'0'&'0'& mem_addr     when "00000001",
	
			-- temp displays
                        x"0000"& AfterExecPC      	when "00000011",
                        x"0000"&IR.instr.immediate	when "00000101",
                        reg_a1       			when "00000110",
                        mem_data_read      		when "00000111",
                        vpcRegisters(z0)         when "00000000",
                        vpcRegisters(a0)         when "00001000",
                        vpcRegisters(a1)         when "00010000",
                        vpcRegisters(g0)         when "00011000",
                        vpcRegisters(g1)         when "00100000",
                        vpcRegisters(g2)         when "00101000",
                        vpcRegisters(g3)         when "00110000",
                        vpcRegisters(g4)         when "00111000",
                        vpcRegisters(g5)         when "01000000",
                        vpcRegisters(g6)         when "01001000",
                        vpcRegisters(g7)         when "01010000",
                        vpcRegisters(sp)         when "01011000",
                        vpcRegisters(fp)         when "01100000",
                        vpcRegisters(r0)         when "01101000",
                        vpcRegisters(r1)         when "01110000",
                        vpcRegisters(r2)         when "01111000",
                        vpcRegisters(r3)         when "10000000",
                        vpcRegisters(r4)         when "10001000",
                        vpcRegisters(r5)         when "10010000",
                        vpcRegisters(r6)         when "10011000",
                        vpcRegisters(r7)         when "10100000",
                        vpcRegisters(t0)         when "10101000",
                        vpcRegisters(t1)         when "10110000",
                        vpcRegisters(t2)         when "10111000",
                        vpcRegisters(t3)         when "11000000",
                        vpcRegisters(s0)         when "11001000",
                        vpcRegisters(s1)         when "11010000",
                        vpcRegisters(s2)         when "11011000",
                        vpcRegisters(s3)         when "11100000",
                        vpcRegisters(s4)         when "11101000",
                        vpcRegisters(gp)         when "11110000",
                        vpcRegisters(ra)         when "11111000",
                        x"00000000"             when others;

	
  seg7 <= get_hex(reg_a0(31 downto 28));
  seg6 <= get_hex(reg_a0(27 downto 24));
  seg5 <= get_hex(reg_a0(23 downto 20));
  seg4 <= get_hex(reg_a0(19 downto 16));
  seg3 <= get_hex(reg_a0(15 downto 12));
  seg2 <= get_hex(reg_a0(11 downto 8));
  seg1 <= get_hex(reg_a0(7 downto 4));
  seg0 <= get_hex(reg_a0(3 downto 0));

  ledr0 <= '1' when CurrentState=Initial else '0';
  ledr1 <= '1' when CurrentState=FetchAwaitMemoryInstruction else '0';
  ledr2 <= '1' when CurrentState=FetchReadingInstruction else '0';
  ledr3 <= '1' when CurrentState=ParseInstruction else '0';
  ledr4 <= '1' when CurrentState=ExecuteInstruction else '0';
  ledr5 <= '1' when CurrentState=ExecuteInstruction2 else '0';
  ledr7 <= '1' when CurrentState=WriteBackAwaitMemory else '0';
  ledr8 <= '1' when CurrentState=WriteBackWriting else '0';
  ledr9 <= '1' when CurrentState=Written else '0';
  ledr10 <= '1' when CurrentState=FetchAwaitMemoryUserData else '0';
  ledr11 <= '1' when CurrentState=FetchReadingUserData else '0';
  ledr12 <= '1' when CurrentState=FetchedUserData else '0';

  ledg0 <= mem_dataready_inv;
  ledg1 <= mem_addressready;
  ledg2 <= mem_rw;
  ledg3 <= InstructionExecuted;
  ledg4 <= memmux_enable;
  
end;
