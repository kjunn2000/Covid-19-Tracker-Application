convert_cases_to_list(List) :- findall((Location,Cases),totalCases(Location,Cases), List).

printlist([]).
printlist([Head|Tail]) :- write(Head), nl, printlist(Tail).

halve([], [], []).
halve([A], [A], []).
halve([A,B|Cs], [A|X], [B|Y]) :- halve(Cs, X, Y).

asc_merge([], Ys, Ys).
asc_merge(Xs, [], Xs).
asc_merge([(X1,X2)|Xs], [(Y1,Y2)|Ys], M) :-
   (
      X2 < Y2 -> M = [(X1,X2)|Ms], asc_merge(Xs, [(Y1,Y2)|Ys], Ms)
    ; M = [(Y1,Y2)|Ms], asc_merge([(X1,X2)|Xs], Ys, Ms)
   ).

asc_mergeSort([], []).
asc_mergeSort([E], [E]).
asc_mergeSort([E1,E2|Es], SL) :-
     halve([E1,E2|Es], L1, L2),
     asc_mergeSort(L1, SL1),
     asc_mergeSort(L2, SL2),
     asc_merge(SL1, SL2, SL).
asc_mergeSort :- convert_cases_to_list(List), asc_mergeSort(List, SL), printlist(SL).


desc_merge([], Ys, Ys).
desc_merge(Xs, [], Xs).
desc_merge([(X1,X2)|Xs], [(Y1,Y2)|Ys], M) :-
   (
      X2 > Y2 -> M = [(X1,X2)|Ms], desc_merge(Xs, [(Y1,Y2)|Ys], Ms)
    ; M = [(Y1,Y2)|Ms], desc_merge([(X1,X2)|Xs], Ys, Ms)
   ).

desc_mergeSort([], []).
desc_mergeSort([E], [E]).
desc_mergeSort([E1,E2|Es], SL) :-
     halve([E1,E2|Es], L1, L2),
     desc_mergeSort(L1, SL1),
     desc_mergeSort(L2, SL2),
     desc_merge(SL1, SL2, SL).
desc_mergeSort :- convert_cases_to_list(List), desc_mergeSort(List, SL), printlist(SL).
