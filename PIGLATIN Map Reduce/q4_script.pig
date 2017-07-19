--load the data from HDFS and define the schema--
raw = LOAD '/data2/cl03.csv' USING PigStorage(',') AS  (date, type:chararray, parl:int, prov:chararray, riding:chararray, lastname:chararray, firstname:chararray, gender:chararray, occupation:chararray, party:chararray, votes:int, percent:double, elected:int);

--group all MPs by Parliament--
groupByParl = GROUP raw BY parl;

--now group by party and parliament--
parlAndParty = GROUP raw BY (parl, party);

--get the number of mps in each party per parliament
partyMem = FOREACH parlAndParty GENERATE FLATTEN ($0), COUNT ($1) AS nuMps;
dump partyMem;

--get the number of mps in parliament in total--
totalByParl = FOREACH groupByParl GENERATE ($0) AS pment, COUNT($1) as totalNuMps;
dump totalByParl;

--final output--
temp = JOIN partyMem by $0, totalByParl by $0;
ansRel = FOREACH temp GENERATE ($0), ($1), ($2), ($4);
dump ansRel;

--storage--
STORE ansRel INTO 'q4' USING PigStorage(',');
