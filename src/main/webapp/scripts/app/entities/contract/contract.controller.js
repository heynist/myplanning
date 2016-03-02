'use strict';

angular.module('myplanningApp')
    .controller('ContractController', function ($scope, $state, Contract, ContractSearch) {

        $scope.contracts = [];
        $scope.loadAll = function() {
            Contract.query(function(result) {
               $scope.contracts = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            ContractSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.contracts = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.contract = {
                startDate: null,
                endDate: null,
                contractNumber: null,
                year: null,
                month: null,
                id: null
            };
        };
    });
