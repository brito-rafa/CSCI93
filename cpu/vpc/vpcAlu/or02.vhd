library ieee;
use ieee.std_logic_1164.all;

entity or02 is
  port (
    a, b : in  std_logic;
    q    : out std_logic);
end entity or02;

architecture dataflow of or02 is
begin
  q <= a or b;
end architecture dataflow;
