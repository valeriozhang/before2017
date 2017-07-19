module hw4

(* Assignment 4 *) (* Do not edit this line. *)
(* Student name: Valerio zhang, Id Number: 260568861 *) (* Edit this line. *)


type typExp =
  | TypInt
  | TypVar of char
  | Arrow of typExp * typExp
  | Lst of typExp

type substitution = (char * typExp) list

(* check if a variable occurs in a term *)
let rec occurCheck (v: char) (tau: typExp) : bool = 
    match tau with
    |TypInt -> false
    |Lst x -> occurCheck v x 
    |TypVar y -> if v = y then true else false
    |Arrow(i, j) -> occurCheck v i || occurCheck v j  
    
(* substitute typExp tau1 for all occurrences of type variable v in typExp tau2 *)
let rec substitute (tau1 : typExp) (v : char) (tau2 : typExp) : typExp =
    match tau2 with
    |TypInt -> tau2
    |TypVar y -> if v = y then tau1 else TypVar y
    |Lst x -> Lst (substitute tau1 v x)
    |Arrow (f, u) -> Arrow(substitute tau1 v f, substitute tau1 v u)

let applySubst (sigma: substitution) (tau: typExp) : typExp =
    List.fold (fun acc (v, t) -> substitute t v acc) tau sigma
    
(* This is a one-line program *)



let rec unify (tau1: typExp) (tau2:typExp) : substitution =
    match (tau1, tau2) with
    |(TypVar x, TypVar y) -> if x = y then [] else [(x, TypVar y)]
    |(TypVar x, TypInt) -> unify tau2 tau1
    |(TypVar x, Arrow(s1,t1)) -> if occurCheck x s1 || occurCheck x t1 then failwith "Failed occurs check" else [(x, Arrow(s1,t1))]
    |(TypVar x, Lst y) -> if occurCheck x y then failwith "Failed occurs check" else [x, Lst y]
    |(TypInt, TypInt ) -> []
    |(TypInt, Arrow(s1, t1)) -> unify tau2 s1 @ unify (applySubst (unify tau2 s1) tau2) (applySubst (unify tau2 s1) t1)
    |(TypInt, TypVar x) -> [(x, TypInt)] 
    |(TypInt, Lst y) -> failwith "Not unifiable" 
    |(Arrow(s1,t1), Arrow(s2,t2)) -> unify s1 s2 @ unify (applySubst (unify s1 s2) (t1)) ( applySubst (unify s1 s2) t2)
    |(Arrow(s1, t1), TypInt ) -> unify tau2 tau1
    |(Arrow(s1,t1), TypVar x) -> unify tau2 tau1
    |(Arrow(s1,t1), Lst y) -> failwith "Clash in principal type constructor"
    |(Lst y, Arrow(s1,t1)) -> failwith "Clash in principal type constructor"
    |(Lst x, TypInt) -> unify tau2 tau1
    |(Lst x, TypVar y) -> unify tau2 tau1         
    |(Lst x, Lst y) -> unify x y

    
(* Use the following signals if unification is not possible:

 failwith "Clash in principal type constructor"
 failwith "Failed occurs check"
 failwith "Not unifiable"

*)


(*

> let te4 = Arrow(TypInt, Arrow(TypVar 'c', TypVar 'a'));;

val te4 : typExp = Prod (TypInt,Arrow (TypVar 'c',TypVar 'a'))

> let te3 = Arrow (TypVar 'a',Arrow (TypVar 'b',TypVar 'c'));;

val te3 : typExp = Prod (TypVar 'a',Arrow (TypVar 'b',TypVar 'c'))

> unify te3 te4;;
val it : substitution = [('c', TypInt); ('b', TypVar 'c'); ('a', TypInt)]
> let result = it;;

val result : substitution = [('c', TypInt); ('b', TypVar 'c'); ('a', TypInt)]

> applySubst result te3;;
val it : typExp = Prod (TypInt,Arrow (TypInt,TypInt))
> applySubst result te4;;
val it : typExp = Prod (TypInt,Arrow (TypInt,TypInt))

*)


    

    
(*

> let te4 = Prod(TypInt, Arrow(TypVar 'c', TypVar 'a'));;

val te4 : typExp = Prod (TypInt,Arrow (TypVar 'c',TypVar 'a'))

> let te3 = Prod (TypVar 'a',Arrow (TypVar 'b',TypVar 'c'));;

val te3 : typExp = Prod (TypVar 'a',Arrow (TypVar 'b',TypVar 'c'))

> unify te3 te4;;
val it : substitution = [('c', TypInt); ('b', TypVar 'c'); ('a', TypInt)]
> let result = it;;

val result : substitution = [('c', TypInt); ('b', TypVar 'c'); ('a', TypInt)]

> applySubst result te3;;
val it : typExp = Prod (TypInt,Arrow (TypInt,TypInt))
> applySubst result te4;;
val it : typExp = Prod (TypInt,Arrow (TypInt,TypInt))

*)


  
