#!/usr/bin/env bash
teachers_pet create_student_teams --organization=EPERS-UNQ --students=students.txt
teachers_pet create_repos --organization=EPERS-UNQ --repository=TP --public=false --students=students.txt
teachers_pet push_files --organization=EPERS-UNQ --repository=TP --students=students.txt

#branch=enunciadoTP5
#
#git push VilmaPalmaEVampiros $branch


