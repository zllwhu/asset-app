#!/bin/bash

function usage()
{
    echo " Usage : "
    echo "   bash office_test_run.sh deploy"
    echo "   bash office_test_run.sh query    office_account "
    echo "   bash office_test_run.sh register office_account office_amount "
    echo "   bash office_test_run.sh transfer from office_account to office_account amount "
    echo " "
    echo " "
    echo "examples : "
    echo "   bash office_test_run.sh deploy "
    echo "   bash office_test_run.sh register  Asset0  10000000 "
    echo "   bash office_test_run.sh register  Asset1  10000000 "
    echo "   bash office_test_run.sh transfer  Asset0  Asset1 11111 "
    echo "   bash office_test_run.sh query Asset0"
    echo "   bash office_test_run.sh query Asset1"
    exit 0
}

    case $1 in
    deploy)
            [ $# -lt 1 ] && { usage; }
            ;;
    record)
            [ $# -lt 12 ] && { usage; }
            ;;
    remove)
            [ $# -lt 2 ] && { usage; }
            ;;
    query)
            [ $# -lt 2 ] && { usage; }
            ;;
    *)
        usage
            ;;
    esac

    java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:conf/:lib/*' org.fisco.bcos.asset.client.OfficeTestClient $@