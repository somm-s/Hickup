#!/bin/bash


# year=2021

# mv lin_hb100000_bi_30-buck_0-1500/ yr-${year}_fi-100_ft-15000000_nb-30_ht-100000_di-bi_mx-1500_lg-no
# mv lin_no-hb_uni_30-buck_0-1500/ yr-${year}_fi-100_ft-15000000_nb-30_ht-0_di-uni_mx-1500_lg-no
# mv lin_hb100000_uni_20-buck_0-1500/ yr-${year}_fi-100_ft-15000000_nb-20_ht-100000_di-uni_mx-1500_lg-no
# mv log_hb100000_uni_30-buck_0-11/ yr-${year}_fi-100_ft-15000000_nb-30_ht-100000_di-uni_mx-11_lg-yes
# mv lin_hb100000_uni_30-buck_0-1500/ yr-${year}_fi-100_ft-15000000_nb-30_ht-100000_di-uni_mx-1500_lg-no

# for dir in */; do
#     zip -r "${dir%/}.zip" "$dir"
# done

for file in *.zip; do
    scp "$file" sosi@192.168.6.240:~/repos/Bert/datasets
done