'use strict';

angular.module('myplanningApp')
    .controller('PersonController', function ($scope, $state, Person, PersonSearch, ParseLinks) {

        $scope.persons = [];
        $scope.predicate = 'lastName';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.loadAll = function() {
            Person.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
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
