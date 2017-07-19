module NewtonsMethod

///////


///////

let deriv (f, dx: float) = fun x -> ((f(x + dx) - f(x))/dx)
(* val deriv : f:(float -> float) * dx:float -> x:float -> float *)


let rec newton(f,guess:float,tol:float,dx:float) = 
(* val newton : f:(float -> float) * guess:float * tol:float * dx:float -> float *)
 if abs (f guess) < tol then guess
 else newton(f, guess - ((f guess) / deriv(f ,dx) guess), tol, dx)
 
(* For testing 
let make_cubic(a:float,b,c) = fun x -> (x*x*x + a * x*x + b*x + c);;
newton(make_cubic(2.0,-3.0,1.0),0.0,0.0001,0.0001);;
*)

///////

type term = float * int
type poly = term list

exception EmptyList

(* Multiply a term by a polynomial. *)
let mtp(t:term,p:poly):poly =
 let (c,e) = t
 List.map (fun (x,y) -> (c*x, e+y)) p
(* val mtp : t:term * p:poly -> poly *)


(* Add a term to a polynomial. *)
let rec atp(t:term,p:poly):poly =
 match p with
 | [] -> [t]
 | x::xs -> if (snd(t)=snd(x)) then (fst(t)+fst(x),snd(x))::xs elif (snd(t)>snd(x)) then t::x::xs else x::atp(t,xs)
(* val atp : t:term * p:poly -> poly *)

(* Add two polynomials.  The result must be properly represented. This means you
cannot have more than one term with the same exponent, you should not have a
term with a zero coefficient, except when the whole polynomial is zero and the
terms should be decreasing order of exponents.   Thus, for example,
5.2 x^7 - 3.8 x^4 +2.0 x - 1729.0 should be represented as
[(5.2,7);(-3.8,4);(2.0,1);(-1729.0,0)] *)

let rec addpolys(p1:poly,p2:poly):poly =
 match p1,p2 with
  |p1,[] -> p1
  |[],p2 -> p2
  |x::xs, p2 -> addpolys(xs,atp(x,p2))
  
(* val addpolys : p1:poly * p2:poly -> poly *)

(* Multiply two polynomials.  All the remarks above apply here too. Raise an
exception if one of the polynomials is the empty list. *)
let rec multpolys(p1:poly,p2:poly) =
 match (p1,p2) with
 |([],p2) -> raise EmptyList
 |(p1,[]) -> raise EmptyList
 |(p1,p2) ->  p1 |> List.collect (fun (x,y) ->
  p2 |> List.map (fun (a,b) -> (x*a,y+b))) |> List.filter(fun (x,y) -> (0.0,y) < (x,y) || (0.0,y) > (x,y))
(* val multpolys : p1:poly * p2:poly -> poly *)

(* This is the tail-recursive version of Russian peasant exponentiation.  I have
done it for you.  You will need it for the next question.  *)
let exp(b:float, e:int) =
  let rec helper(b:float, e:int, a: float) =
    if (b = 0.0) then 0.0
    elif (e = 0) then a
    elif (e % 2 = 1) then helper(b,e-1, b*a)
    else helper(b*b,e/2,a)
  helper(b,e,1.0)

(* Here is how you evaluate a term. *)
let evalterm (v:float) ((c,e):term) = if (e=0) then c else c * exp(v,e)

(* Evaluate a polynomial viewed as a function of the indeterminate.  Use the function
above and List.fold and List.map and a dynamically created function for a one-line
answer.  *)
let evalpoly(p:poly,v:float):float =
 List.fold ( fun acc y -> acc + y) 0.0 (List.map (fun x -> evalterm (v) (x)) p)
(* val evalpoly : p:poly * v:float -> float *)

(* Compute the derivative of a polynomial as a symbolic representation.  Do NOT use
deriv defined above.  I want the answer to be a polynomial represented as a list.
I have done a couple of lines so you can see how to raise an exception.  *)
let rec diff (p:poly):poly = 
  match p with
    | [] -> raise EmptyList
    | _ -> p |> List.map (fun (a,b) -> (a*float b,b-1)) |> List.filter (fun (x,y) -> (0.0,y) < (x,y) || (0.0,y) > (x,y))
 //   | x::xs -> if (snd(x) = 0) then [] else diff((fst(x)*snd(x), (snd(x)-1))::xs)
