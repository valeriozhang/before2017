///////

module SIMPLEFSHARP

let list = [1.0;2.0;3.2;4.3]
let list2 = [1.5;2.3;1.2]

(* val sumlist : l:float list -> float *)
let rec sumlist (l : float list) = 
 match l with
 |[] -> 0.0
 |x::xs -> x + sumlist xs

(* val squarelist : l:float list -> float list *)
let rec squarelist l =
 match l with
 |[] -> []
 |x::xs -> x * x :: squarelist xs

(* val mean : l:float list -> float *)
let mean l = 
 let rec counter l =
  match l with
  |[] -> 0.0
  |x::xs -> 1.0 + counter xs (* remove failwith and replace with your code *)
 in sumlist l / counter l

(* val mean_diffs : l:float list -> float list *)
let mean_diffs l =
 let rec helper (l,average) =
  match l with
  |[] -> []
  |x::xs -> (x - average) :: helper (xs,average)
 in helper (l,mean l)
 
(* val variance : l:float list -> float *)
let variance l = 
 mean(squarelist (mean_diffs l) )
         (* remove failwith and replace with your code *)

///////

///////

(* val memberof : 'a * 'a list -> bool when 'a : equality *)
let rec memberof (i, l) = 
 match (i, l) with
 |(i, []) -> false
 |(i, x::xs) -> if (i = x) then true else memberof (i, xs)

(* val remove : 'a * 'a list -> 'a list when 'a : equality *)
let rec remove (i, l)  = 
 match (i, l) with
 |(i, []) -> [] 
 |(i, x::xs) -> if (i = x) then remove (i, xs) else x::remove (i,xs)
       
 ///////

 ///////

(* val isolate : l:'a list -> 'a list when 'a : equality *)
let rec isolate l =
  match l with
  |[] -> []
  |x::xs -> if memberof(x, xs) then isolate(xs) else x::isolate(xs)

(* End of question 3 *) (* Do not edit this line *)

(* Question 4 *) (* Do not edit this line *)

(* val common : 'a list * 'a list -> 'a list when 'a : equality *)
let rec common (l1, l2) =
 match l1 with
 |[] -> []
 |x::xs -> if memberof(x, l2) then x::common(xs, l2) else common(xs, l2) 


 ///////

 ///////

(* val split : l:'a list -> 'a list * 'a list *)
let split l =
 let rec go l (xs, ys) =
  match l with
  |x::y::everythingelse -> go everythingelse (x::xs, y::ys)
  |[x] -> (x::xs, ys)
  |_ -> (xs, ys)
 go l ([], []) 

(* val merge : 'a list * 'a list -> 'a list when 'a : comparison *)
let rec merge (l1, l2) =
 match (l1,l2) with
 | (x, []) -> x
 | ([], y) -> y
 | (x::xs, y::ys) -> if x <= y then x::merge(xs, y::ys) else y::merge(x::xs, ys)

(* val mergesort : l:'a list -> 'a list when 'a : comparison *)
let rec mergesort l =
 match l with
 |[] -> []
 |[x] -> [x]
 |_ -> let (l1,l2) = split l in merge (mergesort l1, mergesort l2)

(* End of question 5 *) (* Do not edit this line *)
