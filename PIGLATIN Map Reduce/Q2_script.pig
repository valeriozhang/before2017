-- Which candidates have won with over 60% of the vote?

--load the data from HDFS and define the schema
raw = LOAD '/data2/cl03.csv' USING PigStorage(',') AS  (date, type:chararray, parl:int, prov:chararray, riding:chararray, lastname:chararray, firstname:chararray, gender:chararray, occupation:chararray, party:chararray, votes:int, percent:double, elected:int);

-- eliminate all candidates with less than 100 votes
gte100 = FILTER raw by votes >= 100;

-- split relation into winners and losers
SPLIT gte100 INTO genW IF elected == 1, genL IF elected == 0;

--join on specified fields
yearJoin = JOIN genW by (date, type, parl, prov, riding), genL by (date, type, parl, prov, riding) PARALLEL 4;

--create results projection
results = FOREACH yearJoin GENERATE $5,$18, ($10-$23);

--filter results
filteredResults = FILTER results by $2 < 10;

dump filteredResults;

STORE filteredResults INTO 'q2' using PigStorage(',');