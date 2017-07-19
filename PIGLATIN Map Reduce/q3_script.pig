--load the data from HDFS and define the schema
raw = LOAD '/data2/cl03.csv' USING PigStorage(',') AS  (date, type:chararray, parl:int, prov:chararray, riding:chararray, lastname:chararray, firstname:chararray, gender:chararray, occupation:chararray, party:chararray, votes:int, percent:double, elected:int);

--get the set of all MPs elected in the general election--
filtered = FILTER raw BY type == 'Gen' AND  elected == 1;

--group all MPs by Parliament--
groupByParl = GROUP filtered BY parl;

dump groupByParl;

--get the number of MPs in each Parliament into 2 identical tables to generate difference between tuples-
first = FOREACH groupByParl GENERATE ($0) AS p1, COUNT ($1) AS c1;
second = FOREACH groupByParl GENERATE ($0) AS p2, COUNT ($1) AS c2;

dump first;
dump second;

--join by some year a in fst and a-1 in second so that we get tuples of successive years we can take a difference from--
joinedRel = JOIN first BY p1, second BY p2 - 1;
dump joinedRel;

--get the difference in membership between successive parliaments using old aliasess-
diffRel = FOREACH joinedRel GENERATE ($0) AS Parl, c2 - c1 AS GrownBy;

--order by ascending parliament--
ansRel = ORDER diffRel BY Parl;
dump ansRel;
