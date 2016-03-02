'use strict';

angular.module('myplanningApp')
    .controller('ContractController', function ($scope, $state, Contract, ContractSearch, ParseLinks) {

        $scope.contracts = [];
        $scope.predicate = 'endDate';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.loadAll = function() {
            Contract.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.contracts = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
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
