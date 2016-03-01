'use strict';

angular.module('myplanningApp')
    .controller('CompanyDetailController', function ($scope, $rootScope, $stateParams, entity, Company, Contract, User) {
        $scope.company = entity;
        $scope.load = function (id) {
            Company.get({id: id}, function(result) {
                $scope.company = result;
            });
        };
        var unsubscribe = $rootScope.$on('myplanningApp:companyUpdate', function(event, result) {
            $scope.company = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
