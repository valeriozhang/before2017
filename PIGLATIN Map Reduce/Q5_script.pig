  raw = LOAD '/data2/emp.csv' USING PigStorage(',') AS (empid:int, fname:chararray, lname:chararray, deptname:chararray, isManager:chararray, mgrid:int, salary:int);
  SPLIT raw INTO M IF isManager == 'Y', E IF isManager == 'N';
  X = FOREACH M GENERATE empid, lname;
  Y = FOREACH E GENERATE mgrid;
  Z = JOIN X by empid, Y by mgrid;
  V = Group Z by mgrid;
  W = FOREACH V GENERATE group, flatten($1.lname), COUNT(Z);

  STORE filter2 INTO 'q5' using PigStorage(',');

  dump W;
