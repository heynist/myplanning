'use strict';

angular.module('myplanningApp')
    .controller('ContractDetailController', function ($scope, $rootScope, $stateParams, entity, Contract, Person, Company) {
        $scope.contract = entity;
        $scope.load = function (id) {
            Contract.get({id: id}, function(result) {
                $scope.contract = result;
            });
        };
        var unsubscribe = $rootScope.$on('myplanningApp:contractUpdate', function(event, result) {
            $scope.contract = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