(*  val diff : p:poly -> poly *)
    
///////

type Country = string;;
type Chart = Set<Country*Country>;;
type Colour = Set<Country>;;
type Colouring = Set<Colour>;;

(* This is how you tell that two countries are neghbours.  It requires a chart.*)
let areNeighbours ct1 ct2 chart =
  Set.contains (ct1,ct2) chart || Set.contains (ct2,ct1) chart;;
(* val areNeighbours :
  ct1:'a -> ct2:'a -> chart:Set<'a * 'a> -> bool when 'a : comparison
  *)

(* The colour col can be extended by the country ct when they are no neighbours
according to chart.*)
  
let canBeExtBy col ct chart =
 Set.forall (fun x -> not (areNeighbours x ct chart)) col;;

(*
   val canBeExtBy :
  col:Set<'a> -> ct:'a -> chart:Set<'a * 'a> -> bool when 'a : comparison
*)

(* Here you have to extend a colouring by a fixed country. *)
let rec extColouring (chart: Chart) (colours : Colouring) (country : Country) = 
 let x = Set.minElement colours
 let y = Set.remove x colours
 if Set.isEmpty colours
 then Set.singleton (Set.singleton country)
   else if canBeExtBy x country chart
   then Set.add (Set.add country x) y
   else Set.add x (extColouring chart y country);;   
(*
val extColouring :
  chart:Chart -> colours:Colouring -> country:Country -> Set<Set<Country>>
*)

(* This collects the names of the countries in the chart.  A good place
to use Set.fold *) 
let countriesInChart (chart : Chart) = 
 Set.fold(fun x (c1,c2) -> Set.add c1 (Set.add c2 x)) Set.empty chart;;
(* val countriesInChart : chart:Chart -> Set<Country> *)

(* Here is the final function.  It is also most conveniently done with Set.fold *)
let colourTheCountries (chart: Chart)  = 
 Set.fold(extColouring chart) Set.empty (countriesInChart chart);;
(* val colourTheCountries : chart:Chart -> Colouring *)

///////

type Exptree =
  | Const of int 
  | Var of string 
  | Add of Exptree * Exptree 
  | Mul of Exptree * Exptree;;

type Bindings = (string * int) list;;

(* The bindings are stored in a list rather than a BST for simplicity.  The
list is sorted by name, which is a string. *)
let rec lookup(name:string, env: Bindings) = 
 match env with
 |[]-> None
 |x::xs -> if fst(x)=name then Some(snd(x)) else lookup(name, xs);;
(* val lookup : name:string * env:Bindings -> int option *)

(* Insert a new binding.  If the name is already there then the new binding should
be put in front of it so that the lookup finds the latest binding.  *)
let rec insert(name:string, value: int, b: Bindings) = 
 match b with
 |[] -> [name, value]
 |x::xs -> if name > fst(x) then x::insert(name,value,xs) else (name, value)::x::xs;;
(* val insert : name:string * value:int * b:Bindings -> (string * int) list*)

(* The recursive evaluator.  You have to match on the exp.  If a variable is not
found return None.  If you are applying an operator to None and something else the
answer is None.  This leads to a program of about 20 lines but it is conceptually
very easy.  *)

let rec eval(exp : Exptree, env:Bindings) =
 match exp with
 |Const x -> Some x
 |Var y -> lookup(y,env)
 |Add(exp1,exp2) ->
  let (val1,val2) = (eval(exp1,env), eval(exp2,env))
  match (val1,val2) with
   |(Some x1, Some x2) -> Some (x1 + x2)
   |(_, None) -> None
   |(None, _) -> None
 |Mul(expression1,expression2) ->
  let (val1,val2) = (eval(expression1,env), eval(expression2, env))
  match (val1,val2) with
  |(Some x1, Some x2) -> Some (x1 * x2)
  |(_, None) -> None
  |(None, _) -> None
  
(* val eval : exp:Exptree * env:Bindings -> int option  *)
