-- QUERIES!!!

-- find all user ids for users who share the same postal code as target bar owner
SELECT r.uid
FROM users,
WHERE ((
      SELECT RIGHT(RTRIM(r.rAddress), 7)
      FROM regCustomers r
    ) IN (
      SELECT RIGHT(RTRIM(b.bAddress), 7)
      FROM barOwners b
      WHERE b.uid = <target>
    )
);

-- Find all bar owners in Montreal who have made a subscription
SELECT *
FROM barOwners INNER JOIN subscription ON barOwners.uid = subscription.uid
WHERE @shipAddress LIKE '%Montreal%';

-- Find the cheapest drunk
SELECT aid, (price/volume) AS priceByVol
FROM alcohols
ORDER BY priceByVol DESC;

-- Using that sweet view, find most popular product of each alcohol type
SELECT aid, name, alcType, max(amountSold)
FROM alcohols
INNER JOIN quantSold
GROUP BY alcType;

-- Find the most expensive thing in the store
SELECT aid, name, MAX(price)
FROM alcohols


-- MODIFICATIONS!!!

-- creates a sale on items of a type i guess
UPDATE alcohols
SET price = price*0.9
WHERE aType = 'gin'

-- increase the prices of the top 3 selling beers by %10
UPDATE alcohols
SET price = price*1.1
WHERE aid IN (
    SELECT a.aid, a.price, SUM(t.quantity)
    FROM transaction t
    INNER JOIN (
        SELECT * AS alc
        FROM alcohols a
        WHERE aType = 'beer'
    )
    ON t.aid = alc.aid
    GROUP BY aid
    ORDER BY SUM(t.quantity) DESC LIMIT 3
)
-- Lower the price an all poorly-selling subsciption gins by 10% --
UPDATE alcohol
SET price = price * 0.9
WHERE aid IN (
  SELECT *
  FROM alcohol a INNER JOIN subscription s ON a.aid = s.aid AND a.type = 'gin'
  GROUP BY aid
  ORDER BY SUM(s.quantity) ASC LIMIT 5
) AND type = 'gin';



-- user with target ID removes all items with type gin from his subscription
DELETE FROM subscription s
WHERE uid = <target> AND aid IN (
        SELECT aid
        FROM alcohols
        WHERE aType = 'gin'
    );


-- VIEWS!!!

-- view how much of each alcohol has been sold
CREATE VIEW quantSold AS
SELECT a.aid, SUM(t.quantity) AS amountSold
FROM transaction t
INNER JOIN alcohols a
ON t.aid = a.aid
GROUP BY a.aid
ORDER BY amountSold DESC;

-- view the users spending the most money
CREATE VIEW topSpenders AS
SELECT uid, SUM(totalCost) AS totalSpent
FROM transaction
GROUP BY uid
ORDER BY totalSpent DESC;
