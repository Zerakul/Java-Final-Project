SELECT firstName,numOfCredits,rank

FROM (SELECT s1.firstName,s1.numOfCredits, COUNT(s1.numOfCredits) rank

FROM student s1,student s2

WHERE (s1.numOfCredits < s2.numOfCredits

OR (s1.numOfCredits=s2.numOfCredits 

AND s1.firstName <= s2.firstName))

AND s1.dept = 'Computer Science'

AND s2.dept = 'Computer Science'

group by s1.firstName, s1.numOfCredits

order by s1.numOfCredits desc) s3

WHERE Rank = (SELECT (COUNT(*)+1) DIV 2 FROM student WHERE dept = 'Computer Science') 

OR

CASE WHEN (SELECT COUNT(*) FROM student WHERE dept = 'Computer Science') % 2 = 0

THEN Rank = (SELECT ((COUNT(*)+1) DIV 2)+1 FROM student WHERE dept = 'Computer Science')

END