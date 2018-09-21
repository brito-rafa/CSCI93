library ieee;
use ieee.std_logic_1164.all;

entity xnor02 is
  port (
    a, b : in  std_logic;
    q    : out std_logic);
end entity xnor02;

architecture dataflow of xnor02 is
begin
  q <= a xnor b;
end architecture dataflow;
