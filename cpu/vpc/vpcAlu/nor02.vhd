library ieee;
use ieee.std_logic_1164.all;

entity nor02 is
  port (
    a, b : in  std_logic;
    q    : out std_logic);
end entity nor02;

architecture dataflow of nor02 is
begin
  q <= a nor b;
end architecture dataflow;
