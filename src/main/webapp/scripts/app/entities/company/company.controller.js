'use strict';

angular.module('myplanningApp')
    .controller('CompanyController', function ($scope, $state, Company, CompanySearch) {

        $scope.companys = [];
        $scope.loadAll = function() {
            Company.query(function(result) {
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
