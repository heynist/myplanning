'use strict';

angular.module('myplanningApp')
    .controller('CompanyController', function ($scope, $state, Company, CompanySearch, ParseLinks) {

        $scope.companys = [];
        $scope.predicate = 'name';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.loadAll = function() {
            Company.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.companys = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            CompanySearch.query({query: $scope.searchQuery}, function(result) {
                $scope.companys = result;
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
            $scope.company = {
                name: null,
                id: null
            };
        };
    });
