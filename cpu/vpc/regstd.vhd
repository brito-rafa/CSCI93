library ieee;
use ieee.std_logic_1164.all;

entity regstd is
  port (clk, en, clear: in std_logic;
        d: in std_logic_vector(31 downto 0);
        q: out std_logic_vector(31 downto 0));
end entity regstd;

architecture behav of regstd is
begin
  regstd_behavior: process is
  begin
    if clear = '1' then
	q <= "00000000000000000000000000000000";
    end if;
    wait until falling_edge(clk);
    if en = '1' then
      q <= d;
    end if;
  end process regstd_behavior;
end architecture behav;
