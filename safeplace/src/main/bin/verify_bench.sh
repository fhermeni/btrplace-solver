#!/bin/sh

VERIFIERS="impl impl_repair checker"

##Core constraints are evaluated with a continuous restriction model and a 1x1.
##No need of more by design
CSTRS="noVMsOnOfflineNodes hostForVMs"
for CSTR in ${CSTRS}; do
    echo "--- core constraint ${CSTR} ---"
    for VERIFIER in ${VERIFIERS}; do
        echo "\tVerifying ${VERIFIER}"
        ./verify.sh --size 1x1 -v --restriction continuous --nbDurations all --nbDelays all --verifier ${VERIFIER} $* ${CSTR}
    done
done


##Pluggable constraints, discrete restriction only, size=2x2
CSTRS="offline online running ready sleeping killed among ban fence runningCapacity resourceCapacity gather lonely maxOnline overbook preserve spread split splitAmong"
VERIFIERS="impl impl_repair checker"
for CSTR in ${CSTRS}; do
    echo "--- pluggable constraint ${CSTR} ---"
    for VERIFIER in ${VERIFIERS}; do
        echo "\tVerifying ${VERIFIER}"
        ./verify.sh --size 2x2 -v --restriction discrete --verifier ${VERIFIER} $* ${CSTR}
    done
done

##Badly designed continuous pluggable constraints
#CSTRS="quarantine root"
#VERIFIERS="impl impl_repair checker"
#for CSTR in ${CSTRS}; do
#   echo "--- pluggable constraint ${CSTR} ---"
#    for VERIFIER in ${VERIFIERS}; do
#        echo "\tVerifying ${VERIFIER}"
#        ./verify.sh --size 1x1 -v --restriction continuous --verifier ${VERIFIER} $* ${CSTR}
#    done
#   done
