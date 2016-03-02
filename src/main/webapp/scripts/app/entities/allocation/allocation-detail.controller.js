'use strict';

angular.module('myplanningApp')
    .controller('AllocationDetailController', function ($scope, $rootScope, $stateParams, entity, Allocation, Person, Company) {
        $scope.allocation = entity;
        $scope.load = function (id) {
            Allocation.get({id: id}, function(result) {
                $scope.allocation = result;
            });
        };
        var unsubscribe = $rootScope.$on('myplanningApp:allocationUpdate', function(event, result) {
            $scope.allocation = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
