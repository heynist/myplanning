'use strict';

angular.module('myplanningApp')
    .controller('PersonController', function ($scope, $state, Person, PersonSearch) {

        $scope.persons = [];
        $scope.loadAll = function() {
            Person.query(function(result) {
               $scope.persons = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            PersonSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.persons = result;
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
            $scope.person = {
                firstName: null,
                lastName: null,
                email: null,
                mobile: null,
                id: null
            };
        };
    });
