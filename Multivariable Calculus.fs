module hw6

(* Assignment 6 *) (* Do not edit this line. *)
(* Student name: Valerio Zhang, Id Number: 260568861 *) (* Edit this line. *)

(* Some basic functions that I like to use.  You can use the raw F# versions
if you want. *)

(* Sticks an item onto a lazy stream *)
let cons x sigma = Seq.append (Seq.singleton x) sigma

(* Grabs the first item from a stream. *)
let first sigma = Seq.nth 0 sigma

(* Removes the first item and gives you the rest of the stream. *)
let rest sigma = Seq.skip 1 sigma

(* Makes a list out of the first n elements of a stream.  Useful for display.  There is
a built-in primitive to do this but I like this better. *)

let rec prefix (n: int) sigma = 
  if (n = 0) then []
  else (first sigma) :: (prefix (n - 1) (rest sigma))

(* Question 1 *)

let rec expand num den radix =
    Seq.delay (fun x -> cons ((num * radix) / den) (expand (num * radix % den) den radix))

  (* Just three lines of code!  Do not forget to delay the execution.*)

(* Question 2 *)

type term = Term of float * int

(* pown is what you use to raise a float to an integer power. *)
let evalTerm (t: term) (x:float):float = 
  match t with
  | Term (c,e) -> c * (pown x e)

(* This is very similar in pattern to evalTerm *)
let integrateTerm (t: term):term =
  match t with
  |Term(c,e) -> Term(c / (float) (e + 1), (e + 1))
(* Just 2 lines of code! *)

let rec integrateSeries sigma =
  Seq.delay (fun x -> cons (integrateTerm (first sigma)) (integrateSeries (rest sigma)))
(* Just 2 lines of code! *)

(* Examples for testing. *)
let sigma1 = Seq.initInfinite (fun i -> Term(1.0,i))

let sigma2 = integrateSeries sigma1

let rec expSeries =
  Seq.delay (fun x -> cons (Term (1.0,0)) (integrateSeries (expSeries)))
    (* Just one line!  Do NOT forget to delay the code. *)

let rec sumSeries (sigma: seq<term>) (x: float) (n: int) : float =
    if (n=0) then 0.0 else sumSeries (rest sigma) (x) (n-1) + evalTerm (first sigma) (x) 
(* Just 2 lines of code! *)


(*  Here is what my session looked like.  I have removed junk and mistakes so you can
fantasize that I write code without ever making mistakes.

F# Interactive for F# 3.1 (Open Source Edition)
Freely distributed under the Apache 2.0 Open Source License

For help type #help;;

val cons : x:'a -> sigma:seq<'a> -> seq<'a>
val first : sigma:seq<'a> -> 'a
val rest : sigma:seq<'a> -> seq<'a>
val prefix : n:int -> sigma:seq<'a> -> 'a list
> 
val expand : num:int -> den:int -> radix:int -> seq<int>
> let oneSeventh = expand 1 7 10;;
val oneSeventh : seq<int>
> prefix 10 oneSeventh;;
val it : int list = [1; 4; 2; 8; 5; 7; 1; 4; 2; 8]
> let threeSeventh = expand 3 7 10;;
val threeSeventh : seq<int>
> prefix 10 threeSeventh;;
val it : int list = [4; 2; 8; 5; 7; 1; 4; 2; 8; 5]
> let oneSeventeenth = expand 1 17 10;;

val oneSeventeenth : seq<int>

> prefix 20 oneSeventeenth;;
val it : int list =
  [0; 5; 8; 8; 2; 3; 5; 2; 9; 4; 1; 1; 7; 6; 4; 7; 0; 5; 8; 8]
> prefix 10 (expand 1 31 10);;
val it : int list = [0; 3; 2; 2; 5; 8; 0; 6; 4; 5]
> 
type term = | Term of float * int
> 
val evalTerm : t:term -> x:float -> float
> let t1 = Term (2.0,5);;
val t1 : term = Term (2.0,5)
> evalTerm t1 2.7;;
val it : float = 286.97814
> evalTerm t1 1.1;;
val it : float = 3.22102
> float (1);;
val it : float = 1.0
> 
val integrateTerm : t:term -> term
> t1;;
val it : term = Term (2.0,5)
> integrateTerm t1;;
val it : term = Term (0.3333333333,6)
> integrateTerm it;;
val it : term = Term (0.04761904762,7)
> 
val integrateSeries : sigma:seq<term> -> seq<term>
> 
val sigma1 : seq<term>
> prefix 5 sigma1;;
val it : term list =
  [Term (1.0,0); Term (1.0,1); Term (1.0,2); Term (1.0,3); Term (1.0,4)]
> 
val sigma2 : seq<term>
> prefix 5 sigma2;;
val it : term list =
  [Term (1.0,1); Term (0.5,2); Term (0.3333333333,3); Term (0.25,4);
   Term (0.2,5)]
> 
val sumSeries : sigma:seq<term> -> x:float -> n:int -> float
> sumSeries sigma1 1.0 5;;
val it : float = 5.0
> sumSeries sigma2 1.0 7;;
val it : float = 2.592857143
> 
This and other recursive references to the object(s) being defined will be checked for
initialization-soundness at runtime through the use of a delayed reference. This is
because you are defining one or more recursive objects, rather than recursive
functions. This warning may be suppressed by using '#nowarn "40"' or '--nowarn:40'.

val expSeries : seq<term>

> prefix 5 expSeries;;
val it : term list =
  [Term (1.0,0); Term (1.0,1); Term (0.5,2); Term (0.1666666667,3);
   Term (0.04166666667,4)]
> sumSeries expSeries 1.0 10;;
val it : float = 2.718281526
> #quit;;

- Exit...

Process inferior-fsharp finished

*)


    


  


             



