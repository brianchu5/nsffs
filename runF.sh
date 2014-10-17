#!/bin/bash

simtime = $1
realizations = $5
interval = $6
rorF = $2
foxF = $3
TFG = $4
id = $7
outdir = $8

mkdir ~/${8}

~/StochKit2.0.11/ssa -m foxror_stochkitF.xml -t ${1} -r ${5} -i ${6} --keep-trajectories -f --no-stats --out-dir ~/${8} 


mv ~/${8}/trajectories/trajectory0.txt ${7}


awk '{print $2}' ${7}|tail -1> ${2}

awk '{print $3}' ${7}|tail -1> ${3}

awk '{print $4}' ${7}|tail -1> ${4}
